
/**
 * 全局通信总线
 */
window.$bus = {
    events: {},
    emit(event, data) { if (this.events[event]) this.events[event].forEach(cb => cb(data)); },
    on(event, callback) {
        if (!this.events[event]) this.events[event] = [];
        this.events[event].push(callback);
    },
    off(event, callback) { if (this.events[event]) this.events[event] = this.events[event].filter(cb => cb !== callback); }
};

const ComponentRenderer = {
	// CSS 作用域处理
    scopeCSS(css, scopeId) {
        if (!css) return '';
        return css.replace(/([^\r\n,{}]+)(?=[^{]*\{)/g, (match) => {
            return match.split(',').map(s => {
                const trimS = s.trim();
                if (!trimS || trimS.startsWith('@') || trimS === 'from' || trimS === 'to' || /^\d/.test(trimS)) return trimS;
                return `${trimS}[${scopeId}]`;
            }).join(', ');
        });
    },
	// 变量提取
    extractBindings(code) {
        const clean = code.replace(/\/\*[\s\S]*?\*\//g, '').replace(/([^\\:]|^)\/\/.*$/gm, '$1');
        const bindings = new Set();
        const stmtRegex = /(?:const|let|var)\s+([^;=]+)(?:=|[;]|$)/g;
        let match;
        while ((match = stmtRegex.exec(clean)) !== null) {
            match[1].split(',').forEach(n => {
                const name = n.trim();
                if (/^[a-zA-Z_$][\w$]*$/.test(name)) bindings.add(name);
            });
        }
        const funcRegex = /function\s+([a-zA-Z_$][\w$]*)/g;
        while ((match = funcRegex.exec(clean)) !== null) {
            bindings.add(match[1]);
        }
        return Array.from(bindings);
    },

  	/**
     * 构建动态组件
     * @param {string} id 组件ID
     * @param {object} externalData 外部传入的数据对象
     * @param {function} emit 外部传入的emit函数
     */
    async createDynamicComponent(id, externalData, emit) {
		if(!id){
			return ;
		}
		let rawData = {};
		if(!top.window['_components']){
			top.window['_components'] = {};
		}
		if(top.window['_components'][id]){
			rawData = top.window['_components'][id];
		}else{
			try {
	            const res = await axios.get('tiga/component/getById', {params: { id: id }});
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
        
        // 确保 externalData 是对象
	    let processedData = externalData;
	    if (typeof externalData === 'string') {
	        try {
	            processedData = JSON.parse(externalData);
	        } catch (e) {
	            processedData = {};
	        }
	    }

        const instanceId = Math.random().toString(36).slice(2, 10);
        const scopeId = `data-v-${instanceId}`;

        // 样式处理
        const styleTag = document.createElement('style');
        styleTag.id = `style-${instanceId}`; // 标记ID方便追踪
        styleTag.innerHTML = this.scopeCSS(css, scopeId);
        document.head.appendChild(styleTag);
        
        const scopedTpl = tpl.replace(/(<[a-zA-Z0-9\-]+)/, `$1 ${scopeId}`);
        js = js.replace(/import\s+[\s\S]*?from\s+['"].*?['"]/g, '').replace(/const\s+\w+\s*=\s*defineProps\([\s\S]*?\)/g, '');
        const bindings = this.extractBindings(js);

        return {
            name: `DynamicInst_${instanceId}`,
            template: scopedTpl,
            props: ['modelValue'],
            setup(props, context) {
                const { ref, reactive, watch, onUnmounted } = Vue;
                const option = reactive({ ...processedData });

                watch(() => externalData, (newVal) => {
                    Object.assign(option, newVal);
                }, { deep: true });

                const scriptFunc = new Function(
                    'Vue', 'ref', 'reactive', 'computed', 'watch', 'onMounted', 'onUnmounted', 'nextTick',
                    'option', 'ElementPlus', '$bus', 'emit',
                    `try { 
                        ${js} 
                        const __exports = {};
                        ${JSON.stringify(bindings)}.forEach(k => { try{ __exports[k] = eval(k); }catch(e){} });
                        return __exports;
                    } catch(e) { console.error('Dynamic Script Error:', e); return {}; }`
                );

                const userSetup = scriptFunc(
                    Vue, Vue.ref, Vue.reactive, Vue.computed, Vue.watch, Vue.onMounted, Vue.onUnmounted, Vue.nextTick,
                    option, ElementPlus, window.$bus, emit
                ) || {};

                // 彻底销毁：移除样式标签
                onUnmounted(() => {
                    const el = document.getElementById(`style-${instanceId}`);
                    if (el) el.remove();
                });

                return { ...userSetup, option };
            }
        };
    }
};

/**
 * 容器组件
 * 	   新增特点：支持 key 变化强制重绘，并对外暴露 init 方法
 * 使用方式：
 * 	  <dynamic-preview-component id="123" :data="componentNeedData" @submit-success="handle" />
 * 	  1、组件页面中的通过定义事件与主页面联动： emit('submit-success', data); 将事件传递给 @submit-success
 * 		 同时将事件数据 data 传递给主页面事件 handle(data) 中的data；
 * 	  2、组件页面中的通过定义：$bus.emit('__name__', val);将数据发送给
 * 		 	主页面的监听器：window.$bus.on('__name__', (val) => {console.log('主页面获取数据值：', val)});
 *	  3、上面的 @submit-success 和 __name__ 是自己随意定义的
 */
var DynamicPreviewComponent = (app) => {
    app.component('dynamic-preview-component', {
        // 声明 props
        props: ['id', 'data'], 
        data() {
            return { Comp: null, loading: false, error: null, innerKey: 0 };
        },
        watch: {
            // 监听外部数据引用变化 (最保险的刷新触发点)
            data: { 
                deep: false, // 只有当 data.value = {...} 整个替换时才触发
                handler() { 
                   // console.log('检测到数据引用变化，触发重绘');
                    this.init(); 
                } 
            }
        },
        methods: {
            async init() {
                this.Comp = null;
                await Vue.nextTick();
                this.loading = true;
                this.error = null;
                try {
                    // 注意：这里必须重新从 localStorage 加载最新编译的代码
                    const newComp = await ComponentRenderer.createDynamicComponent(this.id, this.data, this.$emit.bind(this));
                    this.Comp = Vue.markRaw(newComp);
                    this.innerKey++;
                } catch (e) {
                    this.error = e.message;
                } finally {
                    this.loading = false;
                }
            }
        },
        render() {
            if (this.error) return Vue.h('div', { style: 'color:red' }, this.error);
            if (!this.Comp) return Vue.h('div', '正在编译组件...');
            
            return Vue.h(this.Comp, {
                key: 'comp-' + this.innerKey, // 强制销毁旧 VNode
                modelValue: this.data,
                'onUpdate:modelValue': (val) => this.$emit('update:modelValue', val),
                ...this.$attrs 
            });
        }
    });
};