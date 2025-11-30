<template>
  <el-container class="layout-container">
    <el-aside class="layout-aside">
      <div class="layout-aside__header">
        <div class="layout-aside__brand">Contest Hub</div>
        <el-tag size="small" class="layout-aside__role" effect="dark">{{ roleLabel }}</el-tag>
      </div>
      <el-scrollbar class="layout-aside__scroll">
        <el-menu :default-active="activeMenu" router class="layout-menu" :collapse="false">
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

    <el-container class="layout-content">
      <el-header class="layout-header">
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
import { computed } from 'vue';
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
  width: 260px;
  background: var(--bg-canvas-inset);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  padding: 24px 20px 28px;
  border-right: 1px solid var(--border-default);
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
  transition: all var(--transition-fast);
}

.menu-item.is-active,
.menu-item:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.menu-item.is-active {
  background: var(--accent-subtle);
  color: var(--accent-primary);
}

.layout-content {
  background: transparent;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 40px 18px;
  background: var(--bg-canvas);
  border-bottom: 1px solid var(--border-muted);
  flex-wrap: wrap;
  gap: 24px;
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

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.28s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

@media (max-width: 1200px) {
  .layout-container {
    flex-direction: column;
  }

  .layout-aside {
    width: 100%;
    flex-direction: column;
    padding: 20px 24px;
  }

  .layout-content {
    width: 100%;
  }

  .layout-header {
    padding: 24px 24px 16px;
  }

  .layout-main {
    padding: 16px 24px 40px;
  }

  .layout-main__inner {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .layout-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 18px;
  }

  .layout-header__actions {
    width: 100%;
    justify-content: space-between;
  }

  .layout-header__welcome {
    min-width: auto;
  }

  .layout-header__subtitle {
    max-width: 100%;
  }

  .layout-main {
    padding: 16px 20px 36px;
  }
}
</style>
