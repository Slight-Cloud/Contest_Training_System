# 竞赛集训系统接口文档（RESTful风格）

---

## 1. 用户管理

### 1.1 注册用户

#### 1.1.1 基本信息

- 请求路径：/register
- 请求方式：POST
- 接口描述：用户注册，创建新账号

#### 1.1.2 请求参数

| 参数名       | 类型   | 是否必须 | 备注   |
| ------------ | ------ | -------- | ------ |
| email        | string | 必须     | 邮箱   |
| phoneNumber | string | 必须     | 手机号 |
| nickname     | string | 必须     | 昵称   |
| studentId | string | 必须     | 学号   |
| password     | string | 必须     | 密码   |

请求参数样例：

```json
{
  "email": "test@example.com",
  "phoneNumber": "13800000000",
  "nickname": "张三",
  "studentId": "20250001",
  "password": "123456"
}
```

#### 1.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 用户信息             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "注册成功",
  "data": {
    "userId": 1001,
    "email": "test@example.com",
    "nickname": "张三"
  }
}
```

---

### 1.2 用户登录

#### 1.2.1 基本信息

- 请求路径：/login
- 请求方式：POST
- 接口描述：用户登录，获取token

#### 1.2.2 请求参数

| 参数名         | 类型   | 是否必须 | 备注         |
| -------------- | ------ | -------- | ------------ |
| emailOrPhone | string | 必须     | 邮箱或手机号 |
| password       | string | 必须     | 密码         |

请求参数样例：

```json
{
  "emailOrPhone": "test@example.com",
  "password": "123456"
}
```

#### 1.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | token信息            |

响应数据样例：

```json
{
  "code": 1,
  "msg": "登录成功",
  "data": {
    "token": "xxxxxx"
  }
}
```

---

### 1.3 注销用户

#### 1.3.1 基本信息

- 请求路径：/user/deactivate
- 请求方式：POST
- 接口描述：注销当前用户

#### 1.3.2 请求参数

| 参数名 | 类型   | 是否必须 | 备注      |
| ------ | ------ | -------- | --------- |
| token  | string | 必须     | 用户token |

请求参数样例：

```json
{
  "token": "xxxxxx"
}
```

#### 1.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | null                 |

响应数据样例：

```json
{
  "code": 1,
  "msg": "注销成功",
  "data": null
}
```

---

### 1.4 更新用户信息

#### 1.4.1 基本信息

- 请求路径：/user/update
- 请求方式：PUT
- 接口描述：更新用户基本信息

#### 1.4.2 请求参数

| 参数名       | 类型   | 是否必须 | 备注   |
| ------------ | ------ | -------- | ------ |
| nickname     | string | 非必须   | 昵称   |
| phoneNumber | string | 非必须   | 手机号 |
| password     | string | 非必须   | 新密码 |

请求参数样例：

```json
{
  "nickname": "新昵称",
  "phoneNumber": "13900000000",
  "password": "newpassword"
}
```

#### 1.4.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 更新后用户信息       |

响应数据样例：

```json
{
  "code": 1,
  "msg": "更新成功",
  "data": {
    "userId": 1001,
    "nickname": "新昵称",
    "phoneNumber": "13900000000"
  }
}
```

---

### 1.5 获取用户信息

#### 1.5.1 基本信息

- 请求路径：/user/profile
- 请求方式：GET
- 接口描述：获取当前用户详细信息

#### 1.5.2 请求参数

无

#### 1.5.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 用户详细信息         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1001,
    "email": "test@example.com",
    "phoneNumber": "13800000000",
    "nickname": "张三",
    "studentId": "20250001",
    "role": "STUDENT",
    "createdAt": "2025-01-01 10:00:00"
  }
}
```

---

### 1.6 查询用户列表（管理员）

#### 1.6.1 基本信息

- 请求路径：/admin/users
- 请求方式：GET
- 接口描述：管理员查询用户列表，支持分页、筛选和排序

#### 1.6.2 请求参数

