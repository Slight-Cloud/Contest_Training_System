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
      <!-- 学生视图 -->
      <el-row :gutter="24" v-if="isStudent">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalProblems }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalContests }}</div>
            <div class="stat-label">赛事总数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalTrainingPlans }}</div>
            <div class="stat-label">我的训练计划</div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 教师视图 -->
      <el-row :gutter="24" v-else-if="isTeacher">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalProblems }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalContests }}</div>
            <div class="stat-label">赛事总数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalTrainingPlans }}</div>
            <div class="stat-label">创建的训练计划</div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 管理员视图 -->
      <el-row :gutter="24" v-else-if="isAdmin">
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
            <div class="stat-number">{{ stats.totalTrainingPlans }}</div>
            <div class="stat-label">训练计划总数</div>
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
      
      <div class="activities-container">
        <!-- 最近题目 -->
        <div class="activity-section">
          <h3 class="section-title">📝 最近题目</h3>
          <div class="activity-list">
            <div 
              class="activity-item clickable" 
              v-for="problem in recentProblems" 
              :key="`problem-${problem.problemId}`"
              @click="goTo(`/problem/${problem.problemId}`)"
            >
              <div class="activity-content">
                <div class="activity-title">{{ problem.title }}</div>
                <div class="activity-time">{{ formatTimeAgo(problem.createdAt) }}</div>
              </div>
            </div>
            <div v-if="!recentProblems.length" class="no-activities">
              暂无最近题目
            </div>
          </div>
        </div>

        <!-- 最近赛事 -->
        <div class="activity-section">
          <h3 class="section-title">🏆 最近赛事</h3>
          <div class="activity-list">
            <div 
              class="activity-item clickable" 
              v-for="contest in recentContests" 
              :key="`contest-${contest.contestId}`"
              @click="goTo(`/contest/${contest.contestId}`)"
            >
              <div class="activity-content">
                <div class="activity-title">
                  {{ contest.title }}
                  <el-tag size="small" :type="getContestStatusType(contest.status)">
                    {{ getContestStatusText(contest.status) }}
                  </el-tag>
                </div>
                <div class="activity-time">{{ formatTimeAgo(contest.startTime) }}</div>
              </div>
            </div>
            <div v-if="!recentContests.length" class="no-activities">
              暂无最近赛事
            </div>
          </div>
        </div>

        <!-- 最近训练计划 -->
        <div class="activity-section">
          <h3 class="section-title">📊 最近训练计划</h3>
          <div class="activity-list">
            <div 
              class="activity-item clickable" 
              v-for="plan in recentPlans" 
              :key="`plan-${plan.planId}`"
              @click="goTo(`/training/${plan.planId}`)"
            >
              <div class="activity-content">
                <div class="activity-title">
                  {{ plan.title }}
                  <el-tag size="small" :type="getTrainingStatusType(plan.status)">
                    {{ getTrainingStatusText(plan.status) }}
                  </el-tag>
                </div>
                <div class="activity-time">{{ formatTimeAgo(plan.startTime) }}</div>
              </div>
            </div>
            <div v-if="!recentPlans.length" class="no-activities">
              暂无最近训练计划
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { getProblemList } from '@/api/problem';
import { getContestList } from '@/api/contest';
import { getTrainingPlanList } from '@/api/training';
import { getUserList } from '@/api/user';
import type { Problem, Contest, TrainingPlan } from '@/types';

const router = useRouter();
const userStore = useUserStore();

const stats = ref({
  totalProblems: 0,
  totalContests: 0,
  totalTrainingPlans: 0,
  totalUsers: 0,
});

const recentProblems = ref<Problem[]>([]);
const recentContests = ref<Contest[]>([]);
const recentPlans = ref<TrainingPlan[]>([]);

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

// 格式化时间差
const formatTimeAgo = (dateString: string): string => {
  const now = new Date();
  const date = new Date(dateString);
  const diffMs = now.getTime() - date.getTime();
  const diffMinutes = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMinutes / 60);
  const diffDays = Math.floor(diffHours / 24);

  if (diffMinutes < 1) return '刚刚';
  if (diffMinutes < 60) return `${diffMinutes}分钟前`;
  if (diffHours < 24) return `${diffHours}小时前`;
  if (diffDays === 1) return '1天前';
  return `${diffDays}天前`;
};

// 获取赛事状态文本
const getContestStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    'SCHEDULED': '未开始',
    'ONGOING': '进行中',
    'ENDED': '已结束',
  };
  return statusMap[status] || status;
};

