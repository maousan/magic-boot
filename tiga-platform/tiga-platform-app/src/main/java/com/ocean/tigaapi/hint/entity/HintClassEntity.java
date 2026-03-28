package com.ocean.tigaapi.hint.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class HintClassEntity {
    private String className;
    private String name;
    private String doc;
    // 新增：存储类注解
    private List<String> annotations = new ArrayList<>();
    private List<HintMethodEntity> methods = new ArrayList<>();
}