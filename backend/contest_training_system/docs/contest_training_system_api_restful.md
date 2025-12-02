# 竞赛集训系统接口文档（RESTful风格）

---

## 1. 用户管理

### 1.1 注册用户

#### 1.1.1 基本信息

- 请求路径：/register
- 请求方式：POST
- 接口描述：用户注册，创建新账号

#### 1.1.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注   |
| ----------- | ------ | -------- | ------ |
| email       | string | 必须     | 邮箱   |
| phoneNumber | string | 必须     | 手机号 |
| nickname    | string | 必须     | 昵称   |
| studentId   | string | 必须     | 学号   |
| password    | string | 必须     | 密码   |

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

| 参数名 | 类型   | 是否必须 | 备注                         |
| ------ | ------ | -------- | ---------------------------- |
| code   | number | 必须     | 响应码，1成功，0失败         |
| msg    | string | 非必须   | 提示信息                     |
| data   | object | 非必须   | 返回数据（注册成功时为null） |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 1.2 用户登录

#### 1.2.1 基本信息

- 请求路径：/login
- 请求方式：POST
- 接口描述：用户登录，获取token

#### 1.2.2 请求参数

| 参数名       | 类型   | 是否必须 | 备注         |
| ------------ | ------ | -------- | ------------ |
| emailOrPhone | string | 必须     | 邮箱或手机号 |
| password     | string | 必须     | 密码         |

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
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

### 1.3 注销用户

#### 1.3.1 基本信息

- 请求路径：/user/deactivate
- 请求方式：POST
- 接口描述：注销当前用户（需要在请求头携带JWT Token）

#### 1.3.2 请求参数

无需请求参数，系统自动从 JWT Token 中获取当前用户信息

请求头：

```
Authorization: Bearer <your_jwt_token>
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
  "msg": "success",
  "data": null
}
```

---

### 1.4 更新用户信息

#### 1.4.1 基本信息

- 请求路径：/user/update
- 请求方式：PUT
- 接口描述：更新用户基本信息（需要在请求头携带JWT Token）

#### 1.4.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注   |
| ----------- | ------ | -------- | ------ |
| nickname    | string | 非必须   | 昵称   |
| phoneNumber | string | 非必须   | 手机号 |
| email       | string | 非必须   | 邮箱   |
| studentId   | string | 非必须   | 学号   |
| password    | string | 非必须   | 新密码 |

请求参数样例：

```json
{
  "nickname": "新昵称",
  "phoneNumber": "13900000000",
  "email": "newemail@example.com",
  "password": "newpassword"
}
```

#### 1.4.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                         |
| ------ | ------ | -------- | ---------------------------- |
| code   | number | 必须     | 响应码，1成功，0失败         |
| msg    | string | 非必须   | 提示信息                     |
| data   | object | 非必须   | 返回数据（更新成功时为null） |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
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
    "createdAt": "2025-01-01 10:00:00",
    "isActive": 1
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

| 参数名   | 类型   | 是否必须 | 备注                                                                                                |
| -------- | ------ | -------- | --------------------------------------------------------------------------------------------------- |
| page     | number | 非必须   | 页码，默认1                                                                                         |
| pageSize | number | 非必须   | 每页数量，默认10                                                                                    |
| keyword  | string | 非必须   | 搜索关键字（昵称/邮箱/学号）                                                                        |
| role     | string | 非必须   | 用户角色筛选（STUDENT/TEACHER/ADMIN）                                                               |
| isActive | number | 非必须   | 用户状态筛选（0/1）                                                                                 |
| sortBy   | string | 非必须   | 排序字段，支持：id_asc, id_desc, nickname_asc, nickname_desc, created_asc, created_desc，默认id_asc |

