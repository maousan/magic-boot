# 亮灯链路模拟测试页面计划

## 目标
- 新增一个 PC 端页面，用于模拟东信和平亮灯链路：`upload -> complete -> control`。
- 支持手动单步触发与一键整链路触发，便于联调与回归。

## 范围
- 新增静态页面：`magic-boot-master/src/main/resources/static/pda/light-chain-simulator.html`
- 更新导航入口：`magic-boot-master/src/main/resources/static/pda/mapping-entry.html`

## 关键功能
1. 可配置基础参数：`waveNo/userId/userName/stationCode`。
2. 可编辑 `details` 明细（JSON 文本区）。
3. 单步按钮：
   - `拣货数据上传(upload)`
   - `拣货完成(complete)`
   - `亮灯控制(control ON/OFF)`
4. 一键链路按钮：按顺序执行 upload(U1) -> upload(U2) -> complete(U2) -> control(mode=3)。
5. 请求/响应日志面板：显示时间、URL、请求体、状态码、响应体。

## 风险
- 页面不直接读取服务日志文件，只展示接口请求与响应。
- 若服务部署路径变化，需要调整默认 API 前缀。

## 验证
- 页面可访问并展示。
- 单步按钮可成功调用目标接口并显示响应。
- 一键链路执行顺序正确，任一步失败会中断并提示。