| 参数名    | 类型   | 是否必须 | 备注                                                                                                |
| --------- | ------ | -------- | --------------------------------------------------------------------------------------------------- |
| page      | number | 非必须   | 页码，默认1                                                                                         |
| pageSize | number | 非必须   | 每页数量，默认20                                                                                    |
| keyword   | string | 非必须   | 搜索关键字（昵称/邮箱/学号）                                                                        |
| role      | string | 非必须   | 用户角色筛选                                                                                        |
| isActive | number | 非必须   | 用户状态筛选（0/1）                                                                                 |
| sortBy   | string | 非必须   | 排序字段，支持：id_asc, id_desc, nickname_asc, nickname_desc, created_asc, created_desc，默认id_asc |

请求参数样例：
/admin/users?page=1&pageSize=20&keyword=张&role=STUDENT&isActive=1&sortBy=created_desc

#### 1.6.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 分页用户列表         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 20,
    "users": [
      {
        "userId": 1001,
        "email": "test@example.com",
        "nickname": "张三",
        "studentId": "20250001",
        "role": "STUDENT",
        "isActive": 1,
        "createdAt": "2025-01-01 10:00:00"
      }
    ]
  }
}
```

---

## 2. 赛事管理

### 2.1 新建赛事

#### 2.1.1 基本信息

- 请求路径：/contest/create
- 请求方式：POST
- 接口描述：教师新建赛事

#### 2.1.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注           |
| ----------- | ------ | -------- | -------------- |
| title       | string | 必须     | 赛事名称       |
| description | string | 非必须   | 赛事介绍       |
| startTime | string | 必须     | 开始时间       |
| endTime | string | 必须     | 结束时间       |
| password    | string | 非必须   | 赛事密码       |
| visibility  | string | 必须     | PUBLIC/PRIVATE |
| problemIds | array  | 必须     | 题目ID列表     |

请求参数样例：

```json
{
  "title": "2025年春季赛",
  "description": "春季编程竞赛",
  "startTime": "2025-04-01 09:00:00",
  "endTime": "2025-04-01 12:00:00",
  "password": "abc123",
  "visibility": "PUBLIC",
  "problemIds": [1,2,3]
}
```

#### 2.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 赛事ID               |

响应数据样例：

```json
{
  "code": 1,
  "msg": "创建成功",
  "data": {
    "contestId": 101
  }
}
```

---

### 2.2 删除赛事

#### 2.2.1 基本信息

- 请求路径：/contest/{contestId}
- 请求方式：DELETE
- 接口描述：根据ID删除赛事

#### 2.2.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注   |
| ---------- | ------ | -------- | ------ |
| contestId | number | 必须     | 赛事ID |

请求参数样例：
/contest/101

#### 2.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | null                 |

响应数据样例：

```json
{
  "code": 1,
  "msg": "删除成功",
  "data": null
}
```

---

### 2.3 修改赛事

#### 2.3.1 基本信息

- 请求路径：/contest/update
- 请求方式：PUT
- 接口描述：教师修改赛事信息

#### 2.3.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注           |
| ----------- | ------ | -------- | -------------- |
| contestId | number | 必须     | 赛事ID         |
| title       | string | 非必须   | 赛事名称       |
| description | string | 非必须   | 赛事介绍       |
| startTime | string | 非必须   | 开始时间       |
| endTime | string | 非必须   | 结束时间       |
| password    | string | 非必须   | 赛事密码       |
| visibility  | string | 非必须   | PUBLIC/PRIVATE |
| problemIds | array  | 非必须   | 题目ID列表     |

请求参数样例：

```json
{
  "contestId": 101,
  "title": "2025年春季赛（修改版）",
  "description": "春季编程竞赛修改版",
  "problemIds": [1,2,3,4]
}
```

#### 2.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 更新后赛事信息       |

响应数据样例：

```json
{
  "code": 1,
  "msg": "修改成功",
  "data": {
    "contestId": 101,
    "title": "2025年春季赛（修改版）"
  }
}
```

---

### 2.4 查询赛事列表

#### 2.4.1 基本信息

- 请求路径：/contest/list
- 请求方式：GET
- 接口描述：查询赛事列表，支持分页、筛选和排序

#### 2.4.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注                                                                                                |
| ---------- | ------ | -------- | --------------------------------------------------------------------------------------------------- |
| page       | number | 非必须   | 页码，默认1                                                                                         |
| pageSize  | number | 非必须   | 每页数量，默认20                                                                                    |
| visibility | string | 非必须   | 赛事类型筛选（PUBLIC/PRIVATE）                                                                      |
| state      | string | 非必须   | 赛事状态筛选                                                                                        |
| keyword    | string | 非必须   | 搜索关键字                                                                                          |
| sortBy    | string | 非必须   | 排序字段，支持：id_asc, id_desc, title_asc, title_desc, start_time_asc, start_time_desc，默认id_asc |

请求参数样例：
/contest/list?page=1&pageSize=10&visibility=PUBLIC&state=ONGOING&sortBy=start_time_desc

#### 2.4.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 分页赛事列表         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 10,
    "contests": [
      {
        "contestId": 101,
        "title": "2025年春季赛",
        "description": "春季编程竞赛",
        "startTime": "2025-04-01 09:00:00",
        "endTime": "2025-04-01 12:00:00",
        "visibility": "PUBLIC",
        "state": "ONGOING",
        "participantCount": 120
      }
    ]
  }
}
```

