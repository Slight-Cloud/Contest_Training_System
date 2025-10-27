<template>
  <div class="submit-page page-shell">
    <!-- 页面头部 -->
    <div class="submit-header">
      <div class="submit-header__info">
        <h1 class="submit-title">提交代码</h1>
        <div class="submit-meta">
          <span v-if="contestTitle" class="meta-item">
            <el-icon><Trophy /></el-icon>
            {{ contestTitle }}
          </span>
          <span class="meta-item">
            <el-icon><Document /></el-icon>
            {{ problemTitle || `题目 #${problemId}` }}
          </span>
        </div>
      </div>
      <div class="submit-header__actions">
        <el-button @click="goBack">返回</el-button>
        <el-button type="info" plain @click="viewProblem">查看题目</el-button>
      </div>
    </div>

    <el-card class="page-card submit-card">
      <!-- 语言和编译器选择 -->
      <div class="submit-config">
        <div class="config-item">
          <label class="config-label">编程语言</label>
          <el-select
            v-model="selectedLanguage"
            placeholder="选择编程语言"
            style="width: 200px"
            @change="handleLanguageChange"
          >
            <el-option
              v-for="lang in availableLanguages"
              :key="lang.value"
              :label="lang.label"
              :value="lang.value"
            >
              <span>{{ lang.label }}</span>
              <span class="lang-version">{{ lang.version }}</span>
            </el-option>
          </el-select>
        </div>

        <div class="config-item">
          <label class="config-label">编辑器主题</label>
          <el-select v-model="editorTheme" style="width: 150px">
            <el-option label="暗色主题" value="vs-dark" />
            <el-option label="亮色主题" value="vs-light" />
          </el-select>
        </div>

        <div class="config-stats">
          <span class="stat-item">代码长度: {{ codeLength }} 字符</span>
          <span class="stat-item">行数: {{ codeLines }}</span>
        </div>
      </div>

      <!-- 代码编辑器 -->
      <div class="editor-container">
        <CodeEditor
          ref="editorRef"
          v-model="code"
          :language="selectedLanguage"
          :theme="editorTheme"
          height="600px"
          @change="handleCodeChange"
        />
      </div>

      <!-- 提交按钮区 -->
      <div class="submit-actions">
        <el-button
          type="primary"
          size="large"
          :loading="submitting"
          :disabled="!code.trim() || submitting"
          @click="handleSubmit"
        >
          <el-icon v-if="!submitting"><Upload /></el-icon>
          {{ submitting ? '提交中...' : '提交代码' }}
        </el-button>
        <el-button size="large" @click="handleReset">清空代码</el-button>
        <el-button size="large" @click="loadTemplate">加载模板</el-button>
      </div>

      <!-- 提交结果 -->
      <el-alert
        v-if="submitResult"
        :type="getResultType(submitResult.result)"
        :closable="false"
        class="submit-result"
      >
        <template #title>
          <div class="result-title">
            <span class="result-status">
              {{ getStatusLabel(submitResult.result, 'submission') }}
            </span>
            <div class="result-stats">
              <span>用时: {{ submitResult.timeUsed }}ms</span>
              <span>内存: {{ submitResult.memoryUsed }}MB</span>
            </div>
          </div>
        </template>
        <div class="result-actions">
          <el-button
            type="primary"
            text
            size="small"
            @click="viewSubmissionDetail"
          >
            查看详情
          </el-button>
          <el-button type="info" text size="small" @click="submitAnother">
            再次提交
          </el-button>
        </div>
      </el-alert>
    </el-card>

    <!-- 代码模板对话框 -->
    <el-dialog
      v-model="templateDialog.visible"
      title="选择代码模板"
      width="600px"
    >
      <el-radio-group v-model="templateDialog.selected" class="template-list">
        <el-radio
          v-for="template in codeTemplates"
          :key="template.id"
          :label="template.id"
          class="template-item"
        >
          <div class="template-info">
            <div class="template-name">{{ template.name }}</div>
            <div class="template-desc">{{ template.description }}</div>
          </div>
        </el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="templateDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="applyTemplate">应用模板</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Trophy, Document, Upload } from '@element-plus/icons-vue';
