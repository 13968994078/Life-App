# API Draft

Base URL: `http://47.97.40.183/api`

开发版微信小程序联调时，如果请求报 `url not in domain list`，请在微信开发者工具中关闭合法域名校验，或确认项目私有配置中的 `urlCheck` 为 `false`。

## Food

### `GET /food/list`

查询当前登录用户可见的美食列表，支持：

- `category`
- `poolView`：兼容保留，当前版本会忽略这个参数

当前返回集合包含：

- 所有公共美食
- 当前用户自己创建的美食

返回对象仍保留 `poolType` 字段兼容旧数据，但当前前端不再暴露池子筛选和池子标签。

列表项会额外返回添加人展示信息，其中 `creatorName` 来自 `user_info.username`，`creatorAvatar` 来自 `user_info.avatar`。如果创建人记录或用户名缺失，名称会回退为 `用户{id}`，头像为空：

```json
{
  "id": 1,
  "userId": 1,
  "name": "黄焖鸡",
  "category": "午餐",
  "priceRange": "20-30",
  "poolType": "PUBLIC",
  "creatorName": "alice",
  "creatorAvatar": "/static/profile-avatars/avatar-tea.png"
}
```

### `POST /food`

新增美食。当前前端不再提供池子选择，未显式传 `poolType` 时后端默认按公开美食处理。

请求体：

```json
{
  "name": "黄焖鸡",
  "category": "午餐",
  "priceRange": "20-30",
  "tags": ["米饭", "热食"],
  "imageUrl": ""
}
```

### `PUT /food/{id}`

更新美食。

说明：

- 只有创建者本人可以编辑或删除对应美食
- 当前前端不再暴露池子切换，旧数据上的 `poolType` 仅作兼容保留

请求体：

```json
{
  "name": "黄焖鸡",
  "category": "午餐",
  "priceRange": "20-30",
  "imageUrl": ""
}
```

### `POST /food/random`

按分类从当前用户可见的全部美食中随机抽取。

请求体：

```json
{
  "category": "午餐"
}
```

`poolView` 参数仍兼容保留，但当前版本会忽略。

### `GET /food/history`

查询抽取历史。

## Auth

### `POST /auth/login`

账号密码登录。密码在数据库中使用 BCrypt 哈希保存，老的明文密码会在首次成功登录后自动升级为哈希。

请求体：

```json
{
  "username": "demo",
  "password": "123456"
}
```

### `POST /auth/register`

注册新账号，成功后直接返回登录态。新注册账号的密码会直接按 BCrypt 哈希保存。

请求体：

```json
{
  "username": "alice",
  "nickname": "Alice",
  "password": "abc123",
  "confirmPassword": "abc123"
}
```

### `PUT /auth/password`

登录后修改密码。修改后的密码会按 BCrypt 哈希保存。

请求体：

```json
{
  "oldPassword": "123456",
  "newPassword": "newpass",
  "confirmPassword": "newpass"
}
```

### `PUT /auth/profile`

登录后更新当前用户资料。昵称不能为空；头像传空字符串会清空头像。

请求体：

```json
{
  "nickname": "Alice",
  "avatar": "https://example.com/avatar.png"
}
```

返回当前最新用户资料：

```json
{
  "id": 1,
  "username": "demo",
  "nickname": "Alice",
  "avatar": "https://example.com/avatar.png"
}
```

### `GET /auth/me`

获取当前登录用户信息，请求头使用：

```text
Authorization: Bearer <token>
```

## Check In

### `GET /check-in/today`

查询今日签到状态。

### `POST /check-in`

执行签到。

### `GET /check-in/calendar`

查询月历签到数据，参数：`year`、`month`。

### `GET /check-in/statistics`

查询连续签到天数与累计签到天数。

### `GET /check-in/public-board`

查询公开签到榜单，返回所有用户的头像、今日签到状态、连续签到天数和累计签到天数摘要。

## Content

### `GET /content/home`

获取首页与主页面共用的轻量文案配置，当前为公开接口，不要求登录。

返回示例：

```json
{
  "quote": {
    "text": "把日子翻到今天这一页，先吃好一顿。",
    "source": "今日短笺"
  },
  "homeHero": {
    "title": "今天这一页，从饭点和作息开始",
    "subtitle": "先吃稳一餐，再记下一次起床时间，日子就有了线索。"
  },
  "foodHero": {
    "title": "这一餐，让转盘提个醒",
    "subtitle": "选个大概方向，剩下的交给一点运气。",
    "emptyTip": "菜单还是空的，先写下几样常吃的。"
  },
  "checkinHero": {
    "title": "把清晨留一笔",
    "subtitle": "起床时间写下来，作息会慢慢露出自己的样子。",
    "emptyTip": "这个月还没有记录，第一次签到会从这里开始。"
  },
  "mineHero": {
    "title": "你的生活小账本",
    "subtitle": "饭点、签到、提醒，都收在这一页，翻起来不费劲。"
  }
}
```

说明：

- 服务端固定优先请求远程文案源
- 远程源不可用时自动回退到本地预置文案
- 服务端对远程结果做短时缓存，避免每次请求都访问第三方

## Settings

### `GET /settings`

获取用户设置。

### `PUT /settings`

更新目标起床时间和提醒开关。
