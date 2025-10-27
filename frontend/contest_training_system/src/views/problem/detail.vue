<template>
  <div class="problem-detail-page page-shell">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-skeleton-item variant="h1" style="width: 60%; margin-bottom: 24px" />
        <el-skeleton-item variant="rect" style="height: 400px" />
      </template>

      <template #default>
        <div v-if="problem">
          <!-- 题目头部 -->
          <div class="problem-header">
            <div class="problem-header__info">
              <h1 class="problem-title">
                <span v-if="problemIndex" class="problem-index">{{ problemIndex }}. </span>
                {{ problem.title }}
              </h1>
              <div class="problem-meta">
                <el-tag v-if="problem.isHidden" type="warning" size="small">已隐藏</el-tag>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  时间限制: {{ problem.timeLimit }}ms
                </span>
                <span class="meta-item">
                  <el-icon><Cpu /></el-icon>
                  内存限制: {{ problem.memoryLimit }}MB
                </span>
              </div>
            </div>
            <div class="problem-header__actions">
              <el-button
                v-if="canEdit"
                type="warning"
                :icon="Edit"
                @click="goToEdit"
              >
                编辑题目
              </el-button>
              <el-button
                v-if="canSubmit"
                type="primary"
                :icon="Upload"
                @click="goToSubmit"
              >
                提交代码
              </el-button>
            </div>
          </div>

          <!-- 题目内容 -->
          <el-card class="page-card">
            <el-tabs v-model="activeTab" class="problem-tabs">
              <!-- 题目描述 -->
              <el-tab-pane label="题目描述" name="description">
                <div class="section">
                  <h3 class="section-title">题目描述</h3>
                  <div class="section-content" v-html="problem.description"></div>
                </div>

                <div class="section">
                  <h3 class="section-title">输入说明</h3>
                  <div class="section-content" v-html="problem.inputSpec"></div>
                </div>

                <div class="section">
                  <h3 class="section-title">输出说明</h3>
                  <div class="section-content" v-html="problem.outputSpec"></div>
                </div>

                <div class="section">
                  <h3 class="section-title">输入样例</h3>
                  <pre class="code-block"><code>{{ problem.sampleInput }}</code></pre>
                </div>

                <div class="section">
                  <h3 class="section-title">输出样例</h3>
                  <pre class="code-block"><code>{{ problem.sampleOutput }}</code></pre>
                </div>

                <div v-if="problem.remark" class="section">
                  <h3 class="section-title">备注</h3>
                  <div class="section-content remark" v-html="problem.remark"></div>
                </div>
              </el-tab-pane>

              <!-- 提交记录 -->
              <el-tab-pane label="提交记录" name="submissions">
                <div class="submissions-section">
                  <div v-if="recentSubmissions.length" class="submissions-list">
                    <div
                      v-for="submission in recentSubmissions"
                      :key="submission.submissionId"
                      class="submission-item"
                      @click="viewSubmission(submission.submissionId)"
                    >
                      <div class="submission-result">
                        <el-tag :type="getStatusTagType(submission.result)" size="small">
                          {{ getStatusLabel(submission.result, 'submission') }}
                        </el-tag>
                      </div>
                      <div class="submission-info">
                        <span class="submission-user">{{ submission.nickname }}</span>
                        <span class="submission-time">{{ formatRelativeTime(submission.createdAt) }}</span>
                      </div>
                      <div class="submission-stats">
                        <span>{{ submission.timeUsed }}ms</span>
                        <span>{{ submission.memoryUsed }}MB</span>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无提交记录" />
                </div>
              </el-tab-pane>

              <!-- 题解 -->
              <el-tab-pane label="题解" name="solutions">
                <div class="solutions-section">
                  <div class="solutions-header">
                    <h3>官方题解</h3>
                    <el-button
                      v-if="canEdit"
                      type="primary"
                      size="small"
                      @click="goToSolution"
                    >
                      发布题解
                    </el-button>
                  </div>

                  <div v-if="solutions.length" class="solutions-list">
                    <div
                      v-for="solution in solutions"
                      :key="solution.reportId"
                      class="solution-item"
                    >
                      <h4 class="solution-title">{{ solution.title }}</h4>
                      <div class="solution-content" v-html="solution.content"></div>
                      <div class="solution-meta">
                        <span>发布时间: {{ formatDateTime(solution.createdAt) }}</span>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无题解" />
                </div>
              </el-tab-pane>

              <!-- 统计信息（教师可见） -->
              <el-tab-pane v-if="canEdit" label="统计" name="statistics">
                <div class="statistics-section">
                  <el-row :gutter="24">
                    <el-col :span="6">
                      <div class="stat-card">
                        <div class="stat-value">{{ statistics.totalSubmissions }}</div>
                        <div class="stat-label">总提交数</div>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="stat-card">
                        <div class="stat-value">{{ statistics.acceptedSubmissions }}</div>
                        <div class="stat-label">通过数</div>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="stat-card">
                        <div class="stat-value">{{ statistics.passRate }}%</div>
                        <div class="stat-label">通过率</div>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="stat-card">
                        <div class="stat-value">{{ statistics.uniqueUsers }}</div>
                        <div class="stat-label">尝试人数</div>
                      </div>
                    </el-col>
                  </el-row>

                  <div v-if="problem.datasets && problem.datasets.length" class="datasets-section">
                    <h3>测试数据</h3>
                    <el-table :data="problem.datasets" class="detail-table">
                      <el-table-column prop="version" label="版本" width="100" align="center" />
                      <el-table-column prop="zipUrl" label="文件地址" min-width="300">
                        <template #default="{ row }">
                          <el-link :href="row.zipUrl" type="primary" target="_blank">
                            {{ row.zipUrl }}
                          </el-link>
                        </template>
                      </el-table-column>
                      <el-table-column prop="isActive" label="状态" width="100" align="center">
                        <template #default="{ row }">
                          <el-tag :type="row.isActive ? 'success' : 'info'" size="small">
                            {{ row.isActive ? '启用' : '禁用' }}
                          </el-tag>
                        </template>
                      </el-table-column>
                      <el-table-column prop="addedAt" label="添加时间" width="180" />
                    </el-table>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </el-card>

          <!-- 快速操作栏 -->
          <div v-if="contestId" class="quick-actions">
            <el-button @click="goBack">返回赛事</el-button>
            <el-button v-if="canSubmit" type="primary" @click="goToSubmit">提交代码</el-button>
          </div>
        </div>

        <el-empty v-else description="题目不存在或已被删除" />
      </template>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Clock, Cpu, Edit, Upload } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/user';
