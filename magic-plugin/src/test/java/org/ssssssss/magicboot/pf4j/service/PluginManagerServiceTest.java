package org.ssssssss.magicboot.pf4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;
import org.ssssssss.magicboot.pf4j.entity.PluginInfo;
import org.ssssssss.magicboot.pf4j.mapper.PluginInfoMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PluginManagerServiceTest {

    @Mock
    private PluginManager pluginManager;

    @Mock
    private PluginInfoMapper pluginInfoMapper;

    @Mock
    private PluginProperties pluginProperties;

    @Mock
    private PluginWrapper pluginWrapper;

    @Mock
    private PluginDescriptor pluginDescriptor;

    private PluginManagerService service;

    @BeforeEach
    void setUp() {
        service = new PluginManagerService(pluginManager, pluginInfoMapper, pluginProperties);
    }

    @Test
    @DisplayName("listAllPlugins 运行态存在但数据库不存在时应返回运行态插件")
    void listAllPlugins_whenOnlyRuntimePluginExists_shouldReturnRuntimePlugin() {
        when(pluginInfoMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(pluginManager.getPlugins()).thenReturn(List.of(pluginWrapper));
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(pluginWrapper.getPluginState()).thenReturn(PluginState.STARTED);
        when(pluginWrapper.getDescriptor()).thenReturn(pluginDescriptor);
        when(pluginDescriptor.getPluginId()).thenReturn("demo-plugin");
        when(pluginDescriptor.getVersion()).thenReturn("1.0.0");
        when(pluginDescriptor.getProvider()).thenReturn("MagicBoot Team");
        when(pluginDescriptor.getPluginClass()).thenReturn("org.ssssssss.magicboot.demo.DemoPlugin");

        List<Map<String, Object>> result = service.listAllPlugins();

        assertEquals(1, result.size());
        Map<String, Object> plugin = result.get(0);
        assertEquals("demo-plugin", plugin.get("pluginId"));
        assertEquals("STARTED", plugin.get("runtimeState"));
        assertEquals("STARTED", plugin.get("statusEnum"));
    }

    @Test
    @DisplayName("initMissingPluginsFromRuntime DB缺失插件时应补录")
    void initMissingPluginsFromRuntime_whenPluginMissingInDb_shouldInsert() {
        when(pluginInfoMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(pluginManager.getPlugins()).thenReturn(List.of(pluginWrapper));
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(pluginWrapper.getPluginState()).thenReturn(PluginState.STARTED);
        when(pluginWrapper.getDescriptor()).thenReturn(pluginDescriptor);
        when(pluginWrapper.getPluginPath()).thenReturn(Paths.get("D:/IdeaProjects/magic-boot/plugins/magic-plugin-demo.jar"));
        when(pluginDescriptor.getPluginId()).thenReturn("demo-plugin");
        when(pluginDescriptor.getVersion()).thenReturn("1.0.0");
        when(pluginDescriptor.getProvider()).thenReturn("MagicBoot Team");
        when(pluginDescriptor.getPluginClass()).thenReturn("org.ssssssss.magicboot.demo.DemoPlugin");
        when(pluginDescriptor.getDependencies()).thenReturn(null);

        Map<String, Object> result = service.initMissingPluginsFromRuntime();

        assertEquals(1, result.get("runtimeTotal"));
        assertEquals(1, result.get("inserted"));
        assertEquals(0, result.get("skipped"));

        ArgumentCaptor<PluginInfo> captor = ArgumentCaptor.forClass(PluginInfo.class);
        verify(pluginInfoMapper, times(1)).insert(captor.capture());
        PluginInfo inserted = captor.getValue();
        assertEquals("demo-plugin", inserted.getPluginId());
        assertEquals("STARTED", inserted.getStatus());
    }

    @Test
    @DisplayName("initMissingPluginsFromRuntime DB已存在插件时应跳过")
    void initMissingPluginsFromRuntime_whenPluginExistsInDb_shouldSkipInsert() {
        PluginInfo dbPlugin = new PluginInfo();
        dbPlugin.setPluginId("demo-plugin");

        when(pluginInfoMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(dbPlugin));
        when(pluginManager.getPlugins()).thenReturn(List.of(pluginWrapper));
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");

        Map<String, Object> result = service.initMissingPluginsFromRuntime();

        assertEquals(1, result.get("runtimeTotal"));
        assertEquals(0, result.get("inserted"));
        assertEquals(1, result.get("skipped"));
        verify(pluginInfoMapper, never()).insert(any(PluginInfo.class));
    }

    @Test
    @DisplayName("getRuntimeSummary 应返回状态统计")
    void getRuntimeSummary_shouldReturnCounts() {
        PluginWrapper wrapper2 = mock(PluginWrapper.class);
        when(pluginManager.getPlugins()).thenReturn(List.of(pluginWrapper, wrapper2));
        when(pluginWrapper.getPluginState()).thenReturn(PluginState.STARTED);
        when(wrapper2.getPluginState()).thenReturn(PluginState.STOPPED);

        Map<String, Object> result = service.getRuntimeSummary();

        assertEquals(2, result.get("total"));
        assertEquals(1L, result.get("started"));
        assertEquals(1L, result.get("stopped"));
    }

    @Test
    @DisplayName("reconcilePlugins dryRun模式不应落库")
    void reconcilePlugins_whenDryRunShouldNotMutate() {
        when(pluginInfoMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(pluginManager.getPlugins()).thenReturn(List.of(pluginWrapper));
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");

        Map<String, Object> result = service.reconcilePlugins(true);

        assertEquals(true, result.get("dryRun"));
        assertEquals(List.of("demo-plugin"), result.get("missingInDb"));
        assertEquals(0, result.get("fixedCount"));
        verify(pluginInfoMapper, never()).insert(any(PluginInfo.class));
    }

    @Test
    @DisplayName("reconcilePlugins 执行模式应补录 missingInDb")
    void reconcilePlugins_whenApplyShouldInsertMissingDb() {
        when(pluginInfoMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(pluginManager.getPlugins()).thenReturn(List.of(pluginWrapper));
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(pluginWrapper.getPluginState()).thenReturn(PluginState.STARTED);
        when(pluginWrapper.getDescriptor()).thenReturn(pluginDescriptor);
        when(pluginWrapper.getPluginPath()).thenReturn(Paths.get("D:/IdeaProjects/magic-boot/plugins/magic-plugin-demo.jar"));
        when(pluginDescriptor.getPluginId()).thenReturn("demo-plugin");
        when(pluginDescriptor.getVersion()).thenReturn("1.0.0");
        when(pluginDescriptor.getProvider()).thenReturn("MagicBoot Team");
        when(pluginDescriptor.getPluginClass()).thenReturn("org.ssssssss.magicboot.demo.DemoPlugin");
        when(pluginDescriptor.getDependencies()).thenReturn(null);
        Map<String, Object> result = service.reconcilePlugins(false);

        assertEquals(false, result.get("dryRun"));
        assertEquals(1, result.get("fixedCount"));
        verify(pluginInfoMapper, times(1)).insert(any(PluginInfo.class));
    }

    @Test
    @DisplayName("installPluginBySource LOCAL_PATH 应分发到本地安装")
    void installPluginBySource_whenLocalPath_shouldDispatch() {
        PluginManagerService spyService = spy(service);
        Map<String, Object> expected = Map.of("pluginId", "demo-plugin");
        doReturn(expected).when(spyService).loadAndInstallPlugin("D:/tmp/demo.jar");

        Map<String, Object> result = spyService.installPluginBySource("LOCAL_PATH", "D:/tmp/demo.jar", null);

        assertEquals(expected, result);
        verify(spyService, times(1)).loadAndInstallPlugin("D:/tmp/demo.jar");
    }

    @Test
    @DisplayName("installFromRemoteUrl 非 http/https 协议应失败")
    void installFromRemoteUrl_whenNonHttp_shouldFail() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.installFromRemoteUrl("ftp://example.com/demo.jar"));
        assertTrue(ex.getMessage().contains("http"));
    }

    @Test
    @DisplayName("installFromRemoteUrl 下载成功后应调用安装")
    void installFromRemoteUrl_whenSuccess_shouldInstall(@TempDir Path tempDir) throws IOException {
        when(pluginProperties.getDir()).thenReturn(tempDir.toString());
        PluginManagerService spyService = spy(service);
        doReturn(Map.of("pluginId", "demo-plugin")).when(spyService).loadAndInstallPlugin(anyString());

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/demo.jar", exchange -> {
            byte[] body = new byte[]{1, 2, 3};
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/demo.jar";
            Map<String, Object> result = spyService.installFromRemoteUrl(url);

            assertEquals("demo-plugin", result.get("pluginId"));
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(spyService).loadAndInstallPlugin(captor.capture());
            assertTrue(captor.getValue().endsWith("demo.jar"));
            assertTrue(Files.exists(Path.of(captor.getValue())));
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("installFromRemoteUrl 下载失败应抛错")
    void installFromRemoteUrl_whenDownloadFailed_shouldThrow(@TempDir Path tempDir) throws IOException {
        when(pluginProperties.getDir()).thenReturn(tempDir.toString());

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/demo.jar", exchange -> {
            byte[] body = "error".getBytes();
            exchange.sendResponseHeaders(500, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/demo.jar";
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.installFromRemoteUrl(url));
            assertTrue(ex.getMessage().contains("下载"));
            assertTrue(Files.list(tempDir).findAny().isEmpty());
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("installFromRemoteUrl 加载失败时应清理临时与目标文件")
    void installFromRemoteUrl_whenLoadFailed_shouldCleanup(@TempDir Path tempDir) throws IOException {
        when(pluginProperties.getDir()).thenReturn(tempDir.toString());
        PluginManagerService spyService = spy(service);
        doThrow(new RuntimeException("mock load failed")).when(spyService).loadAndInstallPlugin(anyString());

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/demo.jar", exchange -> {
            byte[] body = new byte[]{1, 2, 3};
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/demo.jar";
            RuntimeException ex = assertThrows(RuntimeException.class, () -> spyService.installFromRemoteUrl(url));
            assertTrue(ex.getMessage().contains("远程下载安装失败"));
            assertFalse(Files.exists(tempDir.resolve("demo.jar.download")));
            assertFalse(Files.exists(tempDir.resolve("demo.jar")));
        } finally {
            server.stop(0);
        }
    }
}
