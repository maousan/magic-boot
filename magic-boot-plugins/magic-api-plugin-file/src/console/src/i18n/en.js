export default {
    file: {
        title: 'File Storage',
        name: 'File Storage Config',
        copySuccess: 'Copy {0} Success',
        copyFailed: 'Failed to Copy {0}',
        test: 'Test Connection',
        connected: 'Connected',
        connectFailed: 'Connection Failed, Reason:\r\n{0}',
        type: 'Type',
        form: {
            placeholder: {
                name: 'Storage name, e.g., Local Storage, MinIO Storage',
                key: 'Storage key, used in code, e.g., local, minio',
                type: 'Storage type: local, s3, minio'
            },
            type: 'Type',
            basePath: 'Base Path',
            domain: 'Domain',
            endpoint: 'Endpoint URL',
            bucket: 'Bucket Name',
            accessKey: 'Access Key',
            secretKey: 'Secret Key',
            region: 'Region',
            isDefault: 'Set as Default',
            enabled: 'Enabled'
        },
        storageTypes: {
            local: 'Local Storage',
            s3: 'Amazon S3',
            minio: 'MinIO'
        },
        testResult: {
            success: 'Connection test successful',
            failed: 'Connection test failed',
            writeTime: 'Write Time',
            readTime: 'Read Time'
        }
    }
}
