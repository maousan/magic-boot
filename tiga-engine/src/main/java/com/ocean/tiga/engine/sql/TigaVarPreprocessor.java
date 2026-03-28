package com.ocean.tiga.engine.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiga 语法预处理器 (行号绝对对齐版)
 * 核心逻辑：通过字符级扫描替换，确保 JS -> Groovy 转换时换行符位置完全不变。
 * 修复：防止将箭头函数/闭包的大括号误转为 Map 的中括号。
 *
 * @author Tiga Platform Team
 */
public class TigaVarPreprocessor {

    private static final Pattern VAR_PATTERN = Pattern.compile("\\b(var|let|const)\\s+");

    // 匹配方法调用中的箭头函数头：.each((item) => {
    private static final Pattern METHOD_ARROW_START =
            Pattern.compile("\\.\\s*(\\w+)\\s*\\(\\s*\\(([^)]*)\\)\\s*=>\\s*\\{");

    // 匹配普通箭头函数头：(item) => {
    private static final Pattern PLAIN_ARROW_START =
            Pattern.compile("\\(([^)]*)\\)\\s*=>\\s*\\{");

    // 匹配闭包结尾的 })
    private static final Pattern ARROW_END = Pattern.compile("\\}\\s*\\)");

    public static String process(String script) {
        if (script == null || script.isEmpty()) return script;

        // 1. 变量转换 (原地替换：var/let/const -> def )
        script = VAR_PATTERN.matcher(script).replaceAll("def ");

        // 2. 箭头函数头部转换：将 ((p) => { 替换为 { p ->
        script = replaceArrowHeads(script);

        // 3. 闭包尾部转换：将 }) 替换为 }
        script = ARROW_END.matcher(script).replaceAll("} ");

        // 4. 精准 Map 转换：将 JS 对象 { } 转换为 Groovy Map [ ]
        // 增加逻辑：避开已经转换好的 Groovy 闭包 { p -> ... }
        script = convertMapsInPlace(script);

        return script;
    }

