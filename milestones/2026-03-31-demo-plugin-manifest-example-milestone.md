# 2026-03-31 Demo 插件 Manifest 示例文件里程碑

## 交付结果

- 已在 magic-plugin-demo 模块根目录交付 Manifest.json.example。
- 已补齐计划文档与变更日志文档。

## 使用方式

1. 打包插件 ZIP 前，将示例文件复制为 Manifest.json 并放在 ZIP 根目录。
2. 计算真实入口 JAR（通常为 plugin.jar）的 SHA-256，替换：
   - checksumSha256: REPLACE_WITH_PLUGIN_JAR_SHA256
3. 若环境开启签名强校验（signatureRequired=true 且 signatureForceVerify=true），使用私钥对 checksumSha256 字符串签名并替换：
   - signature: REPLACE_WITH_BASE64_SIGNATURE_WHEN_SIGNATURE_FORCE_VERIFY_ENABLED
4. 若未开启签名强校验，可保留占位值或按实际流程移除 signature 字段。

## 验收记录

- 本次仅执行静态校验与字段一致性校验，不执行编译与服务启动。