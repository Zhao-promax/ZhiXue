import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

// 进度上报压测脚本：模拟在线学生心跳上报，验证吞吐与 RT
//
// 3000 QPS = 15000 在线学生 × 每 5s 上报一次
// 运行：k6 run scripts/loadtest/report.js
//
// 环境变量：
//   VUS               在线学生数（默认 15000）
//   DURATION          压测时长（默认 60s）
//   HEARTBEAT_INTERVAL 心跳间隔秒（默认 5，勿低于 3s 防刷阈值）
//   BASE_URL          目标地址（默认 http://localhost:8080 网关）

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const HEARTBEAT_INTERVAL = Number(__ENV.HEARTBEAT_INTERVAL || 5);

const reportRT = new Trend('report_rt_ms', true);

export const options = {
  scenarios: {
    heartbeat: {
      executor: 'constant-vus',
      vus: Number(__ENV.VUS || 15000),
      duration: __ENV.DURATION || '60s',
    },
  },
  thresholds: {
    http_req_duration: ['avg<10', 'p(99)<30'],
    http_req_failed: ['rate<0.0001'],
  },
};

export default function () {
  // 每个 VU 固定一个用户：同用户 5s 心跳不会触发防刷，且避免随机碰撞 429
  const userId = __VU;
  const payload = JSON.stringify({
    userId,
    videoId: 1,
    position: Math.floor(Math.random() * 3600),
    duration: 3600,
    speed: 1.0,
    deviceId: `device-${userId}`,
  });

  const res = http.post(`${BASE_URL}/api/progress/report`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  reportRT.add(res.timings.duration);
  check(res, { 'status is 200': (r) => r.status === 200 });

  sleep(HEARTBEAT_INTERVAL);
}
