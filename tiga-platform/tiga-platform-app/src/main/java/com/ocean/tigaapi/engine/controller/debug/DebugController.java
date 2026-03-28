package com.ocean.tigaapi.engine.controller.debug;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import com.ocean.tigaapi.engine.groovy.GroovyDebugManager;
import com.ocean.tigaapi.engine.groovy.GroovyEngine;
import com.ocean.tigaapi.engine.magic.MagicDebugManager;
import com.ocean.tigaapi.engine.magic.MagicEngine;

@Controller
@Mapping("/debug")
public class DebugController {

    @Inject private MagicEngine magicScriptEngine;
    @Inject private GroovyEngine groovyScriptEngine;
    
    private static final ExecutorService debugLauncher = new ThreadPoolExecutor(
            10, 30, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            r -> new Thread(r, "tiga-debug-launcher-" + r.hashCode())
    );

    @Post
    @Mapping("/run")
    public Result<String> run(Context ctx) throws Throwable {
        ONode node = ONode.load(ctx.body());
        String sid = node.get("sid").getString();
        String code = node.get("code").getString();
        String type = node.get("type").getString();
        
        if (Utils.isEmpty(sid) || Utils.isEmpty(code)) {
            return Result.failure("ID or Code cannot be empty");
        }

        MagicDebugManager.stop(sid);
        
        Set<Integer> bps = new HashSet<>(node.get("breakpoints").toObjectList(Integer.class));
        Map<String, Object> params = node.get("params").toObject(Map.class);

        CompletableFuture.runAsync(() -> {
            try {
                if ("magic".equals(type)) {
                    magicScriptEngine.executeDebug(sid, code, params, 600000L, bps);
                } else if ("groovy".equals(type)) {
                	groovyScriptEngine.executeDebug(sid, code, params, 600000L, bps);
                }
                
            } catch (Throwable e) {
            	e.printStackTrace();
                // 处理中断或终止错误
                if (e.getMessage() != null && (e.getMessage().contains("DEBUG_") || e instanceof InternalError)) {
                    DebugWebSocket.push(sid, "FINISHED", "Debug session ended");
                } else {
                    DebugWebSocket.push(sid, "ERROR", e.getMessage());
                }
            }
        }, debugLauncher);

        return Result.succeed("Debug session started");
    }

    @Mapping("/resume")
    public Result<Void> resume(String sid) {
        MagicDebugManager.cmdResume(sid);
        GroovyDebugManager.cmdResume(sid);
        return Result.succeed();
    }

    @Mapping("/step")
    public Result<Void> step(String sid) {
        MagicDebugManager.cmdStepOver(sid); 
        GroovyDebugManager.cmdStepOver(sid);
        return Result.succeed();
    }

    @Mapping("/stop")
    public Result<Void> stop(String sid) {
        MagicDebugManager.stop(sid);
        GroovyDebugManager.stop(sid);
        return Result.succeed();
    }
}