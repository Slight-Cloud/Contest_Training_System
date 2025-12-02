<template>
  <el-container class="layout-container">
    <!-- 遮罩层 - 仅移动端显示 -->
    <transition name="fade">
      <div 
        v-if="isMobileSidebarOpen" 
        class="sidebar-overlay"
        @click="closeMobileSidebar"
      ></div>
    </transition>

    <!-- 侧边栏 -->
    <el-aside 
      class="layout-aside sidebar-fixed" 
      :class="{ 'is-mobile-open': isMobileSidebarOpen }"
    >
      <div class="layout-aside__header">
        <div class="layout-aside__brand">Contest Hub</div>
        <el-tag size="small" class="layout-aside__role" effect="dark">{{ roleLabel }}</el-tag>
      </div>
      <el-scrollbar class="layout-aside__scroll">
        <el-menu 
          :default-active="activeMenu" 
          router 
          class="layout-menu" 
          :collapse="false"
          @select="handleMenuSelect"
        >
          <template v-for="item in filteredMenus" :key="item.path">
            <el-menu-item :index="item.path" class="menu-item">
              <span>{{ item.label }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
      <div class="layout-aside__footer">
        <p>每日进步一点点 ✨</p>
      </div>
    </el-aside>

    <el-container class="layout-content main-content">
      <el-header class="layout-header main-header">
        <!-- 汉堡菜单按钮 - 仅移动端显示 -->
        <el-button 
          class="mobile-menu-trigger"
          text
          @click="toggleMobileSidebar"
        >
          <el-icon :size="24">
            <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
              <path fill="currentColor" d="M160 448a32 32 0 0 1-32-32V160.064a32 32 0 0 1 32-32h256a32 32 0 0 1 32 32V416a32 32 0 0 1-32 32H160zm448 0a32 32 0 0 1-32-32V160.064a32 32 0 0 1 32-32h255.936a32 32 0 0 1 32 32V416a32 32 0 0 1-32 32H608zM160 896a32 32 0 0 1-32-32V608a32 32 0 0 1 32-32h256a32 32 0 0 1 32 32v256a32 32 0 0 1-32 32H160zm448 0a32 32 0 0 1-32-32V608a32 32 0 0 1 32-32h255.936a32 32 0 0 1 32 32v256a32 32 0 0 1-32 32H608z"/>
            </svg>
          </el-icon>
        </el-button>

        <div class="layout-header__info">
          <h1 class="layout-header__title">{{ currentTitle }}</h1>
          <p class="layout-header__subtitle">{{ headerSubtitle }}</p>
        </div>
        <div class="layout-header__actions">
          <el-tag type="info" round>{{ roleLabel }}</el-tag>
          <el-avatar :size="40" class="layout-header__avatar">{{ initials }}</el-avatar>
          <div class="layout-header__welcome">
            <span class="layout-header__name">{{ displayName }}</span>
            <span class="layout-header__greet">{{ greetMessage }}</span>
          </div>
          <el-button type="primary" plain @click="handleLogout">安全退出</el-button>
        </div>
      </el-header>
      <el-main class="layout-main">
        <div class="layout-main__inner">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';

interface MenuItem {
  label: string;
  path: string;
  roles: string[];
}

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 移动端侧边栏状态
const isMobileSidebarOpen = ref(false);
const isMobile = ref(false);

// 检测屏幕尺寸
const checkMobile = () => {
  isMobile.value = window.innerWidth < 1024;
  // 桌面端时自动关闭移动侧边栏
  if (!isMobile.value) {
    isMobileSidebarOpen.value = false;
  }
};

// 切换移动端侧边栏
const toggleMobileSidebar = () => {
  isMobileSidebarOpen.value = !isMobileSidebarOpen.value;
};

// 关闭移动端侧边栏
const closeMobileSidebar = () => {
  isMobileSidebarOpen.value = false;
};

// 菜单选择时自动关闭移动端侧边栏
const handleMenuSelect = () => {
  if (isMobile.value) {
    closeMobileSidebar();
  }
};

onMounted(() => {
  checkMobile();
  window.addEventListener('resize', checkMobile);
});

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile);
});

