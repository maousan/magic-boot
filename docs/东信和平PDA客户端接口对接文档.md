# 东信和平 PDA 客户端接口对接文档

> 更新日期：2026-09-17　|　适用服务：magic-boot（dongxinheping 部署）
> 本文面向 PDA/App 客户端开发，覆盖拣货亮灯、库位灯绑定、批次 EPC 绑定相关接口。

## 1. 通用约定

| 项 | 说明 |
|---|---|
| Base URL | `http://<服务器IP>:8090` |
| 接口前缀 | 所有接口以 `/api` 开头 |
| 请求格式 | `Content-Type: application/json`（DELETE 的查询参数走 URL） |
| 鉴权 | 全部接口免登录，无需请求头 |
| 响应包装 | `{"code":200,"message":"success","data":...,"timestamp":...,"executeTime":...}` |
| 成功判断 | `code == 200`；非 200 时 `message` 为中文提示，可直接 toast 展示 |

**命名说明**：历史原因，部分接口的库位码参数名叫 `lotNo`。`lotNo`、`locationCode` 在库位灯相关接口中均指**库位码**（如 `B2-52-14-01-02`），本文按各接口实际参数名书写。

## 2. 本次（2026-09）变更摘要 —— App 需要适配的点

| # | 变更 | 影响 |
|---|---|---|
| 1 | **批次 EPC 查询合并**：`GET /location/batch-epc/query-by-batch` 与 `/query-by-epc` 合并为 `GET /location/batch-epc/query`，旧接口**已删除** | **破坏性**，必须改调用地址 |
| 2 | 删除库位灯绑定 `DELETE /location/led-mapping`：`id` 改为可选，支持直接传库位码 `lotNo` | 之前报"参数[id]为必填项"的场景修复 |
| 3 | 删除仓库库位 `DELETE /location/warehouse-location/delete`：支持传 `locationCode` | 同上 |
| 4 | 解绑标签 `DELETE /location/label-mapping`：支持传 `labelCode` | 同上 |
| 5 | 巷道灯设备列表 `GET /location/led-devices`：返回的 `ip` 字段值改为 **MAC 地址**（字段名不变） | 下拉显示为 MAC |
| 6 | **上传拣货数据后不再自动亮灯**：上传仅落库，主单默认"未开始"；必须调亮灯接口（mode=1/2）后灯才亮 | 流程变化，见 §3 |
| 7 | 批次 EPC 绑定 `POST /location/batch-epc/bind`：移除了后端遗留的调试代码，此前传参会被服务端测试数据覆盖 | 绑定结果恢复按实际传参处理 |

## 3. 拣货亮灯流程（核心流程）

```
WMS/PDA 上传拣货数据 ──► 拣货员开始拣货(mode=1亮灯) ──► 扫码拣货 ──► 上报拣货完成 ──► 灯态自动重算 ──► 全部完成自动灭灯
                          (或手动开灯 mode=2)                              (手动灭灯 mode=4)
```

### 3.1 上传拣货数据

`POST /api/light/picking/upload`

**注意：上传后灯不会亮**，需再调 3.2 亮灯控制。

```json
{
  "waveNo": "WV202603250001",
  "userId": "DZJH",
  "userName": "电子拣货供应商",
  "updateTime": 1648195200000,
  "details": [
    {
      "id": "DTL001",
      "materialCode": "2.01.26.01.20306",
      "batchNo": "B20260325",
      "locationCode": "1111A1-11-02-01-01",
      "planQuantity": 100,
      "actualQuantity": 0,
      "status": 0
    }
  ]
}
```

| 字段 | 必填 | 说明 |
|---|---|---|
| waveNo | 是 | 波次号 |
| userId | 是 | 拣货用户ID（决定灯颜色的主体，需后台在「用户灯色映射」中配置） |
| userName | 否 | 用户名 |
| updateTime | 否 | 毫秒时间戳 |
| details[] | 是 | 明细数组，重复上传同一 waveNo+userId 会全量覆盖旧明细 |
| details[].id | 否 | 明细ID，不传服务端生成 |
| details[].status | 是 | 仅支持 0/1；actualQuantity >= planQuantity 时服务端按已拣处理 |

