# Life App

`Life App` 是一个轻量生活类应用，包含两个 MVP 功能：

- 随机美食：按分类筛选并通过大转盘形式抽取吃什么
- 起床签到：记录每日签到时间和连续签到天数

项目结构：

```text
backend/   Java 8 + Spring Boot 后端
frontend/  uni-app 前端骨架
docs/      数据库和接口说明
```

## MVP 功能

### 随机美食

- 美食列表
- 新增美食
- 分类筛选
- 随机抽取
- 抽取历史

### 起床签到

- 今日签到
- 目标起床时间
- 连续签到天数
- 月历数据

## 启动说明

### 后端

要求：`JDK 8`、`Maven 3.8+`、`MySQL 5.7+`

启动前必须提供这些环境变量：

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

示例：

```bash
set DB_URL=jdbc:mysql://127.0.0.1:3306/life_app?useUnicode=true^&characterEncoding=utf8^&serverTimezone=Asia/Shanghai^&useSSL=false^&allowPublicKeyRetrieval=true
set DB_USERNAME=root
set DB_PASSWORD=your_password
set JWT_SECRET=replace_with_a_long_random_secret_at_least_32_chars
```

```bash
cd backend
mvn spring-boot:run
```

默认启动端口：`8080`

默认库名示例：`life_app`

发布前建议：

- 不要在代码里保留数据库密码或 JWT 密钥
- ECS 只开放必要端口，如 `80`，后端进程端口仅保留本机访问

### 前端

使用 HBuilderX 导入 `frontend` 目录，运行到微信小程序或 H5。

前端接口地址默认读取：

- `frontend/config/network.js`

默认值是：

- `http://47.97.40.183/api`

如果运行到手机或模拟器里的独立 App，请把地址改成你电脑的局域网 IP，例如：

- `http://192.168.1.10:8080/api`

也可以在运行时通过本地缓存键覆盖：

- `apiBaseUrl`

### 微信小程序开发版联调

- HBuilderX 发行到微信小程序后，使用微信开发者工具打开 `frontend/unpackage/dist/build/mp-weixin`
- 当前开发联调默认接口地址为 `http://47.97.40.183/api`
- 如果请求报 `url not in domain list`，请在微信开发者工具里关闭“校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”
- 项目私有配置 `frontend/project.private.config.json` 已将 `urlCheck` 设为 `false`，重新编译后通常会同步到产物目录

### 默认体验账号

- 账号：`demo`
- 密码：`123456`

### 登录接口

- `POST /api/auth/login`
- `GET /api/auth/me`

## 后续建议

1. 接入登录能力，补充用户体系和 JWT
2. 为大转盘增加真实动画和自定义样式
3. 为签到增加提醒、积分和勋章
