package org.ssssssss.magicboot.zintis.led.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ssssssss.magicboot.zintis.led.dto.LedControlRequest;
import org.ssssssss.magicboot.zintis.led.dto.LedControlResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedQueryResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSignalStrengthResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemInfoResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedSystemNetworkResponse;
import org.ssssssss.magicboot.zintis.led.dto.LedTcpConfigRequest;
import org.ssssssss.magicboot.zintis.led.protocol.LedCommandConstants;
import org.ssssssss.magicboot.zintis.led.protocol.LedProtocolCodec;
import org.ssssssss.magicboot.zintis.led.transport.LedTcpClientManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedControlService {

    private static final Pattern RSSI_PATTERN = Pattern.compile("(?i)RSSI\\s*[:=]?\\s*(-?\\d{1,3})|(-?\\d{1,3})\\s*dBm");
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "\\b(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}\\b"
    );
    private static final Pattern MAC_PATTERN = Pattern.compile("\\b([0-9A-F]{2}([-:])){5}[0-9A-F]{2}\\b|\\b[0-9A-F]{12}\\b", Pattern.CASE_INSENSITIVE);

    private final LedProtocolCodec protocolCodec;
    private final LedTcpClientManager tcpClientManager;

    public LedControlResponse turnOn(LedControlRequest request) {
        return executeControl(request, LedCommandConstants.CMD_CIRCUIT_ON, "Turn on");
    }

    public LedControlResponse turnOff(LedControlRequest request) {
        return executeControl(request, LedCommandConstants.CMD_CIRCUIT_OFF, "Turn off");
    }

    public LedControlResponse pulse(LedControlRequest request) {
        return executeControl(request, LedCommandConstants.CMD_MOMENTARY, "Pulse");
    }

    public LedControlResponse openTcpServer(LedTcpConfigRequest request) {
        return executeTcpConfig(request, LedCommandConstants.SUB_OPEN_TCP_SERVER, null, "Open TCP server");
    }

    public LedControlResponse closeTcpServer(LedTcpConfigRequest request) {
        return executeTcpConfig(request, LedCommandConstants.SUB_CLOSE_TCP_SERVER, null, "Close TCP server");
    }

    public LedControlResponse openTcpClient(LedTcpConfigRequest request) {
        byte[] payload = buildTargetAddressPayload(request.getTargetIp(), request.getTargetPort());
        return executeTcpConfig(request, LedCommandConstants.SUB_OPEN_TCP_CLIENT, payload, "Open TCP client");
    }

    public LedControlResponse closeTcpClient(LedTcpConfigRequest request) {
        return executeTcpConfig(request, LedCommandConstants.SUB_CLOSE_TCP_CLIENT, null, "Close TCP client");
    }

    public LedQueryResponse query(LedControlRequest request) {
        byte[] frame = protocolCodec.buildControlFrame(
                request.getHostAddress(),
                LedCommandConstants.CMD_QUERY,
                request.getDataCommand().byteValue());
        String requestHex = LedProtocolCodec.toPrefixedHex(frame);

        try {
            byte[] response = tcpClientManager.sendAndReceive(
                    request.getDeviceIp(),
                    request.getDevicePort(),
                    frame,
                    request.getTimeoutMs());

            LedProtocolCodec.ParsedFrame parsed = protocolCodec.parseFrame(response);
            boolean deviceError = parsed.getDataCommand() == (LedCommandConstants.RESP_ERROR & 0xFF);
            return LedQueryResponse.builder()
                    .success(!deviceError)
                    .message(deviceError ? "Device returned error code" : "Query success")
                    .errorCode(deviceError ? "DEVICE_ERROR" : null)
                    .hostAddress(parsed.getHostAddress())
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                    .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                    .payloadHex(LedProtocolCodec.toPrefixedHex(parsed.getPayload()))
                    .rawRequestHex(requestHex)
                    .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                    .build();
        } catch (SocketTimeoutException exception) {
            return LedQueryResponse.builder()
                    .success(false)
                    .message("Request timeout")
                    .errorCode("TIMEOUT")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (ConnectException | UnknownHostException exception) {
            return LedQueryResponse.builder()
                    .success(false)
                    .message("Connection failed")
                    .errorCode("CONNECTION_FAILED")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IllegalArgumentException exception) {
            String errorCode = exception.getMessage() != null && exception.getMessage().contains("CRC")
                    ? "CRC_ERROR" : "PROTOCOL_ERROR";
            return LedQueryResponse.builder()
                    .success(false)
                    .message(exception.getMessage())
                    .errorCode(errorCode)
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IOException exception) {
            log.warn("query io error: {}", exception.getMessage());
            return LedQueryResponse.builder()
                    .success(false)
                    .message("Network IO error")
                    .errorCode("IO_ERROR")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        }
    }

    public LedSystemInfoResponse querySystemInfo(LedControlRequest request) {
        byte[] frame = protocolCodec.buildControlFrame(
                request.getHostAddress(),
                LedCommandConstants.CMD_QUERY,
                request.getDataCommand().byteValue());
        String requestHex = LedProtocolCodec.toPrefixedHex(frame);

        try {
            byte[] response = tcpClientManager.sendAndReceive(
                    request.getDeviceIp(),
                    request.getDevicePort(),
                    frame,
                    request.getTimeoutMs());

            LedProtocolCodec.ParsedFrame parsed = protocolCodec.parseFrame(response);
            boolean deviceError = parsed.getDataCommand() == (LedCommandConstants.RESP_ERROR & 0xFF);
            return LedSystemInfoResponse.builder()
                    .success(!deviceError)
                    .message(deviceError ? "Device returned error code" : "System info query success")
                    .errorCode(deviceError ? "DEVICE_ERROR" : null)
                    .hostAddress(parsed.getHostAddress())
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                    .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                    .payloadHex(LedProtocolCodec.toPrefixedHex(parsed.getPayload()))
                    .payloadAscii(toAscii(parsed.getPayload()))
                    .rawRequestHex(requestHex)
                    .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                    .build();
        } catch (SocketTimeoutException exception) {
            return LedSystemInfoResponse.builder()
                    .success(false)
                    .message("Request timeout")
                    .errorCode("TIMEOUT")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (ConnectException | UnknownHostException exception) {
            return LedSystemInfoResponse.builder()
                    .success(false)
                    .message("Connection failed")
                    .errorCode("CONNECTION_FAILED")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IllegalArgumentException exception) {
            String errorCode = exception.getMessage() != null && exception.getMessage().contains("CRC")
                    ? "CRC_ERROR" : "PROTOCOL_ERROR";
            return LedSystemInfoResponse.builder()
                    .success(false)
                    .message(exception.getMessage())
                    .errorCode(errorCode)
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IOException exception) {
            log.warn("system info io error: {}", exception.getMessage());
            return LedSystemInfoResponse.builder()
                    .success(false)
                    .message("Network IO error")
                    .errorCode("IO_ERROR")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        }
    }

    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("plugin", "zintis-led-plugin");
        result.put("activeConnections", tcpClientManager.getActiveConnectionCount());
        return result;
    }

    private LedControlResponse executeControl(LedControlRequest request, byte controlCommand, String actionName) {
        byte[] frame = protocolCodec.buildControlFrame(
                request.getHostAddress(),
                controlCommand,
                request.getDataCommand().byteValue());
        String requestHex = LedProtocolCodec.toPrefixedHex(frame);

        try {
            byte[] response = tcpClientManager.sendAndReceive(
                    request.getDeviceIp(),
                    request.getDevicePort(),
                    frame,
                    request.getTimeoutMs());
            LedProtocolCodec.ParsedFrame parsed = protocolCodec.parseFrame(response);

            boolean deviceError = parsed.getDataCommand() == (LedCommandConstants.RESP_ERROR & 0xFF);
            if (deviceError) {
                return LedControlResponse.builder()
                        .success(false)
                        .message("Device returned error code")
                        .errorCode("DEVICE_ERROR")
                        .hostAddress(parsed.getHostAddress())
                        .controlCommand(formatByteHex(controlCommand))
                        .dataCommand(formatCommandHex(request.getDataCommand()))
                        .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                        .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                        .rawRequestHex(requestHex)
                        .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                        .build();
            }

            return LedControlResponse.builder()
                    .success(true)
                    .message(actionName + " success")
                    .hostAddress(parsed.getHostAddress())
                    .controlCommand(formatByteHex(controlCommand))
                    .dataCommand(formatCommandHex(request.getDataCommand()))
                    .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                    .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                    .build();
        } catch (SocketTimeoutException exception) {
            return failure("TIMEOUT", "Request timeout", requestHex);
        } catch (ConnectException | UnknownHostException exception) {
            return failure("CONNECTION_FAILED", "Connection failed", requestHex);
        } catch (IllegalArgumentException exception) {
            String errorCode = exception.getMessage() != null && exception.getMessage().contains("CRC")
                    ? "CRC_ERROR" : "PROTOCOL_ERROR";
            return failure(errorCode, exception.getMessage(), requestHex);
        } catch (IOException exception) {
            log.warn("{} io error: {}", actionName, exception.getMessage());
            return failure("IO_ERROR", "Network IO error", requestHex);
        }
    }

    private LedControlResponse failure(String errorCode, String message, String requestHex) {
        return LedControlResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .rawRequestHex(requestHex)
                .build();
    }

    private LedControlResponse executeTcpConfig(
            LedTcpConfigRequest request,
            byte subCommand,
            byte[] payload,
            String actionName
    ) {
        byte[] frame = protocolCodec.buildFrame(
                request.getHostAddress(),
                LedCommandConstants.CMD_TCP_CONFIG,
                subCommand,
                payload == null ? new byte[0] : payload);
        String requestHex = LedProtocolCodec.toPrefixedHex(frame);

        try {
            byte[] response = tcpClientManager.sendAndReceive(
                    request.getDeviceIp(),
                    request.getDevicePort(),
                    frame,
                    request.getTimeoutMs());
            LedProtocolCodec.ParsedFrame parsed = protocolCodec.parseFrame(response);

            boolean deviceError = parsed.getDataCommand() == (LedCommandConstants.RESP_ERROR & 0xFF);
            if (deviceError) {
                return LedControlResponse.builder()
                        .success(false)
                        .message("Device returned error code")
                        .errorCode("DEVICE_ERROR")
                        .hostAddress(parsed.getHostAddress())
                        .controlCommand(formatByteHex(LedCommandConstants.CMD_TCP_CONFIG))
                        .dataCommand(formatByteHex(subCommand))
                        .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                        .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                        .rawRequestHex(requestHex)
                        .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                        .build();
            }

            return LedControlResponse.builder()
                    .success(true)
                    .message(actionName + " success")
                    .hostAddress(parsed.getHostAddress())
                    .controlCommand(formatByteHex(LedCommandConstants.CMD_TCP_CONFIG))
                    .dataCommand(formatByteHex(subCommand))
                    .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                    .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                    .build();
        } catch (SocketTimeoutException exception) {
            return failure("TIMEOUT", "Request timeout", requestHex);
        } catch (ConnectException | UnknownHostException exception) {
            return failure("CONNECTION_FAILED", "Connection failed", requestHex);
        } catch (IllegalArgumentException exception) {
            String errorCode = exception.getMessage() != null && exception.getMessage().contains("CRC")
                    ? "CRC_ERROR" : "PROTOCOL_ERROR";
            return failure(errorCode, exception.getMessage(), requestHex);
        } catch (IOException exception) {
            log.warn("{} io error: {}", actionName, exception.getMessage());
            return failure("IO_ERROR", "Network IO error", requestHex);
        }
    }

    private byte[] buildTargetAddressPayload(String targetIp, Integer targetPort) {
        if (targetIp == null || targetIp.isBlank()) {
            throw new IllegalArgumentException("targetIp is required for open TCP client");
        }
        if (targetPort == null || targetPort < 1 || targetPort > 65535) {
            throw new IllegalArgumentException("targetPort must be in range 1..65535");
        }

        try {
            byte[] ip = InetAddress.getByName(targetIp).getAddress();
            if (ip.length != 4) {
                throw new IllegalArgumentException("targetIp must be IPv4");
            }
            byte[] payload = new byte[6];
            System.arraycopy(ip, 0, payload, 0, 4);
            payload[4] = (byte) ((targetPort >> 8) & 0xFF);
            payload[5] = (byte) (targetPort & 0xFF);
            return payload;
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException("targetIp is invalid", exception);
        }
    }

    private String toAscii(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return "";
        }
        String raw = new String(payload, StandardCharsets.US_ASCII);
        StringBuilder builder = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch >= 32 && ch <= 126) {
                builder.append(ch);
            } else {
                builder.append('.');
            }
        }
        return builder.toString();
    }

    private String formatCommandHex(Integer command) {
        if (command == null) {
            return null;
        }
        return String.format("0x%02X", command & 0xFF);
    }

    public LedSignalStrengthResponse querySignalStrength(LedControlRequest request) {
        byte[] frame = protocolCodec.buildControlFrame(
                request.getHostAddress(),
                LedCommandConstants.CMD_QUERY,
                (byte) 0xBF);
        String requestHex = LedProtocolCodec.toPrefixedHex(frame);

        try {
            byte[] response = tcpClientManager.sendAndReceive(
                    request.getDeviceIp(),
                    request.getDevicePort(),
                    frame,
                    request.getTimeoutMs());

            LedProtocolCodec.ParsedFrame parsed = protocolCodec.parseFrame(response);
            boolean deviceError = parsed.getDataCommand() == (LedCommandConstants.RESP_ERROR & 0xFF);
            String payloadAscii = toAscii(parsed.getPayload());
            Integer signalStrengthDbm = parseSignalStrengthDbm(parsed.getPayload(), payloadAscii);

            if (deviceError) {
                return LedSignalStrengthResponse.builder()
                        .success(false)
                        .message("Device returned error code")
                        .errorCode("DEVICE_ERROR")
                        .hostAddress(parsed.getHostAddress())
                        .requestDataCommand(formatCommandHex(request.getDataCommand()))
                        .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                        .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                        .signalStrengthDbm(signalStrengthDbm)
                        .payloadHex(LedProtocolCodec.toPrefixedHex(parsed.getPayload()))
                        .payloadAscii(payloadAscii)
                        .rawRequestHex(requestHex)
                        .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                        .build();
            }

            return LedSignalStrengthResponse.builder()
                    .success(signalStrengthDbm != null)
                    .message(signalStrengthDbm == null ? "Signal strength not found in payload" : "Signal strength query success")
                    .errorCode(signalStrengthDbm == null ? "SIGNAL_NOT_FOUND" : null)
                    .hostAddress(parsed.getHostAddress())
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                    .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                    .signalStrengthDbm(signalStrengthDbm)
                    .payloadHex(LedProtocolCodec.toPrefixedHex(parsed.getPayload()))
                    .payloadAscii(payloadAscii)
                    .rawRequestHex(requestHex)
                    .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                    .build();
        } catch (SocketTimeoutException exception) {
            return LedSignalStrengthResponse.builder()
                    .success(false)
                    .message("Request timeout")
                    .errorCode("TIMEOUT")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (ConnectException | UnknownHostException exception) {
            return LedSignalStrengthResponse.builder()
                    .success(false)
                    .message("Connection failed")
                    .errorCode("CONNECTION_FAILED")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IllegalArgumentException exception) {
            String errorCode = exception.getMessage() != null && exception.getMessage().contains("CRC")
                    ? "CRC_ERROR" : "PROTOCOL_ERROR";
            return LedSignalStrengthResponse.builder()
                    .success(false)
                    .message(exception.getMessage())
                    .errorCode(errorCode)
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IOException exception) {
            log.warn("signal strength io error: {}", exception.getMessage());
            return LedSignalStrengthResponse.builder()
                    .success(false)
                    .message("Network IO error")
                    .errorCode("IO_ERROR")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        }
    }

    public LedSystemNetworkResponse querySystemNetwork(LedControlRequest request) {
        byte[] frame = protocolCodec.buildControlFrame(
                request.getHostAddress(),
                LedCommandConstants.CMD_QUERY,
                (byte)0x97);
        String requestHex = LedProtocolCodec.toPrefixedHex(frame);

        try {
            byte[] response = tcpClientManager.sendAndReceive(
                    request.getDeviceIp(),
                    request.getDevicePort(),
                    frame,
                    request.getTimeoutMs());

            LedProtocolCodec.ParsedFrame parsed = protocolCodec.parseFrame(response);
            boolean deviceError = parsed.getDataCommand() == (LedCommandConstants.RESP_ERROR & 0xFF);
            NetworkInfo networkInfo = parseSystemNetworkInfo(parsed.getPayload());
            String payloadAscii = networkInfo.payloadAscii;
            String systemIp = networkInfo.systemIp;
            String systemMac = networkInfo.systemMac;

            if (deviceError) {
                return LedSystemNetworkResponse.builder()
                        .success(false)
                        .message("Device returned error code")
                        .errorCode("DEVICE_ERROR")
                        .hostAddress(parsed.getHostAddress())
                        .requestDataCommand(formatCommandHex(request.getDataCommand()))
                        .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                        .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                        .systemIp(systemIp)
                        .systemMac(systemMac)
                        .payloadHex(LedProtocolCodec.toPrefixedHex(parsed.getPayload()))
                        .payloadAscii(payloadAscii)
                        .rawRequestHex(requestHex)
                        .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                        .build();
            }

            boolean found = systemIp != null || systemMac != null;
            return LedSystemNetworkResponse.builder()
                    .success(found)
                    .message(found ? "System network query success" : "IP/MAC not found in payload")
                    .errorCode(found ? null : "NETWORK_INFO_NOT_FOUND")
                    .hostAddress(parsed.getHostAddress())
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .responseControlCommand(formatCommandHex(parsed.getControlCommand()))
                    .responseDataCommand(formatCommandHex(parsed.getDataCommand()))
                    .systemIp(systemIp)
                    .systemMac(systemMac)
                    .payloadHex(LedProtocolCodec.toPrefixedHex(parsed.getPayload()))
                    .payloadAscii(payloadAscii)
                    .rawRequestHex(requestHex)
                    .rawResponseHex(LedProtocolCodec.toPrefixedHex(response))
                    .build();
        } catch (SocketTimeoutException exception) {
            return LedSystemNetworkResponse.builder()
                    .success(false)
                    .message("Request timeout")
                    .errorCode("TIMEOUT")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (ConnectException | UnknownHostException exception) {
            return LedSystemNetworkResponse.builder()
                    .success(false)
                    .message("Connection failed")
                    .errorCode("CONNECTION_FAILED")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IllegalArgumentException exception) {
            String errorCode = exception.getMessage() != null && exception.getMessage().contains("CRC")
                    ? "CRC_ERROR" : "PROTOCOL_ERROR";
            return LedSystemNetworkResponse.builder()
                    .success(false)
                    .message(exception.getMessage())
                    .errorCode(errorCode)
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        } catch (IOException exception) {
            log.warn("system network io error: {}", exception.getMessage());
            return LedSystemNetworkResponse.builder()
                    .success(false)
                    .message("Network IO error")
                    .errorCode("IO_ERROR")
                    .requestDataCommand(formatCommandHex(request.getDataCommand()))
                    .rawRequestHex(requestHex)
                    .build();
        }
    }

    private String formatByteHex(byte command) {
        return String.format("0x%02X", command & 0xFF);
    }

    private Integer parseSignalStrengthDbm(byte[] payload, String payloadAscii) {
        if (payloadAscii != null && !payloadAscii.isBlank()) {
            Matcher matcher = RSSI_PATTERN.matcher(payloadAscii);
            if (matcher.find()) {
                String value = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                if (value != null) {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        if (payload != null && payload.length == 1) {
            return (int) payload[0];
        }
        return null;
    }

    private String parseSystemIp(String payloadAscii) {
        if (payloadAscii == null || payloadAscii.isBlank()) {
            return null;
        }
        Matcher matcher = IPV4_PATTERN.matcher(payloadAscii);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String parseSystemMac(String payloadAscii) {
        if (payloadAscii == null || payloadAscii.isBlank()) {
            return null;
        }
        Matcher matcher = MAC_PATTERN.matcher(payloadAscii);
        if (!matcher.find()) {
            return null;
        }
        String raw = matcher.group().replace("-", "").replace(":", "").toUpperCase();
        if (raw.length() != 12) {
            return null;
        }
        return raw.substring(0, 2) + ":" + raw.substring(2, 4) + ":" + raw.substring(4, 6)
                + ":" + raw.substring(6, 8) + ":" + raw.substring(8, 10) + ":" + raw.substring(10, 12);
    }

    private NetworkInfo parseSystemNetworkInfo(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return new NetworkInfo(null, null, "");
        }

        if (payload.length >= 2) {
            int macLen = payload[0] & 0xFF;
            int ipLen = payload[1] & 0xFF;
            int expected = 2 + macLen + ipLen;
            if (macLen > 0 && ipLen > 0 && expected <= payload.length) {
                String macRaw = readAscii(payload, 2, macLen);
                String ipRaw = readAscii(payload, 2 + macLen, ipLen);
                String mac = parseSystemMac(macRaw);
                String ip = parseSystemIp(ipRaw);
                if (mac == null) {
                    mac = macRaw;
                }
                if (ip == null) {
                    ip = ipRaw;
                }
                String ascii = "MAC:" + (mac == null ? "" : mac) + ", IP:" + (ip == null ? "" : ip);
                return new NetworkInfo(ip, mac, ascii);
            }
        }

        String fallbackAscii = toAscii(payload);
        String ip = parseSystemIp(fallbackAscii);
        String mac = parseSystemMac(fallbackAscii);
        String ascii = (ip != null || mac != null)
                ? "MAC:" + (mac == null ? "" : mac) + ", IP:" + (ip == null ? "" : ip)
                : fallbackAscii;
        return new NetworkInfo(ip, mac, ascii);
    }

    private String readAscii(byte[] data, int offset, int length) {
        if (length <= 0 || offset < 0 || offset + length > data.length) {
            return "";
        }
        return new String(data, offset, length, StandardCharsets.US_ASCII).trim();
    }

    private static final class NetworkInfo {
        private final String systemIp;
        private final String systemMac;
        private final String payloadAscii;

        private NetworkInfo(String systemIp, String systemMac, String payloadAscii) {
            this.systemIp = systemIp;
            this.systemMac = systemMac;
            this.payloadAscii = payloadAscii;
        }
    }
}
