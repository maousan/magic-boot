# 拣货完成灯控失败不中断业务变更日志

## 背景

`拣货完成` 接口在完成业务状态同步后，会调用 AIMS 灯控和巷道灯控制。原实现中灯控返回失败会抛出异常，并被外层总 `catch` 转换为接口 500，导致灯控异常影响拣货完成主业务响应。

## 变更内容

- 将灯控结果校验从抛异常调整为记录日志，灯控返回为空或 `success == false` 时仅输出 `warn`。
- 为 AIMS 关灯、多人亮灯、单人亮灯分别增加局部 `try/catch`，防止灯控调用异常冒泡到外层业务异常处理。
- 为巷道灯开关调用增加局部 `try/catch`，单个库位灯控失败不影响后续库位处理。
- 保持请求校验、业务数据更新、库位状态重算、最终成功响应不变。

## 影响范围

- 修改文件：`data/dongxinheping/api/东信和平/亮灯对接/拣货完成.ms`
- 不影响 `拣货数据上传`、`亮灯灭灯控制` 等其他接口。
- 非灯控异常仍按原逻辑进入外层 `catch` 并返回 500。

## 验证方式

- 静态检查 `拣货完成.ms` 中不再存在 `assertLightControlSuccess` 调用。
- 静态检查灯控调用边界包含局部失败日志：
  - `dispatch_turn_off_failed`
  - `dispatch_turn_on_multi_failed`
  - `dispatch_turn_on_single_failed`
  - `dispatch_aisle_turn_off_failed`
  - `dispatch_aisle_turn_on_failed`
- 编译和接口联调由使用方按项目约定自行执行。
