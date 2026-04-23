# 2026-04-22 redeploy-zintis-led-plugin-skill changelog

## 变更内容
- 新增 Skill：`redeploy-zintis-led-plugin`
- 路径：`.codex/skills/redeploy-zintis-led-plugin/SKILL.md`
- 覆盖场景：
  - 插件编译打包
  - 定位并停止占用进程
  - 覆盖 `plugins/magic-plugin-zintis-led.jar`
  - 覆盖后校验

## 价值
- 降低 Windows 文件锁导致的重复失败。
- 固化为团队可复用的标准流程。
