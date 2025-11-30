<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">🚧</div>
      <h1 class="error-title">功能开发中</h1>
      <p class="error-description">该功能正在开发中，敬请期待。</p>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  router.push({ name: 'Dashboard' });
};

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-canvas);
  color: var(--text-primary);
  padding: 40px;
}

.error-content {
  text-align: center;
  max-width: 600px;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  color: var(--accent-primary);
  line-height: 1;
  margin-bottom: 24px;
  text-shadow: 0 4px 12px rgba(88, 166, 255, 0.3);
}

.error-title {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px;
}

.error-description {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0 0 32px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .error-code {
    font-size: 80px;
  }

  .error-title {
    font-size: 24px;
  }

  .error-description {
    font-size: 14px;
  }

  .error-actions {
    flex-direction: column;
  }

  .error-actions .el-button {
    width: 100%;
  }
}
</style>

