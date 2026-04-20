# 里程碑 - 查询信号强度功能

## 里程碑结果
- 已完成 `magic-plugin-zintis-led` 查询信号强度能力交付。

## 交付清单
- API：`POST /led/system/signal-strength`
- DTO：`LedSignalStrengthResponse`
- Service：`LedControlService#querySignalStrength`
- 测试：`LedControlServiceTest` 新增信号强度解析场景
- HTTP 示例：`http/test-magic-plugin-zintis-led.http`

## 状态
- 代码已完成并可打包部署。
