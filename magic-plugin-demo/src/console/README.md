# Demo Plugin Console

演示插件的前端控制台项目，基于 Vue 3 + Vite 构建。

## 开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

## 项目结构

```
src/console/
├── src/
│   ├── index.js          # 插件入口，导出 install 函数
│   ├── App.vue           # 根组件，包含导航和布局
│   ├── views/            # 页面组件
│   │   ├── Dashboard.vue # 控制台首页
│   │   ├── Settings.vue  # 设置页面
│   │   └── DataView.vue  # 数据管理页面
│   └── components/       # 可复用组件
│       └── DemoComponent.vue
├── index.html            # 开发环境 HTML
├── vite.config.js        # Vite 配置
└── package.json
```

## 构建输出

执行 `npm run build` 后，会生成：

- `../main/resources/static/console.js` - IIFE 格式的 JS 包

该文件会被打包到插件 JAR 中，由主应用通过 `/plugin/demo-plugin/static/console.js` 加载。

## 插件接口

插件必须导出一个包含 `install` 函数的对象：

```javascript
export default {
  name: 'demo-plugin',
  displayName: '演示插件',
  version: '1.0.0',

  install(app, options) {
    // options.pluginId  - 插件 ID
    // options.apiBase   - API 基础路径
    // options.token     - 认证 token

    // 注册组件、路由等

    return {
      routes: [...],      // 路由配置
      menuItems: [...]    // 菜单配置
    }
  }
}
```

## 外部依赖

- `vue` - 使用主应用提供的 Vue 3，不打包进插件

## 注意事项

1. 所有样式应使用 `scoped` 避免污染主应用
2. 不要直接操作 `window` 或全局对象
3. 使用 `options.token` 进行 API 认证
4. 开发时可以通过 `index.html` 独立运行和调试
