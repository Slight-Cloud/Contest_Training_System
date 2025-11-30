# 竞赛训练系统后端-深度解析与开发者手册

---

## 1. 项目概述与愿景

### 1.1. 引言

本项目是**竞赛训练系统(Contest Training System)**的后端服务，一个专为高校、编程爱好者及教育机构设计的，集在线编程、实时评测、竞赛组织、系统化训练于一体的综合性平台。它不仅仅是一个简单的在线判题（Online Judge）系统，更是一个旨在通过结构化的训练计划和模拟竞赛，系统性提升用户算法和编程能力的学习与实践平台。
后端服务是整个系统的“大脑”和“中枢”，采用Java技术栈和业界领先的Spring Boot框架构建。它负责处理所有的业务逻辑、数据持久化、用户认证与授权，并通过一套设计精良的RESTful API与前端Web应用、未来可能的移动客户端或其他第三方服务进行高效、安全的数据交互。
本文档旨在提供一个关于此后端项目*深度解析和详尽的开发者手册*。它将全面覆盖项目的设计理念、技术架构、模块功能、API接口、数据模型、开发规范和部署策略，为项目的维护、二次开发和新功能迭代提供坚实的基础和清晰的指引。

### 1.2. 设计理念与目标

本项目的核心设计理念是*“以赛促学，以练固本”*。我们追求的不仅仅是功能的实现，更是一种能够激发用户学习兴趣、引导用户持续进步的体验。

- **模块化与可扩展性**: 系统被精心设计为一系列松耦合的模块（如用户、题目、竞赛、训练计划等）。这种设计不仅使得当前功能易于维护，更为未来的功能扩展（如在线IDE、社区讨论、代码分享、AI辅助等）预留了清晰的接口和空间。
- **标准化与规范性**: 全面拥抱RESTful架构风格，API设计遵循统一的命名和状态码规范。代码层面，我们遵循业界广泛认可的Java开发规范，并利用现代IDE和构建工具来保证代码质量和一致性。
- **安全与稳定**: 安全是系统的生命线。我们采用了多层次的安全策略，包括基于JWT的无状态认证、精细到接口级别的角色权限控制（RBAC）、以及对所有外部输入的严格校验，以抵御常见的Web攻击，确保系统和用户数据的安全。
- **高效与性能**: 在关键路径（如代码评测、排行榜生成）上，我们关注性能优化。通过合理的数据库设计、索引优化、以及未来可引入的缓存策略，确保系统在高并发场景下依然能够提供稳定、及时的响应。

### 1.3. 目标用户

- **学生/编程爱好者**: 系统的主要用户，他们可以通过平台进行日常练习、参加模拟竞赛、完成老师布置的训练任务，从而提升自己的编程和算法能力。
- **教师/教练**: 可以在平台上创建私有题目、组织班级竞赛、发布训练计划并跟踪学生的学习进度，实现教学管理的数字化和自动化。
- **系统管理员**: 负责整个平台的维护、用户管理、系统配置和内容审核，确保平台的稳定运行和内容的健康。

---

## 2. 核心架构与技术选型

### 2.1. 架构图

```
+-----------------------------------------------------------------------------+
|                            客户侧(Clients)                                |
| (Web App / Mobile App / Third-party Services)                               |
+---------------------------------^-------------------------------------------+
                                  |
                                  |
                                  v
+----------------------------- Nginx -----------------------------------------+
| (Reverse Proxy, Load Balancer, SSL Termination)                             |
+---------------------------------^-------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------------+
|                      竞赛训练系统后端 (Spring Boot App)                       |
|                                                                             |
|    +---------------------------------------------------------------------+
|    |                      Spring Security (Filter Chain)                 |
|    |    +-----------------------------------------------------------+    |
|    |    |   JwtAuthenticationFilter (JWT校验与认证)                 |
|    |    |   RoleBasedAuthorization  (@PreAuthorize注解, 权限控制)   |
|    |    +-----------------------------------------------------------+    |
|    +-----------------------------------^---------------------------------|
|                                        |
|    +---------------------------------------------------------------------+
|    |                           Web层(Controller)                       |
|    | (UserController, ContestController, ProblemController, etc.)        |
|    | - 接收/响应HTTP请求                                                   |
|    | - 验证输入DTO (Data Transfer Object)                                |
|    | - 调用Service层处理业务                                             |
|    | - 封装VO (View Object) 返回JSON                                     |
|    +-----------------------------------^---------------------------------|
|                                        |
|    +---------------------------------------------------------------------+
|    |                          业务逻辑层(Service)                      |
|    | (UserService, ContestService, ProblemService, etc.)                 |
|    | - 实现核心业务逻辑                                                  |
|    | - 事务管理 (@Transactional)                                         |
|    | - 组合Mapper/Repository操作                                         |
|    +-----------------------------------^---------------------------------|
|                                        |
|    +---------------------------------------------------------------------+
|    |                         数据访问层(Mapper/DAO)                    |
|    | (UserMapper, ContestMapper, ProblemMapper, etc. - MyBatis Interfaces)|
|    | - 定义数据库原子操作(CRUD)                                          |
|    | - 与MyBatis XML或注解SQL映射                                        |
|    +-----------------------------------^---------------------------------|
|                                        |
+----------------------------------------|------------------------------------+
                                         |
                                         v
+-----------------------------------------------------------------------------+
|                          数据持久层(Database)                             |
|    +--------------------------------------------------------------------+
|    |                          MySQL Database                          |
|    | (User Table, Problem Table, Contest Table, Submissions, etc.)    |
|    +--------------------------------------------------------------------+
+-----------------------------------------------------------------------------+

```

### 2.2. 分层架构详解

本项目采用了经典且成熟的**三层架构**模式，确保了各层职责单一、边界清晰，从而提高了代码的可读性、可维护性和可测试性。

- **表示层(Presentation Layer)**:
  - **实现**: 主要由 `controller`包下的Spring MVC控制器构成。
  - **职责**: 作为系统的HTTP入口，它负责接收来自客户端的请求，并将请求的报文（如JSON）解析和封装成数据传输对象（DTO）。在处理完业务逻辑后，将结果封装成视图对象（VO）并以JSON格式返回给客户端。此层还负责基础的输入验证（使用 `jakarta.validation`注解）和全局异常处理。
- **业务逻辑层(Business Logic Layer)**:
  - **实现**: 主要由 `service`包下的服务类构成。
  - **职责**: 这是系统核心业务逻辑的所在地。Service层通过组合调用 `mapper`层的原子数据操作，来编排和实现复杂的业务场景，例如“用户报名参加竞赛”可能涉及到查询竞赛信息、检查用户资格、创建报名记录等多个步骤。同时，此层也是数据*事务管理*的核心，通过 `@Transactional`注解确保数据的一致性。
