package com.ocean.tigaapi.api.router;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.handle.Context;

import com.ocean.tigaapi.api.entity.ApiEntity;

@Component
public class ApiRouter {
    
    // 路由查找表：Path -> ApiEntity
    // 增删改查只操作这个 Map，性能是 O(1)，且没有内存膨胀风险
    private final Map<String, ApiEntity> routeTable = new ConcurrentHashMap<>();

    @Init
    public void init() {
        // 注册一个通用的动态入口，所有的动态接口都走这个处理器
        // 假设动态接口都以 /api/ 开头，或者直接拦截全路径
    	// 只需要注册这一次！
    	Solon.app().router().all("/api/**", ctx -> {
        	dispatch(ctx);
        });
    }

    private void dispatch(Context ctx) throws Throwable {
    	// 获取当前访问路径
        String path = ctx.path();
        ApiEntity api = routeTable.get(path);
        
        if (api == null) {
            ctx.status(404);
            ctx.render("API Not Found");
            return;
        }

        // 校验 Method (GET/POST...)
        if (!"ALL".equalsIgnoreCase(api.getMethod()) && !ctx.method().equalsIgnoreCase(api.getMethod())) {
            ctx.status(405);
            ctx.render("API Method Not Found");
            return;
        }
        
        // 获取请求参数
        Map<String, Object> params = new HashMap<>();
        ctx.paramMap().forEach(kv -> params.put(kv.getKey(), ctx.param(kv.getKey())));

        try {
          
            ctx.render(null);
        } catch (Exception e) {
            ctx.status(500);
            ctx.render(Map.of("code", 500, "msg", e.getMessage()));
        }
    }

    public void register(ApiEntity api) {
        routeTable.put(api.getPath(), api);
        // 脚本有变动时，清除脚本引擎缓存以便重新编译
    }

    public void unregister(String id, String path, boolean status) {
        routeTable.remove(path);
        if(status) { //删除路由
        	 // 删除路由同时也需要，清除脚本引擎缓存
        }
    }
}