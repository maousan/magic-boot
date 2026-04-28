# Zintis RFID Netty WebSocket 插件 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 开发 PF4J 插件 `magic-plugin-zintis-rfid`，基于 Netty 实现 WebSocket 服务端，与 Android RFID 设备双向实时通讯。

**Architecture:** PF4J 插件（参考 `magic-plugin-zintis-led`），Netty WebSocket Server 嵌入 Spring Boot 容器，通过 `@PostConstruct` 自动启动。设备上报 RFID 数据经 Redis 缓存后异步落库 MySQL，外部系统通过 HTTP API 下发控制指令。

**Tech Stack:** Java 17, Netty, Spring Boot 3.1.2, PF4J, Redis, MySQL/MyBatis Plus, Lombok

---

## File Structure

| 文件 | 操作 | 职责 |
|------|------|------|
| `magic-plugin-zintis-rfid/pom.xml` | 新增 | Maven 模块配置 |
| `magic-plugin-zintis-rfid/src/main/resources/plugin.properties` | 新增 | PF4J 插件元数据 |
| `ZintisRfidPlugin.java` | 新增 | PF4J 插件入口 |
| `RfidWebSocketProperties.java` | 新增 | 配置属性 |
| `WsMessage.java` | 新增 | 消息协议模型 |
| `DeviceInfo.java` | 新增 | 设备信息 |
| `DeviceManager.java` | 新增 | 设备连接管理 |
| `MessageService.java` | 新增 | ACK、去重、seq 管理 |
| `WebSocketServerHandler.java` | 新增 | Netty WebSocket 消息处理 |
| `NettyWebSocketServer.java` | 新增 | Netty 服务启动/停止 |
| `DataPersistenceService.java` | 新增 | Redis 缓存 + MySQL 落库 |
| `RfidApiController.java` | 新增 | HTTP API |

---

### Task 1: 创建 Maven 模块骨架

**Files:**
- Create: `magic-plugin-zintis-rfid/pom.xml`
- Create: `magic-plugin-zintis-rfid/src/main/resources/plugin.properties`
- Create: `magic-plugin-zintis-rfid/src/main/java/org/ssssssss/magicboot/zintis/rfid/ZintisRfidPlugin.java`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.ssssssss</groupId>
        <artifactId>magic-boot</artifactId>
        <version>${revision}</version>
    </parent>

    <artifactId>magic-plugin-zintis-rfid</artifactId>
    <name>magic-plugin-zintis-rfid</name>
    <description>Zintis RFID WebSocket Plugin</description>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <maven.compiler.release>17</maven.compiler.release>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.ssssssss</groupId>
            <artifactId>magic-plugin-api</artifactId>
            <version>${revision}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.pf4j</groupId>
            <artifactId>pf4j</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.pf4j</groupId>
            <artifactId>pf4j-spring</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建 plugin.properties**

```properties
plugin.id=zintis-rfid-plugin
plugin.name=Zintis RFID WebSocket Plugin
plugin.description=RFID device communication via WebSocket
plugin.class=org.ssssssss.magicboot.zintis.rfid.ZintisRfidPlugin
plugin.version=1.0.0
plugin.provider=MagicBoot Team
```

- [ ] **Step 3: 创建 ZintisRfidPlugin.java**

```java
package org.ssssssss.magicboot.zintis.rfid;

import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.springframework.context.annotation.ComponentScan;

public class ZintisRfidPlugin extends SpringPlugin {

    public ZintisRfidPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("ZintisRfidPlugin started");
    }

    @Override
    public void stop() {
        log.info("ZintisRfidPlugin stopped");
    }

    @Override
    protected SpringPlugin createSpringPlugin() {
        return this;
    }

    @org.springframework.context.annotation.Configuration
    @ComponentScan("org.ssssssss.magicboot.zintis.rfid")
    public static class RfidPluginConfiguration {
    }
}
```

**注意**：PF4J Spring 插件的入口类需根据主应用的 `CustomSpringPluginManager` 实现调整。如果主应用使用的是 `pf4j-spring` 的标准集成，则 `ZintisRfidPlugin` 应继承 `Plugin` 而非 `SpringPlugin`，并通过 `@ComponentScan` 的方式让 Spring 扫描到。参考 zintis-led 插件的实际入口类模式。

