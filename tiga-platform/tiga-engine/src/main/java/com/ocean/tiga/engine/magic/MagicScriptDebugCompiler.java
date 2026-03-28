package com.ocean.tiga.engine.magic;

import org.ssssssss.script.MagicScript;
import org.ssssssss.script.parsing.Parser;
import org.ssssssss.script.parsing.VarIndex;
import org.ssssssss.script.parsing.ast.Node;
import javax.script.ScriptEngine;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

/**
 * 调试脚本解析工具：绕过 MagicScript 内部静态 compileCache
 *
 * @author Tiga Platform Team
 */
public class MagicScriptDebugCompiler {

    private static Constructor<MagicScript> scriptConstructor;

    static {
        try {
            // 反射获取 MagicScript 的私有构造函数
            // MagicScript(List<Node> nodes, Set<VarIndex> varIndices, ScriptEngine scriptEngine, boolean debug)
            scriptConstructor = MagicScript.class.getDeclaredConstructor(
                    List.class, Set.class, ScriptEngine.class, boolean.class);
            scriptConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("未能找到 MagicScript 的私有构造函数，请检查版本兼容性", e);
        }
    }

    /**
     * 直接创建一个不进入全局缓存的脚本对象
     */
    public static MagicScript createWithoutCache(String source, ScriptEngine scriptEngine) {
        try {
            Parser parser = new Parser();
            boolean isDebug = source.startsWith(MagicScript.DEBUG_MARK);
            String scriptContent = isDebug ? source.substring(MagicScript.DEBUG_MARK.length()) : source;

            // 手动执行解析
            List<Node> nodes = parser.parse(scriptContent);
            Set<VarIndex> varIndices = parser.getVarIndices();

            // 反射实例化
            return scriptConstructor.newInstance(nodes, varIndices, scriptEngine, isDebug);
        } catch (Exception e) {
            throw new RuntimeException("反射创建 MagicScript 失败: " + e.getMessage(), e);
        }
    }
}
