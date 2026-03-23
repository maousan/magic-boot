# CC-Connect 飞书接入配置说明

## 配置文件

已为您创建 `cc-connect-config.toml` 配置文件，包含以下配置：

- **项目名称**: magic-boot
- **工作目录**: D:/IdeaProjects/magic-boot
- **飞书 App ID**: cli_a94a71d379f9dcd1
- **飞书 App Secret**: NlRqPKFQ7jlYHOrD4cbFJb2iIBU3lz6D

## 后续配置步骤

### 1. 安装 cc-connect

从 GitHub 下载 cc-connect 工具：

```bash
# macOS/Linux
curl -L https://github.com/chenhg5/cc-connect/releases/latest/download/cc-connect-$(uname -s)-$(uname -m) -o cc-connect
chmod +x cc-connect

# Windows
# 访问 https://github.com/chenhg5/cc-connect/releases/latest 下载 cc-connect-windows-amd64.exe
```

或使用 Go 安装：

```bash
go install github.com/chenhg5/cc-connect@latest
```

### 2. 在飞书开放平台配置应用

#### 2.1 启用机器人能力
1. 进入飞书开放平台控制台
2. 选择应用 → 应用能力 → 机器人
3. 启用机器人功能

#### 2.2 配置权限
在「权限管理」中申请以下权限：

| 权限名称 | 权限标识 | 用途 |
|---------|---------|------|
| 获取与更新用户基本信息 | `contact:user.base:readonly` | 获取用户信息 |
| 接收群聊消息 | `im:message.group:receive` | 接收群消息 |
| 接收单聊消息 | `im:message.p2p:receive` | 接收私聊消息 |
| 读取群消息 | `im:message.group_msg:readonly` | 读取群消息内容 |
| 读取单聊消息 | `im:message.p2p_msg:readonly` | 读取私聊内容 |
| 以应用身份发送群消息 | `im:message:send_as_bot` | 发送消息回复用户 |

#### 2.3 配置事件订阅（长连接模式）
1. 进入「事件订阅」页面
2. 选择「长连接模式」
3. 启用长连接
4. 添加事件：`im.message.receive_v1`（接收消息）

#### 2.4 发布应用
1. 进入「版本管理与发布」
2. 创建版本并发布
3. 等待审核通过（企业版）或立即可用（个人版）

### 3. 启动 cc-connect

使用配置文件启动 cc-connect：

```bash
cc-connect -config cc-connect-config.toml
```

或使用默认配置文件路径（cc-connect 会自动查找当前目录的 config.toml）：

```bash
cc-connect
# 如果您的配置文件名为 config.toml，可以重命名：
# mv cc-connect-config.toml config.toml
# cc-connect
```

### 4. 验证连接

启动成功后，您会看到以下日志：

```
level=INFO msg="platform started" project=magic-boot platform=feishu
level=INFO msg="cc-connect is running" projects=1
[Info] connected to wss://msg-frontier.feishu.cn/ws/v2?...
```

### 5. 添加机器人到会话

#### 单聊使用
在飞书中搜索您的机器人名称，直接发送消息。

#### 群聊使用
1. 进入目标群聊
2. 点击群设置 → 群机器人
3. 添加您创建的机器人

### 6. 测试功能

在飞书中发送消息测试：

```
用户: 帮我分析一下当前项目的结构

cc-connect: 🤔 思考中...
cc-connect: 🔧 执行: Bash(ls -la)
cc-connect: ✅ 这是一个 Spring Boot 项目，包含以下目录...
```

## 架构说明

```
┌─────────────────────────────────────────────────────────────┐
│                         飞书云                               │
│                                                              │
│   用户消息 ──→ 飞书开放平台 ──→ WebSocket Gateway            │
│                                      │                       │
└──────────────────────────────────────┼───────────────────────┘
                                       │
                                       │ WebSocket 长连接
                                       │ (无需公网IP)
                                       ▼
┌─────────────────────────────────────────────────────────────┐
│                      你的本地环境                            │
│                                                              │
│   cc-connect ◄──► Claude Code CLI ◄──► magic-boot 项目      │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## 优势

✅ **无需公网 IP** - 使用长连接模式，无需暴露本地服务到公网
✅ **无需域名** - 不需要购买和配置域名
✅ **无需 HTTPS 证书** - 不需要配置 SSL 证书
✅ **无需反向代理** - 不需要使用 ngrok、frp 等工具
✅ **简单易用** - 配置简单，启动即可使用

## 常见问题

### Q: 长连接断开怎么办？
cc-connect 内置了自动重连机制，断开后会自动尝试重新连接。

### Q: 消息发送后没有响应？
检查以下项目：
1. cc-connect 服务是否正常运行
2. 长连接是否建立成功（查看日志）
3. 事件订阅是否配置了 `im.message.receive_v1`

### Q: 提示权限不足？
确保已在「权限管理」中申请并获得了所有必要权限，并发布了新版本。

## 参考链接

- [cc-connect GitHub](https://github.com/chenhg5/cc-connect)
- [飞书开放平台](https://open.feishu.cn/)
- [飞书开放平台文档](https://open.feishu.cn/document/)
- [飞书 WebSocket 长连接模式](https://open.feishu.cn/document/ukTMukTMukTM/uYjNwUjL2YDM14iN2ATN)
