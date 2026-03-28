# PF4J æ‰‹åŠ¨åˆå§‹åŒ–åŒæ­¥æ¥å£å˜æ›´æ—¥å¿—

## å˜æ›´æ—¥æœŸ
- 2026-03-28

## å˜æ›´ç±»å‹
- æ–°å¢åŠŸèƒ½

## å˜æ›´å†…å®¹
- æ–°å¢ç®¡ç†æ¥å£ï¼š`POST /plugin/admin/init-sync`
- æ¥å£è¡Œä¸ºï¼šè°ƒç”¨ `initMissingPluginsFromRuntime()`ï¼Œè¿”å›è¡¥å½•ç»Ÿè®¡ä¿¡æ¯ã€‚
- æ–°å¢æ§åˆ¶å±‚å•å…ƒæµ‹è¯•ï¼ˆæˆåŠŸ/å¼‚å¸¸åˆ†æ”¯ï¼‰ã€‚
- åœ¨ `http/test-plugin-api.http` å¢åŠ æ¥å£æµ‹è¯•è„šæœ¬ã€‚

## å…¼å®¹æ€§
- ä¸å½±å“ç°æœ‰æ’ä»¶ç®¡ç†æ¥å£ã€‚
- ä»…æ–°å¢ç«¯ç‚¹ï¼Œé»˜è®¤æ— ç ´åæ€§å½±å“ã€‚


---

## 2026-03-28 µÚ¶ş½×¶ÎÀ©Õ¹£¨P0 + P1£©

### ĞÂÔö½Ó¿Ú
- GET /plugin/admin/runtime/summary
- GET /plugin/admin/runtime/{pluginId}
- POST /plugin/admin/scan-new
- POST /plugin/admin/sync
- POST /plugin/admin/install/by-path
- POST /plugin/admin/reconcile
- POST /plugin/admin/enable/{pluginId}
- POST /plugin/admin/disable/{pluginId}
- GET /plugin/admin/health/{pluginId}
- GET /plugin/admin/audit/list

### ËµÃ÷
- Í³Ò»±£³Ö `{code,message,data}` ·µ»Ø½á¹¹¡£
- reconcile Ö§³Ö dryRun£¬²»Âä¿âÔ¤ÀÀ²îÒì¡£
- Ô¤Ğ£Ñé½Ó¿ÚÓëÅúÁ¿½Ó¿Ú±£³Ö P2 ´ı°ì¡£

## 2026-03-28 Â·ÓÉÈ¥ÆçÒå£¨ÆÆ»µĞÔ¸Ä¶¯£©
- ÉÏ´«½Ó¿ÚÓÉ `POST /plugin/admin/install` ¸ÄÎª `POST /plugin/admin/upload`¡£
- ¾É½Ó¿Ú `/plugin/admin/install` ÒÑÒÆ³ı£¬²»Ìá¹©¼æÈİ¡£
- `/plugin/admin/install/by-path` ±£³Ö²»±ä¡£
