export default {
    liteflow: {
        flow: {
            title: 'EL Rule Info',
            name: 'EL Rule',
            form: {
                name: 'Rule Name',
                path: 'Rule Path',
                chainId: 'Chain Id',
                placeholder: {
                    name: 'Please Enter Rule Name',
                    path: 'Please Enter Rule Path',
                    chainId: 'Please Enter Chain Id',
                    description: 'Please Enter Rule Description'
                }
            }
        },
        component: {
            title: 'Component Info',
            name: 'Component Script',
            form: {
                name: 'Component Name',
                path: 'Component Path',
                nodeId: 'Node ID',
                placeholder: {
                    name: 'Please Enter Component Name',
                    path: 'Please Enter Component Path',
                    nodeId: 'Please Enter Node ID',
                    description: 'Please Enter Component Description'
                }
            }
        },
        common: {
            execute: 'Execute',
            refresh: 'Refresh',
            save: 'Save'
        }
    }
}
