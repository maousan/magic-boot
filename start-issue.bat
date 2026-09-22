@echo off
rem 办公签发实例（端口 8091，可与日常 8090 实例并存）：授权关闭 + 签发开启
rem 私钥：C:\Users\Administrator\.license\license-private-key.pkcs8.b64（勿外传）
set LICENSE_PRIVATE_KEY_PATH=C:/Users/Administrator/.license/license-private-key.pkcs8.b64
cd /d D:\IdeaProjects\magic-boot
java -jar magic-boot-master\target\magic-boot.jar --spring.profiles.active=dev,dongxinheping --license.enabled=false --license.issue.enabled=true --server.port=8091