const menuItems = computed<MenuItem[]>(() => [
  { label: '首页', path: '/dashboard', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { label: '题库列表', path: '/problems', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { label: '赛事列表', path: '/contests', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { label: '训练计划', path: '/training', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
  { label: '用户管理', path: '/admin/users', roles: ['ADMIN'] },
  { label: '我的资料', path: '/profile', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
]);

const filteredMenus = computed(() => {
  const currentRole = (userStore.role || '').toUpperCase();
  return menuItems.value.filter((item) => item.roles.includes(currentRole));
});

const activeMenu = computed(() => route.path);

const displayName = computed(() => {
  return userStore.nickname || userStore.email || '访客';
});

const initials = computed(() => {
  const name = displayName.value;
  if (!name) return '访客';
  return name
    .split(' ')
    .map((part) => part.charAt(0))
    .join('')
    .slice(0, 2)
    .toUpperCase();
});

const roleLabel = computed(() => {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    STUDENT: '学生',
  };
  const roleKey = (userStore.role || '').toUpperCase();
  return map[roleKey] || '访客';
});

const currentTitle = computed(() => {
  return (route.meta?.title as string) || '仪表盘';
});

const headerSubtitle = computed(() => {
  switch ((userStore.role || '').toUpperCase()) {
    case 'ADMIN':
      return '统筹赛事与团队，让协作更顺畅';
    case 'TEACHER':
      return '策划训练与赛事，陪伴学生进步';
    case 'JUDGE':
      return '保持专注，评审每一次精彩表现';
    case 'STUDENT':
      return '持续训练，与优秀的自己相遇';
    default:
      return '欢迎来到竞赛集训系统';
  }
});

const greetMessage = computed(() => {
  const hour = new Date().getHours();
  if (hour < 12) return '早上好';
  if (hour < 18) return '下午好';
  return '晚上好';
});

const handleLogout = () => {
  userStore.logout();
  router.push({ name: 'Login' });
};
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background: transparent;
}

.layout-aside {
  /* width: 260px; 宽度由 .sidebar-fixed 的 --sidebar-width 控制 */
  background: var(--bg-glass-sidebar);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  padding: 24px 20px 28px;
  border-right: 1px solid var(--border-subtle);
  /* position: relative; 由 .sidebar-fixed 处理定位 */
}

.layout-aside::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(99, 102, 241, 0.03) 0%, transparent 100%);
  pointer-events: none;
}

.layout-aside__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.layout-aside__brand {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.08em;
  color: var(--text-primary);
}

.layout-aside__role {
  background: var(--accent-subtle);
  border: 1px solid var(--accent-primary);
  color: var(--accent-primary);
}

.layout-aside__scroll {
  flex: 1;
}

.layout-aside__footer {
  margin-top: 18px;
  font-size: 12px;
  color: var(--text-tertiary);
  text-align: center;
}

.layout-menu {
  border-right: none;
  background-color: transparent;
  --el-menu-hover-bg-color: var(--bg-hover);
}

.menu-item {
  border-radius: var(--radius-md);
  margin: 4px 0;
  font-size: 14px;
  color: var(--text-secondary);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: linear-gradient(180deg, var(--accent-primary) 0%, var(--accent-emphasis) 100%);
  border-radius: 0 2px 2px 0;
  transition: height var(--transition-normal);
}

.menu-item.is-active,
.menu-item:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
  transform: translateX(2px);
}

.menu-item:hover::before {
  height: 60%;
}

.menu-item.is-active {
  background: var(--accent-subtle);
  color: var(--accent-primary);
  box-shadow: 0 0 12px rgba(99, 102, 241, 0.15);
}

.menu-item.is-active::before {
  height: 70%;
}

.layout-content {
  background: transparent;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 40px 18px;
  background: var(--bg-glass-header);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  border-bottom: 1px solid var(--border-muted);
  flex-wrap: wrap;
  gap: 24px;
  /* position: sticky; top: 0; z-index: 100; 移除粘性定位 */
  transition: all var(--transition-normal);
}

