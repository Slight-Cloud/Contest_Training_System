<template>
  <div class="solution-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">题解管理</h2>
        <p class="page-header__subtitle">管理题目的解题报告和题解</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" :icon="Plus" @click="showCreateDialog">发布题解</el-button>
        <el-button text type="primary" @click="goBack">返回题目</el-button>
      </div>
    </div>

    <!-- 题解列表 -->
    <el-card v-loading="loading" class="page-card">
      <template v-if="solutionList.length > 0">
        <div class="solution-list">
          <div
            v-for="solution in solutionList"
            :key="solution.reportId"
            class="solution-item"
          >
            <div class="solution-item__header">
              <div class="solution-title-area">
                <h3 class="solution-title">{{ solution.title }}</h3>
                <el-tag v-if="!solution.isPublished" type="info" size="small">未发布</el-tag>
                <el-tag v-else type="success" size="small">已发布</el-tag>
              </div>
              <div class="solution-meta">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ solution.creatorName || '匿名用户' }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(solution.createdAt) }}
                </span>
              </div>
            </div>
            <!-- 内容预览 -->
            <div class="solution-item__preview" v-if="solution.content">
              <MarkdownRenderer :content="getPreviewContent(solution.content)" />
            </div>
            <div class="solution-item__actions">
              <el-button
                text
                type="primary"
                :icon="View"
                @click="viewSolution(solution)"
              >
                查看详情
              </el-button>
              <el-button
                v-if="canEdit(solution)"
                text
                type="warning"
                :icon="Edit"
                @click="editSolution(solution)"
              >
                编辑
              </el-button>
              <el-button
                v-if="canEdit(solution)"
                text
                type="danger"
                :icon="Delete"
                @click="deleteSolution(solution)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <el-empty
        v-else
        description="暂无题解"
        :image-size="120"
      >
        <el-button type="primary" :icon="Plus" @click="showCreateDialog">
          发布第一篇题解
        </el-button>
      </el-empty>
    </el-card>

    <!-- 创建/编辑题解对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑题解' : '发布题解'"
      width="90%"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="题解标题" prop="title">
          <el-input
            v-model="formData.title"
            placeholder="请输入题解标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="题解内容" prop="content">
          <div class="editor-container">
            <el-tabs v-model="editorTab" class="editor-tabs">
              <el-tab-pane label="编辑" name="edit">
                <el-input
                  v-model="formData.content"
                  type="textarea"
                  :rows="20"
                  placeholder="请输入题解内容，支持 Markdown 格式"
                  class="markdown-editor"
                />
              </el-tab-pane>
              <el-tab-pane label="预览" name="preview">
                <div class="markdown-preview">
                  <MarkdownRenderer :content="formData.content" />
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-form-item>

        <el-form-item label="发布状态" prop="isPublished">
          <el-switch
            v-model="formData.isPublished"
            :active-value="1"
            :inactive-value="0"
            active-text="立即发布"
            inactive-text="保存草稿"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitSolution">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看题解详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="viewingSolution?.title"
      width="80%"
      destroy-on-close
    >
      <div v-if="viewingSolution" class="solution-detail">
        <div class="solution-detail__meta">
          <span class="meta-item">
            <el-icon><User /></el-icon>
            作者：{{ viewingSolution.creatorName || '匿名用户' }}
          </span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            发布时间：{{ formatDate(viewingSolution.createdAt) }}
          </span>
          <el-tag v-if="!viewingSolution.isPublished" type="info" size="small">
            未发布
          </el-tag>
        </div>
        <el-divider />
        <div class="solution-detail__content">
          <MarkdownRenderer :content="viewingSolution.content" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import { 
  Plus, 
  Edit, 
  Delete, 
  View, 
  User, 
  Clock 
} from '@element-plus/icons-vue';
import { 
  getSolutionList, 
  publishSolutionReport, 
  updateSolutionReport, 
  deleteSolutionReport 
} from '@/api/problem';
import { useUserStore } from '@/store/user';
import type { SolutionReport, SolutionReportPayload, SolutionUpdatePayload } from '@/types';
import dayjs from 'dayjs';
import MarkdownRenderer from '@/components/MarkdownRenderer.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const problemId = computed(() => {
  const idParam = route.params.id;
  return Array.isArray(idParam) ? Number(idParam[0]) : Number(idParam);
});

// 数据状态
const loading = ref(false);
const solutionList = ref<SolutionReport[]>([]);

// 对话框状态
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editorTab = ref<'edit' | 'preview'>('edit');

// 表单相关
const formRef = ref<FormInstance>();
// currentEditingId 用于存储当前编辑的题解ID
const currentEditingId = ref<number | null>(null);

const formData = reactive<SolutionReportPayload>({
  problemId: problemId.value,
  title: '',
  content: '',
  isPublished: 1,
});

const formRules: FormRules = {
  title: [
    { required: true, message: '请输入题解标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度在 5 到 200 个字符', trigger: 'blur' },
  ],
  content: [
    { required: true, message: '请输入题解内容', trigger: 'blur' },
    { min: 20, message: '内容至少需要 20 个字符', trigger: 'blur' },
  ],
};

