<template>
  <div class="testcase-results">
    <div class="results-header">
      <h4>测试点详情</h4>
      <div class="summary-stats">
        <el-tag :type="overallResult === 'AC' ? 'success' : 'danger'" size="large">
          {{ overallResult === 'AC' ? '通过' : '未通过' }}
        </el-tag>
        <span class="pass-rate">通过率：{{ passRate }}%</span>
      </div>
    </div>

    <div class="testcase-grid" v-loading="loading">
      <div 
        v-for="(testcase, index) in testcases" 
        :key="index"
        class="testcase-item"
        :class="getTestcaseClass(testcase.result)"
      >
        <div class="testcase-header">
          <span class="testcase-number">#{{ testcase.case_index }}</span>
          <el-tag 
            :type="getResultTagType(testcase.result)" 
            size="small"
            effect="plain"
          >
            {{ getResultLabel(testcase.result) }}
          </el-tag>
        </div>
        
        <div class="testcase-stats">
          <div class="stat-item">
            <span class="stat-label">用时</span>
            <span class="stat-value">{{ testcase.time_used }}ms</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">内存</span>
            <span class="stat-value">{{ testcase.memory_used }}MB</span>
          </div>
        </div>

        <div v-if="testcase.message" class="testcase-message">
          <el-tooltip :content="testcase.message" placement="top">
            <span class="message-text">{{ truncateMessage(testcase.message) }}</span>
          </el-tooltip>
        </div>

        <div class="testcase-version">
          <span>数据版本：v{{ testcase.dataset_version }}</span>
        </div>
      </div>
    </div>

    <div v-if="!testcases.length && !loading" class="empty-state">
      <el-empty description="暂无测试点数据" />
    </div>

    <!-- 详细统计 -->
    <div v-if="testcases.length" class="detailed-stats">
      <el-card>
        <template #header>
          <span>统计信息</span>
        </template>
        <el-row :gutter="24">
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ testcases.length }}</div>
              <div class="stat-label">总测试点</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number passed">{{ passedCount }}</div>
              <div class="stat-label">通过</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ maxTime }}ms</div>
              <div class="stat-label">最大用时</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-number">{{ maxMemory }}MB</div>
              <div class="stat-label">最大内存</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getSubmissionTestcases } from '@/api/submission';

interface TestCase {
  case_index: number;
  dataset_version: number;
  result: string;
  time_used: number;
  memory_used: number;
  message: string;
}

interface Props {
  submissionId: number;
}

const props = defineProps<Props>();

const testcases = ref<TestCase[]>([]);
const loading = ref(false);

const passedCount = computed(() => 
  testcases.value.filter(tc => tc.result === 'AC').length
);

const passRate = computed(() => {
  if (!testcases.value.length) return 0;
  return Math.round((passedCount.value / testcases.value.length) * 100);
});

const overallResult = computed(() => {
  return passedCount.value === testcases.value.length ? 'AC' : 'FAILED';
});

const maxTime = computed(() => 
  Math.max(...testcases.value.map(tc => tc.time_used), 0)
);

const maxMemory = computed(() => 
  Math.max(...testcases.value.map(tc => tc.memory_used), 0)
);

const fetchTestcases = async () => {
  if (!props.submissionId) return;
  
  loading.value = true;
  try {
    const res = await getSubmissionTestcases(props.submissionId);
    // 处理 API 响应数据
    if (res.data && Array.isArray(res.data)) {
      testcases.value = res.data;
    } else {
      testcases.value = [];
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('获取测试点结果失败');
  } finally {
    loading.value = false;
  }
};

const getResultLabel = (result: string) => {
  const map: Record<string, string> = {
    'AC': 'AC',
    'WA': 'WA',
    'TLE': 'TLE',
    'MLE': 'MLE',
    'RE': 'RE',
    'CE': 'CE',
    'PE': 'PE',
  };
  return map[result] || result;
};

const getResultTagType = (result: string) => {
  const map: Record<string, 'success' | 'danger' | 'warning' | 'info'> = {
    'AC': 'success',
    'WA': 'danger',
    'TLE': 'warning',
    'MLE': 'warning',
    'RE': 'danger',
    'CE': 'danger',
    'PE': 'warning',
  };
  return map[result] || 'info';
};

const getTestcaseClass = (result: string) => {
  return {
    'testcase-passed': result === 'AC',
    'testcase-failed': result !== 'AC',
  };
};

const truncateMessage = (message: string) => {
  if (!message) return '';
  return message.length > 50 ? message.substring(0, 50) + '...' : message;
};

onMounted(fetchTestcases);
</script>

<style scoped>
.testcase-results {
  padding: 16px 0;
}

.results-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.results-header h4 {
  margin: 0;
  color: var(--text-primary);
  font-size: 18px;
}

.summary-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.pass-rate {
  color: var(--text-secondary);
  font-size: 14px;
}

.testcase-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.testcase-item {
  background: var(--bg-canvas-inset);
  border: 1px solid var(--border-default);
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s ease;
}

.testcase-item:hover {
  border-color: var(--accent-primary);
  transform: translateY(-2px);
}

.testcase-item.testcase-passed {
  border-left: 4px solid var(--success-emphasis);
}

.testcase-item.testcase-failed {
  border-left: 4px solid var(--danger-emphasis);
}

.testcase-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.testcase-number {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 16px;
}

.testcase-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 12px;
}

.stat-value {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 14px;
}

.testcase-message {
  margin-bottom: 8px;
}

.message-text {
  color: var(--danger-fg);
  font-size: 12px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  cursor: help;
}

.testcase-version {
  color: var(--text-tertiary);
  font-size: 11px;
  text-align: right;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.detailed-stats {
  margin-top: 24px;
}

.stat-card {
  text-align: center;
  padding: 16px;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.stat-number.passed {
  color: var(--success-fg);
}

.stat-card .stat-label {
  color: var(--text-secondary);
  font-size: 14px;
}

@media (max-width: 768px) {
  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .testcase-grid {
    grid-template-columns: 1fr;
  }

  .detailed-stats :deep(.el-col) {
    margin-bottom: 16px;
  }
}
</style>
