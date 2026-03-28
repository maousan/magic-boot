package com.ocean.tigaapi.hint.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class HintMethodEntity {
    private String name;
    private String returnType;
    private List<String> parameters = new ArrayList<>();
    private String comment;
    // 新增：存储方法注解
    private List<String> annotations = new ArrayList<>();
}