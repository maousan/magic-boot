export default {
    file: {
        title: '文件存储',
        name: '文件存储配置',
        copySuccess: '复制{0}成功',
        copyFailed: '复制{0}失败',
        test: '测试连接',
        connected: '连接成功',
        connectFailed: '连接失败，错误原因：\r\n{0}',
        type: '类型',
        form: {
            placeholder: {
                name: '存储名称，如：本地存储、MinIO存储',
                key: '存储标识，后续代码中使用，如：local、minio',
                type: '存储类型：local(本地)、s3(Amazon S3)、minio(MinIO)'
            },
            type: '类型',
            basePath: '存储路径',
            domain: '访问域名',
            endpoint: 'Endpoint URL',
            bucket: 'Bucket 名称',
            accessKey: 'Access Key',
            secretKey: 'Secret Key',
            region: '区域',
            isDefault: '设为默认',
            enabled: '是否启用'
        },
        storageTypes: {
            local: '本地存储',
            s3: 'Amazon S3',
            minio: 'MinIO'
        },
        testResult: {
            success: '连接测试成功',
            failed: '连接测试失败',
            writeTime: '写入耗时',
            readTime: '读取耗时'
        }
    }
}
