package com.ocean.tiga.engine.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高级 SQL 预处理器 (Debug 优化版)
 * * 功能：
 * 1. 自动识别脚本中的 SQL 语法并包装为 sql("""...""") 函数调用。
 * 2. 核心算法：行对行包装。转换后脚本的总行数、每一行对应的逻辑含义与原脚本完全一致。
 * 3. 增强识别：支持识别被注释干扰或非行首开始的 SQL 定义。
 * 4. 调试友好：专门为 Monaco Editor 调试设计，解决断点行号错位问题。
 *
 * @author Tiga Platform Team
 */
public class TigaSqlPreprocessor {

    private static final ThreadLocal<List<Integer>> LINE_MAP = new ThreadLocal<>();

    // 匹配 SQL 开始的正则：支持 var/def 名称 = select 或 return select，中间可插入空格或注释
    // 捕获组 1: 前缀(包括缩进和定义); 捕获组 2: select 及其后续内容
    private static final Pattern SQL_START_PATTERN = Pattern.compile("^(.*?(?:var|def|return)\\s+.*?)?\\b(select\\s+.*)$", Pattern.CASE_INSENSITIVE);

    public static List<Integer> getLineMap() {
        return LINE_MAP.get();
    }

    public static void clearLineMap() {
        LINE_MAP.remove();
    }

    /**
     * 处理入口
     */
    public static String process(String script, String scriptType) {
        if (script == null || script.isEmpty()) return script;

        List<Integer> map = new ArrayList<>();
        // -1 保证 split 能够保留末尾的空行，维持行号一致
        String[] lines = script.split("\n", -1);
        StringBuilder out = new StringBuilder();

        boolean inSql = false;
        boolean isReturnSql = false;
        StringBuilder sqlBuffer = new StringBuilder();
        String varNamePart = ""; // 存储 select 之前的所有前缀（缩进、var、变量名、等号）
        int sqlStartLine = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            int currentLineNum = i + 1;

            // 过滤掉纯注释行对 SQL 块判断的干扰
            if (trimmed.startsWith("//") || trimmed.startsWith("/*")) {
                if (!inSql) {
                    out.append(line);
                    if (i < lines.length - 1) out.append("\n");
                    map.add(currentLineNum);
                    continue;
                }
            }

            if (!inSql) {
                Matcher matcher = SQL_START_PATTERN.matcher(line);
                // 识别条件：包含 select 关键字，且满足定义格式
                if (matcher.find()) {
                    inSql = true;
                    sqlStartLine = currentLineNum;

                    String prefix = matcher.group(1); // e.g., "  var result = "
                    String sqlPart = matcher.group(2); // e.g., "select id from..."

                    isReturnSql = prefix != null && prefix.contains("return");
                    varNamePart = prefix != null ? prefix : "";

                    sqlBuffer.setLength(0);
                    sqlBuffer.append(sqlPart).append("\n");
                } else {
                    // 普通代码行：直接原样输出
                    out.append(line);
                    if (i < lines.length - 1) out.append("\n");
                    map.add(currentLineNum);
                }
            } else {
                // 已经在 SQL 块中，检测结束条件
                // 结束条件：遇到了下一个脚本关键字，且 ${} 括号已闭合
                if (isPotentialEnd(trimmed) && isBraceBalanced(sqlBuffer.toString())) {
                    // 刷出 SQL 缓冲区
                    flushSql(out, varNamePart, sqlBuffer.toString(), isReturnSql, scriptType, map, sqlStartLine);

                    inSql = false;
                    varNamePart = "";
                    sqlBuffer.setLength(0);

                    // 关键：SQL 块结束后，立刻补上当前行的换行，确保下一行代码行号正确
                    if (i < lines.length) out.append("\n");

                    i--; // 重新处理当前这一行（因为当前行是 SQL 之后的代码）
                    continue;
                } else {
                    // SQL 内容行
                    sqlBuffer.append(line).append("\n");
                }
            }
        }

        // 处理文件末尾恰好是 SQL 的情况
        if (inSql) {
            flushSql(out, varNamePart, sqlBuffer.toString(), isReturnSql, scriptType, map, sqlStartLine);
        }

        LINE_MAP.set(map);
        return out.toString();
    }

    /**
     * 判断是否是可能的脚本指令结束标识
     */
    private static boolean isPotentialEnd(String trimmed) {
        if (trimmed.isEmpty()) return false;
        // 排除掉以 // 开始的注释干扰
        if (trimmed.startsWith("//")) return false;

        return trimmed.startsWith("var ") ||
               trimmed.startsWith("def ") ||
               trimmed.startsWith("return ") ||
               trimmed.startsWith("println(") ||
               trimmed.startsWith("if ") ||
               trimmed.startsWith("for ") ||
               trimmed.equals("}");
    }

    /**
     * 括号平衡检查：确保 SQL 中的 ${ ... } 已经完整闭合
     */
    private static boolean isBraceBalanced(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == '{') count++;
            if (c == '}') count--;
        }
        // 如果 count > 0 说明 ${ 还在跨行书写中
        return count <= 0;
    }

    /**
     * 核心转换逻辑：将 SQL 块包装成 sql(""" """) 形式
     * 保证：转换后脚本在 IDE 里的视觉行号与原脚本完全对齐
     */
    private static void flushSql(StringBuilder out, String prefix, String sql, boolean isReturn,
                                 String type, List<Integer> map, int sourceLine) {
        String quote = "magic".equals(type) ? "\"\"\"" : "'''";

        // 移除 buffer 最后的换行，由我们手动控制闭合位置
        if (sql.endsWith("\n")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        String[] sqlLines = sql.split("\n", -1);

        for (int j = 0; j < sqlLines.length; j++) {
            String currentSqlLine = sqlLines[j];

            if (j == 0) {
                // 第一行：将 "var r = " 和 "sql("""select" 拼接在同一行
                // 如果 prefix 为空说明是直接以 select 开头（此时变量名策略由后端默认处理）
                String head = (prefix == null || prefix.isEmpty()) ? "var result = " : prefix;

                // 检查 prefix 是否已经包含了赋值符号，没有则补上（针对 var r select 这种不规范写法）
                if (!isReturn && !head.contains("=")) {
                    head += " = ";
                }

                out.append(head).append("sql(").append(quote).append(currentSqlLine);
            } else {
                // 中间行：原样保留 SQL 内容
                out.append(currentSqlLine);
            }

            // 如果是 SQL 块的最后一行
            if (j == sqlLines.length - 1) {
                // 将引号和右括号紧跟在 SQL 末尾，不产生额外行
                out.append(quote).append(")");
            } else {
                // 中间行补回换行
                out.append("\n");
            }
            // 记录每一行对应的原始行号，供 Debug 映射使用
            map.add(sourceLine + j);
        }
    }

    public static void main(String[] args) {
        String script = """
def MY_UPPER = (str) => {
    return str?.toUpperCase()
}
def minAmount = 300
// 2. 定义数据源
def orders = [{
        id: 1,
        title: 'phone',
        price: 500
},
    {
        id: 2,
        title: 'pad',
        price: 200
    }
]

// 3. 定义参数化 SQL
var result = select id,
    MY_UPPER(title) as upper_title //aaaa
from orders
where price >
    ${minAmount}
def aaa = "fdfdfd"
println(result)
return result""";

        System.out.println("--- 处理后的代码 ---");
        String processed = process(script, "groovy");
        System.out.println(processed);

        System.out.println("--- 行数验证 ---");
        System.out.println("原始行数: " + script.split("\n", -1).length);
        System.out.println("处理后行数: " + processed.split("\n", -1).length);
    }
}
