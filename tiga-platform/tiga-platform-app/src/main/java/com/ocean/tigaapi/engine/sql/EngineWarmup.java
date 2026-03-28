package com.ocean.tigaapi.engine.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * SQL 引擎预热
 */
public class EngineWarmup {
    public static void init() {
        try {
            // 构造极简的上下文
            Map<String, Object> warmupEnv = new HashMap<>();
            List<Map<String, Object>> dummyData = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("id", 1);
            dummyData.add(row);
            
            warmupEnv.put("warmup_table", dummyData);
            
            // 执行一次简单的 SQL
            // 这会触发：解析器加载、优化器初始化、代码生成编译
            CalciteEngine.execute(
                "WARMUP_ID", 
                "SELECT id FROM warmup_table WHERE id = 1", 
                warmupEnv, 
                false
            );
            
            // 执行完后可以清除掉这个预热的缓存，释放内存
            CalciteEngine.clearCache("WARMUP_ID");
            
            System.out.println(">>> CalciteEngine 预热完成");
        } catch (Exception e) {
            System.err.println(">>> CalciteEngine 预热失败: " + e.getMessage());
        }
    }
}