# 变更日志 - 2026-04-08 - 获取系统 MAC/IP

## 变更项
- 新增响应对象：`LedSystemNetworkResponse`。
- 新增接口：`POST /led/system/network`。
- 新增服务实现：`querySystemNetwork`。
- 增加 payload 网络信息解析能力：IP 与 MAC。
- 更新 HTTP 测试文件，新增系统网络查询用例。
- 新增服务测试：解析 IP 与 MAC 成功场景。

## 影响范围
- 仅新增能力，不影响已有控制与查询接口行为。