- **数据访问层(Data Access Layer)**:
  - **实现**: 由 `mapper`包下的MyBatis接口以及 `resources/mapper`目录下的XML文件构成。
  - **职责**: 负责与数据库进行直接交互。它将上层业务逻辑与底层数据存储技术（MySQL）解耦。每个Mapper接口都定义了一组对特定数据表（或视图）的增、删、改、查（CRUD）操作，具体的SQL语句则在XML文件或通过注解实现。

### 2.3. 技术栈详情

- **核心框架: Spring Boot**: 提供了“开箱即用”的自动化配置，极大地简化了Spring应用的搭建和开发过程。内置Tomcat服务器，使得应用可以打包成一个可执行的JAR文件独立运行。
- **Web框架: Spring MVC**: 基于模型-视图-控制器（MVC）模式，提供了构建Web应用的强大功能，包括灵活的URL映射、请求参数绑定、视图解析等。
- **安全框架: Spring Security + JWT**:
  - **Spring Security**: 提供全面的身份认证和授权功能。本项目使用其Filter链机制来集成JWT。
  - **JWT (JSON Web Tokens)**: 作为无状态认证的核心。用户登录成功后，服务器会签发一个包含用户身份和权限信息的Token。客户端在后续请求中携带此Token，服务器通过校验Token的签名来认证用户，无需依赖Session，非常适合分布式和前后端分离的架构。
- **持久层框架: MyBatis**: 一个优秀的SQL映射框架。它允许开发者将业务逻辑与SQL语句分离，能够编写原生的、高度优化的SQL，同时提供了强大的动态SQL功能和缓存机制。
  - **PageHelper**: 一个MyBatis的分页插件，通过AOP拦截的方式，无侵入地实现物理分页，极大简化了分页查询的开发。
- **构建工具: Maven**: 强大的Java项目管理和构建工具。通过 `pom.xml`文件管理项目依赖、插件和构建生命周期，实现了项目构建的自动化和标准化。
- **数据库: MySQL**: 全球最受欢迎的开源关系型数据库之一，以其稳定性、高性能和成熟的社区支持而著称。

---

## 3. 项目结构

本项目遵循标准的Maven项目布局。核心代码位于 `src/main/java/com/system` 目录下，并按照功能和分层架构的原则进行了组织。

```
contest_training_system
├── pom.xml                   # Maven项目配置文件，定义项目依赖和构建方式
└── src
    └── main
        ├── java
        │   └── com
        │       └── system
        │           ├── config              # 配置类 (如 SecurityConfig, WebConfig)
        │           ├── controller          # 控制器层 (处理HTTP请求)
        │           ├── dto                 # 数据传输对象 (用于前端请求数据封装)
        │           ├── entity              # 数据库实体类
        │           ├── exception           # 全局异常处理
        │           ├── mapper              # MyBatis数据访问接口 (DAO)
        │           ├── service             # 业务逻辑层
        │           ├── util                # 工具类 (如 JwtUtil, UserContext)
        │           └── vo                  # 视图对象 (用于向前端返回数据)
        └── resources
            ├── application.yml     # Spring Boot主配置文件
            └── mapper              # MyBatis的XML映射文件 (存放SQL语句)
```

### 3.1. 主要包功能说明

- **`config`**: 存放应用的配置类。例如 `SecurityConfig` 用于配置Spring Security的认证和授权规则。
- **`controller`**: 表示层的实现，负责接收HTTP请求，调用 `service`层处理业务，并返回JSON响应。
- **`dto`** (Data Transfer Object): 用于封装从客户端（前端）传入的数据，在各层之间传递。
- **`entity`**: 与数据库表结构一一对应的Java对象，是数据持久化的基础。
- **`exception`**: 定义全局异常处理器（`@RestControllerAdvice`），用于捕获和处理业务异常，并返回统一的错误响应。
- **`mapper`**: 数据访问层的接口，定义了对数据库的原子操作（CRUD）。
- **`service`**: 业务逻辑的核心，组合 `mapper`层的操作，实现复杂的业务功能，并管理事务。
- **`util`**: 存放各种通用的工具类，如JWT生成/解析、`ThreadLocal`用户上下文等。
- **`vo`** (View Object): 用于封装向客户端（前端）返回的数据，通常会根据展示需求对 `entity`进行裁剪或聚合。

---

## 4. 快速入门与开发指南 (Getting Started)

本指南旨在帮助新加入的开发者快速搭建开发环境，并成功运行本项目。

### 4.1. 环境要求

- **JDK**: `17` 或更高版本。
- **Maven**: `3.6` 或更高版本。
- **IDE**: 推荐 `IntelliJ IDEA` (Community 或 Ultimate版均可)。
- **数据库**: `MySQL 8.0` 或更高版本。
- **Git**: 用于代码版本控制。

### 4.2. 设置步骤

#### 1. 克隆代码仓库

```bash
git clone <your-repository-url>
cd ContestSystem/backend/contest_training_system
```

#### 2. 配置数据库

1. 在本地MySQL服务器中创建一个新的数据库。
   ```sql
   CREATE DATABASE contest_system_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. 导入数据库结构。如果项目中有SQL初始化脚本（如 `init.sql`），请执行它以创建所有表。

#### 3. 配置 `application.yml`

打开 `src/main/resources/application.yml` 文件，修改 `spring.datasource` 部分以匹配你的本地数据库设置。

```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/contest_system_db?useSSL=false&serverTimezone=UTC
    username: your_mysql_username
    password: your_mysql_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 4. 配置 `JwtUtil.java`

**重要**: 打开 `src/main/java/com/system/util/JwtUtil.java` 并修改 `SECRET`常量。这是一个用于签名JWT的密钥，必须保证其复杂性和保密性。

```java
// 更改这个密钥！
private static final String SECRET = "a-very-long-and-random-secret-key-that-is-at-least-256-bits";
```

#### 5. 在IntelliJ IDEA中打开项目

1. 选择 `File -> Open...` 并导航到 `contest_training_system` 目录。
2. IDEA会自动识别为Maven项目并下载所有依赖。这个过程可能需要一些时间。

#### 6. 运行应用

1. 在项目结构中找到 `ContestTrainingSystemApplication.java` 文件。
2. 右键点击该文件，选择 `Run 'ContestTrainingSystemApplication'`。
3. 如果一切顺利，你将在控制台看到Spring Boot的启动日志，最后一行会提示应用已在某个端口（默认为8080）上启动。

---

## 5. 核心模块与API参考

本章节将深入探讨系统各个核心模块的功能，并提供详尽的API接口文档。

### 5.1. 统一响应与分页模型

#### 5.1.1. 统一响应对象 `Result<T>`

为了保证前端处理的便利性和一致性，所有API接口都返回一个统一的 `Result<T>`对象。

- **`Result.java` 结构**:

```java
@Data
public class Result<T> {
    private Integer code; // 编码：1为成功，其它数字为失败
    private String msg;   // 错误信息
    private T data;       // 数据
}
```