import { submitCode } from '@/api/submission';
import { getStatusLabel } from '@/utils/helpers';
import CodeEditor from '@/components/CodeEditor.vue';

const route = useRoute();
const router = useRouter();

const problemId = Number(route.params.id);
const contestId = route.query.contestId ? Number(route.query.contestId) : null;
const problemTitle = route.query.problemTitle as string | undefined;
const contestTitle = route.query.contestTitle as string | undefined;

const editorRef = ref<InstanceType<typeof CodeEditor> | null>(null);
const code = ref('');
const selectedLanguage = ref('cpp');
const editorTheme = ref<'vs-dark' | 'vs-light'>('vs-dark');
const submitting = ref(false);
const submitResult = ref<{
  result: string;
  timeUsed: number;
  memoryUsed: number;
  submissionId?: number;
} | null>(null);

// 可用的编程语言
const availableLanguages = [
  { label: 'C', value: 'c', version: 'gcc 11.2.0' },
  { label: 'C++', value: 'cpp', version: 'g++ 11.2.0' },
  { label: 'Java', value: 'java', version: 'OpenJDK 17' },
  { label: 'Python', value: 'python', version: 'Python 3.10' },
  { label: 'Go', value: 'go', version: 'Go 1.20' },
  { label: 'JavaScript', value: 'javascript', version: 'Node.js 18' },
];

// 代码统计
const codeLength = computed(() => code.value.length);
const codeLines = computed(() => code.value.split('\n').length);

// 代码模板
const templateDialog = ref({
  visible: false,
  selected: 'basic',
});

const codeTemplates = computed(() => {
  const templates: Record<string, any[]> = {
    cpp: [
      {
        id: 'basic',
        name: 'C++ 基础模板',
        description: '包含常用头文件和主函数',
        code: `#include <iostream>
using namespace std;

int main() {
    // 在这里编写代码
    
    return 0;
}`,
      },
      {
        id: 'competitive',
        name: 'C++ 竞赛模板',
        description: '包含常用宏定义和快速IO',
        code: `#include <bits/stdc++.h>
using namespace std;

#define ll long long
#define pii pair<int, int>
#define vi vector<int>
#define all(x) (x).begin(), (x).end()

int main() {
    ios::sync_with_stdio(false);
    cin.tie(0);
    
    // 在这里编写代码
    
    return 0;
}`,
      },
    ],
    c: [
      {
        id: 'basic',
        name: 'C 基础模板',
        description: '包含常用头文件和主函数',
        code: `#include <stdio.h>

int main() {
    // 在这里编写代码
    
    return 0;
}`,
      },
    ],
    java: [
      {
        id: 'basic',
        name: 'Java 基础模板',
        description: 'Main 类模板',
        code: `import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 在这里编写代码
        
        sc.close();
    }
}`,
      },
    ],
    python: [
      {
        id: 'basic',
        name: 'Python 基础模板',
        description: '包含常用导入',
        code: `# -*- coding: utf-8 -*-

def main():
    # 在这里编写代码
    pass

if __name__ == '__main__':
    main()`,
      },
    ],
  };

  return templates[selectedLanguage.value] || [];
});

// 处理语言切换
const handleLanguageChange = () => {
  // 可以在这里添加语言切换提示
};

// 处理代码变化
const handleCodeChange = (value: string) => {
  code.value = value;
};

// 提交代码
const handleSubmit = async () => {
  if (!code.value.trim()) {
    ElMessage.warning('请输入代码');
    return;
  }

  if (!contestId) {
    ElMessage.warning('缺少赛事信息');
    return;
  }

  submitting.value = true;
  submitResult.value = null;

  try {
    const lang = availableLanguages.find((l) => l.value === selectedLanguage.value);
    const res = await submitCode({
      contestId: contestId,
      problemId: problemId,
      code: code.value,
      language: lang?.label || selectedLanguage.value,
      compiler: lang?.version || selectedLanguage.value,
    });

    const responseData = res.data as any;
    submitResult.value = {
      result: responseData?.result || 'Pending',
      timeUsed: responseData?.timeUsed || 0,
      memoryUsed: responseData?.memoryUsed || 0,
      submissionId: responseData?.submissionId,
    };

    ElMessage.success('提交成功');
  } catch (error: any) {
    ElMessage.error(error?.message || '提交失败');
  } finally {
    submitting.value = false;
  }
};

