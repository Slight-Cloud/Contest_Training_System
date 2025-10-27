<template>
  <div class="training-plan-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">{{ canManage ? '训练计划管理' : '训练计划列表' }}</h2>
        <p class="page-header__subtitle">{{ canManage ? '管理和查看训练计划' : '浏览我的训练计划' }}</p>
      </div>
      <div v-if="canManage" class="header-actions">
        <el-button type="primary" @click="openPlanDialog()">
          <el-icon><Plus /></el-icon>
          新建计划
        </el-button>
      </div>
    </div>

    <el-card class="page-card">
      <!-- 筛选栏 -->
      <el-form :inline="true" class="filter-form">
        <el-form-item label="关键字">
          <el-input v-model="filters.keyword" placeholder="计划标题" clearable @keyup.enter="handleSearch" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable @change="handleSearch" style="width: 120px;">
            <el-option label="未开始" value="SCHEDULED" />
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="ENDED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 训练计划列表 -->
      <el-table :data="planList" v-loading="loading" border class="plan-table">
        <el-table-column prop="planId" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="计划名称" min-width="200">
          <template #default="{ row }">
            <div class="plan-title-cell">
              <span class="plan-title">{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="时间范围" width="180">
          <template #default="{ row }">
            <div class="time-range">
              <div>{{ formatDate(row.startTime) }}</div>
              <div>{{ formatDate(row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="stateTagType(row.status)">
              {{ stateLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contestCount" label="赛事数" width="100" align="center" />
        <el-table-column prop="studentCount" label="学生数" width="100" align="center" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button size="small" text type="primary" @click="viewDetail(row.planId)">查看</el-button>
              <el-button v-if="canManage && row.status === 'SCHEDULED'" size="small" text type="warning" @click="openPlanDialog(row)">
                编辑
              </el-button>
              <el-popconfirm
                v-if="canManage && row.status === 'SCHEDULED'"
                title="确定删除该训练计划？"
                confirm-button-text="删除"
                cancel-button-text="取消"
                confirm-button-type="danger"
                @confirm="handleDelete(row.planId)"
              >
                <template #reference>
                  <el-button size="small" text type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="table-footer">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :current-page="pagination.page"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 30, 50]"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新建/编辑训练计划对话框 -->
    <el-dialog
      v-model="planDialog.visible"
      :title="planDialog.title"
      width="600px"
      destroy-on-close
      @closed="onPlanDialogClosed"
    >
      <el-form :model="planDialog.form" label-width="100px" :rules="planRules" ref="planFormRef">
        <el-form-item label="计划名称" prop="title">
          <el-input v-model="planDialog.form.title" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="计划说明" prop="description">
          <el-input
            v-model="planDialog.form.description"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="planDialog.form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="planDialog.form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="选择赛事" prop="contestIds">
          <el-select
            v-model="planDialog.form.contestIds"
            multiple
            filterable
            placeholder="请选择赛事"
            style="width: 100%;"
          >
            <el-option
              v-for="contest in availableContests"
              :key="contest.contestId"
              :label="contest.title"
              :value="contest.contestId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择学生" prop="studentIds">
          <el-select
            v-model="planDialog.form.studentIds"
            multiple
            filterable
            placeholder="请选择学生"
            style="width: 100%;"
          >
            <el-option
              v-for="student in availableStudents"
              :key="student.userId"
              :label="`${student.nickname} (${student.studentId})`"
              :value="student.userId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="planDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handlePlanSubmit" :loading="planDialog.submitting">
          {{ planDialog.isEdit ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import {
  getTrainingPlanList,
  createTrainingPlan,
  updateTrainingPlan,
  deleteTrainingPlan,
} from '@/api/training';
import { getContestList } from '@/api/contest';
import { getUserList } from '@/api/user';
import type { TrainingPlan, TrainingPlanPayload } from '@/types';
import { useUserStore } from '@/store/user';

const router = useRouter();
const userStore = useUserStore();

const planList = ref<TrainingPlan[]>([]);
const loading = ref(false);

const filters = reactive({
  keyword: '',
  status: '',
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
});

const isAdmin = computed(() => userStore.role === 'ADMIN');
const isTeacher = computed(() => userStore.role === 'TEACHER');
const canManage = computed(() => isAdmin.value || isTeacher.value);

const planDialog = reactive({
  visible: false,
  title: '新建训练计划',
  isEdit: false,
  submitting: false,
  form: {
    planId: undefined as number | undefined,
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    contestIds: [] as number[],
    studentIds: [] as number[],
  },
});

const planFormRef = ref<FormInstance>();
const availableContests = ref<any[]>([]);
const availableStudents = ref<any[]>([]);

const planRules: FormRules = {
  title: [
    { required: true, message: '请输入计划名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' },
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' },
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
  ],
  contestIds: [
    { required: true, type: 'array', min: 1, message: '请至少选择一个赛事', trigger: 'change' },
  ],
  studentIds: [
    { required: true, type: 'array', min: 1, message: '请至少选择一个学生', trigger: 'change' },
  ],
};

const stateLabel = (value?: string) => {
  const map: Record<string, string> = {
    SCHEDULED: '未开始',
    ONGOING: '进行中',
    ENDED: '已结束',
  };
  return map[value || ''] || '未知';
};

const stateTagType = (value?: string) => {
  const map: Record<string, 'info' | 'success' | 'danger' | 'warning'> = {
    SCHEDULED: 'info',
    ONGOING: 'success',
    ENDED: 'warning',
  };
  return map[value || ''] || 'info';
};

const formatDate = (value?: string) => {
  if (!value) return '-';
  return value.replace('T', ' ').substring(0, 16);
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
  status: filters.status || undefined,
    };
    const res = await getTrainingPlanList(params);
    const data = (res.data as any) || {};
    
    planList.value = (data.plans || data.list || []) as TrainingPlan[];
    pagination.total = Number(data.total) || 0;
    pagination.page = Number(data.page) || params.page || 1;
    pagination.pageSize = Number(data.pageSize) || params.pageSize || 10;
  } catch (error: any) {
    console.error('获取训练计划列表失败:', error);
    ElMessage.error(error?.message || '获取训练计划列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  fetchData();
};

const resetFilters = () => {
  filters.keyword = '';
  filters.status = '';
  handleSearch();
};

const handlePageChange = (page: number) => {
  pagination.page = page;
  fetchData();
};

const handlePageSizeChange = (size: number) => {
  pagination.pageSize = size;
  pagination.page = 1;
  fetchData();
};

const viewDetail = (planId: number) => {
  router.push({ name: 'TrainingPlanDetail', params: { id: planId } });
};

const openPlanDialog = async (plan?: TrainingPlan) => {
  planDialog.isEdit = !!plan;
  planDialog.title = plan ? '编辑训练计划' : '新建训练计划';
  
  if (plan) {
    planDialog.form.planId = plan.planId;
    planDialog.form.title = plan.title;
    planDialog.form.description = plan.description || '';
    planDialog.form.startTime = plan.startTime;
    planDialog.form.endTime = plan.endTime;
    planDialog.form.contestIds = plan.contests?.map(c => c.contestId) || [];
    planDialog.form.studentIds = plan.students?.map(s => s.userId) || [];
  }
  
  // 加载可用的赛事和学生
  await loadAvailableData();
  planDialog.visible = true;
};

const loadAvailableData = async () => {
  try {
    // 获取赛事列表
    const contestRes = await getContestList({ page: 1, pageSize: 100 });
    const contestData = contestRes.data as any;
    availableContests.value = contestData?.contests || contestData?.list || [];

    // 获取学生列表
    const userRes = await getUserList({ page: 1, pageSize: 500, role: 'STUDENT' });
    const userData = userRes.data as any;
    availableStudents.value = userData?.users || userData?.list || [];
  } catch (error) {
    console.error('加载数据失败:', error);
  }
};

const handlePlanSubmit = async () => {
  if (!planFormRef.value) return;
  
  await planFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    planDialog.submitting = true;
    try {
      const payload: TrainingPlanPayload = {
        title: planDialog.form.title,
        description: planDialog.form.description,
        startTime: planDialog.form.startTime,
        endTime: planDialog.form.endTime,
        contestIds: planDialog.form.contestIds,
        studentIds: planDialog.form.studentIds,
      };

      if (planDialog.isEdit && planDialog.form.planId) {
        payload.planId = planDialog.form.planId;
        await updateTrainingPlan(payload);
        ElMessage.success('训练计划更新成功');
      } else {
        await createTrainingPlan(payload);
        ElMessage.success('训练计划创建成功');
      }
      
      planDialog.visible = false;
      fetchData();
    } catch (error: any) {
      console.error('保存训练计划失败:', error);
      ElMessage.error(error?.message || '保存训练计划失败');
    } finally {
      planDialog.submitting = false;
    }
  });
};

const handleDelete = async (planId: number) => {
  try {
    await deleteTrainingPlan(planId);
    ElMessage.success('训练计划删除成功');
    fetchData();
  } catch (error: any) {
    console.error('删除训练计划失败:', error);
    ElMessage.error(error?.message || '删除训练计划失败');
  }
};

const onPlanDialogClosed = () => {
  planDialog.form = {
    planId: undefined,
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    contestIds: [],
    studentIds: [],
  };
  planFormRef.value?.clearValidate();
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.training-plan-page {
  padding: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-form {
  margin-bottom: 16px;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 12px 8px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 表格样式 - 深灰色边框 */
.plan-table {
  margin-bottom: 16px;
  border-color: var(--border-default) !important;
}

.plan-table :deep(.el-table__inner-wrapper::before),
.plan-table :deep(.el-table__border-left-patch) {
  background-color: var(--border-default) !important;
}

.plan-table :deep(td),
.plan-table :deep(th),
.plan-table :deep(.el-table__cell) {
  border-color: var(--border-default) !important;
}

.plan-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.plan-title {
  word-break: break-word;
}

.time-range {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  line-height: 1.5;
  white-space: nowrap;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .filter-form {
    display: grid;
    grid-template-columns: repeat(1, minmax(0, 1fr));
    gap: 12px;
  }

  .filter-form :deep(.el-form-item) {
    margin-right: 0 !important;
  }

  .plan-table {
    overflow-x: auto;
  }
}
</style>