- **成功响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1,
    "nickname": "JohnDoe"
  }
}
```

- **失败响应示例**:

```json
{
  "code": 0,
  "msg": "邮箱格式不正确",
  "data": null
}
```

#### 5.1.2. 分页结果对象 `PageResultVO<T>`

所有需要分页的列表查询接口，其 `data`字段将是一个 `PageResultVO<T>`对象。

- **`PageResultVO.java` 结构**:

```java
@Data
public class PageResultVO<T> {
    private Long total;       // 总记录数
    private Integer page;     // 当前页码
    private Integer pageSize; // 每页数量
    private List<T> list;     // 当前页数据列表
}
```

- **分页查询响应示例**:

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 127,
    "page": 1,
    "pageSize": 10,
    "list": [
      { "problemId": 1001, "title": "A+B Problem" },
      { "problemId": 1002, "title": "FizzBuzz" }
      // ... 8 more items
    ]
  }
}
```

### 5.2. 用户模块 (User)

用户模块是系统的基础，负责处理用户身份相关的所有操作。

#### 5.2.1. API Endpoints

---

#### `POST /register`

- **功能**: 注册一个新用户。
- **权限**: 无需认证。
- **请求体(Request Body)**: `application/json`
  - **类型**: `UserRegisterDTO`
  - **字段详情**:
    - `email` (String, **必需**): 用户的邮箱，将作为主要登录凭证之一。必须符合邮箱格式，且在系统中唯一。
    - `phoneNumber` (String, **必需**): 用户的手机号，也可作为登录凭证。必须是11位数字，且在系统中唯一。
    - `nickname` (String, **必需**): 用户的昵称，长度1-20字符之间。
    - `studentId` (String, **必需**): 用户的学号，长度1-32字符之间，在系统中唯一。
    - `password` (String, **必需**): 用户的登录密码，长度6-32位，且必须同时包含字母和数字。
  - **请求示例**:
    ```json
    {
      "email": "testuser@example.com",
      "phoneNumber": "13800138000",
      "nickname": "Newbie Coder",
      "studentId": "20250001",
      "password": "password123"
    }
    ```
- **响应 (Response)**:
  - **成功 (200 OK)**:
    - **类型**: `Result<null>`
    - **响应示例**:
      ```json
      {
        "code": 1,
        "msg": "success",
        "data": null
      }
      ```
  - **失败 (400 Bad Request)**:
    - 输入验证失败（如邮箱格式错误、密码太短）。
    - 邮箱、手机号或学号已被注册。
    - **响应示例**:
      ```json
      {
        "code": 0,
        "msg": "该邮箱已被注册",
        "data": null
      }
      ```

---

#### `POST /login`

- **功能**: 用户登录，成功后返回JWT。
- **权限**: 无需认证。
- **请求体(Request Body)**: `application/json`
  - **类型**: `UserLoginDTO`
  - **字段详情**:
    - `emailOrPhone` (String, **必需**): 用户的邮箱或手机号。
    - `password` (String, **必需**): 用户的密码。
  - **请求示例**:
    ```json
    {
      "emailOrPhone": "testuser@example.com",
      "password": "password123"
    }
    ```
- **响应 (Response)**:
  - **成功 (200 OK)**:
    - **类型**: `Result<LoginSuccessVO>`
    - `data` 字段包含一个 `LoginSuccessVO`对象：
      - `token` (String): 生成的JWT。
    - **响应示例**:
      ```json
      {
        "code": 1,
        "msg": "success",
        "data": {
          "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTY3MjUzNjAwMH0.abc..."
        }
      }
      ```
  - **失败 (400 Bad Request / 401 Unauthorized)**:
    - 用户名或密码错误。
    - 账户被禁用。
    - **响应示例**:
      ```json
      {
        "code": 0,
        "msg": "用户名或密码错误",
        "data": null
      }
      ```

---

#### `GET /user/profile`

- **功能**: 获取当前登录用户的个人信息。
- **权限**: 需要认证（任何已登录用户）。
- **请求头(Request Headers)**:
  - `Authorization`: `Bearer <jwt_token>`
- **响应 (Response)**:
  - **成功 (200 OK)**:
    - **类型**: `Result<UserProfileVO>`
    - `data` 字段包含一个 `UserProfileVO`对象：
      - `userId` (Long): 用户ID。
      - `email` (String): 邮箱。
      - `phoneNumber` (String): 手机号。
      - `nickname` (String): 昵称。
      - `studentId` (String): 学号。
      - `role` (String): 角色 (e.g., "STUDENT", "ADMIN")。
      - `createdAt` (LocalDateTime): 创建时间。
      - `isActive` (Integer): 账户状态（1=激活，0=禁用）。
    - **响应示例**:
      ```json
      {
        "code": 1,
        "msg": "success",
        "data": {
          "userId": 123,
          "email": "testuser@example.com",
          "phoneNumber": "13800138000",
          "nickname": "Newbie Coder",
          "studentId": "20250001",
          "role": "STUDENT",
          "createdAt": "2025-01-01T10:00:00",
          "isActive": 1
        }
      }
      ```

---

#### `PUT /user/update`

- **功能**: 更新当前登录用户的个人信息。
- **权限**: 需要认证（任何已登录用户）。
- **请求头(Request Headers)**:
  - `Authorization`: `Bearer <jwt_token>`
- **请求体(Request Body)**: `application/json`
  - **类型**: `UserUpdateDTO`
  - **字段详情** (所有字段均为可选):
    - `nickname` (String): 新的昵称。
    - `phoneNumber` (String): 新的手机号（需要验证唯一性）。
    - `email` (String): 新的邮箱 (需要验证唯一性)。
    - `studentId` (String): 新的学号 (需要验证唯一性)。
    - `password` (String): 新的密码 (需要符合强度要求)。
  - **请求示例**:
    ```json
    {
      "nickname": "Advanced Coder",
      "password": "newStrongPassword456"
    }
    ```
- **响应 (Response)**:
  - **成功 (200 OK)**: `Result<null>`
  - **失败 (400 Bad Request)**:
    - 提交的手机号/邮箱/学号已被他人使用。
    - 密码格式不正确。

---

#### `GET /admin/users`

- **功能**: [管理员] 分页查询用户列表。
- **权限**: 需要认证，且角色必须是 `ADMIN`。
- **请求头(Request Headers)**:
  - `Authorization`: `Bearer <jwt_token>`
- **查询参数 (Query Parameters)**:
  - **类型**: `UserQueryDTO`
  - `page` (Integer, 默认1): 页码。
  - `pageSize` (Integer, 默认10): 每页数量。
  - `keyword` (String, 可选): 关键字，用于模糊搜索昵称、邮箱、手机号等。
  - `role` (String, 可选): 按角色筛选（e.g., "STUDENT", "TEACHER"）。
  - `isActive` (Integer, 可选): 按账户状态筛选（1=激活，0=禁用）。
  - `sortBy` (String, 可选): 排序方式 (e.g., "CREATED_AT_DESC")。
