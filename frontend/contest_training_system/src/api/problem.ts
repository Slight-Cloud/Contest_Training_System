import request from '@/utils/request';
import type {
    Problem,
    ProblemListParams,
    ProblemPayload,
    ProblemDatasetPayload,
    SolutionReport,
    SolutionReportPayload,
    SolutionUpdatePayload,
    ApiResponse,
    ProblemListData,
} from '@/types';

// 导出 ProblemPayload 类型供外部使用
export type { ProblemPayload };

// 查询题目列表
export const getProblemList = (params?: ProblemListParams) =>
    request<ApiResponse<ProblemListData>>({
        url: '/problem/list',
        method: 'get',
        params,
    });

// 获取题目详情
export const getProblemDetail = (problemId: number) =>
    request<ApiResponse<Problem>>({
        url: `/problem/${problemId}`,
        method: 'get',
    });

// 新建题目
export const createProblem = (data: ProblemPayload) =>
    request<ApiResponse<{ problemId: number }>>({
        url: '/problem/create',
        method: 'post',
        data,
    });

// 修改题目
export const updateProblem = (data: ProblemPayload) =>
    request<ApiResponse<Problem>>({
        url: '/problem/update',
        method: 'put',
        data,
    });

// 删除/隐藏题目
export const deleteProblem = (problemId: number) =>
    request<ApiResponse<{ problemId: number; action: string }>>({
        url: `/problem/${problemId}`,
        method: 'delete',
    });

// 添加测试数据
export const addProblemDataset = (data: ProblemDatasetPayload) =>
    request<ApiResponse<{ datasetId: number; problemId: number; version: number }>>({
        url: '/problem/dataset/add',
        method: 'post',
        data,
    });

// 发布题解报告
export const publishSolutionReport = (data: SolutionReportPayload) =>
    request<ApiResponse<{ reportId: number; problemId: number }>>({
        url: '/problem/solution/create',
        method: 'post',
        data,
    }) as Promise<ApiResponse<{ reportId: number; problemId: number }>>;

// 查询题解列表
export const getSolutionList = (problemId: number, params?: { page: number; pageSize: number }) =>
    request<ApiResponse<{ total: number; list: SolutionReport[] }>>({
        url: `/problem/${problemId}/solution/list`,
        method: 'get',
        params,
    }) as Promise<ApiResponse<{ total: number; list: SolutionReport[] }>>;

// 修改题解
export const updateSolutionReport = (data: SolutionUpdatePayload) =>
    request<ApiResponse<null>>({
        url: '/problem/solution/update',
        method: 'put',
        data,
    });

// 删除题解
export const deleteSolutionReport = (reportId: number) =>
    request<ApiResponse<null>>({
        url: `/problem/solution/${reportId}`,
        method: 'delete',
    });

