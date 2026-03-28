const Request = {
		 baseOrigin: window.location.origin,
	    /**
		 * componentPayload: {
		 * 		id:'' //组件编号
		 * 		timeoutms: 5000 // 5秒超时
		 * }
		 * 
		 * params: {} //调用逻辑需要的参数
		 */
        async logic(componentPayload, params) {
            try {
	            const res = await axios.post(Request.baseOrigin+ '/tiga/component/logic', componentPayload ,{params: params});
	            if (res.data.code === 200) {
	            	return res.data.data;
	            }
	        } catch (e) { 
	        	console.error(e);
	            ElementPlus.ElMessage.error('组件逻辑执行失败');
	            return null;
	        }
        }

};