import { getProblemDetail } from '@/api/problem';
import { getSubmissionList } from '@/api/submission';
import {
  formatDateTime,
  formatRelativeTime,
  getStatusTagType,
  getStatusLabel,
} from '@/utils/helpers';
import type { Problem, SolutionReport, Submission } from '@/types';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const problemId = Number(route.params.id);
const contestId = route.query.contestId ? Number(route.query.contestId) : null;
const problemIndex = route.query.index as string | undefined;

const loading = ref(true);
const activeTab = ref('description');
const problem = ref<Problem | null>(null);
const solutions = ref<SolutionReport[]>([]);
const recentSubmissions = ref<Submission[]>([]);

const statistics = ref({
  totalSubmissions: 0,
  acceptedSubmissions: 0,
  passRate: 0,
  uniqueUsers: 0,
});

const isTeacher = computed(() => userStore.role === 'TEACHER' || userStore.role === 'ADMIN');
const isStudent = computed(() => userStore.role === 'STUDENT');
const canEdit = computed(() => isTeacher.value);
const canSubmit = computed(() => isStudent.value || isTeacher.value);


// 获取题目详情
const fetchProblemDetail = async () => {
  console.log('=== 获取题目详情调试 ===');
  console.log('题目ID:', problemId);
  console.log('当前用户角色:', userStore.role);
  console.log('当前用户token:', userStore.token ? `${userStore.token.substring(0, 20)}...` : 'null');
  
  loading.value = true;
  try {
    console.log('开始请求题目详情...');
    const res = await getProblemDetail(problemId);
    console.log('题目详情响应:', res);
    problem.value = (res.data as any) || null;
    console.log('题目详情数据:', problem.value);
  } catch (error: any) {
    console.error('获取题目详情失败:', error);
    console.error('错误详情:', error.response);
    ElMessage.error(error?.message || '获取题目详情失败');
  } finally {
    loading.value = false;
  }
};

// 获取题解（使用专门的错误处理）
const fetchSolutions = async () => {
  try {
    console.log('开始获取题解数据...');
    
    // 使用axios直接调用，避免全局错误处理
    const response = await fetch(`/api/problem/${problemId}/solution/list`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.ok) {
      const data = await response.json();
      const allSolutions = data.data || [];
      solutions.value = isTeacher.value
        ? allSolutions
        : allSolutions.filter((s: any) => s.isPublished === 1);
      console.log('题解数据加载成功:', solutions.value.length, '条');
    } else {
      console.log('题解API返回错误状态:', response.status);
      // 不显示错误消息，静默处理
    }
  } catch (error: any) {
    console.log('题解数据加载失败，这是正常的（API可能未实现）:', error.message);
    // 对于题解这种可选功能，不显示错误消息
    // 只记录日志，不影响用户体验
  }
};

