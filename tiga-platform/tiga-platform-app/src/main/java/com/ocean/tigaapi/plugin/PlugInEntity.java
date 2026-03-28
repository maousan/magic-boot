package com.ocean.tigaapi.plugin;

import lombok.Data;

@Data
public class PlugInEntity {
	private int id;
	private String name;  // 引用的别名，如数据源标识符：ds1, ds2
	private String ptype; // 插件类型,db、redis、kafka。。。
	//配置信息，如mysql的：{url:"",username:"",password:"",driver:"",。。。}
	private String info;
}