- [ ] **Step 4: Commit**

```bash
git add magic-plugin-zintis-rfid/
git commit -m "feat: create magic-plugin-zintis-rfid Maven module skeleton"
```

---

### Task 2: 创建模型类

**Files:**
- Create: `model/WsMessage.java`
- Create: `model/DeviceInfo.java`

- [ ] **Step 1: 创建 WsMessage.java**

```java
package org.ssssssss.magicboot.zintis.rfid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsMessage {
    @JsonProperty("msgId")
    private String msgId;
    @JsonProperty("seq")
    private long seq;
    @JsonProperty("type")
    private String type;
    @JsonProperty("payload")
    private Object payload;
}
```

- [ ] **Step 2: 创建 DeviceInfo.java**

```java
package org.ssssssss.magicboot.zintis.rfid.model;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceInfo {
    private String deviceId;
    private Channel channel;
    private String remoteAddress;
    private long connectedAt;
    private volatile long lastActiveAt;
}
```

- [ ] **Step 3: Commit**

```bash
git add model/
git commit -m "feat: add WsMessage and DeviceInfo models"
```

---

### Task 3: 创建配置类

**Files:**
- Create: `config/RfidWebSocketProperties.java`

- [ ] **Step 1: 创建配置类**

```java
package org.ssssssss.magicboot.zintis.rfid.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rfid.websocket")
public class RfidWebSocketProperties {
    private int port = 9090;
    private int maxConnections = 20;
    private int maxFrameSize = 65536;
    private int idleTimeoutSeconds = 30;
    private int ackTimeoutMs = 5000;
    private int ackMaxRetries = 3;
    private int batchSize = 100;
    private int batchIntervalMs = 5000;
    private int dedupCacheSize = 100;
}
```

- [ ] **Step 2: Commit**

```bash
git add config/
git commit -m "feat: add RfidWebSocketProperties configuration"
```

---

### Task 4: 创建 DeviceManager

**Files:**
- Create: `service/DeviceManager.java`

- [ ] **Step 1: 创建设备管理器**

```java
package org.ssssssss.magicboot.zintis.rfid.service;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.model.DeviceInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
                .remoteAddress(channel.remoteAddress().toString())
                .connectedAt(System.currentTimeMillis())
                .lastActiveAt(System.currentTimeMillis())
                .build();
        devices.put(deviceId, info);
        log.info("Device registered: {} from {}", deviceId, channel.remoteAddress());

        flushPendingCommands(deviceId, channel);
    }

    public void unregister(String deviceId) {
        DeviceInfo removed = devices.remove(deviceId);
        if (removed != null) {
            log.info("Device unregistered: {}", deviceId);
        }
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
            info.getChannel().writeAndFlush(new TextWebSocketFrame(json));
        } else {
            cachePendingCommand(deviceId, json);
        }
    }

    public void broadcast(String json) {
        for (DeviceInfo info : devices.values()) {
            if (info.getChannel().isActive()) {
                info.getChannel().writeAndFlush(new TextWebSocketFrame(json));
            }
        }
    }

    public void sendDisconnectAll() {
        String disconnectMsg = "{\"type\":\"disconnect\"}";
        for (DeviceInfo info : devices.values()) {
            if (info.getChannel().isActive()) {
                info.getChannel().writeAndFlush(new TextWebSocketFrame(disconnectMsg));
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
                channel.writeAndFlush(new TextWebSocketFrame(json));
                log.info("Flushed pending command to device: {}", deviceId);
            }
        } catch (Exception e) {
            log.error("Failed to flush pending commands for {}: {}", deviceId, e.getMessage());
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add service/DeviceManager.java
git commit -m "feat: add DeviceManager for device connection management"
```

---

### Task 5: 创建 MessageService

**Files:**
- Create: `service/MessageService.java`

- [ ] **Step 1: 创建消息服务**

