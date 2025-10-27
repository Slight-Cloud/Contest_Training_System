import request from '@/utils/request';
import type {
    Submission,
    SubmissionListParams,
    SubmitCodePayload,
    TestCase,
    ApiResponse,
    SubmissionListData,
} from '@/types';

// 提交代码
export const submitCode = (data: SubmitCodePayload) =>
    request<ApiResponse<{ result: string; timeUsed: number; memoryUsed: number; submissionId?: number; judgeLogUrl?: string }>>({
        url: '/submission/submit',
        method: 'post',
        data,
    });

// 查询提交记录
export const getSubmissionList = (params?: SubmissionListParams) =>
    request<ApiResponse<SubmissionListData>>({
        url: '/submission/list',
        method: 'get',
        params,
    });

// 获取提交详情
export const getSubmissionDetail = (submissionId: number) =>
    request<ApiResponse<Submission>>({
        url: `/submission/${submissionId}`,
        method: 'get',
    });

// 获取提交的测试点结果
export const getSubmissionTestcases = (submissionId: number) =>
    request<ApiResponse<TestCase[]>>({
        url: `/submission/${submissionId}/testcase`,
        method: 'get',
    });

