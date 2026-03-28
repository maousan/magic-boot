package com.ocean.tigaapi.component.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tigaapi.api.entity.ApiEntity;
import com.ocean.tigaapi.component.service.ComponentService;

@Controller
@Mapping("/tiga/component")
public class ComponentController {

    @Inject
    private ComponentService componentService;

    @Inject
    private EngineManager engineManager;			// 脚本引擎
    
    @Get
    @Mapping("list")
    public Result<List<Map<String, Object>>> list(String pid, String ctype, String keyword) throws Exception {
        return Result.succeed(componentService.getList(pid, ctype, keyword));
    }
    
    @Post
    @Mapping("create")
    public Result<?> create(@Body Map<String, Object> params) {
    	try {
            String name = (String) params.get("name");
            if (name == null || name.trim().isEmpty()) {
                return Result.failure("名称不能为空");
            }
            
            boolean success = componentService.createComponent(params);
            
            return success ? Result.succeed() : Result.failure(500, "创建失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("服务器异常: " + e.getMessage());
        }
    }
    
    /**
     *  更新组件信息
     */
    @Post
    @Mapping("update")
    public Result<?> update(@Body Map<String, Object> params) {
    	try {
    		boolean success = componentService.updateComponent(params);
    		if(success) {
    			componentService.unRegister(params.get("id").toString());
    			engineManager.refreshCache(params.get("id").toString());
    			return Result.succeed();
    		}
    		return Result.failure(500, " 更新组件信息失败");
    	} catch (Exception e) {
    		e.printStackTrace();
    		return Result.failure("服务器异常: " + e.getMessage());
    	}
    }

    /**
     * 重命名接口
     */
    @Post
    @Mapping("rename")
    public Result<?> rename(String id, String name) throws Exception {
        if (id == null || name == null || name.trim().isEmpty()) {
            return Result.failure("名称不能为空");
        }
        boolean success = componentService.rename(id, name.trim());
        return success ? Result.succeed() : Result.failure(500,"修改失败");
    }
    /**
     * 删除接口
     */
    @Post
    @Mapping("delete")
    public Result<?> delete(String id) throws Exception {
    	if (id == null) {
    		return Result.failure("编号不能为空");
    	}
    	boolean success = componentService.delete(id);
    	if(success) {
    		componentService.unRegister(id);
    		engineManager.refreshCache(id);
    		return Result.succeed();
    	}
    	return Result.failure(500,"删除失败");
    }
    
    /**
     * 根据id获取组件详情信息
     * @throws Exception
     */
    @Get
    @Mapping("getById")
    public Result<Map<String, Object>> getById(String id) throws Exception {
    	if (id == null) {
    		return Result.failure("编号不能为空");
    	}
    	return Result.succeed(componentService.getById(id));
    }
    
    /**
     * 获取组件页面编译信息，根据组件编号
     * @throws Exception
     */
    @Get
    @Mapping("getCompiledInfoById")
    public Result<Map<String, Object>> getCompiledInfoById(String id) throws Exception {
    	if (id == null) {
    		return Result.failure("编号不能为空");
    	}
    	return Result.succeed(componentService.getCompiledInfoById(id));
    }
    
    /**
     * 组件逻辑执行
     */
    @Post
    @Mapping("/logic")
    public Result<Object> logic(@Body ApiEntity api, Context ctx) throws Exception {
    	Object script = componentService.getScriptById(api.getId());
    	if(script == null || script.toString().equals("")) {
    		return Result.failure(404,"组件后台执行逻辑未定义");
    	}
    	
        // 直接调用脚本引擎执行，不经过路由表
        Map<String, Object> params = new HashMap<>();
        ctx.paramMap().forEach(kv -> params.put(kv.getKey(), ctx.param(kv.getKey())));
        
        Object result = engineManager.execute(api.getStype(), api.getId(), script.toString(), params,  api.getTimeoutms());
        return Result.succeed(result);
    }
}