# 2026-04-07 库位码与电子标签码解绑能力计划

## 背景
- 需要基于 AIMS 接口 `Unlink article and label` 新增一个本地可复用函数。
- 需要完善 PDA 解绑页面调用，支持解绑时携带 `locationCode + labelCode`。

## 目标
- 新增 AIMS 解绑函数：输入 `labelCode`，调用 `/labels/unlink`。
- 新增/完善后端解绑 API：先 AIMS 解绑，再删除本地映射。
- 更新前端解绑表单与请求参数。
- 增加 `.http` 用例便于联调。

## 实施步骤
1. 新增函数文件 `function/aims/标签/解绑article和label.ms`。
2. 新增 API 文件 `api/东信和平/巷道灯/解绑库位码和电子标签码的绑定关系.ms`。
3. 修改 `location-label-mapping.html` 的解绑区域与请求逻辑。
4. 更新 `http/test-dongxinheping-location-label-mapping.http`，增加 `labelCode` 参数。
5. 新增 `http/test-dongxinheping-aims-unlink-article-label.http`。
6. 执行 `mvn -pl magic-boot-master -am -DskipTests compile` 进行编译校验。

## 风险与回退
- 风险：AIMS 解绑失败会导致本地解绑失败（按一致性设计）。
- 回退：回滚新增 API 与页面解绑参数改动，恢复旧删除调用。
