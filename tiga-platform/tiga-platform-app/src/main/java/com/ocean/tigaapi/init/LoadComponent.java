package com.ocean.tigaapi.init;

import java.util.List;
import java.util.Map;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.bean.LifecycleBean;

import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tigaapi.api.entity.ApiEntity;
import com.ocean.tigaapi.api.router.ApiRouter;
import com.ocean.tigaapi.api.service.ApiService;
import com.ocean.tigaapi.component.service.ComponentService;
import com.ocean.tigaapi.db.entity.DataSourceEntity;
import com.ocean.tigaapi.db.service.DynamicDatasourceService;

@Component
public class LoadComponent implements LifecycleBean {
	
	@Inject
    private DynamicDatasourceService dynamicDatasourceService;
	@Inject
	private ApiService apiRepository;
	
	@Inject
	private DynamicDatasourceService dsManager;         // 动态数据源管理器
    
    @Inject
    private ApiRouter routerService; // 路由分发器

    @Inject
    private EngineManager engineManager;			// 脚本引擎

    @Override
    public void start() throws Throwable {
    	System.out.println(">>> [Tiga Platform] 正在初始化低代码引擎...");
    	
    	// 1. 注册RDBMS插件到脚本引擎
        // 将 dsManager 内部的 Map 引用传给引擎，后续动态增删数据源脚本内能直接感知
    	// 这样脚本里就能用 db.ds1 了
    	engineManager.registerGlobalModule("db", dsManager.getAll());
        
        // 2. 从主库加载所有【数据源】配置
        initDynamicDataSources();
        
        // 3. 从主库加载所有【启用的 API】配置
        //initDynamicRoutes();
        
        /**
         * 4.初始化所有业务组件
         */
        initBusinessComponent();

        System.out.println(">>> [Tiga Platform] 引擎启动完成，动态接口已就绪。");
    }
    
    /**
     * 初始化外部动态数据源
     */
    private void initDynamicDataSources() throws Exception {
        List<DataSourceEntity> dsList = dynamicDatasourceService.getAllExternalDs();
        System.out.println("找到外部数据源配置数: " + dsList.size());
        
        for (DataSourceEntity entity : dsList) {
            try {
                dsManager.registerDs(entity);
            } catch (Exception e) {
                System.err.println("数据源 [" + entity.getName() + "] 加载失败: " + e.getMessage());
            }
        }
    }

    /**
     * 初始化动态路由
     */
    private void initDynamicRoutes() throws Exception {
        List<ApiEntity> apiList = apiRepository.getAllActiveApis();
        System.out.println("找到启用的动态接口数: " + apiList.size());
        
        for (ApiEntity api : apiList) {
            routerService.register(api);
        }
    }
    
    @Inject
    private ComponentService componentService;
    
    /**
     * 初始化所有业务组件
     */
    private void initBusinessComponent() throws Exception {
    	List<Map<String, Object>> componentList = componentService.getBusinessComponentList();
    	System.out.println("初始化所有业务组件接口数: " + componentList.size());
    	
    	for (Map<String, Object> component : componentList) {
    		Object businessCode = component.get("script");
    		if(businessCode != null) {
    			componentService.register(component.get("id").toString(), businessCode.toString());
    		}
    		
    	}
    }
}