```java
package org.ssssssss.magicboot.zintis.rfid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.config.RfidWebSocketProperties;
import org.ssssssss.magicboot.zintis.rfid.model.WsMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;
    private final RfidWebSocketProperties properties;
    private final DataPersistenceService persistenceService;

    private final Map<String, Boolean> receivedMsgIds = new LruSet<>(100);
    private final ConcurrentHashMap<String, PendingAck> ackMap = new ConcurrentHashMap<>();

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
            persistenceService.cacheRfidData(deviceId, msg.getPayload().toString(), System.currentTimeMillis());
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
        Runnable timeoutRunnable = new Runnable() {
            int retryCount = 0;

            @Override
            public void run() {
                PendingAck p = ackMap.get(msgId);
                if (p == null) return;

                retryCount++;
                if (retryCount > properties.getAckMaxRetries()) {
                    ackMap.remove(msgId);
                    log.error("ACK failed after {} retries: {}", properties.getAckMaxRetries(), msgId);
                    return;
                }
                log.warn("ACK timeout, retry #{} for: {}", retryCount, msgId);
            }
        };

        PendingAck pending = new PendingAck(json, timeoutRunnable);
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

    private static final AtomicLong SEQ_GENERATOR = new AtomicLong(0);

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
        final Runnable timeoutRunnable;

        PendingAck(String json, Runnable timeoutRunnable) {
            this.json = json;
            this.timeoutRunnable = timeoutRunnable;
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add service/MessageService.java
git commit -m "feat: add MessageService with ACK, dedup and seq management"
```

---

### Task 6: 创建 WebSocketServerHandler

**Files:**
- Create: `server/WebSocketServerHandler.java`

- [ ] **Step 1: 创建 Netty WebSocket 处理器**

```java
package org.ssssssss.magicboot.zintis.rfid.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final DeviceManager deviceManager;
    private final MessageService messageService;
    private final int maxConnections;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete handshake) {
            String deviceId = extractDeviceId(handshake.requestUri());
            if (deviceId == null || deviceId.isEmpty()) {
                log.warn("No deviceId in WebSocket URL, closing connection");
                ctx.close();
                return;
            }

            if (deviceManager.getActiveCount() >= maxConnections) {
                log.warn("Max connections ({}) reached, rejecting device: {}", maxConnections, deviceId);
                ctx.close();
                return;
            }

            ctx.channel().attr(DEVICE_ID_KEY).set(deviceId);
            deviceManager.register(deviceId, ctx.channel());
            log.info("WebSocket handshake complete, device: {}", deviceId);
            return;
        }

        if (evt instanceof IdleStateEvent idleStateEvent
                && idleStateEvent.state() == IdleState.READER_IDLE) {
            String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
            log.warn("Read idle timeout for device: {}, closing", deviceId);
            ctx.close();
            return;
        }

        ctx.fireUserEventTriggered(evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String text = frame.text();
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        log.debug("Received from {}: {}", deviceId, text);

        deviceManager.updateActivity(deviceId);

        String reply = messageService.handleMessage(text, deviceId);
        if (reply != null) {
            ctx.writeAndFlush(new TextWebSocketFrame(reply));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        if (deviceId != null) {
            deviceManager.unregister(deviceId);
            log.info("Device disconnected: {}", deviceId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String deviceId = ctx.channel().attr(DEVICE_ID_KEY).get();
        log.error("Channel exception for device {}: {}", deviceId, cause.getMessage());
        ctx.close();
    }

    private String extractDeviceId(String requestUri) {
        if (requestUri == null) return null;
        int queryIndex = requestUri.indexOf('?');
        if (queryIndex < 0) return null;
        String query = requestUri.substring(queryIndex + 1);
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if ("deviceId".equals(kv[0]) && kv.length == 2) {
                return kv[1];
            }
        }
        return null;
    }

    private static final io.netty.util.AttributeKey<String> DEVICE_ID_KEY =
            io.netty.util.AttributeKey.valueOf("deviceId");
}
```

- [ ] **Step 2: Commit**

```bash
git add server/WebSocketServerHandler.java
git commit -m "feat: add WebSocketServerHandler with device tracking and idle detection"
```

---

### Task 7: 创建 NettyWebSocketServer

**Files:**
- Create: `server/NettyWebSocketServer.java`