- **响应 (Response)**:
  - **成功 (200 OK)**:
    - **类型**: `Result<PageResultVO<UserProfileVO>>`
    - `data` 字段包含一个用户列表的分页结果。

---

### 5.3. 题目模块 (Problem)

题目模块负责管理题库中的所有编程题目。

#### 5.3.1. API Endpoints

---

#### `POST /problem/create`

- **功能**: [管理员/教师] 创建一个新题目。
- **权限**: 需要认证，且角色为 `ADMIN` 或 `TEACHER` (`@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")`)。
- **请求头(Request Headers)**:
  - `Authorization`: `Bearer <jwt_token>`
- **请求体(Request Body)**: `application/json`
  - **类型**: `ProblemCreateDTO`
  - **字段详情**:
    - `title` (String, **必需**): 题目名称。
    - `description` (String, **必需**): 题目描述 (支持Markdown)。
    - `inputSpec` (String, 可选): 输入格式说明。
    - `outputSpec` (String, 可选): 输出格式说明。
    - `sampleInput` (String, **必需**): 示例输入。
    - `sampleOutput` (String, **必需**): 示例输出。
    - `remark` (String, 可选): 提示或备注。
    - `timeLimit` (Integer, **必需**): 时间限制 (ms)。
    - `memoryLimit` (Integer, **必需**): 内存限制 (MB)。
    - `testdataZip` (String, **必需**): 测试数据压缩包的URL。
  - **请求示例**:
    ```json
    {
      "title": "Two Sum",
      "description": "Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.",
      "inputSpec": "The first line contains an integer `n`, the size of the array. ...",
      "outputSpec": "Output two integers, the indices of the two numbers.",
      "sampleInput": "[2, 7, 11, 15]\n9",
      "sampleOutput": "0 1",
      "remark": "You may assume that each input would have exactly one solution.",
      "timeLimit": 1000,
      "memoryLimit": 256,
      "testdataZip": "https://my-data-store.com/datasets/two-sum.zip"
    }
    ```
- **响应 (Response)**:
  - **成功 (200 OK)**:
    - **类型**: `Result<Long>`
    - `data` 字段是新创建题目的ID。
    - **响应示例**:
      ```json
      {
        "code": 1,
        "msg": "success",
        "data": 1024
      }
      ```
  - **失败 (400 Bad Request)**: 缺少必需字段。

---

#### `PUT /problem/update`

- **功能**: [管理员/教师] 更新一个已存在的题目。
- **权限**: `ADMIN` 或 `TEACHER`。
- **请求头**: `Authorization: Bearer <jwt_token>`
- **请求体(Request Body)**: `application/json`
  - **类型**: `ProblemUpdateDTO`
  - **字段详情** (所有字段可选，但 `problemId`必需):
    - `problemId` (Long, **必需**): 要更新的题目ID。
    - `title`, `description`, `timeLimit`, etc. (同 `ProblemCreateDTO`)。
- **响应 (Response)**:
  - **成功 (200 OK)**: `Result<null>`
  - **失败 (400 Bad Request / 404 Not Found)**: 题目ID不存在或输入无效。

---

#### `GET /problem/list`

- **功能**: 获取题目列表，支持分页和筛选。
- **权限**: 需要认证（任何已登录用户）。
- **请求头**: `Authorization: Bearer <jwt_token>`
- **查询参数 (Query Parameters)**:
  - **类型**: `ProblemQueryDTO`
  - `page` (Integer, 默认1)
  - `pageSize` (Integer, 默认10)
  - `keyword` (String, 可选): 按题目名称或ID搜索。
  - `creatorId` (Long, 可选): 按创建者ID筛选。
  - `isHidden` (Integer, 可选): 筛选隐藏/可见的题目（0=可见, 1=隐藏）。
  - `sortBy` (String, 可选): 排序字段 (e.g., "CREATED_AT_DESC")。
- **响应 (Response)**:
  - **成功 (200 OK)**:
    - **类型**: `Result<PageResultVO<ProblemListVO>>` (对管理员/教师) 或 `Result<PageResultVO<ProblemListStudentVO>>` (对学生)。
    - `ProblemListVO` 包含 `problemId`, `title`, `creatorId`, `isHidden` 等管理字段。
    - `ProblemListStudentVO` 只包含 `title`, `timeLimit` 等学生可见字段。

---

#### `GET /problem/{problemId}`

- **功能**: 获取单个题目的详细信息。
- **权限**: 需要认证（任何已登录用户）。
- **请求头**: `Authorization: Bearer <jwt_token>`
- **路径参数 (Path Parameters)**:
  - `problemId` (Long): 题目的ID。
- **响应 (Response)**:
  - **成功 (200 OK)**:
    - **类型**: `Result<ProblemDetailVO>`
    - `data` 字段是一个 `ProblemDetailVO`对象，包含题目的所有详细信息。
  - **失败 (404 Not Found)**: 题目不存在或已被隐藏且当前用户无权查看。

---

#### `DELETE /problem/{problemId}`

- **功能**: [管理员] 删除或隐藏一个题目。
- **逻辑**: 该接口实现的*逻辑删除*，即将题目的 `is_hidden`字段设为1，而不是物理删除，以保护历史提交记录和竞赛数据的完整性。
- **权限**: `ADMIN`。
- **请求头**: `Authorization: Bearer <jwt_token>`
- **路径参数**:
  - `problemId` (Long): 要隐藏的题目ID。
- **响应 (Response)**:
  - **成功 (200 OK)**: `Result<null>`
  - **失败 (404 Not Found)**: 题目ID不存在。

---

### 5.4. 竞赛模块 (Contest)

竞赛模块是平台的核心功能之一，它允许管理员和教师创建和管理竞赛，并让学生参与其中。

#### 5.4.1. API Endpoints

---

#### `POST /contest/create`

- **功能**: [管理员/教师] 创建一个新竞赛。
- **权限**: ADMIN 或 TEACHER。
- **请求体(Request Body)**: `ContestCreateDTO`
  - `title` (String, **必需**): 竞赛标题。
  - `description` (String, 可选): 竞赛描述。
  - `startTime` (LocalDateTime, **必需**): 竞赛开始时间。
  - `endTime` (LocalDateTime, **必需**): 竞赛结束时间。
  - `password` (String, 可选): 竞赛密码（仅对私有竞赛有效）。
  - `visibility` (String, **必需**): 可见性 ("PUBLIC" or "PRIVATE")。
  - `problemIds` (List`<Long>`, **必需**): 竞赛题目ID列表。
- **响应**: `Result<Long>` (新竞赛ID)。

---

#### `PUT /contest/update`

- **功能**: [管理员/教师] 更新一个已存在的竞赛。
- **权限**: ADMIN 或 TEACHER。
- **请求体(Request Body)**: `ContestUpdateDTO`
  - `contestId` (Long, **必需**): 要更新的竞赛ID。
  - 其他字段同 `ContestCreateDTO` (均为可选)。
- **响应**: `Result<null>`。

---

#### `GET /contest/list`

