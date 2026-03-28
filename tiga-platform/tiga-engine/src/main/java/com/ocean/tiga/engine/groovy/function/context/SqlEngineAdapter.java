package com.ocean.tiga.engine.groovy.function.context;

import com.ocean.tiga.engine.sql.CalciteEngine;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.Map;

/**
 * 脚本基类（最终整合版）
 *
 * 所有 Groovy 脚本都会继承这个类。
 * SQL 调用最终都会走 sql(String, Map)。
 *
 * @author Tiga Platform Team
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
}
