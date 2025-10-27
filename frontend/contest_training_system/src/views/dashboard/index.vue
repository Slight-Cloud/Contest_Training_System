<template>
  <div class="dashboard-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">仪表盘</h2>
        <p class="page-header__subtitle">{{ welcomeMessage }}</p>
      </div>
    </div>

    <!-- 快速操作卡片 -->
    <div class="quick-actions">
      <el-card class="action-card" v-if="isAdmin || isTeacher">
        <div class="card-content">
          <div class="card-icon">📝</div>
          <h3>题目管理</h3>
          <p>创建和管理题目库</p>
          <el-button type="primary" @click="goTo('/problems')">进入题库</el-button>
        </div>
      </el-card>

      <el-card class="action-card">
        <div class="card-content">
          <div class="card-icon">🏆</div>
          <h3>赛事中心</h3>
          <p>查看和参与竞赛</p>
          <el-button type="primary" @click="goTo('/contests')">查看赛事</el-button>
        </div>
      </el-card>

      <el-card class="action-card" v-if="isStudent">
        <div class="card-content">
          <div class="card-icon">📊</div>
          <h3>提交记录</h3>
          <p>查看我的代码提交</p>
          <el-button type="primary" @click="goTo('/submissions')">查看记录</el-button>
        </div>
      </el-card>

      <el-card class="action-card">
        <div class="card-content">
          <div class="card-icon">👤</div>
          <h3>个人资料</h3>
          <p>管理个人信息</p>
          <el-button type="primary" @click="goTo('/profile')">编辑资料</el-button>
        </div>
      </el-card>

      <el-card class="action-card" v-if="isAdmin">
        <div class="card-content">
          <div class="card-icon">👥</div>
          <h3>用户管理</h3>
          <p>管理系统用户</p>
          <el-button type="primary" @click="goTo('/admin/users')">用户管理</el-button>
        </div>
      </el-card>
    </div>

    <!-- 统计信息 -->
    <el-card class="page-card">
      <template #header>
        <span>系统概览</span>
      </template>
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalProblems }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalContests }}</div>
            <div class="stat-label">赛事总数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalSubmissions }}</div>
            <div class="stat-label">提交总数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalUsers }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最近活动 -->
    <el-card class="page-card">
      <template #header>
        <span>最近活动</span>
      </template>
      <div class="recent-activities">
        <div class="activity-item" v-for="activity in recentActivities" :key="activity.id">
          <div class="activity-icon">{{ activity.icon }}</div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-time">{{ activity.time }}</div>
          </div>
        </div>
        <div v-if="!recentActivities.length" class="no-activities">
          暂无最近活动
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';

const router = useRouter();
const userStore = useUserStore();

const stats = ref({
  totalProblems: 0,
  totalContests: 0,
  totalSubmissions: 0,
  totalUsers: 0,
});

const recentActivities = ref([
  {
    id: 1,
    icon: '🏆',
    title: '2025年春季编程竞赛开始报名',
    time: '2小时前',
  },
  {
    id: 2,
    icon: '📝',
    title: '新增算法题目：二分查找',
    time: '5小时前',
  },
  {
    id: 3,
    icon: '👤',
    title: '新用户注册：张三',
    time: '1天前',
  },
]);

const isAdmin = computed(() => userStore.role === 'ADMIN');
const isTeacher = computed(() => userStore.role === 'TEACHER');
const isStudent = computed(() => userStore.role === 'STUDENT');

const welcomeMessage = computed(() => {
  const name = userStore.nickname || '用户';
  const hour = new Date().getHours();
  let greeting = '早上好';
  if (hour >= 12 && hour < 18) greeting = '下午好';
  else if (hour >= 18) greeting = '晚上好';
  
  return `${greeting}，${name}！欢迎使用竞赛集训系统`;
});

const goTo = (path: string) => {
  router.push(path);
};

const fetchStats = async () => {
  // 这里应该调用API获取统计数据，暂时使用模拟数据
  stats.value = {
    totalProblems: 156,
    totalContests: 23,
    totalSubmissions: 1247,
    totalUsers: 89,
  };
};

onMounted(() => {
  fetchStats();
});
</script>

<style scoped>
.dashboard-page {
  gap: 24px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.action-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-content {
  text-align: center;
  padding: 20px;
}

.card-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.card-content h3 {
  margin: 0 0 8px;
  color: #e6edf3;
  font-size: 18px;
}

.card-content p {
  margin: 0 0 20px;
  color: #9fb2c6;
  font-size: 14px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #2f81f7;
  margin-bottom: 8px;
}

.stat-label {
  color: #9fb2c6;
  font-size: 14px;
}

.recent-activities {
  max-height: 300px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-default);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  font-size: 24px;
  margin-right: 16px;
  width: 40px;
  text-align: center;
}

.activity-content {
  flex: 1;
}

.activity-title {
  color: #e6edf3;
  font-size: 14px;
  margin-bottom: 4px;
}

.activity-time {
  color: #9fb2c6;
  font-size: 12px;
}

.no-activities {
  text-align: center;
  color: #9fb2c6;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .quick-actions {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
  }
}
</style>
