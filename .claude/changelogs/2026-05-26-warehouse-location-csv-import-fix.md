# 仓库库位 CSV 导入取值修复

## 背景

批量导入仓库库位接口在导入 CSV 文件时，Hutool 返回的行对象类型为 `cn.hutool.core.text.csv.CsvRow`。
原脚本后续统一使用 `row[key]` 按表头取值，MagicScript 不支持对 `CsvRow` 使用 `[]` 动态下标访问，导致接口抛出：

```text
操作符 `.或[]` 不支持 (cn.hutool.core.text.csv.CsvRow,java.lang.String) 类型
```

## 变更

- CSV 读取后，将每一行 `CsvRow` 通过 `getFieldMap()` 转换为普通 Map。
- 保持 Excel 分支和后续字段匹配逻辑不变。

## 影响范围

- 仅影响 `POST /warehouse-location/import` 的 CSV 文件导入路径。
- Excel 导入路径未调整。

## 验证

- 已确认当前 Hutool `CsvRow` 类型提供 `getFieldMap()` 方法。
- 已做静态检查，确认脚本中 `row[key]` 的 CSV 来源已转换为 Map。
