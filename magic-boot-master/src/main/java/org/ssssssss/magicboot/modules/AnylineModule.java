package org.ssssssss.magicboot.modules;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.anyline.data.datasource.DataSourceHolder;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.data.runtime.DataRuntime;
import org.anyline.data.transaction.TransactionState;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.entity.PageNavi;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.anyline.util.ConfigTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.annotation.MagicModule;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;
import org.ssssssss.magicboot.utils.JdbcUrlBuilder;
import org.ssssssss.magicboot.utils.SnowflakeIdGenerator;
import org.ssssssss.script.annotation.Comment;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Component  //注入到Spring容器中
@MagicModule("anyline")    // 模块名称
public class AnylineModule implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(AnylineModule.class);

    @Autowired
    private AnylineService service;

    @Autowired
    private MagicDynamicDataSource dynamicDataSource;


    @Comment("获取实例")
    public AnylineService instance() {
        return service;
    }

    @Comment("返回数据源对应的实例")
    public AnylineService instance(@Comment("数据源名称") String dataSource) {
        return ServiceProxy.service(dataSource); //返回crm数据源对应的service
    }

    @Comment("注册数据源")
    public DataRuntime register(@Comment("数据源名称") String dataSource, @Comment("参数") Map<String, ?> params) throws Exception {
        return DataSourceHolder.reg(dataSource, params);
    }

    @Comment("获取数据库的所有表")
    public LinkedHashMap<String, Table> tableMap(@Comment("数据源名称") String dataSource) {
        if (null != dataSource && !dataSource.isEmpty()) {
            return instance(dataSource).metadata().tables();
        }
        return service.metadata().tables();
    }

    @Comment("获取数据库的所有表")
    public Collection<Table> tableList(@Comment("数据源名称") String dataSource) {
        Collection<Table> tables = null;
        if (null != dataSource && !dataSource.isEmpty()) {
            return instance(dataSource).metadata().tables().values();
        }
        return service.metadata().tables().values();
    }

    @Comment("获取表的列结构")
    public List<Map<String, Object>> columns(@Comment("数据源名称") String dataSource,
                                @Comment("表名称") String tableName) {
        List<Column> columns = null;
        if (null != dataSource && !dataSource.isEmpty()) {
            AnylineService anyline = instance(dataSource);
            if (anyline != null) {
                columns = Optional.ofNullable(anyline.metadata().table(tableName))
                        .map(table -> table.columns())
                        .orElse(new ArrayList<>());
            }
        }
        columns = service.metadata().table(tableName).columns();
        return columns.stream().map(column -> {
            Map<String, Object> columnMap = new HashMap<>();
            columnMap.put("name", column.getName());
            columnMap.put("className", column.getClassName());
            columnMap.put("jdbcType", column.getJdbcType());
            columnMap.put("originType", column.getOriginType());
            columnMap.put("defaultValue", column.getDefaultValue());
            columnMap.put("length", column.getLength());
            columnMap.put("position", column.getPosition());
            columnMap.put("primaryKey", column.getPrimaryKey());
            columnMap.put("nullable", column.getNullable());
            columnMap.put("comment", column.getComment());
            return columnMap;
        }).collect(Collectors.toList());
    }

    @Comment("测试连接")
    public boolean test(@Comment("数据源名称") String dataSource) throws Exception {
        if (instance(dataSource) != null) {
            return DataSourceHolder.hit(dataSource);
        }
        return false;
    }

    @Comment("测试连接")
    public boolean test(@Comment("数据源信息") DataRow dataRow) throws Exception {
        Map<String, Object> param = dataRow.camel().toMap();
        param.put("pool", dataRow.getString("poolType"));
        param.put("driver", dataRow.getString("driverClass"));
        String url = dataRow.getString("url");
        param.put("jdbcUrl", url);
        if (StringUtils.isEmpty(url)) {
            //根据db_type、host、port、database拼接jdbcUrl，使用模板
            url = JdbcUrlBuilder.build(param);
            param.put("jdbcUrl", url);
        }
        DataSource ds1 = DataSourceUtil.build(param);
        AnylineService anylineService = ServiceProxy.temporary(ds1);
        return anylineService.hit();
    }

    @Comment("插入数据")
    public DataRow insert(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                          @Comment("数据行") Object data) {
        return insert(dest, data, false);
    }

    @Comment("插入数据")
    public DataRow insert(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                          @Comment("数据行") Object data,
                          @Comment("是否生成ID") boolean isGenerateId) {
        DataRow dataRow = null;
        if (data instanceof Map) {
            dataRow = new DataRow();
            dataRow.putAll((Map<String, Object>) data);
            if (isGenerateId) {
                dataRow.put("id", SnowflakeIdGenerator.nextId());
            }
        }
        dataRow.camel_();
        return service.insert(dest, dataRow) > 0 ? dataRow : null;
    }

    @Comment("插入数据")
    public DataRow insert(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                          @Comment("数据行") Object data,
                          @Comment("字段") String... columns) {
        DataRow dataRow = null;
        if (data instanceof Map) {
            dataRow = new DataRow();
            dataRow.putAll((Map<String, Object>) data);
        }
        dataRow.camel_();
        return service.insert(dest, dataRow, columns) > 0 ? dataRow : null;
    }

    @Comment("插入数据")
    public DataRow insert(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                          @Comment("数据行") Object data,
                          @Comment("字段") List<String> columns) {
        DataRow dataRow = null;
        if (data instanceof Map) {
            dataRow = new DataRow();
            dataRow.putAll((Map<String, Object>) data);
        }
        dataRow.camel_();
        return service.insert(dest, data, columns) > 0 ? dataRow : null;
    }

    @Comment("更新数据")
    public long update(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                       @Comment("数据行") Object data) {
        DataRow dataRow = null;
        if (data instanceof Map) {
            dataRow = new DataRow();
            dataRow.putAll((Map<String, Object>) data);
        }
        dataRow.camel_();
        return service.update(dest, dataRow);
    }

    @Comment("更新数据")
    public long update(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                       @Comment("数据行") Object data,
                       @Comment("查询条件") String... conditions) {
        DataRow dataRow = null;
        if (data instanceof Map) {
            dataRow = new DataRow();
            dataRow.putAll((Map<String, Object>) data);
        }
        dataRow.camel_();
        return service.update(dest, dataRow, conditions);
    }

    @Comment("更新数据")
    public long update(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                       @Comment("数据行") Object data,
                       @Comment("更新条件") ConfigStore configs,
                       @Comment("字段") List<String> columns) {
        DataRow dataRow = null;
        if (data instanceof Map) {
            dataRow = new DataRow();
            dataRow.putAll((Map<String, Object>) data);
        }
        dataRow.camel_();
        return service.update(dest, dataRow, configs, columns);
    }

    @Comment("保存")
    public long save(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                     @Comment("字段") String... columns) {
        return service.save(dest, columns);
    }

    @Comment("保存")
    public long save(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                     @Comment("数据行") Object data,
                     @Comment("字段") List<String> columns) {
        return service.save(dest, data, columns);
    }

    @Comment("保存")
    public long save(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                     @Comment("数据行") Object data,
                     @Comment("查询条件") String... columns) {
        return service.save(dest, data, columns);
    }

    @Comment("查询")
    public DataRow query(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                         @Comment("查询条件") String... conditions) {
        return service.query(dest, conditions);
    }

    @Comment("查询")
    public DataRow query(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                         @Comment("查询条件") List<String> conditions) {
        return service.query(dest, conditions);
    }

    @Comment("查询")
    public Map<String, Object> queryMap(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                        @Comment("查询条件") String... conditions) {
        return Optional.ofNullable(service.query(dest, conditions)).map(DataRow::toMap).orElse(null);
    }

    @Comment("查询")
    public Map<String, Object> queryMap(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                        @Comment("查询条件") List<String> conditions) {
        return Optional.ofNullable(service.query(dest, conditions)).map(DataRow::toMap).orElse(null);
    }

    @Comment("查询")
    public DataRow query(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                         @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                         @Comment("查询条件") String... conditions) {
        return service.query(dest, obj, conditions);
    }

    @Comment("查询")
    public DataSet<DataRow> querys(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                   @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                                   @Comment("查询条件") String... conditions) {
        return service.querys(dest, obj, conditions);
    }

    @Comment("查询")
    public DataSet<DataRow> querys(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                   @Comment("分页信息") ConfigStore config,
                                   @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                                   @Comment("查询条件") String... conditions) {
        return service.querys(dest, config, obj, conditions);
    }

    @Comment("查询")
    public DataSet<DataRow> querys(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                   @Comment("分页信息") PageNavi navi,
                                   @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                                   @Comment("查询条件") String... conditions) {
        return service.querys(dest, navi, obj, conditions);
    }

    @Comment("查询")
    public DataSet<DataRow> querys(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                   @Comment("分页信息") PageNavi navi,
                                   @Comment("查询条件") String... conditions) {
        return service.querys(dest, navi, conditions);
    }

    @Comment("查询")
    public Map<String, Object> map(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                   @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                                   @Comment("查询条件") String... conditions) {
        List<Map<String, Object>> items = service.maps(dest, obj, conditions);
        return items.get(0);
    }

    @Comment("查询")
    public Map<String, Object> map(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                   @Comment("复杂条件") ConfigStore config,
                                   @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                                   @Comment("查询条件") String... conditions) {
        List<Map<String, Object>> items = service.maps(dest, config, obj, conditions);
        return items.get(0);
    }

    @Comment("查询")
    public List<Map<String, Object>> maps(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                          @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                                          @Comment("查询条件") String... conditions) {
        return service.maps(dest, obj, conditions);
    }

    @Comment("查询")
    public List<Map<String, Object>> maps(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                                          @Comment("复杂条件") ConfigStore config,
                                          @Comment("根据obj的field/value构造查询条件(支侍Map和Object)(查询条件只支持 =和in)") Object obj,
                                          @Comment("查询条件") String... conditions) {
        return service.maps(dest, config, obj, conditions);
    }

    @Comment("执行sql")
    public long execute(@Comment("SQL语句") String dest,
                        @Comment("参数") String... conditions) {
        return service.execute(dest, conditions);
    }

    @Comment("删除数据")
    public long deletes(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                        @Comment("key") String key,
                        @Comment("查询条件") String... values) {
        return service.deletes(dest, key, values);
    }

    @Comment("删除数据")
    public long deletes(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                        @Comment("key") String key,
                        @Comment("查询条件") Long... values) {
//        SQL:DELETE FROM HR_EMPLOYEE WHERE ID = ?(100)
//        service.deletes("HR_EMPLOYEE", "ID", "100");
//        SQL:DELETE FROM HR_EMPLOYEE WHERE ID IN(?,?)
//        service.deletes("HR_EMPLOYEE", "ID", "100","200");
        return service.deletes(dest, key, values);
    }

    @Comment("删除数据")
    public long delete(@Comment("查询或操作的目标(表｜视图｜函数｜自定义SQL | SELECT语句)") String dest,
                       @Comment("复炸查询") DefaultConfigStore config) {
        return service.delete(dest, config);
    }

    @Comment("开始事务")
    public TransactionState beginTrans() throws Exception {
        return service.start();
    }

    @Comment("提交事务")
    public void commitTrans(@Comment("事务状态") TransactionState state) throws Exception {
        service.commit(state);
    }

    @Comment("回滚事务")
    public void rollbackTrans(@Comment("事务状态") TransactionState state) throws Exception {
        service.rollback(state);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigTable.IS_UPDATE_EMPTY_COLUMN = true;
        ConfigTable.IS_INSERT_EMPTY_COLUMN = true;

        //注释magic-api数据源
        Collection<MagicDynamicDataSource.DataSourceNode> dataSources = dynamicDataSource.datasourceNodes();
        dataSources.forEach(dataSource -> {
            if (StringUtils.isEmpty(dataSource.getKey()))
                return;
            try {
                DataSourceHolder.reg(dataSource.getKey(), dataSource.getDataSource());
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        });
    }
}
