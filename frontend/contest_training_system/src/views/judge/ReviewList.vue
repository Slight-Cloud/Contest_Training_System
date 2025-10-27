<template>
  <div class="judge-review-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评审任务</span>
          <el-button type="primary" text @click="fetchReviews">刷新</el-button>
        </div>
      </template>
      <el-table :data="reviewList" v-loading="loading" class="detail-table">
        <el-table-column prop="submission_id" label="提交ID" width="120" />
        <el-table-column prop="contest_title" label="赛事" min-width="160" />
        <el-table-column prop="team_name" label="参赛队伍" min-width="140" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewSubmission(row.submission_id)">查看作品</el-button>
            <el-button type="success" link @click="reviewSubmission(row.submission_id)">评审</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

interface ReviewItem {
  submission_id: number;
  contest_title?: string;
  team_name?: string;
  status?: string;
  [key: string]: unknown;
}

const reviewList = ref<ReviewItem[]>([]);
const loading = ref(false);

const viewSubmission = (submissionId: number) => {
  console.log('查看提交', submissionId);
};

const reviewSubmission = (submissionId: number) => {
  console.log('评审提交', submissionId);
};

const fetchReviews = async () => {
  loading.value = true;
  try {
    const res = await request({
      url: '/submission/list',
      method: 'get',
      params: { page: 1, page_size: 20 },
    });
    const data = res.data || {};
    reviewList.value = Array.isArray(data.list) ? data.list : data.items || [];
  } catch (error) {
    console.error(error);
    ElMessage.error('获取评审任务失败');
  } finally {
    loading.value = false;
  }
};

onMounted(fetchReviews);
</script>

<style scoped>
.judge-review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
