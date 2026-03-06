package org.ssssssss.magicapi.liteflow.model;

import org.ssssssss.magicapi.core.model.MagicEntity;
import org.ssssssss.magicapi.core.model.PathMagicEntity;

import java.util.Objects;

public class FlowInfo extends PathMagicEntity {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 规则ID
     */
    private String chainId;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 路由
     */
    private String route;

    /**
     * 组件描述
     */
    private String description;

    /**
     * 入参
     */
    private String params;

    /**
     * EL表达式
     */
    private String el;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getEl() {
        return el;
    }

    public void setEl(String el) {
        this.el = el;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FlowInfo copy() {
        FlowInfo info = new FlowInfo();
        super.copyTo(info);
        info.setChainId(this.chainId);
        info.setEnabled(this.enabled);
        info.setName(this.name);
        info.setDescription(this.description);
        info.setParams(this.params);
        return info;
    }

    @Override
    public MagicEntity simple() {
        FlowInfo info = new FlowInfo();
        super.simple(info);
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FlowInfo componentInfo = (FlowInfo) o;
        return Objects.equals(id, componentInfo.id) &&
                Objects.equals(path, componentInfo.path) &&
                Objects.equals(script, componentInfo.script) &&
                Objects.equals(name, componentInfo.name) &&
                Objects.equals(description, componentInfo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, script, name, groupId, description);
    }

}