---

### 2.5 获取赛事详情

#### 2.5.1 基本信息

- 请求路径：/contest/{contestId}
- 请求方式：GET
- 接口描述：获取赛事详细信息和题目列表

#### 2.5.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注               |
| ---------- | ------ | -------- | ------------------ |
| contestId | number | 必须     | 赛事ID（路径参数） |

请求参数样例：
/contest/101

#### 2.5.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 赛事详细信息         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "contestId": 101,
    "title": "2025年春季赛",
    "description": "春季编程竞赛",
    "startTime": "2025-04-01 09:00:00",
    "endTime": "2025-04-01 12:00:00",
    "visibility": "PUBLIC",
    "state": "ONGOING",
    "creatorId": 2001,
    "problems": [
      {
        "problemId": 1,
        "title": "A+B Problem",
        "displayOrder": 1,
        "timeLimit": 1000,
        "memoryLimit": 256
      }
    ],
    "participantCount": 120
  }
}
```

---

### 2.6 参与赛事

#### 2.6.1 基本信息

- 请求路径：/contest/join
- 请求方式：POST
- 接口描述：学生参与赛事（公开赛事或输入密码）

#### 2.6.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注     |
| ---------- | ------ | -------- | -------- |
| contestId | number | 必须     | 赛事ID   |
| password   | string | 非必须   | 赛事密码 |
| teamName | string | 非必须   | 队伍名   |

请求参数样例：

```json
{
  "contestId": 101,
  "password": "abc123",
  "teamName": "我的队伍"
}
```

#### 2.6.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 参赛信息             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "参赛成功",
  "data": {
    null
  }
}
```

---

## 3. 题库管理

### 3.1 新建题目

#### 3.1.1 基本信息

- 请求路径：/problem/create
- 请求方式：POST
- 接口描述：教师新建题目

#### 3.1.2 请求参数

| 参数名        | 类型   | 是否必须 | 备注         |
| ------------- | ------ | -------- | ------------ |
| title         | string | 必须     | 题目标题     |
| description   | string | 必须     | 题目描述     |
| inputSpec | string | 必须     | 输入说明     |
| outputSpec | string | 必须     | 输出说明     |
| sampleInput | string | 必须     | 输入样例     |
| sampleOutput | string | 必须     | 输出样例     |
| remark        | string | 非必须   | 备注         |
| timeLimit | number | 必须     | 时间限制(ms) |
| memoryLimit | number | 必须     | 内存限制(MB) |
| testdataZip | string | 必须     | 测试数据ZIP  |

请求参数样例：

```json
{
  "title": "A+B Problem",
  "description": "计算两个数之和",
  "inputSpec": "输入两个整数",
  "outputSpec": "输出一个整数",
  "sampleInput": "1 2",
  "sampleOutput": "3",
  "remark": "无",
  "timeLimit": 1000,
  "memoryLimit": 256,
  "testdataZip": "url/to/zip"
}
```

#### 3.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 题目ID               |

响应数据样例：

```json
{
  "code": 1,
  "msg": "创建成功",
  "data": {
    "problemId": 201
  }
}
```

---

### 3.2 修改题目

#### 3.2.1 基本信息

- 请求路径：/problem/update
- 请求方式：PUT
- 接口描述：教师修改题目信息（测试数据只能增加，不能修改或删除）

