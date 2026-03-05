package org.ssssssss.magicapi.liteflow.model;

import org.ssssssss.magicapi.core.model.MagicEntity;
import org.ssssssss.magicapi.core.model.PathMagicEntity;

import java.util.Objects;

public class FlowComponentInfo extends PathMagicEntity {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 规则ID
     */
    private String nodeId;

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
     * 脚本类型
     */
    private String scriptType;

    /**
     * 节点类型
     */
    private String nodeType;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public FlowComponentInfo copy() {
        FlowComponentInfo info = new FlowComponentInfo();
        super.copyTo(info);
        info.setNodeId(this.nodeId);
        info.setEnabled(this.enabled);
        info.setName(this.name);
        info.setDescription(this.description);
        info.setScriptType(this.scriptType);
        info.setNodeType(this.nodeType);
        return info;
    }

    @Override
    public MagicEntity simple() {
        FlowComponentInfo info = new FlowComponentInfo();
        super.simple(info);
        return info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FlowComponentInfo componentInfo = (FlowComponentInfo) o;
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


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }
}