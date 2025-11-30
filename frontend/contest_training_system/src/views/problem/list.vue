<template>
  <div class="problem-list-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">{{ canManage ? '题库管理' : '题库列表' }}</h2>
        <p class="page-header__subtitle">{{ canManage ? '管理题目库，创建和编辑题目，为赛事准备题目资源' : '浏览题目库，查看题目详情和练习' }}</p>
      </div>
      <div class="header-actions">
        <el-button v-if="canManage" type="primary" @click="goToCreate">新建题目</el-button>
        <el-button type="info" plain @click="fetchData">刷新列表</el-button>
      </div>
    </div>

    <el-card class="page-card">
      <el-form :inline="true" :model="filters" label-suffix=":" class="filter-form">
        <el-form-item label="关键字">
          <el-input v-model="filters.keyword" placeholder="按题目名称搜索" clearable @keyup.enter="handleSearch" style="width: 200px;" />
        </el-form-item>
        <el-form-item v-if="canManage" label="状态">
          <el-select v-model="filters.isHidden" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="显示" :value="0" />
            <el-option label="隐藏" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="filters.sortBy" placeholder="默认排序" clearable @change="handleSearch" style="width: 140px;">
            <el-option label="最新创建" value="created_desc" />
            <el-option label="最早创建" value="created_asc" />
            <el-option label="ID升序" value="id_asc" />
            <el-option label="ID降序" value="id_desc" />
            <el-option label="标题A-Z" value="title_asc" />
            <el-option label="标题Z-A" value="title_desc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="problemList" v-loading="loading" border :header-cell-style="headerStyle" class="problem-table">
        <el-table-column prop="problemId" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="题目名称" min-width="200">
          <template #default="{ row }">
            <div class="problem-name-cell">
              <span class="problem-title">{{ row.title || '-' }}</span>
              <el-tag v-if="row.isHidden" size="small" type="info" effect="plain">已隐藏</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="creatorId" label="创建者ID" width="120" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="200">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button size="small" text type="primary" @click="goToDetail(row.problemId)">查看详情</el-button>
              <el-button
                v-if="isStudent"
                size="small"
                text
                type="success"
                @click="goToPractice(row.problemId, row.title)"
              >
                开始练习
              </el-button>
              <el-button v-if="canManage" size="small" text type="warning" @click="goToEdit(row.problemId)">
                编辑
              </el-button>
              <el-popconfirm
                v-if="canManage"
                :title="`确定${row.isHidden ? '显示' : '隐藏'}该题目？`"
                confirm-button-text="确定"
                cancel-button-text="取消"
                @confirm="handleToggleHidden(row)"
              >
                <template #reference>
                  <el-button size="small" text :type="row.isHidden ? 'success' : 'info'">
                    {{ row.isHidden ? '显示' : '隐藏' }}
                  </el-button>
                </template>
              </el-popconfirm>
              <el-popconfirm
                v-if="canManage"
                title="确定删除该题目？"
                confirm-button-text="删除"
                cancel-button-text="取消"
                confirm-button-type="danger"
                @confirm="handleDelete(row.problemId)"
              >
                <template #reference>
                  <el-button size="small" text type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </el-space>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/user';
import { getProblemList, getProblemDetail, updateProblem, deleteProblem } from '@/api/problem';
import type { Problem } from '@/types';
import { formatDateTime } from '@/utils/helpers';

interface ProblemItem {
  problemId: number;
  title: string;
  timeLimit: number;
  memoryLimit: number;
  creatorId?: number;
  isHidden: number;
  createdAt?: string;
  [key: string]: unknown;
}

const router = useRouter();
const userStore = useUserStore();

const problemList = ref<ProblemItem[]>([]);
const loading = ref(false);

const filters = reactive({
  keyword: '',
  isHidden: undefined as number | undefined,
  sortBy: 'id_asc' as string, // 默认按ID升序排序
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
});

const isAdmin = computed(() => userStore.role === 'ADMIN');
const isTeacher = computed(() => userStore.role === 'TEACHER');
const isStudent = computed(() => userStore.role === 'STUDENT');
const canManage = computed(() => isAdmin.value || isTeacher.value);