- [ ] **Step 1: 创建 Netty 服务**

```java
package org.ssssssss.magicboot.zintis.rfid.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.config.RfidWebSocketProperties;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class NettyWebSocketServer {

    private final RfidWebSocketProperties properties;
    private final DeviceManager deviceManager;
    private final MessageService messageService;

    private final ReentrantLock lock = new ReentrantLock();
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int boundPort = -1;

    @PostConstruct
    public void autoStart() {
        start(properties.getPort());
    }

    @PreDestroy
    public void shutdown() {
        deviceManager.sendDisconnectAll();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        stop();
    }

    public void start(int port) {
        lock.lock();
        try {
            if (isRunning()) {
                log.info("WebSocket server already running on port {}", boundPort);
                return;
            }

            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(properties.getMaxFrameSize()))
                                    .addLast(new IdleStateHandler(properties.getIdleTimeoutSeconds(), 0, 0))
                                    .addLast(new WebSocketServerProtocolHandler("/ws", null, true, properties.getMaxFrameSize()))
                                    .addLast(new WebSocketServerHandler(deviceManager, messageService, properties.getMaxConnections()));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind(port).syncUninterruptibly();
            serverChannel = future.channel();
            boundPort = ((InetSocketAddress) serverChannel.localAddress()).getPort();
            log.info("RFID WebSocket server started on port {}", boundPort);
        } catch (Exception e) {
            log.error("Failed to start WebSocket server: {}", e.getMessage(), e);
            safeStopInternal();
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        lock.lock();
        try {
            if (!isRunning()) return;
            safeStopInternal();
            log.info("RFID WebSocket server stopped");
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return serverChannel != null && serverChannel.isOpen() && serverChannel.isActive();
    }

    public int getBoundPort() {
        return boundPort;
    }

    private void safeStopInternal() {
        if (serverChannel != null) {
            try {
                serverChannel.close().syncUninterruptibly();
            } catch (Exception ignored) {
            }
            serverChannel = null;
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
            workerGroup = null;
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
            bossGroup = null;
        }
        boundPort = -1;
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add server/NettyWebSocketServer.java
git commit -m "feat: add NettyWebSocketServer with lifecycle management"
```

---

### Task 8: 创建 DataPersistenceService

**Files:**
- Create: `service/DataPersistenceService.java`

- [ ] **Step 1: 创建数据持久化服务**

```java
package org.ssssssss.magicboot.zintis.rfid.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.rfid.config.RfidWebSocketProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
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
        List<String> batch = new ArrayList<>();
        while (batch.size() < properties.getBatchSize()) {
            String data = redisTemplate.opsForZSet().rangeAndRemove(key, 0, 0) != null
                    ? String.valueOf(redisTemplate.opsForZSet().range(key, 0, 0))
                    : null;
            if (data == null) break;
            batch.add(data);
        }

        if (batch.isEmpty()) return;

        try {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO rfid_record (device_id, epc, rssi, read_time) VALUES (?, ?, ?, ?)",
                    batch.stream().map(data -> {
                        try {
                            Map map = objectMapper.readValue(data, Map.class);
                            return new Object[]{
                                    deviceId,
                                    map.get("epc"),
                                    map.get("rssi"),
                                    new java.sql.Timestamp(Long.parseLong(String.valueOf(map.get("timestamp"))))
                            };
                        } catch (Exception e) {
                            return null;
                        }
                    }).filter(java.util.Objects::nonNull).toList()
            );
            log.debug("Flushed {} RFID records for device {}", batch.size(), deviceId);
        } catch (Exception e) {
            log.error("Failed to flush RFID data for {}: {}", deviceId, e.getMessage());
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add service/DataPersistenceService.java
git commit -m "feat: add DataPersistenceService with Redis caching and MySQL batch flush"
```

---

### Task 9: 创建 RfidApiController

**Files:**
- Create: `controller/RfidApiController.java`

- [ ] **Step 1: 创建 HTTP API 控制器**

