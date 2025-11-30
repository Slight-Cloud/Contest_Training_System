import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { login, getUserProfile } from '@/api/user';
import type { User, LoginPayload, UserRole } from '@/types';

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '');
    const userInfo = ref<User | null>(null);

    // 响应拦截器已统一处理，不再需要unwrapResponse方法

    const role = computed<UserRole | 'GUEST'>(() => {
        if (!userInfo.value || !userInfo.value.role) {
            return 'GUEST';
        }
        return userInfo.value.role;
    });

    const userId = computed(() => userInfo.value?.userId || 0);
    const nickname = computed(() => userInfo.value?.nickname || '');
    const email = computed(() => userInfo.value?.email || '');

    const userLogin = async (loginData: LoginPayload) => {
        const res = await login(loginData);
        const tokenValue = (res as any)?.data?.token || '';
        token.value = tokenValue;
        if (token.value) {
            localStorage.setItem('token', token.value);
        }
    };

    const fetchUserProfile = async () => {
        if (import.meta.env.MODE === 'development') {
            console.log('=== 获取用户信息调试 ===');
            console.log('当前token:', token.value ? `${token.value.substring(0, 20)}...` : 'null');
        }
        try {
            const res = await getUserProfile();
            if (import.meta.env.MODE === 'development') {
                console.log('用户信息响应:', res);
            }
            userInfo.value = (res as any)?.data || null;
            if (import.meta.env.MODE === 'development') {
                console.log('用户信息数据:', userInfo.value);
                console.log('用户角色:', role.value);
            }
        } catch (error) {
            console.error('获取用户信息失败:', error);
            throw error;
        }
    };

    const logout = () => {
        token.value = '';
        userInfo.value = null;
        localStorage.removeItem('token');
    };

    const clearToken = () => {
        token.value = '';
        userInfo.value = null;
        localStorage.removeItem('token');
    };

    return { 
        token, 
        userInfo, 
        role, 
        userId, 
        nickname, 
        email, 
        userLogin, 
        fetchUserProfile, 
        logout,
        clearToken
    };
});
