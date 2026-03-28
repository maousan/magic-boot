/**
 * Tiga 编辑器管理对象
 * 负责 Monaco Editor 的初始化、代码高亮配置、自动补全、悬浮提示及变量类型推断。
 */
(function (window) {
    const TigaEditorManager = {
        _isRegistered: false, // 标记位：防止重复注册全局语言特性
        // 全局变量及其对应的全路径类名映射
        globalVars: {
            'db': 'java.util.Map<java.lang.String, com.ocean.tigaapi.db.DbModule>'
        },
        // 用于存储 全局组件库 别名： Map 类型的 Key 列表
        mapKeysCache: {
            'db': []
        },

        classCache: {}, // 类元数据缓存（存储从后端获取的方法、文档等）
        allClasses: [], // 存储所有从后端加载的全路径类名列表（用于导入提示）

        /**
         * 初始化编辑器
         * @param {oject} selector 挂载容器的 对象
         * @param {string} scriptId 脚本编号
         * @param {string} script 脚本
         * @param {string} modifiedSet 正在编辑的code窗口
         */
        init: (selector,scriptId,script,modifiedSet) => {
			return new Promise((resolve, reject) => {
	            // Monaco 基础配置
	            require.config({
	                paths: { 'vs': './js/monaco/vs', 'editor': '.' },
	                'vs/nls': { availableLanguages: { '*': 'zh-cn' } }
	            });
	
	            // 加载必要的编辑器扩展组件
	            require([
	                './js/monaco/scripts/editor/default-theme.js',
	                './js/monaco/scripts/editor/dark-new-theme.js',
	                './js/monaco/scripts/editor/high-light.js',
	                './js/monaco/scripts/editor/mybatis.js',
	                './js/monaco/scripts/editor/completion.js',
	                './js/monaco/scripts/beautifier/javascript/beautifier.js',
	                'vs/editor/editor.main'
	            ], function (defTheme,darkTheme, HighLightOptions, mybatis, CompletionItemProvider, Beautifier) {
	                try {
		                const language = 'java';

                        // 仅在未注册时执行语言特性注册
                        if (!TigaEditorManager._isRegistered) {
                            mybatis();
                            // 定义并注册主题与语言
                            monaco.editor.defineTheme('darkTheme', darkTheme.editor);
                            monaco.editor.defineTheme('defTheme', defTheme.editor);
                            
                            monaco.languages.register({ id: language });
                            monaco.languages.setMonarchTokensProvider(language, HighLightOptions);
                            monaco.languages.registerCompletionItemProvider(language, CompletionItemProvider);
                            
                            // 语言交互特性配置
                            monaco.languages.setLanguageConfiguration(language, {
                                wordPattern: /(-?\d*\.\d\w*)|([^`~!#%^&*()\-=+[{\]}\\|;:'",.<>/?\s]+)/g,
                                brackets: [['{', '}'], ['[', ']'], ['(', ')']],
                                onEnterRules: [
                                    // 回车时自动补全 Javadoc 格式
                                    {
                                        beforeText: /^\s*\/\*\*(?!\/)([^\*]|\*(?!\/))*$/,
                                        afterText: /^\s*\*\/$/,
                                        action: { indentAction: monaco.languages.IndentAction.IndentOutdent, appendText: ' * ' }
                                    },
                                    {
                                        beforeText: /^\s*\/\*\*(?!\/)([^\*]|\*(?!\/))*$/,
                                        action: { indentAction: monaco.languages.IndentAction.None, appendText: ' * ' }
                                    }
                                ],
                                comments: { lineComment: '//', blockComment: ['/*', '*/'] },
                                operators: ['<=', '>=', '==', '!=', '+', '-', '*', '/', '%', '&', '|', '!', '&&', '||', '?', ':', '++', '--', '+=', '-=', '*=', '/='],
                                autoClosingPairs: [
                                    { open: '{', close: '}' }, { open: '[', close: ']' }, { open: '(', close: ')' },
                                    { open: '"""', close: '"""', notIn: ['string.multi'] },
                                    { open: '"', close: '"', notIn: ['string'] },
                                    { open: '\'', close: '\'', notIn: ['string'] },
                                    { open: '/**', close: ' */', notIn: ['string'] }
                                ],
                            });

                            // 注册文档格式化器
                            monaco.languages.registerDocumentFormattingEditProvider(language, {
                                provideDocumentFormattingEdits(model) {
                                    return [{
                                        text: new Beautifier(model.getValue()).beautify(),
                                        range: model.getFullModelRange()
                                    }]
                                }
                            });

                            // 启动各级增强服务
                            TigaEditorManager.registerCompletion(monaco);
                            TigaEditorManager.registerHover(monaco);
                            TigaEditorManager.registerSignatureHelp(monaco);
                            TigaEditorManager.preloadAllClasses();
                            
                            TigaEditorManager._isRegistered = true;
                        }
		
		                // 创建编辑器实例 (每个实例都需要独立创建)
		                const editor = monaco.editor.create(selector, {
		                    value: script,
		                    language: 'java',
		                    theme: 'defTheme',
		                    automaticLayout: true,
		                    fontSize: 16,
		                    fixedOverflowWidgets: true,
		                    minimap: { enabled: false },
			                scrollBeyondLastLine: false,
			                fontFamily: 'JetBrainsMono, Consolas, "Courier New",monospace, 微软雅黑',
			    			fontLigatures: true,
			                wordWrap: 'on',
			                lineNumbers: 'on',
			                lineDecorationsWidth: 12,
			                renderWhitespace: 'none',
			                roundedSelection: false,
			                scrollbar: { vertical: 'visible', horizontal: 'visible', useShadows: false }
		                });
		                
			            // 注册追加头部命令（用于自动 import）
			            monaco.editor.registerCommand('editor.action.appendHead', (_c, text) => {
			    			if(editor.getValue()?.indexOf(text) > -1){ return }
			    			editor.executeEdits('', [{
			    				forceMoveMarkers: true,
			    				text:text,
			    				range: new monaco.Range(1, 0, 1, 0)
			    			}])
			    		});
		
			            // 绑定快捷键动作
			    		editor.addAction({
						  id: 'editor.action.triggerSuggest.extension',
						  label: '触发代码提示',
						  keybindings: [ monaco.KeyMod.CtrlCmd | monaco.KeyCode.Enter, monaco.KeyMod.Alt | monaco.KeyCode.Enter ],
						  run: function() { editor.trigger(null, 'editor.action.triggerSuggest'); }
						});
						
						editor.addAction({
						    id: 'editor.action.foldAll.extension',
						    label: '全部折叠',
						    keybindings: [ monaco.KeyMod.Alt | monaco.KeyCode.BracketLeft ],
						    run: function() { editor.getAction('editor.foldAll').run(); }
						});
						
						editor.addAction({
						    id: 'editor.action.unfoldAll.extension',
						    label: '全部展开',
						    keybindings: [ monaco.KeyMod.Alt | monaco.KeyCode.BracketRight ],
						    run: function() { editor.getAction('editor.unfoldAll').run(); }
						});
						
						// 注册 Alt + Q 快捷键
						editor.addAction({
						    id: 'tiga.sqlToGinq.action',
						    label: 'SQL 转 GINQ 助手',
						    keybindings: [
						        monaco.KeyMod.Alt | monaco.KeyCode.KeyQ
						    ],
						    precondition: null,
						    keybindingContext: null,
						    contextMenuGroupId: 'navigation', // 添加到右键菜单的“导航”组中
						    contextMenuOrder: 1.5,
						    run: function(ed) {
						        // 直接调用之前定义的 openSqlTool
						        // 注意：由于 openSqlTool 已经处理了 SqlToGinqModal.show
						        // 我们只需要修改其回调，将内容插入光标处
						        SqlToGinqModal.show((convertedCode) => {
						            if (!convertedCode) return;
						            
						            // 获取当前选中的位置（如果没有选中，则是光标位置）
						            const selection = ed.getSelection();
						            const range = new monaco.Range(
						                selection.startLineNumber, 
						                selection.startColumn, 
						                selection.endLineNumber, 
						                selection.endColumn
						            );
						
						            // 执行插入操作
						            ed.executeEdits('sql-to-ginq', [
						                {
						                    range: range,
						                    text: convertedCode,
						                    forceMoveMarkers: true // 插入后光标移至内容末尾
						                }
						            ]);
						            
						            // 插入后自动格式化一下新插入的代码（可选）
						            ed.getAction('editor.action.formatDocument').run();
						        });
						    }
						});
						// 脚本保存 快捷键
						editor.addAction({
						    id: 'tiga.savescript.action',
						    label: '保存',
						    keybindings: [
						        monaco.KeyMod.Alt | monaco.KeyCode.KeyS,
						        monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS,
						    ],
						    run: async(ed) =>{
						         if(modifiedSet.value.has(scriptId)){
									const status =  await TigaEditorManager.Request('/tiga/api/save', 'POST', {id:scriptId,script:ed.getValue()});
				                    if (status){
										modifiedSet.value.delete(scriptId);
									}
								 }
						    }
						});
		
		                // 全局 Promise 错误拦截
						if (!window.hasMonacoErrorHandler) {
						    window.addEventListener('unhandledrejection', function (event) {
						        if (event.reason && (event.reason.name === 'Canceled')) event.preventDefault();
						    });
						    window.hasMonacoErrorHandler = true;
						}
						
				        window.addEventListener('resize', function() { if (editor) editor.layout(); });
		
		                resolve(editor); 
		            } catch (e) {
		                reject(e);
		            }
	            });
            });
        },

        /**
         * 预加载后端所有全路径类名
         */
        async preloadAllClasses() {
            try {
                const txt = await fetch('/tiga/meta/classes.txt').then(r => r.text());
                this.allClasses = this.parseCompressedText(txt);
            } catch (e) { console.error('Failed to load classes.txt', e); }
        },

        /**
         * 解析压缩格式的类名文本
         * @param {string} txt 格式为 "pkg:cls1,cls2"
         */
        parseCompressedText(txt) {
            const result = [];
            if (!txt) return result;
            txt.split('\n').forEach(line => {
                const idx = line.indexOf(':');
                if (idx < 0) return;
                const pkg = line.substring(0, idx);
                line.substring(idx + 1).split(',').forEach(c => {
                    const cls = c.trim();
                    if (cls) result.push(pkg ? (pkg + '.' + cls) : cls);
                });
            });
            return result;
        },

        /**
         * 注册自动补全提供
         */
        registerCompletion(monaco) {
            const self = this;
            monaco.languages.registerCompletionItemProvider('java', {
                triggerCharacters: ['.', ' ', '='],
                provideCompletionItems: async (model, position) => {
                    const line = model.getLineContent(position.lineNumber);
                    const prefix = line.substring(0, position.column);
                    const word = model.getWordUntilPosition(position);

                    // --- 1. 处理多级调用 (如 db.master.isAl) ---
                    const multiMatch = prefix.match(/([a-zA-Z0-9_]+)\.([a-zA-Z0-9_]+)\.([a-zA-Z0-9_]*)$/);
                    if (multiMatch) {
                        const rootVar = multiMatch[1];
                        const subPath = multiMatch[2];
                        const className = self.inferClassName(model, rootVar, subPath);
                        if (className) {
                            const res = await self.getMethodSuggestions(className, monaco, model, position);
                            res.suggestions.forEach(s => s.sortText = '100_' + s.label);
                            return res;
                        }
                    }

                    // --- 2. 处理基础的对象/Map调用 (如 db. 或 a.) ---
                    const dotMatch = prefix.match(/([a-zA-Z0-9_]+)\.([a-zA-Z0-9_]*)$/);
                    if (dotMatch) {
                        const varName = dotMatch[1];
                        const fullType = self.globalVars[varName];
                        
                        // Map 类型特殊处理：提供 Key 提示
                        if (fullType && fullType.startsWith('java.util.Map<')) {
                            const keys = self.mapKeysCache[varName] || [];
                            const keySuggestions = keys.map(k => ({
                                label: k,
                                kind: monaco.languages.CompletionItemKind.EnumMember,
                                detail: `Key of ${varName}`,
                                insertText: k,
                                sortText: '000_' + k,
                                range: new monaco.Range(position.lineNumber, word.startColumn, position.lineNumber, word.endColumn)
                            }));
                            const mapMethods = await self.getMethodSuggestions('java.util.Map', monaco, model, position);
                            mapMethods.suggestions.forEach(s => s.sortText = '100_' + s.label);
                            return { suggestions: [...keySuggestions, ...mapMethods.suggestions] };
                        }

                        // 常规类方法推断 (包含 String, int 等)
                        const className = self.inferClassName(model, varName);
                        if (className) {
                            const res = await self.getMethodSuggestions(className, monaco, model, position);
                            res.suggestions.forEach(s => s.sortText = '100_' + s.label);
                            return res;
                        }
                    }

                    // --- 3. 全局公共提示 (变量名、包名、类名) ---
                    let allSuggestions = [];
                    if (!prefix.includes('.') || prefix.match(/^\s*[a-zA-Z0-9_]*$/)) {
                        const globalSugs = self.getGlobalVarSuggestions(monaco, word, position);
                        globalSugs.suggestions.forEach(s => s.sortText = '200_' + s.label);
                        allSuggestions = [...allSuggestions, ...globalSugs.suggestions];
                    }

                    const pathMatch = prefix.match(/([a-zA-Z0-9._]+)$/);
                    if (pathMatch) {
                        const currentInput = pathMatch[1];
                        const classPathSugs = await self.getClassPathSuggestions(monaco, currentInput, model, position, word);
                        allSuggestions = [...allSuggestions, ...classPathSugs.suggestions];
                    }

                    return { suggestions: allSuggestions };
                }
            });
        },

        /**
         * 获取类路径和包名的补全建议
         */
        async getClassPathSuggestions(monaco, currentInput, model, position, word) {
            if (!this.allClasses.length) return { suggestions: [] };
            const lastDotIndex = currentInput.lastIndexOf('.');
            const isPathMode = lastDotIndex > -1;
            const parentPath = isPathMode ? currentInput.substring(0, lastDotIndex + 1) : "";
            const nextLevelItems = new Set();
            const suggestions = [];

            this.allClasses.forEach(fullPath => {
                let segment = "";
                let isClass = false;
                if (isPathMode) {
                    if (fullPath.startsWith(currentInput)) {
                        const suffix = fullPath.substring(parentPath.length);
                        const parts = suffix.split('.');
                        segment = parts[0];
                        isClass = parts.length === 1;
                    }
                } else {
                    const className = fullPath.substring(fullPath.lastIndexOf('.') + 1);
                    if (className.toLowerCase().startsWith(currentInput.toLowerCase())) {
                        segment = className;
                        isClass = true;
                    }
                }

                if (segment && !nextLevelItems.has(segment + (isClass ? '_cls' : '_pkg'))) {
                    nextLevelItems.add(segment + (isClass ? '_cls' : '_pkg'));
                    suggestions.push({
                        label: segment,
                        kind: isClass ? monaco.languages.CompletionItemKind.Class : monaco.languages.CompletionItemKind.Folder,
                        detail: isClass ? fullPath : `package ${parentPath + segment}`,
                        insertText: segment,
                        range: new monaco.Range(position.lineNumber, word.startColumn, position.lineNumber, word.endColumn),
                        sortText: (isClass ? '200_' : '110_') + segment,
                        additionalTextEdits: (isClass && !model.getLineContent(position.lineNumber).trim().startsWith('import')) 
                                             ? this.generateImportEdits(model, fullPath) : []
                    });
                }
            });
            return { suggestions };
        },

        /**
         * 变量类型推断逻辑
         * 支持字面量识别、显式声明推断及 Map 深度推断
         */
        inferClassName(model, varName, subPath = null) {
            let fullType = this.globalVars[varName];

            // 处理 Map 泛型推断 (例如 db.master -> 获取泛型 V)
            if (fullType && subPath) {
                const genericMatch = fullType.match(/java\.util\.Map<[^,]+,\s*([^>]+)>/);
                if (genericMatch) return genericMatch[1].trim(); 
            }
            if (fullType) return fullType;
            
            const lines = model.getLinesContent();
            // 正则匹配变量赋值行
            const assignmentRegex = new RegExp(`(?:^|\\s+)([a-zA-Z0-9_<>\\[\\]]+)?\\s*${varName}\\s*=([^;\\n]*)`);
            
            for (let i = lines.length - 1; i >= 0; i--) {
                const line = lines[i].trim();
                let match = line.match(assignmentRegex);
                
                if (match) {
                    let explicitType = match[1] ? match[1].trim() : null;
                    let value = match[2].trim();
                    
                    // A. 基础显式类型声明映射（含 Groovy/Java 常用类型）
                    if (explicitType) {
                        const typeMap = {
                            'int': 'java.lang.Integer',
                            'long': 'java.lang.Long',
                            'double': 'java.lang.Double',
                            'float': 'java.lang.Float',
                            'short': 'java.lang.Short',
                            'byte': 'java.lang.Byte',
                            'boolean': 'java.lang.Boolean',
                            'string': 'java.lang.String',
                            'String': 'java.lang.String'
                        };
                        if (typeMap[explicitType]) return typeMap[explicitType];
                        // 复杂对象处理：从 import 查找全路径
                        if (/^[A-Z]/.test(explicitType) && !['var','def'].includes(explicitType)) {
                            return this.findFullClassFromImport(lines, explicitType);
                        }
                    }

                    // B. 基于右侧赋值字面量推断类型
                    if (/^["']/.test(value)) return 'java.lang.String';
                    if (/^-?\d+L$/i.test(value)) return 'java.lang.Long';
                    if (/^-?\d+\.\d+/.test(value)) return 'java.lang.Double';
                    if (/^-?\d+$/.test(value)) return 'java.lang.Integer';
                    if (value.startsWith('[:') || (value.startsWith('[') && value.includes(':'))) return 'java.util.Map';
                    if (value.startsWith('[') && !value.includes(':')) return 'java.util.List';
                    
                    let matchNew = value.match(/^new\s+([a-zA-Z0-9_]+)/);
                    if (matchNew) return this.findFullClassFromImport(lines, matchNew[1]);
                }
            }
            // 处理静态类调用 (首字母大写视为类)
            if (/^[A-Z]/.test(varName)) return this.findFullClassFromImport(lines, varName);
            return null;
        },

        /**
         * 根据简单类名查找全路径类名
         */
        findFullClassFromImport(lines, simpleName) {
            for (let line of lines) {
                if (line.trim().startsWith('import ') && line.trim().endsWith(simpleName)) {
                    return line.trim().replace('import ', '').replace(';', '').trim();
                }
            }
            // 常用基础类兜底
            const common = { 'String': 'java.lang.String', 'Date': 'java.util.Date' };
            if (common[simpleName]) return common[simpleName];
            return this.allClasses.find(f => f.endsWith('.' + simpleName)) || simpleName;
        },

        /**
         * 调用后端接口获取类的详细方法元数据并转化为建议项
         */
       async getMethodSuggestions(className, monaco, model, position) {
            if (!this.classCache[className]) {
                try {
                    const params = new URLSearchParams();
                    params.append('className', className);
                    const res = await fetch(`/tiga/meta/class`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: params
                    }).then(r => r.json());
                    if (res.code === 200) this.classCache[className] = res.data;
                } catch (e) { return { suggestions: [] }; }
            }

            const meta = this.classCache[className];
            if (!meta || !meta.methods) return { suggestions: [] };
            
            const word = model.getWordUntilPosition(position);
            return {
                suggestions: meta.methods.map(m => {
                    // 构建方法插入片段
                    let snippet = m.name + "(";
                    if (m.parameters && m.parameters.length > 0) {
                        snippet += m.parameters.map((p, index) => `\${${index + 1}:${p}}`).join(", ");
                    }
                    snippet += ")";

                    // 构建文档 Markdown 描述
                    let docValue = "";
                    if (m.annotations?.length > 0) {
                        docValue += m.annotations.map(a => `\`${a}\``).join(' ') + "\n\n---\n";
                    }
                    let cleanComment = (m.comment || '暂无说明').replace(/\\n/g, '\n');
                    docValue += `### ${m.name}\n${cleanComment}\n\n**Return:** \`${m.returnType}\``;

                    return {
                        label: m.name,
                        kind: monaco.languages.CompletionItemKind.Method,
                        detail: `${m.returnType} (${m.parameters.join(', ')})`,
                        documentation: { value: docValue, isTrusted: true, supportHtml: true },
                        insertText: snippet,
                        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                        range: new monaco.Range(position.lineNumber, word.startColumn, position.lineNumber, word.endColumn),
                        command: { id: 'editor.action.triggerParameterHints', title: '参数提示' }
                    };
                })
            };
        },

        /**
         * 生成自动导入语句的编辑操作
         */
        generateImportEdits(model, fullPath) {
            const content = model.getValue();
            if (content.includes(`import ${fullPath}`)) return [];
            return [{ range: new monaco.Range(1, 1, 1, 1), text: `import ${fullPath}\n` }];
        },

        /**
         * 获取预定义的全局变量提示
         */
        getGlobalVarSuggestions(monaco, word, position) {
            return {
                suggestions: Object.keys(this.globalVars).map(varName => ({
                    label: varName,
                    kind: monaco.languages.CompletionItemKind.Variable,
                    detail: `内置变量 (${this.globalVars[varName]})`,
                    insertText: varName,
                    sortText: '000_' + varName,
                    range: new monaco.Range(position.lineNumber, word.startColumn, position.lineNumber, word.endColumn)
                }))
            };
        },

        /**
         * 注册悬停提示服务
         */
        registerHover(monaco) {
            const self = this;
            monaco.languages.registerHoverProvider('java', {
                provideHover: async (model, position) => {
                    const word = model.getWordAtPosition(position);
                    if (!word) return null;
                    const line = model.getLineContent(position.lineNumber);
                    const prefix = line.substring(0, word.startColumn - 1);
                    const multiDotMatch = prefix.match(/([a-zA-Z0-9_]+)\.([a-zA-Z0-9_.]+)\.$/);
                    const singleDotMatch = prefix.match(/([a-zA-Z0-9_]+)\.$/);
                    let className = null;
                    let isMethodHover = false;

                    // 推断悬停单词所属的类
                    if (multiDotMatch) {
                        className = self.inferClassName(model, multiDotMatch[1], multiDotMatch[2]);
                        isMethodHover = true;
                    } else if (singleDotMatch) {
                        const rootVar = singleDotMatch[1];
                        const fullType = self.globalVars[rootVar];
                        if (fullType && fullType.startsWith('java.util.Map<')) {
                            className = self.inferClassName(model, rootVar, word.word);
                            isMethodHover = false;
                        } else {
                            className = self.inferClassName(model, rootVar);
                            isMethodHover = true;
                        }
                    } else {
                        className = self.inferClassName(model, word.word);
                        isMethodHover = false;
                    }

                    if (!className) return null;
                    if (!self.classCache[className]) await self.getMethodSuggestions(className, monaco, model, position);
                    const meta = self.classCache[className];
                    if (!meta) return null;

                    // 显示方法文档或类文档
                    if (isMethodHover) {
                        const method = meta.methods.find(m => m.name === word.word);
                        if (method) {
                            let contents = [];
                            if (method.annotations?.length > 0) contents.push({ value: "```java\n" + method.annotations.join('\n') + "\n```" });
                            contents.push({ value: `### 方法: ${method.name}` });
                            contents.push({ value: (method.comment || '').replace(/\\n/g, '\n') });
                            contents.push({ value: `**返回类型:** \`${method.returnType}\`  \n**参数:** \`(${method.parameters.join(', ')})\`` });
                            return { contents: contents };
                        }
                    } 
                    let contents = [];
                    if (meta.annotations?.length > 0) contents.push({ value: "```java\n" + meta.annotations.join('\n') + "\n```" });
                    contents.push({ value: `### 类: ${meta.className}` });
                    contents.push({ value: (meta.doc || '暂无类说明').replace(/\\n/g, '\n') });
                    return { contents: contents };
                }
            });
        },

        /**
         * 注册函数签名帮助（参数提示）
         */
        registerSignatureHelp(monaco) {
            const self = this;
            monaco.languages.registerSignatureHelpProvider('java', {
                signatureHelpTriggerCharacters: ['(', ','],
                provideSignatureHelp: async (model, position) => {
                    const line = model.getLineContent(position.lineNumber);
                    const prefix = line.substring(0, position.column);
                    const multiMatch = prefix.match(/([a-zA-Z0-9_]+)\.([a-zA-Z0-9_]+)\.([a-zA-Z0-9_]+)\s*\($/);
                    const singleMatch = prefix.match(/([a-zA-Z0-9_]+)\.([a-zA-Z0-9_]+)\s*\($/);
                    let className = null, methodName = null;

                    // 解析当前所在的函数调用上下文
                    if (multiMatch) {
                        methodName = multiMatch[3];
                        className = self.inferClassName(model, multiMatch[1], multiMatch[2]);
                    } else if (singleMatch) {
                        methodName = singleMatch[2];
                        className = self.inferClassName(model, singleMatch[1]);
                    }

                    if (className) {
                        if (!self.classCache[className]) await self.getMethodSuggestions(className, monaco, model, position);
                        const meta = self.classCache[className];
                        if (meta && meta.methods) {
                            const methods = meta.methods.filter(m => m.name === methodName);
                            if (methods.length === 0) return null;
                            return {
                                value: {
                                    signatures: methods.map(m => ({
                                        label: `${m.name}(${m.parameters.join(', ')})`,
                                        parameters: m.parameters.map(p => ({ label: p })),
                                        documentation: { value: (m.comment || '').replace(/\\n/g, '\n'), isTrusted: true }
                                    })),
                                    activeSignature: 0, activeParameter: 0
                                },
                                dispose: () => {}
                            };
                        }
                    }
                    return null;
                }
            });
        },
        async Request (url, method, data){
    	    try {
    	        const params = new URLSearchParams();
    	        for (let name in data) { params.append(name, data[name]); }
    	        let options = { method: method, headers: { 'Content-Type': 'application/x-www-form-urlencoded' } };
    	        if (method.toUpperCase() === 'GET') {
    	            const queryString = params.toString();
    	            if (queryString) { url = `${url}${url.includes('?') ? '&' : '?'}${queryString}`; }
    	        } else { options.body = params; }
    	        const res =  await fetch(url, options).then(r => r.json());
    	        if(res.code == 200) return res.data;
    	        return null;
    	    } catch (e) { console.error("请求失败:", e); return null; }
    	},
       async getModuleKeys() {
            var data = await this.Request('/tiga/engin/getModuleKeys', 'get', {modelNames:'db'});
            if(data){
				this.mapKeysCache['db'] = data['db'];
			}
       }
    };

        
	TigaEditorManager.getModuleKeys();
    // 暴露入口
    window.MonacoJave = (el,scriptId,script,modifiedSet) => { return TigaEditorManager.init(el,scriptId,script,modifiedSet); };
})(window);