# DXHP 管理后台 验收报告

> 阶段：阶段 3（验收闭环）
> 验收日期：2026-05-21
> 关联方案 doc：`.codestable/features/2026-05-21-dxhp-admin-panel/dxhp-admin-panel-design.md`

## 1. 接口契约核对

对照方案第 2.1 节名词层逐一核查：

**接口示例逐项核对**：
- [x] `LedDevice`（src/types/index.ts）：{ macAddress, ip, remark } → 与方案一致
- [x] `LocationLedBinding`（src/types/index.ts）：{ id, locationCode, ledId, color, status } → 与方案一致
- [x] `LocationLabelBinding`（src/types/index.ts）：{ id, locationCode, labelCode, bindTime } → 与方案一致
- [x] `BatchEpcBinding`（src/types/index.ts）：{ id, batchId, epc, status, bindTime, unbindTime? } → 与方案一致
- [x] `ImportResult`（src/types/index.ts）：{ totalRows, successCount, failCount, errors } → 与方案一致
- [x] `LedControlCommand`（src/types/index.ts）：{ ledId, command, port, timeoutMs? } → 与方案一致
- [x] 额外类型 `LedControlResult`、`PageResult<T>`、`ApiResponse<T>` → 方案未定义但合理扩展

**API 端点逐项核对**：
- [x] `GET /location/led-devices` → led-device.ts:getLedDevices
- [x] `POST /location/led-device-control` → led-device.ts:controlLedDevice
- [x] `GET /location/led-mapping/list` → led-mapping.ts:getLedMappingList（新增后端）
- [x] `POST /location/led-mapping` → led-mapping.ts:createLedMapping
- [x] `DELETE /location/led-mapping` → led-mapping.ts:deleteLedMapping
- [x] `GET /location/label-mapping/list` → label-mapping.ts:getLabelMappingList（新增后端）
- [x] `POST /location/label-mapping` → label-mapping.ts:createLabelMapping
- [x] `DELETE /location/label-mapping` → label-mapping.ts:deleteLabelMapping
- [x] `GET /location/batch-epc/list` → batch-epc.ts:getBatchEpcList
- [x] `POST /location/batch-epc/bind` → batch-epc.ts:bindBatchEpc
- [x] `POST /location/batch-epc/unbind` → batch-epc.ts:unbindBatchEpc
- [x] `POST /location/warehouse-location/import` → warehouse.ts:importWarehouseLocations

**名词层"现状 → 变化"逐项核对**：
- [x] 新增独立 Vue3 项目 → dongxinheping-admin/ 目录结构一致
- [x] 新增 2 个后端列表查询 API → LED绑定列表查询.ms + 标签绑定列表查询.ms

**流程图核对**（第 2.2 节 mermaid 图）：
- [x] Login → AdminLayout → 5 个业务页面 → 对应 API 模块 → 全部在 router/index.ts 和 views/ 有落点

## 2. 行为与决策核对

**需求摘要逐项验证**：
- [x] Vue3 + Vite + Naive UI 独立项目 → package.json 依赖一致
- [x] 登录凭证配置文件设置 → .env + auth/index.ts 校验逻辑
- [x] PDA 功能桌面端表格化 CRUD → 5 个业务页面均使用 NDataTable

**明确不做逐项核对**（第 3 节反向核对项）：
- [x] sa-token / StpUtil 无引入（grep 确认）
- [x] /api/light/picking / /api/light/control 无调用（grep 确认）
- [x] RFID 控制 API 无调用（grep 确认）
- [x] 移动端 viewport meta / 媒体查询无引入（grep 确认 src/ 目录）

**关键决策落地**：
- [x] D1 独立项目：dongxinheping-admin/ 独立于 magic-boot-vben
- [x] D2 前端配置认证：auth/index.ts 使用 .env 凭证 + localStorage
- [x] D3 构建产物部署：vite.config.ts outDir → plugin static/admin/

**编排层"现状 → 变化"逐项核对**：
- [x] 统一布局 AdminLayout → src/layouts/AdminLayout.vue
- [x] 共享 axios 实例 → src/api/request.ts
- [x] 错误拦截弹出提示 → request.ts response interceptor

**流程级约束核对**：
- [x] API 调用不携带 auth header → request.ts 无 Authorization header
- [x] code !== 200 弹出 Naive UI message → createDiscreteApi(['message'])
- [x] LED 控制无确认重试 → LedDevice.vue executeControl 直接调用

**挂载点反向核对**：
- [x] M1: `static/admin/` → vite.config.ts outDir 已配置
- [x] M2: `.env` → 存在且含 VITE_ADMIN_USER/PASS
- [x] M3: `server.proxy` → vite.config.ts proxy /api → localhost:8090
- [x] M4: `data/dongxinheping/api/东信和平/库位/` → LED绑定列表查询.ms + 标签绑定列表查询.ms
- [x] 反向 grep：dongxinheping-admin/src/ 中无方案外引用
- [x] 拔除沙盘推演：删除 dongxinheping-admin/ + 2 个新 .ms + Flyway 迁移 + 恢复 2 个 .ms 改动 → feature 完全消失

