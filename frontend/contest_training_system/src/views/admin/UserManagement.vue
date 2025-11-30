<template>
  <div class="admin-user-management page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">用户管理</h2>
        <p class="page-header__subtitle">查看系统用户、筛选角色及状态，支持快速查看明细。</p>
      </div>
      <el-button type="info" plain @click="fetchUsers">刷新</el-button>
    </div>

    <el-card class="page-card">
      <el-form :inline="true" :model="filters" label-suffix=":" class="filter-form">
        <el-form-item label="关键字">
          <el-input v-model="filters.keyword" placeholder="昵称 / 邮箱 / 学号" clearable @keyup.enter="handleSearch" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="filters.role" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="评委" value="JUDGE" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.isActive" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="filters.sortBy" placeholder="默认排序" clearable @change="handleSearch" style="width: 140px;">
            <el-option label="最新注册" value="created_desc" />
            <el-option label="最早注册" value="created_asc" />
            <el-option label="ID升序" value="id_asc" />
            <el-option label="ID降序" value="id_desc" />
            <el-option label="昵称A-Z" value="nickname_asc" />
            <el-option label="昵称Z-A" value="nickname_desc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="users" v-loading="loading" border :header-cell-style="headerStyle" class="user-table">
        <el-table-column prop="userId" label="ID" width="80" align="center" />
        <el-table-column label="用户信息" min-width="220">
          <template #default="{ row }">
            <div class="user-info">
              <span class="user-name">{{ row.nickname || '未设置昵称' }}</span>
              <span class="user-email">
                {{ row.email || '未绑定邮箱' }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="studentId" label="学号" min-width="140" />
        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">{{ row.isActive ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" min-width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :current-page="pagination.page"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 30, 50]"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailDrawer.visible" title="用户详情" size="420px" class="user-detail-drawer">
      <el-descriptions column="1" :border="true" size="small">
        <el-descriptions-item label="用户 ID">{{ detailDrawer.user?.userId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detailDrawer.user?.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailDrawer.user?.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailDrawer.user?.phoneNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleLabel(detailDrawer.user?.role) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailDrawer.user?.isActive ? '启用' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ detailDrawer.user?.createdAt || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

interface UserItem {
  userId: number;
  nickname?: string;
  email?: string;
  phoneNumber?: string;
  studentId?: string;
  role?: string;
  isActive?: number;
  createdAt?: string;
  [key: string]: unknown;
}

const filters = reactive({
  keyword: '',
  role: '' as string | undefined,
  isActive: undefined as number | undefined,
  sortBy: 'id_asc' as string, // 默认按ID升序排序
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
});

const users = ref<UserItem[]>([]);
const loading = ref(false);

const headerStyle = {
  background: 'var(--bg-canvas-inset)',
  color: 'var(--text-secondary)',
  fontWeight: '600',
};

const fetchUsers = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
      role: filters.role || undefined,
      isActive: filters.isActive ?? undefined,
      sortBy: filters.sortBy || 'id_asc',
    };
    const res = await request({
      url: '/admin/users',
      method: 'get',
      params,
    });
    const data = res.data || {};
    const list = data.users || data.list || data.items || [];
    users.value = Array.isArray(list) ? list : [];
    pagination.total = Number(data.total) || 0;
    pagination.page = Number(data.page) || params.page || 1;
    pagination.pageSize = Number(data.pageSize) || params.pageSize || 10;
  } catch (error) {
    console.error(error);
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchUsers();
};

const resetFilters = () => {
  filters.keyword = '';
  filters.role = undefined;
  filters.isActive = undefined;
  filters.sortBy = 'id_asc';
  handleSearch();
};

const handlePageChange = (page: number) => {
  pagination.page = page;
  fetchUsers();
};

const handlePageSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.page = 1;
  fetchUsers();
};

const roleLabel = (role?: string) => {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    STUDENT: '学生',
  };
  return map[(role || '').toUpperCase()] || '未知';
};

const detailDrawer = reactive({
  visible: false,
  user: null as UserItem | null,
});

const openDetail = (user: UserItem) => {
  detailDrawer.user = user;
  detailDrawer.visible = true;
};

onMounted(fetchUsers);
</script>

<style scoped>
.admin-user-management {
  gap: 24px;
}

/* 使用全局统一的 .filter-form 样式 */

/* 表格样式 - 统一暗色主题 */
.user-table {
  --el-table-border-color: var(--border-default);
  --el-table-header-bg-color: var(--bg-canvas-inset);
  --el-table-tr-bg-color: var(--bg-surface);
  --el-table-row-hover-bg-color: var(--bg-hover);
  --el-table-text-color: var(--text-primary);
  --el-table-header-text-color: var(--text-secondary);
  background-color: var(--bg-surface);
}

.user-table :deep(td.el-table__cell),
.user-table :deep(th.el-table__cell) {
  border-color: var(--border-default);
  padding: 14px 12px;
}

.user-table :deep(th.el-table__cell) {
  background-color: var(--bg-canvas-inset);
  color: var(--text-secondary);
  font-weight: 600;
}

.user-table :deep(tr.el-table__row:hover td.el-table__cell) {
  background-color: var(--bg-hover);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.user-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 15px;
  line-height: 1.4;
}

.user-email {
  font-weight: 500;
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.3;
}

.table-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 标签样式优化 */
.user-table :deep(.el-table__cell .el-tag) {
  font-weight: 600;
  font-size: 12px;
}

/* 用户详情抽屉样式优化 */
.user-detail-drawer :deep(.el-descriptions-item__label) {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 14px;
}

.user-detail-drawer :deep(.el-descriptions-item__content) {
  color: var(--text-primary);
  font-weight: 500;
  font-size: 14px;
}
</style>
