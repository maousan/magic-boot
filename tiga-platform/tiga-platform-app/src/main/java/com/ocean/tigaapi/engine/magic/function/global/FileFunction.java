package com.ocean.tigaapi.engine.magic.function.global;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.bean.LifecycleBean;
import org.ssssssss.script.MagicScriptEngine;

@Component
public class FileFunction implements LifecycleBean {

    @Override
    public void start() throws Throwable {
        register();
    }

    private void register() {
        System.out.println("======= 开始注入 Magic-Script File 相关全局函数 =======");
        try {
            Method[] methods = FileFunction.class.getDeclaredMethods();
            for (Method method : methods) {
                // 排除 Lifecycle 接口方法和 register 本身
                if (Modifier.isPublic(method.getModifiers()) 
                    && Modifier.isStatic(method.getModifiers()) 
                    && !"register".equals(method.getName())
                    && !"start".equals(method.getName())
                    && !"stop".equals(method.getName())) {

                    MagicScriptEngine.addDefaultImport(method.getName(), (Function<Object[], Object>) (args) -> {
                        try {
                            return method.invoke(null, args);
                        } catch (Exception e) {
                            throw new RuntimeException("MagicScript全局函数 [" + method.getName() + "] 执行异常:"+e.getMessage(), e);
                        }
                    });
                    System.out.println("已注册（File相关）函数: " + method.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File new_file(String path) throws Exception {
    	File file = new File("E://aaa/"+path);
    	if(file.isDirectory()) {
    		throw new Exception("当前path路径指定的不是文件！");
    	}
        return file;
    }
}