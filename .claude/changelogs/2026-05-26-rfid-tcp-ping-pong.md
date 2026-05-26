# RFID TCP ping/pong 心跳变更日志

## 变更时间

2026-05-26

## 变更范围

`magic-plugin-zintis-rfid`

## 变更内容

1. TCP 服务端支持纯文本心跳帧 `ping`。
2. 服务端收到 `ping` 后直接回复 `pong`。
3. 心跳帧仍使用现有 TCP framing：`4字节大端长度 + UTF-8 内容`。
4. 收到 `ping` 时会更新终端最后活跃时间。

## 协议示例

客户端发送：

```text
00 00 00 04 70 69 6E 67
```

服务端回复：

```text
00 00 00 04 70 6F 6E 67
```