请求参数样例：
/admin/users?page=1&pageSize=10&keyword=张&role=STUDENT&isActive=1&sortBy=created_desc

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
    "pageSize": 10,
    "list": [
      {
        "userId": 1001,
        "email": "test@example.com",
        "phoneNumber": "13800000000",
        "nickname": "张三",
        "studentId": "20250001",
        "role": "STUDENT",
        "isActive": 1,
        "createdAt": "2025-01-01T10:00:00"
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
| startTime   | string | 必须     | 开始时间       |
| endTime     | string | 必须     | 结束时间       |
| password    | string | 非必须   | 赛事密码       |
| visibility  | string | 必须     | PUBLIC/PRIVATE |
| problemIds  | array  | 必须     | 题目ID列表     |

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
  "msg": "success",
  "data": 101
}
```

---

### 2.2 删除赛事

#### 2.2.1 基本信息

- 请求路径：/contest/{contestId}
- 请求方式：DELETE
- 接口描述：根据ID删除赛事

#### 2.2.2 请求参数

| 参数名    | 类型   | 是否必须 | 备注   |
| --------- | ------ | -------- | ------ |
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
  "msg": "success",
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
| contestId   | number | 必须     | 赛事ID         |
| title       | string | 非必须   | 赛事名称       |
| description | string | 非必须   | 赛事介绍       |
| startTime   | string | 非必须   | 开始时间       |
| endTime     | string | 非必须   | 结束时间       |
| password    | string | 非必须   | 赛事密码       |
| visibility  | string | 非必须   | PUBLIC/PRIVATE |
| problemIds  | array  | 非必须   | 题目ID列表     |

请求参数样例：

```json
{
  "contestId": 101,
  "title": "2025年春季赛（修改版）",
  "description": "春季编程竞赛修改版",
  "startTime": "2025-04-01 09:00:00",
  "endTime": "2025-04-01 12:00:00",
  "problemIds": [1,2,3,4]
}
```

#### 2.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                         |
| ------ | ------ | -------- | ---------------------------- |
| code   | number | 必须     | 响应码，1成功，0失败         |
| msg    | string | 非必须   | 提示信息                     |
| data   | object | 非必须   | 返回数据（更新成功时为null） |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
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
| pageSize   | number | 非必须   | 每页数量，默认10                                                                                    |
| state      | string | 非必须   | 赛事数据状态筛选（HIDDEN/USING）                                                                    |
| status     | string | 非必须   | 赛事时间状态筛选（SCHEDULED/ONGOING/ENDED）                                                         |
| visibility | string | 非必须   | 赛事可见性筛选（PUBLIC/PRIVATE）                                                                    |
| keyword    | string | 非必须   | 搜索关键字（赛事标题）                                                                              |
| sortBy     | string | 非必须   | 排序字段，支持：id_asc, id_desc, title_asc, title_desc, start_time_asc, start_time_desc，默认id_asc |

请求参数样例：
/contest/list?page=1&pageSize=10&visibility=PUBLIC&status=ONGOING&sortBy=start_time_desc

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
    "list": [
      {
        "contestId": 101,
        "title": "2025年春季赛",
        "description": "春季编程竞赛",
        "startTime": "2025-04-01T09:00:00",
        "endTime": "2025-04-01T12:00:00",
        "visibility": "PUBLIC",
        "status": "SCHEDULED",
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

| 参数名    | 类型   | 是否必须 | 备注               |
| --------- | ------ | -------- | ------------------ |
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
    "startTime": "2025-04-01T09:00:00",
    "endTime": "2025-04-01T12:00:00",
    "visibility": "PUBLIC",
    "status": "SCHEDULED",
    "state": "USING",
    "password": null,
    "creatorId": 2001,
    "createdAt": "2025-03-01T10:00:00",
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

| 参数名    | 类型   | 是否必须 | 备注     |
| --------- | ------ | -------- | -------- |
| contestId | number | 必须     | 赛事ID   |
| password  | string | 非必须   | 赛事密码 |
| teamName  | string | 非必须   | 队伍名   |

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
  "msg": "success",
  "data": null
}
```

---

### 2.7 检查是否已参赛

#### 2.7.1 基本信息

- 请求路径：/contest/hasJoined
- 请求方式：POST
- 接口描述：检查用户是否已经参加了指定赛事

#### 2.7.2 请求参数

| 参数名    | 类型   | 是否必须 | 备注     |
| --------- | ------ | -------- | -------- |
| contestId | number | 必须     | 赛事ID   |
| password  | string | 非必须   | 赛事密码 |
| teamName  | string | 非必须   | 队伍名   |

请求参数样例：

```json
{
  "contestId": 101,
  "password": "abc123"
}
```

#### 2.7.3 响应数据

- 参数格式：application/json

| 参数名 | 类型    | 是否必须 | 备注                 |
| ------ | ------- | -------- | -------------------- |
| code   | number  | 必须     | 响应码，1成功，0失败 |
| msg    | string  | 非必须   | 提示信息             |
| data   | boolean | 非必须   | 是否已参赛           |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": true
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

| 参数名       | 类型   | 是否必须 | 备注         |
| ------------ | ------ | -------- | ------------ |
| title        | string | 必须     | 题目标题     |
| description  | string | 必须     | 题目描述     |
| inputSpec    | string | 必须     | 输入说明     |
| outputSpec   | string | 必须     | 输出说明     |
| sampleInput  | string | 必须     | 输入样例     |
| sampleOutput | string | 必须     | 输出样例     |
| remark       | string | 非必须   | 备注         |
| timeLimit    | number | 必须     | 时间限制(ms) |
| memoryLimit  | number | 必须     | 内存限制(MB) |
| testdataZip  | string | 必须     | 测试数据ZIP  |

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
    "createdAt": "2025-01-01T10:00:00",
    "testdataZip": "url/to/zip"
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

| 参数名       | 类型   | 是否必须 | 备注            |
| ------------ | ------ | -------- | --------------- |
| problemId    | number | 必须     | 题目ID          |
| title        | string | 非必须   | 题目标题        |
| description  | string | 非必须   | 题目描述        |
| inputSpec    | string | 非必须   | 输入说明        |
| outputSpec   | string | 非必须   | 输出说明        |
| sampleInput  | string | 非必须   | 输入样例        |
| sampleOutput | string | 非必须   | 输出样例        |
| remark       | string | 非必须   | 备注            |
| timeLimit    | number | 非必须   | 时间限制(ms)    |
| memoryLimit  | number | 非必须   | 内存限制(MB)    |
| isHidden     | number | 非必须   | 是否隐藏（0/1） |

请求参数样例：

```json
{
  "problemId": 201,
  "title": "A+B Problem（修改版）",
  "description": "计算两个数之和（修改版）",
  "timeLimit": 2000,
  "isHidden": 0
}
```

#### 3.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                               |
| ------ | ------ | -------- | ---------------------------------- |
| code   | number | 必须     | 响应码，1成功，0失败               |
| msg    | string | 非必须   | 提示信息                           |
| data   | object | 非必须   | 返回数据（更新成功时为字符串消息） |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": "题目更新成功"
}
```

---

### 3.3 删除/隐藏题目

#### 3.3.1 基本信息

- 请求路径：/problem/{problemId}
- 请求方式：DELETE
- 接口描述：删除或隐藏题目（如果题目已在赛事中使用则隐藏，否则删除）

#### 3.3.2 请求参数

| 参数名    | 类型   | 是否必须 | 备注               |
| --------- | ------ | -------- | ------------------ |
| problemId | number | 必须     | 题目ID（路径参数） |

请求参数样例：
/problem/201

#### 3.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | string | 非必须   | 操作结果消息         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": "题目已隐藏（在赛事中使用）"
}
```

