import MagicFile from './service/magic-file.js'
import localZhCN from './i18n/zh-cn.js'
import localEn from './i18n/en.js'
import MagicFileInfo from './components/magic-file-info.vue'
import 'vite-plugin-svg-icons/register'

export default (opt) => {
    const i18n = opt.i18n
    // 添加i18n 国际化信息
    i18n.add('zh-cn', localZhCN)
    i18n.add('en', localEn)

    return {
        datasources: [{
            // 资源类型，和后端存储结构一致
            type: 'file',
            // 展示图标
            icon: 'folder',
            // 展示标题
            title: 'File Storage',
            // 展示名称
            name: i18n.format('file.name'),
            // 运行服务 request, $i, modal, JavaClass
            service: MagicFile(opt.request, i18n.format, opt.modal, opt.JavaClass),
            // 表单组件
            component: MagicFileInfo
        }]
    }
}
