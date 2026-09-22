import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  base: '/plugin/dongxinheping-plugin/static/admin/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 3200,
    proxy: {
      '/api': {
        target: 'http://localhost:8091',
        changeOrigin: true,
      },
      '/magic': {
        target: 'http://localhost:8091',
        changeOrigin: true,
      },
      '/plugin/zintis-led-plugin': {
        target: 'http://localhost:8091',
        changeOrigin: true,
      },
      // 授权签发/系统授权接口在 /system 前缀下（默认联调 8090；用 start-issue.bat 的 8091 签发实例时改指 8091）
      '/system': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
      '/ws': {
        target: 'ws://localhost:8090',
        ws: true,
      },
      '/plugin/zintis-rfid-plugin': {
        target: 'http://localhost:8091',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: '../magic-plugin-dongxinheping/src/main/resources/static/admin',
    emptyOutDir: true,
  },
})
