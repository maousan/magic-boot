import LiteflowFlow from './service/magic-liteflow.js'
import LiteflowFlowComponent from './service/magic-liteflow-component.js'
import localZhCN from './i18n/zh-cn.js'
import localEn from './i18n/en.js'
import MagicLiteflowInfo from './components/magic-liteflow-info.vue'
import MagicLiteflowComponent from './components/magic-liteflow-component.vue'
import MagicLiteflowResult from './components/magic-liteflow-result.vue'
import 'vite-plugin-svg-icons/register'

export default (opt) => {
    const i18n = opt.i18n
    // 添加i18n 国际化信息
    i18n.add('zh-cn', localZhCN)
    i18n.add('en', localEn)
    return {
        // 左侧资源 - EL规则定义
        resource: [{
            // 资源类型，和后端存储结构一致
            type: 'liteflow-chain',
            // 展示图标
            icon: '#magic-liteflow',   // #开头表示图标在插件中
            // 展示名称
            title: 'liteflow.flow.title',
            // 运行服务
            service: LiteflowFlow(opt.bus, opt.constants, i18n.format, opt.Message, opt.request),
        }, {
            // 资源类型，和后端存储结构一致
            type: 'liteflow-component',
            // 展示图标
            icon: '#magic-liteflow',   // #开头表示图标在插件中
            // 展示名称
            title: 'liteflow.component.title',
            // 运行服务
            service: LiteflowFlowComponent(opt.bus, opt.constants, i18n.format, opt.Message, opt.request),
        }],
        // 底部工具条
        toolbars: [{
            // 当打开的资源类型为 liteflow 时显示
            type: 'liteflow-chain',
            // 工具条展示的标题
            title: '运行结果',
            // 展示图标
            icon: 'run',
            // 对应的组件
            component: MagicLiteflowResult,
        }, {
            // 当打开的资源类型为 liteflow 时显示
            type: 'liteflow-chain',
            // 工具条展示的标题
            title: 'liteflow.flow.title',
            // 展示图标
            icon: 'parameter',
            // 对应的组件
            component: MagicLiteflowInfo,
        },  {
            type: 'liteflow-component',
            title: 'liteflow.component.title',
            icon: 'parameter',
            component: MagicLiteflowComponent,
        }]
    }
}
