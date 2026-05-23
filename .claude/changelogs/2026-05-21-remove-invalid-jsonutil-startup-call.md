# 移除启动类无效 JSONUtil 调用

## 背景

执行 `mvn clean package -DskipTests -pl magic-boot-master -am` 时，`magic-boot-master` 编译失败。错误位置为 `MagicBootApplication.java`，原因是调用了不存在的 `JSONUtil.toJsonStr()` 无参方法。

## 变更内容

- 删除 `MagicBootApplication.main` 中无返回值使用、无参数且无法编译的 `JSONUtil.toJsonStr()` 调用。
- 删除对应的无用 `cn.hutool.json.JSONUtil` import。
- 保持 Spring Boot 启动入口 `SpringApplication.run(...)` 不变。

## 影响范围

- 修复编译阻断。
- 不改变应用启动参数、自动配置、调度或事务开启逻辑。

## 验证

- 待重新执行 Maven 打包确认。
