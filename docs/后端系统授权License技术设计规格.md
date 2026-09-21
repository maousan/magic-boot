# 后端系统授权（License）技术设计规格

> 版本：v1.0（待评审） · 2026-09-21
> 决策来源：wayfinder 地图 [maousan/magic-boot#1](https://github.com/maousan/magic-boot/issues/1)，工单 #2/#3/#4/#5/#6/#7/#8/#9 全部决议
> 目标读者：开发（可直接开工）、评审人

## 1. 目标与范围

东信和平等私有化部署给客户的后端系统，须经**授权文件**方可使用：

- 授权文件含**绝对有效期**，绑定**客户标识与机器指纹**，Ed25519 签名防伪造
- 无授权 / 过期 / 校验失败 / 时钟回拨 → **阻断业务接口**，保留登录与授权导入通道
- **防回拨**：多存储单调时间戳 + 跳变检测；到期判定取 max(当前时间, 已见最大时间)
- 签发：办公侧管理端「授权签发」网页（条件装配，客户现场无此接口）

**范围外**（明确排除）：防逆向混淆/代码加固、在线 license 服务器/联网激活、模块级/席位级授权。

**安全边界声明**：离线授权且客户持有服务器 root 时，全盘镜像/VM 快照回滚、"新机+新库+拷目录"整机迁移无法绝对防住，本方案目标是大幅抬高作弊成本（见 §5.7）。

## 2. 总体架构

```
办公侧（乙方）                         客户现场（内网，可完全离线）
┌─────────────────┐                 ┌──────────────────────────────┐
│ 管理端「授权签发」页 │  .lic 文件    │ LicenseGateFilter（全局闸门）    │
│ (license.issue.   │ ───────────→ │ PermissionInterceptor 顶部     │
│  enabled=true)    │  抄指纹→签发→  │ @PostConstruct 启动校验          │
│ 私钥=环境变量注入   │  回传导入      │ @Scheduled 60s 巡检             │
└─────────────────┘                 │ 防回拨：4 位置单调时间戳存储        │
                                    └──────────────────────────────┘
```

代码归属：`magic-boot-master` 新增独立包 `org.ssssssss.magicboot.license`（配置驱动，预留多项目通用化）；新增依赖 `oshi-core`（硬件指纹）。

## 3. 授权文件格式（.lic）

单文件 JSON：载荷 + `signature` 字段。签名覆盖**规范化序列化后的载荷**（键名字典序、无空白差异）。

```json
{
  "licenseId": "9f2c1e7a4b8d4f0a9c3d5e6f7a8b9c0d",
  "customer": "东信和平",
  "issuedAt": "2026-09-21",
  "expireAt": "2027-09-21",
  "fingerprints": [
    "a1b2c3d4e5f60718293a4b5c6d7e8f90…(系统盘序列号哈希)",
    "b2c3d4e5f60718293a4b5c6d7e8f90a1…(主板序列号哈希)",
    "c3d4e5f60718293a4b5c6d7e8f90a1b2…(网卡MAC集合哈希)"
  ],
  "formatVersion": 1,
  "notes": "合同号 HT-2026-001",
  "signature": "<Ed25519 签名, 64字节 base64>"
}
```

- 算法：**Ed25519**（JDK 17 JCA 原生，零第三方依赖）
- 有效期：**绝对到期日**，`expireAt` 含当天（阈值按 Asia/Shanghai 23:59:59.999 折算，不依赖服务器时区）；不做"激活后 N 天"；续费 = 签发新文件导入覆盖
- 密钥：公钥**硬编码**在后端校验常量（信任根，公开无妨）；私钥**仅存办公签发服务器**，环境变量注入路径，不入 git。私钥泄露处置 = 换密钥对重签全部客户

## 4. 机器指纹

- **采集三项**（OSHI 库，普通权限）：系统盘序列号、主板序列号、物理网卡 MAC 集合（排除虚拟/回环，排序规范化）；各自规范化后 sha256 → 授权文件 `fingerprints[3]`
- **容差**：**3 项命中 ≥2 通过（K-of-N）**。换盘/换网卡不误伤；整套搬机至少两项同变，判失败需重签
- **采集异常降级**：某项读取失败（如 WMI 被禁）跳过该项按剩余项计容差，但剩余 2 项时必须命中 2 项（不允许再降）
- **展示**：授权状态页/阻断页展示 3 行指纹哈希（每行 32 hex，标注项名）；签发方整块抄取，签发表单整块粘贴解析
- **注意事项**：虚拟机主板序列号可能为平台通用值（区分度低，靠三项组合补偿）；虚拟机克隆/迁移后必须重验指纹

## 5. 防系统日期回拨

### 5.1 存储（4 位置，独立性与清除成本递增）

| # | 位置 | 键/路径 | 价值 | 失效场景 |
|---|------|---------|------|----------|
| 1 | MySQL 业务库单行表 `t_license_time_mark`（主键固定 `'default'`） | — | 客户不敢删业务库；库在独立主机 | 恢复旧 DB 备份时回退 |
| 2 | 本地文件 A：`{upload.dir}/.license/mark.dat`（默认 `D:/mb/`；Linux 建议默认改 `/var/lib/magic-boot/`） | — | 防删库 | 整目录拷贝带走 |
| 3 | 本地文件 B：`{工作目录}/data/.license/mark.dat` | — | 与 A 不同目录树 | 整目录拷贝带走 |
| 4 | Windows 注册表 `java.util.prefs`（`/org/ssssssss/magicboot/license/`）；Linux 用 `/var/lib/magic-boot/` 文件替代 | — | 整目录拷贝带不走、恢复 DB 不影响、全仓库零使用难发现 | 换机/重建用户配置 |
| 5 | Redis `license:time:mark`（固定 database，无 TTL；spring 用 db3 / magic-api 用 db12，实现写死其一） | — | 旁路校验位；现场已存活 | FLUSHALL/删 dump.rdb——**永不作权威值** |

DDL 红线：现场 Flyway 关闭，`t_license_time_mark` 建表脚本幂等 `CREATE TABLE IF NOT EXISTS`，**绝不带 DROP**。

### 5.2 标记结构（每位置同构，单行 JSON）

```json
{"v":1, "installId":"<首启生成UUID,同时存DB>", "maxSeen":1726900000000,
 "writeTime":1726900000000, "sig":"<HMAC-SHA256(installId|maxSeen|writeTime)>"}
```

HMAC 密钥由内置常量混淆派生（定位：抬高篡改门槛，不防逆向）；`installId` 绑定本安装，跨机拷贝标记验签失效。

### 5.3 合并语义

启动 + 每次巡检：读取全部位置 → 逐个验签（失败/installId 不符 = 无效，忽略）→ `effectiveMax = max(有效值)` → **立即回写扩散**到所有可写位置。任一位置读写失败 `log.warn` 不阻断，靠其余位置兜底。

### 5.4 到期与回拨判定

- `now' = max(系统当前时间, effectiveMax)`
- **过期**：`now' > expireAt` → 进入宽限期或阻断（§5.5）
- **时钟异常**：`now < effectiveMax − 5分钟` → 阻断，管理页显示异常原因（容差不构成作弊空间：effectiveMax 只增不减，到期日是绝对日期）
- 恢复双路：时间自然追平 effectiveMax 自动恢复；或导入任意有效新授权立即恢复。**effectiveMax 永不清零**

### 5.5 宽限期

`license.graceDays`（默认 **7** 天）。过期后：宽限期内**业务照常可用** + 三重提醒（登录页横幅、管理页红条、每日 WARN 日志）；超出 → 全面阻断（业务接口固定错误，登录/授权导入放行）。

### 5.6 NTP 可选校验

`license.ntp-servers`（默认空 = 关闭）。配置后启动 + 每小时查询（超时 2s）：拿到时间参与 max() 合并推进；**任何失败直接跳过**（不阻断不告警，仅 DEBUG）。零误伤，纯增益。

### 5.7 推进频率与残余风险

巡检 60s 推进一次 `max(now, effectiveMax)`；MySQL 仅值增大时 UPDATE。**残余风险（明确不承诺）**：全盘镜像/VM 快照回滚；"新机+新库+拷目录"整机迁移——缓解手段仅 vendor 在线校验（已排除）或 per-machine 授权重签。

## 6. 校验点与阻断行为

### 6.1 四层架构

| 层 | 位置 | 职责 |
|----|------|------|
| 全局闸门 | `LicenseGateFilter`（OncePerRequestFilter，order 最前） | 唯一覆盖静态 ResourceHandler 的层，一切 HTTP 过闸；读内存 volatile 标志，零解析开销 |
| 脚本接口层 | `PermissionInterceptor.preHandle` **顶部**（在 dev 判断与 require_login 判断**之前**——天然绕过点） | magic-api 接口语义化响应 |
| 启动校验 | `@PostConstruct`（早于 Tomcat 收请求，零窗口） | 验签、置标志；无有效授权时抑制插件 auto-start |
| 巡检 | `@Scheduled` fixedDelay 60s | 推进时间戳、刷新标志、处置联动。**不放 magic-api job**（Quartz 脚本可被用户增删） |

### 6.2 白名单（Filter 放行）

`/system/security/login`、`/system/security/verification/code`、`/system/security/validateToken`、`/system/security/logout`、`/system/license/**`（状态/导入）、`/magic/web/**`（独立口令，运维恢复通道）、`/plugin/*/static/**` 与插件页容器（管理页必须能加载才能导入授权）、`/favicon.ico`。

白名单集中管理：代码内置默认 + `license.permit-patterns` 追加。

### 6.3 阻断与边界

| 路径 | 处置 |
|------|------|
| `/plugin/*/api/**`、`/plugin/admin/**`、`/logs/**`、`/ws/logs`（拒绝新握手）、`/userfiles/**`、`/actuator/**`、`/druid/**` | 无有效授权阻断 |
| `license.enabled` | dev profile 默认 **false**（办公开发豁免）；dongxinheping 等现场 profile 默认 **true** |

### 6.4 阻断响应与预警

- 业务接口统一 `{code:403, message:"系统授权已过期，请导入有效授权文件", data:null}` + 响应头 `X-License-Status: ok|warning|grace|expired|abnormal`；不复用 402（避免与登录过期混淆）；PDA 端现有拦截器直接 Toast，零改造
- 三级预警：剩余 >30 天正常；≤30 天**黄色**（管理页横幅+每日 WARN）；宽限期**红色**；到期阻断
- **后台线程联动**：阻断生效 → `pluginManager.stopPlugin` 停业务插件（zintis-led/zintis-rfid，掐掉心跳等非 HTTP 线程）；恢复 → `startPlugin`

## 7. 授权管理（客户侧）

- `POST /system/license/import`：上传 .lic → 验签 + 指纹 3中2 + formatVersion 兼容 → 热加载授权、刷新标志、`startPlugin` 恢复。**允许导入已过期文件**（按新文件语义重判状态，防止旧文件锁死系统无法恢复）
- `GET /system/license/status`：客户名、到期日、剩余天数、状态、3 行指纹、异常原因
- 管理页「系统授权」卡：上述信息 + 导入按钮 + 预警横幅

## 8. 签发方案（办公侧）

- **宿主**：管理端「授权签发」页 `/system/license/issue`；签发 Bean `@ConditionalOnProperty(license.issue.enabled=true)` 条件装配——办公实例配私钥才存在，**客户现场接口不存在（404）**
- **页面**：表单（客户名、指纹文本块 3 行粘贴、有效期至、备注）→ 签发 → 浏览器下载 .lic；历史签发列表
- **留痕**：`t_license_issue_log`（licenseId、customer、expireAt、指纹、操作人、时间，办公库）
- **权限**：管理端登录 + 权限码 `license:issue`（仅超管）
- **密钥初始化**：开发期一次性脚本生成 Ed25519 密钥对（页面不做生成）；公钥进代码常量，私钥交办公实例环境变量
- **存量实例首次下发**：升级新 jar → 管理页授权卡抄 3 行指纹 → 交乙方签发 → 回传 .lic → 页面导入

## 9. 配置项清单

| 配置 | 默认 | 说明 |
|------|------|------|
| `license.enabled` | profile 决定（dev=false，现场=true） | 授权总开关 |
| `license.graceDays` | 7 | 过期宽限天数 |
| `license.ntp-servers` | 空（关闭） | NTP 校验，逗号分隔 |
| `license.permit-patterns` | 空 | 白名单追加 |
| `license.issue.enabled` | false | 签发功能开关（办公侧） |
| 环境变量 `LICENSE_PRIVATE_KEY_PATH` | — | 签发私钥路径（仅办公） |

## 10. 数据库对象（DDL 均幂等，存档 db/migration，现场手工执行）

- `t_license_time_mark`：单行表（id varchar(16) PK='default'，mark_json mediumtext，update_time）
- `t_license_issue_log`：办公库签发留痕（id、license_id、customer、expire_at、fingerprints、operator、create_time）

## 11. 安全边界与残余风险

| 威胁 | 结果 |
|------|------|
| 客户自造密钥对签 license | 公钥不匹配，验签失败 |
| 篡改 license 载荷 | 签名失效 |
| 改系统时间回拨 | effectiveMax 兜住，无法延长有效期；超容差触发阻断 |
| 删库 / 恢复旧 DB 备份 / 删程序目录 | 其余位置 max 合并兜住并回写扩散 |
| 整目录拷贝到新机 | 注册表不带走 + 指纹 3中2 失败 + installId 验签失效 |
| **全盘镜像/VM 快照回滚、新机+新库+拷目录** | **防不住（已声明）**——需在线校验缓解，本轮排除 |
| 反编译 jar 阅读校验逻辑 | 范围排除（混淆不做）；绕过需改字节码，成本高于改系统时间 |

## 12. 实施拆解建议

| 模块 | 内容 | 粗估 |
|------|------|------|
| 后端 license 包 | 指纹采集、验签、4 位置存储与合并、Filter、Interceptor 挂点、启动/巡检、状态/导入接口 | 3~4 天 |
| 管理页（客户侧） | 系统授权卡 + 导入 + 预警横幅 | 1 天 |
| 签发端（办公侧） | 条件装配接口 + 签发页 + 留痕 | 1~2 天 |
| 联调测试 | 回拨/过期/宽限/搬机/导入过期文件/插件停启全场景 | 2 天 |
