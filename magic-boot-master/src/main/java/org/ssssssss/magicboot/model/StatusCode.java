package org.ssssssss.magicboot.model;

import org.ssssssss.magicapi.core.model.JsonBean;

public enum StatusCode {

    CERTIFICATE_EXPIRED(402, "凭证已过期"),
    FORBIDDEN(403, "禁止访问"),
    DEMO_FORBIDDEN(403, "演示环境禁止增删改操作"),
    LICENSE_EXPIRED(403, "系统授权已过期，请导入有效授权文件"),
    LICENSE_ABNORMAL(403, "检测到系统时间异常，授权校验失败");

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;

    private String message;

    public JsonBean json(){
        return new JsonBean<>(this.code, this.message);
    }

    public JsonBean json(String message){
        return new JsonBean<>(this.code, message);
    }

}
