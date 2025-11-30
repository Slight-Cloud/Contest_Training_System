<template>
  <div class="login-page">
    <div class="login-hero">
      <h1>Contest Hub</h1>
      <p>欢迎使用竞赛系统</p>
    </div>
    <el-card class="login-card" shadow="always">
      <h2 class="login-title">账号登录</h2>
      <el-form :model="loginForm" @submit.prevent="handleLogin" label-position="top">
        <el-form-item label="账号">
          <el-input v-model="loginForm.emailOrPhone" placeholder="请输入邮箱或手机号" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item class="login-actions">
          <el-button type="primary" :loading="submitting" @click="handleLogin" round>立即登录</el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？<el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const loginForm = ref({
  emailOrPhone: '',
  password: '',
});

const submitting = ref(false);

const handleLogin = async () => {
  if (!loginForm.value.emailOrPhone || !loginForm.value.password) {
    ElMessage.warning('请输入账号和密码');
    return;
  }
  try {
    submitting.value = true;
    await userStore.userLogin({ ...loginForm.value });
    await userStore.fetchUserProfile();
    
    // 检查是否有重定向地址
    const redirect = route.query.redirect as string;
    if (redirect && redirect !== '/login') {
      router.push(redirect);
    } else {
      router.push('/');
    }
  } catch (error: any) {
    ElMessage.error(error?.message || '登录失败');
  } finally {
    submitting.value = false;
  }
};

const goToRegister = () => {
  router.push({ name: 'Register' });
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 56px;
  padding: 40px 60px;
  background: var(--bg-canvas);
  color: var(--text-primary);
}

.login-hero {
  max-width: 420px;
}

.login-hero h1 {
  margin: 0 0 16px;
  font-size: 42px;
  font-weight: 700;
  color: var(--text-primary);
}

.login-hero p {
  margin: 0;
  line-height: 1.7;
  color: var(--text-secondary);
  font-size: 16px;
}

.login-card {
  width: 380px;
  background: var(--bg-surface);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-default);
  box-shadow: var(--shadow-lg);
  color: var(--text-primary);
}

.login-title {
  margin-bottom: 20px;
  text-align: center;
  font-weight: 600;
  color: var(--text-primary);
}

.login-actions {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}

.login-actions :deep(.el-button--primary) {
  background: var(--accent-primary);
  border-color: var(--accent-primary);
  padding: 0 32px;
}

.login-actions :deep(.el-button--primary:hover) {
  background: var(--accent-secondary);
  border-color: var(--accent-secondary);
}

.register-link {
  text-align: center;
  margin-top: 16px;
  color: var(--text-secondary);
  font-size: 14px;
}

:deep(.el-form-item__label) {
  color: var(--text-secondary);
}

:deep(.el-input__wrapper) {
  background: var(--bg-canvas-inset);
  border-color: var(--border-default);
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--border-emphasis);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 2px var(--accent-subtle);
}

:deep(.el-input__inner) {
  color: var(--text-primary);
  background: transparent;
}

:deep(.el-input__inner::placeholder) {
  color: var(--text-placeholder);
}

@media (max-width: 992px) {
  .login-page {
    flex-direction: column;
    padding: 48px 32px;
    text-align: center;
  }

  .login-hero {
    max-width: 520px;
  }
}
</style>
