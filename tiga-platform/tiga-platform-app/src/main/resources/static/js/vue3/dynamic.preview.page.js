var DynamicPreviewPage = (app) => {
    app.component('dynamic-preview-page', {
        props: { id: { type: String, required: true } },
        data() {
            return { loading: false, error: false, errorMsg: '', iframeVisible: false, blobUrl: '' };
        },
        methods: {
            /**
             * 通用变量提取器：不硬编码任何名称，自动抓取所有顶层声明
             */
            extractBindings: function(code) {
                // 1. 预处理：移除注释、字符串，防止干扰
                const clean = code
                    .replace(/\/\*[\s\S]*?\*\//g, '')
                    .replace(/([^\\:]|^)\/\/.*$/gm, '$1')
                    .replace(/`([^`\\]|\\.)*`/g, ' ')
                    .replace(/'([^'\\]|\\.)*'/g, ' ')
                    .replace(/"([^"\\]|\\.)*"/g, ' ');

                // 2. 作用域抹除：只保留最顶层的代码，彻底抹除 {} 和 () 内部，防止抓取局部变量和函数参数
                let depth = 0, parenDepth = 0, topLevelCode = '';
                for (let i = 0; i < clean.length; i++) {
                    const char = clean[i];
                    if (char === '{') { if (depth === 0) topLevelCode += '{'; depth++; }
                    else if (char === '}') { depth--; if (depth === 0) topLevelCode += '}'; }
                    else if (char === '(') { if (depth === 0 && parenDepth === 0) topLevelCode += '('; parenDepth++; }
                    else if (char === ')') { parenDepth--; if (depth === 0 && parenDepth === 0) topLevelCode += ')'; }
                    else { if (depth === 0 && parenDepth === 0) topLevelCode += char; else topLevelCode += ' '; }
                }

                const bindings = new Set();
                
                // 3. 匹配标准变量声明 (const/let/var a = ..., b = ...)
                // 能够识别箭头函数定义：const submitForm = ...
                const stmtRegex = /(?:const|let|var)\s+([^;=]+)(?:=|[;]|$)/g;
                let match;
                while ((match = stmtRegex.exec(topLevelCode)) !== null) {
                    const names = match[1].trim();
                    names.split(',').forEach(n => {
                        const name = n.trim();
                        // 排除解构符号，只取纯变量名
                        if (/^[a-zA-Z_$][\w$]*$/.test(name)) bindings.add(name);
                    });
                }

                // 4. 匹配标准函数定义 (function myFunc()...)
                const funcRegex = /function\s+([a-zA-Z_$][\w$]*)/g;
                while ((match = funcRegex.exec(topLevelCode)) !== null) {
                    bindings.add(match[1]);
                }

                // 5. 匹配解构赋值 (const { name, age } = ...)
                const destrRegex = /(?:const|let|var)\s*\{([^}]+)\}\s*=/g;
                while ((match = destrRegex.exec(topLevelCode)) !== null) {
                    match[1].split(',').forEach(part => {
                        const varName = part.split(':')[0].trim(); // 处理 { a: b } 取 a
                        if (/^[a-zA-Z_$][\w$]*$/.test(varName)) bindings.add(varName);
                    });
                }
                
                return Array.from(bindings);
            },

            async fetchAndRender(){
				
                if (!this.id) return;
                this.loading = true;
                this.error = false;

                try {
					var id = this.id.substring(0,this.id.indexOf('?'));
					let rawData = {};
					if(!top.window['_components']){
						top.window['_components'] = {};
					}
					if(top.window['_components'][id]){
						rawData = top.window['_components'][id];
					}else{
						try {
				            const res = await axios.get('tiga/component/getCompiledInfoById', {params: { id: id }});
				            if (res.data.code === 200) {
				            	rawData = res.data.data;
				            	top.window['_components'][id] = rawData;
				            }
				        } catch (e) { 
				        	console.error(e);
				            ElementPlus.ElMessage.error('获取组件编译文件失败');
				            return;
				        }
					}
		
					const tpl = decodeURIComponent(escape(atob(rawData.compiled_template || '')));
			        let js = decodeURIComponent(escape(atob(rawData.compiled_js || '')));
			        const css = decodeURIComponent(escape(atob(rawData.compiled_css || '')));
                    
                    const businessOption = {componentId: id, _baseInfo:{}};

                    // 预处理脚本：移除 import 和 defineProps，这些在运行时环境不需要
                    js = js.replace(/import\s+[\s\S]*?from\s+['"]vue['"]/g, '')
                           .replace(/import\s+[\s\S]*?from\s+['"]element-plus['"]/g, '')
                           .replace(/const\s+\w+\s*=\s*defineProps\([\s\S]*?\)/g, '');

                    // 动态提取所有变量名
                    const bindings = this.extractBindings(js);
                    
                    // 构建执行脚本体：动态生成 return 对象，包含所有提取到的变量
                    const cleanedJs = `
                        try {
                            ${js}
                            // 动态构建导出对象
                            const __exports = {};
                            ${JSON.stringify(bindings)}.forEach(key => {
                                try { __exports[key] = eval(key); } catch(e) {}
                            });
                            return __exports;
                        } catch (err) {
                            console.error('【组件脚本执行报错】', err);
                            return {};
                        }
                    `;

                    const baseOrigin = window.location.origin;
                    const html = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${baseOrigin}/js/element-plus/index.css">
    <script src="${baseOrigin}/js/axios.min.js"><\/script>
    <script src="${baseOrigin}/js/vue3/vue.global.prod.js"><\/script>
    <script src="${baseOrigin}/js/element-plus/index.full.min.js"><\/script>
    <script src="${baseOrigin}/js/vue3/index.iife.min.js"><\/script>
    <script src="${baseOrigin}/js/request.js"><\/script>
    <style>body{margin:0;padding:0;} ${css}</style>
</head>
<body>
    <div id="app"></div>
    <script>
        const { createApp, reactive, ref, computed, watch, onMounted, nextTick } = Vue;
        
        const Main = {
            template: \`${tpl.replace(/`/g, '\\`').replace(/\${/g, '\\${')}\`,
            setup() { 
                const option = reactive(${JSON.stringify(businessOption || {})});
                
                // 运行动态脚本并获取所有暴露的变量
                const runScript = () => {
                    const scriptFunc = new Function(
                        'Vue', 'ref', 'reactive', 'computed', 'watch', 'onMounted', 'nextTick', 
                        'props', 'option', 'ElementPlus', 
                        \`${cleanedJs}\`
                    );
                    // 传入 option 作为 props 模拟
                    return scriptFunc(Vue, ref, reactive, computed, watch, onMounted, nextTick, { option }, option, ElementPlus);
                };

                const userSetup = runScript() || {};

                // 自动化绑定检查日志
                onMounted(() => {
                    nextTick(() => {
                        console.log("【预览成功】提取变量列表:", ${JSON.stringify(bindings)});
                        // 检查所有 Ref 是否成功绑定到 DOM/组件
                        Object.keys(userSetup).forEach(key => {
                            if (Vue.isRef(userSetup[key])) {
                                const isBound = userSetup[key].value !== null;
                                console.log(\`- 变量 [\${key}]: \`, isBound ? "✅ 已绑定实例" : "❌ 仍为null(请检查模板ref是否对应)");
                            }
                        });
                    });
                });

                // 将所有用户定义的变量平铺返回，Vue 会根据键名自动处理模板中的 ref 绑定
                return { ...userSetup, option }; 
            }
        };
        
        const app = createApp(Main);
        if (window.ElementPlusIconsVue) {
           for (const [k, c] of Object.entries(ElementPlusIconsVue)) { app.component(k, c); }
        }
        app.use(ElementPlus).mount('#app');
    <\/script>
</body>
</html>`;

                    if (this.blobUrl) URL.revokeObjectURL(this.blobUrl);
                    const blob = new Blob([html], { type: 'text/html' });
                    this.blobUrl = URL.createObjectURL(blob);
                    this.iframeVisible = true;
                } catch (e) {
                    console.error(e);
                    this.error = true;
                    this.errorMsg = e.message;
                    this.loading = false;
                }
            }
        },
        watch: { id: { handler: 'fetchAndRender', immediate: true } },
        template: `
            <div v-loading="loading" style="width:100%; height:100%; min-height:400px; position:relative;">
                <div v-if="error" style="color:red; padding:20px;">预览加载失败: {{ errorMsg }}</div>
                <iframe v-else-if="iframeVisible" :src="blobUrl" @load="loading=false" style="width:100%; height:100%; border:none; display:block;"></iframe>
            </div>`
    });
};