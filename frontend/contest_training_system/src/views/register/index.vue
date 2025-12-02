<template>
  <div class="register-page">
    <div class="register-hero">
      <h1>Contest Hub</h1>
      <p>创建新账号，开始您的竞赛集训之旅</p>
    </div>
    <el-card class="register-card" shadow="always">
      <h2 class="register-title">账号注册</h2>
      <el-form 
        ref="registerFormRef" 
        :model="registerForm" 
        :rules="registerRules" 
        @submit.prevent="handleRegister" 
        label-position="top"
      >
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="registerForm.studentId" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phoneNumber">
          <el-input v-model="registerForm.phoneNumber" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码（至少6位）" 
            show-password 
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            show-password 
          />
        </el-form-item>
        <el-form-item class="register-actions">
          <el-button type="primary" :loading="submitting" @click="handleRegister" round>立即注册</el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { register } from '@/api/user';

const router = useRouter();

const registerFormRef = ref<FormInstance>();
const registerForm = ref({
  nickname: '',
  studentId: '',
  email: '',
  phoneNumber: '',
  password: '',
  confirmPassword: '',
});

const submitting = ref(false);

const validateEmail = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!value) {
    callback(new Error('请输入邮箱'));
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'));
  } else {
    callback();
  }
};

const validatePhone = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!value) {
    callback(new Error('请输入手机号'));
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号格式'));
  } else {
    callback();
  }
};

const validatePassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请输入密码'));
  } else if (value.length < 6) {
    callback(new Error('密码长度至少6位'));
  } else {
    callback();
  }
};

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入密码'));
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const registerRules: FormRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  email: [{ required: true, validator: validateEmail, trigger: 'blur' }],
  phoneNumber: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;
  
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    submitting.value = true;
    try {
      const { confirmPassword, ...registerData } = registerForm.value;
      await register(registerData);
      ElMessage.success('注册成功，请登录');
      router.push({ name: 'Login' });
    } catch (error: any) {
      ElMessage.error(error?.message || '注册失败');
    } finally {
      submitting.value = false;
    }
  });
};

const goToLogin = () => {
  router.push({ name: 'Login' });
};
</script>

<style scoped>
.register-page {
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
.register-page::before {
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
.register-page::after {
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

.register-hero {
  max-width: 480px;
  position: relative;
  z-index: 1;
}

.register-hero h1 {
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

.register-hero p {
  margin: 0;
  line-height: 1.8;
  color: var(--text-secondary);
  font-size: 17px;
  font-weight: 400;
  letter-spacing: 0.01em;
}

/* 玻璃拟态卡片 */
.register-card {
  width: 460px;
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
  
  /* 注册表单较长,添加滚动 */
  max-height: 85vh;
  overflow-y: auto;
}

.register-card:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 12px 48px rgba(0, 0, 0, 0.5),
    0 4px 12px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

/* 卡片顶部光晕装饰 */
.register-card::before {
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

/* 优化滚动条样式 */
.register-card::-webkit-scrollbar {
  width: 6px;
}

.register-card::-webkit-scrollbar-track {
  background: transparent;
}

.register-card::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.15);
}

.register-title {
  margin-bottom: 32px;
  text-align: center;
  font-weight: 700;
  font-size: 24px;
  letter-spacing: -0.01em;
  color: var(--text-primary);
}

.register-actions {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.register-actions :deep(.el-button--primary) {
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

.register-actions :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(74, 136, 218, 0.4);
  background: linear-gradient(135deg, var(--accent-emphasis) 0%, var(--accent-primary) 100%);
}

.register-actions :deep(.el-button--primary:active) {
  transform: translateY(0);
}

.login-link {
  text-align: center;
  margin-top: 24px;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
}

.login-link :deep(.el-link) {
  font-weight: 600;
  margin-left: 4px;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
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

/* 表单验证错误信息样式 */
:deep(.el-form-item__error) {
  font-size: 12px;
  padding-top: 4px;
}

@media (max-width: 992px) {
  .register-page {
    flex-direction: column;
    gap: 48px;
    padding: 48px 32px;
    text-align: center;
  }

  .register-page::before,
  .register-page::after {
    display: none;
  }

  .register-hero {
    max-width: 520px;
  }
  
  .register-hero h1 {
    font-size: 42px;
  }

  .register-card {
    width: 100%;
    max-width: 460px;
    padding: 32px 28px;
    max-height: none;
  }
}
</style>

