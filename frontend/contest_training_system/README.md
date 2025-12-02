# 🏆 竞赛集训系统 (Contest Training System) - 前端架构文档

## 1. 项目概览

本项目是 **竞赛集训系统** 的前端单页应用 (SPA)，基于 **Vue 3** 和 **TypeScript** 构建，采用业界流行的 **Vite** 构建工具。系统旨在提供一个高性能、响应式且用户体验优秀的代码练习与竞赛平台。

前端架构设计注重组件化、可维护性和类型安全，利用 Vue 3 的 **Composition API** 实现逻辑复用，结合 **Element Plus** 组件库快速构建现代化的 Web 界面。

## 2. 核心技术栈

| 类别                   | 技术/库       | 版本     | 说明                                      |
| :--------------------- | :------------ | :------- | :---------------------------------------- |
| **核心框架**     | Vue 3         | ^3.5.x   | 使用 Composition API (`<script setup>`) |
| **编程语言**     | TypeScript    | ~5.9.x   | 强类型约束，提升代码质量与维护性          |
| **构建工具**     | Vite          | ^7.1.x   | 极速的开发服务器与构建打包工具            |
| **UI 组件库**    | Element Plus  | ^2.11.x  | 基于 Vue 3 的桌面端组件库                 |
| **路由管理**     | Vue Router    | ^4.5.x   | 处理 SPA 的页面路由跳转                   |
| **状态管理**     | Pinia         | ^3.0.x   | 轻量级、类型安全的 Vue 状态管理库         |
| **HTTP 客户端**  | Axios         | ^1.12.x  | 处理 API 请求与响应拦截                   |
| **代码编辑器**   | Monaco Editor | ^0.54.x  | 提供类似 VS Code 的在线代码编辑体验       |
| **富文本编辑器** | WangEditor    | ^5.1.x   | 用于题目描述、公告发布的富文本编辑        |
| **代码高亮**     | Highlight.js  | ^11.11.x | 代码片段语法高亮                          |
| **日期处理**     | Day.js        | ^1.11.x  | 轻量级的时间日期格式化工具                |

## 3. 项目结构详解

```
frontend/contest_training_system/
├── public/                 # 静态资源 (favicon, etc.)
├── src/
│   ├── api/               # API 接口层 - 封装后端接口调用
│   │   ├── contest.ts    # 竞赛模块接口
│   │   ├── problem.ts    # 题目模块接口
│   │   ├── submission.ts # 提交记录接口
│   │   ├── training.ts   # 训练计划接口
│   │   └── user.ts       # 用户认证接口
│   ├── assets/            # 静态资源 (Images, CSS)
│   ├── components/        # 公共业务组件
│   │   ├── CodeEditor.vue      # Monaco Editor 封装组件
│   │   ├── Countdown.vue       # 倒计时组件
│   │   └── TestCaseResults.vue # 测试用例结果展示组件
│   ├── layout/            # 页面布局组件 (Header, Sidebar, Footer)
│   ├── router/            # 路由配置
│   │   └── index.ts      # 路由定义与导航守卫
│   ├── store/             # Pinia 状态存储
│   │   └── user.ts       # 用户登录状态、个人信息管理
│   ├── types/             # TypeScript 类型定义
│   │   ├── axios.d.ts    # 扩展 Axios 响应类型
│   │   └── index.ts      # 通用业务类型
│   ├── utils/             # 工具库
│   │   ├── helpers.ts    # 通用辅助函数
│   │   └── request.ts    # Axios 实例配置 (拦截器、错误处理)
│   ├── views/             # 页面视图 (Page Views)
│   │   ├── admin/        # 管理员后台页面
│   │   ├── contest/      # 竞赛列表与详情页
│   │   ├── dashboard/    # 用户个人中心/仪表盘
│   │   ├── judge/        # 评测状态页
│   │   ├── problem/      # 题库列表与题目详情页
│   │   ├── submission/   # 提交记录列表与详情页
│   │   └── training/     # 训练计划相关页面
│   ├── App.vue            # 根组件
│   ├── main.ts            # 应用入口 (插件注册, 全局样式)
│   └── style.css          # 全局通用样式
├── .env                   # 环境变量
├── index.html             # 入口 HTML 模板
├── package.json           # 项目依赖与脚本
├── tsconfig.json          # TypeScript 编译器配置
└── vite.config.ts         # Vite 配置 (代理, 插件, 别名)
```

## 4. 架构设计与实现细节

