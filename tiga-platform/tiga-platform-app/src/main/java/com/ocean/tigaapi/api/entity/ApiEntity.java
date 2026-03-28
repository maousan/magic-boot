package com.ocean.tigaapi.api.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ApiEntity {
    private String id;
    private String label;        // 接口名称
    private String pid;         
    private String path;         // 标识路径
    private String request_path; // 访问路径
    private String method;      // GET, POST, PUT, DELETE
    private String stype;      // magic groovy
    private String script;      // Groovy 脚本内容
    private int timeoutms;   // Groovy 脚本执行最大超时时间（毫秒）
    private int is_folder;   // 0：文件；1：文件夹
    private int status;     // 1:启用, 0:禁用
    
    // 非数据库字段，用于前端树结构展示
    private List<ApiEntity> children = new ArrayList<>();
    
    @Data
    public static class UpdatePathItem {
        private String id;
        private String request_path;
    }
    
    private List<UpdatePathItem> batchNeedUpdateRequestPath = new ArrayList<>();
}