/* Override element-plus's default header height with a more specific selector */
.layout-container .layout-header {
  height: auto;
}

.layout-header__info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 220px;
  flex: 1 1 280px;
}

.layout-header__title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.layout-header__subtitle {
  margin: 0;
  color: var(--text-secondary);
  font-size: 14px;
  max-width: 520px;
}

.layout-header__actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  justify-content: flex-end;
  min-width: 260px;
  flex: 1 1 280px;
}

.layout-header__actions :deep(.el-tag.el-tag--info) {
  background: var(--accent-subtle);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

.layout-header__actions :deep(.el-button.is-plain) {
  color: var(--accent-primary);
  border-color: var(--border-default);
  background: var(--bg-canvas-inset);
}

.layout-header__actions :deep(.el-button.is-plain:hover) {
  background: var(--accent-subtle);
  border-color: var(--accent-primary);
  color: var(--accent-emphasis);
}

.layout-header__avatar {
  background: var(--accent-primary);
  font-weight: 600;
  color: #ffffff;
}

.layout-header__welcome {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 120px;
}

.layout-header__name {
  font-weight: 600;
  color: var(--text-primary);
}

.layout-header__greet {
  font-size: 12px;
  color: var(--text-secondary);
}

.layout-main {
  padding: 16px 40px 52px;
  width: 100%;
  background: var(--bg-canvas);
}

.layout-main__inner {
  max-width: 1240px;
  margin: 0 auto;
  width: 100%;
}

/* ========================================
   移动端响应式支持
   ======================================== */

/* 遮罩层 */
.sidebar-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 999;
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
}

/* 遮罩层淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 汉堡菜单按钮 - 默认隐藏 */
.mobile-menu-trigger {
  display: none;
  color: var(--text-primary);
  padding: 8px;
  margin-right: 12px;
  border-radius: 8px;
}

.mobile-menu-trigger:hover {
  background: var(--bg-hover);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.28s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

/* 平板和移动端响应式 (1024px 以下) */
@media (max-width: 1023px) {
  /* 显示汉堡菜单按钮 */
  .mobile-menu-trigger {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  /* 侧边栏默认隐藏在左侧 */
  .sidebar-fixed {
    transform: translateX(-100%);
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    z-index: 1000;
  }

  /* 侧边栏打开状态 */
  .sidebar-fixed.is-mobile-open {
    transform: translateX(0);
    box-shadow: 4px 0 24px rgba(0, 0, 0, 0.5);
  }

  /* 主内容区不留边距 */
  .main-content {
    margin-left: 0 !important;
  }

  /* 头部布局调整 */
  .layout-header {
    padding: 16px 20px;
  }

  .layout-header__title {
    font-size: 22px;
  }

  .layout-header__subtitle {
    font-size: 13px;
  }

  /* 主内容区减少内边距 */
  .layout-main {
    padding: 12px 20px 32px;
  }
}

/* 小屏幕手机 (640px 以下) */
@media (max-width: 640px) {
  .layout-header {
    padding: 12px 16px;
    gap: 16px;
  }

  .layout-header__actions {
    gap: 8px;
    flex-wrap: wrap;
  }

  .layout-header__welcome {
    display: none;  /* 隐藏欢迎语 */
  }

  .layout-header__actions .el-button {
    font-size: 13px;
    padding: 8px 12px;
  }

  .layout-header__title {
    font-size: 20px;
  }

  .layout-main {
    padding: 12px 16px 28px;
  }
}

/* 超小屏幕 (480px 以下) */
@media (max-width: 480px) {
  .layout-header {
    padding: 12px 12px;
  }

  .layout-main {
    padding: 8px 12px 24px;
  }

  .layout-aside__brand {
    font-size: 16px;
  }

  .sidebar-fixed {
    width: 260px;  /* 移动端侧边栏稍窄 */
  }

  .layout-header__actions .el-avatar {
    width: 32px;
    height: 32px;
  }
}

@media (max-width: 1200px) {
  .layout-main__inner {
    max-width: 100%;
  }
}
</style>