**方案外文件改动**：
- 修改了 `绑定库位码和标签码.ms` 和 `解绑库位码和标签码.ms`（追加本地 t_location_label 写入/删除）
- 原因：标签绑定数据在 AIMS 端，本地无表，需新建 t_location_label 表并在创建/解绑时同步
- 新增 `V20260521.001__create_t_location_label_table.sql` Flyway 迁移
- 偏差评估：改动最小化（在 AIMS 调用成功后追加本地操作），不影响原有逻辑

## 3. 验收场景核对

- [x] **S1**：输入正确凭证 → 跳转到 Dashboard
  - 证据来源：代码（Login.vue 校验 .env 值 + router 守卫）
  - 结果：通过

- [x] **S2**：输入错误凭证 → 显示错误提示，停留登录页
  - 证据来源：代码（Login.vue else 分支 window.message.error）
  - 结果：通过

- [x] **S3**：未登录访问管理页 → 重定向到登录页
  - 证据来源：代码（router/index.ts beforeEach 守卫）
  - 结果：通过

- [x] **S4**：LED 设备列表展示 MAC、IP、备注
  - 证据来源：代码（LedDevice.vue columns 定义）
  - 结果：通过

- [x] **S5**：LED 控制面板：选设备+命令+端口+超时→执行并显示结果
  - 证据来源：代码（LedDevice.vue controlLedDevice + NDescriptions 结果展示）
  - 结果：通过

- [x] **S6**：LED 绑定表格分页展示
  - 证据来源：代码（LedMapping.vue NDataTable + pagination）
  - 结果：通过

- [x] **S7**：LED 绑定创建/删除操作后表格刷新
  - 证据来源：代码（LedMapping.vue handleCreate/handleDelete 后 fetchData）
  - 结果：通过

- [x] **S8**：标签绑定表格分页展示
  - 证据来源：代码（LabelMapping.vue NDataTable + pagination）
  - 结果：通过

- [x] **S9**：标签绑定创建/解绑操作后表格刷新
  - 证据来源：代码（LabelMapping.vue handleCreate/handleDelete 后 fetchData）
  - 结果：通过

- [x] **S10**：库位导入下载模板
  - 证据来源：代码（WarehouseImport.vue downloadTemplate 生成 CSV）
  - 结果：通过

- [x] **S11**：上传合规文件显示导入结果
  - 证据来源：代码（WarehouseImport.vue handleUpload + NDescriptions + NDataTable 错误展示）
  - 结果：通过

- [x] **S12**：批次 EPC 表格分页展示 + 搜索
  - 证据来源：代码（BatchEpc.vue searchKeyword + getBatchEpcList params）
  - 结果：通过

- [x] **S13**：批次 EPC 绑定/解绑操作
  - 证据来源：代码（BatchEpc.vue handleBind/handleUnbind）
  - 结果：通过

**前端浏览器验证**：
- [ ] 需要 dev server 启动后浏览器实际验证（需后端服务运行）

## 4. 术语一致性

对照方案第 0 节术语：

- DXHP：代码中未使用，仅在文档注释中可能出现 ✓
- Admin Panel：项目名 dongxinheping-admin，一致 ✓
- LED 映射：代码中 LedMapping / led-mapping，一致 ✓
- 标签映射：代码中 LabelMapping / label-mapping，一致 ✓
- 亮灯链路：代码中未引用（明确不做） ✓

防冲突检查：方案术语均在代码中一致使用，无冲突 ✓

## 5. 架构归并

- [x] ARCHITECTURE.md（`.codestable/architecture/ARCHITECTURE.md`）：已在"子系统 / 模块索引"新增 `dongxinheping-admin` 条目，描述项目路径、构建产物、访问路径、认证方式、功能范围、与 PDA 页面关系 ✓
- attention.md：评估是否需要补入新规约。前端配置认证的约束已在 ARCHITECTURE.md 描述，不需要额外条目

## 6. requirement 回写

- 方案 frontmatter 无 `requirement` 字段
- 本 feature 新增了用户可感能力（桌面端管理后台）
- 触发 backfill：需新建 requirement doc，status: current

## 7. roadmap 回写

- 方案 frontmatter 无 `roadmap` / `roadmap_item` 字段
- 非 roadmap 起头 → 跳过

## 8. attention.md 候选盘点

- 候选 1：`dongxinheping-admin` 项目开发时需 `cd dongxinheping-admin && npm run dev`（端口 3200），构建用 `npm run build`（产物直接输出到 plugin static/admin/）。下一个改这个前端的人会撞"怎么启动"的问题
- 候选 2：标签绑定列表依赖 `t_location_label` 表（Flyway V20260521.001），需重启后端生效。已有数据不会自动同步

## 9. 遗留

- 已知限制：标签绑定列表仅展示本地 `t_location_label` 表数据，历史已通过 AIMS 绑定的记录需手动同步或重新绑定
- 已知限制：库位导入模板为 CSV 格式而非 .xlsx（浏览器生成限制）
- 后续优化点（非 issue）：
  - LED 设备列表可增加刷新按钮
  - WarehouseImport 模板可改为后端下载 .xlsx
  - 标签绑定历史数据同步可做一次性 SQL 脚本
