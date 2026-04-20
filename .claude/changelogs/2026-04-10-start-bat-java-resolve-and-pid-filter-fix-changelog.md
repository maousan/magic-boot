# 变更日志：start.bat Java 解析与 PID 筛选修复

日期：2026-04-10

## 变更内容

- `deploy-win/start.bat` 改为通过 PowerShell 统一解析 `JAVA_BIN`：
  - 允许 `JAVA_BIN=java`
  - 允许 `JAVA_BIN` 配置为 JDK `bin` 目录（自动补 `java.exe`）
  - 允许 `JAVA_BIN` 配置为 `java.exe` 绝对路径
- 修复 PID 文件异常导致 `tasklist` 报“无法识别搜索筛选器”：
  - 仅当 PID 为纯数字时才执行 `tasklist /FI "PID eq ..."`。
  - 非数字 PID 自动清理后继续启动。
