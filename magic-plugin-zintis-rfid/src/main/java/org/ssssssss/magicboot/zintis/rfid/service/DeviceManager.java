package org.ssssssss.magicboot.zintis.rfid.service;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.model.DeviceInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceManager {

    private final Map<String, DeviceInfo> devices = new ConcurrentHashMap<>();
    private final StringRedisTemplate redisTemplate;

    public void register(String deviceId, Channel channel) {
        DeviceInfo existing = devices.get(deviceId);
        if (existing != null && existing.getChannel().isActive()) {
            log.warn("Device {} already connected, closing old channel", deviceId);
            existing.getChannel().close();
        }

        DeviceInfo info = DeviceInfo.builder()
                .deviceId(deviceId)
                .channel(channel)
                .remoteAddress(formatRemoteAddress(channel))
                .connectedAt(System.currentTimeMillis())
                .lastActiveAt(System.currentTimeMillis())
                .build();
        devices.put(deviceId, info);
        log.info("Device registered: {} from {}", deviceId, channel.remoteAddress());

        flushPendingCommands(deviceId, channel);
    }

    public boolean unregister(String deviceId, Channel channel) {
        DeviceInfo existing = devices.get(deviceId);
        if (existing != null && existing.getChannel() == channel && devices.remove(deviceId, existing)) {
            log.info("Device unregistered: {}", deviceId);
            return true;
        }
        return false;
    }

    public void updateActivity(String deviceId) {
        DeviceInfo info = devices.get(deviceId);
        if (info != null) {
            info.setLastActiveAt(System.currentTimeMillis());
        }
    }

    public DeviceInfo getDevice(String deviceId) {
        return devices.get(deviceId);
    }

    public List<DeviceInfo> getAllDevices() {
        return new ArrayList<>(devices.values());
    }

    public int getActiveCount() {
        return devices.size();
    }

    public void sendTo(String deviceId, String json) {
        DeviceInfo info = devices.get(deviceId);
        if (info != null && info.getChannel().isActive()) {
            info.getChannel().writeAndFlush(json);
        } else {
            cachePendingCommand(deviceId, json);
        }
    }

    public void broadcast(String json) {
        for (DeviceInfo info : devices.values()) {
            if (info.getChannel().isActive()) {
                info.getChannel().writeAndFlush(json);
            }
        }
    }

    public void sendDisconnectAll() {
        String disconnectMsg = "{\"type\":\"disconnect\"}";
        for (DeviceInfo info : devices.values()) {
            if (info.getChannel().isActive()) {
                info.getChannel().writeAndFlush(disconnectMsg);
            }
        }
    }

    private void cachePendingCommand(String deviceId, String json) {
        try {
            redisTemplate.opsForList().rightPush("cmd:pending:" + deviceId, json);
            log.info("Cached pending command for offline device: {}", deviceId);
        } catch (Exception e) {
            log.error("Failed to cache pending command for {}: {}", deviceId, e.getMessage());
        }
    }

    private void flushPendingCommands(String deviceId, Channel channel) {
        try {
            String key = "cmd:pending:" + deviceId;
            while (true) {
                String json = redisTemplate.opsForList().leftPop(key);
                if (json == null) break;
                channel.writeAndFlush(json);
                log.info("Flushed pending command to device: {}", deviceId);
            }
        } catch (Exception e) {
            log.error("Failed to flush pending commands for {}: {}", deviceId, e.getMessage());
        }
    }

    private String formatRemoteAddress(Channel channel) {
        if (channel.remoteAddress() instanceof InetSocketAddress address) {
            String host = address.getAddress() != null
                    ? address.getAddress().getHostAddress()
                    : address.getHostString();
            return host + ":" + address.getPort();
        }
        return String.valueOf(channel.remoteAddress());
    }
}