响应：`data: null`，`code=200` 成功。

### 3.2 亮灯 / 灭灯控制

`POST /api/light/control`

```json
{ "waveNo": "WV202603250001", "userId": "DZJH", "mode": 1 }
```

| 字段 | 必填 | 说明 |
|---|---|---|
| waveNo / userId | 是 | 定位该用户在该波次的主单 |
| mode | 是 | **1**=自动亮灯（开始拣货时调）；**2**=手动亮灯（无库位时报 404）；**3**=自动灭灯；**4**=手动灭灯 |
| color / duration | 否 | 不需要传。实际灯色由后台「用户灯色映射」按 userId 决定，无需客户端关心 |

**灯色规则**（后台自动重算，客户端只管触发）：
- 库位上只有该用户未拣完 → 亮该用户的专属颜色；
- 多个未拣完用户共用库位 → 亮青色（CYAN 混合色）；
- 全部拣完 → 自动灭灯；
- 其他用户拣完共享库位后，剩余用户库位自动切换成剩余用户的颜色。

### 3.3 上报拣货完成

`POST /api/light/picking/complete`

```json
{
  "waveNo": "WV202603250001",
  "userId": "DZJH",
  "details": [
    {
      "materialCode": "2.01.26.01.20306",
      "batchNo": "B20260325",
      "locationCode": "1111A1-11-02-01-01",
      "planQuantity": 100,
      "actualQuantity": 100,
      "updateTime": 1648195201000
    }
  ]
}
```

`actualQuantity >= planQuantity` 的明细记为已拣；服务端重算灯态（共享库位自动切换颜色/灭灯）。可重复调用（幂等）。

## 4. 库位灯绑定管理（「库位灯」页面）

### 4.1 巷道灯设备列表（绑定页下拉数据源）

`GET /api/location/led-devices`

```json
{
  "code": 200,
  "data": [
    { "macAddress": "3A:69:7A:08:D0:A5", "ip": "3A:69:7A:08:D0:A5", "remark": "Netty客户端自动注册" }
  ]
}
```

> `ip` 字段自 2026-09 起返回 **MAC 地址**（字段名保留 `ip` 是为了客户端兼容）。创建绑定时 `ledId` 传 `macAddress` 的值。

### 4.2 创建库位灯绑定

`POST /api/location/led-mapping`

```json
{ "lotNo": "B2-52-14-01-02", "ledId": "3A:69:7A:08:D0:A5", "color": "RED" }
```

| 字段 | 必填 | 说明 |
|---|---|---|
| lotNo | 是 | 库位码（可传外箱二维码原文，服务端自动解析出 Lot） |
| ledId | 是 | 巷道灯设备 MAC 地址（来自 4.1 的 `macAddress`） |
| color | 是 | 灯层颜色：`RED`红色/第1层、`YELLOW`黄色/第2层、`GREEN`绿色/第3层 |

错误：该库位码已有绑定 → `code=500`，message 含已绑定的设备号。

### 4.3 删除库位灯绑定

`DELETE /api/location/led-mapping?lotNo=B2-52-14-01-02`

- 参数二选一：`lotNo`（库位码，App 推荐）或 `id`（绑定记录主键，管理端网页用）。
- 也支持 JSON body：`{"lotNo": "B2-52-14-01-02"}`。
- 删除成功 `data: 1`；绑定不存在 `code=404`，message `未找到对应的绑定记录`。

### 4.4 库位灯绑定列表

`GET /api/location/led-mapping/list?page=1&pageSize=20&locationCode=&ledId=&color=`

分页参数与筛选均可选，`color` 取值 RED/YELLOW/GREEN。

