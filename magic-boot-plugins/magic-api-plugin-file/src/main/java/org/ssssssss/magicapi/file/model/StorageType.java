package org.ssssssss.magicapi.file.model;

import java.util.List;

/**
 * 存储类型定义
 */
public class StorageType {

    private String type;
    private String name;
    private String icon;
    private List<StorageTypeField> fields;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<StorageTypeField> getFields() {
        return fields;
    }

    public void setFields(List<StorageTypeField> fields) {
        this.fields = fields;
    }

    /**
     * 存储类型字段定义
     */
    public static class StorageTypeField {
        private String name;
        private String label;
        private String type;
        private boolean required;
        private String placeholder;
        private String defaultValue;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
