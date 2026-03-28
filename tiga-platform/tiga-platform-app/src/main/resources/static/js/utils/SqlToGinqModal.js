/**
 * Tiga Platform - SQL转GINQ专用弹窗工具
 */
const SqlToGinqModal = {
    show(callback) {
        // 生成完全唯一的 ID，绝对不与主页面 monaco-editor 冲突
        const uid = Math.random().toString(36).substring(2, 10);
        const sqlId = `sql_id_${uid}`;
        const ginqId = `ginq_id_${uid}`;

        const container = document.createElement('div');
        document.body.appendChild(container);

        const app = Vue.createApp({
            setup() {
                const visible = Vue.ref(true);
                const loading = Vue.ref(false);
                let sqlEditorInstance = null;
                let ginqEditorInstance = null;

                // 局部初始化函数，配置完全私有化，不影响全局
                const createPrivateEditor = (domId, lang) => {
                    const el = document.getElementById(domId);
                    if (!el) return null;
                    return monaco.editor.create(el, {
                        value: '',
                        language: lang,
                        minimap: { enabled: false },
                        // 关键：不污染全局样式
                        fixedOverflowWidgets: true 
                    });
                };

                Vue.onMounted(() => {
                    // 必须延迟，等待 el-dialog 动画彻底结束，否则 DOM 尺寸计算会错位
                    setTimeout(() => {
                        sqlEditorInstance = createPrivateEditor(sqlId, 'sql');
                        ginqEditorInstance = createPrivateEditor(ginqId, 'groovy');
                        sqlEditorInstance.setValue('-- 请在此输入 SQL 语句');
                    }, 300);
                });

                const handleConvert = async () => {
                    const sql = sqlEditorInstance.getValue();
                    if (!sql || sql.length < 5) return ElementPlus.ElMessage.warning('请输入有效的 SQL');
                    
                    loading.value = true;
                    try {
                        const res = await fetch(`/tiga/sql2Ginq/converter?sql=${encodeURIComponent(sql)}`);
                        const text = await res.text();
                        if (ginqEditorInstance) ginqEditorInstance.setValue(text);
                        ElementPlus.ElMessage.success('转换完成');
                    } catch (e) {
                        ElementPlus.ElMessage.error('转换失败');
                    } finally {
                        loading.value = false;
                    }
                };

                const handleConfirm = () => {
                    if (callback && ginqEditorInstance) callback(ginqEditorInstance.getValue());
                    visible.value = false;
                };

                const cleanup = () => {
                    // 彻底释放实例，否则主页面会卡顿
                    if (sqlEditorInstance) sqlEditorInstance.dispose();
                    if (ginqEditorInstance) ginqEditorInstance.dispose();
                    app.unmount();
                    if (container.parentNode) container.parentNode.removeChild(container);
                };

                return { visible, loading, handleConvert, handleConfirm, cleanup, sqlId, ginqId };
            },
            template: `
                <el-dialog 
                    v-model="visible" 
                    title="SQL 转 GINQ 工具" 
                    width="800px" 
                    @closed="cleanup"
                    :append-to-body="true"
                    :destroy-on-close="true"
                    custom-class="tiga-sql-modal"
                >
                    <div v-loading="loading">
                        <p style="margin: 0 0 8px 0; font-size: 13px; color: #909399;"><el-icon style="margin-right: 5px;"><edit /></el-icon> MySQL 输入:</p>
                        <div :id="sqlId" style="height: 180px; border: 1px solid #dcdfe6; border-radius: 4px;"></div>
                        
                        <div style="padding: 15px 0; text-align: center;">
                            <el-button type="primary" @click="handleConvert">
                                <el-icon style="margin-right: 4px;"><magic-stick /></el-icon> 转换
                            </el-button>
                        </div>

                        <p style="margin: 0 0 8px 0; font-size: 13px; color: #909399;"><el-icon style="margin-right: 5px;"><finished /></el-icon> GINQ 输出:</p>
                        <div :id="ginqId" style="height: 180px; border: 1px solid #dcdfe6; border-radius: 4px;"></div>
                    </div>
                    <template #footer>
                        <el-button @click="visible = false">取消</el-button>
                        <el-button type="success" @click="handleConfirm">应用到设计器</el-button>
                    </template>
                </el-dialog>
            `
        });

        app.use(ElementPlus);
        // 注册图标
        if (window.ElementPlusIconsVue) {
            for (const [key, component] of Object.entries(window.ElementPlusIconsVue)) {
                app.component(key, component);
            }
        }
        app.mount(container);
    }
};