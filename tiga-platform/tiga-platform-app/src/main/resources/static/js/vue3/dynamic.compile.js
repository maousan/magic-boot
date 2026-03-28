const DynamicCompile = {
	 	// 核心：模拟预编译逻辑
        // 在生产中，我们会提取 SFC 的三个部分，减少运行时解析负担
        parseSFC : (source) => {
            const templateMatch = source.match(/<template>([\s\S]*)<\/template>/);
            const scriptMatch = source.match(/<script setup>([\s\S]*)<\/script>/);
            const styleMatch = source.match(/<style scoped>([\s\S]*)<\/style>/);

            return {
                template: templateMatch ? templateMatch[1].trim() : '',
                script: scriptMatch ? scriptMatch[1].trim() : 'export default {}',
                css: styleMatch ? styleMatch[1].trim() : ''
            };
        },

        handleCompileAndSave : (htmlSource, businessCode) => {
            const blocks = DynamicCompile.parseSFC(htmlSource);
            
            // 构造生产级入库载体
           return {
                id: "COMP_" + Date.now(),
                'raw_sfc': htmlSource,
                // 这里我们存储 Base64 后的部分，防止特殊字符导致 SQL 注入或 JSON 解析异常
                'compiled_template': btoa(unescape(encodeURIComponent(blocks.template))),
                'compiled_js': btoa(unescape(encodeURIComponent(blocks.script))),
                'compiled_css': btoa(unescape(encodeURIComponent(blocks.css))),
                'script': businessCode
            };
        }
};