# 东信和平生产部署说明：PDA App 运行日志上传与管理页面

> 部署内容版本：gitee `main` 分支 commit `d97e088`（2026-09-21）
> 涉及：magic-api 脚本 3 个 + 索引表 1 张 + dongxinheping 插件 jar（含下载接口与新版管理页面）
> 预计耗时：15 分钟（含重启与验证）

## 一、这次部署了什么

| 组件 | 内容 | 生效方式 |
|------|------|----------|
| magic-api 脚本 | `App管理/日志管理/{上传运行日志,日志列表,删除日志}.ms` | 服务重启时重新扫描 |
| 数据库表 | `t_app_device_log`（上传索引表） | 现场库执行 DDL |
| 插件 jar | `magic-plugin-dongxinheping.jar`：新增下载接口 `AppDeviceLogController` + 新版管理页面（含「App 运行日志」菜单） | 替换 `plugins/` 下 jar 并重启 |
| 主应用 jar | **不需要更换**（新代码已兼容现场旧版主 jar） | — |

功能：PDA 设置页上传运行日志 zip → 落盘 `{upload.dir}/app-log/{设备ID}/` + 写索引；管理网页查看/下载/删除，按设备自动保留最近 20 个包。

## 二、前置准备

1. 办公机上新插件 jar（已构建好）：
   `magic-plugin-dongxinheping/target/magic-plugin-dongxinheping.jar`（约 1.06MB，构建时间 2026-09-21 17:39 之后）
2. 现场服务器能访问 gitee（拉代码），或有方式把上述 jar 拷贝过去（U盘/共享均可）。
3. 现场库的执行权限（DDL 一条，幂等可重复执行）。

## 三、部署步骤（建议低峰期，全程约 1 次重启）

### 1. 更新脚本代码

在 现场 服务器上的仓库工作目录拉取：

```bash
git fetch && git checkout main && git pull gitee main
# 确认包含：data/dongxinheping/api/东信和平/App管理/日志管理/ 目录（3个.ms + group.json）
```

### 2. 生产库建表

在**生产服务实际连的那个库**上执行（幂等，可重复跑）：

```sql
CREATE TABLE IF NOT EXISTS t_app_device_log (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    device_id varchar(64) NOT NULL COMMENT '设备唯一ID(App首次启动生成并持久化,白名单[A-Za-z0-9_-])',
    app_version varchar(32) DEFAULT NULL COMMENT 'App展示用版本号',
    file_name varchar(128) DEFAULT NULL COMMENT '落盘文件名 {deviceId}-{yyyyMMdd-HHmmss}.zip',
    file_size bigint DEFAULT NULL COMMENT 'zip字节数',
    uploaded_at datetime NOT NULL COMMENT '服务端接收时间',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '索引行创建时间',
    PRIMARY KEY (id),
    KEY idx_device_uploaded_at (device_id, uploaded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='PDA App运行日志上传索引(东信和平)';
```

执行后核对：`show create table t_app_device_log;`

### 3. 替换插件 jar 并重启

```bash
# 1) 停服务（jar 被进程占用，必须先停）
# 2) 备份旧包
cp plugins/magic-plugin-dongxinheping.jar plugins/backup/magic-plugin-dongxinheping.jar.bak-$(date +%Y%m%d)
# 3) 放入新包（覆盖）
# 4) 起服务，观察启动日志无插件加载报错
```

### 4. 部署后验证（按顺序）

| # | 验证点 | 方法 | 预期 |
|---|--------|------|------|
| 1 | 服务存活 | 浏览器开 `http://192.168.2.180:8090/api/app/log/list?pageNo=1&pageSize=5` | 返回 JSON（若 profile 开了登录校验，`code:402 凭证已过期` 也属正常，说明服务在） |
| 2 | PDA 上传 | PDA 设置页点「上传运行日志」 | Toast 成功 |
| 3 | 索引入库 | `select * from t_app_device_log order by id desc limit 3;` | 新增一行，file_name 形如 `{设备ID}-{yyyyMMdd-HHmmss}.zip` |
| 4 | 文件落盘 | 看 `{upload.dir}/app-log/{设备ID}/` | zip 存在（upload.dir 未配置则默认 `D:/mb/`，以现场 `application-*.yml` 为准） |
| 5 | 管理页 | 工作台登录 → 东信和平管理 → App 运行日志 | 列表出现记录，文件状态「在盘」，点下载得到 zip，解包应有 device-info.txt / session-*.log / logcat.txt |
| 6 | 删除 | 页面点删除 | 记录消失、文件被删 |

## 四、注意事项

- **管理页访问方式**：管理页数据接口走 magic-api 登录校验。必须从 工作台（登录后）→ 东信和平管理 菜单进入；直接开 `static/admin/index.html` 裸页会报 402。若现场曾以"免登录 profile"运行，保持原 profile 即可，行为不变。
- **安全边界**：上传接口无鉴权（与既有 `/api/location/*` 一致）；日志 zip 含内网报文，落盘目录故意放在 `userfiles` 公网映射之外，**不要**改到 userfiles 下，也不要对外网暴露 8090。
- **保留策略**：脚本自动按设备只留最近 20 个包，超限自动删文件+删索引行，无需人工清理。
- **App 版本**：需使用已实现 zip 上传的 App 构建（现场 PDA 已在用）。

## 五、回滚

1. 停服务，把 `plugins/` 下新 jar 换回备份的旧 jar。
2. 代码回退：`git reset --hard c8c55de`（可跳过——旧主 jar 不认识新脚本里的写法也仅是接口不存在，不影响既有业务）。
3. 重启。`t_app_device_log` 表可保留不删（空表无副作用）。

## 六、常见问题

| 现象 | 原因 | 处理 |
|------|------|------|
| PDA 上传报"对象为空" | 现场脚本没拉到最新（还在用 getFile 的旧版） | 确认 git 已到 d97e088 并重启 |
| 上传 500，日志报表不存在 | 建表步骤漏了/建到别的库 | 在服务实际连接的库执行第二节 DDL |
| 管理页列表 402 凭证已过期 | 未从工作台登录态进入 | 从工作台菜单进入；或确认现场 profile 与免登录策略 |
| 列表能出但文件状态"已清理" | 设备上传超过 20 次被保留策略清理，或 upload.dir 变更过 | 属预期；如需找回去旧 upload.dir 目录找 zip |
| 下载 404 | 该记录的 zip 不在当前服务的 upload.dir 下 | 检查 upload.dir 配置是否变更过 |
