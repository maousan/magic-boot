package org.ssssssss.magicapi.liteflow.model;

/**
 * Simple in-memory representation of a LiteFlow definition file.
 * This can be extended to include full parsing results when needed.
 */
public class FlowDefinition {
    private String name;
    private String content;

    public FlowDefinition() {
    }

    public FlowDefinition(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
