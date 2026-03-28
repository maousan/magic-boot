package com.ocean.tiga.engine.groovy.function.extension;

import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Magic扩展适配器
 *
 * @author Tiga Platform Team
 */
public class MagicExtensionAdapter {

    @SuppressWarnings("rawtypes")
    public static void autoInject(Class<?> targetClass, Object extensionInstance) {
        // 1. 获取所有声明的方法
        Method[] methods = extensionInstance.getClass().getDeclaredMethods();

        for (Method method : methods) {
            // 2. 过滤掉非 public 方法，并且确保第一个参数类型匹配 targetClass
            if (Modifier.isPublic(method.getModifiers()) &&
                method.getParameterCount() > 0 &&
                method.getParameterTypes()[0].isAssignableFrom(targetClass)) {

                String methodName = method.getName();

                // 3. 在 Java 中通过 GroovySystem 获取或创建 MetaClass
                // 这里我们手动注册一个闭包来转发调用
                ExpandoMetaClass metaClass = new ExpandoMetaClass(targetClass, true, true);
                metaClass.initialize();

                // 注册动态方法
                metaClass.registerInstanceMethod(methodName, new Closure(null) {
                    private static final long serialVersionUID = 1L;

                    @SuppressWarnings("unused")
                    public Object doCall(Object... args) throws Exception {
                        // getDelegate() 相当于 Groovy 里的 delegate (即调用者对象)
                        Object delegate = this.getDelegate();

                        // 准备参数：第一个是 delegate，后面是脚本传的参数
                        Object[] fullArgs = new Object[args.length + 1];
                        fullArgs[0] = delegate;
                        System.arraycopy(args, 0, fullArgs, 1, args.length);

                        return method.invoke(extensionInstance, fullArgs);
                    }
                });

                // 将修改后的 MetaClass 重新放回 Groovy 注册表
                groovy.lang.GroovySystem.getMetaClassRegistry().setMetaClass(targetClass, metaClass);
            }
        }
    }
}
