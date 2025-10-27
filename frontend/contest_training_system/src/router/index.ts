import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/store/user';

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue'),
        meta: { requiresAuth: false },
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/register/index.vue'),
        meta: { requiresAuth: false },
    },
    {
        path: '/',
        component: () => import('@/layout/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/dashboard/index.vue'),
                meta: { title: '首页' },
            },
            {
                path: 'contests',
                name: 'ContestList',
                component: () => import('@/views/contest/list.vue'),
                meta: { title: '赛事列表' },
            },
            {
                path: 'contest/:id',
                name: 'ContestDetail',
                component: () => import('@/views/contest/detail.vue'),
                meta: { title: '赛事详情' },
            },
            {
                path: 'contest/:id/ranking',
                name: 'ContestRanking',
                component: () => import('@/views/contest/ranking.vue'),
                meta: { title: '赛事排名' },
            },
            {
                path: 'contest/:id/arena',
                name: 'ContestArena',
                component: () => import('@/views/contest/arena.vue'),
                meta: { title: '答题' },
            },
            {
                path: 'admin/users',
                name: 'AdminUserManagement',
                component: () => import('@/views/admin/UserManagement.vue'),
                meta: { title: '用户管理', roles: ['ADMIN'] },
            },
            {
                path: 'judge/reviews',
                name: 'JudgeReviewList',
                component: () => import('@/views/judge/ReviewList.vue'),
                meta: { title: '评审任务', roles: ['JUDGE'] },
            },
            {
                path: 'profile',
                name: 'MyProfile',
                component: () => import('@/views/participant/MyProfile.vue'),
                meta: { title: '我的资料' },
            },
            // 题目管理路由
            {
                path: 'problems',
                name: 'ProblemList',
                component: () => import('@/views/problem/list.vue'),
                meta: { title: '题库管理' },
            },
            {
                path: 'problem/create',
                name: 'ProblemCreate',
                component: () => import('@/views/problem/create.vue'),
                meta: { title: '新建题目', roles: ['ADMIN', 'TEACHER'] },
            },
            {
                path: 'problem/:id',
                name: 'ProblemDetail',
                component: () => import('@/views/problem/detail.vue'),
                meta: { title: '题目详情' },
            },
            {
                path: 'problem/:id/edit',
                name: 'ProblemEdit',
                component: () => import('@/views/problem/create.vue'),
                meta: { title: '编辑题目', roles: ['ADMIN', 'TEACHER'] },
            },
            {
                path: 'problem/:id/submit',
                name: 'ProblemSubmit',
                component: () => import('@/views/problem/submit.vue'),
                meta: { title: '提交代码', roles: ['STUDENT'] },
            },
            {
                path: 'problem/:id/solution',
                name: 'ProblemSolution',
                component: () => import('@/views/problem/solution.vue'),
                meta: { title: '题解管理', roles: ['ADMIN', 'TEACHER'] },
            },
            // 提交记录路由
            {
                path: 'submissions',
                name: 'SubmissionList',
                component: () => import('@/views/submission/list.vue'),
                meta: { title: '提交记录' },
            },
            {
                path: 'submission/:id',
                name: 'SubmissionDetail',
                component: () => import('@/views/submission/detail.vue'),
                meta: { title: '提交详情' },
            },
            // 训练计划路由
            {
                path: 'training',
                name: 'TrainingPlanList',
                component: () => import('@/views/training/list.vue'),
                meta: { title: '训练计划' },
            },
            {
                path: 'training/:id',
                name: 'TrainingPlanDetail',
                component: () => import('@/views/training/detail.vue'),
                meta: { title: '训练计划详情' },
            },
        ],
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/error/404.vue'),
        meta: { requiresAuth: false },
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach(async (to, _from, next) => {
    const userStore = useUserStore();
    const token = userStore.token;
    
    // 详细调试日志
    console.log('=== 路由守卫调试 ===');
    console.log('目标路由:', to.path);
    console.log('路由名称:', to.name);
    console.log('完整路径:', to.fullPath);
    console.log('查询参数:', to.query);
    console.log('路由参数:', to.params);
    console.log('路由meta:', to.meta);
    console.log('用户token:', token ? `${token.substring(0, 20)}...` : 'null');
    console.log('用户信息:', userStore.userInfo);
    console.log('用户角色:', userStore.role);
    console.log('用户ID:', userStore.userId);
    
    // 不需要认证的路由
    const requiresAuth = to.meta?.requiresAuth !== false;
    
    if (requiresAuth && !token) {
        // 需要认证但未登录，跳转到登录页
        console.log('未登录，跳转到登录页');
        next({ name: 'Login', query: { redirect: to.fullPath } });
        return;
    }
    
    if (!requiresAuth && token && (to.name === 'Login' || to.name === 'Register')) {
        // 已登录用户访问登录/注册页，跳转到首页
        console.log('已登录用户访问登录页，跳转到首页');
        next({ name: 'Dashboard' });
        return;
    }
    
    // 如果有token但没有用户信息，先获取用户信息
    if (token && !userStore.userInfo) {
        console.log('有token但没有用户信息，正在获取...');
        try {
            await userStore.fetchUserProfile();
            console.log('用户信息获取成功:', userStore.userInfo);
            console.log('用户角色:', userStore.role);
        } catch (error) {
            console.error('获取用户信息失败:', error);
            // 获取用户信息失败，清除token并跳转到登录页
            userStore.clearToken();
            next({ name: 'Login', query: { redirect: to.fullPath } });
            return;
        }
    }
    
    // 检查角色权限
    if (to.meta?.roles && Array.isArray(to.meta.roles)) {
        const allowedRoles = to.meta.roles as string[];
        const userRole = userStore.role;
        console.log('需要角色:', allowedRoles);
        console.log('当前角色:', userRole);
        if (allowedRoles.length > 0 && userRole && !allowedRoles.includes(userRole)) {
            console.log('角色不匹配，拒绝访问');
            next({ name: 'Dashboard' });
            return;
        }
    }
    
    console.log('通过路由守卫');
    next();
});

export default router;