- **功能**: 获取竞赛列表，支持分页和筛选。
- **权限**: 需要认证。
- **查询参数(Query Parameters)**: `ContestQueryDTO`
  - `page`, `pageSize`
  - `status` (String, 可选): "SCHEDULED", "ONGOING", "ENDED"。
  - `visibility` (String, 可选): "PUBLIC", "PRIVATE"。
  - `keyword` (String, 可选): 竞赛标题搜索。
- **响应**: `Result<PageResultVO<ContestListVO>>`。

---

#### `GET /contest/{contestId}`

- **功能**: 获取单个竞赛的详细信息。
- **权限**: 需要认证。
- **路径参数**: `contestId` (Long)。
- **响应**: `Result<ContestDetailVO>`。

---

#### `POST /contest/join`

- **功能**: 用户尝试加入一个竞赛。
- **权限**: 需要认证。
- **请求体(Request Body)**: `ContestJoinDTO`
  - `contestId` (Long, **必需**)。
  - `password` (String, 可选): 竞赛密码（如果需要）。
- **响应**: `Result<null>`。

---

#### `DELETE /contest/{contestId}`

- **功能**: [管理员] 删除一个竞赛。
- **权限**: ADMIN。
- **路径参数**: `contestId` (Long)。
- **响应**: `Result<null>`。

### 5.5. 训练计划模块 (Training Plan)

训练计划模块允许教师和管理员为学生创建结构化的学习计划，通常包含一系列竞赛或题目。

#### 5.5.1. API Endpoints

---

#### `POST /training_plan/create`

- **功能**: [管理员/教师] 创建一个新训练计划。
- **权限**: ADMIN 或 TEACHER。
- **请求体(Request Body)**: `TrainingPlanCreateDTO`
  - `title` (String, **必需**): 计划标题。
  - `description` (String, 可选): 计划描述。
  - `startTime` (LocalDateTime, **必需**): 计划开始时间。
  - `endTime` (LocalDateTime, **必需**): 计划结束时间。
  - `contestIds` (List`<Long>`, **必需**): 关联的竞赛ID列表。
  - `studentIds` (List`<Long>`, **必需**): 关联的学生ID列表。
- **响应**: `Result<Long>` (新计划ID)。

---

#### `PUT /training_plan/update`

- **功能**: [管理员/教师] 更新一个已存在的训练计划。
- **权限**: ADMIN 或 TEACHER。
- **请求体(Request Body)**: `TrainingPlanUpdateDTO`
  - `planId` (Long, **必需**): 要更新的计划ID。
  - 其他字段同 `TrainingPlanCreateDTO` (均为可选)。
- **响应**: `Result<null>`。

---

#### `GET /training_plan/list`

- **功能**: 获取训练计划列表，支持分页和筛选。
- **权限**: 需要认证。
- **查询参数(Query Parameters)**: `TrainingPlanQueryDTO`
  - `page`, `pageSize`
  - `status` (String, 可选): 计划状态。
  - `creatorId` (Long, 可选): 创建者ID。
  - `keyword` (String, 可选): 计划标题搜索。
- **响应**: `Result<PageResultVO<TrainingPlanListVO>>`。

---

#### `GET /training_plan/{planId}`

- **功能**: 获取单个训练计划的详细信息。
- **权限**: 需要认证。
- **路径参数**: `planId` (Long)。
- **响应**: `Result<TrainingPlanDetailVO>`。

---

#### `DELETE /training_plan/{planId}`

- **功能**: [管理员] 删除一个训练计划。
- **权限**: ADMIN。
- **路径参数**: `planId` (Long)。
- **响应**: `Result<null>`。

---

## 6. 数据库表结构

数据库是整个系统的基石，以下是数据库的主要表结构以及它们之间的关系。

### 6.1. `user` - 用户表

存储系统用户的基本信息。

| 列名 (Column)     | 数据类型 (Type) | 键 (Key)      | 允许NULL | 描述 (Description)                   |
| ----------------- | --------------- | ------------- | -------- | ------------------------------------ |
| `user_id`       | BIGINT          | **PK**  | NOT NULL | 用户唯一标识符，主键                 |
| `email`         | VARCHAR(100)    | **UNI** | NOT NULL | 用户的邮箱地址，唯一，用于登录和通知 |
| `phone_number`  | VARCHAR(20)     | **UNI** | NOT NULL | 用户的手机号码，唯一，用于登录和验证 |
| `nickname`      | VARCHAR(50)     |               | NOT NULL | 用户的昵称，用于公开显示             |
| `student_id`    | VARCHAR(50)     | **UNI** | NOT NULL | 用户的学号，唯一                     |
| `password_hash` | VARCHAR(255)    |               | NOT NULL | 存储加密后的密码，使用BCrypt算法     |
| `role`          | VARCHAR(20)     |               | NOT NULL | 用户角色 (STUDENT, TEACHER, ADMIN)   |
| `is_active`     | TINYINT(1)      |               | NOT NULL | 账户状态 (1: 激活, 0: 禁用/逻辑删除) |
| `created_at`    | DATETIME        |               | NOT NULL | 记录创建时间。                       |
| `updated_at`    | DATETIME        |               | NOT NULL | 记录最后更新时间。                   |

### 6.2. `problem` - 题目表

存储在线编程题库中的所有题目信息。

| 列名              | 数据类型     | 键           | 允许NULL | 描述                                         |
| ----------------- | ------------ | ------------ | -------- | -------------------------------------------- |
| `problem_id`    | BIGINT       | **PK** | NOT NULL | 题目唯一标识符，主键                         |
| `title`         | VARCHAR(255) |              | NOT NULL | 题目名称。                                   |
| `description`   | TEXT         |              | NULL     | 详细题目描述 (支持Markdown)。                |
| `input_spec`    | TEXT         |              | NULL     | 输入格式说明。                               |
| `output_spec`   | TEXT         |              | NULL     | 输出格式说明。                               |
| `sample_input`  | TEXT         |              | NULL     | 示例输入。                                   |
| `sample_output` | TEXT         |              | NULL     | 示例输出。                                   |
| `remark`        | TEXT         |              | NULL     | 题目提示或备注。                             |
| `time_limit`    | INT          |              | NOT NULL | 时间限制 (单位:毫秒)。                       |
| `memory_limit`  | INT          |              | NOT NULL | 内存限制 (单位:MB)。                         |
| `creator_id`    | BIGINT       | **FK** | NOT NULL | 创建该题目的用户ID (关联 `user.user_id`)。 |
| `is_hidden`     | TINYINT(1)   |              | NOT NULL | 是否隐藏 (1: 隐藏, 0: 可见)。                |
| `created_at`    | DATETIME     |              | NOT NULL | 记录创建时间。                               |
| `updated_at`    | DATETIME     |              | NOT NULL | 记录最后更新时间。                           |

### 6.3. `contest` - 竞赛表

存储所有竞赛的基本信息。

