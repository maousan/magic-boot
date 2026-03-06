package org.ssssssss.magicapi.excel.model;

import lombok.Data;

/**
 * @author hunterhou
 * @date 2023/2/28 10:14
 */
@Data
public class ExcelHead<T> {


    //内容里的字段名称
    private String fieldName;
    //显示值，一般为中文的
    private String title;
    private T nullValue; //如果为null的值

    public ExcelHead() {}

    public ExcelHead(String fieldName, String title) {
        this.fieldName = fieldName;
        this.title = title;
    }

    public ExcelHead(String fieldName, String title, T nullValue) {
        this.fieldName = fieldName;
        this.title = title;
        this.nullValue = nullValue;
    }
}