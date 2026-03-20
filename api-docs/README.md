# 个人中心 API 接口文档

## 概述

本文档定义了 Magic Boot 个人中心模块的 RESTful API 接口规范，基于 OpenAPI 3.0.3 标准。

## 功能模块

| 模块 | 描述 |
|------|------|
| 头像管理 | 上传、裁剪、更新用户头像 |
| 密码管理 | 修改登录密码 |
| 第三方绑定 | 绑定/解绑微信、钉钉、飞书、GitHub 等第三方账号 |
| 设备管理 | 查看登录设备、踢下线可疑设备 |
| 操作日志 | 查看个人操作记录 |
| 账号注销 | 永久注销账号（不可逆操作） |

## 如何使用文档

### 1. 在线预览

推荐使用以下工具查看和测试 API：

- **Swagger Editor**: https://editor.swagger.io/
- **Redoc**: https://redocly.github.io/redoc/
- **Stoplight Studio**: https://stoplight.io/studio/

### 2. 导入到工具

#### Postman
1. 打开 Postman
2. 点击 Import 按钮
3. 选择 `profile-api.yaml` 文件
4. 导入成功后即可使用

#### Apifox / ApiPost
1. 创建或打开项目
2. 选择 "导入" -> "OpenAPI/Swagger"
3. 上传 `profile-api.yaml` 文件

### 3. 生成代码

使用 OpenAPI Generator 生成客户端 SDK：

```bash
# 生成 TypeScript Axios 客户端
openapi-generator-cli generate \
  -i profile-api.yaml \
  -g typescript-axios \
  -o ./client

# 生成 Java 客户端
openapi-generator-cli generate \
  -i profile-api.yaml \
  -g java \
  -o ./client
```

## 认证方式

所有接口均需要 Bearer Token 认证：

```http
Authorization: Bearer <your_jwt_token>
```

## 统一响应格式

所有接口遵循统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1710123456789,
  "executeTime": 15
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或 Token 失效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 接口列表

### 1. 头像上传
- **接口**: `POST /system/user/avatar/update`
- **类型**: multipart/form-data
- **说明**: 上传裁剪后的头像图片

### 2. 修改密码
- **接口**: `POST /system/user/password/update`
- **类型**: application/json
- **说明**: 修改登录密码

### 3. 第三方账号绑定
- **获取绑定列表**: `GET /system/user/bindings`
- **绑定账号**: `POST /system/user/bind`
- **解绑账号**: `POST /system/user/unbind`

### 4. 登录设备管理
- **获取设备列表**: `GET /system/user/devices`
- **踢下线设备**: `POST /system/user/device/kickout`

### 5. 操作日志
- **接口**: `GET /system/user/logs`
- **支持**: 分页、关键词搜索、时间范围筛选

### 6. 账号注销
- **接口**: `POST /system/user/cancel`
- **警告**: 此操作不可逆！

## 前端对接示例

### 使用 alova.js

```typescript
// 获取绑定列表
const { data } = useRequest(
  () => Apis.systemUser.getSystemUserBindings().send()
);

// 上传头像
const response = await Apis.systemUser
  .postSystemUserAvatarUpdate({
    data: formData,
  })
  .send();

// 修改密码
await Apis.systemUser
  .postSystemUserPasswordUpdate({
    data: {
      oldPassword: 'xxx',
      newPassword: 'yyy',
      confirmPassword: 'yyy',
    },
  })
  .send();
```

## 后端实现参考

### 控制器示例 (Spring Boot)

```java
@RestController
@RequestMapping("/system/user")
public class UserProfileController {

    @PostMapping("/avatar/update")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file) {
        // 实现头像上传逻辑
        String avatarUrl = userService.updateAvatar(file);
        return Result.success(avatarUrl);
    }

    @PostMapping("/password/update")
    public Result<Void> updatePassword(@RequestBody @Valid PasswordUpdateDTO dto) {
        userService.updatePassword(dto);
        return Result.success();
    }

    @GetMapping("/bindings")
    public Result<List<BindingInfo>> getBindings() {
        return Result.success(userService.getBindings());
    }

    // ... 其他接口
}
```

### 实体类示例

```java
@Data
public class BindingInfo {
    private String platform;
    private Boolean bound;
    private String nickname;
    private LocalDateTime bindTime;
}

@Data
public class DeviceInfo {
    private String id;
    private String deviceName;
    private String deviceType;
    private String browser;
    private String os;
    private String ip;
    private String address;
    private LocalDateTime loginTime;
    private LocalDateTime lastActiveTime;
    private Boolean isCurrent;
}
```

## 更新日志

### v1.0.0 (2024-03-15)
- 初始版本
- 新增头像上传接口
- 新增修改密码接口
- 新增第三方账号绑定接口
- 新增登录设备管理接口
- 新增操作日志查询接口
- 新增账号注销接口

## 相关文档

- [OpenAPI 规范](https://spec.openapis.org/oas/v3.0.3)
- [Swagger 文档](https://swagger.io/specification/)
- [项目文档](https://doc.vben.pro/)

## 联系方式

如有问题，请联系：
- Email: support@example.com
- 项目地址: https://github.com/example/magic-boot
