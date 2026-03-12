package org.ssssssss.magicboot.utils;

import com.baomidou.mybatisplus.core.toolkit.Sequence;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
/**
 * 雪花算法ID生成工具类
 * 完全基于MyBatis-Plus 3.5.5版本实现，解决无参构造方法问题
 */
public class SnowflakeIdGenerator {

    // 默认雪花算法序列生成器（使用自动生成的workerId）
    private static final Sequence DEFAULT_SEQUENCE;

    /** 雪花算法起始时间戳 (MyBatis-Plus 3.5.5默认值) */
    private static final long TWEPOCH = 1588261200000L; // 2020-04-30 00:00:00

    /** 时间戳占用位数 */
    private static final long TIMESTAMP_LEFT_SHIFT = 22;

    static {
        // 初始化默认序列生成器，手动计算workerId（模仿MyBatis-Plus内部实现）
        long workerId = getWorkerId(1L, 31L);
        DEFAULT_SEQUENCE = new Sequence(workerId, 1L);
    }

    /**
     * 私有化构造方法，防止实例化
     */
    private SnowflakeIdGenerator() {
        throw new AssertionError("不能实例化工具类SnowflakeIdGenerator");
    }

    /**
     * 生成雪花算法ID（Long类型）
     * @return 生成的ID
     */
    public static long nextId() {
        return DEFAULT_SEQUENCE.nextId();
    }

    /**
     * 生成雪花算法ID（字符串类型）
     * @return 生成的ID字符串
     */
    public static String nextIdStr() {
        return String.valueOf(DEFAULT_SEQUENCE.nextId());
    }

    /**
     * 根据指定的机器ID和数据中心ID生成雪花算法ID
     * @param workerId 机器ID (0-31)
     * @param dataCenterId 数据中心ID (0-31)
     * @return 生成的ID
     */
    public static long nextId(long workerId, long dataCenterId) {
        // 严格使用3.5.5版本支持的双参数构造方法
        Sequence sequence = new Sequence(workerId, dataCenterId);
        return sequence.nextId();
    }

    /**
     * 解析雪花ID，获取生成时间戳（毫秒）
     * @param id 雪花ID
     * @return 生成时间戳（毫秒）
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_LEFT_SHIFT) + TWEPOCH;
    }

    /**
     * 解析雪花ID，获取机器ID
     * @param id 雪花ID
     * @return 机器ID
     */
    public static long parseWorkerId(long id) {
        long workerIdBits = 5L;
        long dataCenterIdBits = 5L;
        long sequenceBits = 12L;

        long mask = ~(-1L << workerIdBits);
        return (id >> (sequenceBits + dataCenterIdBits)) & mask;
    }

    /**
     * 解析雪花ID，获取数据中心ID
     * @param id 雪花ID
     * @return 数据中心ID
     */
    public static long parseDataCenterId(long id) {
        long dataCenterIdBits = 5L;
        long sequenceBits = 12L;
        long mask = ~(-1L << dataCenterIdBits);

        return (id >> sequenceBits) & mask;
    }

    /**
     * 解析雪花ID，获取序列号
     * @param id 雪花ID
     * @return 序列号
     */
    public static long parseSequence(long id) {
        long sequenceBits = 12L;
        long mask = ~(-1L << sequenceBits);
        return id & mask;
    }

    /**
     * 自动生成workerId（模仿MyBatis-Plus内部实现）
     */
    private static long getWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (sb.length() > 0) {
            return (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
        } else {
            try {
                String hostName = InetAddress.getLocalHost().getHostName();
                int hashCode = hostName.hashCode();
                return (hashCode & 0xffff) % (maxWorkerId + 1);
            } catch (UnknownHostException e) {
                return (int) (Math.random() * (maxWorkerId + 1));
            }
        }
    }

    public static void main(String[] args) {

        // 生成默认ID
        long id = SnowflakeIdGenerator.nextId();
        String idStr = SnowflakeIdGenerator.nextIdStr();

        // 指定机器ID和数据中心ID生成
        long customId = SnowflakeIdGenerator.nextId(1, 2);

        System.out.println("生成默认ID====" + id);
        System.out.println("idStr====" + idStr);
        System.out.println("customId====" + customId);
        //生成默认ID====1962325604158308353
        //idStr====1962325604158308354
        //customId====1962325604158410753
    }
}
