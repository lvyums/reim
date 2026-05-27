import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    VueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 👇 关键：前端代理，自动转发请求到后端
  server: {
    proxy: {
      '/api': {  // 所有以 /api 开头的请求都会转发到后端
        target: 'http://localhost:8080', // 你的后端地址
        changeOrigin: true, // 开启跨域
      }
    }
  }
})
