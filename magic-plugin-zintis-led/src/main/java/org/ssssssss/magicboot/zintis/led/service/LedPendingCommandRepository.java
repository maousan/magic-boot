package org.ssssssss.magicboot.zintis.led.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * LED 待重试指令持久化层。
 *
 * 采用 per-(mac,color) 最新目标状态覆盖语义（B 模型）：
 * 同一 mac+color 的新指令通过 ON DUPLICATE KEY UPDATE 覆盖旧指令，
 * 保证设备最终状态 = 用户最后操作，过期指令自动作废。
 */
@Slf4j
@Service
public class LedPendingCommandRepository {

    private JdbcTemplate jdbcTemplate;

    public LedPendingCommandRepository() {
    }

    @Autowired
    public void setJdbcTemplate(ObjectProvider<JdbcTemplate> jdbcTemplateProvider) {
        this.jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
    }

    LedPendingCommandRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 写入或覆盖一条待重试指令（同 mac+color 覆盖）。
     */
    public void upsert(String macAddress, String color, String command, String frameHex, String remoteAddress) {
        if (jdbcTemplate == null) {
            log.debug("skip upsert LED pending command because JdbcTemplate is unavailable: mac={}, color={}", macAddress, color);
            return;
        }
        if (macAddress == null || macAddress.isBlank()) {
            log.warn("skip upsert LED pending command because mac is blank: color={}, command={}", color, command);
            return;
        }
        String normalizedColor = (color == null || color.isBlank()) ? "ALL" : color.trim().toUpperCase();
        try {
            jdbcTemplate.update("""
                    INSERT INTO t_led_pending_command
                        (mac_address, color, command, frame_hex, remote_address, retry_count)
                    VALUES (?, ?, ?, ?, ?, 0)
                    ON DUPLICATE KEY UPDATE
                        command = VALUES(command),
                        frame_hex = VALUES(frame_hex),
                        remote_address = VALUES(remote_address),
                        retry_count = 0,
                        updated_at = CURRENT_TIMESTAMP
                    """, macAddress, normalizedColor, command, frameHex, remoteAddress);
            log.info("LED pending command upserted: mac={}, color={}, command={}", macAddress, normalizedColor, command);
        } catch (Exception exception) {
            log.warn("upsert LED pending command failed: mac={}, color={}, command={}, error={}",
                    macAddress, normalizedColor, command, exception.getMessage());
        }
    }

    /**
     * 成功后清除指定 mac+color 的待重试指令。
     */
    public void deleteByMacAndColor(String macAddress, String color) {
        if (jdbcTemplate == null || macAddress == null || macAddress.isBlank()) {
            return;
        }
        String normalizedColor = (color == null || color.isBlank()) ? "ALL" : color.trim().toUpperCase();
        try {
            int rows = jdbcTemplate.update(
                    "DELETE FROM t_led_pending_command WHERE mac_address = ? AND color = ?",
                    macAddress, normalizedColor);
            if (rows > 0) {
                log.info("LED pending command deleted after success: mac={}, color={}, rows={}", macAddress, normalizedColor, rows);
            }
        } catch (Exception exception) {
            log.warn("delete LED pending command failed: mac={}, color={}, error={}",
                    macAddress, normalizedColor, exception.getMessage());
        }
    }

    /**
     * 删除指定 mac 的所有待重试指令（设备重连后目标状态已确认时使用）。
     */
    public void deleteByMac(String macAddress) {
        if (jdbcTemplate == null || macAddress == null || macAddress.isBlank()) {
            return;
        }
        try {
            int rows = jdbcTemplate.update(
                    "DELETE FROM t_led_pending_command WHERE mac_address = ?",
                    macAddress);
            if (rows > 0) {
                log.info("LED pending commands deleted by mac: mac={}, rows={}", macAddress, rows);
            }
        } catch (Exception exception) {
            log.warn("delete LED pending commands by mac failed: mac={}, error={}", macAddress, exception.getMessage());
        }
    }

    /**
     * 查询指定 mac 的所有待重试指令，按 updated_at 升序（先入先重试）。
     */
    public List<LedPendingCommand> findPendingByMac(String macAddress) {
        if (jdbcTemplate == null || macAddress == null || macAddress.isBlank()) {
            return List.of();
        }
        try {
            return jdbcTemplate.query("""
                    SELECT mac_address, color, command, frame_hex, remote_address, retry_count
                      FROM t_led_pending_command
                     WHERE mac_address = ?
                     ORDER BY updated_at ASC
                    """,
                    (rs, rowNum) -> new LedPendingCommand(
                            rs.getString("mac_address"),
                            rs.getString("color"),
                            rs.getString("command"),
                            rs.getString("frame_hex"),
                            rs.getString("remote_address"),
                            rs.getInt("retry_count")),
                    macAddress);
        } catch (Exception exception) {
            log.warn("find LED pending commands by mac failed: mac={}, error={}", macAddress, exception.getMessage());
            return List.of();
        }
    }

    /**
     * 查询全部待重试指令（定时扫描兜底用）。
     */
    public List<LedPendingCommand> findAllPending() {
        if (jdbcTemplate == null) {
            return List.of();
        }
        try {
            return jdbcTemplate.query("""
                    SELECT mac_address, color, command, frame_hex, remote_address, retry_count
                      FROM t_led_pending_command
                     ORDER BY updated_at ASC
                    """,
                    (rs, rowNum) -> new LedPendingCommand(
                            rs.getString("mac_address"),
                            rs.getString("color"),
                            rs.getString("command"),
                            rs.getString("frame_hex"),
                            rs.getString("remote_address"),
                            rs.getInt("retry_count")));
        } catch (Exception exception) {
            log.warn("find all LED pending commands failed: error={}", exception.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 重试失败后递增计数并刷新远端地址。
     */
    public void incrementRetry(String macAddress, String color, String remoteAddress) {
        if (jdbcTemplate == null || macAddress == null || macAddress.isBlank()) {
            return;
        }
        String normalizedColor = (color == null || color.isBlank()) ? "ALL" : color.trim().toUpperCase();
        try {
            jdbcTemplate.update("""
                    UPDATE t_led_pending_command
                       SET retry_count = retry_count + 1,
                           remote_address = ?,
                           updated_at = CURRENT_TIMESTAMP
                     WHERE mac_address = ? AND color = ?
                    """, remoteAddress, macAddress, normalizedColor);
        } catch (Exception exception) {
            log.warn("increment LED pending command retry failed: mac={}, color={}, error={}",
                    macAddress, normalizedColor, exception.getMessage());
        }
    }

    /**
     * 待重试指令记录（值对象）。
     */
    public record LedPendingCommand(
            String macAddress,
            String color,
            String command,
            String frameHex,
            String remoteAddress,
            int retryCount
    ) {
    }
}
