package com.ocean.tigaapi.engine.groovy.test;

import com.ocean.tigaapi.engine.groovy.GroovyEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GroovyMetaspaceTest {

    // 使用 volatile 确保多线程可见性
    private volatile GroovyEngine engine;

    @BeforeEach
    public void setup() {
        // 1. 显式创建对象，确保不为 null
        this.engine = new GroovyEngine();
        // 2. 显式调用 init() 初始化锁数组
        this.engine.init();
        System.out.println("Engine 初始化完成...");
    }

    @Test
    public void testMetaspaceStability() throws InterruptedException {
        int threadCount = 8;
        int iterationsPerThread = 100; 
        String scriptId = "stress_test_script";
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Random random = new Random();

        printMetaspaceUsage("测试开始前状态");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    // 在 Lambda 内部再次检查，防止 NPE
                    if (engine == null) {
                        System.err.println("错误：Engine 对象为 Null");
                        return;
                    }

                    for (int j = 0; j < iterationsPerThread; j++) {
                        // 构造动态脚本触发重编译
                        String scriptText = "/* ver " + System.nanoTime() + " */ \n" +
                                            "var x = " + random.nextInt(1000) + "; return x + 1;";
                        
                        // 执行
                        engine.execute(scriptId, scriptText, new HashMap<>(), 2000L);
                    }
                } catch (Exception e) {
                    // 打印详细错误，方便定位是脚本报错还是引擎报错
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程执行完毕
        boolean finished = latch.await(5, TimeUnit.MINUTES);
        if(!finished) {
            System.err.println("测试超时，部分线程未完成");
        }

        executor.shutdownNow();

        // 强迫 JVM 进行类卸载清理
        System.out.println("正在触发系统 GC 以清理 Metaspace...");
        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(1000);
        }

        printMetaspaceUsage("最终状态 (GC后)");
    }

    private void printMetaspaceUsage(String label) {
        boolean found = false;
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getName().toLowerCase().contains("metaspace")) {
                long used = pool.getUsage().getUsed() / 1024 / 1024;
                long committed = pool.getUsage().getCommitted() / 1024 / 1024;
                System.out.printf("[%s] Metaspace Used: %d MB, Committed: %d MB%n", label, used, committed);
                found = true;
            }
        }
        if (!found) {
            System.out.println("[" + label + "] 未找到 Metaspace 统计信息 (可能使用了非 HotSpot JVM)");
        }
    }
}