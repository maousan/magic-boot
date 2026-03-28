package com.ocean.tigaapi.engine.controller.hint;

import java.util.Map;
import java.util.Set;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;

import com.ocean.tiga.engine.api.EngineManager;

/**
 * 获取插件资源信息，用于前端设计器，提示。
 */
@Controller
@Mapping("/tiga/plugin/resource")
public class PlugInResourceCollectorController {

	@Inject
    private EngineManager engineManager;

	/**
	 * 获取插件名称及插件实例的名称
	 * @param modelNames
	 * 		插件名称集合，数据格式：db,redis,kafka,mqtt,hbase,es,tcp,...
	 * @return
	 * 		{
	 * 			dh : [_,yjy,...],
	 * 			redis : [_,yjy,...],
	 * 			...
	 *		}
	 */
    @Get
    @Mapping("model")
    public Result<Map<String, Set<String>>> getModuleKeys(String modelNames) {
        return Result.succeed(engineManager.getModuleKeys(modelNames));
    }
}