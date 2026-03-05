# 日志文件下载 API 文档

## 功能概述

日志文件下载功能允许已登录用户通过HTTP接口下载系统的日志文件，包括当前实时日志和历史归档日志。

## 接口列表

### 1. 下载日志文件

**接口地址**: `GET /logs/download`

**权限要求**: 需要登录（Sa-Token验证）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| type | String | 否 | 日志类型：`application`（默认）或 `error` | application |
| startDate | String | 否 | 开始日期（格式：yyyy-MM-dd），不指定则下载当前日志 | 2026-03-01 |
| endDate | String | 否 | 结束日期（格式：yyyy-MM-dd），不指定则等于startDate | 2026-03-05 |

**请求示例**:

```bash
# 下载当前应用日志
curl -X GET "http://localhost:8089/logs/download?type=application" \
  -H "satoken: YOUR_TOKEN"

# 下载当前错误日志
curl -X GET "http://localhost:8089/logs/download?type=error" \
  -H "satoken: YOUR_TOKEN"

# 下载指定日期的归档日志
curl -X GET "http://localhost:8089/logs/download?type=error&startDate=2026-03-01" \
  -H "satoken: YOUR_TOKEN"

# 下载日期范围的归档日志
curl -X GET "http://localhost:8089/logs/download?type=error&startDate=2026-03-01&endDate=2026-03-05" \
  -H "satoken: YOUR_TOKEN"
```

**响应**:

- **成功**: 返回日志文件内容（二进制流），浏览器会自动下载
- **失败**: 返回JSON格式的错误信息

**成功响应头**:
```
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="all.log"
```

**失败响应示例**:
```json
{
  "success": false,
  "message": "未授权访问，请先登录",
  "timestamp": 1709616000000
}
```

**HTTP状态码**:

| 状态码 | 说明 |
|--------|------|
| 200 | 下载成功 |
| 400 | 请求参数错误（如无效的日志类型） |
| 401 | 未授权（未登录或token无效） |
| 404 | 日志文件不存在 |
| 500 | 服务器内部错误 |

---

### 2. 列出可用日志文件

**接口地址**: `GET /logs/list`

**权限要求**: 需要登录（Sa-Token验证）

**请求示例**:

```bash
curl -X GET "http://localhost:8089/logs/list" \
  -H "satoken: YOUR_TOKEN"
```

**响应示例**:

```json
{
  "success": true,
  "data": [
    {
      "type": "application",
      "fileName": "all.log",
      "size": 1048576,
      "lastModified": 1709616000000
    },
    {
      "type": "error",
      "fileName": "error.log",
      "size": 524288,
      "lastModified": 1709615900000
    },
    {
      "type": "archived",
      "fileName": "error.2026-03-04.0.log.gz",
      "size": 102400,
      "lastModified": 1709529600000
    }
  ]
}
```

---

## 使用场景

### 场景1: 下载今天的应用日志

```bash
# 获取token（假设已登录）
TOKEN="your-sa-token-here"

# 下载当前应用日志
curl -X GET "http://localhost:8089/logs/download?type=application" \
  -H "satoken: $TOKEN" \
  -o application.log
```

### 场景2: 下载最近3天的错误日志

```bash
# 下载2026-03-03到2026-03-05的错误日志
curl -X GET "http://localhost:8089/logs/download?type=error&startDate=2026-03-03&endDate=2026-03-05" \
  -H "satoken: $TOKEN" \
  -o error_logs.log
```

### 场景3: 在前端页面中使用

```html
<!DOCTYPE html>
<html>
<head>
    <title>日志下载</title>
</head>
<body>
    <h1>日志文件下载</h1>
    
    <button onclick="downloadLog('application')">下载应用日志</button>
    <button onclick="downloadLog('error')">下载错误日志</button>
    
    <script>
        function downloadLog(type) {
            // 获取token（假设已存储在localStorage）
            const token = localStorage.getItem('satoken');
            
            // 构造下载URL
            const url = `http://localhost:8089/logs/download?type=${type}`;
            
            // 创建隐藏的iframe进行下载
            const iframe = document.createElement('iframe');
            iframe.style.display = 'none';
            iframe.src = url;
            document.body.appendChild(iframe);
            
            // 5秒后移除iframe
            setTimeout(() => {
                document.body.removeChild(iframe);
            }, 5000);
        }
    </script>
