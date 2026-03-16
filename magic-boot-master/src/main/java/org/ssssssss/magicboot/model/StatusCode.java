package org.ssssssss.magicboot.model;

import org.ssssssss.magicapi.core.model.JsonBean;

public enum StatusCode {

    CERTIFICATE_EXPIRED(402, "凭证已过期"),
    FORBIDDEN(403, "禁止访问"),
    DEMO_FORBIDDEN(403, "演示环境禁止增删改操作");

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
