package com.ocean.tiga.engine.groovy;

import com.ocean.tiga.engine.sql.TigaSqlPreprocessor;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import java.util.*;

/**
 * 调试探针注入器
 *
 * 作用：
 *  1. 在 Groovy 编译阶段（SEMANTIC_ANALYSIS）向每一条语句注入调试探针 onLine(...)
 *  2. 探针行号使用 SQL 预处理器提供的"源代码行号映射"，确保调试器行号与原始脚本一致
 *  3. 维护作用域栈（scopeStack），捕获当前作用域内已声明的变量，用于调试变量展示
 *  4. 支持 if/for/while/try-catch 等嵌套结构的递归处理
 *  5. 每条语句注入两类探针：
 *      - before-probe（canPause = true）
 *      - after-probe（canPause = false，仅在声明变量时注入）
 *  6. 所有探针都调用 GroovyDebugManager.onLine(...)，用于断点、单步、变量快照等调试功能
 *
 * 说明：
 *  - 本类只负责 AST 注入，不负责调试逻辑本身
 *  - 调试逻辑由 GroovyDebugManager 统一管理
 *  - 本类必须在 SQL 预处理器之后执行，否则行号映射无法生效
 *
 * @author Tiga Platform Team
 */
public class GroovyDebugCustomizer extends CompilationCustomizer {

    /** 调试会话 ID（由前端传入，用于区分不同脚本调试） */
    private final String sid;

    /** 是否开启调试模式（普通执行时不注入探针） */
    private final boolean isDebug;

    /** 标记 AST 节点为探针，避免重复处理 */
    private static final String IS_PROBE = "IS_PROBE";

    /** 作用域栈：每一层是一个 Set<String>，记录当前作用域内已声明的变量 */
    private final Deque<Set<String>> scopeStack = new ArrayDeque<>();

    /** runtimeLine → sourceLine 的映射表（由 SQL 预处理器生成） */
    private final List<Integer> lineMap;

    public GroovyDebugCustomizer(String sid, boolean isDebug) {
        super(CompilePhase.SEMANTIC_ANALYSIS);
        this.sid = sid;
        this.isDebug = isDebug;
        this.lineMap = TigaSqlPreprocessor.getLineMap(); // 从 ThreadLocal 读取行号映射
    }

    /**
     * 将运行时行号转换为原始脚本行号
     */
    private int toSourceLine(int runtimeLine) {
        if (lineMap == null) return runtimeLine;
        if (runtimeLine <= 0 || runtimeLine > lineMap.size()) return runtimeLine;
        return lineMap.get(runtimeLine - 1);
    }

    /**
     * 编译入口：遍历所有方法，注入探针
     */
    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        if (!isDebug) return; // 非调试模式不注入探针

