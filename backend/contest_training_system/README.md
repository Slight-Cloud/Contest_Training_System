# 竞赛训练系统 - 后端服务 (Contest Training System - Backend)

---

## 1. 项目概述

本项目是 **竞赛训练系统 (Contest Training System)** 的后端服务。它是一个采用 Java 和 Spring Boot 构建的高性能、高可用的服务端应用程序，为整个平台提供核心业务逻辑、数据持久化、用户认证与授权、以及 RESTful API 接口。

### 1.1. 设计理念

- **模块化与可扩展性**: 系统被精心设计为一系列松耦合的模块（如用户、题目、竞赛、训练计划等），易于维护和扩展。
- **标准化与规范性**: 全面拥抱 RESTful 架构风格，API 设计遵循统一规范。
- **安全与稳定**: 采用基于 JWT 的无状态认证和 RBAC 权限模型，确保系统和用户数据的安全。
- **高效与性能**: 在关键业务路径上关注性能优化，确保系统在高并发场景下的稳定性。

---

## 2. 技术栈

| 技术类别 | 技术名称 | 版本/说明 |
| :--- | :--- | :--- |
| **核心框架** | Spring Boot | 3.5.6 |
| **Web 框架** | Spring MVC | 包含在 Spring Boot 中 |
| **安全框架** | Spring Security | 包含在 Spring Boot 中 |
| **认证方案** | JSON Web Tokens (JWT) | 0.12.5 |
| **持久层框架**| MyBatis | mybatis-spring-boot-starter 3.0.3 |
| **分页插件** | PageHelper | 1.4.7 |
| **数据库** | MySQL | 8.0+ |
| **数据库驱动**| mysql-connector-java | 8.0.33 |
| **构建工具** | Maven | |
| **开发语言** | Java | 17 |
| **辅助工具** | Lombok | |

---

## 3. 架构设计

### 3.1. 分层架构

本项目采用经典的三层架构模式，确保了各层职责单一、边界清晰，从而提高了代码的可读性、可维护性和可测试性。

- **表示层 (Presentation Layer)**: 由 `controller` 包下的 Spring MVC 控制器构成，作为系统的 HTTP 入口，负责接收请求、参数校验和结果封装。
- **业务逻辑层 (Business Logic Layer)**: 由 `service` 包下的服务类构成，是系统核心业务逻辑的所在地，负责编排和实现复杂的业务场景，并管理数据库事务。
- **数据访问层 (Data Access Layer)**: 由 `mapper` 包下的 MyBatis 接口及对应的 XML 文件构成，负责与数据库进行直接交互，实现数据的增、删、改、查（CRUD）。

### 3.2. 架构图

```
+-----------------------------------------------------------------------------+
|                            客户侧 (Clients)                               |
| (Web App / Mobile App / Third-party Services)                               |
+---------------------------------^-------------------------------------------+
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
|    +-----------------------------------^---------------------------------|
|                                        |
|    +---------------------------------------------------------------------+
|    |                          业务逻辑层(Service)                      |
|    | (UserService, ContestService, ProblemService, etc.)                 |
|    +-----------------------------------^---------------------------------|
|                                        |
|    +---------------------------------------------------------------------+
|    |                         数据访问层(Mapper/DAO)                    |
|    | (UserMapper, ContestMapper, ProblemMapper, etc. - MyBatis Interfaces)|
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

---

## 4. 项目结构

核心代码位于 `src/main/java/com/system` 目录下，并按照功能和分层架构的原则进行了组织。

```
contest_training_system
├── pom.xml                   # Maven项目配置文件
└── src
    └── main
        ├── java
        │   └── com
        │       └── system
        │           ├── config/              # 配置类 (如 SecurityConfig)
        │           ├── controller/          # 控制器层 (处理HTTP请求)
        │           ├── dto/                 # 数据传输对象 (封装请求数据)
        │           ├── entity/              # 数据库实体类
        │           ├── exception/           # 全局异常处理
        │           ├── mapper/              # MyBatis数据访问接口 (DAO)
        │           ├── service/             # 业务逻辑层
        │           ├── util/                # 工具类 (如 JwtUtil)
        │           └── vo/                  # 视图对象 (封装响应数据)
        └── resources
            ├── application.yml     # Spring Boot主配置文件
            └── mapper/             # MyBatis的XML映射文件 (存放SQL)
