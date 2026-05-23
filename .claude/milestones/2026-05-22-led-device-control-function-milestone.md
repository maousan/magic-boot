# 巷道灯设备控制函数里程碑

## 完成内容

- 新增可复用巷道灯设备控制函数 `/led/controlAisleLedDevice`。
- 亮灯函数 `/led/turnOnAisleLedByLotNo` 已接入新控制函数。
- 关灯函数 `/led/turnOffAisleLedByLotNo` 已接入新控制函数。
- 新函数支持 `mode/ledId/command/port/timeoutMs/waitResponse` 参数。
