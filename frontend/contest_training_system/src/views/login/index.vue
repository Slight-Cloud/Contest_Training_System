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
  gap: 80px;
  padding: 40px 60px;
  position: relative;
  overflow: hidden;
  
  /* 深色渐变背景 + 微妙网格效果 */
  background: linear-gradient(135deg, #0a0e16 0%, #161b28 50%, #0d1117 100%);
  color: var(--text-primary);
}

/* 动态背景光晕 - 顶部右侧蓝色光晕 */
.login-page::before {
  content: '';
  position: absolute;
  top: -20%;
  right: -10%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(74, 136, 218, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  animation: pulse 8s ease-in-out infinite;
}

/* 底部左侧紫色光晕 */
.login-page::after {
  content: '';
  position: absolute;
  bottom: -15%;
  left: -8%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(138, 99, 210, 0.12) 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(70px);
  pointer-events: none;
  animation: pulse 10s ease-in-out infinite reverse;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.8;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
}

.login-hero {
  max-width: 480px;
  position: relative;
  z-index: 1;
}

.login-hero h1 {
  margin: 0 0 24px;
  font-size: 56px;
  font-weight: 800;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, #e6edf3 0%, #b1bac4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.1;
}

.login-hero p {
  margin: 0;
  line-height: 1.8;
  color: var(--text-secondary);
  font-size: 17px;
  font-weight: 400;
  letter-spacing: 0.01em;
}

/* 玻璃拟态卡片 */
.login-card {
  width: 420px;
  position: relative;
  z-index: 1;
  
  /* Glassmorphism 效果 */
  background: rgba(33, 38, 45, 0.6);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  
  /* 增强阴影层次 */
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  
  color: var(--text-primary);
  padding: 40px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 12px 48px rgba(0, 0, 0, 0.5),
    0 4px 12px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

/* 卡片顶部光晕装饰 */
.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent-primary), transparent);
  opacity: 0.6;
}

.login-title {
  margin-bottom: 32px;
  text-align: center;
  font-weight: 700;
  font-size: 24px;
  letter-spacing: -0.01em;
  color: var(--text-primary);
}

.login-actions {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.login-actions :deep(.el-button--primary) {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
  
  background: linear-gradient(135deg, var(--accent-primary) 0%, var(--accent-secondary) 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(74, 136, 218, 0.3);
  
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-actions :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(74, 136, 218, 0.4);
  background: linear-gradient(135deg, var(--accent-emphasis) 0%, var(--accent-primary) 100%);
}

.login-actions :deep(.el-button--primary:active) {
  transform: translateY(0);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
}

.register-link :deep(.el-link) {
  font-weight: 600;
  margin-left: 4px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.01em;
  margin-bottom: 8px;
}

/* 输入框增强效果 */
:deep(.el-input__wrapper) {
  height: 44px;
  background: rgba(22, 27, 34, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.15);
  background: rgba(22, 27, 34, 0.8);
}

/* 聚焦时的光晕效果 */
:deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent-primary);
  background: rgba(22, 27, 34, 0.9);
  box-shadow: 
    0 0 0 3px rgba(74, 136, 218, 0.15),
    0 2px 8px rgba(74, 136, 218, 0.2);
}

:deep(.el-input__inner) {
  color: var(--text-primary);
  background: transparent;
  font-size: 15px;
}

:deep(.el-input__inner::placeholder) {
  color: var(--text-placeholder);
  font-weight: 400;
}

/* 密码显示图标样式优化 */
:deep(.el-input__suffix) {
  color: var(--text-tertiary);
}

:deep(.el-input__suffix:hover) {
  color: var(--text-secondary);
}

@media (max-width: 992px) {
  .login-page {
    flex-direction: column;
    gap: 48px;
    padding: 48px 32px;
    text-align: center;
  }

  .login-page::before,
  .login-page::after {
    display: none;
  }

  .login-hero {
    max-width: 520px;
  }
  
  .login-hero h1 {
    font-size: 42px;
  }
  
  .login-card {
    width: 100%;
    max-width: 420px;
    padding: 32px 28px;
  }
}
</style>
