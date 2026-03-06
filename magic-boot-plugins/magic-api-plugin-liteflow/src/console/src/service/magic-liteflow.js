export default function (bus, constants, $i, Message, request) {
    return {
        // svg text
        getIcon: item => ['FLOW', '#1890ff'],
        // 任务名称
        name: $i('liteflow.flow.name'),
        // 脚本语言 - LiteFlow EL表达式
        language: 'magicscript',
        // 默认脚本
        defaultScript: `// LiteFlow EL 规则表达式
// 示例: THEN(THEN(a, b), WHEN(c, d))
THEN(
    component1,
    WHEN(component2, component3),
    component4
)`,
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
            requestConfig.headers["Magic-Data-Type"] = "chain"
            requestConfig.headers[constants.HEADER_REQUEST_CLIENT_ID] = constants.CLIENT_ID
            requestConfig.headers[constants.HEADER_REQUEST_SCRIPT_ID] = opened.item.id
            requestConfig.headers[constants.HEADER_MAGIC_TOKEN] = constants.HEADER_MAGIC_TOKEN_VALUE
            const fullName = opened.path()
            bus.status(`开始测试EL规则「${fullName}」`)
            request.sendPost('/liteflow/execute', {
                id: info.id,
                chainName: info.chainName || info.name,
                script: info.script 
            }, requestConfig).success(res => {
                opened.running = false
            }).end(() => {
                bus.status(`EL规则「${fullName}」测试完毕`)
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
