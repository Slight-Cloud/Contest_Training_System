<template>
  <div class="contest-detail-page page-shell">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-skeleton-item variant="h1" style="width: 40%; margin-bottom: 16px" />
        <el-skeleton-item variant="text" style="width: 80%; margin-bottom: 32px" />
        <el-skeleton-item variant="rect" style="height: 200px" />
      </template>

      <template #default>
        <div v-if="contest">
          <!-- 赛事头部 -->
          <div class="contest-header">
            <div class="contest-header__info">
              <h1 class="contest-title">{{ contest.title }}</h1>
              <div class="contest-meta">
                <el-tag :type="getStatusTagType(contest.status)" size="large">
                  {{ getStatusLabel(contest.status, 'contest') }}
                </el-tag>
                <el-tag type="info" size="large" effect="plain">
                  {{ getVisibilityLabel(contest.visibility) }}
                </el-tag>
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ contest.participantCount || 0 }} 人参赛
                </span>
              </div>
            </div>
            <div class="contest-header__actions">
              <el-button
                v-if="canEdit"
                type="warning"
                :icon="Edit"
                @click="goToEdit"
              >
                编辑赛事
              </el-button>
              <el-button
                v-if="canJoin && !hasJoined"
                type="primary"
                :icon="Trophy"
                :loading="checkingJoinStatus"
                :disabled="checkingJoinStatus"
                @click="handleJoin"
              >
                参加赛事
              </el-button>
              <el-button
                v-if="hasJoined && isOngoing"
                type="success"
                :icon="Edit"
                @click="enterContest"
              >
                进入赛事
              </el-button>
              <el-button
                v-if="contest.status === 'ENDED'"
                type="info"
                :icon="TrendCharts"
                @click="viewRanking"
              >
                查看排名
              </el-button>
            </div>  
          </div>

          
          <!-- 赛事时间信息 -->
          <el-card class="page-card time-card">
            <div class="time-info">
              <div class="time-item">
                <div class="time-label">开始时间</div>
                <div class="time-value">{{ formatDateTime(contest.startTime) }}</div>
              </div>
              <div class="time-divider"></div>
              <div class="time-item">
                <div class="time-label">结束时间</div>
                <div class="time-value">{{ formatDateTime(contest.endTime) }}</div>
              </div>
              <div class="time-divider"></div>
              <div class="time-item">
                <div class="time-label">
                  {{ contest.status === 'SCHEDULED' ? '距离开始' : contest.status === 'ONGOING' ? '剩余时间' : '已结束' }}
                </div>
                <div class="time-value countdown">
                  <Countdown
                    v-if="isCountdownVisible"
                    :end-time="countdownEndTime"
                    @finish="handleTimeFinish"
                  />
                  <span v-else class="ENDED-text">赛事已结束</span>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 赛事描述 -->
          <el-card v-if="contest.description" class="page-card">
            <template #header>
              <span>赛事介绍</span>
            </template>
            <div class="contest-description" v-html="contest.description"></div>
          </el-card>

          <!-- 题目列表 -->
          <el-card class="page-card">
            <template #header>
              <div class="card-header">
                <span>题目列表（{{ problems.length }} 道）</span>
                <el-tag v-if="canEdit" type="warning" size="small">教师可见</el-tag>
              </div>
            </template>

            <el-table
              :data="problems"
              class="detail-table problem-table"
              @row-click="handleProblemClick"
            >
              <el-table-column label="题号" width="80" align="center">
                <template #default="{ $index }">
                  <span class="problem-index">{{ String.fromCharCode(65 + $index) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="title" label="题目名称" min-width="250">
                <template #default="{ row }">
                  <div class="problem-title-cell">
                    <span class="problem-title">{{ row.title }}</span>
                    <el-tag v-if="getProblemStatus(row.problemId)" :type="getProblemStatusType(row.problemId)" size="small">
                      {{ getProblemStatus(row.problemId) }}
                    </el-tag>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="限制" width="180" align="center">
                <template #default="{ row }">
                  <div class="limit-cell">
                    <span>{{ row.timeLimit }}ms</span>
                    <span class="divider">/</span>
                    <span>{{ row.memoryLimit }}MB</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="通过率" width="120" align="center">
                <template #default="{ row }">
                  <span class="pass-rate">
                    {{ calculatePassRate(row.problemId) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    text
                    size="small"
                    @click.stop="viewProblem(row.problemId)"
                  >
                    查看题目
                  </el-button>
                  <el-button
                    v-if="hasJoined && isOngoing"
                    type="success"
                    text
                    size="small"
                    @click.stop="submitProblem(row.problemId, row.title)"
                  >
                    提交
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty v-if="!problems.length" description="暂无题目" />
          </el-card>

          <!-- 参赛须知 -->
          <el-alert
            v-if="contest.status === 'SCHEDULED'"
            type="info"
            :closable="false"
            class="notice-alert"
          >
            <template #title>
              <div class="notice-content">
                <el-icon><InfoFilled /></el-icon>
                <span>赛事尚未开始，请在开始时间后参与答题</span>
              </div>
            </template>
          </el-alert>
        </div>

        <el-empty v-else description="赛事不存在或已被删除" />
      </template>
    </el-skeleton>

    <!-- 参赛对话框 -->
    <el-dialog
      v-model="joinDialog.visible"
      title="参加赛事"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form :model="joinDialog.form" label-width="88px">
        <el-form-item label="赛事名称">
          <span>{{ contest?.title }}</span>
        </el-form-item>
        <el-form-item v-if="contest?.visibility === 'PRIVATE'" label="赛事密码">
          <el-input
            v-model="joinDialog.form.password"
            type="password"
            placeholder="请输入赛事密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="队伍名称">
          <el-input
            v-model="joinDialog.form.teamName"
            placeholder="可选，用于团队赛"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="joinDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="joinDialog.submitting" @click="submitJoin">
          确认参赛
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Edit, Trophy, TrendCharts, User, InfoFilled } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/user';
import { getContestDetail, joinContest, hasJoinedContest } from '@/api/contest';
import { formatDateTime, getStatusTagType, getStatusLabel, getVisibilityLabel } from '@/utils/helpers';
import Countdown from '@/components/Countdown.vue';
import type { Contest, ContestProblem } from '@/types';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const contestId = Number(route.params.id);
const loading = ref(true);
const contest = ref<Contest | null>(null);
const problems = ref<ContestProblem[]>([]);
const hasJoined = ref(false);
const checkingJoinStatus = ref(false);

const isController = computed(() => userStore.role === 'TEACHER' || userStore.role === 'ADMIN');
const isStudent = computed(() => userStore.role === 'STUDENT');
const canEdit = computed(() => isController.value);
const ACTIVE_STATES = ['SCHEDULED'] as const;
const isActiveState = (state?: string | null): state is (typeof ACTIVE_STATES)[number] => {
  return !!state && ACTIVE_STATES.includes(state as (typeof ACTIVE_STATES)[number]);
};
const canJoin = computed(() => isStudent.value && isActiveState(contest.value?.status));
const isOngoing = computed(() => contest.value?.status === 'ONGOING');
const COUNTDOWN_STATES = ['SCHEDULED', 'ONGOING'] as const;
const isCountdownState = (state?: string | null): state is (typeof COUNTDOWN_STATES)[number] => {
  return !!state && COUNTDOWN_STATES.includes(state as (typeof COUNTDOWN_STATES)[number]);
};
const countdownEndTime = computed(() => {
  if (!contest.value) return '';
  return contest.value.status === 'SCHEDULED' ? contest.value.startTime : contest.value.endTime;
});
const isCountdownVisible = computed(() => isCountdownState(contest.value?.status) && !!countdownEndTime.value);


const joinDialog = ref({
  visible: false,
  form: {
    password: '',
    teamName: '',
  },
  submitting: false,
});

// 获取赛事详情
const fetchContestDetail = async () => {
  loading.value = true;
  try {
    const res = await getContestDetail(contestId);
    contest.value = (res.data as any) || null;
    problems.value = contest.value?.problems || [];

    if (contest.value && isStudent.value) {
      await checkJoinStatus();
    }
  } catch (error: any) {
    ElMessage.error(error?.message || '获取赛事详情失败');
  } finally {
    loading.value = false;
  }
};

const checkJoinStatus = async () => {
  if (!contest.value) return;
  if (!isStudent.value) {
    hasJoined.value = false;
    return;
  }
  if (checkingJoinStatus.value) {
    return;
  }
  checkingJoinStatus.value = true;
  try {
    const res = await hasJoinedContest({ contestId });
    const raw = res as unknown;
    if (typeof raw === 'boolean') {
      hasJoined.value = raw;
    } else if (raw && typeof raw === 'object') {
      const maybe = raw as { data?: unknown };
      const dataValue = maybe.data;
      if (typeof dataValue === 'boolean') {
        hasJoined.value = dataValue;
      } else if (dataValue !== undefined) {
        hasJoined.value = Boolean(dataValue);
      } else {
        hasJoined.value = false;
      }
    } else {
      hasJoined.value = false;
    }
  } catch (error) {
    console.warn('检查参赛状态失败：', error);
    hasJoined.value = false;
  } finally {
    checkingJoinStatus.value = false;
  }
};

// 处理参赛
const handleJoin = () => {
  joinDialog.value.visible = true;
  joinDialog.value.form = {
    password: '',
    teamName: '',
  };
};

const submitJoin = async () => {
  if (contest.value?.visibility === 'PRIVATE' && !joinDialog.value.form.password) {
    ElMessage.warning('请输入赛事密码');
    return;
  }

  joinDialog.value.submitting = true;
  try {
    await joinContest({
      contestId: contestId,
      password: joinDialog.value.form.password || undefined,
      teamName: joinDialog.value.form.teamName || undefined,
    });
    ElMessage.success('参赛成功');
    joinDialog.value.visible = false;
    hasJoined.value = true;
    await fetchContestDetail();
  } catch (error: any) {
    ElMessage.error(error?.message || '参赛失败');
  } finally {
    joinDialog.value.submitting = false;
  }
};

// 进入赛事
const enterContest = () => {
  router.push({
    name: 'ContestArena',
    params: { id: contestId },
  });
};

// 查看题目
const viewProblem = (problemId: number) => {
  router.push({
    name: 'ProblemDetail',
    params: { id: problemId },
    query: { contestId: contestId },
  });
};

// 提交代码
const submitProblem = (problemId: number, problemTitle: string) => {
  router.push({
    name: 'ProblemSubmit',
    params: { id: problemId },
    query: {
      contestId: contestId,
      problemTitle: problemTitle,
      contestTitle: contest.value?.title,
    },
  });
};

// 查看排名
const viewRanking = () => {
  router.push({
    name: 'ContestRanking',
    params: { id: contestId },
  });
};

// 编辑赛事
const goToEdit = () => {
  router.push({
    name: 'ContestList',
    query: { edit: contestId },
  });
};

// 题目点击
const handleProblemClick = (row: ContestProblem) => {
  viewProblem(row.problemId);
};

// 获取题目状态（需要提交记录数据）
const getProblemStatus = (_problemId: number) => {
  // TODO: 根据用户提交记录返回状态
  return '';
};

const getProblemStatusType = (problemId: number) => {
  const status = getProblemStatus(problemId);
  if (status === 'AC') return 'success';
  if (status === '尝试中') return 'warning';
  return 'info';
};

// 计算通过率
const calculatePassRate = (_problemId: number) => {
  // TODO: 根据提交统计计算通过率
  return '-';
};


// script setup
const handleTimeFinish = () => {
  // 在倒计时结束时，contest.value.status 仍然是变化前的状态
  const currentStatus = contest.value?.status;

  if (currentStatus === 'SCHEDULED') {
    // 从 "未开始" 状态倒计时结束，意味着赛事现在开始了
    ElMessage.success('赛事已开始，可以开始答题了！');
  } else if (currentStatus === 'ONGOING') {
    // 从 "进行中" 状态倒计时结束，意味着赛事刚刚结束了
    ElMessage.info('赛事已结束，无法再提交答案。');
  }

  // 重新从服务器获取最新的赛事状态（例如：从 SCHEDULED 变为 ONGOING）
  fetchContestDetail(); 
};

onMounted(() => {
  fetchContestDetail();
});
 
 watch(
   () => isStudent.value,
   (value) => {
     if (value) {
       checkJoinStatus();
     } else {
       hasJoined.value = false;
     }
   },
 );
</script>

<style scoped>
.contest-detail-page {
  gap: 24px;
}

.contest-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.contest-header__info {
  flex: 1;
  min-width: 300px;
}

.contest-title {
  margin: 0 0 16px;
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
}

.contest-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.contest-header__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.time-card {
  background: var(--bg-canvas-inset);
}

.time-info {
  display: flex;
  align-items: center;
  justify-content: space-around;
  gap: 24px;
  flex-wrap: wrap;
}

.time-item {
  text-align: center;
  flex: 1;
  min-width: 150px;
}

.time-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 8px;
}

.time-value {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.countdown {
  color: var(--accent-primary);
}

.ENDED-text {
  color: rgba(255, 255, 255, 0.5);
}

.time-divider {
  width: 1px;
  height: 40px;
  background: var(--border-default);
}

.contest-description {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  font-size: 15px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.problem-table {
  cursor: pointer;
}


.problem-index {
  font-size: 18px;
  font-weight: 700;
  color: var(--accent-primary);
}

.problem-title-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.problem-title {
  font-weight: 600;
  color: #e6edf3;
}

.limit-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
}

.divider {
  color: rgba(255, 255, 255, 0.4);
}

.pass-rate {
  color: rgba(255, 255, 255, 0.8);
}

.notice-alert {
  margin-top: 24px;
}

.notice-content {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .contest-title {
    font-size: 24px;
  }

  .contest-header {
    flex-direction: column;
  }

  .contest-header__actions {
    width: 100%;
  }

  .time-info {
    flex-direction: column;
  }

  .time-divider {
    width: 100%;
    height: 1px;
  }
}
</style>