| 列名              | 数据类型     | 键           | 允许NULL | 描述                                         |
| ----------------- | ------------ | ------------ | -------- | -------------------------------------------- |
| `contest_id`    | BIGINT       | **PK** | NOT NULL | 竞赛唯一标识符，主键                         |
| `title`         | VARCHAR(255) |              | NOT NULL | 竞赛标题。                                   |
| `description`   | TEXT         |              | NULL     | 竞赛详细描述。                               |
| `start_time`    | DATETIME     |              | NOT NULL | 竞赛开始时间。                               |
| `end_time`      | DATETIME     |              | NOT NULL | 竞赛结束时间。                               |
| `visibility`    | VARCHAR(20)  |              | NOT NULL | 可见性 (PUBLIC, PRIVATE)。                   |
| `state`         | VARCHAR(20)  |              | NOT NULL | 状态 (HIDDEN, USING)。                       |
| `password_hash` | VARCHAR(255) |              | NULL     | 私有竞赛的密码哈希值。                       |
| `creator_id`    | BIGINT       | **FK** | NOT NULL | 创建该竞赛的用户ID (关联 `user.user_id`)。 |
| `created_at`    | DATETIME     |              | NOT NULL | 记录创建时间。                               |
| `updated_at`    | DATETIME     |              | NOT NULL | 记录最后更新时间。                           |

### 6.4. `training_plan` - 训练计划表

存储所有训练计划的基本信息。

| 列名            | 数据类型     | 键           | 允许NULL | 描述                                         |
| --------------- | ------------ | ------------ | -------- | -------------------------------------------- |
| `plan_id`     | BIGINT       | **PK** | NOT NULL | 训练计划唯一标识符，主键                     |
| `title`       | VARCHAR(255) |              | NOT NULL | 计划标题。                                   |
| `description` | TEXT         |              | NULL     | 训练计划详细描述。                           |
| `status`      | VARCHAR(20)  |              | NOT NULL | 计划状态 (NOT_STARTED, ONGOING, COMPLETED)。 |
| `creator_id`  | BIGINT       | **FK** | NOT NULL | 创建该计划的用户ID (关联 `user.user_id`)。 |
| `start_time`  | DATETIME     |              | NOT NULL | 计划开始时间。                               |
| `end_time`    | DATETIME     |              | NOT NULL | 计划结束时间。                               |
| `created_at`  | DATETIME     |              | NOT NULL | 记录创建时间。                               |
| `updated_at`  | DATETIME     |              | NOT NULL | 记录最后更新时间。                           |

### 6.5. `submission` - 提交记录表

存储用户每一次提交的代码及其评测结果。

| 列名              | 数据类型     | 键           | 允许NULL | 描述                                                         |
| ----------------- | ------------ | ------------ | -------- | ------------------------------------------------------------ |
| `submission_id` | BIGINT       | **PK** | NOT NULL | 提交唯一标识符，主键                                         |
| `contest_id`    | BIGINT       | **FK** | NULL     | 竞赛ID (关联 `contest.contest_id`)，若为普通提交则为NULL。 |
| `problem_id`    | BIGINT       | **FK** | NOT NULL | 题目ID (关联 `problem.problem_id`)。                       |
| `user_id`       | BIGINT       | **FK** | NOT NULL | 提交用户ID (关联 `user.user_id`)。                         |
| `code`          | TEXT         |              | NOT NULL | 提交的源代码。                                               |
| `code_length`   | INT          |              | NULL     | 源代码长度。                                                 |
| `language`      | VARCHAR(20)  |              | NOT NULL | 编程语言 (e.g., C++, Java, Python)。                         |
| `compiler`      | VARCHAR(50)  |              | NULL     | 使用的编译器版本。                                           |
| `result`        | VARCHAR(20)  |              | NOT NULL | 评测结果 (e.g., PENDING, AC, WA, TLE)。                      |
| `time_used`     | INT          |              | NULL     | 运行时间（毫秒）。                                           |
| `memory_used`   | INT          |              | NULL     | 运行内存（KB）。                                             |
| `created_at`    | DATETIME     |              | NOT NULL | 提交时间。                                                   |
| `judge_log_url` | VARCHAR(255) |              | NULL     | 详细评测日志的存储地址。                                     |

### 6.6. 关联表 (Junction Tables)

#### 6.6.1. `contest_participant` - 竞赛参与者

记录用户与竞赛的参与关系。

| 列名              | 数据类型    | 键               | 允许NULL | 描述                                   |
| ----------------- | ----------- | ---------------- | -------- | -------------------------------------- |
| `contest_id`    | BIGINT      | **PK, FK** | NOT NULL | 竞赛ID (关联 `contest.contest_id`)。 |
| `user_id`       | BIGINT      | **PK, FK** | NOT NULL | 用户ID (关联 `user.user_id`)。       |
| `registered_at` | DATETIME    |                  | NOT NULL | 注册时间。                             |
| `is_official`   | TINYINT(1)  |                  | NOT NULL | 是否为官方参赛者 (1: 是, 0: 否/旁观)。 |
| `team_name`     | VARCHAR(50) |                  | NULL     | 队伍名称。                             |

#### 6.6.2. `contest_problem` - 竞赛题目

记录竞赛与题目的关联关系。

| 列名              | 数据类型 | 键               | 允许NULL | 描述                                   |
| ----------------- | -------- | ---------------- | -------- | -------------------------------------- |
| `contest_id`    | BIGINT   | **PK, FK** | NOT NULL | 竞赛ID (关联 `contest.contest_id`)。 |
| `problem_id`    | BIGINT   | **PK, FK** | NOT NULL | 题目ID (关联 `problem.problem_id`)。 |
| `display_order` | INT      |                  | NOT NULL | 题目在竞赛中的显示顺序。               |

#### 6.6.3. `training_plan_contest` - 训练计划关联竞赛

记录训练计划与竞赛的关联关系。

| 列名            | 数据类型     | 键               | 允许NULL | 描述                                          |
| --------------- | ------------ | ---------------- | -------- | --------------------------------------------- |
| `plan_id`     | BIGINT       | **PK, FK** | NOT NULL | 训练计划ID (关联 `training_plan.plan_id`)。 |
| `contest_id`  | BIGINT       | **PK, FK** | NOT NULL | 竞赛ID (关联 `contest.contest_id`)。        |
| `description` | VARCHAR(255) |                  | NULL     | 对该竞赛的补充说明。                          |
| `sequence`    | INT          |                  | NOT NULL | 在计划中的顺序。                              |
| `added_time`  | DATETIME     |                  | NOT NULL | 添加到计划的时间。                            |

#### 6.6.4. `training_plan_student` - 训练计划关联学生

记录训练计划与学生的关联关系。

| 列名            | 数据类型 | 键               | 允许NULL | 描述                                          |
| --------------- | -------- | ---------------- | -------- | --------------------------------------------- |
| `plan_id`     | BIGINT   | **PK, FK** | NOT NULL | 训练计划ID (关联 `training_plan.plan_id`)。 |
| `user_id`     | BIGINT   | **PK, FK** | NOT NULL | 学生用户ID (关联 `user.user_id`)。          |
| `enrolled_at` | DATETIME |                  | NOT NULL | 注册计划的时间。                              |

