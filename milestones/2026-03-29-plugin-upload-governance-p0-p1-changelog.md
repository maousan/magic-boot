# 2026-03-29 鎻掍欢涓婁紶娌荤悊 P0/P1 鍙樻洿鏃ュ織

## 鍙樻洿绫诲瀷

鍔熻兘澧炲己 / 娌荤悊鑳藉姏琛ラ綈 / 鎺ュ彛鍝嶅簲鏍囧噯鍖?
## 鍙樻洿鍐呭

1. 鏍囧噯閿欒鐮?- 涓婁紶鐩稿叧鎺ュ彛鍝嶅簲鏂板 `errorCode` 瀛楁銆?- 鏂板缁熶竴閿欒鐮佹灇涓惧拰寮傚父绫诲瀷锛屼笂浼犲け璐ュ彲绋冲畾鎸夐敊璇爜璇嗗埆銆?
2. 涓婁紶绛栫暐寮€鍏冲寲
- 鏂板閰嶇疆锛歚plugin.upload-zip-only`锛堥粯璁?true锛夈€?- 寮€鍏冲叧闂椂锛宍/plugin/admin/upload` 鍏佽 legacy JAR 涓婁紶銆?
3. ZIP 娌荤悊淇℃伅钀藉簱
- `magic_plugin` 琛ㄦ柊澧炴不鐞嗗瓧娈碉細
  - `package_type`
  - `package_checksum`
  - `manifest_version`
  - `manifest_json`
  - `requires_magic_boot`
  - `permissions`
  - `install_source`
  - `install_time`
- ZIP 瀹夎鎴愬姛鍚庡啓鍏?Manifest 涓庢潵婧愪俊鎭€?- 鍒楄〃鍜岃鎯呮帴鍙ｈ繑鍥炰笂杩版不鐞嗗瓧娈点€?
4. 娴嬭瘯涓庨獙璇?- 鏂板/鏇存柊鍗曟祴瑕嗙洊锛?  - ZIP 涓婁紶鏍￠獙鍒嗘敮锛堢被鍨嬨€佺粨鏋勩€丮anifest銆乧hecksum銆佺増鏈吋瀹癸級
  - Controller 閿欒鐮佹槧灏?  - zip-only 鍏抽棴鏃?legacy JAR 涓婁紶
- 鏂板 HTTP 娴嬭瘯鑴氭湰锛?  - `http/plugin-upload-zip.http`
  - `http/plugin-upload-negative.http`
  - `http/plugin-upload-compatibility.http`

## 楠岃瘉缁撴灉

鎵ц鍛戒护锛?
`mvn -pl magic-plugin "-Dtest=ZipPluginInstallerTest,PluginAdminControllerTest,PluginAdminControllerRouteTest,PluginManagerServiceTest" test`

缁撴灉锛?- Tests run: 26
- Failures: 0
- Errors: 0
- BUILD SUCCESS

## 鍏煎鎬ц鏄?
- 涓婁紶涓昏矾鐢变繚鎸佷笉鍙橈細`POST /plugin/admin/upload`銆?- 鍘熸湁 `code/message/data` 缁撴瀯淇濇寔锛屾柊澧?`errorCode` 涓哄悜鍚庡吋瀹瑰寮恒€?- 鍘嗗彶鎻掍欢璁板綍娌荤悊瀛楁鍏佽涓虹┖锛屼笉褰卞搷鏌ヨ銆?