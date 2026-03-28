package com.ocean.tigaapi.db.util.segment;

import java.util.Map;

public interface Segment {
	Object evaluate(Map<String, Object> variables,String... status);
}
