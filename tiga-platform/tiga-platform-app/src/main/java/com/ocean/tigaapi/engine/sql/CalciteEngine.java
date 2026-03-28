package com.ocean.tigaapi.engine.sql;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.CallImplementor;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.avatica.ColumnMetaData;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.prepare.CalcitePrepareImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.FunctionParameter;
import org.apache.calcite.schema.ImplementableFunction;
import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.server.CalciteServerStatement;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.Program;
import org.apache.calcite.tools.Programs;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * CalciteEngine: 高性能 SQL 执行引擎
 * 基于 Apache Calcite 实现，支持将内存 List<Map> 映射为表进行 SQL 查询，
 * 并支持动态参数绑定（#{var}）和自定义函数（UDF）调用。
 */
@SuppressWarnings("unchecked")
public final class CalciteEngine {
	
    // 正则表达式：用于匹配 SQL 中的变量占位符，支持 #{var} 或 ${var} 格式
    private static final Pattern VAR_PATTERN = Pattern.compile("(?s)[#$]\\{\\s*([^}\\s]+)\\s*\\}");

    /**
     * 内部监控统计类：用于追踪引擎的性能和缓存状态
     */
    public static class Metrics {
        public static final AtomicLong HIT_COUNT = new AtomicLong(0);  // 成功从缓存获取预编译计划的次数
        public static final AtomicLong MISS_COUNT = new AtomicLong(0); // 缓存未命中（需要重新编译）的次数
        
        /**
         * 打印当前引擎的执行报告到控制台
         */
        public static void report() {
            System.out.println("\n======= 引擎监控报告 =======");
            System.out.printf("缓存命中: %d, 错失: %d, 当前缓存数: %d\n", HIT_COUNT.get(), MISS_COUNT.get(), CONTEXT_CACHE.size());
            System.out.println("===========================");
        }
    }

    /**
     * 脚本上下文：核心缓存对象
     * 存储 SQL 预编译后的中间产物，避免重复解析和优化产生的性能开销
     */
    private static class ScriptContext {
        final String rawSql;           // 用户传入的带占位符的原始 SQL
        @SuppressWarnings("unused")
		final String targetSql;        // 转换后符合 Calcite 语法的 SQL（占位符变为了 ?）
        final List<String> paramPaths; // 记录参数出现的顺序，例如 ["user.id", "order.no"]
        final CalcitePrepare.CalciteSignature<Object[]> signature; // Calcite 编译后的执行签名（包含执行计划）
        final JavaTypeFactory typeFactory; // 类型工厂，用于运行时数据类型转换
        final Map<String, Object> localUdfPool = new ConcurrentHashMap<>(); // 存储本次执行可用的自定义函数对象

        ScriptContext(String rawSql, String targetSql, List<String> paramPaths, 
                      CalcitePrepare.CalciteSignature<Object[]> signature, JavaTypeFactory typeFactory) {
            this.rawSql = rawSql;
            this.targetSql = targetSql;
            this.paramPaths = paramPaths;
            this.signature = signature;
            this.typeFactory = typeFactory;
        }
    }

    // 设置最大缓存条数，防止内存溢出
    private static final int MAX_CACHE_SIZE = 500; 
    
    // 使用 LRU (Least Recently Used) 策略存储编译好的 ScriptContext
    // LinkedHashMap 的 accessOrder 设置为 true，确保最常使用的脚本保留在内存中
    private static final Map<String, ScriptContext> CONTEXT_CACHE = Collections.synchronizedMap(
        new LinkedHashMap<String, ScriptContext>(MAX_CACHE_SIZE, 0.75f, true) {
            private static final long serialVersionUID = 1L;
            @Override protected boolean removeEldestEntry(Map.Entry<String, ScriptContext> eldest) {
                return size() > MAX_CACHE_SIZE; // 超过阈值时自动移除最久未使用的项
            }
        }
    );

    /**
     * 清除指定 ID 的缓存
     */
    public static void clearCache(String scriptId) {
        CONTEXT_CACHE.remove(scriptId);
    }

    /**
     * 清空所有预编译缓存
     */
    public static void clearAllCache() {
        int size = CONTEXT_CACHE.size();
        CONTEXT_CACHE.clear();
        System.out.println("[CalciteEngine] 已清空全部缓存，释放条数: " + size);
    }
    
