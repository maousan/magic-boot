# 2026-03-29 鎻掍欢涓婁紶娌荤悊 P0/P1 瀹炴柦璁″垝

## 鐩爣

鍦ㄤ笉鏀瑰姩涓婁紶涓昏矾鐢?`/plugin/admin/upload` 鐨勫墠鎻愪笅锛屽畬鎴愪互涓嬭兘鍔涳細

1. 涓婁紶澶辫触杩斿洖鍙満璇婚敊璇爜銆?2. ZIP + Manifest 娌荤悊淇℃伅钀藉簱骞跺彲鏌ヨ銆?3. 涓婁紶绛栫暐鏀寔 zip-only 寮€鍏冲寲锛堥粯璁ゅ紑鍚級銆?
## 瀹炴柦椤?
1. 閿欒鐮佹爣鍑嗗寲
- 鏂板鎻掍欢瀹夎閿欒鐮佹ā鍨嬩笌寮傚父灏佽銆?- `PluginAdminController` 鍦ㄥ搷搴斾腑鏂板 `errorCode` 瀛楁銆?- 涓婁紶鍦烘櫙鏄犲皠鏍囧噯閿欒鐮侊細
  - `PLUGIN_UPLOAD_INVALID_TYPE`
  - `PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID`
  - `PLUGIN_MANIFEST_INVALID`
  - `PLUGIN_CHECKSUM_MISMATCH`
  - `PLUGIN_DESCRIPTOR_MISMATCH`
  - `PLUGIN_VERSION_INCOMPATIBLE`
  - `PLUGIN_INSTALL_FAILED`

2. ZIP 娌荤悊瀛楁闂幆
- 鎵╁睍 `magic_plugin` 琛ㄧ粨鏋勫拰 `PluginInfo` 瀹炰綋锛?  - `package_type`
  - `package_checksum`
  - `manifest_version`
  - `manifest_json`
  - `requires_magic_boot`
  - `permissions`
  - `install_source`
  - `install_time`
- ZIP 瀹夎瀹屾垚鍚庤惤搴撴不鐞嗗瓧娈点€?- `list/info` 杈撳嚭鏂板娌荤悊瀛楁銆?
3. zip-only 寮€鍏冲寲
- `PluginProperties` 鏂板 `uploadZipOnly`锛堥粯璁?`true`锛夈€?- 寮€鍏冲叧闂椂锛屼笂浼犲叆鍙ｅ厑璁?legacy JAR 鐩翠紶骞惰惤搴?`LEGACY_JAR` 鍏冩暟鎹€?
4. 娴嬭瘯涓庤剼鏈?- 鍗曟祴瑕嗙洊閿欒鐮佹槧灏勩€乑IP 鏍￠獙鍒嗘敮銆乴egacy JAR 涓婁紶銆?- 鏂板鎷嗗垎 HTTP 鐢ㄤ緥锛?  - `http/plugin-upload-zip.http`
  - `http/plugin-upload-negative.http`
  - `http/plugin-upload-compatibility.http`
