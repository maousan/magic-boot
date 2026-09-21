package org.ssssssss.magicboot.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * SNTP 单次时间查询（RFC 2030，UDP 48 字节报文），零依赖。
 * 任何失败返回 null，由调用方跳过——NTP 是可选增强，不是依赖项。
 */
@Service
public class LicenseNtpService {

    private static final Logger log = LoggerFactory.getLogger(LicenseNtpService.class);

    private static final int NTP_PORT = 123;
    private static final long NTP_EPOCH_OFFSET = 2208988800L; // 1900→1970 秒差

    /**
     * 查询 NTP 服务器时间（毫秒）。失败返回 null。
     */
    public Long query(String server, int timeoutMillis) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeoutMillis);
            InetAddress address = InetAddress.getByName(server.trim());

            byte[] request = new byte[48];
            request[0] = 0x1B; // LI=0, VN=3, Mode=3(client)
            socket.send(new DatagramPacket(request, request.length, address, NTP_PORT));

            byte[] response = new byte[48];
            DatagramPacket packet = new DatagramPacket(response, response.length);
            socket.receive(packet);

            if (packet.getLength() < 48) {
                return null;
            }
            long seconds = readUnsignedInt32(response, 40) - NTP_EPOCH_OFFSET;
            long fraction = readUnsignedInt32(response, 44);
            return seconds * 1000L + (fraction * 1000L >>> 32);
        } catch (Exception e) {
            log.debug("license ntp query {} failed: {}", server, e.getMessage());
            return null;
        }
    }

    private static long readUnsignedInt32(byte[] data, int offset) {
        return ((data[offset] & 0xffL) << 24) | ((data[offset + 1] & 0xffL) << 16)
                | ((data[offset + 2] & 0xffL) << 8) | (data[offset + 3] & 0xffL);
    }
}
