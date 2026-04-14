# Data Push Service - 灵码使用数据推送服务

从 MySQL 数据库查询灵码(Lingma)使用数据，按指定格式推送到远程 API 接口。

## 环境要求

- JDK 1.8
- Maven 3.6+
- MySQL 5.7+

## 快速开始

### 1. 修改配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-mysql-host:3306/dwd?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

push:
  api:
    url: http://10.15.149.29:80/api/ai/lingma_usage/create
```

### 2. 编译打包

```bash
mvn clean package -DskipTests
```

### 3. 运行服务

```bash
java -jar target/data-push-service-1.0.0.jar
```

也可指定外部配置文件：

```bash
java -jar target/data-push-service-1.0.0.jar --spring.config.location=/path/to/application.yml
```

### 4. 触发数据推送

```bash
curl -X POST "http://localhost:8081/api/push/lingma-usage?start=2026-03-23&end=2026-03-29"
```

返回示例：

```json
{
  "total": 15,
  "success": 14,
  "failed": 1,
  "message": "Push completed: total=15, success=14, failed=1"
}
```

## 配置说明

### MySQL 数据源

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.datasource.url` | MySQL 连接地址 | `localhost:3306/dwd` |
| `spring.datasource.username` | 数据库用户名 | `root` |
| `spring.datasource.password` | 数据库密码 | - |

### 推送 API

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `push.api.url` | 推送接口地址 | `http://10.15.149.29:80/api/ai/lingma_usage/create` |
| `push.api.connect-timeout` | 连接超时(ms) | `5000` |
| `push.api.read-timeout` | 读取超时(ms) | `10000` |

### 查询 SQL

| 配置项 | 说明 |
|--------|------|
| `push.query.sql` | 查询 SQL，支持 `${start}` 和 `${end}` 占位符 |

可在 `application.yml` 中自定义查询 SQL，SQL 中的 `${start}` 和 `${end}` 会在运行时由接口传入的时间参数替换。

## 推送数据格式

每条推送到远程接口的数据格式如下：

```json
{
  "week_day": "202613周",
  "dept_name": "测试部门",
  "user_name": "test_user",
  "chatturnsaccepted": 10,
  "lines_accepted": 100,
  "lines_suggested": 150,
  "total_chat_turns": 20,
  "count_accepted": 30,
  "count_suggested": 50,
  "accept_rate": 0.6,
  "total_lines_changed": 1000,
  "total_lines_accepted": 800,
  "generate_rate": 0.8
}
```

## 接口列表

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| POST | `/api/push/lingma-usage` | `start` (开始日期), `end` (结束日期) | 触发数据推送 |

### 请求示例

```bash
# 推送 2026 年第 13 周的数据
curl -X POST "http://localhost:8081/api/push/lingma-usage?start=2026-03-23&end=2026-03-29"

# 推送指定月份的数据
curl -X POST "http://localhost:8081/api/push/lingma-usage?start=2026-03-01&end=2026-03-31"
```

## 项目结构

```
src/main/java/com/datapush/
├── DataPushApplication.java       # Spring Boot 启动类
├── config/
│   ├── PushConfig.java            # 配置属性映射 (push.*)
│   └── RestTemplateConfig.java    # HTTP 客户端配置
├── controller/
│   └── DataPushController.java    # 推送触发接口
├── dto/
│   ├── LingmaUsageDTO.java        # 推送数据模型
│   └── PushResultDTO.java         # 推送结果模型
└── service/
    └── DataPushService.java       # 核心业务逻辑
```

## 工作流程

1. 接收请求参数 `start`、`end`（时间区间）
2. 从配置文件读取 SQL 模板，替换 `${start}`、`${end}` 占位符
3. 执行 SQL 查询 MySQL 数据库
4. 将查询结果逐条组装为 JSON 并 POST 到远程推送接口
5. 汇总推送结果（总数/成功/失败）并返回
