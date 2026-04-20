package org.ssssssss.magicboot.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局线程安全计数Map。
 */
public final class CommonCountMap {

    private static final ConcurrentHashMap<String, AtomicInteger> COUNT_MAP = new ConcurrentHashMap<>();
    private static final int MIN_COUNT = 0;

    private CommonCountMap() {
    }

    /**
     * 计数+1，返回最新值。
     */
    public static int increment(String key) {
        return add(key, 1);
    }

    /**
     * 计数-1，最小为0，返回最新值。
     */
    public static int decrement(String key) {
        return add(key, -1);
    }

    /**
     * 按增量调整计数，最终结果最小为0。
     */
    public static int add(String key, int delta) {
        validateKey(key);
        if (delta == 0) {
            return get(key);
        }
        AtomicInteger result = COUNT_MAP.compute(key, (k, current) -> {
            if (current == null) {
                int next = Math.max(delta, MIN_COUNT);
                return next == MIN_COUNT ? null : new AtomicInteger(next);
            }
            int next = current.get() + delta;
            if (next <= MIN_COUNT) {
                return null;
            }
            current.set(next);
            return current;
        });
        return result == null ? MIN_COUNT : result.get();
    }

    /**
     * 直接设置计数，负数按0处理；为0时移除键。
     */
    public static void set(String key, int count) {
        validateKey(key);
        int normalized = Math.max(count, MIN_COUNT);
        if (normalized == MIN_COUNT) {
            COUNT_MAP.remove(key);
            return;
        }
        COUNT_MAP.put(key, new AtomicInteger(normalized));
    }

    /**
     * 获取计数，不存在时返回0。
     */
    public static int get(String key) {
        validateKey(key);
        AtomicInteger value = COUNT_MAP.get(key);
        return value == null ? MIN_COUNT : Math.max(value.get(), MIN_COUNT);
    }

    /**
     * 删除指定键记录。
     */
    public static void remove(String key) {
        validateKey(key);
        COUNT_MAP.remove(key);
    }

    /**
     * 清空全部记录。
     */
    public static void clear() {
        COUNT_MAP.clear();
    }

    /**
     * 返回只读快照。
     */
    public static Map<String, Integer> snapshot() {
        Map<String, Integer> result = new HashMap<>();
        COUNT_MAP.forEach((key, counter) -> result.put(key, Math.max(counter.get(), MIN_COUNT)));
        return Collections.unmodifiableMap(result);
    }

    /**
     * 是否包含指定键。
     */
    public static boolean containsKey(String key) {
        validateKey(key);
        return COUNT_MAP.containsKey(key);
    }

    /**
     * 当前键数量。
     */
    public static int size() {
        return COUNT_MAP.size();
    }

    /**
     * 统计全部计数总和。
     */
    public static long totalCount() {
        long total = 0L;
        for (AtomicInteger value : COUNT_MAP.values()) {
            if (value != null && value.get() > MIN_COUNT) {
                total += value.get();
            }
        }
        return total;
    }

    private static void validateKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key不能为空");
        }
    }
}
