package com.ocean.tigaapi.api.service;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocean.tigaapi.api.entity.ApiEntity;
import com.ocean.tigaapi.api.entity.ApiEntity.UpdatePathItem;
import com.ocean.tigaapi.db.DbModule;

@Component
public class ApiService {
	
	private final ObjectMapper mapper = new ObjectMapper();

	@Inject("main")
	private DbModule db;
	
	
    /**
     * 获取所有启用的接口
     */
    public List<ApiEntity> getAllActiveApis() throws Exception {
        return db.table("tiga_api")
                   .where("status = ?", 1)
                   .selectList("*", ApiEntity.class);
    }
    
    /**
     * 批量更新 访问路径
     * @param dataList
     * 			数据集 [[id,request_path],...]
     * @throws SQLException
     */
    public void batchUpdateRequestPath(List<Object[]> dataList) throws Exception {
    	db.transaction(() -> {
    		 try {
				db.exeBatch("UPDATE tiga_api SET request_path = ? WHERE id = ?", dataList);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
    	});
    }

    /**
     * 保存或更新接口
     */
    @SuppressWarnings("unchecked")
	public Object saveApi(ApiEntity api) throws Exception {
    	if(api.getId() == null) {
    		String uuid = UUID.randomUUID().toString().replace("-", "");
    		api.setId(uuid);
    		db.saveOrUpdate("""
    			 insert into tiga_api 
    			 #i{
			 		id=id
			 		,label=label
			 		,pid=pid
			 		,path=path
			 		,request_path=request_path
			 		,method=method
			 		,stype=stype
			 		,script=script
			 		,timeoutms=timeoutms
			 		,is_folder=is_folder
			 	}
    			""", mapper.convertValue(api, Map.class));
    		return uuid;
    	}else {
            final boolean[] resultWrapper = new boolean[1];
    		db.transaction(() -> {
	    		try {
	    			List<UpdatePathItem> items = api.getBatchNeedUpdateRequestPath();
	    			if (items != null && !items.isEmpty()) {
	    			    List<Object[]> args = items.stream()
	    			        .map(i -> new Object[]{ i.getRequest_path(), i.getId() }) // 这里的顺序要匹配你的 SQL 占位符
	    			        .collect(Collectors.toList());
	    			    
	    			    batchUpdateRequestPath(args);
	    			}
	    			 
	                resultWrapper[0] = db.saveOrUpdate("""
						update tiga_api set
						 	#U{
						 		label=label
						 		,pid=pid
						 		,path=path
						 		,request_path=request_path
						 		,method=method
						 		,stype=stype
						 		,script=script
						 		,timeoutms=timeoutms
						 		,is_folder=is_folder
						 	}
						where id = #{id}
						""", mapper.convertValue(api, Map.class));
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
    		});
    		return resultWrapper[0];
    	}
    }

    /**
     * 删除接口
     */
    public boolean deleteApi(String ids) throws Exception {
        return db.table("tiga_api").where("id IN (?...)", Arrays.asList(ids.split(","))).delete() > 0;
    }
    
    
    /**
     * 获取所有启用接口的树形结构数据
     */
    public List<ApiEntity> getTreeOfAllActiveApis() throws Exception {
        return  buildTree(getAllActiveApis(), "0");
    }
    
    /**
     * 递归构建树
     * @param list 全量数据
     * @param pid 当前查找的父ID
     */
    private List<ApiEntity> buildTree(List<ApiEntity> list, String pid) {
        return list.stream()
            .filter(item -> Objects.equals(item.getPid(), pid)) // 过滤出当前层级的子节点
            .map(item -> {
                // 递归查找子节点
                item.setChildren(buildTree(list, item.getId()));
                return item;
            })
            .collect(Collectors.toList());
    }
}