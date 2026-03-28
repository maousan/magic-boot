package com.ocean.tigaapi.component.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import com.ocean.tigaapi.db.DbModule;

@Component
public class ComponentService {

	@Inject("main")
	private DbModule db;

	private final Map<String, Object> scriptCache = new ConcurrentHashMap<>();

	/**
	 * 注销业务组件的后台脚本
	 */
	public void unRegister(String id) {
		scriptCache.remove(id);
	}
	
	/**
	 * 注册业务组件的后台脚本
	 */
	public void register(String id, String code) {
		scriptCache.put(id, code);
	}

	
	/**
	 * 获取组件执行代码，根据组件编号
	 */
	public Object getScriptById(String id) {
		// 先从缓存获取，没有再查数据库
		return scriptCache.computeIfAbsent(id, _id -> {
    		try {
    			return getById(_id).get("script");
    		} catch (SQLException e) {
    			throw new RuntimeException("根据编号加载组件信息异常: " + id, e);
    		}
    	});
	}

	public List<Map<String, Object>> getList(String pid, String ctype, String keyword) throws SQLException {
		return db.queryByList("""
				select
					t.*,
					(SELECT COUNT(*) FROM tiga_component WHERE pid = t.id AND status = 1) as child_count
				from
					component_info t
				where
					t.status = 1
					#and{t.name like keyword,t.ctype=ctype,t.pid=pid}
					order by t.create_time DESC
			""", Map.of("keyword", keyword, "pid", pid, "ctype", ctype));
	}

	/**
	 * 获取所有业务组件信息
	 */
	public List<Map<String, Object>> getBusinessComponentList() throws SQLException {
		return db.queryByList("select id,script from tiga_component where ctype='2' and is_folder=0", Map.of());
	}

	/**
	 * 创建新组件或文件夹
	 * 
	 * @throws Exception
	 */
	public boolean createComponent(Map<String, Object> params) throws Exception {
		return db.saveOrUpdate("""
				insert into  tiga_component
				 	#i{
				 		id=sysId
				 		,name=name
				 		,pid=pid
				 		,ctype=ctype
				 		,raw_sfc=raw_sfc
				 		,compiled_js=compiled_js
				 		,compiled_template=compiled_template
				 		,compiled_css=compiled_css
				 		,stype=stype
				 		,script=script
				 		,create_by=create_by
				 		,create_time=sysNowTime
				 		,update_time=update_time
				 		,status=status
				 		,is_folder=is_folder
				 	}
				""",params);
	}

	/**
	 * 更新名称
	 */
	public boolean rename(String id, String name) throws SQLException {
		return db.table("tiga_component").set("name", name).whereEq("id", id).update() > 0;
	}

	/**
	 * 删除组件
	 */
	public boolean delete(String id) throws SQLException {
		return db.table("tiga_component").whereEq("id", id).delete() > 0;
	}

	/**
	 * 获取组件信息，根据组件编号
	 * 
	 * @throws SQLException
	 */
	public Map<String, Object> getById(String id) throws SQLException {
		return db.table("tiga_component").whereEq("id", id).selectMap("*");
	}

	/**
	 * 获取组件页面编译信息，根据组件编号
	 * 
	 * @throws SQLException
	 */
	public Map<String, Object> getCompiledInfoById(String id) throws SQLException {
		return db.table("tiga_component").whereEq("id", id).selectMap("id,compiled_js,compiled_template,compiled_css");
	}

	/**
	 * 修改组件
	 */
	public boolean updateComponent(Map<String, Object> params) throws SQLException {

		return db.saveOrUpdate("""
				update tiga_component set
				 	#U{
				 		name=name
				 		,raw_sfc=raw_sfc
				 		,compiled_js=compiled_js
				 		,compiled_template=compiled_template
				 		,compiled_css=compiled_css
				 		,stype=stype
				 		,script=script
				 		,create_by=create_by
				 		,update_time=sysNowTime
				 		,status=status
				 		,is_folder=is_folder
				 	}
				where id = #{id}
				""", params);
	}
}