    /**
     * 执行 SQL 入口
     * @param scriptId 脚本唯一标识（用于缓存检索，建议相同 SQL 使用固定 ID）
     * @param sql SQL 语句（支持 #{var} 语法）
     * @param env 环境变量上下文（Key 为表名或函数名，Value 为 List<Map> 数据或 Function 对象）
     * @param fullScan 是否扫描全量数据推断 Schema（true: 检查所有行获取列并集；false: 仅检查第一行）
     */
    public static List<Map<String, Object>> execute(String scriptId, String sql, Map<String, Object> env, boolean fullScan) throws Exception {
        long start = System.nanoTime();
        
        // 1. 尝试从缓存获取已编译的上下文
        ScriptContext ctx = (scriptId != null) ? CONTEXT_CACHE.get(scriptId) : null;

        // 如果缓存不存在，或者原始 SQL 发生了变化，则触发重新编译
        boolean isMiss = (ctx == null || !ctx.rawSql.equals(sql));
        if (isMiss) {
            Metrics.MISS_COUNT.incrementAndGet();
            ctx = compileToContext(sql, env, fullScan);
            if (scriptId != null) CONTEXT_CACHE.put(scriptId, ctx);
        } else {
            Metrics.HIT_COUNT.incrementAndGet();
        }

        // 2. 准备 UDF（用户自定义函数）环境
        // 将 env 中非 List 类型（即对象、Lambda、Closure 等）识别为函数，放入执行池
        final ScriptContext currentCtx = ctx;
        env.forEach((k, v) -> {
            if (!(v instanceof List)) {
                currentCtx.localUdfPool.put(k.toUpperCase(), v);
            }
        });

        long compileEnd = System.nanoTime();
        
        // 3. 绑定数据并实际执行
        List<Map<String, Object>> result = executeContext(ctx, env, fullScan);
        long totalEnd = System.nanoTime();

        // 打印耗时统计日志
        System.out.printf("[%s] %s | 总耗时: %.2fms (解析/编译: %.2fms, 纯执行: %.2fms)\n", 
            isMiss ? "MISS" : "HIT ", scriptId, (totalEnd - start)/1e6, (compileEnd - start)/1e6, (totalEnd - compileEnd)/1e6);
        
        return result;
    }

