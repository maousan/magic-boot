package com.ocean.tigaapi.engine.groovy.function.context;

import java.util.HashMap;
import java.util.Map;

import com.ocean.tigaapi.engine.sql.CalciteEngine;

import groovy.lang.Script;

/**
 * 脚本基类（最终整合版）
 *
 * 所有 Groovy 脚本都会继承这个类。
 * SQL 调用最终都会走 sql(String, Map)。
 */
public abstract class SqlEngineAdapter extends Script {

    /**
     * SQL 执行入口（由 AST 注入 locals）
     */
    @SuppressWarnings("unchecked")
	public Object sql(String sqlStr, Map<String, Object> locals) throws Exception {
        if (locals == null) {
            locals = new HashMap<>();
        }

        // 调试输出（可保留，也可删除）
//        System.out.println(">>> sql locals = " + locals);

        // 合并 binding 变量（全局）+ locals（局部）
        Map<String, Object> env = new HashMap<>();
        if (getBinding() != null && getBinding().getVariables() != null) {
            env.putAll(getBinding().getVariables());
        }
        env.putAll(locals);

        // 获取 scriptId（用于 LinqEngine 缓存）
        String scriptId = (String) env.getOrDefault("__scriptId__", "default");

        // 执行 SQL
        return CalciteEngine.execute(scriptId, sqlStr, env, true);
    }
    
//    // 存放所有函数实例
//    private static final List<Object> functionInstances = new ArrayList<>();

//    static {
//        // 在这里添加所有 magic-script 的函数实例
//        functionInstances.add(new com.ocean.tigaapi.engine.groovy.function.global.MagicFunctionAdapter());
//    }
//
//    @Override
//    public Object invokeMethod(String name, Object args) {
//        Object[] argsArray = (Object[]) args;
//        
//        // 遍历所有实例，寻找匹配的方法
//        for (Object instance : functionInstances) {
//            try {
//                // 尝试通过反射找到并执行方法
//                // 这里使用简单的匹配，Groovy 的 MetaClass 性能会更好，但这样写最通用
//                for (Method method : instance.getClass().getMethods()) {
//                    if (method.getName().equals(name) && method.getParameterCount() == argsArray.length) {
//                        return method.invoke(instance, argsArray);
//                    }
//                }
//            } catch (Exception ignored) {
//                // 继续尝试下一个实例
//            }
//        }
//        
//        // 如果都没找到，交给父类处理（报错或执行 Groovy 自有方法）
//        return super.invokeMethod(name, args);
//    }
}
