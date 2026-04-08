# 2026-04-07 库位标签解绑能力变更日志

## 变更概述
- 新增 AIMS 解绑函数：`/unlinkArticleAndLabel`
- 新增库位标签解绑 API（DELETE `/api/location/label-mapping`）支持 `locationCode + labelCode`
- 完善 PDA 解绑页面调用参数与交互提示

## 文件变更
- 新增：`data/dongxinheping/function/aims/标签/解绑article和label.ms`
- 新增：`data/dongxinheping/api/东信和平/巷道灯/解绑库位码和电子标签码的绑定关系.ms`
- 修改：`magic-boot-master/src/main/resources/static/pda/location-label-mapping.html`
- 修改：`http/test-dongxinheping-location-label-mapping.http`
- 新增：`http/test-dongxinheping-aims-unlink-article-label.http`

## 逻辑变化
- 解绑 API 现在会先调用 AIMS `POST /labels/unlink?labelCode=...`。
- 仅当 AIMS 返回 2xx 时，才删除本地 `t_location_label` 绑定记录。
- 页面解绑从“仅 locationCode”升级为“locationCode + labelCode”双参数提交。
