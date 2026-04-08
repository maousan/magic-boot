# 东信和平库位-巷道灯绑定页面测试报告

## 测试时间
- 2026-04-04 01:25（Asia/Shanghai）

## 测试环境
- 服务地址：`http://localhost:8090`
- 测试方式：`curl` 调用 `POST/DELETE /api/location/led-mapping`
- 页面入口：`/pda/led-mapping.html`（静态文件已落地）

## 结果汇总
- 共执行 5 条用例。
- 5 条均返回业务体 `code=500`，均为后端内部异常（返回了错误ID）。
- 结论：前端页面功能已实现，但当前后端接口在本环境不可正常执行业务流程，需先排查后端脚本/数据表问题后再进行通过性回归。

## 详细结果
1. 创建绑定-成功（预期）
- 请求：`POST /api/location/led-mapping`
- 参数：`{\"lotNo\":\"LOT_PDA_20260404_001\",\"ledId\":\"LED_PDA_001\"}`
- 预期：`code=200`
- 实际：`code=500`，错误ID=`753947dea03745b9b1d84c638eb31fd6`
- 结论：失败

2. 创建绑定-重复绑定（预期失败）
- 请求：`POST /api/location/led-mapping`
- 参数：`{\"lotNo\":\"LOT_PDA_20260404_001\",\"ledId\":\"LED_PDA_002\"}`
- 预期：后端返回“已存在绑定关系”类错误
- 实际：`code=500`，错误ID=`25244280eb15462cac4570ee5a1a914a`
- 结论：失败（未返回预期业务错误文案）

3. 删除绑定-成功（预期）
- 请求：`DELETE /api/location/led-mapping?lotNo=LOT_PDA_20260404_001`
- 预期：`code=200`，`data>=0`
- 实际：`code=500`，错误ID=`d53d04dd2c56469f88ef89ecd2912952`
- 结论：失败

4. 删除绑定-不存在记录
- 请求：`DELETE /api/location/led-mapping?lotNo=LOT_PDA_20260404_999`
- 预期：`code=200`，`data=0`
- 实际：`code=500`，错误ID=`8430080940f943a1a48da96f00d5e046`
- 结论：失败

5. 创建绑定-缺少 lotNo
- 请求：`POST /api/location/led-mapping`
- 参数：`{\"ledId\":\"LED_ONLY\"}`
- 预期：参数校验错误（通常 4xx 或业务错误提示）
- 实际：`code=500`，错误ID=`397b8c9fe2ee42e39fbeba47149aefa4`
- 结论：失败

## 建议
- 优先查看 magic-api 错误日志（按上述错误ID检索）定位异常根因。
- 建议后端在这两个接口补充入参校验（lotNo/ledId）与明确业务错误文案，避免统一 500。
- 后端修复后，按 `http/test-dongxinheping-location-led-mapping.http` 重新回归。