---

## 7. 安全策略与认证授权 (Security Deep Dive)

本系统对安全性的考虑非常充分，主要依赖 **Spring Security** 框架，并结合 **JWT (JSON Web Tokens)** 实现无状态的认证。响应当前的认证流程和授权机制：

### 7.1. 认证流程

1. **用户登录**: 用户通过 `/login` 接口提交用户名（邮箱或手机号）和密码。
2. **凭证验证**: UserService 验证用户提供的凭证是否正确。
3. **签发Token**: 验证成功后，JwtUtil 生成一个包含用户信息的JWT Token。Token 的 Payload 中包含用户唯一标识 `userId` 和用户角色 `role`。
4. **返回Token**: 服务器将签发的JWT Token 返回给客户端。
5. **客户端存储**: 客户端（如浏览器）将 Token 存储在本地（通常使用 localStorage 或 sessionStorage），并在后续的 API 请求中通过 `Authorization` 请求头携带，格式为 `Bearer <token>`。
6. **请求拦截**: 每次需要认证的 API 请求，都会经过一个自定义的过滤器 `JwtAuthenticationFilter`。
7. **Token校验**: 该过滤器会从请求头中提取 Token，并使用 `JwtUtil` 进行校验（包括签名是否有效、是否过期等）。
8. **安全上下文设置**: 如果 Token 有效，过滤器会解析出 `userId` 和 `role`，并创建一个 `UsernamePasswordAuthenticationToken` 对象，将其设置到 Spring Security 的 `SecurityContextHolder` 中。这表示当前请求已被认证。
9. **权限控制**: 在 Controller 层，可以使用 `@PreAuthorize` 注解来定义方法级别的访问权限。Spring Security 会根据当前 `SecurityContext` 中的认证信息（包括用户角色）来判断用户是否有权执行该方法。
10. **获取用户信息**: 认证成功后，可以在 Service 或 Controller 层通过 `UserContext` 工具类（一个 ThreadLocal 实现）方便地获取当前登录用户的 ID，而无需每次都从 Token 中解析。

### 7.2. 核心组件

#### 7.2.1. `SecurityConfig.java` - 安全配置

这是 Spring Security 的核心配置类，定义了应用的整体安全策略。

- **`@EnableWebSecurity` & `@EnableMethodSecurity`**:

  - `@EnableWebSecurity`: 启用 Spring Security 的 Web 安全功能。
  - `@EnableMethodSecurity`: 启用方法级别的安全注解，如 `@PreAuthorize`，用于实现基于角色的访问控制 (RBAC)。
- **`passwordEncoder()` Bean**:

  - 配置 `BCryptPasswordEncoder` 作为密码编码器。这是因为数据库中存储的 `password_hash` 是经过 BCrypt 加密的，我们需要使用相同的算法来验证用户输入的密码。
- **`securityFilterChain()` Bean**:

  - **禁用 CSRF**: `csrf.disable()`。由于我们使用 JWT 进行认证，是无状态的，不需要依赖 Session，因此禁用了 CSRF 保护。
  - **无状态会话管理**: `sessionManagement(SessionCreationPolicy.STATELESS)`。强制 Spring Security 不创建或使用 HTTP Session，这与 JWT 的无状态特性相匹配。
  - **授权配置**: `authorizeHttpRequests()` 用于配置哪些 URL 需要认证，以及它们的访问权限。
    - `/register` 和 `/login` 接口允许任何人访问 (`permitAll()`)。
    - 其他所有请求都需要认证 (`authenticated()`)。
  - **添加 JWT 过滤器**: `.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`。将我们自定义的 `JwtAuthenticationFilter` 插入到 Spring Security 的过滤器链中，确保在用户认证（`UsernamePasswordAuthenticationFilter`）之前进行 JWT 的校验。

#### 7.2.2. `JwtUtil.java` - Token 生成与解析

封装了 JWT 的生成、解析和校验逻辑。

- **`generateToken(Long userId, String role)`**:

  - **参数**: 用户ID 和用户角色。
  - **逻辑**:
    1. 设置 `subject` 为用户 ID（字符串形式），表示 Token 的主体。
    2. 在 `claims` 中添加 `userId` 和 `role` 信息。
    3. 设置 `issuedAt`（签发时间）和 `expiration`（过期时间），当前设置为 7 天。
    4. 使用预定义的 `SECRET` 和 HMAC-SHA 算法进行签名。
  - **返回值**: 一个 URL 安全的 JWT 字符串。
- **`parseClaims(String token)`**:

  - **参数**: JWT 字符串。
  - **逻辑**: 使用相同的 `SECRET` 验证 Token 的签名。如果签名无效或 Token 已过期，则会抛出异常。
  - **返回值**: 包含 Token 信息的 `Claims` 对象。
- **`getUserId(String token)`** & **`getRole(String token)`**:

  - 根据解析出的 `Claims` 对象，提取 `userId` 和 `role` 信息。

#### 7.2.3. `JwtAuthenticationFilter.java` - 认证过滤器

这是一个自定义的 Servlet 过滤器，用于在 Spring Security 的认证流程中处理 JWT。

- **继承 `OncePerRequestFilter`**: 确保在一次 HTTP 请求中该过滤器只执行一次。
- **`doFilterInternal()` 方法实现**:

  1. 从 `Authorization` 请求头中提取 `Bearer Token`。
  2. 如果请求头不存在或格式不正确，则直接放行，让后续过滤器处理。
  3. 使用 `JwtUtil` 解析和校验 Token。如果校验失败（如 Token 无效、过期），则直接放行，Spring Security 的后续认证机制会处理（通常会返回 401 Unauthorized）。
  4. **核心逻辑**: 如果 Token 有效且当前 `SecurityContext` 中尚未认证信息，则执行以下操作：
     a.  使用 `JwtUtil` 从 Token 中提取 `userId` 和 `role`。
     b.  将 `userId` 和 `role` 信息存储到 `ThreadLocal` 的 `UserContext` 中，方便后续业务代码（Service, Controller）通过 `UserContext.getUserId()` 等方法获取当前用户信息。
     c.  创建一个 `UsernamePasswordAuthenticationToken`。这是一个 Spring Security 的认证令牌，用于表示已认证的用户。我们将 `userId` 作为 principal，并将 `role` 转换为 `SimpleGrantedAuthority` 列表作为权限。
     d.  `SecurityContextHolder.getContext().setAuthentication(authToken)`: 将这个已认证的令牌设置到当前请求的安全上下文中，表示该用户已通过认证。
- **`UserContext.clear()`**: 在 `finally` 块中调用，确保每次请求结束后，`ThreadLocal` 中的用户信息被清除，防止内存泄漏和信息串用。

### 7.3. 权限控制 (Authorization)

