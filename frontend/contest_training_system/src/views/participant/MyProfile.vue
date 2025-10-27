<template>
  <div class="participant-profile page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">我的资料</h2>
        <p class="page-header__subtitle">管理您的个人信息</p>
      </div>
      <div class="header-actions">
        <el-button v-if="!isEditing" type="primary" @click="startEdit">编辑资料</el-button>
        <el-button v-if="isEditing" type="success" :loading="submitting" @click="saveProfile">保存</el-button>
        <el-button v-if="isEditing" @click="cancelEdit">取消</el-button>
        <el-button type="info" plain @click="fetchProfile">刷新</el-button>
      </div>
    </div>

    <el-card class="page-card" v-loading="loading">
      <template v-if="!isEditing">
        <el-descriptions :column="2" border size="default">
          <el-descriptions-item label="用户ID">{{ profile.userId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ profile.studentId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ profile.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ profile.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ profile.phoneNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag type="info">{{ roleLabel }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="账户状态">
            <el-tag :type="profile.isActive !== null && profile.isActive !== undefined ? (profile.isActive ? 'success' : 'danger') : 'info'">
              {{ profile.isActive !== null && profile.isActive !== undefined ? (profile.isActive ? '激活' : '未激活') : '未知' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDate(profile.createdAt) || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>

      <template v-else>
        <el-form 
          ref="profileFormRef" 
          :model="editForm" 
          :rules="profileRules" 
          label-width="100px"
        >
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phoneNumber">
                <el-input v-model="editForm.phoneNumber" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="学号">
                <el-input :value="profile.studentId" disabled placeholder="学号不可修改" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input :value="profile.email" disabled placeholder="邮箱不可修改" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="修改密码">
            <el-input 
              v-model="editForm.password" 
              type="password" 
              placeholder="如需修改密码请输入新密码，否则留空"
              show-password
            />
          </el-form-item>
        </el-form>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { getUserProfile, updateUserInfo } from '@/api/user';
import { useUserStore } from '@/store/user';

interface UserProfile {
  userId?: number;
  email?: string;
  phoneNumber?: string;
  nickname?: string;
  studentId?: string;
  role?: string;
  createdAt?: string;
  isActive?: number | boolean | null;
}

const userStore = useUserStore();

const loading = ref(false);
const submitting = ref(false);
const isEditing = ref(false);

const profile = ref<UserProfile>({});
const profileFormRef = ref<FormInstance>();

const editForm = reactive({
  nickname: '',
  phoneNumber: '',
  password: '',
});

const roleLabel = computed(() => {
  const roleMap: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    JUDGE: '评委',
    STUDENT: '学生',
  };
  return roleMap[profile.value.role?.toUpperCase() || ''] || '未知';
});

const profileRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度应在2-20字符之间', trigger: 'blur' }
  ],
  phoneNumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
};

const fetchProfile = async () => {
  loading.value = true;
  try {
    const res = await getUserProfile();
    profile.value = (res.data as any) || {};
  } catch (error) {
    console.error(error);
    ElMessage.error('获取用户信息失败');
  } finally {
    loading.value = false;
  }
};

const startEdit = () => {
  isEditing.value = true;
  editForm.nickname = profile.value.nickname || '';
  editForm.phoneNumber = profile.value.phoneNumber || '';
  editForm.password = '';
};

const cancelEdit = () => {
  isEditing.value = false;
  editForm.nickname = '';
  editForm.phoneNumber = '';
  editForm.password = '';
};

const saveProfile = async () => {
  if (!profileFormRef.value) return;
  
  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    submitting.value = true;
    try {
      const updateData: any = {
        nickname: editForm.nickname,
        phoneNumber: editForm.phoneNumber,
      };
      
      // 只有输入了密码才更新密码
      if (editForm.password && editForm.password.trim()) {
        updateData.password = editForm.password;
      }
      
      await updateUserInfo(updateData);
      ElMessage.success('资料更新成功');
      
      // 重新获取用户信息
      await fetchProfile();
      await userStore.fetchUserProfile();
      
      isEditing.value = false;
    } catch (error) {
      console.error(error);
    } finally {
      submitting.value = false;
    }
  });
};

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '';
  return dateStr.replace('T', ' ').replace(/\.\d{3}Z?$/, '');
};

onMounted(fetchProfile);
</script>

<style scoped>
.participant-profile {
  gap: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 移除自定义样式，使用全局统一样式 */

:deep(.el-input__wrapper) {
  background: var(--bg-canvas-inset);
  border-color: var(--border-default);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 1px var(--accent-primary);
}

:deep(.el-input__inner) {
  color: #ffffff;
  background: transparent;
}

:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

@media (max-width: 768px) {
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .participant-profile :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>