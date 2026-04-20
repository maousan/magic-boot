package org.ssssssss.magicboot.zintis.led.transport;

import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.led.protocol.Crc16Modbus;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class LedTcpClientManager {

    private static final Crc16Modbus CRC16_MODBUS = new Crc16Modbus();

    private final ConcurrentMap<String, ConnectionHolder> holderMap = new ConcurrentHashMap<>();

    public byte[] sendAndReceive(String deviceIp, int devicePort, byte[] request, int timeoutMs) throws IOException {
        if (deviceIp == null || deviceIp.isBlank()) {
            throw new IllegalArgumentException("deviceIp 不能为空");
        }
        if (devicePort < 1 || devicePort > 65535) {
            throw new IllegalArgumentException("devicePort 必须在 1~65535 范围内");
        }
        if (request == null || request.length == 0) {
            throw new IllegalArgumentException("request 不能为空");
        }
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs 必须大于 0");
        }

        String key = key(deviceIp, devicePort);
        ConnectionHolder holder = holderMap.computeIfAbsent(key, ignored -> new ConnectionHolder(deviceIp, devicePort));

        holder.lock.lock();
        try {
            ensureConnected(holder, timeoutMs);
            holder.socket.setSoTimeout(timeoutMs);
            holder.outputStream.write(request);
            holder.outputStream.flush();
            return readResponse(holder.inputStream);
        } catch (IOException ex) {
            closeConnection(deviceIp, devicePort);
            throw ex;
        } finally {
            holder.lock.unlock();
        }
    }

    public boolean isConnected(String deviceIp, int devicePort) {
        ConnectionHolder holder = holderMap.get(key(deviceIp, devicePort));
        return holder != null && holder.socket != null && holder.socket.isConnected() && !holder.socket.isClosed();
    }

    public int getActiveConnectionCount() {
        int count = 0;
        for (ConnectionHolder holder : holderMap.values()) {
            if (holder.socket != null && holder.socket.isConnected() && !holder.socket.isClosed()) {
                count++;
            }
        }
        return count;
    }

    public void closeConnection(String deviceIp, int devicePort) {
        ConnectionHolder holder = holderMap.remove(key(deviceIp, devicePort));
        if (holder != null) {
            closeQuietly(holder);
        }
    }

    @PreDestroy
    public void closeAll() {
        for (Map.Entry<String, ConnectionHolder> entry : holderMap.entrySet()) {
            closeQuietly(entry.getValue());
        }
        holderMap.clear();
    }

    private void ensureConnected(ConnectionHolder holder, int timeoutMs) throws IOException {
        if (holder.socket != null && holder.socket.isConnected() && !holder.socket.isClosed()) {
            return;
        }

        closeQuietly(holder);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(holder.deviceIp, holder.devicePort), timeoutMs);
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);

        holder.socket = socket;
        holder.inputStream = socket.getInputStream();
        holder.outputStream = socket.getOutputStream();

        log.debug("LED device connected: {}:{}", holder.deviceIp, holder.devicePort);
    }

    private byte[] readResponse(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        while (true) {
            try {
                int value = inputStream.read();
                if (value < 0) {
                    throw new EOFException("设备连接已关闭");
                }
                buffer.write(value);
                byte[] current = buffer.toByteArray();
                if (isCompleteFrame(current)) {
                    return current;
                }
            } catch (SocketTimeoutException timeoutException) {
                byte[] current = buffer.toByteArray();
                if (isCompleteFrame(current)) {
                    return current;
                }
                throw timeoutException;
            }
        }
    }

    private boolean isCompleteFrame(byte[] frame) {
        return frame.length >= 5 && CRC16_MODBUS.isValidFrame(frame);
    }

    private void closeQuietly(ConnectionHolder holder) {
        if (holder.outputStream != null) {
            try {
                holder.outputStream.close();
            } catch (IOException ignored) {
            }
        }
        if (holder.inputStream != null) {
            try {
                holder.inputStream.close();
            } catch (IOException ignored) {
            }
        }
        if (holder.socket != null) {
            try {
                holder.socket.close();
            } catch (IOException ignored) {
            }
        }
        holder.socket = null;
        holder.inputStream = null;
        holder.outputStream = null;
    }

    private String key(String deviceIp, int devicePort) {
        return deviceIp + ":" + devicePort;
    }

    private static class ConnectionHolder {

        private final String deviceIp;
        private final int devicePort;
        private final ReentrantLock lock = new ReentrantLock();

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        private ConnectionHolder(String deviceIp, int devicePort) {
            this.deviceIp = deviceIp;
            this.devicePort = devicePort;
        }
    }
}