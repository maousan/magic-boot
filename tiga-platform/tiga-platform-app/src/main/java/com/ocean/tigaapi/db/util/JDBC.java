package com.ocean.tigaapi.db.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocean.tigaapi.db.util.segment.Segment;
import com.ocean.tigaapi.db.util.segment.impl.PlainTextSegment;
import com.ocean.tigaapi.db.util.segment.impl.VariableSegment;
import com.ocean.tigaapi.utils.DateUtil;

public class JDBC {

	private String template;
	private List<Object> parmList = new LinkedList<Object>();
	private Map<String, Object> variables = new HashMap<String, Object>();
	private Map<String, String> dynamicFieldMap = null;

	public void setTemplate(String template) {
		this.template = template;
	}
	public JDBC(String sql) {
		this.template = sql.replaceAll("′", "'").replaceAll("&prime;", "'");
	}
	public JDBC(String sql,  Map<String, Object> params) {
		this.template = sql.replaceAll("′", "'").replaceAll("&prime;", "'");
		setVariables(params);
	}

	public JDBC setData(String key, String value){
		variables.put(key, value);
		return this;
	}

	public JDBC setDataAll(Map<String, Object> params){
		variables.putAll(params);
		return this;
	}

	private List<Segment> parseSegments(String sqlTemplate) {
		final List<Segment> segments = new ArrayList<Segment>();
		final int index = collectSegments(segments, sqlTemplate);
		addTail(segments, index, sqlTemplate);
		return segments;
	}

	private int collectSegments(List<Segment> segs, String sqlTemplate) {
		Pattern pattern = Pattern.compile("\\#\\{[^}]*\\}");
		Matcher matcher = pattern.matcher(sqlTemplate);
		int index = 0;
		while (matcher.find()) {
			addPrecedingPlainText(segs, matcher, index, sqlTemplate);
			addVarisble(segs, matcher, sqlTemplate);
			index = matcher.end();
		}
		return index;
	}

	private void addVarisble(List<Segment> segs, Matcher matcher, String sqlTemplate) {
		String name = sqlTemplate.substring(matcher.start() + 2, matcher.end() - 1);
		parmList.add(variables.get(name));
		if (dynamicFieldMap != null) {
			dynamicFieldMap.put(name, name);
		}
		segs.add(new VariableSegment(name));
	}

	private void addPrecedingPlainText(List<Segment> segs, Matcher matcher, int index, String sqlTemplate) {
		if (index != matcher.start()) {
			segs.add(new PlainTextSegment(sqlTemplate.substring(index, matcher.start())));
		}
	}

	private void addTail(List<Segment> segs, int index, String sqlTemplate) {
		if (index < sqlTemplate.length()) {
			segs.add(new PlainTextSegment(sqlTemplate.substring(index)));
		}
	}

	private void clearParmList() {
		parmList.clear();
		parmList = new LinkedList<Object>();
	}
	private String _sql;
	private Object[] args;
	public String getSql() {
		return this._sql;
	}
	public Object[] getArgs() {
		return this.args;
	}

