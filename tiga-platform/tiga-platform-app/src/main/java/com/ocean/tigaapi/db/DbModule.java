package com.ocean.tigaapi.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.noear.solon.data.tran.TranUtils;
import org.noear.wood.DbContext;
import org.noear.wood.SQLBuilder;

import com.ocean.tigaapi.db.util.JDBC;

public class DbModule extends DbContext{

	public DbModule(DataSource dataSource) {
		super(dataSource);
	}

    private static final Pattern VAR_PATTERN = Pattern.compile("([#$])\\{([^}]+)\\}");

    /**
     * 解析 SQL 语法
     * #{id} -> 会被加入到参数列表，由 Wood 安全处理
     * ${id} -> 直接替换字符串
     */
    private SQLBuilder buildSql(String sql, Map<String, Object> params) {
        SQLBuilder builder = new SQLBuilder();
        Matcher matcher = VAR_PATTERN.matcher(sql);
        int lastEnd = 0;

        while (matcher.find()) {
            // 添加上一个匹配项之前的静态 SQL
            builder.append(sql.substring(lastEnd, matcher.start()));
            
            String type = matcher.group(1); // # 或 $
            String key = matcher.group(2).trim(); // 变量名
            Object value = (params != null) ? params.get(key) : null;

            if ("$".equals(type)) {
                // 直接拼接字符串内容 (注意：此处需确保变量安全)
                builder.append(value != null ? value.toString() : "");
            } else {
                // 重点：使用 Wood 的参数化处理机制
                // Wood 的 SQLBuilder 接受 ? 占位符或直接 append(Object)
                builder.append(" ? ", value);
            }
            lastEnd = matcher.end();
        }
        builder.append(sql.substring(lastEnd));
        return builder;
    }

    /**
     * 查询数据列表
     * @param sql
     * 		select * from ${table} where id in (#{ids})
     * @param params
     * 		提交的数据集（Map<String, Object>）
     * 		{
     * 			table:'user',
     * 			ids:'1,2,3,...'
     *		}
     * @return
     * 		List<Map<String, Object>>
     * @throws SQLException
     */
    public List<Map<String, Object>> select(String sql, Map<String, Object> params) throws SQLException {
        return sql(buildSql(sql, params)).getMapList();
    }
    
    /**
     * 查询数据列表
     * @param sql
     * 		select * from table where 1=1 #and{a=a,b rlike b} #OR{c=c,d=d} #FIND_IN_SET{e=strList}
     * @param params
     * 		提交的数据集（Map<String, Object>）
     * 		{
     * 			a:1,
     * 			b:'xx',
     * 			c:'x',
     * 			d:d
     * 		}
     * 		注意：
     * 			sql 脚本中定义的字段如果在 params 中没有被赋值，将在查询中被抛弃。
     * 			上面样例中 FIND_IN_SET 查询片段将被 丢弃
     * @return
     * 		List<Map<String, Object>>
     * @throws SQLException
     */
    public List<Map<String, Object>> queryByList(String sql, Map<String, Object> params) throws SQLException {
		JDBC jdbc = new JDBC(sql).setDataAll(params).getSqlSegmentInfo();
		return sql(jdbc.getSql(), jdbc.getArgs()).getMapList();
    }

    /**
     * 查询单条数据
     * @param sql
     * 		select * from table where id=#{id}
     * @param params
     * 		提交的数据集（Map<String, Object>）
     * 		{id : 'xxx'}
     * @return
     * 		Map<String, Object>
     * @throws SQLException
     */
    public Map<String, Object> selectOne(String sql, Map<String, Object> params) throws SQLException {
        return sql(buildSql(sql, params)).getMap();
    }
    
    /**
     * 查询单条数据
     * @param sql
     * 		select * from table where id=#{id} #and{field1=field1,field2 rlike field2}
     * @param params
     * 		提交的数据集（Map<String, Object>）
     * 		{
     * 			id:1,
     * 			field1:'xx'
     * 		}
     * 		注意：
     * 			sql 脚本中定义的字段如果在 params 中没有被赋值，将在查询中被抛弃。
     * 			上面样例中 field2 字段查询将本丢弃。
     * @return
     * 		List<Map<String, Object>>
     * @throws SQLException
     */
    public Map<String, Object> queryByOne(String sql, Map<String, Object> params) throws SQLException {
		JDBC jdbc = new JDBC(sql).setDataAll(params).getSqlSegmentInfo();
		return sql(jdbc.getSql(), jdbc.getArgs()).getMap();
    }

