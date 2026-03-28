package com.ocean.tigaapi.component.entity;
import lombok.Data;
import java.io.Serializable;

@Data
public class ComponentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 组件唯一标识 */
    private String id;
    /** 组件名称 */
    private String name;
    /** 父组件编号 */
    private String pid;
    /** 组件类型：1：表单组件；2：业务组件 */
    private String ctype;
    
    /** 原始 HTML/SFC 代码 */
    private String rawSfc;
    /** 编译处理后的 Script 内容 */
    private String compiledJs;
    /** 编译处理后的 模板字符串 */
    private String compiledTemplate;
    /** 编译处理后的 样式字符串 */
    private String compiledCss;
    /** 业务代码  */
    private String businessCode;
    
    private String createBy;
    private String createTime;
    private String updateTime;
    //是否有效：0：无效；1：有效
    private Integer status;

    //是否为文件夹
    // 0：文件；1：文件夹
    private String isFolder;
}