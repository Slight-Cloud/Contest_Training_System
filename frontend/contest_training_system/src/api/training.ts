import request from '@/utils/request';
import type {
    TrainingPlan,
    TrainingPlanListParams,
    TrainingPlanPayload,
    ApiResponse,
    TrainingPlanListData,
} from '@/types';

// 创建训练计划
export const createTrainingPlan = (data: TrainingPlanPayload) =>
    request<ApiResponse<{ planId: number }>>({
        url: '/training_plan/create',
        method: 'post',
        data,
    });

// 更新训练计划
export const updateTrainingPlan = (data: TrainingPlanPayload) =>
    request<ApiResponse<TrainingPlan>>({
        url: '/training_plan/update',
        method: 'put',
        data,
    });

// 删除训练计划
export const deleteTrainingPlan = (planId: number) =>
    request<ApiResponse<{ planId: number; action: string }>>({
        url: `/training_plan/${planId}`,
        method: 'delete',
    });

// 获取训练计划列表
export const getTrainingPlanList = (params?: TrainingPlanListParams) =>
    request<ApiResponse<TrainingPlanListData>>({
        url: '/training_plan/list',
        method: 'get',
        params,
    });

// 获取训练计划详情
export const getTrainingPlanDetail = (planId: number) =>
    request<ApiResponse<TrainingPlan>>({
        url: `/training_plan/${planId}`,
        method: 'get',
    });

// 获取训练计划的进度统计
export const getTrainingPlanProgress = (planId: number) =>
    request<ApiResponse<any>>({
        url: `/training_plan/${planId}/progress`,
        method: 'get',
           suppressGlobalError: true,
    });
