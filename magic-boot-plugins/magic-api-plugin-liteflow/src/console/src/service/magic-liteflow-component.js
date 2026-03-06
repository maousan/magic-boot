export default function (bus, constants, $i, Message, request) {
    return {
        // svg text
        getIcon: item => ['NODE', '#1890ff'],
        // 任务名称
        name: $i('liteflow.component.name'),
        // 脚本语言 - LiteFlow EL表达式
        language: 'magicscript',
        // 默认脚本
        defaultScript: `// LiteFlow Groovy 脚本`,
        // 执行测试的逻辑
        doTest: (opened) => {
            opened.running = true
            const info = opened.item
            const requestConfig = {
                baseURL: constants.SERVER_URL,
                url: '/liteflow/execute',
                method: 'POST',
                responseType: 'json',
                headers: {},
                withCredentials: true
            }
            bus.$emit(Message.SWITCH_TOOLBAR, 'log')
            requestConfig.headers["Magic-Data-Type"] = "component"
            requestConfig.headers[constants.HEADER_REQUEST_CLIENT_ID] = constants.CLIENT_ID
            requestConfig.headers[constants.HEADER_REQUEST_SCRIPT_ID] = opened.item.id
            requestConfig.headers[constants.HEADER_MAGIC_TOKEN] = constants.HEADER_MAGIC_TOKEN_VALUE
            const fullName = opened.path()
            bus.status(`开始测试EL规则「${fullName}」`)
            request.sendPost('/liteflow/execute', {
                id: info.id,
                nodeId: info.nodeId,
                script: info.script,
                scriptType: info.scriptType,
                nodeType: info.nodeType
            }, requestConfig).success(res => {
                opened.running = false
            }).end(() => {
                bus.status(`节点「${fullName}」测试完毕`)
                opened.running = false
            })
        },
        // 是否允许执行测试
        runnable: true,
        // 是否需要填写路径
        requirePath: true,
        // 合并
        merge: item => item
    }
}
