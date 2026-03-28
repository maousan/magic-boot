package com.ocean.tigaapi.api.controller;

import java.util.List;

import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Result;

import com.ocean.tigaapi.api.entity.ApiEntity;
import com.ocean.tigaapi.api.router.ApiRouter;
import com.ocean.tigaapi.api.service.ApiService;

@Controller
@Mapping("/tiga/api")
public class ApiController {
    
    @Inject
    private ApiRouter routerService;
    
    @Inject
    private ApiService apiService;
    
    @Get
    @Mapping("tree")
    public Result<List<ApiEntity>> getApiTree() throws Exception {
        return Result.succeed(apiService.getTreeOfAllActiveApis(), 200);
    }
    
    @Post
    @Mapping("delete")
    public Result<Boolean> deleteApi(String ids) throws Exception {
    	return Result.succeed(apiService.deleteApi(ids), 200);
    }
    
    /**
     * 保存并即时生效
     */
    @Post
    @Mapping("/save")
    public Result<Object> save(@Body ApiEntity api) {
        try {
            // 1. 持久化到主库 (t_api 表)
        	Object id = apiService.saveApi(api);

            // 2. 刷新内存路由表与脚本编译缓存
            // 如果接口是启用的，直接注册或覆盖；如果禁用了，则移除
        	if(api.getRequest_path() != null && api.getRequest_path().equals("")) {
        		if (api.getStatus() == 1) {
                    routerService.register(api);
                } else {
                    routerService.unregister(api.getId()+"", api.getRequest_path(), false);
                }
        	}

            return Result.succeed(id, 200);
        } catch (Exception e) {
        	e.printStackTrace();
            return Result.failure(500, "API发布失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存并即时生效
     */
    @Post
    @Mapping("/delete")
    public Result<String> delete(@Body ApiEntity api) {
    	try {
    		apiService.deleteApi(api.getPath());
    		routerService.unregister(api.getId()+"", api.getPath(), false);
    		return Result.succeed("API删除成功");
    	} catch (Exception e) {
    		return Result.failure("API删除失败：" + e.getMessage());
    	}
    }

//    /**
//     * 调试接口 (不保存，仅预览运行结果)
//     */
//    @Post
//    @Mapping("/debug")
//    public Object debug(@Body ApiEntity api, Context ctx) throws Exception {
//        // 直接调用脚本引擎执行，不经过路由表
//        Map<String, Object> params = new HashMap<>();
//        ctx.paramMap().forEach(kv -> params.put(kv.getKey(), ctx.param(kv.getKey())));
//        String id = "debug_" + System.currentTimeMillis();
//        // 传递一个临时的 ID 以避免覆盖正式缓存
//        Object result = scriptEngine.execute(
//        	id, 
//            api.getScript(), 
//            params, 
//            api.getTimeoutms()
//        );
//        scriptEngine.removeCache(id);
//        return result;
//        
//    }
}