<template>
  <div class="contest-list-page page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-header__title">{{ canManage ? '赛事管理' : '赛事列表' }}</h2>
        <p class="page-header__subtitle">{{ canManage ? '管理赛事，创建和编辑赛事，为用户准备赛事资源' : '浏览赛事，查看赛事详情和参与' }}</p>
      </div>
      <div class="header-actions">
        <el-button v-if="canManage" type="primary" @click="openContestDialog()">新建赛事</el-button>
        <el-button type="info" plain @click="fetchData">刷新列表</el-button>
      </div>
    </div>

    <el-card class="page-card">
      <el-form :inline="true" :model="filters" label-suffix=":" class="filter-form">
        <el-form-item label="关键字">
          <el-input v-model="filters.keyword" placeholder="按名称搜索" clearable @keyup.enter="handleSearch" class="filter-input-keyword" />
        </el-form-item>
        <el-form-item label="可见性">
          <el-select v-model="filters.visibility" placeholder="全部" clearable @change="handleSearch" class="filter-select-visibility">
            <el-option label="公开" value="PUBLIC" />
            <el-option label="私有" value="PRIVATE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable @change="handleSearch" class="filter-select-status">
            <el-option label="未开始" value="SCHEDULED" />
            <el-option label="进行中" value="ONGOING" />
            <el-option label="已结束" value="ENDED" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="canManage" label="显示状态">
          <el-select v-model="filters.state" placeholder="全部" clearable @change="handleSearch" class="filter-select-state">
            <el-option label="使用中" value="USING" />
            <el-option label="已隐藏" value="HIDDEN" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="filters.sortBy" placeholder="默认排序" @change="handleSearch" class="filter-select-sortby">
            <el-option label="ID升序" value="id_asc" />
            <el-option label="ID降序" value="id_desc" />
            <el-option label="开始时间升序" value="start_asc" />
            <el-option label="开始时间降序" value="start_desc" />
            <el-option label="结束时间升序" value="end_asc" />
            <el-option label="结束时间降序" value="end_desc" />
            <el-option label="标题A-Z" value="title_asc" />
            <el-option label="标题Z-A" value="title_desc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="contestList" v-loading="loading" border :header-cell-style="headerStyle" class="contest-table">
        <el-table-column prop="contestId" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="赛事名称" min-width="200">
          <template #default="{ row }">
            <div class="contest-name-cell">
              <span class="contest-title">{{ row.title || '-' }}</span>
              <el-tag v-if="row.visibility" size="small" type="info" effect="plain">
                {{ visibilityLabel(row.visibility) }}
              </el-tag>
            </div>
            <p class="muted-text">{{ row.description || '暂无描述' }}</p>
          </template>
        </el-table-column>
        <el-table-column label="时间" min-width="220">
          <template #default="{ row }">
            <div class="time-column">
              <span>开始：{{ formatDate(row.startTime) }}</span>
              <span>结束：{{ formatDate(row.endTime) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
  <!-- 参赛人数列已移除 according to new requirements -->
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button size="small" text type="primary" @click="goDetail(row.contestId)">查看详情</el-button>
              <el-button
                v-if="canJoin && canJoinContest(row)"
                size="small"
                text
                type="success"
                @click="openJoinDialog(row)"
              >
                参与赛事
              </el-button>
              <el-button
                v-if="canManage"
                size="small"
                text
                type="warning"
                @click="openContestDialog(row)">
                编辑
              </el-button>
              <el-popconfirm
                v-if="canManage"
                title="确定删除该赛事？"
                confirm-button-text="删除"
                cancel-button-text="取消"
                confirm-button-type="danger"
                @confirm="handleDelete(row.contestId)"
              >
                <template #reference>
                  <el-button size="small" text type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog
      v-model="contestDialog.visible"
      :title="contestDialog.title"
      width="520px"
      destroy-on-close
      @closed="onContestDialogClosed"
    >
      <div v-loading="contestDialog.loading" element-loading-text="加载中...">
      <el-form :model="contestDialog.form" label-width="96px" :rules="contestRules" ref="contestFormRef">
        <el-form-item label="赛事名称" prop="title">
          <el-input v-model="contestDialog.form.title" maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="赛事介绍" prop="description">
          <el-input
            v-model="contestDialog.form.description"
            type="textarea"
            :rows="3"
            maxlength="300"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="contestDialog.form.startTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择开始时间"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="contestDialog.form.endTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择结束时间"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="可见性" prop="visibility">
          <el-radio-group v-model="contestDialog.form.visibility">
            <el-radio label="PUBLIC">公开</el-radio>
            <el-radio label="PRIVATE">私有</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="参赛密码">
          <el-input v-model="contestDialog.form.password" placeholder="可选" />
        </el-form-item>
        <el-form-item label="题目 ID">
          <el-input
            v-model="contestDialog.problemIdsText"
            placeholder="输入题目ID，使用逗号分隔"
          />
        </el-form-item>
      </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="contestDialog.visible = false">取消</el-button>
          <el-button type="primary" :loading="contestDialog.submitting" @click="submitContest">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="joinDialog.visible" title="参赛确认" width="420px" destroy-on-close>
      <el-form :model="joinDialog.form" label-width="88px">
        <el-form-item label="赛事名称">
          <span>{{ joinDialog.contest?.title || '-' }}</span>
        </el-form-item>
        <el-form-item label="赛事密码">
          <el-input v-model="joinDialog.form.password" placeholder="如有密码请填写" />
        </el-form-item>
        <el-form-item label="队伍名称">
          <el-input v-model="joinDialog.form.teamName" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="joinDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="joinDialog.submitting" @click="submitJoin">确认参赛</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue';
import { useRouter, useRoute, type LocationQueryRaw } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/user';
import {
  getContestList,
  getContestDetail,
  createContest,
  updateContest,
  deleteContest,
  joinContest,
} from '@/api/contest';
import type { ContestPayload } from '@/types';

interface ContestItem {
  contestId: number;
  title: string;
  description?: string;
  startTime?: string;
  endTime?: string;
  visibility?: string;
  status?: string;
  participantCount?: number;
  problemIds?: number[];
  password?: string;
  [key: string]: unknown;
}

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const contestList = ref<ContestItem[]>([]);
const loading = ref(false);

const filters = reactive({
  keyword: '',
  visibility: '',
  status: '',
  state: '', // admin/teacher 用于筛选 HIDDEN/USING 状态
  sortBy: 'id_asc' as string, // 默认按ID升序排序
});

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
});

