# MAGIC-BOOT-MASTER

**Purpose:** Spring Boot 主应用模块，magic-boot 平台的核心入口。

## STRUCTURE

```
magic-boot-master/
├── src/main/java/org/ssssssss/magicboot/
│   ├── MagicBootApplication.java    # 应用入口
│   ├── configuration/               # Spring 配置类
│   ├── extension/                   # magic-api 扩展函数
│   ├── interceptor/                 # 请求拦截器（权限等）
│   ├── model/                       # 数据模型和常量
│   ├── provider/                    # magic-api 结果提供者
│   └── utils/                       # 工具类
└── src/main/resources/
    └── application.yml              # 主配置文件
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| 应用启动 | `src/main/java/.../MagicBootApplication.java` |
| Web 配置 | `configuration/WebConfiguration.java` |
| Magic-API 配置 | `configuration/MagicBootConfiguration.java` |
| 权限拦截 | `interceptor/PermissionInterceptor.java` |
| 响应扩展 | `extension/ResponseFunctionExtension.java` |
| 异常处理 | `provider/ExceptionResultProvider.java` |
| 全局常量 | `model/MagicBootConstants.java` |

## KEY DEPENDENCIES

- `magic-api-spring-boot-starter` 2.2.2
- `sa-token-spring-boot3-starter` 1.35.0.RC
- `druid-spring-boot-3-starter` 1.2.20
- `hutool-all` 5.8.15
- `mysql-connector-java` 8.0.32

## NOTES

- **端口**：8081
- **数据库连接池**：Druid，最大连接数 1000
- **认证**：Sa-Token，token 有效期 30 天
- **文件上传**：最大 200MB
- **时区**：GMT+8
