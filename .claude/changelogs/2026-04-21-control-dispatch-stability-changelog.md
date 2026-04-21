# 2026-04-21 control下发稳定性修复变更日志

## 背景
重启后出现 `/api/light/control` 仅记录 `dispatch async ...`，但无 `AIMS亮灯调用成功/失败` 日志，链路结果不可观测。

## 变更内容
- [亮灯灭灯控制.ms](/D:/IdeaProjects/magic-boot/data/dongxinheping/api/东信和平/亮灯控制/亮灯灭灯控制.ms)
  - 新增 `controlDispatchAsync` 配置读取，默认 `false`（同步）。
  - 6个下发分支统一改为“异步/同步可切换”。
  - 同步模式下新增 `stage=dispatch_result` 日志，记录 `action` 与 `success`。
  - 保留原 `dispatch_error` 异常日志。
  - 将 `duration` 校验中的 `!==` 改为 `!=`，避免脚本解析兼容问题。
- [application-dongxinheping.yml](/D:/IdeaProjects/magic-boot/magic-boot-master/src/main/resources/application-dongxinheping.yml)
  - 新增 `forest.variables.controlDispatchAsync: false`。

## 影响评估
- 默认行为从异步改为同步（仅 control 接口），提升可观测性与稳定性。
- 需要高吞吐时可显式配置回异步模式。
