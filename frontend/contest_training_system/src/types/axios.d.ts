import 'axios';

declare module 'axios' {
  interface AxiosRequestConfig {
    /**
     * 是否在响应拦截器中跳过全局错误提示。
     */
    suppressGlobalError?: boolean;
  }
}
