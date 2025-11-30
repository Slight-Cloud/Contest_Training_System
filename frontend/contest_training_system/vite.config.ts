import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

const backendTarget = 'http://localhost:8000'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  // 构建配置：直接输出到 nginx 的 html 目录
  build: {
    outDir: 'nginx-1.22.0-web/html',
    emptyOutDir: true, // 构建前清空目录
    chunkSizeWarningLimit: 1000, // 提高警告阈值到 1000KB
  },
  server: {
    port: 9999,
    host: 'localhost',
    open: true,
    proxy: {
      '/api': {
        target: backendTarget,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
