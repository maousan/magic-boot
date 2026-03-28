package com.ocean.tigaapi.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import com.ocean.tigaapi.db.DbModule;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DbMainConfig {

    @Bean("main") // 显式注册名为 main 的 DbContext
    public DbModule dbMain(@Inject("${db_config.main}") HikariDataSource ds) {
        return new DbModule(ds);
    }
}