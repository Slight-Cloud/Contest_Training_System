# 竞赛集训系统 (Contest Training System) - 项目说明

---

## 1. 项目概述与愿景

### 1.1. 项目简介

**竞赛集训系统 (Contest Training System)** 是一个专为高校、编程爱好者及教育机构设计的，集在线编程、实时评测、竞赛组织、系统化训练于一体的综合性平台。它不仅是一个传统的在线判题（Online Judge）系统，更是一个旨在通过结构化的训练计划和模拟竞赛，系统性提升用户算法和编程能力的学习与实践平台。

本项目采用主流且成熟的前后端分离架构，包含一个由 **Spring Boot** 构建的高性能后端服务和一个基于 **Vue.js 3** 的现代化前端应用。

### 1.2. 设计理念

本项目的核心设计理念是 **“以赛促学，以练固本”**。我们追求的不仅是功能的实现，更是一种能够激发用户学习兴趣、引导用户持续进步的体验。

- **模块化与可扩展性**: 系统被精心设计为一系列松耦合的模块（如用户、题目、竞赛、训练计划等），为未来的功能扩展（如在线IDE、社区讨论、AI辅助等）预留了清晰的接口和空间。
- **标准化与规范性**: 后端全面拥抱 RESTful 架构风格，API 设计遵循统一规范。前端采用组件化开发模式，代码风格遵循社区最佳实践。
- **安全与稳定**: 采用了多层次的安全策略，包括基于 JWT 的无状态认证、精细到接口级别的角色权限控制（RBAC），以及对所有外部输入的严格校验，确保系统和用户数据的安全。
- **高效与性能**: 在关键路径（如代码评测、排行榜生成）上关注性能优化，通过合理的数据库设计和索引优化，确保系统在高并发场景下依然能够提供稳定、及时的响应。

### 1.3. 目标用户

- **学生/编程爱好者**: 进行日常练习、参加模拟竞赛、完成训练任务，提升编程和算法能力。
- **教师/教练**: 创建私有题目、组织班级竞赛、发布训练计划并跟踪学生的学习进度。
- **系统管理员**: 负责整个平台的维护、用户管理、系统配置和内容审核。

---

## 2. 项目整体结构

本项目由两个核心子项目组成，分别位于 `backend` 和 `frontend` 目录下。

```
ContestSystem/
├── backend/
│   └── contest_training_system/      # 后端 Spring Boot 项目
├── frontend/
│   └── contest_training_system/      # 前端 Vue.js 项目
├── sql/
│   └── bakeup.sql                    # 数据库备份文件
└── README.md                         # 本文件
```

---

## 3. 技术栈

### 3.1. 后端技术栈

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

### 3.2. 前端技术栈

| 技术类别 | 技术名称 | 版本/说明 |
| :--- | :--- | :--- |
| **核心框架** | Vue.js | 3.5.22 |
| **UI 组件库** | Element Plus | 2.11.4 |
| **构建工具** | Vite | 7.1.7 |
| **开发语言** | TypeScript | ~5.9.3 |
| **路由管理** | Vue Router | 4.5.1 |
| **状态管理** | Pinia | 3.0.3 |
| **HTTP 请求**| Axios | 1.12.2 |
| **代码编辑器**| Monaco Editor | 0.54.0 |
| **包管理器** | pnpm | |

---

## 4. 后端服务 (Backend)

### 4.1. 后端架构

后端服务是整个系统的“大脑”和“中枢”，采用经典的三层架构模式，确保各层职责单一、边界清晰。

