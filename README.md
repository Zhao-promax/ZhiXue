# 知学学情平台（ZhiXue）

在线教育视频回放与学情管理平台，后端基于 Spring Cloud 微服务架构。核心解决「高并发学习进度上报」与「有效学习时长统计」两类问题，支持断点续播、多端进度同步、防刷过滤与学习行为分析。

## 架构

```
客户端 ──▶ zixue-gateway (8080, Spring Cloud Gateway)
              ├── /api/progress/** ──▶ zixue-progress (8081)  进度上报服务
              └── /api/learning/** ──▶ zixue-learning (8082)  学情服务
                                           │ OpenFeign 跨服务调用
                                           ▼
                                    zixue-progress 内部接口

依赖中间件：Nacos(8848) 注册中心 · MySQL(3306) · Redis(6379) · RabbitMQ(5672)
```

## 技术栈

| 类别 | 技术 |
| --- | --- |
| 微服务 | Spring Cloud Alibaba 2023.0.1.0 · Spring Cloud 2023.0.1 · Spring Boot 3.2.4 |
| 网关 | Spring Cloud Gateway |
| 服务调用 | OpenFeign + Spring Cloud LoadBalancer |
| 注册中心 | Nacos 2.3.2 |
| 数据层 | MyBatis-Plus 3.5.7 · MySQL 8.0 |
| 缓存/锁 | Redis 7 |
| 消息队列 | RabbitMQ 3.13 |
| 构建/部署 | Maven · Docker |

## 模块结构

```
zixue/
├── zixue-common     公共模块：统一返回 Result、业务异常、全局异常处理
├── zixue-gateway    网关：Nacos 动态路由 + 全局 CORS
├── zixue-progress   进度上报服务（核心）
└── zixue-learning   学情服务：聚合观看完成率/平均时长/跳出率
```

## 核心设计

- **高并发上报**：进度走「Redis 直写即返回」快路径，RabbitMQ 削峰 + 消费者线程池批量落库 MySQL 慢路径，异步化后接口 RT 大幅下降。
- **断点续播与多端同步**：Redis Hash 缓存实时进度，Lua 脚本按上报时间戳裁决多端并发写冲突；MySQL 以 `(user_id, video_id)` 唯一键 + `ON DUPLICATE KEY UPDATE` 保证幂等。
- **三重防刷与有效时长**：心跳间隔校验 + 倍速校验 + 多端并发去重，有效时长按心跳间隔累加、剔除拖动/断档段。
- **可靠投递**：MQ 生产者 confirm + 消费者手动 ack + 死信队列，保证异步落库不丢不重。
- **学情聚合**：学情服务通过 OpenFeign 调用进度服务，Redis 分布式锁保证定时任务单实例执行。

## 环境要求

- JDK 17
- Docker + Docker Compose（运行中间件）
- Maven 3.9+（或 IntelliJ IDEA 内置 Maven）
- k6（可选，压测）

## 快速开始

### 1. 启动中间件

```bash
docker compose up -d
```

启动后自动执行 `sql/init.sql` 建库建表。中间件地址：

| 中间件 | 地址 |
| --- | --- |
| MySQL | `localhost:3306`（root/root，库 `zixue`） |
| Redis | `localhost:6379` |
| RabbitMQ | `localhost:5672`（管理台 `http://localhost:15672`，guest/guest） |
| Nacos | `localhost:8848`（控制台 `http://localhost:8848/nacos`） |

### 2. 启动服务

**推荐用 IntelliJ IDEA**：打开项目后分别运行 `GatewayApplication`、`ProgressApplication`、`LearningApplication` 三个启动类（IDEA 会自动处理多模块依赖）。

命令行方式（先安装公共模块，再启动各服务）：

```bash
./mvnw -DskipTests install
./mvnw -pl zixue-progress spring-boot:run
./mvnw -pl zixue-learning  spring-boot:run
./mvnw -pl zixue-gateway   spring-boot:run
```

启动顺序建议：progress → learning → gateway。启动后各服务注册到 Nacos。

### 3. 接口示例

```bash
# 进度上报（经网关，高频心跳）
curl -X POST http://localhost:8080/api/progress/report \
  -H "Content-Type: application/json" \
  -d '{"userId":10001,"videoId":1,"position":120,"duration":3600,"speed":1.0,"deviceId":"web-abc"}'

# 断点续播查询
curl http://localhost:8080/api/progress/1 -H "X-User-Id: 10001"

# 触发某视频学情聚合
curl -X POST http://localhost:8080/api/learning/stats/aggregate/1

# 查询某视频学情指标
curl http://localhost:8080/api/learning/stats/1
```

## 压测

见 [scripts/loadtest/README.md](scripts/loadtest/README.md)。默认场景：15000 在线学生 × 每 5s 心跳 = 3000 QPS，预期平均 RT < 10ms、P99 < 30ms。

```bash
k6 run scripts/loadtest/report.js
```

## Docker 部署

各服务已提供多阶段构建 `Dockerfile`（构建上下文为仓库根目录）：

```bash
docker build -f zixue-progress/Dockerfile -t zixue-progress .
docker build -f zixue-learning/Dockerfile  -t zixue-learning .
docker build -f zixue-gateway/Dockerfile   -t zixue-gateway .
```

容器内运行时需将各服务 `application.yml` 中中间件地址（`localhost`）与 Nacos 地址替换为容器网络地址，可通过环境变量覆盖。
