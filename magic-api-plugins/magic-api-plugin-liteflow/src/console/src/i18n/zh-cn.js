export default {
    liteflow: {
        flow: {
            title: 'Chain信息',
            name: 'Chain信息',
            form: {
                name: '规则名称',
                path: '规则路径',
                chainId: '规则ID',
                namespace: '命名空间',
                route: '规则路由',
                placeholder: {
                    name: '请输入规则名称',
                    path: '请输入规则路径',
                    chainId: '请输入规则ID',
                    namespace: '请输入规则命名空间',
                    route: '请输入规则路由',
                    description: '请输入规则描述'
                }
            }
        },
        component: {
            title: '组件信息',
            name: '组件信息',
            form: {
                name: '组件名称',
                path: '组件路径',
                nodeId: '节点ID',
                scriptType: '脚本类型',
                nodeType: '节点类型',
                placeholder: {
                    name: '请输入组件名称',
                    path: '请输入组件路径',
                    nodeId: '请输入节点ID',
                    nodeType: '请输入节点类型',
                    description: '请输入组件描述',
                    scriptType: '请选择脚本类型'
                }
            }
        },
        common: {
            execute: '执行',
            refresh: '刷新',
            save: '保存'
        }
    }
}
