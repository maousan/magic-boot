package org.ssssssss.magicapi.excel.model;

import lombok.Data;

@Data
public class ExcelField {

    private String title;

    private String field;

    private Integer sort;

    private String headerAlign;

    private String align;

    private String dataType;
}