package org.ssssssss.magicboot.plugin.api.datasource;

import org.pf4j.ExtensionPoint;
import javax.sql.DataSource;
import java.util.List;

/**
 * 数据源扩展点
 * 允许插件注册额外的数据源
 */
public interface DataSourceExtension extends ExtensionPoint {

    /**
     * 获取数据源定义列表
     */
    List<DataSourceDefinition> getDataSources();

    /**
     * 数据源创建回调
     * @param definition 数据源定义
     * @param dataSource 创建的数据源
     */
    default void onDataSourceCreated(DataSourceDefinition definition, DataSource dataSource) {
    }

    /**
     * 数据源销毁回调
     * @param definition 数据源定义
     * @param dataSource 销毁的数据源
     */
    default void onDataSourceDestroyed(DataSourceDefinition definition, DataSource dataSource) {
    }
}