---

### 3.4 查询题目列表

#### 3.4.1 基本信息

- 请求路径：/problem/list
- 请求方式：GET
- 接口描述：查询题目列表，支持分页、筛选和排序

#### 3.4.2 请求参数

| 参数名    | 类型   | 是否必须 | 备注                                                                                          |
| --------- | ------ | -------- | --------------------------------------------------------------------------------------------- |
| page      | number | 非必须   | 页码，默认1                                                                                   |
| pageSize  | number | 非必须   | 每页数量，默认10                                                                              |
| keyword   | string | 非必须   | 搜索关键字（题目标题）                                                                        |
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
    "list": [
      {
        "problemId": 201,
        "title": "A+B Problem",
        "timeLimit": 1000,
        "memoryLimit": 256,
        "creatorId": 2001,
        "isHidden": 0,
        "createdAt": "2025-01-01T10:00:00"
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

| 参数名    | 类型   | 是否必须 | 备注               |
| --------- | ------ | -------- | ------------------ |
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
    "createdAt": "2025-01-01T10:00:00",
    "datasets": [
      {
        "datasetId": 1,
        "problemId": 201,
        "version": 1,
        "zipUrl": "url/to/dataset1.zip",
        "isActive": 1,
        "addedAt": "2025-01-01T10:00:00"
      }
    ]
  }
}
```

---

## 4. 训练计划

### 4.1 新建训练计划

#### 4.1.1 基本信息

- 请求路径：/training_plan/create
- 请求方式：POST
- 接口描述：教师新建训练计划

#### 4.1.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注       |
| ----------- | ------ | -------- | ---------- |
| title       | string | 必须     | 计划标题   |
| description | string | 非必须   | 计划说明   |
| startTime   | string | 必须     | 开始时间   |
| endTime     | string | 必须     | 结束时间   |
| contestIds  | array  | 必须     | 赛事ID列表 |
| studentIds  | array  | 必须     | 学生ID列表 |

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

#### 4.1.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | number | 非必须   | 计划ID               |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": 301
}
```

---

### 4.2 修改训练计划

#### 4.2.1 基本信息

- 请求路径：/training_plan/update
- 请求方式：PUT
- 接口描述：教师修改训练计划

