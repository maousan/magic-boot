package com.ocean.tigaapi.engine.magic.function.context;
import org.ssssssss.script.MagicScriptContext;

import com.ocean.tigaapi.engine.sql.CalciteEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class SqlFunctionExtension implements Function<Object, Object> {
    
    private final MagicScriptContext context;

    public SqlFunctionExtension(MagicScriptContext context) {
        this.context = context;
    }

    @Override
    public Object apply(Object arg) {
        // --- 这里就是获取 SQL 字符串的地方 ---
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

        // 打印或处理获取到的 SQL
//        System.out.println("捕获到 SQL 字符串: " + sqlText);
        
        String scriptId = Objects.toString(context.get("__scriptId__"), "default");
        Map<String, Object> env = new HashMap<>(context.getRootVariables());
        
        Map<String, Object> variables = context.getVariables().getVariables(context);
        if(variables != null) {
        	env.putAll(variables);
        }
        try {
            return CalciteEngine.execute(scriptId, sqlText, env, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}