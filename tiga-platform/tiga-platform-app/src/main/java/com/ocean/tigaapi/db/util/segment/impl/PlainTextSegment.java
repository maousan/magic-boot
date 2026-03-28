package com.ocean.tigaapi.db.util.segment.impl;

import java.util.Map;

import com.ocean.tigaapi.db.util.segment.Segment;


public class PlainTextSegment implements Segment {
    private String text;

    public PlainTextSegment(String textplain) {
        this.text = textplain;
    }

    public Object evaluate(Map<String, Object> variables,String... status) {
        if (variables.get(this.text) == null) {
            return this.text;
        }
        return variables.get(this.text);
    }
}