#### 4.2.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注       |
| ----------- | ------ | -------- | ---------- |
| planId      | number | 必须     | 计划ID     |
| title       | string | 非必须   | 计划标题   |
| description | string | 非必须   | 计划说明   |
| startTime   | string | 非必须   | 开始时间   |
| endTime     | string | 非必须   | 结束时间   |
| contestIds  | array  | 非必须   | 赛事ID列表 |
| studentIds  | array  | 非必须   | 学生ID列表 |

请求参数样例：

```json
{
  "planId": 301,
  "title": "春季集训（修改版）",
  "description": "2025年春季集训计划修改版",
  "contestIds": [101,102,103]
}
```

#### 4.2.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                         |
| ------ | ------ | -------- | ---------------------------- |
| code   | number | 必须     | 响应码，1成功，0失败         |
| msg    | string | 非必须   | 提示信息                     |
| data   | object | 非必须   | 返回数据（更新成功时为null） |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 4.3 删除训练计划

#### 4.3.1 基本信息

- 请求路径：/training_plan/{planId}
- 请求方式：DELETE
- 接口描述：删除训练计划

#### 4.3.2 请求参数

| 参数名 | 类型   | 是否必须 | 备注               |
| ------ | ------ | -------- | ------------------ |
| planId | number | 必须     | 计划ID（路径参数） |

请求参数样例：
/training_plan/301

#### 4.3.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                         |
| ------ | ------ | -------- | ---------------------------- |
| code   | number | 必须     | 响应码，1成功，0失败         |
| msg    | string | 非必须   | 提示信息                     |
| data   | object | 非必须   | 返回数据（删除成功时为null） |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 4.4 查询训练计划列表

#### 4.4.1 基本信息

- 请求路径：/training_plan/list
- 请求方式：GET
- 接口描述：查询训练计划列表，支持分页、筛选和排序

#### 4.4.2 请求参数

| 参数名    | 类型   | 是否必须 | 备注                                                                                                |
| --------- | ------ | -------- | --------------------------------------------------------------------------------------------------- |
| page      | number | 非必须   | 页码，默认1                                                                                         |
| pageSize  | number | 非必须   | 每页数量，默认10                                                                                    |
| status    | string | 非必须   | 计划状态筛选（SCHEDULED/ONGOING/ENDED）                                                             |
| creatorId | number | 非必须   | 创建者ID筛选                                                                                        |
| keyword   | string | 非必须   | 搜索关键字（计划标题）                                                                              |
| sortBy    | string | 非必须   | 排序字段，支持：id_asc, id_desc, title_asc, title_desc, start_time_asc, start_time_desc，默认id_asc |

请求参数样例：
/training_plan/list?page=1&pageSize=10&status=ONGOING&sortBy=start_time_desc

#### 4.4.3 响应数据

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
    "list": [
      {
        "planId": 301,
        "title": "春季集训",
        "status": "ONGOING",
        "startTime": "2025-03-01T09:00:00",
        "endTime": "2025-05-01T18:00:00",
        "creatorId": 2001,
        "studentCount": 40,
        "contestCount": 8
      }
    ]
  }
}
```

---

### 4.5 获取训练计划详情

#### 4.5.1 基本信息

- 请求路径：/training_plan/{planId}
- 请求方式：GET
- 接口描述：获取训练计划详细信息，包括赛事安排和学生列表

#### 4.5.2 请求参数

| 参数名 | 类型   | 是否必须 | 备注               |
| ------ | ------ | -------- | ------------------ |
| planId | number | 必须     | 计划ID（路径参数） |

请求参数样例：
/training_plan/301

#### 4.5.3 响应数据

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
    "status": "ONGOING",
    "startTime": "2025-03-01T09:00:00",
    "endTime": "2025-05-01T18:00:00",
    "creatorId": 2001,
    "contests": [
      {
        "contestId": 101,
        "title": "第一周练习赛",
        "sequence": 1,
        "scheduledTime": "2025-03-08T14:00:00",
        "description": "基础算法练习"
      }
    ],
    "students": [
      {
        "userId": 1001,
        "nickname": "张三",
        "enrolledAt": "2025-02-28T10:00:00"
      }
    ]
  }
}
```

---

## 5. 通用约定

- 所有需要鉴权的接口需在请求头传递 JWT Token（Authorization: Bearer `<token>`）
- 所有时间字段统一使用 ISO 8601 格式（例如：2025-04-01T09:00:00）
- 列表接口统一支持分页参数（page, pageSize）
- 分页响应统一使用 PageResultVO 格式，包含 total, page, pageSize, list
- 错误返回统一格式：