// 获取赛事状态类型
const getContestStatusType = (status: string): string => {
  const typeMap: Record<string, string> = {
    'SCHEDULED': 'info',
    'ONGOING': 'success',
    'ENDED': 'info',
  };
  return typeMap[status] || 'info';
};

// 获取训练计划状态文本
const getTrainingStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    'SCHEDULE': '未开始',
    'SCHEDULED': '未开始', // 同时支持SCHEDULED
    'ONGOING': '进行中',
    'ENDED': '已结束',
  };
  return statusMap[status] || status;
};

// 获取训练计划状态类型
const getTrainingStatusType = (status: string): string => {
  const typeMap: Record<string, string> = {
    'SCHEDULE': 'info',
    'SCHEDULED': 'info', // 同时支持SCHEDULED
    'ONGOING': 'success',
    'ENDED': 'info',
  };
  return typeMap[status] || 'info';
};

// 获取统计数据
const fetchStats = async () => {
  try {
    // 获取题目总数
    const problemRes = await getProblemList({ page: 1, pageSize: 1 });
    stats.value.totalProblems = problemRes.data?.total || 0;

    // 获取赛事总数
    const contestRes = await getContestList({ page: 1, pageSize: 1 });
    stats.value.totalContests = contestRes.data?.total || 0;

    // 获取训练计划总数
    if (isStudent.value) {
      // 学生查看自己参与的训练计划
      const trainingRes = await getTrainingPlanList({ page: 1, pageSize: 1 });
      stats.value.totalTrainingPlans = trainingRes.data?.total || 0;
    } else if (isTeacher.value) {
      // 教师查看自己创建的训练计划
      const trainingRes = await getTrainingPlanList({ 
        page: 1, 
        pageSize: 1,
        creatorId: userStore.userId 
      });
      stats.value.totalTrainingPlans = trainingRes.data?.total || 0;
    } else if (isAdmin.value) {
      // 管理员查看所有训练计划
      const trainingRes = await getTrainingPlanList({ page: 1, pageSize: 1 });
      stats.value.totalTrainingPlans = trainingRes.data?.total || 0;
      
      // 管理员还需要获取用户总数
      const userRes = await getUserList({ page: 1, pageSize: 1 });
      stats.value.totalUsers = userRes.data?.total || 0;
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

// 获取最近题目（最多5个）
const fetchRecentProblems = async () => {
  try {
    // 根据角色获取不同的题目列表
    let allProblems: Problem[] = [];
    
    if (isTeacher.value || isAdmin.value) {
      // 教师/管理员：先获取自己创建的题目（按创建时间降序）
      const myProblemsRes = await getProblemList({ 
        page: 1, 
        pageSize: 5,
        creatorId: userStore.userId,
        sortBy: 'created_desc' // 使用后端排序
      });
      const myProblems = myProblemsRes.data?.list || myProblemsRes.data?.problems || [];
      
      // 如果自己的题目不足5个，再获取其他人的题目补充
      if (myProblems.length < 5) {
        const otherProblemsRes = await getProblemList({ 
          page: 1, 
          pageSize: 5 - myProblems.length,
          sortBy: 'created_desc' // 使用后端排序
        });
        const otherProblems = (otherProblemsRes.data?.list || otherProblemsRes.data?.problems || [])
          .filter((p: Problem) => p.creatorId !== userStore.userId);
        
        allProblems = [...myProblems, ...otherProblems];
      } else {
        allProblems = myProblems;
      }
      
      if (import.meta.env.MODE === 'development') {
        console.log('教师/管理员 - 我的题目:', myProblems.length);
        console.log('教师/管理员 - 总题目:', allProblems.length);
      }
    } else {
      // 学生：直接按创建时间降序获取
      const problemRes = await getProblemList({ 
        page: 1, 
        pageSize: 5,
        sortBy: 'created_desc' // 使用后端排序
      });
      allProblems = problemRes.data?.list || problemRes.data?.problems || [];
      
      if (import.meta.env.MODE === 'development') {
        console.log('学生 - 获取题目数量:', allProblems.length);
      }
    }
    
    recentProblems.value = allProblems.slice(0, 5);
    
    if (import.meta.env.MODE === 'development') {
      console.log('最终展示的题目（前5个）:', recentProblems.value);
      console.log('题目详情:', recentProblems.value.map(p => ({
        id: p.problemId,
        title: p.title,
        creatorId: p.creatorId,
        createdAt: p.createdAt
      })));
    }
  } catch (error) {
    console.error('获取最近题目失败:', error);
    recentProblems.value = [];
  }
};

// 获取最近赛事（根据用户角色，最多5个）
const fetchRecentContests = async () => {
  try {
    const contestRes = await getContestList({ 
      page: 1, 
      pageSize: 20,
    });
    const contests = contestRes.data?.list || contestRes.data?.contests || [];
    
    if (import.meta.env.MODE === 'development') {
      console.log('获取赛事列表:', contests);
    }
    
    // 后端已经根据角色返回了正确的数据
    // 学生：返回参与的赛事
    // 教师/管理员：返回创建的赛事
    
    // 排序优先级：
    // 对于学生/教师/管理员都是：正在进行 > 未开始 > 已结束
    const sortedContests = contests.sort((a: Contest, b: Contest) => {
      // 按状态排序
      const statusPriority: Record<string, number> = {
        'ONGOING': 3,
        'SCHEDULED': 2,
        'ENDED': 1,
      };
      const priorityDiff = (statusPriority[b.status] || 0) - (statusPriority[a.status] || 0);
      if (priorityDiff !== 0) return priorityDiff;
      
      // 状态相同时按开始时间降序
      return new Date(b.startTime).getTime() - new Date(a.startTime).getTime();
    });
    
    recentContests.value = sortedContests.slice(0, 5);
    
    if (import.meta.env.MODE === 'development') {
      console.log('排序后的赛事:', recentContests.value);
    }
  } catch (error) {
    console.error('获取最近赛事失败:', error);
    recentContests.value = [];
  }
};

// 获取最近训练计划（根据用户角色，最多5个）
const fetchRecentPlans = async () => {
  try {
    const planRes = await getTrainingPlanList({ 
      page: 1, 
      pageSize: 20,
    });
    // 后端可能返回 plans 或 list 字段
    const plans = planRes.data?.plans || planRes.data?.list || [];
    
    if (import.meta.env.MODE === 'development') {
      console.log('获取训练计划列表:', plans);
    }
    
    // 后端已经根据角色返回了正确的数据
    // 学生：返回参与的训练计划
    // 教师/管理员：返回创建的训练计划
    
    // 排序优先级：
    // 对于学生/教师/管理员都是：正在进行 > 未开始 > 已结束
    const sortedPlans = plans.sort((a: TrainingPlan, b: TrainingPlan) => {
      // 按状态排序
      const statusPriority: Record<string, number> = {
        'ONGOING': 3,
        'SCHEDULE': 2,
        'SCHEDULED': 2, // 支持两种未开始状态
        'ENDED': 1,
      };
      const priorityDiff = (statusPriority[b.status] || 0) - (statusPriority[a.status] || 0);
      if (priorityDiff !== 0) return priorityDiff;
      
      // 状态相同时按开始时间降序
      return new Date(b.startTime).getTime() - new Date(a.startTime).getTime();
    });
    
    recentPlans.value = sortedPlans.slice(0, 5);
    
    if (import.meta.env.MODE === 'development') {
      console.log('排序后的训练计划:', recentPlans.value);
    }
  } catch (error) {
    console.error('获取最近训练计划失败:', error);
    recentPlans.value = [];
  }
};

// 获取所有最近活动
const fetchRecentActivities = async () => {
  await Promise.all([
    fetchRecentProblems(),
    fetchRecentContests(),
    fetchRecentPlans(),
  ]);
};

onMounted(() => {
  fetchStats();
  fetchRecentActivities();
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
  transition: transform var(--transition-fast), box-shadow var(--transition-fast);
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
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
  color: var(--text-primary);
  font-size: 18px;
}

.card-content p {
  margin: 0 0 20px;
  color: var(--text-secondary);
  font-size: 14px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: var(--accent-primary);
  margin-bottom: 8px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.activities-container {
  display: grid;
  grid-template-columns: 1fr 1.2fr 1.2fr;
  gap: 24px;
}

.activity-section {
  min-height: 200px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--border-default);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  background: var(--bg-secondary);
  transition: all var(--transition-fast);
}

.activity-item.clickable {
  cursor: pointer;
}

.activity-item.clickable:hover {
  background: var(--bg-hover);
  transform: translateX(4px);
  box-shadow: var(--shadow-sm);
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  color: var(--text-primary);
  font-size: 14px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.activity-time {
  color: var(--text-tertiary);
  font-size: 12px;
}

.no-activities {
  text-align: center;
  color: var(--text-tertiary);
  padding: 40px 0;
  font-size: 14px;
}

@media (max-width: 1200px) {
  .activities-container {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .quick-actions {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
  }
  
  .activities-container {
    gap: 12px;
  }
}
</style>
