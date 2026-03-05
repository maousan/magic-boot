import MagicJob from './service/magic-job.js'
import localZhCN from './i18n/zh-cn.js'
import localEn from './i18n/en.js'
import MagicJobInfo from './components/magic-job-info.vue'
import 'vite-plugin-svg-icons/register'

import EnhancedJobInfo from './components/enhanced-job-info.vue'

export default (opt) => {
    const i18n = opt.i18n
    // 添加i18n 国际化信息
    i18n.add('zh-cn', localZhCN)
    i18n.add('en', localEn)
    return {
        // 左侧资源
        resource: [{
            // 资源类型，和后端存储结构一致
            type: 'job',
            // 展示图标
            icon: '#magic-job-job',   // #开头表示图标在插件中
            // 展示名称
            title: 'job.name',
            // 运行服务
            service: MagicJob(opt.bus, opt.constants, i18n.format, opt.Message, opt.request),
        }],
        // 底部工具条
        toolbars: [{
            // 当打开的资源类型为 job 时显示
            type: 'job',
            // 工具条展示的标题
            title: 'job.title',
            // 展示图标
            icon: 'parameter',
            // 对应的组件
            component: MagicJobInfo,
        }]
    }
}
