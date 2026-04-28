package org.ssssssss.magicboot.zintis.rfid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.config.RfidWebSocketProperties;
import org.ssssssss.magicboot.zintis.rfid.model.WsMessage;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ObjectMapper objectMapper;
    private final RfidWebSocketProperties properties;
    private final DataPersistenceService persistenceService;

    private final Map<String, Boolean> receivedMsgIds = new LruSet<>(100);
    private final ConcurrentHashMap<String, PendingAck> ackMap = new ConcurrentHashMap<>();
    private static final AtomicLong SEQ_GENERATOR = new AtomicLong(0);

    public String handleMessage(String text, String deviceId) {
        try {
            WsMessage msg = objectMapper.readValue(text, WsMessage.class);
            String type = msg.getType();

            switch (type) {
                case "data":
                    return handleData(msg, deviceId);
                case "ack":
                    handleAck(msg);
                    return null;
                case "disconnect":
                    return null;
                default:
                    return buildErrorMsg(msg.getMsgId(), "Unknown type: " + type);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse message: {}", e.getMessage());
            return buildErrorMsg(null, "Invalid JSON: " + e.getMessage());
        }
    }

    private String handleData(WsMessage msg, String deviceId) {
        if (msg.getMsgId() != null && !msg.getMsgId().isEmpty()) {
            synchronized (receivedMsgIds) {
                if (receivedMsgIds.containsKey(msg.getMsgId())) {
                    log.debug("Duplicate message, skipping: {}", msg.getMsgId());
                    return buildAckMsg(msg.getMsgId());
                }
                receivedMsgIds.put(msg.getMsgId(), Boolean.TRUE);
            }
        }

        String ack = buildAckMsg(msg.getMsgId());

        if (msg.getPayload() != null) {
            persistenceService.cacheRfidData(deviceId, msg.getPayload(), System.currentTimeMillis());
        }

        return ack;
    }

    private void handleAck(WsMessage msg) {
        PendingAck pending = ackMap.remove(msg.getMsgId());
        if (pending != null) {
            log.debug("ACK received for command: {}", msg.getMsgId());
        }
    }

    public String buildCommandMessage(String command, Object params) {
        String msgId = UUID.randomUUID().toString();
        long seq = nextSeq();
        WsMessage msg = new WsMessage();
        msg.setMsgId(msgId);
        msg.setSeq(seq);
        msg.setType("data");
        msg.setPayload(Map.of("command", command, "params", params != null ? params : Map.of()));

        String json;
        try {
            json = objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize command", e);
            return null;
        }

        scheduleAck(msgId, json);
        return json;
    }

    private void scheduleAck(String msgId, String json) {
        PendingAck pending = new PendingAck(json);
        ackMap.put(msgId, pending);
    }

    private String buildAckMsg(String msgId) {
        WsMessage ack = new WsMessage();
        ack.setMsgId(msgId);
        ack.setSeq(nextSeq());
        ack.setType("ack");
        try {
            return objectMapper.writeValueAsString(ack);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String buildErrorMsg(String msgId, String message) {
        WsMessage err = new WsMessage();
        err.setMsgId(msgId);
        err.setSeq(nextSeq());
        err.setType("error");
        err.setPayload(Map.of("code", 400, "message", message));
        try {
            return objectMapper.writeValueAsString(err);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private long nextSeq() {
        return SEQ_GENERATOR.incrementAndGet();
    }

    private static class LruSet<K> extends LinkedHashMap<K, Boolean> {
        private final int maxSize;

        LruSet(int maxSize) {
            super(maxSize, 0.75f, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, Boolean> eldest) {
            return size() > maxSize;
        }
    }

    private static class PendingAck {
        final String json;

        PendingAck(String json) {
            this.json = json;
        }
    }
}
