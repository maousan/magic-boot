package org.ssssssss.magicboot.demo.service;

import org.pf4j.ExtensionPoint;

/**
 * 示例服务接口
 */
public interface HelloService extends ExtensionPoint {
    String sayHello(String name);
    String getPluginInfo();
}
