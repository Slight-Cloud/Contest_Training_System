<template>
  <div class="submission-list-page page-shell">
    <div class="page-header">
      <h2 class="page-header__title">提交记录</h2>
      <p class="page-header__subtitle">查看所有提交记录和评测结果</p>
    </div>

    <el-card class="page-card">
      <el-form :inline="true" :model="filters" label-suffix=":" class="filter-form">
        <el-form-item label="赛事ID">
          <el-input v-model="filters.contestId" placeholder="赛事ID" clearable @keyup.enter="handleSearch" style="width: 120px;" />
        </el-form-item>
        <el-form-item label="题目ID">
          <el-input v-model="filters.problemId" placeholder="题目ID" clearable @keyup.enter="handleSearch" style="width: 120px;" />
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="filters.result" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="通过(AC)" value="AC" />
            <el-option label="答案错误(WA)" value="WA" />
            <el-option label="时间超限(TLE)" value="TLE" />
            <el-option label="内存超限(MLE)" value="MLE" />
            <el-option label="运行错误(RE)" value="RE" />
            <el-option label="编译错误(CE)" value="CE" />
          </el-select>
        </el-form-item>
        <el-form-item label="语言">
          <el-select v-model="filters.language" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="C++" value="C++" />
            <el-option label="Java" value="Java" />
            <el-option label="Python" value="Python" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="filters.sortBy" placeholder="默认排序" clearable @change="handleSearch" style="width: 140px;">
            <el-option label="最新提交" value="id_desc" />
            <el-option label="最早提交" value="id_asc" />
            <el-option label="创建时间降序" value="created_desc" />
            <el-option label="创建时间升序" value="created_asc" />
            <el-option label="用时最少" value="time_used_asc" />
            <el-option label="用时最多" value="time_used_desc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="submissionList" v-loading="loading" border :header-cell-style="headerStyle" class="submission-table">
        <el-table-column prop="submissionId" label="ID" width="80" align="center" />
        <el-table-column prop="userId" label="用户ID" width="100" align="center" />
        <el-table-column prop="problemId" label="题目ID" width="100" align="center" />
        <el-table-column prop="contestId" label="赛事ID" width="100" align="center" />
        <el-table-column prop="result" label="结果" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getResultTagType(row.result)" size="small">
              {{ row.result || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="language" label="语言" width="120" align="center" />
        <el-table-column label="资源使用" width="180" align="center">
          <template #default="{ row }">
            <div class="resource-column">
              <span>时间: {{ row.timeUsed || 0 }}ms</span>
              <span>内存: {{ row.memoryUsed || 0 }}MB</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" text type="primary" @click="goToDetail(row.submissionId)">
              查看详情
            </el-button>
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
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getSubmissionList } from '@/api/submission';

interface SubmissionItem {
  submissionId: number;
  userId: number;
  problemId: number;
  contestId: number;
  result: string;
  language: string;
  timeUsed: number;
  memoryUsed: number;
  createdAt: string;
  [key: string]: unknown;
}

const router = useRouter();

const submissionList = ref<SubmissionItem[]>([]);
const loading = ref(false);

const filters = reactive({
  contestId: '',
  problemId: '',
  result: '',
  language: '',
  sortBy: 'id_desc' as string, // 默认按ID降序（最新提交在前）
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
});

const headerStyle = {
  background: 'rgba(13, 17, 23, 0.65)',
  color: '#cdd9e5',
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params: any = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      contestId: filters.contestId ? Number(filters.contestId) : undefined,
      problemId: filters.problemId ? Number(filters.problemId) : undefined,
      result: filters.result || undefined,
      language: filters.language || undefined,
      sortBy: filters.sortBy || 'id_desc',
    };
    
    const res = await getSubmissionList(params);
    const listData = res.data as any;
    
    if (listData) {
      submissionList.value = (listData.submissions || listData.list || []) as SubmissionItem[];
      pagination.total = listData.total || 0;
      pagination.page = listData.page || params.page || 1;
      pagination.pageSize = listData.pageSize || params.pageSize || 10;
    }
  } catch (error: any) {
    console.error('获取提交记录错误:', error);
    ElMessage.error(`获取提交记录失败: ${error.message || '未知错误'}`);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchData();
};

const resetFilters = () => {
  filters.contestId = '';
  filters.problemId = '';
  filters.result = '';
  filters.language = '';
  filters.sortBy = 'id_desc';
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

const getResultTagType = (result: string) => {
  const typeMap: Record<string, string> = {
    'AC': 'success',
    'WA': 'danger',
    'TLE': 'warning',
    'MLE': 'warning',
    'RE': 'danger',
    'CE': 'info',
  };
  return typeMap[result] || 'info';
};

const goToDetail = (submissionId: number) => {
  router.push({ name: 'SubmissionDetail', params: { id: submissionId } });
};

onMounted(fetchData);
</script>

<style scoped>
.submission-list-page {
  gap: 24px;
}

.filter-form {
  margin-bottom: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.submission-table :deep(.el-table__row) {
  transition: background 0.2s ease;
}

.submission-table :deep(.el-table__row:hover) {
  background: rgba(47, 129, 247, 0.08);
}

.resource-column {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #9fb2c6;
  font-size: 13px;
}

.table-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .filter-form {
    display: grid;
    grid-template-columns: repeat(1, minmax(0, 1fr));
    gap: 12px;
  }

  .filter-form :deep(.el-form-item) {
    margin-right: 0 !important;
  }
}
</style>
