package com.ocean.tigaapi.engine.groovy;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：
 *  1. 精准 locals 注入（只注入 SQL 实际使用的变量）
 *  2. 不踩未来变量（基于声明行号）
 *  3. 支持 closure 参数
 *  4. 与 JS 语法完全兼容（var/let/const/function/arrow）
 *  5. 与调试器完全兼容
 */
public class TigaSqlAstCustomizer extends CompilationCustomizer {

    public TigaSqlAstCustomizer() {
        super(CompilePhase.CANONICALIZATION);
    }

    @Override
    public void call(SourceUnit source, org.codehaus.groovy.classgen.GeneratorContext context, ClassNode classNode) {
        SqlVisitor visitor = new SqlVisitor();
        for (MethodNode m : classNode.getMethods()) {
            visitor.visitMethod(m);
        }
    }

    private static class SqlVisitor extends ClassCodeVisitorSupport {

        // 变量名 -> 声明行号
        private final Map<String, Integer> declaredLines = new LinkedHashMap<>();

        // SQL 中使用的变量名（自动推断）
        private static final Pattern SQL_VAR_PATTERN =
                Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b");

        @Override
        protected SourceUnit getSourceUnit() {
            return null;
        }

        @Override
        public void visitMethod(MethodNode node) {
            declaredLines.clear();
            super.visitMethod(node);
        }

        @Override
        public void visitDeclarationExpression(DeclarationExpression expression) {
            super.visitDeclarationExpression(expression);

            int line = expression.getLineNumber();
            if (line <= 0) return;

            if (expression.isMultipleAssignmentDeclaration()
                    && expression.getLeftExpression() instanceof TupleExpression) {

                TupleExpression tuple = (TupleExpression) expression.getLeftExpression();
                for (Expression e : tuple.getExpressions()) {
                    if (e instanceof VariableExpression) {
                        declaredLines.putIfAbsent(((VariableExpression) e).getName(), line);
                    }
                }

            } else if (expression.getLeftExpression() instanceof VariableExpression) {
                declaredLines.putIfAbsent(
                        ((VariableExpression) expression.getLeftExpression()).getName(),
                        line
                );
            }
        }

        @Override
        public void visitForLoop(ForStatement forLoop) {
            if (forLoop.getVariable() != null) {
                declaredLines.putIfAbsent(
                        forLoop.getVariable().getName(),
                        forLoop.getLineNumber()
                );
            }
            super.visitForLoop(forLoop);
        }

        @Override
        public void visitCatchStatement(CatchStatement statement) {
            if (statement.getVariable() != null) {
                declaredLines.putIfAbsent(
                        statement.getVariable().getName(),
                        statement.getLineNumber()
                );
            }
            super.visitCatchStatement(statement);
        }

        @Override
        public void visitMethodCallExpression(MethodCallExpression call) {

            if ("sql".equals(call.getMethodAsString())) {

                int callLine = call.getLineNumber();

                Expression args = call.getArguments();
                if (args instanceof ArgumentListExpression) {

                    ArgumentListExpression argList = (ArgumentListExpression) args;

                    // 第一个参数必须是 SQL 字符串
                    Expression sqlExpr = argList.getExpression(0);
                    if (!(sqlExpr instanceof ConstantExpression)) {
                        super.visitMethodCallExpression(call);
                        return;
                    }

                    String sqlText = ((ConstantExpression) sqlExpr).getText();

                    // 自动推断 SQL 中使用的变量
                    Set<String> usedVars = extractSqlVars(sqlText);

                    // 构造 locals map
                    NamedArgumentListExpression mapExpr = new NamedArgumentListExpression();

                    for (Map.Entry<String, Integer> e : declaredLines.entrySet()) {
                        String name = e.getKey();
                        Integer declLine = e.getValue();

                        if (declLine != null
                                && declLine > 0
                                && declLine < callLine
                                && usedVars.contains(name)) {

                            mapExpr.addMapEntryExpression(
                                    new ConstantExpression(name),
                                    new VariableExpression(name)
                            );
                        }
                    }

                    // 注入为 sql 的第二个参数
                    if (argList.getExpressions().size() == 1) {
                        argList.addExpression(mapExpr);
                    } else {
                        argList.getExpressions().set(1, mapExpr);
                    }
                }
            }

            super.visitMethodCallExpression(call);
        }

        /**
         * 自动推断 SQL 中使用的变量名
         */
        private Set<String> extractSqlVars(String sql) {
            Set<String> vars = new HashSet<>();
            Matcher m = SQL_VAR_PATTERN.matcher(sql);

            while (m.find()) {
                String token = m.group(1);

                // 排除 SQL 关键字（简单过滤）
                if (isSqlKeyword(token)) continue;

                // 排除数字
                if (token.matches("\\d+")) continue;

                vars.add(token);
            }

            return vars;
        }

        private boolean isSqlKeyword(String s) {
            String k = s.toUpperCase();
            return k.equals("SELECT") || k.equals("FROM") || k.equals("WHERE")
                    || k.equals("AND") || k.equals("OR") || k.equals("AS")
                    || k.equals("GROUP") || k.equals("BY") || k.equals("ORDER")
                    || k.equals("LIMIT") || k.equals("JOIN") || k.equals("LEFT")
                    || k.equals("RIGHT") || k.equals("ON") || k.equals("IN")
                    || k.equals("NOT") || k.equals("NULL") || k.equals("IS");
        }
    }
}
