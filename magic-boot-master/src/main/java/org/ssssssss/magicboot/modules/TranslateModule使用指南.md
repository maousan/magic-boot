# TranslateModule (trans) 模块使用指南

## 概述

`TranslateModule` 是一个 Magic API 模块，模块名称为 `trans`，它提供了字典翻译功能，可以将数据列表中的原始字段值（如状态码 1）转换为可读文本（如"启用"），并支持从数据库查询字典项。

## 核心功能

1. **数据列表翻译** - 根据 JSON 或 Map 配置翻译数据列表
2. **字典项查询** - 从数据库查询字典项（支持缓存）
3. **单个值翻译** - 翻译单个字段值
4. **自动翻译** - 根据字典类型自动翻译字段
5. **缓存管理** - 清除或刷新字典缓存

## 方法列表

### 1. translate(dataList, configJson)

**描述**: 翻译数据列表（使用 JSON 配置）

**参数**:
- `dataList`: 数据列表 (List<Map<String, Object>>)
- `configJson`: JSON 配置字符串

**返回**: 翻译后的数据列表

**示例**:
```javascript
var data = db.select("select id, name, status from sys_user");

var translated = trans.translate(data, '''
{
  "status": {
    "target": "statusName",
    "dicts": [
      {"value": 1, "label": "启用"},
      {"value": 0, "label": "禁用"}
    ]
  }
}
''');

return translated;
```

### 2. translate(dataList, configMap)

**描述**: 翻译数据列表（使用 Map 配置）

**参数**:
- `dataList`: 数据列表 (List<Map<String, Object>>)
- `configMap`: 配置 Map

**返回**: 翻译后的数据列表

**示例**:
```javascript
var data = db.select("select id, name, type from sys_office");

// 从数据库获取字典项
var items = trans.getDictItems('office_type');

var config = {
  "type": {
    "翻译字段": "typeName",
    "dictConfig": items
  }
};

return trans.translate(data, config);
```

### 3. getDictItems(dictType)

**描述**: 根据字典类型获取字典项列表（带缓存）

**参数**:
- `dictType`: 字典类型（对应 sys_dict 表的 type 字段）

**返回**: 字典项列表 `[{"value": "1", "label": "部门"}, ...]`

**示例**:
```javascript
// 获取组织机构类型的字典项
var items = trans.getDictItems('office_type');
// 返回: [{"value": "1", "label": "部门"}, {"value": "2", "label": "公司"}]

return items;
```

### 4. translateValue(dictType, value)

**描述**: 翻译单个值

**参数**:
- `dictType`: 字典类型
- `value`: 原始值

**返回**: 翻译后的文本，未找到返回 null

**示例**:
```javascript
var label = trans.translateValue('office_type', '1');
// 返回: "部门"

return label;
```

### 5. translateByDictType(dataList, fieldName, dictType)

**描述**: 根据字典类型自动翻译字段（推荐使用）

**参数**:
- `dataList`: 数据列表
- `fieldName`: 要翻译的字段名
- `dictType`: 字典类型

**返回**: 翻译后的数据列表（自动添加 `字段名Name` 字段）

**示例**:
```javascript
var data = db.select("select id, name, type from sys_office");

// 自动从数据库查询 'office_type' 字典并翻译 'type' 字段
var translated = trans.translateByDictType(data, 'type', 'office_type');
// 自动添加 typeName 字段

return translated;
```

### 6. clearCache()

**描述**: 清除所有字典缓存

**示例**:
```javascript
trans.clearCache();
return '缓存已清除';
```

### 7. refreshCache(dictType)

**描述**: 刷新指定字典类型的缓存

**参数**:
- `dictType`: 字典类型

**示例**:
```javascript
trans.refreshCache('office_type');
return '缓存已刷新';
```

## 完整示例

### 示例 1: 用户列表翻译

```javascript
// 查询用户列表
var users = db.page("""
  select id, username, status, type, create_date
  from sys_user
  where is_del = 0
  order by create_date desc
""");

// 使用数据库字典翻译多个字段
var config = {
  "status": {
    "翻译字段": "statusName",
    "dictConfig": trans.getDictItems('user_status')
  },
  "type": {
    "翻译字段": "typeName",
    "dictConfig": trans.getDictItems('user_type')
  }
};

return trans.translate(users, config);
```

### 示例 2: 组织机构列表翻译
```javascript
// 查询组织机构
var offices = db.page("""
  select id, name, type, create_date
  from sys_office
  where is_del = 0
  order by create_date desc
""");

// 自动翻译（推荐方式）
return trans.translateByDictType(offices, 'type', 'office_type');
```