	public JDBC getSqlSegmentInfo() {
		this.variables.put("sysId", UUID.randomUUID().toString().replace("-", ""));
		this.variables.put("sysNowTime", DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
		String sql = "";
		List<Segment> segments = parseSegments(JDBCPluginUtil.getSegmentReplace(template, this.variables));
		for (int j = 0; j < segments.size(); j++) {
			sql += segments.get(j).evaluate(this.variables);
		}
		_sql = sql;
		args = parmList.toArray();
		clearParmList();
		return this;
	}

	private void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
	 
	static class JDBCPluginUtil {

		static String andquery = "select  * from table where 1=1  #and{a=b,a rlike b}    #OR{a=b,a=b,a=c} #FIND_IN_SET{id=b,id=b}";
		static String updateQuery = "update table set #U{a=b,a=b,a=b,a=c}  where id =#{id}";
		static String insertseg = "insert into table #i{a=b,m=b}";

		public static Map<String, String> relation = new ConcurrentHashMap<String, String>();
		public static String full = "\\#u\\{([^}]*)\\}";
		public static String full_up = "\\#U\\{([^}]*)\\}";
		public static String fullStart = "\\#u\\{";
		public static String fullStart_up = "\\#U\\{";
		public static String start = "\\#\\{";
		public static String end = "}";
		public static String dh = ",";
		public static String eq = "=";
		public static String space = " ";
		public static String and = "and";
		public static String and_up = "AND";
		public static String or = "or";
		public static String or_up = "OR";
		public static String like = "like";
		public static String llike = "llike";
		public static String rlike = "rlike";
		public static String andPtr = "\\#and\\{([^}]*)\\}";
		public static String andPtr_up = "\\#AND\\{([^}]*)\\}";
		public static String FIND_IN_SET = "\\#FIND_IN_SET\\{([^}]*)\\}";
		public static String FIND_IN_SET_LOW = "\\#find_in_set\\{([^}]*)\\}";
		public static String FIND_IN_SET_STR = "FIND_IN_SET";
		public static String FIND_IN_SET_STR_LOW = "find_in_set";
		public static String FIND_IN_SET_START = "\\#FIND_IN_SET\\{";
		public static String FIND_IN_SET_START_LOW = "\\#find_in_set\\{";
		public static String orPtr = "\\#or\\{([^}]*)\\}";
		public static String orPtr_up = "\\#OR\\{([^}]*)\\}";
		public static String andPtrStart = "\\#and\\{";
		public static String andPtrStart_up = "\\#AND\\{";
		public static String orPtrStart = "\\#or\\{";
		public static String orPtrStart_up = "\\#OR\\{";
		public static String leftC = "(";
		public static String rightC = ")";
		public static String insert = "\\#i\\{([^}]*)\\}";
		public static String insert_up = "\\#I\\{([^}]*)\\}";
		public static String insertStart = "\\#i\\{";
		public static String insertStart_up = "\\#I\\{";
		public static String values = "values";
		public static String lt = "<";
		public static String lte = "<=";
		public static String gte = ">=";
		public static String gt = ">";
		public static String neq = "!=";

		static {
			relation.put(full, fullStart);
			relation.put(full_up, fullStart_up);
			relation.put(andPtr, andPtrStart);
			relation.put(andPtr_up, andPtrStart_up);
			relation.put(orPtr, orPtrStart);
			relation.put(orPtr_up, orPtrStart_up);
			relation.put(insert, insertStart);
			relation.put(insert_up, insertStart_up);
			relation.put(FIND_IN_SET, FIND_IN_SET_START);
			relation.put(FIND_IN_SET_LOW, FIND_IN_SET_START_LOW);
		}

		public static Map<String, String> getBatchInsert(String sql, List<Map<String, Object>> params) {
			List<Map<String, String>> pm = coverList(sql, params);
			int index = 0;
			StringBuffer bsql = new StringBuffer();
			Map<String, String> p = new HashMap<String, String>();
			for (Map<String, String> map : pm) {
				String nsql = map.get("sql");
				Map<String, List<String>> segFromContent = getSegFromConteng(nsql, new String[] { insert, insert_up });
				for (String ptr : segFromContent.keySet()) {
					if (ptr.equals(insert) || ptr.equals(insert_up)) {
						nsql = ins(nsql, map, segFromContent.get(ptr), ptr);
					}
				}
				p.putAll(map);
				if (index == 0) {
					bsql.append(nsql);
				} else {
					bsql.append(",").append(nsql.substring(nsql.indexOf("values") + 6, nsql.length()));
				}
				index++;
			}
			p.put("sql", bsql.toString());
			return p;
		}

		public static String ins(String content, Map<String, String> params, List<String> inSeg, String ptr) {

			for (String seg : inSeg) {
				StringBuffer sbKey = new StringBuffer();
				StringBuffer sbValue = new StringBuffer();
				String[] orgSegs = seg.split(dh);
				String sg = "";
				for (int i = 0; i < orgSegs.length; i++) {
					sg = orgSegs[i];
					if (!sg.contains(eq)) {
						sbKey.append(sg);
						sbValue.append(start).append(sg).append(end);
						continue;
					}
					int startLen = sbKey.length();
					String[] split = sg.split(eq);
					String key = split[0].trim();
					String valueKey = split[1].trim();
					if (params.get(valueKey) != null) {
						sbKey.append(key);
						String randKey = getRandomString();
						sbValue.append(start).append(randKey).append(end);
						params.put(randKey, params.get(valueKey));
						params.remove(valueKey);
					}
					if (i != (orgSegs.length - 1) && sbKey.length() > 0 && sbKey.length() > startLen) {
						sbKey.append(dh);
						sbValue.append(dh);
					}
				}
				if (sbKey.length() > 0 && String.valueOf(sbKey.charAt(sbKey.length() - 1)).equals(dh)) {
					String keySeg = leftC + sbKey.toString().substring(0, sbKey.length() - 1).toString() + rightC;
					String valueSeg = leftC + sbValue.toString().substring(0, sbValue.length() - 1).toString() + rightC;
					content = content.replaceAll(relation.get(ptr) + seg + end,
							space + keySeg + space + values + space + valueSeg);
				} else {
					String keySeg = leftC + sbKey + rightC;
					String valueSeg = leftC + sbValue + rightC;
					content = content.replaceAll(relation.get(ptr) + seg + end,
							space + keySeg + space + values + space + valueSeg);
				}
			}
			return content;
		}

		public static List<Map<String, String>> coverList(String sql, List<Map<String, Object>> params) {
			List<Map<String, String>> result = new LinkedList<Map<String, String>>();
			for (Map<String, Object> map : params) {
				Map<String, String> inMap = new HashMap<String, String>();
				inMap.put("sql", new String(sql));
				for (String key : map.keySet()) {
					inMap.put(key, map.get(key) == null ? "" : String.valueOf(map.get(key)));
				}
				result.add(inMap);
			}
			return result;
		}

		public static String getSegmentReplace(String content, Map<String, Object> params) {
			Map<String, List<String>> segFromContent = getSegFromConteng(content, new String[] { andPtr, andPtr_up,
					orPtr, orPtr_up, full, full_up, FIND_IN_SET, FIND_IN_SET_LOW, insert, insert_up });
			for (String ptr : segFromContent.keySet()) {
				if (ptr.equals(andPtr) || ptr.equals(andPtr_up)) {
					content = andPtrReplace(content, params, segFromContent.get(ptr), ptr);
				}
				if (ptr.equals(orPtr) || ptr.equals(orPtr_up)) {
					content = orPtrReplace(content, params, segFromContent.get(ptr), ptr);
				}
				if (ptr.equals(full) || ptr.equals(full_up)) {
					content = standardReplace(content, params, segFromContent.get(ptr), ptr);
				}
				if (ptr.equals(FIND_IN_SET) || ptr.equals(FIND_IN_SET_LOW)) {
					content = standardFindInSet(content, params, segFromContent.get(ptr), ptr);
				}
				if (ptr.equals(insert) || ptr.equals(insert_up)) {
					content = insertReplace(content, params, segFromContent.get(ptr), ptr);
				}
			}
			return content;
		}

		public static String andPtrReplace(String content, Map<String, Object> params, List<String> inSeg, String ptr) {
			for (String seg : inSeg) {
				String[] segArr = seg.split(dh);
				StringBuffer sb = new StringBuffer();
				String orgSeg = relation.get(ptr) + seg + end;
				for (int i = 0; i < segArr.length; i++) {
					int runCount = 0;// 每个片段只会被执行一次
					String segOne = segArr[i];
					try {
						String[] compares = new String[] { llike, rlike, like, gte, lte, gt, lt, neq, eq };
						for (int j = 0; j < compares.length; j++) {
							String sqlCompare = compares[j];
							if (segOne.length() > 0 && segOne.contains(sqlCompare)) {
								if (runCount > 0) {
									continue;
								}
								runCount++;
								String[] innerSegs = segOne.split(sqlCompare);
								String key = innerSegs[0].trim();
								String valueKey = innerSegs[1].trim();
								Object value = params.get(valueKey);
								if (j == 0 && value != null && !value.equals("")) {
									valueKey = getRandomString();
									params.put(valueKey, "%" + value);
								} else if (j == 1 && value != null && !value.equals("")) {
									valueKey = getRandomString();
									params.put(valueKey, value + "%");
								} else if (j == 2 && value != null && !value.equals("")) {
									valueKey = getRandomString();
									params.put(valueKey, "%" + value + "%");
								}
								if (value != null && !value.equals("")) {
									sb.append(space).append(and).append(space).append(key).append(space);
									if (j < 3) {
										sb.append(like);
									} else {
										sb.append(sqlCompare);
									}
									sb.append(space).append(start).append(valueKey).append(end).append(space);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				content = content.replaceAll(orgSeg, sb.toString());
			}
			return content;
		}

		public static String orPtrReplace(String content, Map<String, Object> params, List<String> inSeg, String ptr) {
			int tag = 0;
			for (String seg : inSeg) {
				String[] segArr = seg.split(dh);
				StringBuffer sb = new StringBuffer();
				String orgSeg = relation.get(ptr) + seg + end;
				for (int i = 0; i < segArr.length; i++) {
					int runCount = 0;// 每个片段只会被执行一次
					String segOne = segArr[i];
					try {
						String[] compares = new String[] { llike, rlike, like, gte, lte, gt, lt, neq, eq };
						for (int j = 0; j < compares.length; j++) {
							String sqlCompare = compares[j];
							if (segOne.length() > 0 && segOne.contains(sqlCompare)) {
								if (runCount > 0) {
									continue;
								}
								runCount++;
								String[] innerSegs = segOne.split(sqlCompare);
								String key = innerSegs[0].trim();
								String valueKey = innerSegs[1].trim();
								Object value = params.get(valueKey);
								if (j == 0 && params.get(valueKey) != null) {
									valueKey = getRandomString();
									params.put(valueKey, "%" + value);
								} else if (j == 1 && params.get(valueKey) != null) {
									valueKey = getRandomString();
									params.put(valueKey, value + "%");
								} else if (j == 2 && params.get(valueKey) != null) {
									valueKey = getRandomString();
									params.put(valueKey, "%" + value + "%");
								}
								if (sqlCompare.equals(eq) && (segOne.contains("<") || segOne.contains(">")))
									continue;
								if (value != null) {
									sb.append(space);
									if (tag > 0)
										sb.append(or);
									sb.append(space).append(key).append(space);
									if (j < 2) {
										sb.append(like);
									} else {
										sb.append(sqlCompare);
									}
									sb.append(space).append(start).append(valueKey).append(end).append(space);
								}
								tag++;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (sb.length() > 0) {
					sb.insert(0, leftC);
					sb.insert(0, space);
					sb.insert(0, and);
					sb.append(rightC);
					sb.append(space);
				}
				content = content.replaceAll(orgSeg, sb.toString());
			}
			return content;
		}

		/**
		 * "select * from table where 1=1 #and{a = b,a=c,b=c}"; 获取其中的{#and{}:[a = b,a =
		 * c],#or{}:[c = c]}
		 * 
		 * @param content
		 * @param ptrs
		 * @return
		 */
		public static Map<String, List<String>> getSegFromConteng(String content, String... ptrs) {
			Map<String, List<String>> segAll = new HashMap<String, List<String>>();
			for (String ptr : ptrs) {
				List<String> seg = new LinkedList<String>();
				Pattern reg = Pattern.compile(ptr);
				Matcher matcher = reg.matcher(content);
				while (matcher.find()) {
					String group = matcher.group(1);
					seg.add(group);
				}
				segAll.put(ptr, seg);
			}
			return segAll;
		}

		public static String standardFindInSet(String content, Map<String, Object> params, List<String> inSeg,
				String ptr) {
			for (String seg : inSeg) {
				StringBuffer sb = new StringBuffer();
				String[] orgSegs = seg.split(dh);
				String sg = "";
				for (int i = 0; i < orgSegs.length; i++) {
					sg = orgSegs[i];
					if (!sg.contains(eq)) {
						sb.append(start).append(sg).append(end);
						continue;
					}
					String[] split = sg.split(eq);
					String key = split[0].trim();
					String valueKey = split[1].trim();
					if (params.get(valueKey) != null) {
						sb.append(space).append(and).append(space).append(FIND_IN_SET_STR.toLowerCase()).append(leftC)
								.append(key).append(dh).append(start).append(valueKey).append(end).append(rightC);
					}

				}
				content = content.replaceAll(relation.get(ptr) + seg + end, sb.toString());
			}
			return content;
		}

		public static String insertReplace(String content, Map<String, Object> params, List<String> inSeg, String ptr) {
			for (String seg : inSeg) {
				StringBuffer sbKey = new StringBuffer();
				StringBuffer sbValue = new StringBuffer();
				String[] orgSegs = seg.split(dh);
				String sg = "";
				for (int i = 0; i < orgSegs.length; i++) {
					sg = orgSegs[i];
					if (!sg.contains(eq)) {
						sbKey.append(sg);
						sbValue.append(start).append(sg).append(end);
						continue;
					}
					int startLen = sbKey.length();
					String[] split = sg.split(eq);
					String key = split[0].trim();
					String valueKey = split[1].trim();
					if (params.get(valueKey) != null && !"".equals(params.get(valueKey))) {
						sbKey.append(key);
						sbValue.append(start).append(valueKey).append(end);
					}
					if (i != (orgSegs.length - 1) && sbKey.length() > 0 && sbKey.length() > startLen) {
						sbKey.append(dh);
						sbValue.append(dh);
					}
				}
				if (sbKey.length() > 0 && String.valueOf(sbKey.charAt(sbKey.length() - 1)).equals(dh)) {
					String keySeg = leftC + sbKey.toString().substring(0, sbKey.length() - 1).toString() + rightC;
					String valueSeg = leftC + sbValue.toString().substring(0, sbValue.length() - 1).toString() + rightC;
					content = content.replaceAll(relation.get(ptr) + seg + end,
							space + keySeg + space + values + space + valueSeg);
				} else {
					String keySeg = leftC + sbKey + rightC;
					String valueSeg = leftC + sbValue + rightC;
					content = content.replaceAll(relation.get(ptr) + seg + end,
							space + keySeg + space + values + space + valueSeg);
				}
			}
			return content;
		}

		/**
		 * #{a=c,a=m}进行替换
		 * 
		 * @param content
		 * @param params
		 * @param inSeg
		 * @return
		 */
		public static String standardReplace(String content, Map<String, Object> params, List<String> inSeg,
				String ptr) {
			for (String seg : inSeg) {
				StringBuffer sb = new StringBuffer();
				String[] orgSegs = seg.split(dh);
				String sg = "";
				for (int i = 0; i < orgSegs.length; i++) {
					sg = orgSegs[i];
					if (!sg.contains(eq)) {
						sb.append(start).append(sg).append(end);
						continue;
					}
					int startLen = sb.length();
					String[] split = sg.split(eq);
					String key = split[0].trim();
					String valueKey = split[1].trim();
					if (params.get(valueKey) != null) {
						sb.append(space).append(key).append(space).append(eq).append(start).append(valueKey).append(end)
								.append(space);
					}
					if (i != (orgSegs.length - 1) && sb.length() > 0 && sb.length() > startLen) {
						sb.append(dh);
					}
				}
				if (sb.length() > 0 && String.valueOf(sb.charAt(sb.length() - 1)).equals(dh)) {
					content = content.replaceAll(relation.get(ptr) + seg + end,
							sb.toString().substring(0, sb.length() - 2).toString());
				} else {
					content = content.replaceAll(relation.get(ptr) + seg + end, sb.toString());
				}

			}
			return content;
		}

		/**
		 * 获取随机数
		 * 
		 * @return
		 */
		public static String getRandomString() {
			Random r = new Random();
			String code = "";
			for (int i = 0; i < 9; ++i) {
				int temp = r.nextInt(52);
				char x = (char) (temp < 26 ? temp + 97 : (temp % 26) + 65);
				code += x;
			}
			return code;
		}
	}

	public static void main(String[] args) {
		JDBC jdbc1 = new JDBC("select  * from table where 1=1  #and{a=b,a rlike b}  #OR{a=b,a=b,a=c} #FIND_IN_SET{str=strlist,id=b}");
		JDBC jdbc2 = new JDBC("update table set #U{a=b,b=b,c=b,d=c}  where id =#{id}");
		JDBC jdbc3 = new JDBC("insert into table #i{a=b,m=b}");
		
		jdbc1.setData("b", "test").getSqlSegmentInfo();
		System.out.print(jdbc1.getSql()+"  ");
		System.out.println(jdbc1.getArgs());
		
		jdbc2.setData("b", "test").setData("c", "1").setData("id", "2").getSqlSegmentInfo();
		System.out.print(jdbc2.getSql()+"  ");
		System.out.println(jdbc2.getArgs());
		
		jdbc3.setData("b", "test").getSqlSegmentInfo();
		System.out.print(jdbc3.getSql()+"  ");
		System.out.println(jdbc3.getArgs());
		 
		
		
	}
}
