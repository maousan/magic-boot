package com.ocean.tiga.engine.magic.function.context;

import com.ocean.tiga.engine.sql.CalciteEngine;
import org.ssssssss.script.MagicScriptContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * SQL函数扩展（用于MagicScript引擎）
 *
 * @author Tiga Platform Team
 */
public class SqlFunctionExtension implements Function<Object, Object> {

    private final MagicScriptContext context;

    public SqlFunctionExtension(MagicScriptContext context) {
        this.context = context;
    }

    @Override
    public Object apply(Object arg) {
        // 获取 SQL 字符串
        // Magic-Script 传入的参数可能是 String，也可能是数组（多参数情况下）
        String sqlText = null;
        if (arg instanceof Object[]) {
            Object[] args = (Object[]) arg;
            sqlText = args.length > 0 ? String.valueOf(args[0]) : null;
        } else {
            sqlText = String.valueOf(arg);
        }

        if (sqlText == null || sqlText.trim().isEmpty()) {
            return null;
        }

        String scriptId = Objects.toString(context.get("__scriptId__"), "default");
        Map<String, Object> env = new HashMap<>(context.getRootVariables());

        Map<String, Object> variables = context.getVariables().getVariables(context);
        if (variables != null) {
            env.putAll(variables);
        }

        try {
            return CalciteEngine.execute(scriptId, sqlText, env, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