        for (MethodNode method : classNode.getMethods()) {

            Statement code = method.getCode();
            if (!(code instanceof BlockStatement)) continue;

            // 初始化根作用域（包含方法参数）
            scopeStack.clear();
            Set<String> root = new LinkedHashSet<>();
            for (Parameter p : method.getParameters()) {
                root.add(p.getName());
            }
            scopeStack.push(root);

            // 处理方法体
            BlockStatement block = (BlockStatement) code;
            processBlock(block, classNode);

            // 在方法第一行插入一个探针（用于调试器初始化）
            int srcLine = toSourceLine(method.getLineNumber());
            if (srcLine > 0) {
                block.getStatements().add(0, createProbe(srcLine, true, classNode));
            }

            if (code instanceof BlockStatement) {
                injectProbesIntoClosures((BlockStatement) code, classNode);
            }
        }
    }

    // 扫描方法体内部嵌套的闭包
    private void injectProbesIntoClosures(BlockStatement block, ClassNode owner) {
        block.visit(new org.codehaus.groovy.ast.CodeVisitorSupport() {
            @Override
            public void visitClosureExpression(ClosureExpression expression) {
                super.visitClosureExpression(expression);

                if (expression.getCode() instanceof BlockStatement) {
                    BlockStatement closureBlock = (BlockStatement) expression.getCode();

                    // 1. 开启闭包作用域：放入闭包参数
                    Set<String> closureScope = new LinkedHashSet<>();
                    if (expression.getParameters() != null) {
                        for (Parameter p : expression.getParameters()) {
                            closureScope.add(p.getName());
                        }
                    }

                    scopeStack.push(closureScope);

                    // 2. 递归处理闭包内的语句块（注入探针）
                    processBlock(closureBlock, owner);

                    // 3. 退出闭包作用域
                    scopeStack.pop();
                }
            }
        });
    }

    /**
     * 处理一个 BlockStatement（语句块）
     * 为每条语句注入 before-probe 和 after-probe
     */
    private void processBlock(BlockStatement block, ClassNode owner) {
        List<Statement> original = block.getStatements();
        List<Statement> rewritten = new ArrayList<>();

        int previousLine = 0;

        for (Statement stat : original) {

            // 修复无行号节点：强制补行号
            int ln = stat.getLineNumber();
            if (ln <= 0) {
                ln = previousLine > 0 ? previousLine + 1 : 1;
                stat.setLineNumber(ln);
            }
            previousLine = ln;

            // 跳过空语句或已注入的探针
            if (stat instanceof EmptyStatement || Boolean.TRUE.equals(stat.getNodeMetaData(IS_PROBE))) {
                rewritten.add(stat);
                continue;
            }

            // 获取源代码行号
            int runtimeLine = stat.getLineNumber();
            int sourceLine = toSourceLine(runtimeLine);
            stat.setLineNumber(sourceLine);

            // 捕获变量声明（用于 after-probe）
            Set<String> declaredVars = extractDeclaredVars(stat);

            // 注入 before-probe（可暂停）
            rewritten.add(createProbe(sourceLine, true, owner));

            // 处理 if/for/while/try-catch 等嵌套结构
            handleNested(stat, owner);

            // 添加原语句
            rewritten.add(stat);

            // 若有变量声明，注入 after-probe（不可暂停）
            if (!declaredVars.isEmpty()) {
                // 更新当前作用域，确保后续探针包含新变量
                scopeStack.peek().addAll(declaredVars);
                // 这里的 canPause 设为 false，防止在同一行停两次，但它会推送最新的变量快照
                rewritten.add(createProbe(sourceLine, false, owner));
            }
        }

        // 替换原语句列表
        original.clear();
        original.addAll(rewritten);
    }

    /**
     * 提取当前语句中的变量声明（支持单变量与多变量声明）
     */
    private Set<String> extractDeclaredVars(Statement stat) {
        Set<String> vars = new LinkedHashSet<>();

        if (stat instanceof ExpressionStatement) {
            Expression exp = ((ExpressionStatement) stat).getExpression();

            // 1. 处理 def _str / String _str
            if (exp instanceof DeclarationExpression) {
                DeclarationExpression decl = (DeclarationExpression) exp;

                // 多变量声明：def (a, b) = ...
                if (decl.isMultipleAssignmentDeclaration()
                        && decl.getLeftExpression() instanceof TupleExpression) {

                    TupleExpression tuple = (TupleExpression) decl.getLeftExpression();
                    for (Expression e : tuple.getExpressions()) {
                        if (e instanceof VariableExpression) {
                            vars.add(((VariableExpression) e).getName());
                        }
                    }

                } else {
                    // 单变量声明：def a = ...
                    vars.add(decl.getVariableExpression().getName());
                }
            }
            // 2. 处理直接赋值（如果变量是第一次出现）
            else if (exp instanceof BinaryExpression) {
                BinaryExpression bin = (BinaryExpression) exp;
                if (bin.getOperation().getType() == org.codehaus.groovy.syntax.Types.ASSIGN) {
                    if (bin.getLeftExpression() instanceof VariableExpression) {
                        vars.add(((VariableExpression) bin.getLeftExpression()).getName());
                    }
                }
            }
        }

        return vars;
    }

    /**
     * 处理 if/for/while/try-catch 等嵌套结构
     */
    private void handleNested(Statement stat, ClassNode owner) {

        if (stat instanceof IfStatement) {
            IfStatement ifs = (IfStatement) stat;
            processSubBlock(ifs.getIfBlock(), owner);
            processSubBlock(ifs.getElseBlock(), owner);

        } else if (stat instanceof ForStatement) {
            ForStatement fs = (ForStatement) stat;

            // for 循环变量也属于当前作用域
            if (fs.getVariable() != null) {
                scopeStack.peek().add(fs.getVariable().getName());
            }

            processSubBlock(fs.getLoopBlock(), owner);

        } else if (stat instanceof WhileStatement) {
            processSubBlock(((WhileStatement) stat).getLoopBlock(), owner);

        } else if (stat instanceof TryCatchStatement) {
            TryCatchStatement t = (TryCatchStatement) stat;

            processSubBlock(t.getTryStatement(), owner);

            for (CatchStatement c : t.getCatchStatements()) {
                if (c.getVariable() != null) {
                    scopeStack.peek().add(c.getVariable().getName());
                }
                processSubBlock(c.getCode(), owner);
            }

            processSubBlock(t.getFinallyStatement(), owner);

        } else if (stat instanceof BlockStatement) {
            processSubBlock(stat, owner);
        }
    }

    /**
     * 处理子 BlockStatement（进入新作用域）
     */
    private void processSubBlock(Statement sub, ClassNode owner) {
        if (sub instanceof BlockStatement) {

            // 新作用域继承上一层变量
            scopeStack.push(new LinkedHashSet<>(scopeStack.peek()));

            processBlock((BlockStatement) sub, owner);

            // 退出作用域
            scopeStack.pop();
        }
    }

    /**
     * 创建一个调试探针（调用 GroovyDebugManager.onLine）
     *
     * @param sourceLine 源代码行号
     * @param canPause   是否允许暂停（before-probe = true，after-probe = false）
     */
    private Statement createProbe(int sourceLine, boolean canPause, ClassNode owner) {

        ArgumentListExpression args = new ArgumentListExpression();

        // 参数 1：sid（调试会话 ID）
        args.addExpression(new ConstantExpression(sid));

        // 参数 2：源代码行号
        args.addExpression(new ConstantExpression(sourceLine));

        // 参数 3：是否可暂停
        args.addExpression(new ConstantExpression(canPause));

        // 参数 4：locals（当前作用域变量）
        NamedArgumentListExpression locals = new NamedArgumentListExpression();
        if (!scopeStack.isEmpty()) {
            for (String v : scopeStack.peek()) {

                // 关键点：使用 VariableExpression 的动态构造
                VariableExpression ve = new VariableExpression(v);

                // 1: 必须设置为 null。
                // 这告诉 Groovy 编译器不要在编译时绑定索引，而是在运行时去 Context 查找。
                ve.setAccessedVariable(null);

                // 2: 告诉编译器这是一个闭包共享变量（Closure Shared Variable）
                // 虽然它看起来是本地的，但在 AST 转换后它是通过 Reference 容器共享的。
                ve.setClosureSharedVariable(true);

                // 3: 直接使用引用。
                // 配合 GroovyDebugManager 里的 Reference 处理逻辑。
                ve.setUseReferenceDirectly(true);

                locals.addMapEntryExpression(new ConstantExpression(v), ve);
            }
        }
        args.addExpression(locals);

        // 参数 5：binding 变量
        MethodCallExpression getBinding = new MethodCallExpression(
                new VariableExpression("this"),
                "getBinding",
                ArgumentListExpression.EMPTY_ARGUMENTS
        );
        MethodCallExpression getVars = new MethodCallExpression(
                getBinding,
                "getVariables",
                ArgumentListExpression.EMPTY_ARGUMENTS
        );
        args.addExpression(getVars);

        // 参数 6：脚本实例（用于抓取字段）
        args.addExpression(new VariableExpression("this"));

        // 调用 GroovyDebugManager.onLine(...)
        StaticMethodCallExpression call = new StaticMethodCallExpression(
                ClassHelper.make(GroovyDebugManager.class),
                "onLine",
                args
        );

        // 构造探针语句
        ExpressionStatement probe = new ExpressionStatement(call);
        probe.setNodeMetaData(IS_PROBE, true); // 标记为探针
        probe.setLineNumber(sourceLine);       // 设置源代码行号

        return probe;
    }
}
