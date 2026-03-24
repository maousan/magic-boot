# 文件插件 API 测试指南

## 📋 测试文件说明

- **test-file-plugin-api.http** - 文件插件完整接口测试脚本
- **test-plugin-api.http** - PF4J 插件系统测试脚本（参考）
- **test-file.bin** - 5MB 测试文件，用于大文件上传测试

## 🚀 快速开始

### 前置条件

1. **启动后端服务**
   ```bash
   ./start.bat
   ```

2. **安装 IDEA HTTP Client 插件**（如果使用 IntelliJ IDEA）
   - 在 Settings → Plugins 中搜索 "HTTP Client" 并安装

3. **配置认证 Token**（如果需要）
   - 在测试文件中修改 `@token` 变量
   - 或者先调用登录接口获取 token

### 运行测试

#### 方式 1：使用 IDEA HTTP Client（推荐）

1. 打开 `test-file-plugin-api.http` 文件
2. 点击每个请求旁边的绿色运行按钮
3. 查看响应结果和测试断言

#### 方式 2：使用命令行

```bash
# 安装 httpyac（HTTP Client CLI）
npm install -g httpyac

# 运行所有测试
httpyac test-file-plugin-api.http --all

# 运行特定测试
httpyac test-file-plugin-api.http --name "获取存储类型"
```

## 📚 测试模块说明

### 模块 1: 存储管理 API 测试

测试存储配置管理接口，包括：

| 测试编号 | 测试项 | 接口路径 | 方法 |
|---------|--------|---------|------|
| TC-1.1 | 获取支持的存储类型 | `/file/storage/types` | GET |
| TC-1.2 | 测试本地存储连接 - 有效路径 | `/file/storage/test` | POST |
| TC-1.3 | 测试本地存储连接 - 空路径 | `/file/storage/test` | POST |
| TC-1.4 | 测试 MinIO 存储连接 - 无效端点 | `/file/storage/test` | POST |
| TC-1.5 | 测试 S3 存储连接 - 无效凭证 | `/file/storage/test` | POST |
| TC-1.6 | 测试不支持的存储类型 | `/file/storage/test` | POST |
| TC-1.7 | 测试空存储类型 | `/file/storage/test` | POST |
| TC-1.8 | 测试 null 存储信息 | `/file/storage/test` | POST |
| TC-1.9 | 查询存储平台列表 | `/system/file/storage/list` | GET |

**关键验证点：**
- ✅ 返回 3 种存储类型（local、minio、s3）
- ✅ 本地存储连接测试成功，具有读写权限
- ✅ 无效参数返回明确的错误信息
- ✅ 边界条件（空值、null）正确处理

### 模块 2: 文件操作 API 测试

测试文件上传、下载、删除等操作，包括：

| 测试编号 | 测试项 | 接口路径 | 方法 |
|---------|--------|---------|------|
| TC-2.1 | 获取文件列表 - 根目录 | `/system/file/browse/list` | GET |
| TC-2.2 | 获取文件列表 - 指定目录 | `/system/file/browse/list` | GET |
| TC-2.3 | 上传文件 - 文本文件 | `/system/file/browse/upload` | POST |
| TC-2.4 | 上传文件 - 带中文文件名 | `/system/file/browse/upload` | POST |
| TC-2.5 | 上传文件 - 不指定路径 | `/system/file/browse/upload` | POST |
| TC-2.6 | 下载文件 | `/system/file/browse/download` | GET |
| TC-2.7 | 下载不存在的文件 | `/system/file/browse/download` | GET |
| TC-2.8 | 删除文件 | `/system/file/browse/delete` | DELETE |
| TC-2.9 | 删除不存在的文件 | `/system/file/browse/delete` | DELETE |
| TC-2.10 | 删除文件 - 空路径 | `/system/file/browse/delete` | DELETE |

**关键验证点：**
- ✅ 文件列表返回正确的文件信息
- ✅ 上传文件返回文件 URL 和大小
- ✅ 支持中文文件名
- ✅ 下载文件包含正确的响应头
- ✅ 删除操作返回删除的文件路径

### 模块 3: 边界条件测试

测试异常情况和边界条件：

| 测试编号 | 测试项 |
|---------|--------|
| TC-3.1 | 获取文件列表 - 不存在的目录 |
| TC-3.2 | 获取文件列表 - 不带路径参数 |
| TC-3.3 | 上传大文件 |
| TC-3.4 | 上传文件 - 不提供文件 |

### 模块 4: 多存储源测试

测试多存储源功能：

| 测试编号 | 测试项 |
|---------|--------|
| TC-4.1 | 上传到指定存储源 |

**前提条件：** 需要先配置多个存储源

### 模块 5: 性能测试

测试性能和并发能力：

| 测试编号 | 测试项 |
|---------|--------|
| TC-5.1 | 并发上传测试 |
| TC-5.2~5.4 | 连续上传多个文件 |
| TC-5.5 | 验证批量上传结果 |

## 🔧 测试断言说明

每个测试都包含 JavaScript 断言脚本，验证：

1. **HTTP 状态码** - 应为 200
2. **响应数据结构** - 包含必需的字段
3. **业务逻辑** - 返回正确的业务结果
4. **错误处理** - 异常情况返回明确的错误信息

示例：
```javascript
client.test("获取存储类型成功", function() {
    client.assert(response.status === 200, "HTTP状态码应为200");
    client.assert(response.body.data.length === 3, "应返回3种存储类型");
});
```

## 📊 测试覆盖率

### API 接口覆盖

| 接口类型 | 接口数量 | 测试用例数 | 覆盖率 |
|---------|---------|-----------|--------|
| 存储管理 | 3 | 9 | 100% |
| 文件操作 | 4 | 15 | 100% |
| **总计** | **7** | **24** | **100%** |

### 测试类型覆盖

- ✅ **正向测试** - 正常流程和参数
- ✅ **异常测试** - 无效参数和边界条件
- ✅ **性能测试** - 并发和批量操作
- ✅ **安全测试** - 路径遍历等安全问题

## 🐛 常见问题

### 1. 上传测试失败

**问题：** 上传文件测试返回 400 错误

**解决方案：**
- 检查 Content-Type 是否为 `multipart/form-data`
- 确保 boundary 格式正确
- 确认文件字段名为 `file`

### 2. 下载测试失败

**问题：** 下载文件时返回错误信息

**解决方案：**
- 确认文件路径正确（使用上传返回的 `filePath`）
- 检查文件是否存在

### 3. 认证失败

**问题：** 返回 401 未授权

**解决方案：**
- 先调用登录接口获取 token
- 在请求头中添加：`Authorization: Bearer {{token}}`

## 📝 添加新测试

### 模板示例

```http
### TC-X.X 测试描述
# @name 测试名称
METHOD {{baseUrl}}/api/path
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "key": "value"
}

> {%
client.test("测试断言", function() {
    client.assert(response.status === 200, "HTTP状态码应为200");
    client.log("✅ 测试成功");
});
%}

###
```

## 🔗 相关文档

- [Magic-API 文档](../data/magic-api/)
- [文件插件实现](../magic-boot-plugins/magic-api-plugin-file/)
- [单元测试](../magic-boot-plugins/magic-api-plugin-file/src/test/)

## 📧 反馈

如有问题或建议，请创建 Issue 或联系开发团队。
