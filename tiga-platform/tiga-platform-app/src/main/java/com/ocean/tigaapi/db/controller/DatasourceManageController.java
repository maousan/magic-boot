package com.ocean.tigaapi.db.controller;

import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;

import com.ocean.tigaapi.db.entity.DataSourceEntity;
import com.ocean.tigaapi.db.service.DynamicDatasourceService;

@Controller
@Mapping("/tiga/ds")
public class DatasourceManageController {
    
    @Inject
    private DynamicDatasourceService dsManager;

    @Post
    @Mapping("/save")
    public String save(@Body DataSourceEntity entity) {
        dsManager.registerDs(entity);
        return "DataSource Updated";
    }
    
    @Post
    @Mapping("/remove")
    public String remove(String name) {
        dsManager.removeDs(name);
        return "DataSource Removed";
    }
}