    /**
     * 替换箭头函数头部，保持行号
     */
    private static String replaceArrowHeads(String script) {
        // 先处理方法调用型 (如 .map((it) => { )
        Matcher m1 = METHOD_ARROW_START.matcher(script);
        StringBuilder sb = new StringBuilder();
        while (m1.find()) {
            String methodName = m1.group(1);
            String params = m1.group(2).trim();
            // 转换为 Groovy 闭包写法： .methodName { params ->
            String replacement = "." + methodName + " { " + (params.isEmpty() ? "" : params + " -> ");
            m1.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m1.appendTail(sb);
        script = sb.toString();

        // 再处理普通定义型 (如 const f = (it) => { )
        Matcher m2 = PLAIN_ARROW_START.matcher(script);
        sb = new StringBuilder();
        while (m2.find()) {
            String params = m2.group(1).trim();
            String replacement = "{ " + (params.isEmpty() ? "" : params + " -> ");
            m2.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m2.appendTail(sb);
        return sb.toString();
    }

    /**
     * 原地转换 Map 括号：{ -> [ , } -> ]
     */
    private static String convertMapsInPlace(String script) {
        char[] chars = script.toCharArray();
        boolean inString = false;
        char quoteChar = 0;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            // 1. 处理字符串，防止误伤字符串内的括号
            if ((c == '\'' || c == '"') && (i == 0 || chars[i - 1] != '\\')) {
                if (!inString) {
                    inString = true;
                    quoteChar = c;
                } else if (c == quoteChar) {
                    inString = false;
                }
                continue;
            }

            if (inString) continue;

            // 2. 识别 Map 的核心逻辑
            if (c == '{') {
                // 必须同时满足：1.上下文符合赋值/参数传递 2.内部结构像Map而非闭包
                if (isMapContext(chars, i) && isActuallyMapContent(chars, i)) {
                    chars[i] = '[';
                    // 寻找匹配的 } 并替换为 ]
                    replaceMatchingBrace(chars, i);
                }
            }
        }
        return new String(chars);
    }

    /**
     * 判断当前大括号是否处于 Map 可能出现的上下文环境中
     */
    private static boolean isMapContext(char[] chars, int index) {
        for (int i = index - 1; i >= 0; i--) {
            char c = chars[i];
            if (Character.isWhitespace(c)) continue;
            // Map 通常出现在赋值、数组元素、参数分隔符、或嵌套Map中
            return c == '=' || c == '[' || c == ',' || c == ':';
        }
        return false;
    }

    /**
     * 深度扫描大括号内部结构
     * 规则：如果第一层级内包含 ':' 且不包含 '->'，则判定为 Map
     */
    private static boolean isActuallyMapContent(char[] chars, int start) {
        int stack = 0;
        boolean hasColon = false;
        boolean inStr = false;
        char q = 0;

        for (int i = start + 1; i < chars.length; i++) {
            char c = chars[i];

            // 内部字符串处理
            if ((c == '\'' || c == '"') && chars[i-1] != '\\') {
                if (!inStr) { inStr = true; q = c; }
                else if (c == q) inStr = false;
            }
            if (inStr) continue;

            if (c == '{') stack++;
            if (c == '}') {
                if (stack == 0) break; // 扫描到当前大括号结束
                stack--;
            }

            // 只在第一层级判断
            if (stack == 0) {
                // 如果发现 -> 说明它是 Groovy 闭包头部，不能转成 Map
                if (c == '-' && i + 1 < chars.length && chars[i+1] == '>') {
                    return false;
                }
                // Map 必须有冒号
                if (c == ':') {
                    hasColon = true;
                }
            }
        }
        return hasColon;
    }

    /**
     * 找到匹配的闭合大括号并替换为中括号
     */
    private static void replaceMatchingBrace(char[] chars, int start) {
        int stack = 1;
        boolean inStr = false;
        char q = 0;
        for (int i = start + 1; i < chars.length; i++) {
            char c = chars[i];
            if ((c == '\'' || c == '"') && chars[i-1] != '\\') {
                if (!inStr) { inStr = true; q = c; }
                else if (c == q) inStr = false;
            }
            if (inStr) continue;

            if (c == '{') stack++;
            if (c == '}') {
                stack--;
                if (stack == 0) {
                    chars[i] = ']';
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        String testScript = """
import com.ocean.tiga.engine.sql.CalciteFastEngine
import java.util.concurrent.ThreadLocalRandom
import java.util.function.BiFunction
import java.util.function.Function

// ==========================================
// 1. 数据准备 (模拟数据库数据)
// ==========================================

def regions = ['CN-North', 'CN-South', 'CN-East', 'US-West', 'EU-Central']
def statuses = ['COMPLETED', 'PENDING', 'CANCELLED']

// 构造 Users 数据 (50条)
List<Map<String, Object>> users = new ArrayList<>()
for (int i = 1; i <= 50; i++) {
    users.add([
        "user_id"  : i,
        "name"     : "User_" + i,
        "vip_level": ThreadLocalRandom.current().nextInt(1, 6), // 1-5
        "region"   : regions[i % regions.size()]
    ])
}

// 构造 Orders 数据 (1000条)
List<Map<String, Object>> orders = new ArrayList<>()
for (int i = 1; i <= 1000; i++) {
    orders.add([
        "order_id": i,
        "user_id" : ThreadLocalRandom.current().nextInt(1, 51),
        "amount"  : ThreadLocalRandom.current().nextDouble(100.0, 2000.0).round(2),
        "status"  : statuses[ThreadLocalRandom.current().nextInt(statuses.size())]
    ])
}

// ==========================================
// 2. 定义 UDF (适配 Java 反射调用)
// ==========================================

// UDF 1: 计算折后价 (双参数)
// 使用 'as BiFunction' 确保生成 Java 类中包含 'apply' 方法
var calcDiscountPrice = (vipLevel, originalPrice) =>{
    double rate = 1.0
    if (vipLevel != null) {
        int vip = Integer.parseInt(vipLevel.toString())
        if (vip >= 4) rate = 0.8
        else if (vip >= 2) rate = 0.9
    }
    double price = Double.parseDouble(originalPrice.toString())
    return (double)Math.round(price * rate * 100) / 100
}

// UDF 2: 区域标签生成 (双参数)
var regionTag = (regionName, count) =>{
    return "${regionName}_TOP_${count}"
}
// ==========================================
// 4. 定义复杂 SQL
// ==========================================
// 逻辑：
// 1. 关联查询 Users 和 Orders
// 2. 筛选状态为 COMPLETED 的订单
// 3. 按 Region 分组
// 4. 计算：订单数、平均金额、总GMV、实付金额(UDF计算)
// 5. 生成：报表标签(UDF生成)
// 6. 过滤：HAVING 总金额 > 5000
// 7. 排序：按实付金额倒序


 return  SELECT
        u.region,
        COUNT(o.order_id) as total_orders,
        AVG(o.amount) as avg_price,
        SUM(o.amount) as gm_value,
        SUM(calcDiscountPrice(u.vip_level, o.amount)) as real_pay_amount,
        regionTag(u.region, COUNT(o.order_id)) as report_tag
    FROM ORDERS o
    JOIN USERS u ON o.user_id = u.user_id
    WHERE o.status = 'COMPLETED'
    GROUP BY u.region
    HAVING SUM(o.amount) > 5000
    ORDER BY real_pay_amount DESC


        		""";

        String processed = process(testScript);
        System.out.println("--- 转换结果 ---");
        System.out.println(processed);

        System.out.println("\n--- 行数严格对比 ---");
        String[] ori = testScript.split("\n", -1);
        String[] pro = processed.split("\n", -1);
        System.out.println("原始行数: " + ori.length);
        System.out.println("转换后行数: " + pro.length);

        for(int i=0; i<ori.length; i++) {
            if (!ori[i].trim().equals(pro[i].trim())) {
                System.out.println("第 " + (i+1) + " 行逻辑已转换");
            }
        }
    }
}
