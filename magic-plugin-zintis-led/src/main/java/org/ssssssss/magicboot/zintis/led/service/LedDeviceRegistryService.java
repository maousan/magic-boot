package org.ssssssss.magicboot.zintis.led.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LedDeviceRegistryService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LedDeviceRegistryService(ObjectProvider<JdbcTemplate> jdbcTemplateProvider) {
        this(jdbcTemplateProvider.getIfAvailable());
    }

    LedDeviceRegistryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveClientDevice(String macAddress, String ipAddress) {
        if (macAddress == null || macAddress.isBlank()) {
            return;
        }
        if (jdbcTemplate == null) {
            log.debug("skip save LED netty client device because JdbcTemplate is unavailable");
            return;
        }
        String normalizedMac = macAddress.trim();
        String normalizedIp = ipAddress == null ? "" : ipAddress.trim();
        try {
            jdbcTemplate.update("""
                    insert into t_led_device (mac_address, ip, remark)
                    values (?, ?, ?)
                    on duplicate key update
                        ip = values(ip)
                    """, normalizedMac, normalizedIp, "Netty客户端自动注册");
            log.info("LED netty client device saved: macAddress={}, ip={}", normalizedMac, normalizedIp);
        } catch (Exception exception) {
            log.warn("save LED netty client device failed, macAddress={}, ip={}, error={}",
                    normalizedMac, normalizedIp, exception.getMessage());
        }
    }
}