const headerStyle = {
  background: 'var(--bg-canvas-inset)',
  color: 'var(--text-secondary)',
  fontWeight: '600',
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params: any = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
      sortBy: filters.sortBy || 'id_asc', // 确保始终有值
    };
    
    // 只有管理员和教师才能传递 isHidden 参数
    // 学生用户不传递该参数,后端会自动过滤隐藏题目
    if (canManage.value) {
      params.isHidden = filters.isHidden;
    }
    
    console.log('=== 题目列表请求调试 ===');
    console.log('用户角色:', userStore.role);
    console.log('是否可管理:', canManage.value);
    console.log('请求参数:', params);
    console.log('用户Token:', userStore.token ? `${userStore.token.substring(0, 30)}...` : 'null');
    
    const res = await getProblemList(params);
    const listData = res.data as any;
    
    if (listData) {

      problemList.value = (listData.problems || listData.list || []) as any[];
      pagination.total = listData.total || 0;
      pagination.page = listData.page || params.page || 1;
      pagination.pageSize = listData.pageSize || params.pageSize || 10;
    }
  } catch (error: any) {
    console.error('=== 题目列表错误详情 ===');
    console.error('错误类型:', error.name);
    console.error('错误消息:', error.message);
    console.error('响应状态:', error.response?.status);
    console.error('响应数据:', error.response?.data);
    console.error('响应头:', error.response?.headers);
    console.error('完整错误:', error);
    
    // 针对403错误的特殊处理
    if (error.response?.status === 403) {
      // 静默处理权限问题
      console.log('题目列表访问权限不足');
    } else {
      ElMessage.error(`获取题目列表失败: ${error.message || '未知错误'}`);
    }
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchData();
};

const resetFilters = () => {
  filters.keyword = '';
  filters.isHidden = undefined;
  filters.sortBy = 'id_asc'; // 重置为默认排序
  pagination.page = 1;
  fetchData();
};

const handlePageChange = (page: number) => {
  pagination.page = page;
  fetchData();
};

const handlePageSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.page = 1;
  fetchData();
};

const goToDetail = (problemId: number) => {
  router.push({ name: 'ProblemDetail', params: { id: problemId } });
};

const goToCreate = () => {
  router.push({ name: 'ProblemCreate' });
};

const goToEdit = (problemId: number) => {
  router.push({ name: 'ProblemEdit', params: { id: problemId } });
};

const goToPractice = (problemId: number, problemTitle: string) => {
  // 练习模式，使用默认赛事ID或创建练习赛事
  router.push({ 
    name: 'ProblemSubmit', 
    params: { id: problemId },
    query: {
      contestId: 0, // 练习模式
      problemTitle: problemTitle,
      contestTitle: '练习模式'
    }
  });
};

const handleToggleHidden = async (problem: ProblemItem) => {
  try {
    // 先获取题目完整信息
    const detailRes = await getProblemDetail(problem.problemId);
    const detail = detailRes.data as unknown as Problem | undefined;
    
    if (!detail) {
      ElMessage.error('无法获取题目详情');
      return;
    }
    
    // 更新题目的隐藏状态
    await updateProblem({
      problemId: detail.problemId,
      title: detail.title,
      description: detail.description,
      inputSpec: detail.inputSpec,
      outputSpec: detail.outputSpec,
      sampleInput: detail.sampleInput,
      sampleOutput: detail.sampleOutput,
      remark: detail.remark,
      timeLimit: detail.timeLimit,
      memoryLimit: detail.memoryLimit,
      isHidden: problem.isHidden ? 0 : 1,
    });
    
    ElMessage.success(problem.isHidden ? '题目已显示' : '题目已隐藏');
    fetchData();
  } catch (error) {
    ElMessage.error('操作失败');
  }
};

const handleDelete = async (problemId: number) => {
  try {
    await deleteProblem(problemId);
    ElMessage.success('题目删除成功');
    fetchData();
  } catch (error) {
    ElMessage.error('删除失败');
  }
};

onMounted(fetchData);

const formatDate = (value?: string) => formatDateTime(value);
</script>

<style scoped>
.problem-list-page {
  gap: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 使用全局统一的 .filter-form 样式 */

/* 表格样式 - 统一暗色主题 */
.problem-table {
  --el-table-border-color: var(--border-default);
  --el-table-header-bg-color: var(--bg-canvas-inset);
  --el-table-tr-bg-color: var(--bg-surface);
  --el-table-row-hover-bg-color: var(--bg-hover);
  --el-table-text-color: var(--text-primary);
  --el-table-header-text-color: var(--text-secondary);
  background-color: var(--bg-surface);
}

.problem-table :deep(.el-table__inner-wrapper::before),
.problem-table :deep(.el-table__border-left-patch) {
  background-color: var(--border-default);
}

.problem-table :deep(td.el-table__cell),
.problem-table :deep(th.el-table__cell) {
  border-color: var(--border-default);
  padding: 14px 12px;
}

.problem-table :deep(th.el-table__cell) {
  background-color: var(--bg-canvas-inset);
  color: var(--text-secondary);
  font-weight: 600;
}

.problem-table :deep(tr.el-table__row:hover td.el-table__cell) {
  background-color: var(--bg-hover);
}

.problem-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.problem-title {
  font-weight: 600;
  color: var(--text-primary);
  word-break: break-word;
}

.limit-column {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: var(--text-secondary);
  font-size: 13px;
  white-space: nowrap;
}

.table-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .problem-table {
    overflow-x: auto;
  }
}
</style>