项目的权限控制主要基于**角色**和**资源**的**访问控制列表 (ACL)**，具体实现为**基于角色的访问控制 (RBAC)**。

- **实现方式**: 通过 Controller 方法上的 `@PreAuthorize` 注解。
- **示例**:
  ```java
  @PostMapping("/create")
  @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
  public Result<Long> createContest(...) { ... }
  ```
- **工作原理**: 当一个请求到达上述 `createContest` 方法时，`@EnableMethodSecurity` 启用的 AOP 会拦截该方法调用。它会检查当前 `SecurityContext` 中的认证信息（用户的角色列表）。如果用户的角色列表中包含 'ADMIN' 或 'TEACHER'，则允许方法执行；否则，将抛出 `AccessDeniedException`，最终导致 Spring Security 返回一个 403 Forbidden 的 HTTP 响应。

---

## 9. 入门指南 (Getting Started)

本节将指导您如何在本机上搭建和运行本项目。按照以下步骤操作，您将能够成功启动后端服务。

### 9.1. 前置要求

- **JDK**: 17 或更高版本。
- **Maven**: 3.6 或更高版本。
- **IDE**: 推荐 IntelliJ IDEA (Community 或 Ultimate 版本均可)。
- **数据库**: MySQL 8.0 或更高版本。
- **Git**: 用于代码版本控制。

### 9.2. 配置步骤

#### 1. 克隆代码仓库

```bash
git clone <your-repository-url>
cd ContestSystem/backend/contest_training_system
```

#### 2. 创建数据库

1. 在您的 MySQL 服务器上创建一个新的数据库。
   ```sql
   CREATE DATABASE contest_system_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. 执行项目根目录下的 `init.sql` 文件，它包含了创建数据库表结构和插入初始数据的 SQL 语句。

#### 3. 配置 `application.yml`

修改 `src/main/resources/application.yml` 文件，更新 `spring.datasource` 部分的配置，使其与您的数据库连接信息匹配。

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/contest_system_db?useSSL=false&serverTimezone=UTC
    username: your_mysql_username
    password: your_mysql_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 4. 配置 `JwtUtil.java`

**重要**: 在 `src/main/java/com/system/util/JwtUtil.java` 文件中，修改 `SECRET` 常量的值。请将其设置为一个足够长且随机的字符串，作为签名 JWT 的密钥，以保证安全性和防止 Token 被伪造。

```java
// 请替换为您的安全密钥
private static final String SECRET = "a-very-long-and-random-secret-key-that-is-at-least-256-bits";
```

#### 5. 在 IntelliJ IDEA 中导入项目

1. 选择 `File -> Open...`，然后导航到并选择 `contest_training_system` 目录。
2. IDEA 会自动识别为 Maven 项目，并开始下载依赖。这个过程可能需要一些时间。

#### 6. 运行应用

1. 找到主类 `ContestTrainingSystemApplication.java`。
2. 右键点击该文件，选择 `Run 'ContestTrainingSystemApplication'`。
3. 程序启动后，您将在控制台看到 Spring Boot 的启动日志，表明应用已成功启动。默认端口为 8080。

---

## 10. 部署指南 (Deployment Guide)

### 10.1. 打包应用

项目在 `backend/contest_training_system` 目录下，可以使用 Maven 命令打包成一个可执行的 JAR 文件。

```bash
mvn clean package -DskipTests
```

执行完毕后，打包好的 JAR 文件会位于 `target/` 目录下，文件名为 `contest_training_system-x.x.x.jar` (x.x.x 为版本号)。

### 10.2. 部署到服务器

1. **上传 JAR 包**: 将打包好的可执行 JAR 文件上传到您的目标服务器。
2. **运行应用**:
   - **前台运行 (开发/测试)**:
     ```bash
     java -jar contest_training_system-x.x.x.jar
     ```
   - **后台运行 (生产环境，推荐)**: 使用 `nohup` 命令让应用在后台持续运行，并将日志输出到文件。
     ```bash

     ```

nohup java -jar contest_training_system-x.x.x.jar --spring.profiles.active=prod > app.log 2>&1 &
      ```
      这会将应用的日志输出到 `app.log` 文件，并且即使您关闭了终端，应用也会继续运行。

### 10.3. 使用 Nginx 进行反向代理

在生产环境中，强烈建议使用 Nginx 作为反向代理服务器。

- **优势**:

  - **负载均衡**: 可以将请求分发到多个后端应用实例。
  - **SSL/TLS加密**: 在 Nginx 层配置 HTTPS，为后端应用提供安全加密。
  - **静态资源服务**: 让 Nginx 直接提供静态资源，提高效率。
- **示例 Nginx 配置 (`/etc/nginx/conf.d/contest-system.conf`)**:

  ```nginx
  server {
      listen 80;
      server_name api.yourdomain.com;

      # For HTTPS (recommended)
      # listen 443 ssl;
      # server_name api.yourdomain.com;
      # ssl_certificate /path/to/your/cert.pem;
      # ssl_certificate_key /path/to/your/key.pem;

      location / {
          proxy_pass http://localhost:8080; # 指向Spring Boot应用
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
      }
  }
  ```

---

## 11. Roadmap & Future Work

本项目已经实现了核心功能，但未来还有广阔的扩展空间。

### 11.1. 核心功能增强

- **在线IDE与代码提交**:
  - 集成一个前端代码编辑器（如 Monaco Editor）。
  - 支持用户在线编写代码、运行、调试并直接提交。
- **实时评测与通知**:
  - **异步评测**: 引入消息队列（如 RabbitMQ 或 Kafka）来处理评测任务。Judge Service 接收到提交后，将其放入队列，由独立的评测机集群处理。评测结果通过 WebSocket 或其他实时通信方式推送给前端。
  - **实时通知**: 使用 WebSocket 向用户实时推送评测状态（如 AC, WA, TLE 等），并实时更新页面。
- **排行榜系统**:
  - 为每个竞赛创建实时更新的排行榜。
  - 可能需要使用 Redis 的 Sorted Set 数据结构来高效实现。

### 11.2. 扩展模块与集成

- **题库社区化**:
  - 允许用户互相分享题目、评论题目。
  - 支持 Markdown 和代码高亮。
- **用户贡献**:
  - 允许用户提交题目、题解，并进行审核。

### 11.3. 性能优化与监控

- **缓存策略**:
  - 对热点数据（如题目信息、用户信息、排行榜数据）进行缓存（如 Redis 或 Caffeine）。
  - 优化数据库查询，减少数据库压力。
- **异步处理**:
  - 将非核心、耗时的操作（如发送邮件通知、生成复杂报表）改为异步处理（使用 `@Async` 注解和线程池）。
- **可观测性**:
  - 集成 Spring Boot Actuator 提供监控端点。
  - 使用 Prometheus (监控) 和 Grafana (可视化) 来收集和展示应用指标。
  - 实现分布式追踪（如 OpenTelemetry），方便排查分布式系统中的问题。