#### 3.2.2 请求参数

| 参数名        | 类型   | 是否必须 | 备注         |
| ------------- | ------ | -------- | ------------ |
| problemId | number | 必须     | 题目ID       |
| title         | string | 非必须   | 题目标题     |
| description   | string | 非必须   | 题目描述     |
| inputSpec | string | 非必须   | 输入说明     |
| outputSpec | string | 非必须   | 输出说明     |
| sampleInput | string | 非必须   | 输入样例     |
| sampleOutput | string | 非必须   | 输出样例     |
| remark        | string | 非必须   | 备注         |
| timeLimit | number | 非必须   | 时间限制(ms) |
| memoryLimit | number | 非必须   | 内存限制(MB) |

请求参数样例：

```json
{
  "problemId": 201,
  "title": "A+B Problem（修改版）",
  "description": "计算两个数之和（修改版）",
  "timeLimit": 2000
}
```

#### 3.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 更新后题目信息       |

响应数据样例：

```json
{
  "code": 1,
  "msg": "修改成功",
  "data": {
    "problemId": 201,
    "title": "A+B Problem（修改版）"
  }
}
```

---

### 3.3 删除/隐藏题目

#### 3.3.1 基本信息

- 请求路径：/problem/{problemId}
- 请求方式：DELETE
- 接口描述：删除或隐藏题目（未在赛事中使用可删除，否则隐藏）

#### 3.3.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注               |
| ---------- | ------ | -------- | ------------------ |
| problemId | number | 必须     | 题目ID（路径参数） |

请求参数样例：
/problem/201

#### 3.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 删除结果             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "题目已隐藏（在赛事中使用）",
  "data": {
    "problemId": 201,
    "action": "hidden"
  }
}
```

---

### 3.4 查询题目列表

#### 3.4.1 基本信息

- 请求路径：/problem/list
- 请求方式：GET
- 接口描述：查询题目列表，支持分页、筛选和排序

#### 3.4.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注                                                                                          |
| ---------- | ------ | -------- | --------------------------------------------------------------------------------------------- |
| page       | number | 非必须   | 页码，默认1                                                                                   |
| pageSize  | number | 非必须   | 每页数量，默认20                                                                              |
| keyword    | string | 非必须   | 搜索关键字（题目标题）                                                                        |
| creatorId | number | 非必须   | 创建者ID筛选                                                                                  |
| isHidden  | number | 非必须   | 隐藏状态筛选（0/1）                                                                           |
| sortBy    | string | 非必须   | 排序字段，支持：id_asc, id_desc, title_asc, title_desc, created_asc, created_desc，默认id_asc |

请求参数样例：
/problem/list?page=1&pageSize=10&keyword=A+B&isHidden=0&sortBy=id_asc

#### 3.4.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 分页题目列表         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 10,
    "problems": [
      {
        "problemId": 201,
        "title": "A+B Problem",
        "timeLimit": 1000,
        "memoryLimit": 256,
        "creatorId": 2001,
        "isHidden": 0,
        "createdAt": "2025-01-01 10:00:00"
      }
    ]
  }
}
```

---

### 3.5 获取题目详情

#### 3.5.1 基本信息

- 请求路径：/problem/{problemId}
- 请求方式：GET
- 接口描述：获取题目详细信息

#### 3.5.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注               |
| ---------- | ------ | -------- | ------------------ |
| problemId | number | 必须     | 题目ID（路径参数） |

请求参数样例：
/problem/201

