package org.ssssssss.magicboot.demo.service;

import org.pf4j.Extension;
import org.springframework.stereotype.Service;

/**
 * 示例服务实现
 */
@Service
@Extension
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "! 欢迎使用 PF4J 插件系统！";
    }

    @Override
    public String getPluginInfo() {
        return "Demo Plugin v1.0.0 - 这是一个示例插件，用于演示 PF4J 插件系统的功能。";
    }
}
