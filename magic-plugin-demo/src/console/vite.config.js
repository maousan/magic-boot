import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  define: {
    // 定义 process.env，避免浏览器环境报错
    "process.env.NODE_ENV": JSON.stringify("production"),
    "process.env": {},
  },
  build: {
    // 不压缩，便于调试
    minify: false,
    // CSS 内联到 JS
    cssCodeSplit: false,
    // 输出目录
    outDir: "../main/resources/static",
    // 不清空输出目录（避免删除其他资源）
    emptyOutDir: false,
    // 库模式配置
    lib: {
      entry: path.resolve(__dirname, "src/index.js"),
      name: "DemoPlugin", // 全局变量名
      formats: ["iife"], // 自执行函数格式
      fileName: () => "console.js",
    },
    rollupOptions: {
      // 不再外部化 Vue，内联到产物中（支持独立运行）
      // external: ['vue'],
      // output: {
      //   globals: {
      //     vue: 'Vue'
      //   }
      // }
    },
  },
  server: {
    port: 5174,
    proxy: {
      "/api": {
        target: "http://localhost:8089",
        changeOrigin: true,
      },
    },
  },
});