#### 3.5.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 题目详细信息         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "problemId": 201,
    "title": "A+B Problem",
    "description": "计算两个数之和",
    "inputSpec": "输入两个整数",
    "outputSpec": "输出一个整数",
    "sampleInput": "1 2",
    "sampleOutput": "3",
    "remark": "无",
    "timeLimit": 1000,
    "memoryLimit": 256,
    "creatorId": 2001,
    "isHidden": 0,
    "createdAt": "2025-01-01 10:00:00",
    "datasets": [
      {
        "datasetId": 1,
        "version": 1,
        "zipUrl": "url/to/dataset1.zip",
        "isActive": 1,
        "addedAt": "2025-01-01 10:00:00"
      }
    ]
  }
}
```

---

### 3.6 添加测试数据

#### 3.6.1 基本信息

- 请求路径：/problem/dataset/add
- 请求方式：POST
- 接口描述：为题目添加新的测试数据（只能增加，不能修改已有数据）

#### 3.6.2 请求参数

| 参数名       | 类型   | 是否必须 | 备注        |
| ------------ | ------ | -------- | ----------- |
| problemId | number | 必须     | 题目ID      |
| testdataZip | string | 必须     | 测试数据ZIP |

请求参数样例：

```json
{
  "problemId": 201,
  "testdataZip": "url/to/new_dataset.zip"
}
```

#### 3.6.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 数据集信息           |

响应数据样例：

```json
{
  "code": 1,
  "msg": "添加成功",
  "data": {
    "datasetId": 2,
    "problemId": 201,
    "version": 2
  }
}
```

---

### 3.7 发布题解报告

#### 3.7.1 基本信息

- 请求路径：/problem/solution/create
- 请求方式：POST
- 接口描述：教师为题目发布题解报告

#### 3.7.2 请求参数

| 参数名       | 类型   | 是否必须 | 备注                   |
| ------------ | ------ | -------- | ---------------------- |
| problemId | number | 必须     | 题目ID                 |
| title        | string | 必须     | 题解标题               |
| content      | string | 必须     | 题解内容               |
| isPublished | number | 非必须   | 是否发布（0/1），默认1 |

请求参数样例：

```json
{
  "problemId": 201,
  "title": "A+B Problem 题解",
  "content": "这是一道简单的加法题...",
  "isPublished": 1
}
```

#### 3.7.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 题解ID               |

响应数据样例：

```json
{
  "code": 1,
  "msg": "发布成功",
  "data": {
    "reportId": 301,
    "problemId": 201
  }
}
```

---

### 3.8 查询题解列表

#### 3.8.1 基本信息

- 请求路径：/problem/{problemId}/solution/list
- 请求方式：GET
- 接口描述：查询题目的题解报告列表

#### 3.8.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注               |
| ---------- | ------ | -------- | ------------------ |
| problemId | number | 必须     | 题目ID（路径参数） |

请求参数样例：
/problem/201/solution/list

#### 3.8.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | array  | 非必须   | 题解列表             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "reportId": 301,
      "title": "A+B Problem 题解",
      "creatorId": 2001,
      "isPublished": 1,
      "createdAt": "2025-01-01 15:00:00"
    }
  ]
}
```

---

## 4. 提交与评测

### 4.1 提交代码

#### 4.1.1 基本信息

- 请求路径：/submission/submit
- 请求方式：POST
- 接口描述：学生提交代码，自动评测

#### 4.1.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注       |
| ---------- | ------ | -------- | ---------- |
| contestId | number | 必须     | 赛事ID     |
| problemId | number | 必须     | 题目ID     |
| code       | string | 必须     | 代码内容   |
| language   | string | 必须     | 编程语言   |
| compiler   | string | 必须     | 编译器版本 |

请求参数样例：

```json
{
  "contestId": 101,
  "problemId": 201,
  "code": "print(sum(map(int,input().split())))",
  "language": "Python",
  "compiler": "Python 3.10"
}
```

#### 4.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 评测结果             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "提交成功",
  "data": {
    "result": "AC",
    "timeUsed": 12,
    "memoryUsed": 128,
    "judgeLogUrl": "url/to/log"
  }
}
```

---

### 4.2 查询提交记录

#### 4.2.1 基本信息

- 请求路径：/submission/list
- 请求方式：GET
- 接口描述：查询提交记录，支持分页、筛选和排序

#### 4.2.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注                                                                                                   |
| ---------- | ------ | -------- | ------------------------------------------------------------------------------------------------------ |
| page       | number | 非必须   | 页码，默认1                                                                                            |
| pageSize  | number | 非必须   | 每页数量，默认20                                                                                       |
| contestId | number | 非必须   | 赛事ID筛选                                                                                             |
| problemId | number | 非必须   | 题目ID筛选                                                                                             |
| userId | number | 非必须   | 用户ID筛选                                                                                             |
| result     | string | 非必须   | 评测结果筛选（AC/WA等）                                                                                |
| language   | string | 非必须   | 编程语言筛选                                                                                           |
| sortBy    | string | 非必须   | 排序字段，支持：id_asc, id_desc, created_asc, created_desc, time_used_asc, time_used_desc，默认id_desc |

请求参数样例：
/submission/list?page=1&pageSize=10&contestId=101&result=AC&sortBy=created_desc

#### 4.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 分页提交列表         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "submissions": [
      {
        "submissionId": 1001,
        "contestId": 101,
        "problemId": 201,
        "userId": 1001,
        "nickname": "张三",
        "language": "Python",
        "result": "AC",
        "timeUsed": 12,
        "memoryUsed": 128,
        "createdAt": "2025-04-01 10:30:00"
      }
    ]
  }
}
```

