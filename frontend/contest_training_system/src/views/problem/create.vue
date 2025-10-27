<template>
  <div class="problem-create-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">{{ isEdit ? '编辑题目' : '新建题目' }}</h2>
        <p class="page-header__subtitle">{{ isEdit ? '修改题目信息和测试数据' : '创建新的题目，设置题目描述和测试数据' }}</p>
      </div>
      <div class="header-actions">
        <el-button text type="primary" @click="goBack">返回</el-button>
        <el-button type="success" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '创建题目' }}
        </el-button>
      </div>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="problem-form">
      <el-card class="page-card">
        <template #header>
          <span>基本信息</span>
        </template>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="题目标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入题目标题" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="时间限制" prop="timeLimit">
              <el-input-number v-model="form.timeLimit" :min="100" :max="10000" :step="100" />
              <span class="unit-text">ms</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="内存限制" prop="memoryLimit">
              <el-input-number v-model="form.memoryLimit" :min="16" :max="1024" :step="16" />
              <span class="unit-text">MB</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="题目描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="6" 
            placeholder="请输入题目描述，支持HTML格式"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="输入说明" prop="inputSpec">
              <el-input 
                v-model="form.inputSpec" 
                type="textarea" 
                :rows="4" 
                placeholder="请描述输入格式"
                maxlength="2000"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="输出说明" prop="outputSpec">
              <el-input 
                v-model="form.outputSpec" 
                type="textarea" 
                :rows="4" 
                placeholder="请描述输出格式"
                maxlength="2000"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="输入样例" prop="sampleInput">
              <el-input 
                v-model="form.sampleInput" 
                type="textarea" 
                :rows="4" 
                placeholder="请输入样例输入"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="输出样例" prop="sampleOutput">
              <el-input 
                v-model="form.sampleOutput" 
                type="textarea" 
                :rows="4" 
                placeholder="请输入样例输出"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="题目备注信息（可选）"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-card>

      <el-card class="page-card">
        <template #header>
          <span>测试数据</span>
        </template>
        
        <el-form-item label="测试数据包" prop="testdataZip">
          <div class="upload-section">
            <el-upload
              ref="uploadRef"
              :action="uploadAction"
              :headers="uploadHeaders"
              :before-upload="beforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :file-list="fileList"
              :limit="1"
              accept=".zip"
              drag
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将ZIP文件拖拽到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  只能上传ZIP格式文件，且不超过50MB。ZIP包应包含输入文件(.in)和输出文件(.out)
                </div>
              </template>
            </el-upload>
            
            <div v-if="form.testdataZip" class="current-file">
              <el-icon><document /></el-icon>
              <span>当前文件：{{ form.testdataZip }}</span>
            </div>
          </div>
        </el-form-item>
      </el-card>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, type FormInstance, type FormRules, type UploadInstance } from 'element-plus';
import { UploadFilled, Document } from '@element-plus/icons-vue';
import { createProblem, updateProblem, getProblemDetail, type ProblemPayload } from '@/api/problem';
import { useUserStore } from '@/store/user';
import type { Problem } from '@/types';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const formRef = ref<FormInstance>();
const uploadRef = ref<UploadInstance>();
const submitting = ref(false);

const isEdit = computed(() => route.name === 'ProblemEdit');
const problemId = computed(() => {
  const idParam = route.params.id;
  return Array.isArray(idParam) ? Number(idParam[0]) : Number(idParam);
});

const form = reactive<ProblemPayload>({
  title: '',
  description: '',
  inputSpec: '',
  outputSpec: '',
  sampleInput: '',
  sampleOutput: '',
  remark: '',
  timeLimit: 1000,
  memoryLimit: 256,
  testdataZip: '',
});

const fileList = ref([]);

