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

查询公开签到榜单，返回所有用户的今日签到状态、连续签到天数和累计签到天数摘要。

## Settings

### `GET /settings`

获取用户设置。

### `PUT /settings`

更新目标起床时间和提醒开关。
