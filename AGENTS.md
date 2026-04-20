## 构建与运行命令

```bash
# 启动/重启后端服务，必须使用task启动
./start.bat

# 先构建再启动，必须使用task启动
./start.bat -b
```

## 项目架构

### 技术栈

- **框架**: Spring Boot 3.1.2
- **JDK**: 17
- **数据库**: MySQL + Druid 连接池
- **API 框架**: magic-api 2.2.2
- **认证**: Sa-Token 1.44.0
- **Redis**: 可选 Redis 集成（Redisson）
- **插件系统**: PF4J 3.12.0（运行时热加载）
- **其他**: LiteFlow、Quartz、Forest、WebSocket

### 模块结构

项目包含两套独立插件体系：

| 系统 | 位置 | 加载方式 | 用途 |
| ---- | ---- | -------- | ---- |
| PF4J 动态插件系统 | `magic-plugins/` | 运行时热加载 | 用户自定义扩展 |
| Magic-API 内置插件 | `magic-boot-plugins/` | 编译时模块 | 核心功能扩展 |

## 核心组件

### magic-api 集成

- API 脚本位于 `data/magic-api/`（`.ms` 文件）

### PF4J 插件系统

- 插件目录：`./plugins/`（可由 `plugin.dir` 配置）
- 管理 API：`/plugin/admin/*`
- 支持运行时热加载和 ClassLoader 隔离

### 认证流程

- Sa-Token 会话管理，默认 30 天
- Token 名称：`token`
- 登录接口：`/system/security/登录`

### Redis 集成

- 开关：`extend.redis.enabled`

## 开发规则

- 修改代码后不需要主动编译，用户会自行编译测试
- 新增扩展点：在 `magic-plugin-api` 定义接口，在 `magic-plugins` 实现执行器
- 开发插件：参考 `magic-plugins-demo`，实现扩展点并加 `@Extension`

## 测试

- HTTP 接口测试请在 `http` 目录编写 `.http` 文件
- 写完测试用例必须要输出具体测试结果和报告

## 其他

- 使用中文回复
- 新增特性或功能前，必须生成计划文档，保存到项目文件夹`.claude/plans`
- 功能调整或者功能重构，必须生成变更日志文档
- 新增特性或功能后，需生成里程碑文档
- 源码文件统一保存为 UTF-8

## File Encoding & Line Endings
- All newly created or modified files must use UTF-8 encoding