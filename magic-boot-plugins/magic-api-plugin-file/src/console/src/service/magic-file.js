/**
 * 文件存储服务
 */
export default (request, $i, modal, JavaClass) => {
    let findResources;

    // 设置代码提示
    JavaClass.setExtensionAttribute('org.ssssssss.magicapi.file.FileModule', () => {
        return findResources && (findResources('file')[0]?.children || []).filter(it => it.key).map(it => {
            return {
                name: it.key,
                type: 'org.ssssssss.magicapi.file.FileModule',
                comment: it.name
            }
        }) || []
    });

    return {
        injectResources: fn => findResources = fn,
        requireScript: false,
        doTest: (info, callback) => {
            request.sendJson('/file/storage/test', info).success(res => {
                if (res.success) {
                    modal.alert($i('file.connected'), $i('file.test'));
                } else {
                    modal.alert($i('file.connectFailed', res.message), $i('file.test'));
                }
                callback && callback(res);
            }).error(res => {
                modal.alert($i('file.connectFailed', res.message || 'Unknown error'), $i('file.test'));
                callback && callback(res);
            });
        },
        getStorageTypes: () => {
            return request.get('/file/storage/types');
        }
    }
}
