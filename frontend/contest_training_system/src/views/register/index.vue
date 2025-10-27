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
  gap: 56px;
  padding: 40px 60px;
  background: var(--bg-canvas);
  color: #ffffff;
}

.register-hero {
  max-width: 420px;
}

.register-hero h1 {
  margin: 0 0 16px;
  font-size: 42px;
  font-weight: 700;
  color: #ffffff;
}

.register-hero p {
  margin: 0;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
}

.register-card {
  width: 420px;
  background: var(--bg-surface);
  border-radius: 12px;
  border: 1px solid var(--border-default);
  box-shadow: 0 8px 24px rgba(1, 4, 9, 0.15);
  color: #ffffff;
}

.register-title {
  margin-bottom: 20px;
  text-align: center;
  font-weight: 600;
  color: #ffffff;
}

.register-actions {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}

.register-actions :deep(.el-button--primary) {
  background: var(--accent-primary);
  border-color: var(--accent-primary);
  padding: 0 32px;
}

.register-actions :deep(.el-button--primary:hover) {
  background: var(--accent-emphasis);
  border-color: var(--accent-emphasis);
}

.login-link {
  text-align: center;
  margin-top: 16px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-input__wrapper) {
  background: var(--bg-canvas-inset);
  border-color: var(--border-default);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 1px var(--accent-primary);
}

@media (max-width: 992px) {
  .register-page {
    flex-direction: column;
    padding: 48px 32px;
    text-align: center;
  }

  .register-hero {
    max-width: 520px;
  }

  .register-card {
    width: 100%;
    max-width: 420px;
  }
}
</style>

