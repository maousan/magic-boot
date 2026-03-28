package org.ssssssss.script.parsing.ast.statement;

import org.ssssssss.script.compile.MagicScriptCompiler;
import org.ssssssss.script.parsing.Span;
import org.ssssssss.script.parsing.VarIndex;
import org.ssssssss.script.parsing.ast.Expression;
import org.ssssssss.script.parsing.ast.Node;

import java.util.ArrayList;
import java.util.List;

public class VariableDestructuringDefine extends VariableDefine {

	private final List<VariableDefine> defines;
	private final Expression right;
	private final boolean mapAccess;
	private final VariableDefine restDefine;  // 支持 rest 操作符 (...)

	public VariableDestructuringDefine(Span span, int size, Expression right, boolean mapAccess) {
		this(span, size, right, mapAccess, null);
	}

	public VariableDestructuringDefine(Span span, int size, Expression right, boolean mapAccess, VariableDefine restDefine) {
		super(span, null, null);
		this.defines = new ArrayList<>(size);
		this.right = right;
		this.mapAccess = mapAccess;
		this.restDefine = restDefine;
	}

	public void add(VariableDefine variableDefine) {
		defines.add(variableDefine);
	}

	@Override
	public void visitMethod(MagicScriptCompiler compiler) {
		if (right != null) {
			right.visitMethod(compiler);
		}
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.visit(this.right).store(4);
		
		// 编译普通属性
		for(int i =0, size = this.defines.size(); i < size ; i++){
			VariableDefine define = this.defines.get(i);
			compiler.pre_store(define.getVarIndex());
			if(this.mapAccess){
				compiler.newRuntimeContext()
					.load4()
					.ldc(define.getVarIndex().getName())    // 成员名
					.insn(ICONST_1)    // 是否可空调用 ?.
					.asBoolean()
					.insn(ICONST_0)    // 非linq中
					.asBoolean()
					.call("member_access", 5);
			} else {
				compiler.load4().visitInt(i).asInteger().operator("map_or_array_access");
			}
			compiler.scopeStore();
		}
		
		// 编译剩余属性（rest 操作符）
		if (restDefine != null) {
			compiler.pre_store(restDefine.getVarIndex());
			if (this.mapAccess) {
				// 对于对象解构，需要传递已提取的属性名数组
				compiler.newRuntimeContext()
					.load4()
					.ldc("object_rest");
				// 创建已提取属性名数组
				compiler.visitInt(this.defines.size());
				compiler.typeInsn(ANEWARRAY, Object.class);
				compiler.insn(DUP);
				for (int i = 0; i < this.defines.size(); i++) {
					VariableDefine define = this.defines.get(i);
					compiler.visitInt(i)
						.ldc(define.getVarIndex().getName())
						.insn(AASTORE);
					if (i < this.defines.size() - 1) {
						compiler.insn(DUP);
					}
				}
				compiler.call("destructuring_rest", 4);
			} else {
				// 对于数组解构，只需要传递已提取的数量（作为整数，需要装箱为Object）
				compiler.newRuntimeContext()
					.load4()
					.ldc("array_rest")
					.visitInt(this.defines.size())
					.invoke(INVOKESTATIC, Integer.class, "valueOf", Integer.class, int.class)
					.call("destructuring_rest", 4);
			}
			compiler.scopeStore();
		}
	}
}
