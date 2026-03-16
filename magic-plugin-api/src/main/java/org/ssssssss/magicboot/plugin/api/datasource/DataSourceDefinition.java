package org.ssssssss.magicboot.plugin.api.datasource;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源定义
 */
public class DataSourceDefinition {

    private String name;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String type = "HikariCP";
    private Map<String, String> properties = new HashMap<>();

    public DataSourceDefinition() {
    }

    public DataSourceDefinition(String name, String url, String username, String password) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public DataSourceDefinition property(String key, String value) {
        this.properties.put(key, value);
        return this;
    }
}
