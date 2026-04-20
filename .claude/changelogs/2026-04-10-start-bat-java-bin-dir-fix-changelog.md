# 变更日志：修复 start.bat 的 JAVA_BIN 目录场景

日期：2026-04-10

## 变更内容

- 修复 `deploy-win/start.bat` 在 `JAVA_BIN` 配置为目录时的兼容性。
- 新逻辑：
  - 若 `JAVA_BIN` 是目录，自动补全为 `java.exe`。
  - 若最终仍是目录，直接报错并给出正确示例路径。
  - 清理 `JAVA_BIN` 中可能包含的双引号，避免路径解析异常。

## 目标

- 避免 `Start-Process -FilePath '...\\bin'` 导致的“拒绝访问”错误。
