package org.ssssssss.magicboot.zintis.led.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 复现：设备 TCP 连接正常、报文持续发送（抓包可见），
 * 但服务端活跃客户端里查不到该设备（MAC 永不注册）。
 *
 * 真实上报帧样本（CRC 已验证为 0x9078，大端）：
 * 66 AB 97 11 0D [17B mac "3A:69:7A:08:D0:A5"] [13B ip "192.168.2.102"] 90 78
 */
class LedNettyReportFrameFragmentationTest {

    private static final byte[] REPORT_FRAME = new byte[]{
            0x66, (byte) 0xAB, (byte) 0x97,
            0x11, 0x0D,
            0x33, 0x41, 0x3A, 0x36, 0x39, 0x3A, 0x37, 0x41, 0x3A, 0x30, 0x38, 0x3A, 0x44, 0x30, 0x3A, 0x41, 0x35,
            0x31, 0x39, 0x32, 0x2E, 0x31, 0x36, 0x38, 0x2E, 0x32, 0x2E, 0x31, 0x30, 0x32,
            (byte) 0x90, 0x78
    };

    private static final String MAC = "3A:69:7A:08:D0:A5";

    /** 真实抓包帧（失败设备 10.33.1.243，CRC 大端 EA82 校验通过） */
    private static final byte[] CAPTURED_REPORT_FRAME = new byte[]{
            0x07, (byte) 0xAB, (byte) 0x97,
            0x11, 0x0D,
            0x33, 0x41, 0x3A, 0x36, 0x39, 0x3A, 0x37, 0x41, 0x3A, 0x30, 0x38, 0x3A, 0x38, 0x34, 0x3A, 0x41, 0x43,
            0x31, 0x39, 0x32, 0x2E, 0x31, 0x36, 0x38, 0x2E, 0x31, 0x30, 0x30, 0x2E, 0x37,
            (byte) 0xEA, (byte) 0x82
    };

    private static final String CAPTURED_MAC = "3A:69:7A:08:84:AC";

    /** 基线：整帧一次性到达，decoder 应识别。预期 GREEN。 */
    @Test
    void decoder_shouldEmitReportFrame_whenFrameArrivesWhole() {
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder());

        channel.writeInbound(Unpooled.wrappedBuffer(REPORT_FRAME.clone()));
        ByteBuf decoded = channel.readInbound();

