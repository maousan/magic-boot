package org.ssssssss.magicboot.utils;

import org.anyline.util.BeanUtil;

import java.util.Map;

public class JdbcUrlBuilder {

    private static final Map<String, String> URL_TEMPLATE = Map.of(
        "mysql", "jdbc:mysql://%s:%s/%s",
        "postgresql", "jdbc:postgresql://%s:%s/%s",
        "oracle", "jdbc:oracle:thin:@%s:%s:%s",
        "sqlserver", "jdbc:sqlserver://%s:%s;databaseName=%s",
        "clickhouse", "jdbc:clickhouse://%s:%s/%s",
        "dm", "jdbc:dm://%s:%s/%s",
        "kingbase", "jdbc:kingbase8://%s:%s/%s"
    );

    public static String build(String dbType, String host, int port, String database) {
        String template = URL_TEMPLATE.get(dbType.toLowerCase());

        if (template == null) {
            throw new IllegalArgumentException("Unsupported db_type: " + dbType);
        }

        return String.format(template, host, port, database);
    }


    /**
     * Map重载方法
     */
    public static String build(Map<String, Object> config) {

        String dbType = (String) BeanUtil.propertyNvl(config, new String[]{"dbType", "db_type"});
        if (dbType == null) {
            throw new IllegalArgumentException("dbType 不能为空");
        }
        String host = (String) BeanUtil.propertyNvl(config, new String[]{"host", "Host"});
        String database = (String) BeanUtil.propertyNvl(config, new String[]{"db", "database", "schema", "schemaName"});

        int port = (int) BeanUtil.propertyNvl(config, new String[]{"port"});

        return build(dbType, host, port, database);
    }
}