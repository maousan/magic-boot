---
name: deploy-dongxinheping
description: 编译 dongxinheping-admin 前端并构建部署 magic-plugin-dongxinheping PF4J 插件。触发：用户说"部署东信和平"、"编译部署"、"发布 dongxinheping"、"重新编译并部署"。
---

# 部署东信和平前后端

将 dongxinheping-admin 前端编译产物打包进 magic-plugin-dongxinheping 插件 JAR，然后热部署到运行中的应用。

## 适用场景
- 前端页面改动后需要发布
- 插件代码改动后需要重新部署
- 前端 + 后端都有改动，需要全量部署

## 目录约定

| 角色 | 路径 |
|---|---|
| 前端项目 | `dongxinheping-admin/` |
| 插件模块 | `magic-plugin-dongxinheping/` |
| 前端输出 | `magic-plugin-dongxinheping/src/main/resources/static/admin/` |
| 插件部署 | `plugins/magic-plugin-dongxinheping.jar` |
| 插件 ID | `dongxinheping-plugin` |

## 部署流程

### Step 1：编译前端

在 `dongxinheping-admin/` 目录执行：

```bash
cd dongxinheping-admin && npm run build
```

**注意**：
- `vite.config.ts` 已配置 `emptyOutDir: true`，构建前会自动清空输出目录
- 输出目录：`magic-plugin-dongxinheping/src/main/resources/static/admin/`
- TypeScript 类型检查是构建的一部分（`vue-tsc -b`），类型错误会导致构建失败

**类型错误处理**：
如果 `npm run build` 因 TS 错误失败，先修复类型问题再重新构建。常见问题：
- `reactive({ field: undefined })` 后赋值 `string` → 需要显式类型声明 `reactive<{ field: string | undefined }>(...)`

### Step 2：构建插件 JAR

在 `magic-plugin-dongxinheping/` 目录执行（不要从项目根目录用 `-pl`，Windows 下模块定位不稳定）：

```bash
cd magic-plugin-dongxinheping && mvn package -DskipTests -q
```

### Step 3：替换插件 JAR

**如果后端正在运行**，先停掉 Java 进程（JAR 文件被锁）：

```bash
# 查找进程
wmic process where "name='java.exe'" get ProcessId,CommandLine | grep -i "magic-boot"

# 停止进程
taskkill /F /PID <PID>
```

然后复制：

```bash
cp magic-plugin-dongxinheping/target/magic-plugin-dongxinheping.jar plugins/magic-plugin-dongxinheping.jar
```

**注意：不需要启动验证，用户会自行启动和验证。**

## 快速模板

```bash
cd dongxinheping-admin && npm run build && \
cd ../magic-plugin-dongxinheping && mvn package -DskipTests -q && \
cp target/magic-plugin-dongxinheping.jar ../plugins/magic-plugin-dongxinheping.jar
```

## 常见问题

| 问题 | 原因 | 解决 |
|---|---|---|
| `vue-tsc` 类型错误 | TS 类型不匹配 | 修复类型声明后重新构建 |
| `Copy-Item` 文件被锁 | 后端 Java 进程未停止 | 先 `taskkill` 再复制 |
| `-pl` 找不到模块 | Windows 下 Maven reactor 不稳定 | 在模块目录内直接执行 `mvn` |
| 页面 404 | 插件未加载 | 检查 `/plugin/admin/list` 是否包含 dongxinheping-plugin |
| API 代理 404 | Vite 代理未配置对应前缀 | 在 `vite.config.ts` 的 `proxy` 中添加路径规则 |
