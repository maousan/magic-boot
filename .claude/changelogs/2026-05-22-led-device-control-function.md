# 巷道灯设备控制函数变更日志

## 变更内容

- 新增 `data/dongxinheping/function/led/控制巷道灯设备开关.ms`。
- 新函数复用设备控制接口的 `server/client` 模式、端口命令解析、状态更新和 TCP 客户端发送逻辑。
- `根据库位码亮巷道灯.ms` 改为先按库位码查询绑定关系，再调用新函数执行亮灯。

## 影响范围

- `turnOnAisleLedByLotNo` 的核心控制逻辑不再直接调用插件接口。
- 返回结果保留原有关键字段，并新增 `controlResult` 用于查看底层控制函数结果。
