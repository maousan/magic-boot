package com.ocean.tigaapi.engine.magic.function.extension;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.bean.LifecycleBean;
import org.ssssssss.script.reflection.JavaReflection;

import cn.hutool.core.io.FileUtil;

@Component
public class FileExtension implements LifecycleBean {

    @Override
    public void start() throws Throwable {
    	// 核心代码：注册类型扩展
        // 参数 1：要扩展的目标 Java 类型 (Map.class)
        // 参数 2：包含扩展方法的类实例 (FileExtension 的实例)
        JavaReflection.registerMethodExtension(File.class, this);
        
        System.out.println("✅ Magic-script File 类型扩展已加载");
    }

    public static Map<String, String> info(File file){
        String suffix = FileUtil.getSuffix(file.getName());
        Map<String, String> map = new HashMap<>();
        map.put("filePath", file.getPath());
        map.put("name", file.getName());
        map.put("suffix", suffix);
        return map;
    }

}