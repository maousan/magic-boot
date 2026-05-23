# INFO 日志中文乱码修复

## 背景

INFO 级别日志写入 `logs/all.log`，该文件由 `ALL_FILE` appender 生成。原配置中 `ERROR_FILE` 已显式指定 `UTF-8`，但 `ALL_FILE` 和 `CONSOLE` encoder 未指定 charset，在 Windows 环境下可能使用系统默认编码，导致日志查看端按 UTF-8 读取时中文乱码。

## 变更内容

- 为 `CONSOLE` appender 的 `PatternLayoutEncoder` 增加 `<charset>UTF-8</charset>`。
- 为 `ALL_FILE` appender 的 `PatternLayoutEncoder` 增加 `<charset>UTF-8</charset>`。
- 保持原有日志格式、日志级别、滚动策略和 WebSocket 日志推送逻辑不变。

## 影响范围

- 新生成的控制台日志输出明确使用 UTF-8。
- 新生成的 `logs/all.log` 明确使用 UTF-8。
- 已经写入的旧日志文件不会被重新编码。

## 验证

- 已检查 `logback-spring.xml` XML 结构，变更只补充 encoder charset。
- 未按项目规则主动编译，待用户使用 `./start.bat -b` 或自行测试确认运行效果。
