import request from '@/utils/request';
import type { RegisterPayload, LoginPayload, UpdateUserPayload, ApiResponse, User } from '@/types';

export const register = (data: RegisterPayload) => {
    return request<ApiResponse<{ userId: number; email: string; nickname: string }>>({
        url: '/register',
        method: 'post',
        data,
    });
};

export const login = (data: LoginPayload) => {
    return request<ApiResponse<{ token: string }>>({
        url: '/login',
        method: 'post',
        data,
    });
};

export const getUserProfile = () => {
    return request<ApiResponse<User>>({
        url: '/user/profile',
        method: 'get',
    });
};

export const updateUserInfo = (data: UpdateUserPayload) => {
    return request<ApiResponse<User>>({
        url: '/user/update',
        method: 'put',
        data,
    });
};

export const deactivateUser = () => {
    return request<ApiResponse<null>>({
        url: '/user/deactivate',
        method: 'post',
    });
};

export const getUserList = (params: {
    page?: number;
    pageSize?: number;
    keyword?: string;
    role?: string;
    isActive?: number;
    sortBy?: string;
}) => {
    return request<ApiResponse<any>>({
        url: '/admin/users',
        method: 'get',
        params,
    });
};