```
+-----------------------------------------------------------------------------+
|                            客户侧 (Frontend App)                            |
+---------------------------------^-------------------------------------------+
                                  | (RESTful API Call)
                                  v
+----------------------------- Nginx (可选) ----------------------------------+
| (反向代理, 负载均衡, SSL 终止)                                              |
+---------------------------------^-------------------------------------------+
                                  |
                                  v
+-----------------------------------------------------------------------------+
|                      后端 Spring Boot 应用 (Port: 8080)                     |
|                                                                             |
|    +-------------------- Spring Security Filter Chain -------------------+
|    |  (JWT 校验与认证, @PreAuthorize 注解权限控制)                         |
|    +-----------------------------------^----------------------------------+
|                                        |
|    +------------------------- Web 层 (Controller) ------------------------+
|    | (接收/响应HTTP请求, 验证DTO, 调用Service, 封装VO)                     |
|    +-----------------------------------^----------------------------------+
|                                        |
|    +----------------------- 业务逻辑层 (Service) -------------------------+
|    | (实现核心业务逻辑, 事务管理 @Transactional)                           |
|    +-----------------------------------^----------------------------------+
|                                        |
|    +----------------------- 数据访问层 (Mapper/DAO) ----------------------+
|    | (定义数据库原子操作, 与 MyBatis XML 映射)                             |
|    +-----------------------------------^----------------------------------+
|                                        |
+----------------------------------------|------------------------------------+
                                         |
                                         v
+---------------------------- 数据持久层 (Database) --------------------------+
|    +----------------------------- MySQL --------------------------------+
|    | (用户表, 题目表, 竞赛表, 提交记录表等)                                |
|    +--------------------------------------------------------------------+
+-----------------------------------------------------------------------------+
```

### 4.2. 后端项目结构

核心代码位于 `backend/contest_training_system/src/main/java/com/system` 目录下。

```
java/com/system/
├── config/              # 配置类 (如 SecurityConfig, WebConfig)
├── controller/          # 控制器层 (处理HTTP请求)
├── dto/                 # 数据传输对象 (用于前端请求数据封装)
├── entity/              # 数据库实体类
├── exception/           # 全局异常处理
├── mapper/              # MyBatis数据访问接口 (DAO)
├── service/             # 业务逻辑层
├── util/                # 工具类 (如 JwtUtil, UserContext)
└── vo/                  # 视图对象 (用于向前端返回数据)
```

### 4.3. 后端本地开发指南

#### 4.3.1. 环境要求
- **JDK**: `17` 或更高版本
- **Maven**: `3.6` 或更高版本
- **IDE**: 推荐 `IntelliJ IDEA`
- **数据库**: `MySQL 8.0` 或更高版本

#### 4.3.2. 设置步骤

1.  **克隆代码仓库**:
    ```bash
    git clone <your-repository-url>
    cd ContestSystem
    ```
2.  **创建与配置数据库**:
    - 在本地 MySQL 服务器中创建一个新的数据库，例如 `contest_system_db`。
      ```sql
      CREATE DATABASE contest_system_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
      ```
    - 将项目根目录 `sql/bakeup.sql` 文件导入到你创建的数据库中，以初始化表结构和基础数据。

3.  **配置 `application.yml`**:
    - 打开 `backend/contest_training_system/src/main/resources/application.yml` 文件。
    - 修改 `spring.datasource` 部分，填入你的本地数据库连接信息（用户名和密码）。
      ```yaml
      spring:
        datasource:
          url: jdbc:mysql://localhost:3306/contest_system_db?useSSL=false&serverTimezone=UTC
          username: your_mysql_username
          password: your_mysql_password
          driver-class-name: com.mysql.cj.jdbc.Driver
      ```
4.  **配置 JWT 密钥**:
    - **重要**: 打开 `backend/contest_training_system/src/main/java/com/system/util/JwtUtil.java`。
    - 修改 `SECRET` 常量的值。请将其设置为一个足够长且随机的字符串，以保证 Token 安全。

5.  **在 IDE 中运行**:
    - 使用 IntelliJ IDEA 打开 `backend/contest_training_system` 目录。
    - IDEA 会自动识别为 Maven 项目并下载所有依赖。
    - 找到主类 `ContestTrainingSystemApplication.java` 并运行它。
    - 服务默认将在 `8080` 端口启动。

---

## 5. 前端应用 (Frontend)

### 5.1. 前端架构

前端是一个基于 Vue.js 3 的现代化单页应用 (SPA)，使用 Vite 作为构建工具，具备快速的开发体验和高效的打包性能。

