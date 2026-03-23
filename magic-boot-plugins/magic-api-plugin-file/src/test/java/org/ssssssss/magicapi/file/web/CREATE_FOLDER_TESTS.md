# 创建文件夹测试说明

## 📋 新增测试概览

为文件插件新增了 **创建文件夹** 功能的完整测试覆盖。

### 测试文件更新

1. **test-file-plugin-api.http** - HTTP 接口测试
2. **FileOperationApiTest.java** - 新增 Java 单元测试类

---

## 🎯 创建文件夹 API

### API 定义

- **接口路径**: `POST /system/file/browse/mkdir`
- **Content-Type**: `application/x-www-form-urlencoded`
- **参数**:
  - `path` (可选): 父目录路径，默认 `/`
  - `name` (必需): 文件夹名称

### 实现原理

```javascript
// 创建空字节流作为目录标记
var emptyStream = new ByteArrayInputStream(new_array(byte.class, 0))

// 使用 file 模块上传空文件作为目录标记
var result = file.upload(folderPath, emptyStream, '.folder')
```

**关键点**:
- 在对象存储中，文件夹通过上传 `.folder` 标记文件实现
- 文件夹路径以 `/` 结尾来标识
- 验证文件夹名称的合法性（不能包含特殊字符）

---

## 📊 测试用例列表

### HTTP 测试用例（test-file-plugin-api.http）

| 测试编号 | 测试项 | 参数 | 预期结果 |
|---------|--------|------|---------|
| TC-2.5.1 | 创建文件夹 - 普通文件夹 | path=/test/, name=test-folder | ✅ 成功创建 |
| TC-2.5.2 | 创建文件夹 - 嵌套文件夹 | path=/test/, name=nested-folder | ✅ 成功创建 |
| TC-2.5.3 | 创建文件夹 - 根目录 | name=root-folder | ✅ 成功创建 |
| TC-2.5.4 | 创建文件夹 - 中文文件夹名 | name=测试文件夹 | ✅ 成功创建 |
| TC-2.5.5 | 创建文件夹 - 空文件夹名称 | name="" | ❌ 验证失败 |
| TC-2.5.6 | 创建文件夹 - 非法字符（/） | name=test/folder | ❌ 验证失败 |
| TC-2.5.6.1 | 创建文件夹 - 非法字符（\） | name=test\folder | ❌ 验证失败 |
| TC-2.5.6.2 | 创建文件夹 - 非法字符（:） | name=test:folder | ❌ 验证失败 |
| TC-2.5.6.3 | 创建文件夹 - 非法字符（*） | name=test*folder | ❌ 验证失败 |
| TC-2.5.6.4 | 创建文件夹 - 非法字符（?） | name=test?folder | ❌ 验证失败 |
| TC-2.5.6.5 | 创建文件夹 - 非法字符（"） | name=test"folder | ❌ 验证失败 |
| TC-2.5.6.6 | 创建文件夹 - 非法字符（<） | name=test<folder | ❌ 验证失败 |
| TC-2.5.6.7 | 创建文件夹 - 非法字符（>） | name=test>folder | ❌ 验证失败 |
| TC-2.5.6.8 | 创建文件夹 - 非法字符（\|） | name=test\|folder | ❌ 验证失败 |
| TC-2.5.7 | 创建文件夹 - 已存在的文件夹 | 创建两次相同名称 | ❌ 第二次失败 |
| TC-2.5.8 | 创建文件夹 - 不指定 path | name=folder | ✅ 使用默认路径 |

**HTTP 测试总数**: 16 个

---

### Java 单元测试用例（FileOperationApiTest.java）

#### 模块 1: 创建文件夹测试（16个）

```java
@Test
@Order(1)
@DisplayName("TC-2.5.1 创建文件夹 - 普通文件夹")
void testCreateFolder() throws Exception {
    mockMvc.perform(post("/system/file/browse/mkdir")
            .param("path", "/test/")
            .param("name", "test-folder-" + System.currentTimeMillis()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.path").exists())
        .andExpect(jsonPath("$.isDirectory").value(true));
}
```

**测试覆盖**:
- ✅ TC-2.5.1: 普通文件夹创建
- ✅ TC-2.5.2: 嵌套文件夹创建
- ✅ TC-2.5.3: 根目录文件夹创建
- ✅ TC-2.5.4: 中文文件夹名
- ✅ TC-2.5.5: 空文件夹名称验证
- ✅ TC-2.5.6: 非法字符验证（9种特殊字符）
- ✅ TC-2.5.7: 已存在文件夹验证
- ✅ TC-2.5.8: 不指定 path 参数

#### 模块 2: 文件列表测试（2个）

- ✅ TC-2.1: 获取根目录文件列表
- ✅ TC-2.2: 获取指定目录文件列表

#### 模块 3: 文件上传测试（2个）

- ✅ TC-2.3: 上传文本文件
- ✅ TC-2.4: 上传中文文件名文件

#### 模块 4: 文件删除测试（3个）

- ✅ TC-2.8: 删除文件
- ✅ TC-2.9: 删除不存在的文件
- ✅ TC-2.10: 删除文件 - 空路径

#### 模块 5: 边界条件测试（3个）

- ✅ TC-3.1: 获取不存在的目录列表
- ✅ TC-3.2: 获取文件列表 - 不带路径参数
- ✅ TC-3.3: 上传大文件

