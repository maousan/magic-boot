package org.ssssssss.magicapi.file;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 文件插件测试启动类
 * 用于集成测试时启动 Spring Boot 上下文
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.ssssssss.magicapi.file",
    "org.ssssssss.magicapi.core"
})
public class MagicApiFileTestApplication {
    // 测试启动类，不需要 main 方法
}
