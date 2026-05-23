package org.ssssssss.magicboot.zintis.led.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class LedDeviceRegistryServiceTest {

    @Test
    void saveClientDevice_shouldUpsertDevice() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        LedDeviceRegistryService service = new LedDeviceRegistryService(jdbcTemplate);

        service.saveClientDevice(" 3A:69:7A:08:D0:A5 ", " 192.168.2.102 ");

        verify(jdbcTemplate).update(
                contains("insert into t_led_device"),
                eq("3A:69:7A:08:D0:A5"),
                eq("192.168.2.102"),
                eq("Netty客户端自动注册")
        );
    }

    @Test
    void saveClientDevice_shouldNotOverwriteRemarkWhenDeviceExists() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        LedDeviceRegistryService service = new LedDeviceRegistryService(jdbcTemplate);

        service.saveClientDevice("3A:69:7A:08:D0:A5", "192.168.2.102");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sqlCaptor.capture(), eq("3A:69:7A:08:D0:A5"), eq("192.168.2.102"), eq("Netty客户端自动注册"));
        String sql = sqlCaptor.getValue().toLowerCase();
        String duplicateUpdateSql = sql.substring(sql.indexOf("on duplicate key update"));
        assertFalse(duplicateUpdateSql.contains("remark"));
    }

    @Test
    void saveClientDevice_shouldIgnoreBlankMac() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        LedDeviceRegistryService service = new LedDeviceRegistryService(jdbcTemplate);

        service.saveClientDevice(" ", "192.168.2.102");

        verify(jdbcTemplate, never()).update(contains("insert into t_led_device"));
    }
}
