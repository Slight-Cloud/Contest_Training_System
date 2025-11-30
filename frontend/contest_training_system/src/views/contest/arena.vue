<template>
  <div class="arena-page page-shell">
    <div class="page-header">
      <h2 class="page-header__title">赛事答题</h2>
      <p class="page-header__subtitle">赛事进行中</p>
    </div>

    <el-card class="page-card">
      <div class="dev-placeholder">
        <el-icon size="64" class="placeholder-icon"><InfoFilled /></el-icon>
        <h3>功能开发中</h3>
        <p>赛事答题功能正在紧张开发中，敬请期待。</p>
        <el-button type="primary" @click="goBack">返回上一页</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { InfoFilled } from '@element-plus/icons-vue';

const router = useRouter();

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.arena-page {
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
</style>

