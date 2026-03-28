package com.ocean.tigaapi.db.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import com.ocean.tiga.engine.api.EngineManager;
import com.ocean.tigaapi.db.DbModule;
import com.ocean.tigaapi.db.entity.DataSourceEntity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Component
public class DynamicDatasourceService {
	@Inject("main")
	DbModule db;

	@Inject
	private EngineManager engineManager;
	
	// 缓存数据源上下文，key 为数据源名称（如 ds1）
    private Map<String, DbModule> dbContextMap = new ConcurrentHashMap<>();
    
    // 缓存原始连接池，用于关闭资源
    private final Map<String, HikariDataSource> dataSourceMap = new ConcurrentHashMap<>();

    /**
     * 动态注册或刷新数据源
     */
    public synchronized void registerDs(DataSourceEntity entity) {
        // 1. 如果已存在同名数据源，先关闭旧连接池，防止内存/连接泄露
        closeDs(entity.getName());

        // 2. 创建新连接池
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(entity.getUrl());
        config.setUsername(entity.getUsername());
        config.setPassword(entity.getPassword());
        config.setDriverClassName(entity.getDriver());
        
        // 关键：设置连接池名称，方便监控
        config.setPoolName("HikariPool-" + entity.getName());
        config.setMaximumPoolSize(entity.getMaxPoolSize()); 
        config.setMinimumIdle(entity.getMinIdle());
        config.setConnectionTimeout(entity.getConnectionTimeout());
        config.setIdleTimeout(entity.getIdleTimeout());
        HikariDataSource ds = new HikariDataSource(config);
        
        DbModule dbModule = new DbModule(ds);
        
        dataSourceMap.put(entity.getName(), ds);
        dbContextMap.put(entity.getName(), dbModule);

        engineManager.registerGlobalModule("db", getAll());

        System.out.println("数据源 [" + entity.getName() + "] 已成功加载/刷新");
    }

    /**
     * 删除数据源
     */
    public synchronized void removeDs(String name) {
        closeDs(name);
        dbContextMap.remove(name);
        dataSourceMap.remove(name);
        engineManager.registerGlobalModule("db", getAll());
    }
    
    /**
     * 获取所有外部数据源配置
     */
    public List<DataSourceEntity> getAllExternalDs() throws Exception {
        return db.table("tiga_datasource")
                   .selectList("*", DataSourceEntity.class);
    }

    /**
     * 根据名称查询单个数据源
     */
    public DataSourceEntity getDsByName(String name) throws Exception {
        return db.table("tiga_datasource")
                   .where("name = ?", name)
                   .selectItem("*", DataSourceEntity.class);
    }
    
    private void closeDs(String name) {
        HikariDataSource oldDs = dataSourceMap.get(name);
        if (oldDs != null && !oldDs.isClosed()) {
            oldDs.close();
            System.out.println("旧数据源 [" + name + "] 已关闭释放资源");
        }
    }

    public DbModule getDb(String name) {
        return dbContextMap.get(name);
    }
    
    public Map<String,DbModule> getAll() {
    	dbContextMap.put("_", db);
        return dbContextMap;
    }
}