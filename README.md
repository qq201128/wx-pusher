# wx-pusher

> 基于 Spring Boot 的微信消息推送服务

- **项目ID**: wx-pusher
- **创建于**: 2025-06-30 21:35:15 +08:00
- **协议**: RIPER-5 v5.0

## 项目简介

wx-pusher 是一个基于 Spring Boot 的微信消息推送服务，支持定时任务、模板消息、订阅管理等功能，适用于需要通过微信公众号向用户定时推送天气、英语等内容的场景。

## 主要特性
- 微信公众号消息推送（支持多种模板）
- 定时任务自动推送（如早安、午安、晚安）
- 支持特殊订阅者与普通订阅者分组
- 支持自定义消息模板
- 基于 Spring Boot，易于二次开发
- MySQL 持久化用户与订阅信息

## 安装与运行

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 启动步骤
1. 克隆本项目：
   ```bash
   git clone <repo-url>
   cd wx-pusher-1.0.0
   ```
2. 配置数据库与微信参数（见下方配置说明）。
3. 构建并运行：
   ```bash
   mvn clean package
   java -jar target/wx-pusher-0.0.1-SNAPSHOT.jar
   ```

## 配置说明（src/main/resources/application.yml）
```yaml
server:
  port: 19090
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1/jerry?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
wx:
  specialOpenIdList:
    - oWrc7vsTuNyZM-mWcc7CyH0GALOA
    - oWrc7vseFU1e67lyviuwE6HJWI68
```

## 主要API接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /checkToken | 订阅校验（SubscribeController）|
| GET  | /checkToken | 微信服务器校验（ValidateController）|
| POST | /executeSpecialMorningTask | 推送特殊订阅者早安消息 |
| POST | /executeCommonMorningTask  | 推送普通订阅者早安消息 |
| POST | /executeSpecialAfternoonTask | 推送特殊订阅者午安消息 |
| POST | /executeSpecialNightTask | 推送特殊订阅者晚安消息 |

## 消息模板示例（CommonMorning.txt）
```
当前位置：{{location.DATA}}
实时天气：{{now_temp.DATA}}°C  {{now_weather.DATA}}
气象：{{now_wind_dir.DATA}}{{now_wind_class.DATA}} | 湿度{{now_rh.DATA}}%
今天：{{today_weather.DATA}}  {{today_high.DATA}}/{{today_low.DATA}}°C
明天：{{tomorrow_weather.DATA}}  {{tomorrow_high.DATA}}/{{tomorrow_low.DATA}}°C

每日英语：
{{daily_english_en1.DATA}}{{daily_english_en2.DATA}}
{{daily_english_cn1.DATA}}{{daily_english_cn2.DATA}}
```

## 目录结构
```
wx-pusher-1.0.0/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/liu/...
│   │   └── resources/
│   │       ├── application.yml
│   │       └── template/
│   └── test/
│       └── java/com/liu/test/...
└── ...
```

## 贡献与反馈
欢迎提交 issue 或 PR 参与改进。

---

> 文档自动生成，符合 RIPER-5 归档规范。 