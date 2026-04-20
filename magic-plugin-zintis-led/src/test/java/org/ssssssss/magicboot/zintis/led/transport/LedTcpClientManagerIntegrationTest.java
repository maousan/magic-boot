package org.ssssssss.magicboot.zintis.led.transport;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ssssssss.magicboot.zintis.led.protocol.Crc16Modbus;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;
import org.ssssssss.magicboot.zintis.led.protocol.LedProtocolCodec;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LedTcpClientManagerIntegrationTest {

    private final LedProtocolCodec codec = new LedProtocolCodec(new Crc16Modbus());
    private final LedTcpClientManager manager = new LedTcpClientManager();

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private int port;

    @BeforeEach
    void setUp() throws Exception {
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try (Socket socket = serverSocket.accept()) {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                byte[] request = in.readNBytes(5);
                LedProtocolCodec.ParsedFrame parsed = codec.parseFrame(request);
                byte[] response = codec.buildFrame(
                        parsed.getHostAddress(),
                        LedCommandConstants.CMD_QUERY,
                        LedCommandConstants.RESP_SUCCESS,
                        new byte[]{0x01});
                out.write(response);
                out.flush();
            } catch (Exception ignored) {
            }
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        manager.closeAll();
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    @Test
    void sendAndReceive_shouldWorkWithMockTcpServer() throws Exception {
        byte[] request = codec.buildControlFrame(0xEA, LedCommandConstants.CMD_CIRCUIT_ON, (byte) 0xF5);

        byte[] response = manager.sendAndReceive("127.0.0.1", port, request, 3000);

        LedProtocolCodec.ParsedFrame parsed = codec.parseFrame(response);
        assertEquals(LedCommandConstants.RESP_SUCCESS & 0xFF, parsed.getDataCommand());
        assertTrue(manager.isConnected("127.0.0.1", port));
    }
}