## 5. 库位码与标签绑定（「标签绑定」页面）

### 5.1 绑定

`POST /api/location/label-mapping`

```json
{ "locationCode": "B2-52-14-01-02", "labelCode": "0C722B51669D" }
```

`locationCode` 可传外箱二维码原文（服务端解析），`labelCode` 为电子标签码。调 AIMS 成功后落库。

### 5.2 解绑

`DELETE /api/location/label-mapping?labelCode=0C722B51669D`

- 参数二选一：`labelCode`（App 推荐）或 `id`（绑定记录主键）。
- 也支持 JSON body：`{"labelCode": "0C722B51669D"}`。
- 先调 AIMS 解绑，成功后删本地记录；无记录返回 `code=404`。

## 6. 删除仓库库位

`DELETE /api/location/warehouse-location/delete?locationCode=B2-52-14-01-02`

- 参数二选一：`locationCode`（库位码）或 `id`（库位记录主键）。
- 成功返回 `data: "删除成功"`；不存在时 `code=0`，message `库位记录不存在`。

## 7. 批次 EPC 绑定（「批次EPC」页面）

### 7.1 绑定（扫外箱二维码 + EPC）

`POST /api/location/batch-epc/bind`

```json
{
  "locationId": "B2-52-14-01-02",
  "batchId": "<外箱二维码原始内容>",
  "epc": "E281D0122000975658773D8C"
}
```

| 字段 | 必填 | 说明 |
|---|---|---|
| locationId | 是 | 库位号 |
| batchId | 是 | **外箱二维码原文**（不是拼接后的批次ID），服务端解析出 `MtlCode`/`Lot` 后拼成最终批次ID：`locationId_MtlCode_Lot` |
| epc | 是 | RFID 标签 EPC |

**幂等/改绑规则**：同批次同 EPC 重复绑 → 幂等返回已有绑定；EPC 已绑其他批次 → 自动改绑到新批次；批次已绑其他 EPC → `code=400`。

成功返回绑定记录（2026-09 起含拆分字段）：
`data: {"id","batchId","epc","locationId","sku","lotNo","bindTime"}`

> 服务端会解析二维码并将 `locationId`（库位号）、`sku`（MtlCode）、`lotNo`（Lot）拆分入库；EPC 改绑/重复绑定幂等返回时同样返回这些字段。

### 7.2 查询绑定（2026-09 新合并接口）

`GET /api/location/batch-epc/query?batchId=xxx`　或　`GET /api/location/batch-epc/query?epc=xxx`

- `batchId`（最终批次ID）与 `epc` 二选一，都传时 `batchId` 优先；都不传 `code=400`。
- 响应：`{"success":true,"data":{"id","batchId","epc","locationId","sku","lotNo","bindTime"}}`；未绑定返回 `data: null`。
- **旧接口 `/query-by-batch`、`/query-by-epc` 已删除**，请改用本接口。

### 7.3 解绑

`POST /api/location/batch-epc/unbind`

```json
{ "epc": "E281D0122000975658773D8C" }
```

`batchId` / `epc` 至少传一个，逻辑删除绑定记录。

### 7.4 绑定列表

`GET /api/location/batch-epc/list?page=1&pageSize=20&keyword=&status=`

`keyword` 对批次ID/EPC 模糊匹配，`status` 传 `1` 查有效绑定。

## 8. 联调自检清单

1. 上传拣货数据后确认**灯不亮**（新行为），调 mode=1 后亮该用户配置的颜色。
2. 用户在管理端「用户灯色映射」未配置时，单人库位兜底亮**绿色**；服务端日志会出现 `resolve_single_color_miss`，按 userId 补配置即可。
3. 删除绑定/解绑标签请确认传的是 `lotNo`/`labelCode`（不再是必传 `id`）。
4. 批次 EPC 查询确认已切换到 `/query` 合并接口。