- **组件化**: 遵循 Vue 的组件化思想，将 UI 拆分为可复用的组件，存放于 `src/components` 和 `src/views`。
- **状态管理**: 使用 Pinia 统一管理全局状态，例如用户信息、角色和权限。
- **路由**: 使用 Vue Router 管理页面路由，并结合路由守卫进行登录校验和权限控制。
- **UI 组件库**: 大量采用 Element Plus 组件库，并在此基础上进行了深度的主题定制，以实现 `Darkanian (GitHub Soft)` 的暗色主题风格。
- **请求封装**: 使用 Axios 进行 HTTP 请求，并在 `src/utils/request.ts` 中封装了请求/响应拦截器，用于统一处理 Token 注入和错误信息。

### 5.2. 前端项目结构

核心代码位于 `frontend/contest_training_system/src` 目录下。

```
src/
├── api/              # API 接口定义
├── assets/           # 静态资源
├── components/       # 全局公共组件
├── layout/           # 布局组件
├── router/           # 路由配置
├── store/            # Pinia 状态管理
├── types/            # TypeScript 类型定义
├── utils/            # 工具函数
├── views/            # 页面视图
├── App.vue           # 根组件
├── main.ts           # 入口文件
└── style.css         # 全局样式与主题变量
```

### 5.3. 前端本地开发指南

#### 5.3.1. 环境要求
- **Node.js**: `18.x` 或更高版本
- **包管理器**: `pnpm` (项目使用 `pnpm` 进行依赖管理)
- **IDE**: 推荐 `Visual Studio Code`

#### 5.3.2. 设置步骤

1.  **进入前端目录**:
    在你的终端中，导航到前端项目目录。
    ```bash
    cd frontend/contest_training_system
    ```

2.  **安装依赖**:
    使用 `pnpm` 安装所有项目依赖。
    ```bash
    pnpm install
    ```

3.  **配置后端代理**:
    - `vite.config.ts` 文件中已配置了开发环境下的 API 代理。所有对 `/api` 的请求都会被转发到后端服务。
    - 请确保你的后端服务正在 `http://localhost:8000` 上运行。如果你的后端端口不是 `8000`，请相应修改 `vite.config.ts` 中的 `backendTarget` 常量。
      ```typescript
      // vite.config.ts
      const backendTarget = 'http://localhost:8000'; // 修改为你的后端地址
      ```

4.  **启动开发服务器**:
    运行以下命令。
    ```bash
    pnpm run dev
    ```
    - 此命令会启动一个热重载的开发服务器。
    - **注意**: 由于本地环境的差异，可能会遇到端口权限问题。我们已将默认端口设置为 `9999` 以尽量避免冲突。
    - 成功启动后，你可以在浏览器中访问 `http://localhost:9999` 来查看应用。

---

## 6. 部署说明

### 6.1. 后端部署
1.  在 `backend/contest_training_system` 目录下执行 Maven 打包命令：
    ```bash
    mvn clean package -DskipTests
    ```
2.  这将在 `target/` 目录下生成一个可执行的 `JAR` 文件。
3.  将此 JAR 文件上传到服务器，并通过以下命令在后台运行：
    ```bash
    nohup java -jar contest_training_system-x.x.x.jar --spring.profiles.active=prod > app.log 2>&1 &
    ```

### 6.2. 前端部署
1.  在 `frontend/contest_training_system` 目录下执行打包命令：
    ```bash
    pnpm build
    ```
2.  这将在 `frontend/contest_training_system/nginx-1.22.0-web/html` 目录下生成用于生产环境的静态文件。
3.  你可以将 `html` 目录下的所有内容部署到任何静态文件服务器上，例如 Nginx。

### 6.3. 使用 Nginx 进行反向代理 (推荐)
在生产环境中，建议使用 Nginx 作为反向代理服务器，统一处理前端静态资源和后端 API 请求。

下面是一个示例 Nginx 配置：
```nginx
server {
    listen       80;
    server_name  your_domain.com;

    # 前端静态资源
    location / {
        root   /path/to/your/frontend/dist; # 指向前端打包后的目录
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass         http://localhost:8080/; # 指向后端 Spring Boot 应用
        proxy_set_header   Host $host;
        proxy_set_header   X-Real-IP $remote_addr;
        proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
    }
}
```
这只是一个基本示例，你需要根据你的实际部署情况进行调整。