        assertNotNull(decoded, "整帧到达时 decoder 必须识别出上报帧");
        byte[] actual = new byte[decoded.readableBytes()];
        decoded.readBytes(actual);
        decoded.release();
        assertEquals(REPORT_FRAME.length, actual.length);
        channel.finishAndReleaseAll();
    }

    /**
     * 复现 1：上报帧被 TCP 分成两段到达（20 + 17 字节）。
     * 设备抓包显示报文完整发出，但 decoder 在首段无法校验 CRC 时
     * 逐字节丢弃，导致整帧丢失。预期 RED（修复后 GREEN）。
     */
    @Test
    void decoder_shouldEmitReportFrame_whenFrameArrivesInTwoSegments() {
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder());

        channel.writeInbound(Unpooled.wrappedBuffer(REPORT_FRAME, 0, 20));
        channel.writeInbound(Unpooled.wrappedBuffer(REPORT_FRAME, 20, REPORT_FRAME.length - 20));

        ByteBuf decoded = channel.readInbound();
        assertNotNull(decoded, "分两段到达的上报帧被 decoder 丢弃（用户 bug：连接正常但 MAC 永不注册）");
        byte[] actual = new byte[decoded.readableBytes()];
        decoded.readBytes(actual);
        decoded.release();
        assertEquals(REPORT_FRAME.length, actual.length);
        channel.finishAndReleaseAll();
    }

    /**
     * 复现 2：一个 TCP 段里装着 [完整帧A][帧B的前半]，帧B 的后半在下一段到达。
     * 帧B 的前半会被当作垃圾逐字节丢弃。预期 RED（修复后 GREEN）。
     */
    @Test
    void decoder_shouldEmitBothFrames_whenSecondFrameSpansSegments() {
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder());
        byte[] twoFrames = concat(REPORT_FRAME, REPORT_FRAME);
        int split = REPORT_FRAME.length + 10; // 帧A完整 + 帧B前10字节

        channel.writeInbound(Unpooled.wrappedBuffer(twoFrames, 0, split));
        channel.writeInbound(Unpooled.wrappedBuffer(twoFrames, split, twoFrames.length - split));

        List<byte[]> decodedFrames = drain(channel);
        assertEquals(2, decodedFrames.size(),
                "第二帧跨越段边界时被 decoder 丢弃，实际只解出 " + decodedFrames.size() + " 帧");
        channel.finishAndReleaseAll();
    }

    /**
     * 复现 3（用户症状的精确断言）：decoder + handler 全链路，上报帧分两段到达。
     * TCP 连接存在（channel 活跃），但 MAC 永不注册 →
     * findActiveRemoteAddressByMac 查不到 → 按设备查"活跃客户端"永远失败。
     * 预期 RED（修复后 GREEN）。
     */
    @Test
    void pipeline_shouldRegisterMac_whenReportFrameFragmented() {
        LedNettyServerHandler handler = new LedNettyServerHandler();
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder(), handler);

        channel.writeInbound(Unpooled.wrappedBuffer(REPORT_FRAME, 0, 20));
        channel.writeInbound(Unpooled.wrappedBuffer(REPORT_FRAME, 20, REPORT_FRAME.length - 20));

        assertTrue(channel.isActive(), "TCP 连接应一直存在（与抓包现象一致）");
        // 连接在，但按 MAC 查活跃客户端查不到 —— 用户报告的症状
        assertFalse(handler.findActiveRemoteAddressByMac(MAC).isBlank(),
                "设备连接正常且持续发送上报帧，但按 MAC 查不到活跃客户端");
        // 客户端列表里该连接 MAC 应为空 —— 另一种症状表现
        boolean macRegistered = handler.listActiveClients().stream()
                .anyMatch(client -> MAC.equals(client.macAddress()));
        assertTrue(macRegistered, "活跃客户端列表中该连接的 MAC 未注册");
        // 服务端从未收到完整上报帧 → 不会回 ACK（38 46 55 64 73 82）
        // 抓包判别点：失败设备的抓包里若看不到服务端回的 ACK，即命中此 bug
        Object ack = channel.readOutbound();
        assertNotNull(ack, "服务端未收到上报帧，因此从未回复 ACK 心跳帧");
        channel.finishAndReleaseAll();
    }

    /**
     * 真实抓包帧（单段、CRC 正确）必须被解码并注册 MAC。
     * 若线上程序对同样的帧毫无反应（抓包无 ACK），说明线上跑的不是这份代码。
     */
    @Test
    void pipeline_shouldRegisterMac_forRealCapturedFrame() {
        LedNettyServerHandler handler = new LedNettyServerHandler();
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder(), handler);

        channel.writeInbound(Unpooled.wrappedBuffer(CAPTURED_REPORT_FRAME.clone()));

        assertFalse(handler.findActiveRemoteAddressByMac(CAPTURED_MAC).isBlank(),
                "真实抓包帧未被注册为活跃客户端");
        assertNotNull(channel.readOutbound(), "收到上报帧后必须回复 ACK（38 46 55 64 73 82）");
        channel.finishAndReleaseAll();
    }

    /** 真实抓包帧被 TCP 分段时同样不能丢（H1 回归保护）。 */
    @Test
    void pipeline_shouldRegisterMac_whenCapturedFrameFragmented() {
        LedNettyServerHandler handler = new LedNettyServerHandler();
        EmbeddedChannel channel = new EmbeddedChannel(new LedNettyFrameDecoder(), handler);

        channel.writeInbound(Unpooled.wrappedBuffer(CAPTURED_REPORT_FRAME, 0, 20));
        channel.writeInbound(Unpooled.wrappedBuffer(CAPTURED_REPORT_FRAME, 20, CAPTURED_REPORT_FRAME.length - 20));

        assertFalse(handler.findActiveRemoteAddressByMac(CAPTURED_MAC).isBlank(),
                "分段到达的真实抓包帧被丢弃，MAC 未注册");
        channel.finishAndReleaseAll();
    }

    private List<byte[]> drain(EmbeddedChannel channel) {
        List<byte[]> frames = new ArrayList<>();
        ByteBuf buf;
        while ((buf = channel.readInbound()) != null) {
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            buf.release();
            frames.add(data);
        }
        return frames;
    }

    private byte[] concat(byte[] left, byte[] right) {
        byte[] result = new byte[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }
}