```

---

## 5. 快速入门指南

本指南旨在帮助新加入的开发者快速搭建开发环境，并成功运行后端服务。

### 5.1. 环境要求

- **JDK**: `17` 或更高版本
- **Maven**: `3.6` 或更高版本
- **IDE**: 推荐 `IntelliJ IDEA` (Community 或 Ultimate 版均可)
- **数据库**: `MySQL 8.0` 或更高版本

### 5.2. 设置步骤

1.  **克隆代码仓库**
    ```bash
    git clone <your-repository-url>
    cd ContestSystem/backend/contest_training_system
    ```

2.  **配置数据库**
    - 在本地 MySQL 服务器中创建一个新的数据库。
      ```sql
      CREATE DATABASE contest_system_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
      ```
    - 将项目根目录 `../../sql/bakeup.sql` 文件导入到你创建的数据库中，以初始化表结构和数据。

3.  **配置 `application.yml`**
    - 打开 `src/main/resources/application.yml` 文件。
    - 修改 `spring.datasource` 部分以匹配你的本地数据库设置（主要是用户名和密码）。
      ```yaml
      spring:
        datasource:
          url: jdbc:mysql://localhost:3306/contest_system_db?useSSL=false&serverTimezone=UTC
          username: your_mysql_username
          password: your_mysql_password
          driver-class-name: com.mysql.cj.jdbc.Driver
      ```

4.  **配置 JWT 密钥**
    - **重要**: 打开 `src/main/java/com/system/util/JwtUtil.java` 并修改 `SECRET` 常量。这是一个用于签名 JWT 的密钥，必须保证其复杂性和保密性。

5.  **在 IntelliJ IDEA 中打开项目**
    - 选择 `File -> Open...` 并导航到本项目的 `pom.xml` 文件或根目录。
    - IDEA 会自动识别为 Maven 项目并下载所有依赖。

6.  **运行应用**
    - 在项目结构中找到 `src/main/java/com/system/ContestTrainingSystemApplication.java` 文件。
    - 右键点击该文件，选择 `Run 'ContestTrainingSystemApplication'`。
    - 如果一切顺利，控制台将显示 Spring Boot 的启动日志，表明应用已在默认端口 `8080` 上启动。

---

## 6. API 概览

所有 API 接口都返回一个统一的 `Result<T>` 对象，并对分页查询使用 `PageResultVO<T>`。详细的 API 文档请参阅 `docs/ProjectStructure.md`。

### 6.1. 用户模块 (`/user`, `/admin/users`)
- `POST /register`: 注册新用户。
- `POST /login`: 用户登录，返回 JWT。
- `GET /user/profile`: 获取当前登录用户的个人信息。
- `PUT /user/update`: 更新当前登录用户的个人信息。
- `GET /admin/users`: [管理员] 分页查询用户列表。

### 6.2. 题目模块 (`/problem`)
- `POST /problem/create`: [管理员/教师] 创建一个新题目。
- `PUT /problem/update`: [管理员/教师] 更新一个已存在的题目。
- `GET /problem/list`: 获取题目列表，支持分页和筛选。
- `GET /problem/{problemId}`: 获取单个题目的详细信息。
- `DELETE /problem/{problemId}`: [管理员] 逻辑删除一个题目。

### 6.3. 竞赛模块 (`/contest`)
- `POST /contest/create`: [管理员/教师] 创建一个新竞赛。
- `PUT /contest/update`: [管理员/教师] 更新一个已存在的竞赛。
- `GET /contest/list`: 获取竞赛列表，支持分页和筛选。
- `GET /contest/{contestId}`: 获取单个竞赛的详细信息。
- `POST /contest/join`: 用户加入一个竞赛。

### 6.4. 训练计划模块 (`/training_plan`)
- `POST /training_plan/create`: [管理员/教师] 创建一个新训练计划。
- `PUT /training_plan/update`: [管理员/教师] 更新一个已存在的训练计划。
- `GET /training_plan/list`: 获取训练计划列表。
- `GET /training_plan/{planId}`: 获取单个训练计划的详细信息。

---

## 7. 数据库表结构

以下为核心数据表，详细关系请参考 `docs/ProjectStructure.md`。

- **`user`**: 用户表
- **`problem`**: 题目表
- **`contest`**: 竞赛表
- **`training_plan`**: 训练计划表
- **`submission`**: 提交记录表
- **关联表**: `contest_participant`, `contest_problem`, `training_plan_contest`, `training_plan_student`

---

## 8. 部署指南

1.  **打包应用**
    在本项目根目录下，使用 Maven 命令打包成一个可执行的 JAR 文件。
    ```bash
    mvn clean package -DskipTests
    ```
    打包好的 JAR 文件会位于 `target/` 目录下。

2.  **运行应用**
    将打包好的 JAR 文件上传到服务器，并通过以下命令在后台运行：
    ```bash
    nohup java -jar contest_training_system-x.x.x.jar --spring.profiles.active=prod > app.log 2>&1 &
    ```
    这会将应用的日志输出到 `app.log` 文件。
