package org.ssssssss.magicboot.demo.controller;

import org.ssssssss.magicboot.demo.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 示例插件 Controller
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private HelloService helloService;

    /**
     * 问候接口
     */
    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", helloService.sayHello(name));
        result.put("plugin", "demo");
        return result;
    }

    /**
     * 插件信息接口
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", helloService.getPluginInfo());
        result.put("plugin", "demo");
        return result;
    }
}
