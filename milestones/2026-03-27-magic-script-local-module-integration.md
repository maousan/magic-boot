# 里程碑：magic-script 本地模块集成

日期：2026-03-27

## 背景

`magic-script` 作为独立 git 项目已拉入仓库目录，但当前是嵌套仓库形态（`magic-script/.git`），未纳入主工程 Maven Reactor 管理，存在构建与仓库管理不一致风险。

## 本次变更

1. 根工程 `pom.xml` 新增模块：
   - `<module>magic-script</module>`
2. 集成方式采用“本地源码模块纳入主仓”，不使用 git submodule。
3. 保持版本策略不变：
   - `magic-script` 继续使用上游版本 `1.9.0`
   - 不改 `magic-dependencies` 中 `magic-script.version`。
4. 清理嵌套 git 仓库：
   - 移除 `magic-script/.git`，由主仓直接管理 `magic-script/` 源码。

## 为什么不使用 submodule

- 当前目标是统一主仓构建与依赖解析流程，减少 CI 与本地环境差异。
- 本地模块纳入 Reactor 后，下游依赖可直接走本地源码编译产物，联调效率更高。

## 后续同步上游建议

1. 通过定期同步上游 `magic-script` 代码（手动或脚本）更新目录内容。
2. 同步后执行主仓聚合构建与关键模块回归构建。
3. 若上游版本变更，再同步调整 `magic-dependencies` 中 `magic-script.version`。
