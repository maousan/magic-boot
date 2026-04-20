# 亮灯链路模拟页面模块化改造变更日志

## 变更时间
- 2026-04-20

## 变更内容
- 重构页面 `light-chain-simulator.html`：
  - 新增模块页签：`业务模拟`、`定时器模拟`。
  - 业务模拟：
    - 新增波次单列表（模拟数据）。
    - 点击波次后自动调用上传接口 `/api/light/picking/upload`。
    - 明细表支持字段编辑（id/materialCode/batchNo/locationCode/planQuantity/actualQuantity/status/updateTime）。
    - 新增“完成”按钮，调用 `/api/light/picking/complete`。
    - 保留“亮灯控制”按钮，调用 `/api/light/control`。
  - 定时器模拟：
    - 支持配置波次、用户、间隔秒数。
    - 支持启动/停止定时器并周期调用 `/api/light/control`。
- 保留并增强请求/响应日志区与状态区，便于联调复盘。

## 影响范围
- 仅静态页面调整，不涉及后端业务代码。

## 回滚方式
- 用历史版本覆盖 `light-chain-simulator.html`。

## 补充（定时器接口约束）
- 定时器模拟已明确为固定调用 `亮灯灭灯控制.ms` 对应接口：`POST /api/light/control`。
- 页面新增“接口”只读字段，避免误解为可切换到其他接口。
- 定时器日志输出增加 endpoint 信息，便于联调排查。