// 获取最近提交
const fetchRecentSubmissions = async () => {
  try {
    const res = await getSubmissionList({
      problemId: problemId,
      page: 1,
      pageSize: 10,
    });
    const data = (res.data as any) || {};
    recentSubmissions.value = data.submissions || data.list || [];
  } catch (error: any) {
    // 忽略错误
  }
};

// 查看提交详情
const viewSubmission = (submissionId: number) => {
  router.push({
    name: 'SubmissionDetail',
    params: { id: submissionId },
  });
};

// 前往提交页面
const goToSubmit = () => {
  router.push({
    name: 'ProblemSubmit',
    params: { id: problemId },
    query: {
      contestId: contestId || undefined,
      problemTitle: problem.value?.title,
    },
  });
};

// 前往编辑页面
const goToEdit = () => {
  router.push({
    name: 'ProblemEdit',
    params: { id: problemId },
  });
};

// 前往题解管理
const goToSolution = () => {
  router.push({
    name: 'ProblemSolution',
    params: { id: problemId },
  });
};

// 返回赛事
const goBack = () => {
  if (contestId) {
    router.push({
      name: 'ContestDetail',
      params: { id: contestId },
    });
  } else {
    router.push({ name: 'ProblemList' });
  }
};

// 监听标签切换，按需加载题解
watch(activeTab, (newTab) => {
  if (newTab === 'solutions' && solutions.value.length === 0) {
    console.log('切换到题解标签，开始加载题解数据');
    fetchSolutions();
  }

  if (newTab === 'submissions' && recentSubmissions.value.length === 0) {
    fetchRecentSubmissions();
  }
});

onMounted(async () => {
  await fetchProblemDetail();
  // 注释掉自动加载题解，改为按需加载
  // fetchSolutions();
  // fetchRecentSubmissions();
  
});
</script>

<style scoped>
.problem-detail-page {
  gap: 24px;
}

.problem-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.problem-header__info {
  flex: 1;
  min-width: 300px;
}

.problem-title {
  margin: 0 0 16px;
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.problem-index {
  color: var(--accent-primary);
}

.problem-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.problem-header__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.problem-tabs {
  min-height: 500px;
}

.problem-tabs :deep(.el-tabs__nav-wrap) {
  margin-bottom: 20px;
}

.problem-tabs :deep(.el-tabs__nav) {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.problem-tabs :deep(.el-tabs__item) {
  flex: 1;
  text-align: center;
  color: var(--text-primary);
  font-weight: 500;
  opacity: 0.85;
}

.problem-tabs :deep(.el-tabs__item.is-active) {
  opacity: 1;
  color: var(--text-primary);
}

.problem-tabs :deep(.el-tabs__active-bar) {
  background-color: var(--accent-primary);
  height: 3px;
}

.section {
  margin-bottom: 32px;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: var(--accent-primary);
  padding-bottom: 8px;
  border-bottom: 2px solid var(--border-default);
}

.section-content {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  font-size: 15px;
}

.section-content :deep(p) {
  margin: 0 0 12px;
}

.section-content :deep(ul),
.section-content :deep(ol) {
  margin: 0 0 12px;
  padding-left: 24px;
}

.code-block {
  background: var(--bg-canvas-inset);
  border: 1px solid var(--border-default);
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #e6edf3;
}

.code-block code {
  background: none;
  padding: 0;
  color: inherit;
}

.remark {
  background: rgba(198, 144, 38, 0.1);
  border-left: 3px solid var(--attention-emphasis);
  padding: 12px 16px;
  border-radius: 4px;
}

.submissions-section,
.solutions-section,
.statistics-section {
  padding: 16px 0;
}

.submissions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.submission-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: var(--bg-canvas-inset);
  border: 1px solid var(--border-default);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.submission-item:hover {
  background: var(--bg-canvas-subtle);
  border-color: var(--accent-primary);
}

.submission-result {
  flex: 0 0 100px;
}

.submission-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.submission-user {
  font-weight: 600;
  color: #ffffff;
}

.submission-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.submission-stats {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.solutions-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.solutions-header h3 {
  margin: 0;
  font-size: 18px;
  color: #ffffff;
}

.solutions-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.solution-item {
  padding: 24px;
  background: var(--bg-canvas-inset);
  border: 1px solid var(--border-default);
  border-radius: 8px;
}

.solution-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.solution-content {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  margin-bottom: 16px;
}

.solution-meta {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.stat-card {
  text-align: center;
  padding: 24px;
  background: var(--bg-canvas-inset);
  border: 1px solid var(--border-default);
  border-radius: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--accent-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.datasets-section {
  margin-top: 32px;
}

.datasets-section h3 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #ffffff;
}

.quick-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .problem-title {
    font-size: 20px;
  }

  .problem-header {
    flex-direction: column;
  }

  .problem-header__actions {
    width: 100%;
  }

  .submission-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .submission-stats {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