---

### 4.3 获取提交详情

#### 4.3.1 基本信息

- 请求路径：/submission/{submissionId}
- 请求方式：GET
- 接口描述：获取提交的详细信息和代码

#### 4.3.2 请求参数

| 参数名        | 类型   | 是否必须 | 备注               |
| ------------- | ------ | -------- | ------------------ |
| submissionId | number | 必须     | 提交ID（路径参数） |

请求参数样例：
/submission/1001

#### 4.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 提交详细信息         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "submissionId": 1001,
    "contestId": 101,
    "problemId": 201,
    "userId": 1001,
    "code": "print(sum(map(int,input().split())))",
    "codeLength": 35,
    "language": "Python",
    "compiler": "Python 3.10",
    "result": "AC",
    "timeUsed": 12,
    "memoryUsed": 128,
    "createdAt": "2025-04-01 10:30:00",
    "judgeLogUrl": "url/to/judge_log.txt"
  }
}
```

---

### 4.4 获取测试点结果

#### 4.4.1 基本信息

- 请求路径：/submission/{submissionId}/testcase
- 请求方式：GET
- 接口描述：获取提交的各测试点详细结果

#### 4.4.2 请求参数

| 参数名        | 类型   | 是否必须 | 备注               |
| ------------- | ------ | -------- | ------------------ |
| submissionId | number | 必须     | 提交ID（路径参数） |

请求参数样例：
/submission/1001/testcase

#### 4.4.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | array  | 非必须   | 测试点结果列表       |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "caseIndex": 1,
      "datasetVersion": 1,
      "result": "AC",
      "timeUsed": 10,
      "memoryUsed": 120,
      "message": ""
    },
    {
      "caseIndex": 2,
      "datasetVersion": 1,
      "result": "AC",
      "timeUsed": 8,
      "memoryUsed": 115,
      "message": ""
    }
  ]
}
```

---

## 5. 排名与积分榜

### 5.1 获取赛事排名

#### 5.1.1 基本信息

- 请求路径：/contest/{contestId}/rank
- 请求方式：GET
- 接口描述：获取赛事排名信息

#### 5.1.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注               |
| ---------- | ------ | -------- | ------------------ |
| contestId | number | 必须     | 赛事ID（路径参数） |

请求参数样例：
/contest/101/rank

#### 5.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | array  | 非必须   | 排名列表             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "userId": 1001,
      "nickname": "张三",
      "solvedCount": 3,
      "penaltyMinutes": 40
    }
  ]
}
```

---

### 5.2 获取积分榜统计

#### 5.2.1 基本信息

- 请求路径：/scoreboard/statistics
- 请求方式：POST
- 接口描述：教师选择多个赛事，统计学生在这些赛事中的表现

#### 5.2.2 请求参数

| 参数名      | 类型  | 是否必须 | 备注       |
| ----------- | ----- | -------- | ---------- |
| contestIds | array | 必须     | 赛事ID列表 |

请求参数样例：

```json
{
  "contestIds": [101, 102, 103]
}
```

#### 5.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 积分榜统计           |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "totalContests": 3,
    "statistics": [
      {
        "userId": 1001,
        "nickname": "张三",
        "totalParticipations": 3,
        "totalSolved": 8,
        "totalPenalty": 120,
        "averageSolved": 2.67,
        "contestDetails": [
          {
            "contestId": 101,
            "solvedCount": 3,
            "penaltyMinutes": 40
          }
        ]
      }
    ]
  }
}
```