    /**
     * 核心编译流程：将 SQL 转换为 Calcite 的 RelNode 执行计划
     */
    private static ScriptContext compileToContext(String sql, Map<String, Object> env, boolean fullScan) throws Exception {
        // 1. 参数提取与 SQL 转换：将 #{path.to.var} 替换为 JDBC 风格的 ?
        List<String> paramPaths = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        Matcher matcher = VAR_PATTERN.matcher(sql);
        while (matcher.find()) {
            paramPaths.add(matcher.group(1).trim()); 
            matcher.appendReplacement(sb, "?");
        }
        matcher.appendTail(sb);
        String targetSql = sb.toString();

        // 2. 配置 Calcite 优化规则：包含谓词下推、投影转置、聚合规约等
        final List<RelOptRule> rules = new ArrayList<>(Arrays.asList(
            CoreRules.FILTER_INTO_JOIN,            
            CoreRules.FILTER_PROJECT_TRANSPOSE,
            CoreRules.PROJECT_JOIN_TRANSPOSE,
            CoreRules.JOIN_CONDITION_PUSH,
            CoreRules.PROJECT_REDUCE_EXPRESSIONS,  
            CoreRules.FILTER_REDUCE_EXPRESSIONS,
            CoreRules.AGGREGATE_REDUCE_FUNCTIONS,
            EnumerableRules.ENUMERABLE_JOIN_RULE,
            EnumerableRules.ENUMERABLE_PROJECT_RULE,
            EnumerableRules.ENUMERABLE_FILTER_RULE,
            EnumerableRules.ENUMERABLE_AGGREGATE_RULE,
            EnumerableRules.ENUMERABLE_SORT_RULE,
            EnumerableRules.ENUMERABLE_LIMIT_RULE,
            EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE
        ));
        rules.addAll(EnumerableRules.rules());

        // 3. 建立 Calcite 逻辑连接
        Properties info = new Properties();
        info.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), "false"); // 忽略大小写
        
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info)) {
            CalciteConnection calciteConn = connection.unwrap(CalciteConnection.class);
            SchemaPlus rootSchema = calciteConn.getRootSchema();
            
            // 4. 动态注册表（List）和标量函数（Object）到元数据 Schema 中
            env.forEach((k, v) -> {
                if (v instanceof List) {
                    rootSchema.add(k, new MapListTable((List<Map<String, Object>>) v, fullScan));
                } else {
                    // 对于所有非 List 对象，注册为一个通用的标量函数适配器
                    rootSchema.add(k.toUpperCase(), new UniversalScalarFunction(k.toUpperCase()));
                }
            });

            // 5. 配置解析与优化器环境
            FrameworkConfig config = Frameworks.newConfigBuilder()
                .parserConfig(SqlParser.Config.DEFAULT.withCaseSensitive(false))
                .defaultSchema(rootSchema)
                .programs(Programs.ofRules(rules)) 
                .build();

            // 6. SQL 标准处理流程：解析 -> 校验 -> 逻辑计划(RelNode)
            Planner planner = Frameworks.getPlanner(config);
            SqlNode parse = planner.parse(targetSql);
            SqlNode validate = planner.validate(parse);
            RelNode rel = planner.rel(validate).project();
            
            // 7. 运行启发式/代价优化器，生成 Enumerable（可执行）物理计划
            RelOptPlanner relOptPlanner = rel.getCluster().getPlanner();
            Program program = Programs.ofRules(rules);
            RelNode optimizedRel = program.run(relOptPlanner, rel, 
                    rel.getTraitSet().replace(EnumerableConvention.INSTANCE), 
                    Collections.emptyList(), Collections.emptyList());

            // 8. 生成可执行签名：包含了运行时所需的类型信息和代码生成的执行逻辑
            CalciteServerStatement serverStatement = calciteConn.createStatement().unwrap(CalciteServerStatement.class);
            CalcitePrepare.Context prepareContext = serverStatement.createPrepareContext();
            
            CalcitePrepare.Query<Object[]> query = CalcitePrepare.Query.of(optimizedRel);
            CalcitePrepare.CalciteSignature<Object[]> signature = new CalcitePrepareImpl().prepareSql(
                    prepareContext, query, Object[].class, -1);
            
            return new ScriptContext(sql, targetSql, paramPaths, signature, prepareContext.getTypeFactory());
        }
    }

    /**
     * 运行时：基于编译好的上下文，绑定实际数据并获取结果集
     */
    private static List<Map<String, Object>> executeContext(ScriptContext ctx, Map<String, Object> env, boolean fullScan) {
        // 创建运行时数据上下文，Calcite 执行时会回调 get() 方法获取具体参数值
        DataContext dataContext = new DataContext() {
            @Override public SchemaPlus getRootSchema() {
                // 重新构建 Schema 以便获取最新的 List 数据引用
                SchemaPlus root = Frameworks.createRootSchema(true);
                env.forEach((k, v) -> {
                    if (v instanceof List) root.add(k, new MapListTable((List<Map<String, Object>>) v, fullScan));
                    else root.add(k.toUpperCase(), new UniversalScalarFunction(k.toUpperCase()));
                });
                return root;
            }
            @Override public JavaTypeFactory getTypeFactory() { return ctx.typeFactory; }
            @Override public QueryProvider getQueryProvider() { return null; }
            @Override public @Nullable Object get(String name) {
                // 绑定动态参数：Calcite 内部占位符格式为 ?0, ?1 ...
                if (name.startsWith("?")) {
                    int index = Integer.parseInt(name.substring(1));
                    if (index < ctx.paramPaths.size()) {
                        String path = ctx.paramPaths.get(index);
                        return getValueByPath(env, path); // 从环境 Map 中递归查找 path 对应的值
                    }
                }
                // 特殊逻辑：将 scriptContext 注入，方便 UDF 在执行时能反查函数池
                return "_SCRIPT_CONTEXT".equals(name) ? ctx : null;
            }
        };

        // 执行计划生成的 Enumerable，并开始迭代数据
        Enumerable<?> enumerable = ctx.signature.enumerable(dataContext);
        List<String> fieldNames = new ArrayList<>();
        // 提取结果集的列名
        for (ColumnMetaData col : ctx.signature.columns) fieldNames.add(col.label);
        
        List<Map<String, Object>> results = new ArrayList<>();
        // 遍历迭代器，将行数据转换回熟悉的 Map 结构
        for (Object row : enumerable) results.add(normalizeRow(row, fieldNames));
        return results;
    }

    /**
     * 工具方法：根据点号分隔的路径（如 user.profile.name）从嵌套 Map 中提取值
     */
    private static Object getValueByPath(Map<String, Object> env, String path) {
        if (path == null) return null;
        if (!path.contains(".")) return env.get(path);
        
        String[] keys = path.split("\\.");
        Object current = env;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null; 
            }
            if (current == null) return null;
        }
        return current;
    }

    /**
     * 格式化行数据：
     * Calcite 单列查询返回单对象，多列查询返回 Object[]，此方法统一转为 Map<列名, 值>
     */
    private static Map<String, Object> normalizeRow(Object row, List<String> fieldNames) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (fieldNames.size() > 1) {
            Object[] rowArray = (Object[]) row;
            for (int i = 0; i < fieldNames.size(); i++) map.put(fieldNames.get(i), rowArray[i]);
        } else {
            map.put(fieldNames.get(0), row);
        }
        return map;
    }

    /**
     * 通用 UDF 分发器：
     * Calcite 在 SQL 中遇到自定义函数时，会生成的代码调用此 dispatch 方法。
     * 它负责处理各种类型的 Java 调用：包括普通方法、Lambda 表达式和 Groovy 闭包。
     */
    public static class UniversalDispatcher {
        public static Object dispatch(String name, Object[] args, DataContext root) {
            try {
                // 获取当前执行上下文
                ScriptContext ctx = (ScriptContext) root.get("_SCRIPT_CONTEXT");
                if (ctx == null) return "ERR:CONTEXT_MISSING";
                
                // 从池中查找函数对象
                Object func = ctx.localUdfPool.get(name);
                if (func == null) return "ERR:UDF_NOT_FOUND";

                // 1. 预处理：将 Calcite 内部专有对象（如 NlsString, BigDecimal）转为标准 Java 对象
                Object[] unwrappedArgs = new Object[args.length];
                int lastNonNullIndex = -1;
                for (int i = 0; i < args.length; i++) {
                    Object val = unwrapCalciteObject(args[i]);
                    unwrappedArgs[i] = val;
                    if (args[i] != null) {
                        lastNonNullIndex = i;
                    }
                }

                // 2. 动态裁剪参数长度（过滤末尾因 SQL 占位产生的 null 参数）
                int effectiveLength = Math.max(1, lastNonNullIndex + 1);
                Object[] finalArgs = Arrays.copyOf(unwrappedArgs, effectiveLength);

                // 3. 专项适配：Java Lambda (特别是 MagicScript 动态生成的 Lambda)
                if (func.getClass().getName().contains("$$Lambda$")) {
                    Method[] methods = func.getClass().getMethods();
                    for (Method m : methods) {
                        // 寻找非合成的 apply 方法
                        if ("apply".equals(m.getName()) && !m.isSynthetic()) {
                            m.setAccessible(true);
                            if (m.getParameterCount() == 2) {
                                // 适配特定的二元调用约定
                                return m.invoke(func, null, (Object) finalArgs);
                            }
                        }
                    }
                }

                // 4. 专项适配：Groovy Closure
                if (func instanceof groovy.lang.Closure) {
                    groovy.lang.Closure<?> closure = (groovy.lang.Closure<?>) func;
                    int maxParam = closure.getMaximumNumberOfParameters();
                    // 按闭包支持的最大参数量进行截断调用
                    return closure.call(Arrays.copyOf(unwrappedArgs, Math.min(effectiveLength, maxParam)));
                }

                // 5. 兜底处理：反射寻找通用的 apply 方法
                Method applyMethod = Arrays.stream(func.getClass().getMethods())
                        .filter(m -> m.getName().equals("apply") && !m.isSynthetic())
                        .findFirst().orElse(null);
                if (applyMethod != null) {
                    applyMethod.setAccessible(true);
                    int pCount = applyMethod.getParameterCount();
                    // 1个参数通常是数组，2个参数通常是上下文+数组
                    if (pCount == 1) return applyMethod.invoke(func, (Object) finalArgs);
                    return applyMethod.invoke(func, null, (Object) finalArgs);
                }

            } catch (Exception e) {
                Throwable t = e.getCause() != null ? e.getCause() : e;
                return "ERR:" + t.getMessage();
            }
            return "ERR:UNKNOWN_FUNC_TYPE";
        }

        /**
         * 内部数据类型转换，解包 Calcite 封装的对象
         */
        private static Object unwrapCalciteObject(Object obj) {
            if (obj == null) return null;
            // 处理字符串
            if (obj instanceof org.apache.calcite.util.NlsString) {
                return ((org.apache.calcite.util.NlsString) obj).getValue();
            }
            // 处理数值
            if (obj instanceof BigDecimal) {
                BigDecimal bd = (BigDecimal) obj;
                // 如果没有小数位则转为 Long，否则转为 Double
                return bd.scale() <= 0 ? bd.longValue() : bd.doubleValue();
            }
            return obj;
        }
    }

    /**
     * 自定义标量函数定义：使 Calcite 逻辑层能够感知并验证外部函数调用
     */
    private static class UniversalScalarFunction implements ScalarFunction, ImplementableFunction {
        private final String funcName;
        public UniversalScalarFunction(String funcName) { this.funcName = funcName; }

        @Override public List<FunctionParameter> getParameters() {
            // 定义 10 个可选的 ANY 类型参数。这允许 SQL 在编译期通过校验，
            // 无论用户传 1 个还是 5 个参数，都能正确匹配到这个函数。
            return new AbstractList<FunctionParameter>() {
                @Override public int size() { return 10; } 
                @Override public FunctionParameter get(int index) {
                    return new FunctionParameter() {
                        @Override public int getOrdinal() { return index; }
                        @Override public String getName() { return "arg" + index; }
                        @Override public RelDataType getType(RelDataTypeFactory tf) { return tf.createSqlType(SqlTypeName.ANY); }
                        @Override public boolean isOptional() { return true; }
                    };
                }
            };
        }

        @Override public RelDataType getReturnType(RelDataTypeFactory tf) { 
            // 声明返回值为 VARCHAR，实际运行时可以返回任意对象并转为字符串
            return tf.createSqlType(SqlTypeName.VARCHAR); 
        }

        @Override public CallImplementor getImplementor() {
            // 代码生成器：将 SQL 里的函数名调用 映射为执行 Java 代码：
            // UniversalDispatcher.dispatch("FUNC_NAME", new Object[]{...}, DataContext.ROOT)
            return (translator, call, nullAs) -> {
                try {
                    Method method = UniversalDispatcher.class.getMethod("dispatch", String.class, Object[].class, DataContext.class);
                    return Expressions.call(method, Expressions.constant(funcName), 
                            Expressions.newArrayInit(Object.class, translator.translateList(call.getOperands(), nullAs)), DataContext.ROOT);
                } catch (Exception e) { throw new RuntimeException(e); }
            };
        }
    }

    /**
     * 数据表适配器：将普通的 List<Map> 包装为 Calcite 识别的表
     */
    private static class MapListTable extends AbstractTable implements ScannableTable {
        private final List<Map<String, Object>> data;
        private final boolean fullScan;
        
        MapListTable(List<Map<String, Object>> data, boolean fullScan) { 
            this.data = data; 
            this.fullScan = fullScan; 
        }
        
        @Override public RelDataType getRowType(RelDataTypeFactory tf) {
            // 核心：推断表结构（Schema）
            RelDataTypeFactory.Builder b = tf.builder();
            // 如果开启 fullScan，则遍历所有行把所有出现过的 Key 都当作列；
            // 否则只拿第一行当作列定义。
            Map<String, Object> schema = (fullScan && !data.isEmpty()) ? scanFull() : (data.isEmpty() ? Collections.emptyMap() : data.get(0));
            schema.forEach((k, v) -> b.add(k, deduceSqlType(tf, v)).nullable(true));
            return b.build();
        }

        /**
         * 启发式类型推断：将 Java 对象映射为 SQL 标准类型
         */
        private RelDataType deduceSqlType(RelDataTypeFactory tf, Object value) {
            if (value instanceof Integer) return tf.createSqlType(SqlTypeName.INTEGER);
            if (value instanceof Long) return tf.createSqlType(SqlTypeName.BIGINT);
            if (value instanceof Double) return tf.createSqlType(SqlTypeName.DOUBLE);
            if (value instanceof BigDecimal) return tf.createSqlType(SqlTypeName.DECIMAL);
            if (value instanceof String) return tf.createSqlType(SqlTypeName.VARCHAR);
            if (value instanceof Boolean) return tf.createSqlType(SqlTypeName.BOOLEAN);
            return tf.createSqlType(SqlTypeName.ANY); // 兜底类型
        }
        
        /**
         * 全扫描推断：合并所有行的 KeySet
         */
        private Map<String, Object> scanFull() {
            Map<String, Object> m = new LinkedHashMap<>();
            data.forEach(m::putAll);
            return m;
        }
        
        @Override public Enumerable<Object[]> scan(DataContext root) {
            // 数据读取：将 List<Map> 的数据按照之前定义的 RowType 列顺序转换为 Object[] 数组
            List<String> fields = getRowType(root.getTypeFactory()).getFieldNames();
            return Linq4j.asEnumerable(data).select(r -> {
                Object[] res = new Object[fields.size()];
                for (int i = 0; i < fields.size(); i++) res[i] = r.get(fields.get(i));
                return res;
            });
        }
    }
}