### 4.1 组件化设计 (Component-Based Architecture)

系统采用分层组件设计：

* **基础组件**: Element Plus 提供的按钮、表单、表格等原子组件。
* **业务组件 (`src/components`)**: 封装特定业务逻辑的组件，如 `CodeEditor.vue` (集成 Monaco Editor，处理代码补全和主题切换) 和 `TestCaseResults.vue` (展示评测点的通过/失败状态)。
* **页面组件 (`src/views`)**: 组装业务组件和基础组件，负责页面级的数据获取和状态管理。

### 4.2 网络请求封装 (`src/utils/request.ts`)

基于 Axios 进行深度封装，统一处理 HTTP 请求生命周期：

* **请求拦截器**:
  * 自动读取 Pinia `UserStore` 中的 Token。
  * 将 Token 注入 Header `Authorization: Bearer <token>`，实现无感认证。
* **响应拦截器**:
  * 统一解包后端 `Result<T>` 结构，直接返回 `data` 字段给业务层。
  * **全局错误处理**: 拦截非 200 状态码或业务错误码 (code=0)，使用 `ElMessage` 全局提示错误信息。
  * **403/401 处理**: 检测到 Token 过期或未登录时，自动跳转至登录页或提示重新登录。

### 4.3 状态管理 (Pinia)

使用 Pinia 管理全局状态，核心 Store 包括：

* **User Store (`src/store/user.ts`)**:
  * 管理 `token`, `userInfo`, `role`。
  * 提供 `login()`, `logout()`, `fetchUserProfile()` 等 Action。
  * 利用持久化策略 (LocalStorage) 保持登录态刷新不丢失。

### 4.4 路由与权限控制 (`src/router`)

* 使用 **Vue Router** 进行页面路由。
* **导航守卫 (Navigation Guards)**:
  * 在 `beforeEach` 中检查路由元信息 (`meta.requiresAuth`)。
  * 对于需要权限的页面（如 Admin 模块），校验用户角色 (`userStore.role`)。如果权限不足，拦截并重定向。

## 5. 核心功能模块

### 5.1 在线代码编辑 (Monaco Editor集成)

* **位置**: `src/components/CodeEditor.vue`
* **功能**:
  * 集成微软 Monaco Editor，提供类似 VS Code 的体验。
  * 支持 C++, Java, Python 等多种语言的语法高亮和智能提示。
  * 支持深色/浅色主题切换。
  * 双向绑定代码内容，实时同步到父组件。

### 5.2 竞赛模块

* **列表页**: 展示正在进行、即将开始和已结束的竞赛，支持状态筛选。
* **详情页**:
  * 受保护的路由，需验证报名状态。
  * 包含题目列表、实时排行榜 (Ranklist)、提交记录。
  * **倒计时组件**: 实时显示距离竞赛开始或结束的时间。

### 5.3 题目与评测

* **Markdown 渲染**: 题目描述支持 Markdown 格式，使用相关库进行渲染和数学公式支持。
* **实时评测反馈**: 提交代码后，通过轮询或 WebSocket (规划中) 获取评测状态 (Pending -> Running -> AC/WA)，并动态更新 UI。

### 5.4 题解系统

* **查看题解**: 用户可以在题目详情页查看相关题解，支持 Markdown 渲染。
* **管理功能**: 教师/管理员可以发布、编辑和删除题解，为学生提供解题思路。

## 6. 开发与构建

### 6.1 环境要求

* Node.js >= 16
* pnpm >= 8

### 6.2 常用命令

```bash
# 安装依赖
pnpm install

# 启动开发服务器 (默认端口 9999)
pnpm dev

# 类型检查并构建生产版本
pnpm build

# 预览生产构建
pnpm preview
```

### 6.3 开发环境代理 (Proxy)

为了解决开发时的跨域问题 (CORS)，`vite.config.ts` 配置了反向代理：

```typescript
server: {
    port: 9999,
    host: 'localhost',
    open: true,
    proxy: {
      '/api': {
        target: backendTarget,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
```

## 7. 代码规范

* **TypeScript**: 严格模式 (`strict: true`)，禁止隐式 `any`，确保类型安全。
* **Vue**: 推荐使用 Setup Sugar (`<script setup lang="ts">`)。
* **样式**: 尽量使用 Element Plus 提供的 CSS 变量进行样式定制，自定义样式建议使用 Scoped CSS。

---

**维护者**: Contest System Team
**最后更新**: 2025-12-01