---

### 5.3 获取排名详细信息

#### 5.3.1 基本信息

- 请求路径：/contest/rank/detail
- 请求方式：GET
- 接口描述：获取赛事排名的详细信息，包括每道题的用时和错误次数

#### 5.3.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注       |
| ---------- | ------ | -------- | ---------- |
| contestId | number | 必须     | 赛事ID     |
| userId | number | 非必须   | 用户ID筛选 |

请求参数样例：
/contest/rank/detail?contestId=101&userId=1001

#### 5.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | array  | 非必须   | 详细排名列表         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "userId": 1001,
      "nickname": "张三",
      "rank": 1,
      "solvedCount": 3,
      "penaltyMinutes": 40,
      "problemStats": [
        {
          "problemId": 201,
          "displayOrder": 1,
          "isSolved": true,
          "timeUsedMinutes": 15,
          "wrongAttempts": 1,
          "firstAcTime": "2025-04-01 09:15:00"
        },
        {
          "problemId": 202,
          "displayOrder": 2,
          "isSolved": true,
          "timeUsedMinutes": 25,
          "wrongAttempts": 0,
          "firstAcTime": "2025-04-01 09:25:00"
        }
      ]
    }
  ]
}
```

---

## 6. 训练计划

### 6.1 新建训练计划

#### 6.1.1 基本信息

- 请求路径：/training_plan/create
- 请求方式：POST
- 接口描述：教师新建训练计划

#### 6.1.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注       |
| ----------- | ------ | -------- | ---------- |
| title       | string | 必须     | 计划标题   |
| description | string | 非必须   | 计划说明   |
| startTime | string | 必须     | 开始时间   |
| endTime | string | 必须     | 结束时间   |
| contestIds | array  | 必须     | 赛事ID列表 |
| studentIds | array  | 必须     | 学生ID列表 |

请求参数样例：

```json
{
  "title": "春季集训",
  "description": "2025年春季集训计划",
  "startTime": "2025-03-01 09:00:00",
  "endTime": "2025-05-01 18:00:00",
  "contestIds": [101,102],
  "studentIds": [1001,1002]
}
```

#### 6.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 计划ID               |

响应数据样例：

```json
{
  "code": 1,
  "msg": "创建成功",
  "data": {
    "planId": 301
  }
}
```

---

### 6.2 修改训练计划

#### 6.2.1 基本信息

- 请求路径：/training_plan/update
- 请求方式：PUT
- 接口描述：教师修改训练计划（仅未进行部分可修改）

#### 6.2.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注       |
| ----------- | ------ | -------- | ---------- |
| planId | number | 必须     | 计划ID     |
| title       | string | 非必须   | 计划标题   |
| description | string | 非必须   | 计划说明   |
| startTime | string | 非必须   | 开始时间   |
| endTime | string | 非必须   | 结束时间   |
| contestIds | array  | 非必须   | 赛事ID列表 |
| studentIds | array  | 非必须   | 学生ID列表 |

请求参数样例：

```json
{
  "planId": 301,
  "title": "春季集训（修改版）",
  "description": "2025年春季集训计划修改版",
  "contestIds": [101,102,103]
}
```

#### 6.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 更新后计划信息       |

响应数据样例：

```json
{
  "code": 1,
  "msg": "修改成功",
  "data": {
    "planId": 301,
    "title": "春季集训（修改版）"
  }
}
```

---

### 6.3 删除训练计划

#### 6.3.1 基本信息

- 请求路径：/training_plan/{planId}
- 请求方式：DELETE
- 接口描述：删除训练计划（未开始可删除，已开始只能修改）

#### 6.3.2 请求参数

| 参数名  | 类型   | 是否必须 | 备注               |
| ------- | ------ | -------- | ------------------ |
| planId | number | 必须     | 计划ID（路径参数） |

请求参数样例：
/training_plan/301

#### 6.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 删除结果             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "删除成功",
  "data": {
    "planId": 301,
    "action": "deleted"
  }
}
```

---

### 6.4 查询训练计划列表

#### 6.4.1 基本信息

- 请求路径：/training_plan/list
- 请求方式：GET
- 接口描述：查询训练计划列表，支持分页、筛选和排序