// 查看的题解
const viewingSolution = ref<SolutionReport | null>(null);

// 获取题解列表
const fetchSolutionList = async () => {
  loading.value = true;
  try {
    // 这里暂时获取所有题解，或者默认第一页的一定量
    const res = await getSolutionList(problemId.value, { page: 1, pageSize: 100 });
    if (res && res.code === 1 && res.data) {
      // 后端返回的是分页对象 { total, list }
      solutionList.value = res.data?.list || [];
    } else {
      ElMessage.error(res?.msg || '获取题解列表失败');
    }
  } catch (error) {
    console.error('获取题解列表失败:', error);
    ElMessage.error('获取题解列表失败');
  } finally {
    loading.value = false;
  }
};

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false;
  currentEditingId.value = null;
  formData.problemId = problemId.value;
  formData.title = '';
  formData.content = '';
  formData.isPublished = 1;
  dialogVisible.value = true;
  editorTab.value = 'edit';
};

// 查看题解详情
const viewSolution = (solution: SolutionReport) => {
  viewingSolution.value = solution;
  viewDialogVisible.value = true;
};

// 编辑题解
const editSolution = (solution: SolutionReport) => {
  isEdit.value = true;
  currentEditingId.value = solution.reportId;
  formData.problemId = problemId.value;
  formData.title = solution.title;
  formData.content = solution.content;
  formData.isPublished = solution.isPublished;
  dialogVisible.value = true;
  editorTab.value = 'edit';
};

// 删除题解
const deleteSolution = async (solution: SolutionReport) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除题解"${solution.title}"吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    loading.value = true;
    const res = await deleteSolutionReport(solution.reportId);
    if (res && res.code === 1) {
      ElMessage.success('删除成功');
      await fetchSolutionList();
    } else {
      ElMessage.error(res?.msg || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除题解失败:', error);
      ElMessage.error('删除失败');
    }
  } finally {
    loading.value = false;
  }
};

// 提交题解
const submitSolution = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    submitting.value = true;

    let res;
    if (isEdit.value && currentEditingId.value) {
      // 更新
      const updateData: SolutionUpdatePayload = {
        reportId: currentEditingId.value,
        title: formData.title,
        content: formData.content,
        isPublished: formData.isPublished
      };
      res = await updateSolutionReport(updateData);
    } else {
      // 创建
      res = await publishSolutionReport(formData);
    }

    if (res && res.code === 1) {
      ElMessage.success(isEdit.value ? '保存成功' : '发布成功');
      dialogVisible.value = false;
      await fetchSolutionList();
    } else {
      ElMessage.error(res?.msg || '操作失败');
    }
  } catch (error) {
    console.error('提交题解失败:', error);
    ElMessage.error('操作失败');
  } finally {
    submitting.value = false;
  }
};

// 判断是否可以编辑
const canEdit = (solution: SolutionReport) => {
  const userRole = userStore.role;
  const userId = userStore.userId;
  // 管理员和教师可以编辑所有题解，创建者可以编辑自己的题解
  return userRole === 'ADMIN' || userRole === 'TEACHER' || solution.creatorId === userId;
};

// 获取预览内容（截取前200个字符）
const getPreviewContent = (content: string): string => {
  if (!content) return '';
  // 移除代码块，获取纯文本预览
  const cleanContent = content.replace(/```[\s\S]*?```/g, '[代码块]');
  if (cleanContent.length > 200) {
    return cleanContent.substring(0, 200) + '...';
  }
  return cleanContent;
};

// 格式化日期
const formatDate = (dateStr: string) => {
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm');
};

// 返回题目详情
const goBack = () => {
  router.push({ name: 'ProblemDetail', params: { id: problemId.value } });
};

// 初始化
onMounted(() => {
  fetchSolutionList();
});
</script>

<style scoped>
.solution-page {
  gap: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 题解列表样式 */
.solution-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.solution-item {
  padding: 20px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  transition: all 0.3s;
}

.solution-item:hover {
  border-color: var(--el-color-primary);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.solution-item__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.solution-item__preview {
  padding: 12px;
  margin-bottom: 12px;
  background-color: var(--bg-canvas-inset, #161b22);
  border-radius: 6px;
  max-height: 120px;
  overflow: hidden;
  position: relative;
}

.solution-item__preview::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  background: linear-gradient(transparent, var(--bg-canvas-inset, #161b22));
  pointer-events: none;
}

.solution-title-area {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.solution-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.solution-meta {
  display: flex;
  gap: 20px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.solution-item__actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
}

/* 编辑器样式 */
.editor-container {
  width: 100%;
}

.editor-tabs {
  width: 100%;
}

.markdown-editor {
  font-family: 'Courier New', Consolas, Monaco, monospace;
}

.markdown-preview {
  min-height: 400px;
  padding: 20px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 4px;
  overflow-y: auto;
  max-height: 600px;
}

/* 题解详情样式 */
.solution-detail__meta {
  display: flex;
  gap: 24px;
  align-items: center;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 16px;
}

.solution-detail__content {
  padding: 20px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 8px;
  min-height: 300px;
}
</style>
