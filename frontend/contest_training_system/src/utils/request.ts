import axios, { AxiosHeaders } from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/user';

const service = axios.create({
    baseURL: '/api',
    timeout: 10000,
});

service.interceptors.request.use(
    (config) => {
        const userStore = useUserStore();
        console.log('=== API请求调试 ===');
        console.log('请求URL:', config.url);
        console.log('请求方法:', config.method);
        console.log('请求参数:', config.params);
        console.log('请求数据:', config.data);
        console.log('用户token:', userStore.token ? `${userStore.token.substring(0, 20)}...` : 'null');
        console.log('用户角色:', userStore.role);

        if (userStore.token) {
            if (config.headers instanceof AxiosHeaders) {
                config.headers.set('Authorization', `Bearer ${userStore.token}`);
            } else {
                const headers = new AxiosHeaders(config.headers);
                headers.set('Authorization', `Bearer ${userStore.token}`);
                config.headers = headers;
            }
            console.log('已添加Authorization头');
        } else {
            console.log('未添加Authorization头 - 无token');
        }
        return config;
    },
    (error) => {
        console.error('请求拦截器错误:', error);
        return Promise.reject(error);
    },
);

service.interceptors.response.use(
    (response) => {
        const res = response.data;
        console.log('=== API响应调试 ===');
        console.log('响应状态:', response.status);
        console.log('响应数据:', res);
        console.log('响应头:', response.headers);

        // 如果响应格式正确，检查code
        if (res && typeof res === 'object' && 'code' in res) {
            console.log('响应code:', res.code);
            console.log('响应msg:', res.msg);
            // code为0表示失败
            if (res.code === 0) {
                console.log('API返回失败:', res.msg);
                if (!response.config?.suppressGlobalError) {
                    ElMessage.error(res.msg || '请求失败');
                }
                return Promise.reject(new Error(res.msg || 'Error'));
            }
            // code为1表示成功，返回整个响应对象
            console.log('API返回成功');
            return res;
        }
        // 如果没有标准格式，直接返回数据
        console.log('非标准响应格式，直接返回数据');
        return response.data;
    },
    (error) => {
        console.log('=== API错误调试 ===');
        console.log('错误状态:', error.response?.status);
        console.log('错误数据:', error.response?.data);
        console.log('错误信息:', error.message);
        console.log('完整错误:', error);

        const config = error.config || {};

        if (config.suppressGlobalError) {
            console.log('已开启 suppressGlobalError，跳过全局错误提示');
            return Promise.reject(error);
        }

        // 403错误特殊处理
        if (error.response?.status === 403) {
            console.log('检测到403错误');
            const userStore = useUserStore();

            // 检查是否是认证问题（token无效或过期）
            if (!userStore.token || userStore.token === '') {
                console.log('用户未登录，检查登录状态 / Token 是否过期。');
                ElMessage.error('请先登录');
                window.location.href = '/login';
            } else {
                // 用户已登录但权限不足，不跳转登录页
                console.log('用户已登录 403 错误');
                ElMessage.error('用户已登录 确认 URL 是否正确（是否拼错或权限路径）');



            }
            return Promise.reject(error);
        }

        const errorMessage = error.response?.data?.msg || error.message || '网络请求失败';
        console.log('显示错误消息:', errorMessage);
        ElMessage.error(errorMessage);
        return Promise.reject(error);
    },
);

export default service;
