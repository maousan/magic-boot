package org.ssssssss.magicboot.zintis.led.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ssssssss.magicboot.zintis.led.dto.LedControlRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedControlResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedQueryResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSignalStrengthResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemInfoResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemNetworkResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedOtaUpdateRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedTcpConfigRequest;
import org.ssssssss.magicboot.zintis.led.protocol.Crc16Modbus;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;
import org.ssssssss.magicboot.zintis.led.protocol.LedProtocolCodec;
import org.ssssssss.magicboot.zintis.led.transport.LedTcpClientManager;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LedControlServiceTest {

    @Mock
    private LedTcpClientManager tcpClientManager;

    private LedControlService ledControlService;
    private LedProtocolCodec codec;

    @BeforeEach
    void setUp() {
        codec = new LedProtocolCodec(new Crc16Modbus());
        ledControlService = new LedControlService(codec, tcpClientManager);
    }

    @Test
    void turnOn_shouldReturnSuccess_whenDeviceRespondsNormally() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildControlFrame(0xEA, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS));

        LedControlResponse response = ledControlService.turnOn(baseRequest());

        assertTrue(response.isSuccess());
        assertEquals("Turn on success", response.getMessage());
    }

    @Test
    void turnOff_shouldReturnDeviceError_whenDeviceReturns0x0E() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildControlFrame(0xEA, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_ERROR));

        LedControlResponse response = ledControlService.turnOff(baseRequest());

        assertFalse(response.isSuccess());
        assertEquals("DEVICE_ERROR", response.getErrorCode());
    }

    @Test
    void pulse_shouldReturnTimeout_whenSocketTimeout() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenThrow(new SocketTimeoutException("read timed out"));

        LedControlResponse response = ledControlService.pulse(baseRequest());

        assertFalse(response.isSuccess());
        assertEquals("TIMEOUT", response.getErrorCode());
    }

    @Test
    void query_shouldReturnPayloadHex() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xF5, new byte[]{0x01, 0x02}));

        LedQueryResponse response = ledControlService.query(baseRequest());

        assertTrue(response.isSuccess());
        assertEquals("0x01 0x02", response.getPayloadHex());
    }

    @Test
    void query_shouldReturnCrcError_whenFrameInvalid() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05});

        LedQueryResponse response = ledControlService.query(baseRequest());

        assertFalse(response.isSuccess());
        assertEquals("CRC_ERROR", response.getErrorCode());
    }

    @Test
    void querySystemInfo_shouldReturnAsciiPayload() throws Exception {
        byte[] payload = "SYS-V1.0".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xF5, payload));

        LedSystemInfoResponse response = ledControlService.querySystemInfo(baseRequest());

        assertTrue(response.isSuccess());
        assertEquals("0x53 0x59 0x53 0x2D 0x56 0x31 0x2E 0x30", response.getPayloadHex());
        assertEquals("SYS-V1.0", response.getPayloadAscii());
    }

    @Test
    void querySignalStrength_shouldParseAsciiRssi() throws Exception {
        byte[] payload = "RSSI:-67dBm".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xCF, payload));

        LedSignalStrengthResponse response = ledControlService.querySignalStrength(baseRequest());

        assertTrue(response.isSuccess());
        assertEquals(Integer.valueOf(-67), response.getSignalStrengthDbm());
    }

    @Test
    void querySignalStrength_shouldReturnSignalNotFound_whenNoRssiInPayload() throws Exception {
        byte[] payload = "SYS-V1.0".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xCF, payload));

        LedSignalStrengthResponse response = ledControlService.querySignalStrength(baseRequest());

        assertFalse(response.isSuccess());
        assertEquals("SIGNAL_NOT_FOUND", response.getErrorCode());
        assertNull(response.getSignalStrengthDbm());
    }

    @Test
    void querySystemNetwork_shouldParseIpAndMac() throws Exception {
        byte[] payload = "IP:192.168.2.198,MAC:AA-BB-CC-DD-EE-FF".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xCF, payload));

        LedSystemNetworkResponse response = ledControlService.querySystemNetwork(baseRequest());

        assertTrue(response.isSuccess());
        assertEquals("192.168.2.198", response.getSystemIp());
        assertEquals("AA:BB:CC:DD:EE:FF", response.getSystemMac());
    }

    @Test
    void querySystemNetwork_shouldParseLengthPrefixedMacAndIp() throws Exception {
        byte[] payload = new byte[]{
                0x11, 0x0D,
                0x33, 0x41, 0x3A, 0x36, 0x39, 0x3A, 0x37, 0x41, 0x3A, 0x30, 0x38, 0x3A, 0x44, 0x30, 0x3A, 0x41, 0x35,
                0x31, 0x39, 0x32, 0x2E, 0x31, 0x36, 0x38, 0x2E, 0x32, 0x2E, 0x31, 0x39, 0x38
        };
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildFrame(0xEA, LedCommandConstants.CMD_QUERY, (byte) 0xCF, payload));

        LedSystemNetworkResponse response = ledControlService.querySystemNetwork(baseRequest());

        assertTrue(response.isSuccess());
        assertEquals("192.168.2.198", response.getSystemIp());
        assertEquals("3A:69:7A:08:D0:A5", response.getSystemMac());
        assertEquals("MAC:3A:69:7A:08:D0:A5, IP:192.168.2.198", response.getPayloadAscii());
    }

    @Test
    void openTcpServer_shouldUseTcpConfigCommandAndOpenServerSubCommand() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildControlFrame(198, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS));

        LedControlResponse response = ledControlService.openTcpServer(baseTcpConfigRequest());

        assertTrue(response.isSuccess());
        ArgumentCaptor<byte[]> frameCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(tcpClientManager).sendAndReceive(anyString(), anyInt(), frameCaptor.capture(), anyInt());
        LedProtocolCodec.ParsedFrame parsedFrame = codec.parseFrame(frameCaptor.getValue());
        assertEquals(198, parsedFrame.getHostAddress());
        assertEquals(LedCommandConstants.CMD_TCP_CONFIG & 0xFF, parsedFrame.getControlCommand());
        assertEquals(LedCommandConstants.SUB_OPEN_TCP_SERVER & 0xFF, parsedFrame.getDataCommand());
    }

    @Test
    void closeTcpServer_shouldUseCloseServerSubCommand() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildControlFrame(198, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS));

        LedControlResponse response = ledControlService.closeTcpServer(baseTcpConfigRequest());

        assertTrue(response.isSuccess());
        ArgumentCaptor<byte[]> frameCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(tcpClientManager).sendAndReceive(anyString(), anyInt(), frameCaptor.capture(), anyInt());
        LedProtocolCodec.ParsedFrame parsedFrame = codec.parseFrame(frameCaptor.getValue());
        assertEquals(LedCommandConstants.SUB_CLOSE_TCP_SERVER & 0xFF, parsedFrame.getDataCommand());
    }

    @Test
    void openTcpClient_shouldEncodeTargetIpAndPortInPayload() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildControlFrame(198, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS));

        LedTcpConfigRequest request = baseTcpConfigRequest();
        request.setTargetIp("192.168.2.198");
        request.setTargetPort(9527);

        LedControlResponse response = ledControlService.openTcpClient(request);

        assertTrue(response.isSuccess());
        ArgumentCaptor<byte[]> frameCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(tcpClientManager).sendAndReceive(anyString(), anyInt(), frameCaptor.capture(), anyInt());
        LedProtocolCodec.ParsedFrame parsedFrame = codec.parseFrame(frameCaptor.getValue());
        assertEquals(LedCommandConstants.SUB_OPEN_TCP_CLIENT & 0xFF, parsedFrame.getDataCommand());
        assertArrayEquals("192.168.2.198:9527".getBytes(java.nio.charset.StandardCharsets.US_ASCII), parsedFrame.getPayload());
    }

    @Test
    void closeTcpClient_shouldUseCloseClientSubCommand() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildControlFrame(198, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS));

        LedControlResponse response = ledControlService.closeTcpClient(baseTcpConfigRequest());

        assertTrue(response.isSuccess());
        ArgumentCaptor<byte[]> frameCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(tcpClientManager).sendAndReceive(anyString(), anyInt(), frameCaptor.capture(), anyInt());
        LedProtocolCodec.ParsedFrame parsedFrame = codec.parseFrame(frameCaptor.getValue());
        assertEquals(LedCommandConstants.SUB_CLOSE_TCP_CLIENT & 0xFF, parsedFrame.getDataCommand());
    }

    @Test
    void otaUpdate_shouldEncodeVersionAsAsciiPayload() throws Exception {
        when(tcpClientManager.sendAndReceive(anyString(), anyInt(), any(), anyInt()))
                .thenReturn(codec.buildControlFrame(0xEA, LedCommandConstants.CMD_QUERY, LedCommandConstants.RESP_SUCCESS));

        LedControlResponse response = ledControlService.otaUpdate(baseOtaUpdateRequest());

        assertTrue(response.isSuccess());
        ArgumentCaptor<byte[]> frameCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(tcpClientManager).sendAndReceive(anyString(), anyInt(), frameCaptor.capture(), anyInt());
        byte[] frame = frameCaptor.getValue();
        assertEquals("0xEA 0x9F 0x56 0x30 0x2E 0x32 0x2E 0x31 0x2E 0x62 0x69 0x6E 0x3A 0x68",
                LedProtocolCodec.toPrefixedHex(frame));
        LedProtocolCodec.ParsedFrame parsedFrame = codec.parseFrame(frame);
        assertEquals(0xEA, parsedFrame.getHostAddress());
        assertEquals(LedCommandConstants.CMD_OTA & 0xFF, parsedFrame.getControlCommand());
        assertEquals('V', parsedFrame.getDataCommand());
        assertArrayEquals("0.2.1.bin".getBytes(java.nio.charset.StandardCharsets.US_ASCII), parsedFrame.getPayload());
    }

    private LedControlRequest baseRequest() {
        LedControlRequest request = new LedControlRequest();
        request.setDeviceIp("127.0.0.1");
        request.setDevicePort(9527);
        request.setHostAddress(0xEA);
        request.setDataCommand(0xF5);
        request.setTimeoutMs(2000);
        return request;
    }

    private LedTcpConfigRequest baseTcpConfigRequest() {
        LedTcpConfigRequest request = new LedTcpConfigRequest();
        request.setDeviceIp("192.168.2.198");
        request.setDevicePort(9527);
        request.setHostAddress(198);
        request.setTimeoutMs(2000);
        return request;
    }

    private LedOtaUpdateRequest baseOtaUpdateRequest() {
        LedOtaUpdateRequest request = new LedOtaUpdateRequest();
        request.setDeviceIp("192.168.2.198");
        request.setDevicePort(9527);
        request.setHostAddress(0xEA);
        request.setVersion("1.0.7.bin");
        request.setTimeoutMs(2000);
        return request;
    }
}
