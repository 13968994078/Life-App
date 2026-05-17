# Life App

`Life App` 是一个轻量生活类应用，当前围绕两类高频场景展开：

- `随机美食`：按分类维护菜单，并通过大转盘决定今天吃什么
- `起床签到`：记录每日签到、连续天数和月历数据，观察作息节奏

项目已包含基础账号体系、首页内容配置和两个小工具页，是一个可继续迭代的前后端一体化原型。

## 项目结构

```text
backend/   Java 8 + Spring Boot 后端
frontend/  uni-app 前端
docs/      数据库和接口说明
```

## 当前能力

### 业务功能

#### 随机美食

- 美食列表查询
- 新增、编辑、删除美食
- 按分类筛选
- 大转盘随机抽取
- 抽取历史回看
- 展示美食创建人昵称和头像

#### 起床签到

- 今日签到
- 连续签到和累计签到统计
- 月历签到视图
- 目标起床时间设置
- 晨间提醒开关
- 公开签到榜单

#### 账号与个人页

- 用户登录
- 用户注册
- 获取当前登录用户
- 修改个人资料
- 修改密码
- 个人页汇总美食、签到和设置状态

#### 首页内容与工具页

- 首页和主页面共用文案配置
- 远程文案拉取失败时自动回退本地默认文案
- 个税计算器
- 贷款计算器

## 技术栈

### 后端

- `Java 8`
- `Spring Boot 2.7.18`
- `MyBatis-Plus`
- `MySQL 5.7+`
- `JWT` 登录态

### 前端

- `uni-app`
- 主要通过 `HBuilderX` 导入 `frontend/` 目录运行

## 页面入口

前端当前页面包括：

- `pages/index/index`：首页
- `pages/login/index`：登录 / 注册
- `pages/food/index`：随机美食
- `pages/food/history`：抽取记录
- `pages/checkin/index`：签到页
- `pages/mine/index`：个人页
- `pages/mine/profile`：个人资料
- `pages/mine/password`：修改密码
- `pages/tools/tax-calculator`：个税计算器
- `pages/tools/loan-calculator`：贷款计算器

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

首次启动前请先执行：

- `docs/schema.sql`

启动命令：

```bash
cd backend
mvn spring-boot:run
```

默认端口：`8080`

启动后会自动初始化：

- 默认体验账号
- 默认美食数据
- 默认起床设置

### 前端

使用 `HBuilderX` 导入 `frontend/` 目录，运行到微信小程序或 `H5`。

前端接口地址默认来自：

- `frontend/config/network.js`

默认值：

- `http://47.97.40.183/api`

如果你在本地联调后端，通常需要改成自己机器可访问的地址，例如：

- `http://127.0.0.1:8080/api`
- `http://192.168.1.10:8080/api`

也可以在运行时通过本地缓存键覆盖：

- `apiBaseUrl`

### 微信小程序开发版联调

- HBuilderX 发行到微信小程序后，使用微信开发者工具打开 `frontend/unpackage/dist/build/mp-weixin`
- 如果请求报 `url not in domain list`，请在微信开发者工具里关闭“校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”
- 项目私有配置 `frontend/project.private.config.json` 已将 `urlCheck` 设为 `false`，重新编译后通常会同步到产物目录

## 默认体验账号

- 账号：`demo`
- 密码：`123456`

## 接口概览

详细接口见：`docs/api.md`

当前主要接口分组如下：

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me`
- `PUT /api/auth/profile`
- `PUT /api/auth/password`
- `GET /api/food/list`
- `POST /api/food`
- `PUT /api/food/{id}`
- `DELETE /api/food/{id}`
- `POST /api/food/random`
- `GET /api/food/history`
- `GET /api/check-in/today`
- `POST /api/check-in`
- `GET /api/check-in/statistics`
- `GET /api/check-in/calendar`
- `GET /api/check-in/public-board`
- `GET /api/content/home`
- `GET /api/settings`
- `PUT /api/settings`

## 数据说明

数据库结构见：`docs/schema.sql`

当前核心表包括：

- `user_info`
- `food`
- `food_draw_record`
- `check_in`
- `user_setting`

## 验证与测试

### 后端

- 项目包含部分控制器和服务层测试
- 本地运行 `mvn test` 需要真实 `JDK`，只有 `JRE` 时会因为缺少 `javac` 失败

### 前端

- 仓库中包含若干校验脚本，主要用于验证大转盘几何、标签布局、认证联动和页面刷新后的关键行为
- `frontend/package.json` 目前不是完整的前端工程脚本入口，实际运行仍以 `HBuilderX` 为主

## 当前限制

- 前端默认运行路径仍以 `HBuilderX` 为准，不假设完整的 Node 工具链已经配置好
- 默认 API 地址仍指向一个固定远程地址，本地联调时需要手动调整
- 项目目前仍是轻量 MVP 范围，没有引入 Redis、消息队列或更完整的后台管理能力

## 后续方向

1. 继续完善多用户体验和个人数据隔离细节
2. 丰富首页和内容配置能力
3. 为签到增加提醒、积分或勋章等激励机制
4. 为生活工具页补充更多实用计算器或轻量辅助能力