const isAdmin = computed(() => userStore.role === 'ADMIN');
const isTeacher = computed(() => userStore.role === 'TEACHER');
const isStudent = computed(() => userStore.role === 'STUDENT');

const canManage = computed(() => isAdmin.value || isTeacher.value);
const canJoin = computed(() => isStudent.value);

const headerStyle = {
  background: 'var(--bg-canvas-inset)',
  color: 'var(--text-secondary)',
  fontWeight: '600',
};

const fetchData = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
      visibility: filters.visibility || undefined,
      status: filters.status || undefined,
      state: filters.state || undefined,
      sortBy: filters.sortBy || 'id_asc',
    };
    const res = await getContestList(params);
    const data = (res.data as any) || {};
    
    // 后端可能返回contests或list
    const list = data.contests || data.list || [];
    const normalizedList = Array.isArray(list) ? list : [];
    contestList.value = normalizedList.map(transformContestItem);
    
    pagination.total = Number(data.total) || 0;
    pagination.page = Number(data.page) || params.page || 1;
    pagination.pageSize = Number(data.pageSize) || params.pageSize || 10;
    handleRouteIntent();
  } catch (error: any) {
    console.error('获取赛事列表错误:', error);
    ElMessage.error(error?.message || '获取赛事列表失败');
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
  filters.visibility = '';
  filters.status = '';
  filters.state = '';
  filters.sortBy = 'id_asc';
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

const visibilityLabel = (value?: string) => {
  if (value === 'PUBLIC') return '公开';
  if (value === 'PRIVATE') return '私有';
  return '未知';
};

const statusLabel = (value?: string) => {
  const map: Record<string, string> = {
    SCHEDULED: '未开始',
    ONGOING: '进行中',
    ENDED: '已结束',
  };
  return map[value || ''] || '未知';
};

const statusTagType = (value?: string) => {
  const map: Record<string, 'info' | 'success' | 'danger' | 'warning'> = {
    SCHEDULED: 'info',
    ONGOING: 'success',
    ENDED: 'warning',
  };
  return map[value || ''] || 'info';
};

const formatDate = (value?: string) => {
  if (!value) return '-';
  return value.replace('T', ' ');
};

const goDetail = (contestId: number) => {
  router.push({ name: 'ContestDetail', params: { id: contestId } });
};

type ContestForm = ContestPayload;

const defaultContestForm = (): ContestForm => ({
  contestId: undefined,
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  password: '',
  visibility: 'PUBLIC',
  problemIds: [],
});

const contestDialog = reactive<{
  visible: boolean;
  title: string;
  form: ContestForm;
  problemIdsText: string;
  submitting: boolean;
  loading: boolean;
}>({
  visible: false,
  title: '新建赛事',
  form: defaultContestForm(),
  problemIdsText: '',
  submitting: false,
  loading: false,
});

const contestFormRef = ref<FormInstance>();

const contestRules: FormRules = {
  title: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  visibility: [{ required: true, message: '请选择可见性', trigger: 'change' }],
};

const openContestDialog = async (contest?: ContestItem) => {
  if (!canManage.value) return;
  contestDialog.visible = true;
  contestDialog.submitting = false;
  contestDialog.loading = false;
  if (contest) {
    contestDialog.title = '编辑赛事';
    contestDialog.loading = true;
    try {
      const res = await getContestDetail(contest.contestId);
      const detail = (res.data as any) || {};
      contestDialog.form = {
        contestId: detail.contestId || detail.contest_id || contest.contestId,
        title: detail.title || contest.title,
        description: detail.description || '',
        startTime: normalizeDateTime(detail.startTime || detail.start_time || contest.startTime),
        endTime: normalizeDateTime(detail.endTime || detail.end_time || contest.endTime),
        password: detail.password || contest.password || '',
        visibility: (detail.visibility as ContestPayload['visibility']) || 'PUBLIC',
        problemIds: Array.isArray(detail.problems)
          ? detail.problems
              .map((item: any) => Number(item.problemId || item.problem_id))
              .filter((id: number) => !Number.isNaN(id))
          : contest.problemIds || [],
      };
      contestDialog.problemIdsText = contestDialog.form.problemIds.join(',');
    } catch (error: any) {
      ElMessage.error(error?.message || '加载赛事详情失败');
      contestDialog.visible = false;
      return;
    } finally {
      contestDialog.loading = false;
    }
  } else {
    contestDialog.title = '新建赛事';
    contestDialog.form = defaultContestForm();
    contestDialog.problemIdsText = '';
    await nextTick();
  }
  nextTickReset();
};

const nextTickReset = async () => {
  await nextTick();
  contestFormRef.value?.clearValidate?.();
};

const parseProblemIds = (input: string) => {
  if (!input) return [] as number[];
  return input
    .split(',')
    .map((item) => Number(item.trim()))
    .filter((num) => !Number.isNaN(num));
};

const transformContestItem = (item: Record<string, unknown>): ContestItem => {
  const visibility = typeof item.visibility === 'string' ? item.visibility.toUpperCase() : '';
  const status = deriveStatus(item);
  const rawId = Number(item.contestId || item.contest_id);
  const contestId = Number.isNaN(rawId) ? 0 : rawId;
  return {
    contestId,
    title: String(item.title || ''),
    description: typeof item.description === 'string' ? item.description : '',
    startTime: normalizeDateTime(String(item.startTime || item.start_time || '')),
    endTime: normalizeDateTime(String(item.endTime || item.end_time || '')),
    visibility,
    status,
    participantCount:
      (item.participantCount || item.participant_count) !== undefined && (item.participantCount || item.participant_count) !== null
        ? Number(item.participantCount || item.participant_count)
        : undefined,
  } as ContestItem;
};

const deriveStatus = (item: Record<string, unknown>) => {
  if (typeof item.status === 'string' && item.status.trim()) {
    const status = item.status.toUpperCase();
    // 统一映射到后端使用的状态枚举
    if (status === 'UPCOMING') return 'SCHEDULED';
    if (status === 'FINISHED') return 'ENDED';
    return status;
  }
  const now = Date.now();
  const start = Date.parse(String(item.startTime || item.start_time || ''));
  const end = Date.parse(String(item.endTime || item.end_time || ''));
  if (!Number.isNaN(start) && now < start) return 'SCHEDULED';
  if (!Number.isNaN(end) && now > end) return 'ENDED';
  return 'ONGOING';
};

const submitContest = () => {
  if (!contestFormRef.value) return;
  contestFormRef.value.validate(async (valid) => {
    if (!valid) return;
    contestDialog.submitting = true;
    try {
      const payload: ContestPayload = {
        contestId: contestDialog.form.contestId,
        title: contestDialog.form.title,
        description: contestDialog.form.description,
        startTime: contestDialog.form.startTime,
        endTime: contestDialog.form.endTime,
        password: contestDialog.form.password || undefined,
        visibility: contestDialog.form.visibility,
        problemIds: parseProblemIds(contestDialog.problemIdsText),
      };
      if (!payload.problemIds.length) {
        ElMessage.warning('请至少填写一个题目 ID');
        contestDialog.submitting = false;
        return;
      }
      const startMs = Date.parse(payload.startTime.replace(' ', 'T'));
      const endMs = Date.parse(payload.endTime.replace(' ', 'T'));
      if (!Number.isNaN(startMs) && !Number.isNaN(endMs) && endMs <= startMs) {
        ElMessage.warning('结束时间必须晚于开始时间');
        contestDialog.submitting = false;
        return;
      }
      if (!payload.contestId) {
        await createContest(payload);
        ElMessage.success('赛事创建成功');
      } else {
        await updateContest(payload);
        ElMessage.success('赛事更新成功');
      }
      contestDialog.visible = false;
      fetchData();
    } catch (error: any) {
      ElMessage.error(error?.message || '操作失败');
    } finally {
      contestDialog.submitting = false;
    }
  });
};

const handleDelete = async (contestId: number) => {
  try {
    await deleteContest(contestId);
    ElMessage.success('赛事删除成功');
    fetchData();
  } catch (error: any) {
    ElMessage.error(error?.message || '删除失败');
  }
};

const joinDialog = reactive({
  visible: false,
  contest: null as ContestItem | null,
  form: {
    password: '',
    teamName: '',
  },
  submitting: false,
});

const openJoinDialog = (contest: ContestItem) => {
  joinDialog.visible = true;
  joinDialog.contest = contest;
  joinDialog.form.password = '';
  joinDialog.form.teamName = '';
};

const submitJoin = async () => {
  if (!joinDialog.contest) return;
  joinDialog.submitting = true;
  try {
    await joinContest({
      contestId: joinDialog.contest.contestId,
      password: joinDialog.form.password || undefined,
      teamName: joinDialog.form.teamName || undefined,
    });
    ElMessage.success('参赛成功');
    joinDialog.visible = false;
    fetchData();
  } catch (error: any) {
    ElMessage.error(error?.message || '参赛失败');
  } finally {
    joinDialog.submitting = false;
  }
};

const canJoinContest = (contest: ContestItem) => {
  if (!canJoin.value) return false;
  const status = (contest.status || '').toUpperCase();
  return status === 'UPCOMING' || status === 'ONGOING';
};

const normalizeDateTime = (value?: string) => {
  if (!value) return '';
  return value.replace('T', ' ').trim();
};

const onContestDialogClosed = () => {
  contestDialog.form = defaultContestForm();
  contestDialog.problemIdsText = '';
  contestDialog.submitting = false;
  contestDialog.loading = false;
  contestFormRef.value?.clearValidate?.();
};

const handleRouteIntent = () => {
  const intents: string[] = [];
  const editId = Number(route.query.edit);
  if (!Number.isNaN(editId) && editId > 0 && canManage.value) {
    openContestDialog({ contestId: editId, title: '', visibility: 'PUBLIC', status: '', startTime: '', endTime: '' } as ContestItem);
    intents.push('edit');
  }
  const joinId = Number(route.query.join);
  if (!Number.isNaN(joinId) && joinId > 0 && canJoin.value) {
    const target = contestList.value.find((item) => item.contestId === joinId);
    if (target) {
      openJoinDialog(target);
      intents.push('join');
    } else {
      resolveContestForJoin(joinId).then((contest) => {
        if (contest) {
          openJoinDialog(contest);
        }
      });
      intents.push('join');
    }
  }
  if (intents.length) {
    const nextQuery: LocationQueryRaw = {};
    Object.entries(route.query).forEach(([key, value]) => {
      if (!intents.includes(key)) {
        nextQuery[key] = value as LocationQueryRaw[string];
      }
    });
    router.replace({ query: nextQuery });
  }
};

const resolveContestForJoin = async (contestId: number) => {
  try {
    const res = await getContestDetail(contestId);
    const detail = (res.data as any) || {};
    return transformContestItem(detail);
  } catch (error: any) {
    ElMessage.warning(error?.message || '无法获取该赛事信息');
    return null;
  }
};

onMounted(fetchData);
</script>

<style scoped>
.contest-list-page {
  gap: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 使用全局统一的 .filter-form 样式 */

/* 表格样式 - 统一暗色主题 */
.contest-table {
  --el-table-border-color: var(--border-default);
  --el-table-header-bg-color: var(--bg-canvas-inset);
  --el-table-tr-bg-color: var(--bg-surface);
  --el-table-row-hover-bg-color: var(--bg-hover);
  --el-table-text-color: var(--text-primary);
  --el-table-header-text-color: var(--text-secondary);
  background-color: var(--bg-surface);
}

.contest-table :deep(.el-table__inner-wrapper::before),
.contest-table :deep(.el-table__border-left-patch) {
  background-color: var(--border-default);
}

.contest-table :deep(td.el-table__cell),
.contest-table :deep(th.el-table__cell) {
  border-color: var(--border-default);
  padding: 14px 12px;
}

.contest-table :deep(th.el-table__cell) {
  background-color: var(--bg-canvas-inset);
  color: var(--text-secondary);
  font-weight: 600;
}

.contest-table :deep(tr.el-table__row:hover td.el-table__cell) {
  background-color: var(--bg-hover);
}

.contest-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.contest-title {
  font-weight: 600;
  color: var(--text-primary);
  word-break: break-word;
}

.time-column {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.table-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .contest-table {
    overflow-x: auto;
  }
}
</style>