```java
package org.ssssssss.magicboot.zintis.rfid.controller;

import lombok.RequiredArgsConstructor;
import org.ssssssss.magicboot.zintis.rfid.model.DeviceInfo;
import org.ssssssss.magicboot.zintis.rfid.server.NettyWebSocketServer;
import org.ssssssss.magicboot.zintis.rfid.service.DeviceManager;
import org.ssssssss.magicboot.zintis.rfid.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rfid")
@RequiredArgsConstructor
public class RfidApiController {

    private final NettyWebSocketServer server;
    private final DeviceManager deviceManager;
    private final MessageService messageService;

    @PostMapping("/command")
    public Map<String, Object> sendCommand(@RequestBody Map<String, Object> request) {
        String deviceId = (String) request.get("deviceId");
        String command = (String) request.get("command");
        Object params = request.get("params");

        DeviceInfo device = deviceManager.getDevice(deviceId);
        boolean online = device != null && device.getChannel().isActive();

        String json = messageService.buildCommandMessage(command, params);
        if (json == null) {
            return Map.of("success", false, "status", "error", "message", "Failed to build command");
        }

        if (online) {
            deviceManager.sendTo(deviceId, json);
            return Map.of(
                    "success", true,
                    "msgId", extractMsgId(json),
                    "status", "delivered",
                    "message", "指令已送达设备"
            );
        } else {
            deviceManager.sendTo(deviceId, json);
            return Map.of(
                    "success", true,
                    "msgId", extractMsgId(json),
                    "status", "pending",
                    "message", "设备离线，指令已缓存"
            );
        }
    }

    @PostMapping("/broadcast")
    public Map<String, Object> broadcast(@RequestBody Map<String, Object> request) {
        String command = (String) request.get("command");
        Object params = request.get("params");

        String json = messageService.buildCommandMessage(command, params);
        if (json == null) {
            return Map.of("success", false, "message", "Failed to build command");
        }

        List<DeviceInfo> devices = deviceManager.getAllDevices();
        int total = devices.size();
        int success = 0;
        for (DeviceInfo device : devices) {
            if (device.getChannel().isActive()) {
                deviceManager.sendTo(device.getDeviceId(), json);
                success++;
            }
        }

        return Map.of(
                "success", success > 0,
                "totalTargets", total,
                "successCount", success,
                "failedCount", total - success,
                "failedTargets", List.of()
        );
    }

    @GetMapping("/devices")
    public Map<String, Object> listDevices() {
        List<Map<String, Object>> deviceList = deviceManager.getAllDevices().stream()
                .map(d -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("deviceId", d.getDeviceId());
                    m.put("remoteAddress", d.getRemoteAddress());
                    m.put("connectedAt", d.getConnectedAt());
                    m.put("lastActiveAt", d.getLastActiveAt());
                    return m;
                })
                .collect(Collectors.toList());

        return Map.of(
                "running", server.isRunning(),
                "devices", deviceList
        );
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "running", server.isRunning(),
                "port", server.isRunning() ? server.getBoundPort() : -1,
                "activeConnections", deviceManager.getActiveCount()
        );
    }

    private String extractMsgId(String json) {
        try {
            int start = json.indexOf("\"msgId\":\"") + 9;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add controller/RfidApiController.java
git commit -m "feat: add RfidApiController with command, broadcast, devices and status APIs"
```

---

### Task 10: 编译验证

- [ ] **Step 1: 在父 pom.xml 中注册模块**

在 `D:\IdeaProjects\magic-boot\pom.xml` 的 `<modules>` 中添加：

```xml
<module>magic-plugin-zintis-rfid</module>
```

- [ ] **Step 2: 编译**

```bash
cd D:\IdeaProjects\magic-boot
mvn compile -pl magic-plugin-zintis-rfid -am
```

Expected: `BUILD SUCCESS`

- [ ] **Step 3: 打包并部署**

```bash
mvn package -pl magic-plugin-zintis-rfid -am -DskipTests
cp magic-plugin-zintis-rfid/target/magic-plugin-zintis-rfid.jar plugins/
```

- [ ] **Step 4: Commit**

```bash
git add magic-plugin-zintis-rfid/ pom.xml
git commit -m "feat: complete magic-plugin-zintis-rfid plugin implementation"
```