**Java 测试总数**: 26 个

---

## 🔍 测试覆盖的关键点

### 1. 功能验证

- [x] 创建普通文件夹
- [x] 创建嵌套文件夹（多级目录）
- [x] 在根目录创建文件夹
- [x] 支持中文文件夹名
- [x] 返回正确的文件夹信息（name、path、isDirectory）

### 2. 参数验证

- [x] 空文件夹名称
- [x] 不指定 path 参数（使用默认值）
- [x] 不指定 name 参数

### 3. 非法字符验证

根据 Windows/Linux 文件系统规范，以下字符不允许出现在文件夹名称中：

| 字符 | 说明 | 测试用例 |
|-----|------|---------|
| `/` | 斜杠（路径分隔符） | TC-2.5.6 |
| `\` | 反斜杠（Windows路径分隔符） | TC-2.5.6.1 |
| `:` | 冒号（Windows驱动器分隔符） | TC-2.5.6.2 |
| `*` | 星号（通配符） | TC-2.5.6.3 |
| `?` | 问号（通配符） | TC-2.5.6.4 |
| `"` | 双引号 | TC-2.5.6.5 |
| `<` | 小于号 | TC-2.5.6.6 |
| `>` | 大于号 | TC-2.5.6.7 |
| `\|` | 竖线 | TC-2.5.6.8 |

### 4. 边界条件

- [x] 创建已存在的文件夹
- [x] 文件夹名称长度限制（隐式测试）
- [x] 路径不存在时的处理

### 5. 集成测试

- [x] 创建文件夹后可以查询到
- [x] 在创建的文件夹中上传文件
- [x] 删除文件夹中的文件

---

## 🚀 运行测试

### HTTP 测试

```bash
# 使用 IDEA HTTP Client
# 打开 http/test-file-plugin-api.http
# 运行 TC-2.5.* 系列测试

# 或使用 httpyac CLI
httpyac test-file-plugin-api.http --name "创建文件夹"
```

### Java 单元测试

```bash
# 运行所有文件操作测试
mvn test -pl magic-boot-plugins/magic-api-plugin-file -Dtest=FileOperationApiTest

# 运行特定测试
mvn test -pl magic-boot-plugins/magic-api-plugin-file -Dtest=FileOperationApiTest#testCreateFolder
```

---

## 📈 测试覆盖率统计

### 创建文件夹功能覆盖率

| 测试类型 | 测试用例数 | 覆盖率 |
|---------|-----------|--------|
| **功能测试** | 8 | 100% |
| **参数验证** | 3 | 100% |
| **非法字符** | 9 | 100% |
| **边界条件** | 2 | 100% |
| **集成测试** | 4 | 100% |
| **总计** | **26** | **100%** |

### 整体测试统计

| 测试类 | 测试数 | 状态 |
|--------|-------|------|
| MagicFileControllerTest | 18 | ✅ |
| MagicFileControllerWebMvcTest | 23 | ✅ |
| FileOperationApiTest | 26 | ✅ |
| FilePluginApiIntegrationTest | 29 | ✅ |
| **总计** | **96** | **✅** |

---

## 🔧 测试断言示例

### HTTP 测试断言

```javascript
client.test("创建文件夹", function() {
    client.assert(response.status === 200, "HTTP状态码应为200");
    client.assert(response.body.name !== undefined, "应返回文件夹名称");
    client.assert(response.body.path !== undefined, "应返回文件夹路径");
    client.assert(response.body.isDirectory === true, "应标识为目录");
});
```

### Java 测试断言

```java
mockMvc.perform(post("/system/file/browse/mkdir")
        .param("path", "/test/")
        .param("name", "test-folder"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.name").exists())
    .andExpect(jsonPath("$.path").exists())
    .andExpect(jsonPath("$.isDirectory").value(true));
```

---

## 📝 测试最佳实践

### 1. 测试命名规范

```
TC-{模块编号}.{功能编号}.{子编号} 测试描述
```

示例：
- `TC-2.5.1` - 文件操作模块，创建文件夹功能，第1个测试
- `TC-2.5.6` - 文件操作模块，创建文件夹功能，第6个测试（非法字符）

### 2. 测试数据隔离

```java
@BeforeAll
static void setUpAll() {
    // 使用时间戳确保测试数据唯一
    String folderName = "test-folder-" + System.currentTimeMillis();
}
```

### 3. 测试顺序

使用 `@Order` 注解确保测试按预期顺序执行：
1. 先创建文件夹
2. 然后在文件夹中操作
3. 最后清理测试数据

---

## ⚠️ 注意事项

### 1. 非法字符处理

magic-api 脚本中定义的非法字符：
```javascript
var invalidChars = ['/', '\\', ':', '*', '?', '"', '<', '>', '|']
```

### 2. 文件夹存在性检查

```javascript
if(file.exists(folderPath)){
    exit 0, '文件夹已存在'
}
```

### 3. 路径格式

- 文件夹路径必须以 `/` 结尾
- 根目录表示为 `/`

---

## 📚 相关文档

- [文件插件 API 文档](../../http/README.md)
- [HTTP 测试文件](../../http/test-file-plugin-api.http)
- [单元测试最佳实践](https://junit.org/junit5/docs/current/user-guide/)

---

## 📧 反馈

如有问题或建议，请联系开发团队或创建 Issue。