### 示例 3: 自定义字典配置
```javascript
var data = db.select("select id, name, level from sys_data");

// 自定义字典配置
var config = {
  "level": {
    "翻译字段": "levelDesc",
    "dictConfig": [
      {"value": 1, "label": "低"},
      {"value": 2, "label": "中"},
      {"value": 3, "label": "高"}
    ]
  }
};

return trans.translate(data, config);
```

### 示例 4: 动态获取字典并翻译
```javascript
// 查询订单列表
var orders = db.page("""
  select id, order_no, status, priority
  from sys_order
  where is_del = 0
  order by create_date desc
""");

// 动态获取字典项
var statusItems = trans.getDictItems('order_status');
var priorityItems = trans.getDictItems('order_priority');

// 构建配置
var config = {
  "status": {
    "dictConfig": statusItems
  },
  "priority": {
    "dictConfig": priorityItems
  }
};

return trans.translate(orders, config);
```

## 配置说明

### JSON 配置格式

```json
{
  "源字段名": {
    "target": "目标字段名（可选，默认为 源字段名Name）",
    "dicts": [
      {"value": 匹配值, "label": "显示文本"},
      ...
    ]
  },
  ...
}
```

**配置字段说明**:
- `target`: 翻译后的目标字段名（可选）
  - 如果不配置，自动使用 `源字段名 + "Name"`
  - 例如: `status` 字段会翻译到 `statusName`
- `dicts`: 字典配置列表（必需）
  - `value`: 要匹配的值
  - `label`: 匹配成功后显示的文本

**示例**:
```json
{
  "status": {
    "target": "statusText",
    "dicts": [
      {"value": 1, "label": "启用"},
      {"value": 0, "label": "禁用"}
    ]
  },
  "type": {
    "dicts": [
      {"value": "A", "label": "类型A"},
      {"value": "B", "label": "类型B"}
    ]
  }
}
```

在上述示例中：
- `status` 字段会翻译到 `statusText` 字段
- `type` 字段会翻译到 `typeName` 字段（自动命名）

### 字典表结构

本模块依赖以下数据库表：

**sys_dict (字典表)**:
- `type`: 字典类型（如 'office_type', 'user_status'）
- `desc_ribe`: 描述
- `sort`: 排序
- `is_del`: 删除标识

**sys_dict_items (字典项表)**:
- `value`: 值
- `label`: 标签（显示文本）
- `dict_id`: 关联字典 ID
- `sort`: 排序
- `is_del`: 删除标识

## 缓存机制

- 使用内存缓存（ConcurrentHashMap）
- 延迟加载：第一次查询时加载到缓存
- 自动管理：无需手动管理缓存生命周期
- 提供手动刷新: `refreshCache()` 和 `clearCache()` 方法

- 提升性能: 减少数据库查询次数

## 注意事项

1. **字段命名规则**
   - 如果未指定"翻译字段"，则自动使用 `源字段名 + "Name"`
   - 例如: `status` 字段翻译后为 `statusName`

2. **类型模糊匹配**
   - 支持 Integer/Long/String 等类型的自动匹配
   - 统一转为 String 进行比较

   - 例如: `1` (Integer) 和 `"1"` (String) 会被视为相同

3. **空值处理**
   - 字段不存在: 跳过翻译
   - 字段值为 null: 跳过翻译
   - 未匹配到字典项: 不添加翻译字段

4. **性能建议**
   - 使用 `translateByDictType()` 而不是手动配置（自动使用缓存）
   - 字典项会自动缓存，避免重复查询数据库
   - 如需更新字典,调用 `refreshCache()` 刷新缓存

## 与原 MapTranslateUtil 的对比

| 特性 | MapTranslateUtil | TranslateModule |
|------|------------------|------------------|
| 使用方式 | Java 静态方法调用 | Magic API 模块调用 |
| 调用位置 | 仅限 Java 代码 | Magic API 脚本和 Java 代码 |
| 字典查询 | 需手动配置 | 从数据库自动查询 |
| 缓存 | 无 | 自动缓存 |
| 返回值 | void（原地修改） | List（返回新列表） |

| 使用示例 | `MapTranslateUtil.translate(list, json)` | `trans.translate(list, json)` |

## 迁移指南

如果您正在使用 `MapTranslateUtil`，建议逐步迁移到 `trans` 模块：

**迁移前** (Java 代码):
```java
List<Map<String, Object>> dataList = ...;
MapTranslateUtil.translate(dataList, configJson);
```

**迁移后** (Magic API 脚本):
```javascript
var dataList = ...;
var translated = trans.translate(dataList, configJson);
```

**优势**:
- 更简洁的 API 接口
- 自动从数据库查询字典项
- 更好的性能（缓存机制）
- 在 Magic API 脚本中直接使用

