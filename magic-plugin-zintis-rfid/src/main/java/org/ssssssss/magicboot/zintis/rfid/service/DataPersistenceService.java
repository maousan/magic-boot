package org.ssssssss.magicboot.zintis.rfid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.config.RfidWebSocketProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPersistenceService {

    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final RfidWebSocketProperties properties;

    public void cacheRfidData(String deviceId, String payload, long timestamp) {
        try {
            String data = objectMapper.writeValueAsString(Map.of(
                    "deviceId", deviceId,
                    "payload", payload,
                    "timestamp", timestamp
            ));
            redisTemplate.opsForZSet().add("rfid:pending:" + deviceId, data, timestamp);
        } catch (Exception e) {
            log.error("Failed to cache RFID data for {}: {}", deviceId, e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${rfid.websocket.batchIntervalMs:5000}")
    public void flushToMySQL() {
        Set<String> keys = redisTemplate.keys("rfid:pending:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String deviceId = key.substring("rfid:pending:".length());
            flushDeviceBatch(key, deviceId);
        }
    }

    private void flushDeviceBatch(String key, String deviceId) {
        int batchSize = properties.getBatchSize();
        for (int i = 0; i < batchSize; i++) {
            var scoredValues = redisTemplate.opsForZSet().rangeWithScores(key, 0, 0);
            if (scoredValues == null || scoredValues.isEmpty()) break;

            var entry = scoredValues.iterator().next();
            String data = entry.getValue();
            double score = entry.getScore();

            redisTemplate.opsForZSet().removeRange(key, 0, 0);

            try {
                Map map = objectMapper.readValue(data, Map.class);
                jdbcTemplate.update(
                        "INSERT INTO rfid_record (device_id, epc, rssi, read_time) VALUES (?, ?, ?, ?)",
                        deviceId,
                        map.get("epc") != null ? map.get("epc") : "unknown",
                        map.get("rssi") != null ? map.get("rssi") : 0,
                        new java.sql.Timestamp((long) score)
                );
            } catch (Exception e) {
                log.error("Failed to persist RFID record for {}: {}", deviceId, e.getMessage());
            }
        }
    }
}
