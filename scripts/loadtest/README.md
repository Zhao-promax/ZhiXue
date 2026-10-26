# 压测脚本

用 k6 模拟学生心跳上报，验证进度上报接口的吞吐与 RT。

## 环境准备

- 安装 k6：`npm i -g k6`，或参考 https://k6.io/docs/get-started/installation/
- 中间件与服务已启动（见根目录 README「快速开始」）

## 运行

默认打网关 8080，3000 QPS（15000 在线学生 × 每 5s 上报一次）：

```bash
k6 run scripts/loadtest/report.js
```

## 参数（环境变量）

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| `VUS` | `15000` | 在线学生数 |
| `DURATION` | `60s` | 压测时长 |
| `HEARTBEAT_INTERVAL` | `5` | 心跳间隔（秒），勿低于 3s 防刷阈值 |
| `BASE_URL` | `http://localhost:8080` | 目标地址，直连进度服务用 `http://localhost:8081` |

示例：压测 5 分钟、直连进度服务：

```bash
k6 run -e VUS=15000 -e DURATION=300s -e BASE_URL=http://localhost:8081 scripts/loadtest/report.js
```

## 期望结果

- 平均 RT < 10ms，P99 < 30ms
- 错误率 < 0.01%
- 3000 QPS 下 Redis / MQ 无积压、MySQL 连接不被打满

## QPS 换算

`在线学生数 ÷ 心跳间隔（秒） = QPS`。调整 `VUS` 与 `HEARTBEAT_INTERVAL` 可得到不同量级，例如 `15000 / 5 = 3000 QPS`。
