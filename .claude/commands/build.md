---
title: 'Maven Build Command'
read_only: true
type: 'command'
---

# Maven Build Command

编译 Maven 项目，支持指定模块和可选的测试执行。

## Usage

```bash
# 编译整个项目（跳过测试）
/build

# 编译指定模块（跳过测试）
/build magic-plugin
/build magic-plugin magic-plugin-api

# 编译并运行测试
/build --test
/build magic-plugin --test

# 清理后编译
/build --clean
/build magic-plugin --clean --test
```

## Command Behavior

1. **模块选择**：
   - 无参数：编译整个项目
   - 有参数：只编译指定的模块（使用 `-pl` 参数）
   - 自动编译依赖模块（使用 `-am` 参数）

2. **测试控制**：
   - 默认：跳过测试 (`-DskipTests`)
   - `--test` 标志：运行测试

3. **清理选项**：
   - `--clean` 标志：先执行 `mvn clean` 再编译

4. **输出控制**：
   - 使用安静模式 (`-q`) 减少不必要的输出
   - 显示编译结果摘要

## Command Options

- `--test` : 运行测试（默认跳过）
- `--clean` : 清理后编译

## Examples

```bash
# 快速编译整个项目
/build

# 编译插件模块
/build magic-plugin

# 编译插件模块并运行测试
/build magic-plugin --test

# 清理并完整编译（包含测试）
/build --clean --test

# 编译多个相关模块
/build magic-plugin magic-plugin-api magic-plugin-demo
```

## Implementation Details

执行 Maven 命令格式：
```bash
mvn [clean] compile [-pl module1,module2] [-am] [-DskipTests] [-q]
```

参数说明：
- `clean` : 可选，清理目标目录
- `compile` : 编译源代码
- `-pl` : 指定模块列表
- `-am` : 同时编译依赖的模块
- `-DskipTests` : 跳过测试（除非指定 --test）
- `-q` : 安静模式