</body>
</html>
```

### 场景4: 使用fetch API下载

```javascript
async function downloadLog(type, startDate, endDate) {
    const token = localStorage.getItem('satoken');
    
    let url = `http://localhost:8089/logs/download?type=${type}`;
    if (startDate) url += `&startDate=${startDate}`;
    if (endDate) url += `&endDate=${endDate}`;
    
    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'satoken': token
            }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        // 获取文件名
        const contentDisposition = response.headers.get('Content-Disposition');
        const fileName = contentDisposition
            ? contentDisposition.split('filename=')[1]
            : `${type}.log`;
        
        // 下载文件
        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(downloadUrl);
        document.body.removeChild(a);
        
    } catch (error) {
        console.error('下载失败:', error);
        alert('下载失败: ' + error.message);
    }
}
```

---

## 安全性说明

### 1. 权限验证
- 所有接口都需要Sa-Token验证
- 未登录用户将收到401错误
- 每次下载都会记录用户ID和IP地址

### 2. 路径安全
- 仅支持预定义的日志类型（application/error）
- 不允许下载任意路径的文件
- 防止目录遍历攻击

### 3. 文件访问控制
- 只能访问`./logs/`目录下的文件
- 归档日志只能访问`./logs/archived/`目录
- 文件名经过严格验证

---

## 注意事项

### 1. 大文件下载
- 当前日志文件可能很大（几百MB）
- 建议在非高峰期下载
- 下载时会占用服务器内存

### 2. 归档日志格式
- error日志归档格式：`error.yyyy-MM-dd.N.log.gz`（已压缩）
- application日志归档格式：`magic-boot-yyyyMMdd.log`（未压缩）
- 多个归档文件会合并下载

### 3. 日期范围
- 开始日期和结束日期可以相同（下载单天）
- 日期范围不能超过系统归档保留期（默认14天）
- 日期格式必须严格遵循：`yyyy-MM-dd`

### 4. 并发下载
- 支持多个用户同时下载
- 不会影响日志文件的正常写入
- 建议控制并发下载数量

---

## 日志记录

所有下载操作都会记录到系统日志：

```
2026-03-05 14:00:00.000 INFO  [ReqId:xxx] [/] [http-nio-8089-exec-1] o.s.m.controller.LogDownloadController : 用户 1001 请求下载日志文件，类型: error, 日期范围: 2026-03-01 ~ 2026-03-05
2026-03-05 14:00:00.100 INFO  [ReqId:xxx] [/] [http-nio-8089-exec-1] o.s.m.controller.LogDownloadController : 用户 1001 下载归档日志，类型: error, 日期范围: 2026-03-01 ~ 2026-03-05
```

---

## 常见问题

### Q1: 下载时提示401未授权？

**A**: 请确保请求头中包含有效的Sa-Token：
```bash
curl -H "satoken: YOUR_VALID_TOKEN" ...
```

### Q2: 下载的文件是空的？

**A**: 可能原因：
- 日志文件确实为空（系统刚启动）
- 请求的日期范围内没有归档日志
- 日志文件路径配置错误

### Q3: 下载速度很慢？

**A**: 可能原因：
- 日志文件很大（几百MB）
- 服务器网络带宽限制
- 建议在非高峰期下载

### Q4: 能否下载实时日志？

**A**: 可以，不指定`startDate`参数即可下载当前正在写入的日志文件。但注意文件可能不完整（正在写入中）。

---

## 技术实现

### 核心类
- `LogDownloadController.java` - 日志下载控制器
- 使用Spring Boot ResponseEntity处理文件下载
- Sa-Token进行权限验证
- Java NIO进行文件读取

### 依赖
- Spring Boot 3.1.2
- Sa-Token 1.35.0.RC
- Java 17

---

## 更新日志

### v1.0.0 (2026-03-05)
- ✅ 初始版本发布
- ✅ 支持当前日志下载
- ✅ 支持历史归档日志下载
- ✅ 支持日期范围过滤
- ✅ Sa-Token权限验证
- ✅ 日志文件列表查询接口
