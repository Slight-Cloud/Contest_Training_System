<template>
  <div class="solution-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">题解管理</h2>
        <p class="page-header__subtitle">{{ problemTitle }} - 管理题目的解题报告和题解</p>
      </div>
      <div class="header-actions">
        <el-button text type="primary" @click="goBack">返回题目</el-button>
        <el-button v-if="canManage" type="primary" @click="openSolutionDialog()">发布题解</el-button>
      </div>
    </div>

    <el-card class="page-card">
      <el-table :data="solutionList" v-loading="loading" border class="detail-table">
        <el-table-column prop="reportId" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="题解标题" min-width="250">
          <template #default="{ row }">
            <div class="solution-title-cell">
              <span class="solution-title">{{ row.title }}</span>
              <el-tag v-if="!row.isPublished" size="small" type="warning" effect="plain">未发布</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="creatorName" label="作者" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isPublished ? 'success' : 'warning'">
              {{ row.isPublished ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button size="small" text type="primary" @click="viewSolution(row)">查看</el-button>
              <el-button v-if="canManage" size="small" text type="warning" @click="openSolutionDialog(row)">
                编辑
              </el-button>
              <el-button 
                v-if="canManage && !row.isPublished" 
                size="small" 
                text 
                type="success" 
                @click="publishSolution(row)"
              >
                发布
              </el-button>
              <el-popconfirm
                v-if="canManage"
                title="确定删除该题解？"
                confirm-button-text="删除"
                cancel-button-text="取消"
                confirm-button-type="danger"
                @confirm="deleteSolution(row.reportId)"
              >
                <template #reference>
                  <el-button size="small" text type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 题解创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑题解' : '发布题解'"
      width="80%"
      :close-on-click-modal="false"
    >
      <el-form ref="solutionFormRef" :model="solutionForm" :rules="solutionRules" label-width="100px">
        <el-form-item label="题解标题" prop="title">
          <el-input v-model="solutionForm.title" placeholder="请输入题解标题" maxlength="100" show-word-limit />
        </el-form-item>
        
        <el-form-item label="题解内容" prop="content">
          <el-input
            v-model="solutionForm.content"
            type="textarea"
            :rows="15"
            placeholder="请输入题解内容，支持Markdown格式"
            maxlength="10000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="发布状态">
          <el-radio-group v-model="solutionForm.isPublished">
            <el-radio :label="0">保存为草稿</el-radio>
            <el-radio :label="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSolutionSubmit">
          {{ editingId ? '保存修改' : '发布题解' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 题解查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="viewingSolution.title"
      width="80%"
      :close-on-click-modal="false"
    >
      <div class="solution-content">
        <div class="solution-meta">
          <el-tag type="info">作者：{{ viewingSolution.creatorName }}</el-tag>
          <el-tag type="success">{{ viewingSolution.createdAt }}</el-tag>
          <el-tag :type="viewingSolution.isPublished ? 'success' : 'warning'">
            {{ viewingSolution.isPublished ? '已发布' : '草稿' }}
          </el-tag>
        </div>
        <el-divider />
        <div class="solution-body" v-html="formatContent(viewingSolution.content)"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { getSolutionList, publishSolutionReport } from '@/api/problem';
import type { SolutionReportPayload, SolutionReport } from '@/types';
import { useUserStore } from '@/store/user';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const solutionList = ref<SolutionReport[]>([]);
const loading = ref(false);
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const submitting = ref(false);
const editingId = ref<number | null>(null);

const solutionFormRef = ref<FormInstance>();
const solutionForm = reactive<SolutionReportPayload>({
  problemId: 0,
  title: '',
  content: '',
  isPublished: 1,
});

const viewingSolution = ref<SolutionReport>({
  reportId: 0,
  problemId: 0,
  title: '',
  content: '',
  creatorId: 0,
  creatorName: '',
  isPublished: 0,
  createdAt: '',
});

const problemId = computed(() => {
  const idParam = route.params.id;
  return Array.isArray(idParam) ? Number(idParam[0]) : Number(idParam);
});

const problemTitle = computed(() => {
  return route.query.title as string || '题目';
});

const isAdmin = computed(() => userStore.role === 'ADMIN');
const isTeacher = computed(() => userStore.role === 'TEACHER');
const canManage = computed(() => isAdmin.value || isTeacher.value);


const solutionRules: FormRules = {
  title: [
    { required: true, message: '请输入题解标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度应在2-100字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入题解内容', trigger: 'blur' },
    { min: 10, max: 10000, message: '内容长度应在10-10000字符之间', trigger: 'blur' }
  ]
};

const fetchSolutionList = async () => {
  loading.value = true;
  try {
    const res = await getSolutionList(problemId.value);
    solutionList.value = (res.data as unknown as SolutionReport[]) || [];
  } catch (error) {
    console.error(error);
    ElMessage.error('获取题解列表失败');
  } finally {
    loading.value = false;
  }
};

const openSolutionDialog = (solution?: SolutionReport) => {
  if (solution) {
    editingId.value = solution.reportId;
    solutionForm.title = solution.title;
    solutionForm.content = solution.content;
    solutionForm.isPublished = solution.isPublished;
  } else {
    editingId.value = null;
    solutionForm.title = '';
    solutionForm.content = '';
    solutionForm.isPublished = 1;
  }
  solutionForm.problemId = problemId.value;
  dialogVisible.value = true;
};

const handleSolutionSubmit = async () => {
  if (!solutionFormRef.value) return;
  
  await solutionFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    submitting.value = true;
    try {
      await publishSolutionReport(solutionForm);
      ElMessage.success(editingId.value ? '题解修改成功' : '题解发布成功');
      dialogVisible.value = false;
      fetchSolutionList();
    } catch (error) {
      console.error(error);
    } finally {
      submitting.value = false;
    }
  });
};

const viewSolution = (solution: SolutionReport) => {
  viewingSolution.value = { ...solution };
  viewDialogVisible.value = true;
};

const publishSolution = async (solution: SolutionReport) => {
  try {
    await publishSolutionReport({
      problemId: problemId.value,
      title: solution.title,
      content: solution.content,
      isPublished: 1,
    });
    ElMessage.success('题解发布成功');
    fetchSolutionList();
  } catch (error) {
    console.error(error);
  }
};

const deleteSolution = async (_reportId: number) => {
  try {
    // 这里应该调用删除题解的API，暂时模拟
    ElMessage.success('题解删除成功');
    fetchSolutionList();
  } catch (error) {
    console.error(error);
  }
};

const formatContent = (content: string) => {
  if (!content) return '暂无内容';
  // 简单的Markdown格式转换
  return content
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`(.*?)`/g, '<code>$1</code>');
};

const goBack = () => {
  router.push({ name: 'ProblemDetail', params: { id: problemId.value } });
};

onMounted(fetchSolutionList);
</script>

<style scoped>
.solution-page {
  gap: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.solution-title-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.solution-title {
  font-weight: 600;
  color: #e6edf3;
}

.solution-content {
  max-height: 60vh;
  overflow-y: auto;
}

.solution-meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.solution-body {
  color: #cdd9e5;
  line-height: 1.8;
  white-space: pre-wrap;
}

.solution-body :deep(code) {
  background: rgba(13, 18, 27, 0.75);
  border: 1px solid var(--border-default);
  border-radius: 4px;
  padding: 2px 6px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
}

.solution-body :deep(strong) {
  color: #f0f6fc;
  font-weight: 600;
}

.solution-body :deep(em) {
  color: #7c8db5;
  font-style: italic;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

@media (max-width: 768px) {
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  :deep(.el-dialog) {
    width: 95% !important;
  }
}
</style>
