package org.ssssssss.magicboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.ssssssss.magicboot.model.CommonCountMap;

import java.util.List;
import java.util.Map;

/**
 * 应用启动时重建 CommonCountMap 缓存，避免重启后库位操作用户计数丢失。
 */
//@Component
public class CommonCountMapRecoveryInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CommonCountMapRecoveryInitializer.class);

    private static final String SQL_RECOVER = """
            select d.location_code as location_code,
                   count(distinct d.bill_id) as user_count
              from t_picking_upload_detail d
             where ifnull(d.status, 0) <> 1
               and ifnull(d.location_code, '') <> ''
               and ifnull(d.bill_id, '') <> ''
             group by d.location_code
            """;

    private final JdbcTemplate jdbcTemplate;

    public CommonCountMapRecoveryInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(SQL_RECOVER);
            CommonCountMap.clear();
            int recovered = 0;
            for (Map<String, Object> row : rows) {
                String locationCode = asString(row.get("location_code"));
                int userCount = asInt(row.get("user_count"));
                if (locationCode == null || locationCode.isBlank() || userCount <= 0) {
                    continue;
                }
                CommonCountMap.set(locationCode, userCount);
                recovered++;
            }
            log.info("CommonCountMap 启动恢复完成: {} 个库位, 总计数={}", recovered, CommonCountMap.totalCount());
        } catch (Exception e) {
            // 表不存在/启动初期数据库不可用时不阻断应用
            log.warn("CommonCountMap 启动恢复失败，已跳过: {}", e.getMessage());
        }
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static int asInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignored) {
            return 0;
        }
    }
}
