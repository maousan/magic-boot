# 亮灯链路模拟测试页面变更日志

## 变更时间
- 2026-04-20

## 变更内容
- 新增 PC 端静态页面：`light-chain-simulator.html`
  - 支持手动单步调用：
    - `POST /api/light/picking/upload`
    - `POST /api/light/picking/complete`
    - `POST /api/light/control`
  - 支持一键顺序执行整链路（upload U1 -> upload U2 -> complete U2 -> control）。
  - 支持编辑明细 JSON、基础参数、控制模式与颜色。
  - 新增请求/响应日志窗口，记录 URL、请求体、状态码、响应体。
- 更新导航页 `mapping-entry.html`
  - 增加“亮灯链路模拟测试（PC）”入口。

## 影响范围
- 仅新增/修改静态页面文件，不影响后端业务逻辑。

## 回滚方式
- 删除 `light-chain-simulator.html` 并移除 `mapping-entry.html` 对应入口卡片。
