package com.ocean.tiga.engine.magic;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocean.tiga.engine.util.StringUtils;

/**
 * 强化版 MagicScript 安全沙箱检查器
 * 防御逻辑：编码还原 -> 注释剔除 -> 语义特征扫描
 *
 * @author Tiga Platform Team
 */
public class MagicScriptSecurityChecker {

    // 1. 深度敏感包黑名单
    private static final List<String> BLACKLIST_PACKAGES = Arrays.asList(
            "java.lang.System", "java.lang.Runtime", "java.lang.Thread",
            "java.lang.reflect", "java.lang.Process", "java.io.File",
            "java.io.FileInputStream", "java.io.FileOutputStream",
            "java.net", "javax.naming", "java.util.Scanner",
            "org.noear.solon.Solon", "java.lang.ClassLoader",
            "javax.script.ScriptEngineManager", "com.sun."
    );

    // 2. 危险方法/特征码黑名单 (防止反射与动态调用)
    private static final List<String> DANGER_FEATURES = Arrays.asList(
            "Class.forName", ".getClass(", ".getMethod(", ".getConstructor(",
            ".getDeclaredField(", ".newInstance(", ".invoke(", "System.exit",
            "Runtime.getRuntime", ".loadLibrary(", "newThread", "newFile"
    );

    // 正则：匹配 import 语句
    private static final Pattern IMPORT_PATTERN = Pattern.compile("(?m)^\\s*import\\s+([\\w\\.*]+)");

    // 正则：匹配所有十六进制转义 (\\x6a) 和 Unicode 转义 (\\u006a)
    private static final Pattern ENCODING_PATTERN = Pattern.compile("\\\\(?:u[0-9a-fA-F]{4}|x[0-9a-fA-F]{2})");

    public static void check(String scriptText) throws RuntimeException {
        if (StringUtils.isEmpty(scriptText)) return;

        // --- 预处理阶段 ---
        // 1. 还原编码 (防止 \\u006a 绕过)
        String processedCode = decodeSource(scriptText);
        // 2. 移除所有空白符和注释，压缩成单行语义串 (防止通过换行、空格打断正则匹配)
        String compressedCode = processedCode.replaceAll("(?s)/\\*.*?\\*/|//.*", "").replaceAll("\\s+", "");

        // --- 扫描阶段 ---
        // 1. 检查 Import
        checkImports(scriptText);

        // 2. 检查危险特征码 (在压缩后的代码中找，防止拼接干扰)
        for (String feature : DANGER_FEATURES) {
            if (compressedCode.contains(feature.replaceAll("\\s+", ""))) {
                throw new RuntimeException("安全合规异常：检测到危险指令或反射调用特征 [" + feature + "]");
            }
        }

        // 3. 扫描全限定名 (java.lang.System...)
        for (String blackItem : BLACKLIST_PACKAGES) {
            if (compressedCode.contains(blackItem)) {
                throw new RuntimeException("安全合规异常：禁止直接引用敏感类库 [" + blackItem + "]");
            }
        }

        // 4. 防御字符串拼接绕过 (检查关键敏感词拆分后出现在代码中)
        // 这种检查比较严格，如果脚本确实需要处理包含 "Runtime" 字符串的数据可能会误伤
        checkStringSplitHacks(compressedCode);
    }

    /**
     * 还原源码中的转义字符
     */
    private static String decodeSource(String source) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = ENCODING_PATTERN.matcher(source);
        int last = 0;
        while (matcher.find()) {
            sb.append(source, last, matcher.start());
            String group = matcher.group();
            try {
                int res = group.startsWith("\\u")
                        ? Integer.parseInt(group.substring(2), 16)
                        : Integer.parseInt(group.substring(2), 16);
                sb.append((char) res);
            } catch (Exception e) {
                sb.append(group); // 还原失败则保留原样
            }
            last = matcher.end();
        }
        sb.append(source.substring(last));
        return sb.toString();
    }

    private static void checkImports(String scriptText) {
        Matcher matcher = IMPORT_PATTERN.matcher(scriptText);
        while (matcher.find()) {
            String imported = matcher.group(1).trim();
            for (String blackItem : BLACKLIST_PACKAGES) {
                if (imported.startsWith(blackItem)) {
                    throw new RuntimeException("安全合规异常：禁止导入敏感包 [" + imported + "]");
                }
            }
        }
    }

    private static void checkStringSplitHacks(String compressedCode) {
        // 针对最高等级的绕过：如 var a = "java.lang."; var b = "Runtime"; Class.forName(a+b);
        // 如果代码中同时出现了关键碎片的组合，则预警
        if (compressedCode.contains("Class.forName") || compressedCode.contains(".invoke")) {
             if (compressedCode.contains("Runtime") || compressedCode.contains("Process") || compressedCode.contains("System")) {
                 throw new RuntimeException("安全合规异常：检测到疑似动态拼接反射调用");
             }
        }
    }
}
