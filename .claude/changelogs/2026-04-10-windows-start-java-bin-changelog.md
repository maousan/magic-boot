# 变更日志：Windows 启动脚本支持自定义 Java 路径

日期：2026-04-10

## 变更内容

- `deploy-win/set-env.bat` 新增 `JAVA_BIN` 配置项，默认值为 `java`。
- `deploy-win/start.bat` 启动时优先使用 `JAVA_BIN`：
  - 当 `JAVA_BIN=java` 时走 `PATH` 检测。
  - 当 `JAVA_BIN` 为绝对路径或命令名时执行可用性校验。
  - 启动日志打印当前使用的 Java 可执行文件。
- `deploy-win/README.md` 增加 `JAVA_BIN` 配置示例。

## 影响范围

- 仅影响 Windows 非 Docker 启动脚本行为。
- 业务代码与接口功能不受影响。
