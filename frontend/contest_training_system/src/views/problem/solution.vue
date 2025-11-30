<template>
  <div class="solution-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">题解管理</h2>
        <p class="page-header__subtitle">管理题目的解题报告和题解</p>
      </div>
      <div class="header-actions">
        <el-button text type="primary" @click="goBack">返回题目</el-button>
      </div>
    </div>

    <el-card class="page-card">
      <div class="dev-placeholder">
        <el-icon size="64" class="placeholder-icon"><InfoFilled /></el-icon>
        <h3>功能开发中</h3>
        <p>题解管理功能正在紧张开发中，敬请期待。</p>
        <el-button type="primary" @click="goBack">返回上一页</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { InfoFilled } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();

const problemId = computed(() => {
  const idParam = route.params.id;
  return Array.isArray(idParam) ? Number(idParam[0]) : Number(idParam);
});

const goBack = () => {
  router.push({ name: 'ProblemDetail', params: { id: problemId.value } });
};
</script>

<style scoped>
.solution-page {
  gap: 24px;
}

.dev-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  min-height: 300px;
}

.dev-placeholder h3 {
  margin: 20px 0 12px;
  font-size: 24px;
  color: var(--text-primary);
  font-weight: 600;
}

.dev-placeholder p {
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 32px;
  max-width: 400px;
}

.header-actions {
  display: flex;
  gap: 12px;
}
</style>