#### 6.4.2 请求参数

| 参数名     | 类型   | 是否必须 | 备注                                                                                               |
| ---------- | ------ | -------- | -------------------------------------------------------------------------------------------------- |
| page       | number | 非必须   | 页码，默认1                                                                                        |
| pageSize  | number | 非必须   | 每页数量，默认20                                                                                   |
| state      | string | 非必须   | 计划状态筛选（UPCOMING/ONGOING/FINISHED）                                                          |
| creatorId | number | 非必须   | 创建者ID筛选                                                                                       |
| keyword    | string | 非必须   | 搜索关键字（计划标题）                                                                             |
| sortBy    | string | 非必须   | 排序字段，支持：id_asc, id_desc, title_asc, title_desc, start_time_asc, start_time_desc, created_asc, created_desc，默认id_asc |

请求参数样例：
/training_plan/list?page=1&pageSize=10&state=ONGOING&sortBy=start_time_desc

#### 6.4.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 分页计划列表         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 20,
    "page": 1,
    "pageSize": 10,
    "plans": [
      {
        "planId": 301,
        "title": "春季集训",
        "state": "ONGOING",
        "startTime": "2025-03-01 09:00:00",
        "endTime": "2025-05-01 18:00:00",
        "creatorId": 2001,
        "studentCount": 40,
        "contestCount": 8
      }
    ]
  }
}
```

---

### 6.5 获取训练计划详情

#### 6.5.1 基本信息

- 请求路径：/training_plan/{planId}
- 请求方式：GET
- 接口描述：获取训练计划详细信息，包括赛事安排和学生列表

#### 6.5.2 请求参数

| 参数名  | 类型   | 是否必须 | 备注               |
| ------- | ------ | -------- | ------------------ |
| planId | number | 必须     | 计划ID（路径参数） |

请求参数样例：
/training_plan/301

#### 6.5.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 计划详细信息         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "planId": 301,
    "title": "春季集训",
    "description": "2025年春季集训计划",
    "state": "ONGOING",
    "startTime": "2025-03-01 09:00:00",
    "endTime": "2025-05-01 18:00:00",
    "creatorId": 2001,
    "contests": [
      {
        "contestId": 101,
        "title": "第一周练习赛",
        "sequence": 1,
  "addedTime": "2025-03-08 14:00:00",
        "description": "基础算法练习"
      }
    ],
    "students": [
      {
        "userId": 1001,
        "nickname": "张三",
        "enrolledAt": "2025-02-28 10:00:00"
      }
    ]
  }
}
```

---

## 8. 文件管理

### 8.1 上传测试数据

#### 8.1.1 基本信息

- 请求路径：/file/upload/testdata
- 请求方式：POST
- 接口描述：上传题目测试数据ZIP文件

#### 8.1.2 请求参数

| 参数名 | 类型 | 是否必须 | 备注    |
| ------ | ---- | -------- | ------- |
| file   | file | 必须     | ZIP文件 |

请求参数样例：
Content-Type: multipart/form-data

#### 8.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 文件信息             |

响应数据样例：

```json
{
  "code": 1,
  "msg": "上传成功",
  "data": {
    "fileUrl": "https://example.com/uploads/testdata/abc123.zip",
    "fileSize": 102400,
    "uploadTime": "2025-01-01 10:00:00"
  }
}
```

---

### 8.2 下载评测日志

#### 8.2.1 基本信息

- 请求路径：/file/download/judge_log
- 请求方式：GET
- 接口描述：下载提交的评测日志文件

#### 8.2.2 请求参数

| 参数名        | 类型   | 是否必须 | 备注               |
| ------------- | ------ | -------- | ------------------ |
| submissionId | number | 必须     | 提交ID（路径参数） |

请求参数样例：
/file/download/judge_log/1001

#### 8.2.3 响应数据

- 参数格式：application/octet-stream
- 直接返回文件流或重定向到文件URL

---

## 7. 通用约定

- 所有需要鉴权的接口需传递 token（建议用 Authorization 头部 Bearer Token 方式）
- 文件上传接口需支持 multipart/form-data
- 列表接口需支持分页参数
- 错误返回统一格式：

```json
{
  "code": 0,
  "msg": "错误描述",
  "data": null
}
```
