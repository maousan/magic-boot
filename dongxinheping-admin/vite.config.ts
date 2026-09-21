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
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
      '/magic': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
      '/plugin/zintis-led-plugin': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
      '/plugin/dongxinheping-plugin': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
      '/ws': {
        target: 'ws://localhost:8090',
        ws: true,
      },
      '/plugin/zintis-rfid-plugin': {
        target: 'http://localhost:8090',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: '../magic-plugin-dongxinheping/src/main/resources/static/admin',
    emptyOutDir: true,
  },
})
