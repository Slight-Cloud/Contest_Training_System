<template>
  <div class="training-detail-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">{{ planDetail.title || '训练计划详情' }}</h2>
        <p class="page-header__subtitle">{{ planDetail.description || '查看训练计划的详细信息和进度' }}</p>
      </div>
      <div class="header-actions">
        <el-button text type="primary" @click="goBack">返回列表</el-button>
      </div>
    </div>

    <div v-loading="loading">
      <!-- 基本信息 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">基本信息</span>
            <el-tag :type="stateTagType(planDetail.status)">
              {{ stateLabel(planDetail.status) }}
            </el-tag>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="计划ID">{{ planDetail.planId }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ formatDate(planDetail.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatDate(planDetail.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="赛事数量">{{ planDetail.contestCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="学生数量">{{ planDetail.studentCount || 0 }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 赛事列表 -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">包含的赛事</span>
            <span class="card-subtitle">{{ planDetail.contests?.length || 0 }} 场赛事</span>
          </div>
        </template>
        
        <el-table :data="planDetail.contests" border>
          <el-table-column prop="contestId" label="赛事ID" width="100" align="center" />
          <el-table-column prop="title" label="赛事名称" min-width="200" />
          <el-table-column prop="status" label="状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="stateTagType(row.status)">
                {{ stateLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="studentCount" label="学生数" width="120" align="center">
            <template #default="{ row }">
              {{ row.studentCount ?? 0 }}
            </template>
          </el-table-column>
          <el-table-column prop="contestCount" label="赛事数" width="120" align="center">
            <template #default="{ row }">
              {{ row.contestCount ?? 0 }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button size="small" text type="primary" @click="goToContest(row.contestId)">
                查看赛事
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 学生列表 -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">参与学生</span>
            <span class="card-subtitle">{{ planDetail.students?.length || 0 }} 名学生</span>
          </div>
        </template>
        
        <el-table :data="planDetail.students" border>
          <el-table-column prop="userId" label="用户ID" width="100" align="center" />
          <el-table-column prop="nickname" label="昵称" min-width="150" />
          <el-table-column prop="enrolledAt" label="加入时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.enrolledAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button size="small" text type="primary" @click="viewStudentProgress(row.userId)">
                查看进度
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 进度统计 -->
      <el-card v-if="progressData" class="section-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">整体进度</span>
          </div>
        </template>
        
        <div class="progress-stats">
          <el-statistic title="平均完成率" :value="progressData.averageCompletion" suffix="%" />
          <el-statistic title="总提交数" :value="progressData.totalSubmissions" />
          <el-statistic title="通过题目数" :value="progressData.totalAccepted" />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getTrainingPlanDetail, getTrainingPlanProgress } from '@/api/training';
import type { TrainingPlan } from '@/types';

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const planDetail = ref<TrainingPlan>({
  planId: 0,
  title: '',
  description: '',
  status: 'SCHEDULE',
  startTime: '',
  endTime: '',
  creatorId: 0,
  studentCount: 0,
  contestCount: 0,
  contests: [],
  students: [],
});

const progressData = ref<any>(null);

const stateLabel = (value?: string) => {
  const map: Record<string, string> = {
    SCHEDULE: '未开始',
    ONGOING: '进行中',
    FINISHED: '已结束',
  };
  return map[value || ''] || '未知';
};

const stateTagType = (value?: string) => {
  const map: Record<string, 'info' | 'success' | 'danger' | 'warning'> = {
    SCHEDULE: 'info',
    ONGOING: 'success',
    FINISHED: 'warning',
  };
  return map[value || ''] || 'info';
};

const formatDate = (value?: string) => {
  if (!value) return '-';
  return value.replace('T', ' ').substring(0, 16);
};

const fetchPlanDetail = async () => {
  loading.value = true;
  try {
    const planId = Number(route.params.id);
    const res = await getTrainingPlanDetail(planId);
    planDetail.value = (res.data as unknown as TrainingPlan) || planDetail.value;
    
    // 获取进度统计
    try {
      const progressRes = await getTrainingPlanProgress(planId);
      progressData.value = progressRes.data;
    } catch (error) {
      console.log('获取进度统计失败:', error);
    }
  } catch (error: any) {
    console.error('获取训练计划详情失败:', error);
    ElMessage.error(error?.message || '获取训练计划详情失败');
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.push({ name: 'TrainingPlanList' });
};

const goToContest = (contestId: number) => {
  router.push({ name: 'ContestDetail', params: { id: contestId } });
};

const viewStudentProgress = (_userId: number) => {
  // TODO: 跳转到学生在该训练计划中的进度页面
  ElMessage.info('功能开发中');
};

onMounted(() => {
  fetchPlanDetail();
});
</script>

<style scoped>
.training-detail-page {
  padding: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.info-card,
.section-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

.progress-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}
</style>