// 重置代码
const handleReset = async () => {
  if (code.value.trim()) {
    try {
      await ElMessageBox.confirm('确定要清空代码吗？', '提示', {
        type: 'warning',
      });
      code.value = '';
      submitResult.value = null;
      ElMessage.success('已清空');
    } catch {
      // 用户取消
    }
  }
};

// 加载模板
const loadTemplate = () => {
  if (codeTemplates.value.length === 0) {
    ElMessage.info(`${selectedLanguage.value} 暂无代码模板`);
    return;
  }

  templateDialog.value.visible = true;
  templateDialog.value.selected = 'basic';
};

// 应用模板
const applyTemplate = () => {
  const template = codeTemplates.value.find(
    (t) => t.id === templateDialog.value.selected
  );
  if (template) {
    code.value = template.code;
    templateDialog.value.visible = false;
    ElMessage.success('模板已加载');
    editorRef.value?.focus();
  }
};

// 查看提交详情
const viewSubmissionDetail = () => {
  if (submitResult.value?.submissionId) {
    router.push({
      name: 'SubmissionDetail',
      params: { id: submitResult.value.submissionId },
    });
  }
};

// 再次提交
const submitAnother = () => {
  submitResult.value = null;
  editorRef.value?.focus();
};

// 返回
const goBack = () => {
  if (contestId) {
    router.push({
      name: 'ContestDetail',
      params: { id: contestId },
    });
  } else {
    router.push({
      name: 'ProblemDetail',
      params: { id: problemId },
    });
  }
};

// 查看题目
const viewProblem = () => {
  router.push({
    name: 'ProblemDetail',
    params: { id: problemId },
    query: contestId ? { contestId: contestId } : {},
  });
};

// 获取结果类型
const getResultType = (result: string): 'success' | 'error' | 'warning' | 'info' => {
  if (result === 'AC') return 'success';
  if (result === 'WA' || result === 'RE' || result === 'CE') return 'error';
  if (result === 'TLE' || result === 'MLE') return 'warning';
  return 'info';
};

onMounted(() => {
  // 从 localStorage 加载上次的语言选择
  const savedLang = localStorage.getItem('last_language');
  if (savedLang && availableLanguages.some((l) => l.value === savedLang)) {
    selectedLanguage.value = savedLang;
  }
});
</script>

<style scoped>
.submit-page {
  gap: 24px;
}

.submit-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.submit-header__info {
  flex: 1;
  min-width: 300px;
}

.submit-title {
  margin: 0 0 12px;
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
}

.submit-meta {
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

.submit-header__actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.submit-card {
  padding: 24px;
}

.submit-config {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.config-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.config-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.lang-version {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-left: 8px;
}

.config-stats {
  margin-left: auto;
  display: flex;
  gap: 16px;
}

.stat-item {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.editor-container {
  margin-bottom: 24px;
}

.submit-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.submit-result {
  margin-top: 24px;
}

.result-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.result-status {
  font-size: 18px;
  font-weight: 600;
}

.result-stats {
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.result-actions {
  margin-top: 12px;
  display: flex;
  gap: 12px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.template-item {
  padding: 16px;
  border: 1px solid var(--border-default);
  border-radius: 8px;
  transition: all 0.2s ease;
}

.template-item:hover {
  background: var(--bg-canvas-subtle);
  border-color: var(--accent-primary);
}

.template-info {
  margin-left: 8px;
}

.template-name {
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 4px;
}

.template-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

@media (max-width: 768px) {
  .submit-title {
    font-size: 20px;
  }

  .submit-header {
    flex-direction: column;
  }

  .submit-header__actions {
    width: 100%;
  }

  .submit-config {
    flex-direction: column;
    align-items: flex-start;
  }

  .config-stats {
    margin-left: 0;
    width: 100%;
    justify-content: space-between;
  }
}
</style>
