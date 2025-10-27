import request from '@/utils/request';
import type {
    Contest,
    ContestListParams,
    ContestPayload,
    JoinContestPayload,
    HasJoinedContestPayload,
    ApiResponse,
    ContestListData,
    RankingItem,
} from '@/types';

export const getContestList = (params?: ContestListParams) =>
    request<ApiResponse<ContestListData>>({
        url: '/contest/list',
        method: 'get',
        params,
    });

export const createContest = (data: ContestPayload) =>
    request<ApiResponse<{ contestId: number }>>({
        url: '/contest/create',
        method: 'post',
        data,
    });

export const updateContest = (data: ContestPayload) =>
    request<ApiResponse<Contest>>({
        url: '/contest/update',
        method: 'put',
        data,
    });

export const deleteContest = (contestId: number) =>
    request<ApiResponse<null>>({
        url: `/contest/${contestId}`,
        method: 'delete',
    });

export const joinContest = (data: JoinContestPayload) =>
    request<ApiResponse<null>>({
        url: '/contest/join',
        method: 'post',
        data,
    });

export const hasJoinedContest = (data: HasJoinedContestPayload) =>
    request<ApiResponse<boolean>>({
        url: '/contest/hasJoined',
        method: 'post',
        data,
        suppressGlobalError: true,
    });

export const getContestDetail = (contestId: number) =>
    request<ApiResponse<Contest>>({
        url: `/contest/${contestId}`,
        method: 'get',
    });

export const getContestRanking = (contestId: number) =>
    request<ApiResponse<RankingItem[]>>({
        url: `/contest/${contestId}/rank`,
        method: 'get',
    });

export const getContestRankingDetail = (contestId: number, userId?: number) =>
    request<ApiResponse<RankingItem[]>>({
        url: `/contest/rank/detail`,
        method: 'get',
        params: { contestId, userId },
    });