const rules: FormRules = {
  title: [
    { required: true, message: '请输入题目标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度应在2-100字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入题目描述', trigger: 'blur' },
    { min: 10, max: 5000, message: '描述长度应在10-5000字符之间', trigger: 'blur' }
  ],
  inputSpec: [
    { required: true, message: '请输入输入说明', trigger: 'blur' }
  ],
  outputSpec: [
    { required: true, message: '请输入输出说明', trigger: 'blur' }
  ],
  sampleInput: [
    { required: true, message: '请输入输入样例', trigger: 'blur' }
  ],
  sampleOutput: [
    { required: true, message: '请输入输出样例', trigger: 'blur' }
  ],
  timeLimit: [
    { required: true, message: '请设置时间限制', trigger: 'blur' },
    { type: 'number', min: 100, max: 10000, message: '时间限制应在100-10000ms之间', trigger: 'blur' }
  ],
  memoryLimit: [
    { required: true, message: '请设置内存限制', trigger: 'blur' },
    { type: 'number', min: 16, max: 1024, message: '内存限制应在16-1024MB之间', trigger: 'blur' }
  ],
  testdataZip: [
    { required: !isEdit.value, message: '请上传测试数据包', trigger: 'change' }
  ]
};

const uploadAction = '/file/upload/testdata';
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}));

const fetchProblemDetail = async () => {
  if (!isEdit.value) return;
  
  try {
    const apiResponse = await getProblemDetail(problemId.value);
    // 从 ApiResponse 中提取实际的 Problem 数据
    // getProblemDetail 返回 ApiResponse<Problem>, 访问 .data 得到 Problem | undefined
    if (apiResponse && apiResponse.data) {
      const problemData = apiResponse.data as unknown as Problem;
      // 将 Problem 类型 (camelCase) 转换为表单数据 (ProblemPayload 也是 camelCase)
      Object.assign(form, {
        title: problemData.title,
        description: problemData.description,
        inputSpec: problemData.inputSpec,
        outputSpec: problemData.outputSpec,
        sampleInput: problemData.sampleInput,
        sampleOutput: problemData.sampleOutput,
        remark: problemData.remark || '',
        timeLimit: problemData.timeLimit,
        memoryLimit: problemData.memoryLimit,
        testdataZip: '',  // 测试数据不从接口获取
      });
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('获取题目信息失败');
  }
};

const beforeUpload = (file: File) => {
  const isZip = file.type === 'application/zip' || file.name.endsWith('.zip');
  const isLt50M = file.size / 1024 / 1024 < 50;

  if (!isZip) {
    ElMessage.error('只能上传ZIP格式文件!');
    return false;
  }
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB!');
    return false;
  }
  return true;
};

const handleUploadSuccess = (response: any) => {
  if (response.code === 1) {
    form.testdataZip = response.data.file_url;
    ElMessage.success('测试数据上传成功');
  } else {
    ElMessage.error(response.msg || '上传失败');
  }
};

const handleUploadError = () => {
  ElMessage.error('上传失败，请重试');
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    
    submitting.value = true;
    try {
      const data = { ...form };
      if (isEdit.value) {
        data.problemId = problemId.value;
        await updateProblem(data);
        ElMessage.success('题目修改成功');
      } else {
        await createProblem(data);
        ElMessage.success('题目创建成功');
      }
      router.push({ name: 'ProblemList' });
    } catch (error) {
      console.error(error);
    } finally {
      submitting.value = false;
    }
  });
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  if (isEdit.value) {
    fetchProblemDetail();
  }
});
</script>

<style scoped>
.problem-create-page {
  gap: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.problem-form {
  gap: 24px;
}

.unit-text {
  margin-left: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.upload-section {
  width: 100%;
}

.current-file {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

:deep(.el-upload-dragger) {
  background: var(--bg-canvas-inset);
  border: 2px dashed var(--border-default);
  border-radius: 8px;
}

:deep(.el-upload-dragger:hover) {
  border-color: var(--accent-primary);
}

:deep(.el-upload__text) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-upload__tip) {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .problem-form :deep(.el-form-item__label) {
    width: 100px !important;
  }
}
</style>