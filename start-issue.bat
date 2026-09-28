@echo off
rem 办公签发实例（端口 8091，可与日常 8090 实例并存）：授权关闭 + 签发开启
rem 私钥路径等环境变量从 .env 加载（模板见 .env.example，.env 不入库）

cd /d D:\IdeaProjects\magic-boot
if not exist "%~dp0.env" (
  echo [ERROR] 未找到 .env：请复制 .env.example 为 .env 并填写签发私钥路径
  exit /b 1
)
for /f "usebackq eol=# tokens=1,* delims==" %%a in ("%~dp0.env") do set "%%a=%%b"
if not defined LICENSE_PRIVATE_KEY_PATH (
  echo [ERROR] .env 中缺少 LICENSE_PRIVATE_KEY_PATH
  exit /b 1
)
echo LICENSE_PRIVATE_KEY_PATH=%LICENSE_PRIVATE_KEY_PATH%

java -jar magic-boot-master\target\magic-boot.jar --spring.profiles.active=dev,dongxinheping --license.enabled=false --license.issue.enabled=true --server.port=8091
