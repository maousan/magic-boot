package com.ocean.tigaapi.db.util.segment.impl;

import java.util.Map;

import com.ocean.tigaapi.db.util.segment.Segment;

public class VariableSegment implements Segment {
	final transient private String name;

	public VariableSegment(String nameParam) {
		this.name = nameParam;
	}

	public Object evaluate(Map<String, Object> variables, String... status) {
		if (status.length == 0) {
			if (!variables.containsKey(this.name)) {
				throw new RuntimeException("变量#{" + this.name + "}没有被赋值!");
			}
			return "?";
		} else {
			if (!variables.containsKey(this.name)) {
				 return "#{"+this.name+"}";
			}
			return variables.get(this.name);
		}

	}
}
