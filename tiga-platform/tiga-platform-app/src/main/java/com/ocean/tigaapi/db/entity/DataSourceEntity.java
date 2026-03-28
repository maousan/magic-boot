package com.ocean.tigaapi.db.entity;

import lombok.Data;

@Data
public class DataSourceEntity {
    private String name;        	// 数据源标识符：ds1, ds2
    private String url;
    private String username;
    private String password;
    private String driver;
    private int minIdle; 			//最小空闲连接数   2
    private int maxPoolSize;   		//最大连接池大小   10
    private int connectionTimeout;	// 连接超时毫秒    30000
    private int idleTimeout; 		//空闲释放超时毫秒 600000
}