    /**
     * 执行保存/更新
     * @param sql
     * 		update ${table} set name=#{name} where id=#{id}
     * 		insert into ${table}(field1,field2) values (${field1,field2})
     * @param params
     * 		提交的数据集（Map<String, Object>）
     * 		{
     * 			id:1,
     * 			name:'xx',
     * 			table:'user'
     * 			field1:'xx'
     * 			field2:'xx'
     * 		}
     * @return
     * 		boolean true/false
     * @throws SQLException
     */
    public boolean update(String sql, Map<String, Object> params) throws SQLException {
        return sql(buildSql(sql, params)).execute() > 0;
    }
    /**
     * 执行保存/更新
     * @param sql
     * 		update table set #U{a=a1,b=b1,c=c1,d=d1}  where id =#{id}
     * 		insert into table #i{id=sysId,a=a1,ctime=sysNowTime}
     * 		--- sysId 无需 params 赋值，返回 UUID值
     * 		--- sysNowTime 无需 params 赋值，返回 yyyy-MM-dd HH:mm:ss 值
     * @param params
     * 		提交的数据集（Map<String, Object>）
     * 		{
     * 			id:1,
     * 			name:'xx',
     * 			table:'user'
     * 		}
     * 		注意：
     * 			sql 脚本中定义的字段如果在 params 中没有被赋值，将在查询中被抛弃。
     * 			上面样例中 field2 字段查询将本丢弃。
     * @return
     * 		List<Map<String, Object>>
     * @throws SQLException
     */
    public boolean saveOrUpdate(String sql, Map<String, Object> params) throws SQLException {
    	JDBC jdbc = new JDBC(sql).setDataAll(params).getSqlSegmentInfo();
		return sql(jdbc.getSql(), jdbc.getArgs()).execute() > 0;
    }

    /**
     * 分页查询
     * @param sql
     * 			select * from table left join table2 on(table.field=table2.field) where name like #{name}
     * @param params
     * 			提交的数据集（Map<String, Object>）
     * 			{name:'xuhaiyang'}
     * @param pageIndex
     * 			当前页
     * @param pageSize
     * 			每页显示的条目数
     * @return
     * 		{
     * 			"total": 20,
     * 			"pageIndex": 1,
     * 			"pageSize": 10,
     * 			"list": [{},...]
     * 		}
     */
    public Map<String, Object> page(String sql, Map<String, Object> params, int pageIndex, int pageSize) {
        try {
            SQLBuilder baseBuilder = buildSql(sql, params);
            long total = sql(baseBuilder).getCount();

            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("pageIndex", pageIndex);
            result.put("pageSize", pageSize);

            if (total <= 0) {
                result.put("list", new ArrayList<>());
                return result;
            }

            int start = (pageIndex - 1) * pageSize;
            
            // 创建新的 SQLBuilder 用于分页，防止污染原 SQLBuilder
            SQLBuilder pageBuilder = new SQLBuilder();
            pageBuilder.append(baseBuilder.toString());
            pageBuilder.paramS.addAll(baseBuilder.paramS);

            // 这里 table1 传空字符串，因为我们是原生 SQL 查询
            getDialect().buildSelectRangeCode(this, "", pageBuilder, new StringBuilder(), start, pageSize);

            result.put("list", sql(pageBuilder).getMapList());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("分页异常: " + e.getMessage());
        }
    }

    /**
     * db.transaction(() => {
     * 		db.update(sql1);
     * 		db.update(sql2);
     * 	    //自动提交... 报错则自动回滚
     * });
     * @param scriptRunnable
     */
    public void transaction(Runnable scriptRunnable) {
        try {
            // 关键点：将第一个参数强转为 Transaction 消除 Ambiguous 错误
            // 不要使用 Tran，因为它已被标记为 @Deprecated
            TranUtils.execute((org.noear.solon.data.annotation.Transaction) null, () -> {
                scriptRunnable.run();
            });
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("脚本事务执行失败", e);
        }
    }
}