```json
{
  "code": 0,
  "msg": "错误描述",
  "data": null
}
```

---

### 3.6 发布题解

#### 3.6.1 基本信息

- 请求路径：/problem/solution/create
- 请求方式：POST
- 接口描述：发布题解（需要登录）

#### 3.6.2 请求参数

| 参数名      | 类型   | 是否必须 | 备注                  |
| ----------- | ------ | -------- | --------------------- |
| problemId   | number | 必须     | 题目ID                |
| title       | string | 必须     | 题解标题              |
| content     | string | 必须     | 题解内容              |
| isPublished | number | 非必须   | 是否发布（0/1），默认1 |

请求参数样例：

```json
{
  "problemId": 201,
  "title": "A+B Problem 题解",
  "content": "本题使用简单的加法即可...",
  "isPublished": 1
}
```

#### 3.6.3 响应数据

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
  "msg": "success",
  "data": null
}
```

---

### 3.7 查询题解列表

#### 3.7.1 基本信息

- 请求路径：/problem/{problemId}/solution/list
- 请求方式：GET
- 接口描述：查询指定题目的题解列表

#### 3.7.2 请求参数

| 参数名    | 类型   | 是否必须 | 备注                                  |
| --------- | ------ | -------- | ------------------------------------- |
| problemId | number | 必须     | 题目ID（路径参数）                    |
| page      | number | 非必须   | 页码，默认1                           |
| pageSize  | number | 非必须   | 每页数量，默认10                      |
| sortBy    | string | 非必须   | 排序字段，支持：created_desc，默认id_desc |

请求参数样例：
/problem/201/solution/list?page=1&pageSize=10

#### 3.7.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 分页题解列表         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "reportId": 1,
        "problemId": 201,
        "title": "A+B Problem 题解",
        "creatorId": 1001,
        "creatorName": "张三",
        "createdAt": "2025-01-02T10:00:00"
      }
    ]
  }
}
```

---

### 3.8 获取题解详情

#### 3.8.1 基本信息

- 请求路径：/problem/solution/{reportId}
- 请求方式：GET
- 接口描述：获取题解详细信息

#### 3.8.2 请求参数

| 参数名   | 类型   | 是否必须 | 备注               |
| -------- | ------ | -------- | ------------------ |
| reportId | number | 必须     | 题解ID（路径参数） |

请求参数样例：
/problem/solution/1

#### 3.8.3 响应数据

- 参数格式：application/json

| 参数名 | 类型   | 是否必须 | 备注                 |
| ------ | ------ | -------- | -------------------- |
| code   | number | 必须     | 响应码，1成功，0失败 |
| msg    | string | 非必须   | 提示信息             |
| data   | object | 非必须   | 题解详细信息         |

响应数据样例：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "reportId": 1,
    "problemId": 201,
    "title": "A+B Problem 题解",
    "content": "详细内容...",
    "creatorId": 1001,
    "creatorName": "张三",
    "createdAt": "2025-01-02T10:00:00"
  }
}
```

---

## 6. 状态说明

### 6.1 赛事状态 (status)

| 状态      | 说明   |
| --------- | ------ |
| SCHEDULED | 未开始 |
| ONGOING   | 进行中 |
| ENDED     | 已结束 |

### 6.2 赛事数据状态 (state)

| 状态   | 说明   |
| ------ | ------ |
| USING  | 使用中 |
| HIDDEN | 已隐藏 |

### 6.3 训练计划状态 (status)

| 状态      | 说明   |
| --------- | ------ |
| SCHEDULED | 未开始 |
| ONGOING   | 进行中 |
| ENDED     | 已结束 |

### 6.4 用户角色 (role)

| 角色    | 说明   |
| ------- | ------ |
| STUDENT | 学生   |
| TEACHER | 教师   |
| ADMIN   | 管理员 |

---

## 附录：未实现的接口

以下接口在当前版本中尚未实现，将在后续版本中补充：

1. **提交与评测模块**

   - POST /submission/submit - 提交代码
   - GET /submission/list - 查询提交记录
   - GET /submission/{submissionId} - 获取提交详情
   - GET /submission/{submissionId}/testcase - 获取测试点结果
2. **排名与积分榜模块**

   - GET /contest/{contestId}/rank - 获取赛事排名
   - POST /scoreboard/statistics - 获取积分榜统计
   - GET /contest/rank/detail - 获取排名详细信息
3. **测试数据管理模块**

   - POST /problem/dataset/add - 添加测试数据

