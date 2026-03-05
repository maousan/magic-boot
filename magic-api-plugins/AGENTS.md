# MAGIC-API-PLUGINS

**Purpose:** magic-api 扩展插件集合，提供 Redis、MQTT、定时任务等功能及前端编辑器资源。

## STRUCTURE

```
magic-api-plugins/
├── magic-editor/              # 编辑器前端资源（Vue 3）
├── magic-api-plugin-redis/    # Redis 操作插件
├── magic-api-plugin-mqtt/     # MQTT 消息插件
└── magic-api-plugin-job/      # 定时任务插件
```

## PLUGIN STRUCTURE (通用)

每个插件包含：
```
plugin-name/
├── src/main/java/           # Java 后端实现
│   └── org/ssssssss/magicapi/
│       ├── model/           # 数据模型
│       ├── service/         # 业务逻辑
│       ├── starter/         # 自动配置
│       └── web/             # REST 控制器
└── src/console/             # Vue 3 前端控制台
    ├── src/
    │   ├── index.js         # 插件入口
    │   ├── components/      # UI 组件
    │   └── service/         # API 服务
    └── package.json
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| Redis 操作 | `magic-api-plugin-redis/src/main/java/.../redis/` |
| MQTT 消息 | `magic-api-plugin-mqtt/src/main/java/.../mqtt/` |
| 定时任务 | `magic-api-plugin-job/src/main/java/.../job/` |
| 编辑器资源 | `magic-editor/src/main/resources/magic-editor/` |

## BUILD CONVENTIONS

- **开发构建**：`mvn clean package -Pdev -DskipTests`（跳过前端）
- **生产构建**：`mvn clean package -Pprod -DskipTests`（包含前端）
- 前端产物复制到 `target/classes/magic-editor/plugins/`

## NOTES

- 所有插件依赖 `magic-api` 和 `magic-script`（provided scope）
- 前端控制台使用 Vue 3 + Vite
- 无单元测试配置
