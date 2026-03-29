# 2026-03-29 鎻掍欢绛惧悕寮烘牎楠屽彉鏇存棩蹇?
## 鍙樻洿绫诲瀷

瀹夊叏澧炲己 / 涓婁紶娌荤悊鑳藉姏鎻愬崌

## 鍙樻洿鍐呭

1. 绛惧悕寮烘牎楠屽惎鐢?- ZIP 涓婁紶瀹夎瑕佹眰 `Manifest.signature` 蹇呭～銆?- 鍦?checksum 鏍￠獙閫氳繃鍚庯紝鏂板绛惧悕楠岀姝ラ銆?- 楠岀鏁版嵁涓?`entryJar` 鐨?SHA-256 鎽樿瀛楃涓层€?
2. 閰嶇疆椤规柊澧?- `plugin.signature-required`锛堥粯璁?true锛?- `plugin.signature-algorithm`锛堥粯璁?`SHA256withRSA`锛?- `plugin.signature-public-key`锛圥EM/Base64 X509锛?
3. 閿欒鐮佸寮?- 鏂板 `PLUGIN_SIGNATURE_INVALID`锛?  - Manifest.signature 缂哄け
  - 鍏挜閰嶇疆缂哄け/闈炴硶
  - 绛惧悕涓嶅尮閰嶆垨楠岀澶辫触

4. 娴嬭瘯琛ュ厖
- `ZipPluginInstallerTest` 澧炲姞绛惧悕鍚堟硶/涓嶅悎娉曞垎鏀€?- 淇濇寔鍘熸湁涓婁紶娌荤悊鍒嗘敮鐢ㄤ緥瑕嗙洊銆?