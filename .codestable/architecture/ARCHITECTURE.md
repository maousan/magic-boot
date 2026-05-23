# magic-boot 架构总入口

> 状态：骨架（待填充）
> 创建日期：2026-05-21

## 1. 项目简介

magic-boot 是基于 Spring Boot 3.1.2 + magic-api 2.2.2 的低代码开发平台，支持 PF4J 运行时插件热加载、Sa-Token 认证、LiteFlow 规则引擎等。

## 2. 核心概念 / 术语表

## 3. 子系统 / 模块索引

### dongxinheping-admin

独立 Vue3 + Vite + Naive UI 管理后台，服务于东信和平（DXHP）插件的桌面端管理。与主项目 `magic-boot-vben` 前端 monorepo 平行存在，不纳入工作空间体系。

- 项目路径：`dongxinheping-admin/`
- 构建产物：`magic-plugin-dongxinheping/src/main/resources/static/admin/`
- 访问路径：`/plugin/dongxinheping-plugin/static/admin/`
- 认证方式：前端配置认证（`.env` + `localStorage`），不接入 Sa-Token
- 功能范围：LED 设备管理、库位-LED 绑定、库位-标签绑定、库位导入、批次 EPC 管理
- 与 PDA 页面（`static/pda/`）共存，PDA 面向手持终端操作员，Admin 面向桌面端仓库管理员

## 4. 关键架构决定

## 5. 已知约束 / 硬边界
