package org.ssssssss.magicboot.pf4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;
import org.ssssssss.magicboot.pf4j.entity.PluginInfo;
import org.ssssssss.magicboot.pf4j.mapper.PluginInfoMapper;
import org.ssssssss.magicboot.pf4j.model.PluginInstallErrorCode;
import org.ssssssss.magicboot.pf4j.model.PluginInstallException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
    void installPluginBySource_whenLocalPath_shouldDispatch() {
        PluginManagerService spyService = spy(service);
        Map<String, Object> expected = Map.of("pluginId", "demo-plugin");
        doReturn(expected).when(spyService).loadAndInstallPlugin("D:/tmp/demo.jar");

        Map<String, Object> result = spyService.installPluginBySource("LOCAL_PATH", "D:/tmp/demo.jar", null);

        assertEquals(expected, result);
        verify(spyService, times(1)).loadAndInstallPlugin("D:/tmp/demo.jar");
    }

    @Test
    void installFromRemoteUrl_whenNonHttp_shouldFail() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.installFromRemoteUrl("ftp://example.com/demo.jar"));
        assertTrue(ex.getMessage().contains("http"));
    }

    @Test
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
            assertTrue(ex.getMessage().toLowerCase().contains("failed"));
            assertTrue(Files.list(tempDir).findAny().isEmpty());
        } finally {
            server.stop(0);
        }
    }

    @Test
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
            assertTrue(ex.getMessage().toLowerCase().contains("failed"));
            assertFalse(Files.exists(tempDir.resolve("demo.jar.download")));
            assertFalse(Files.exists(tempDir.resolve("demo.jar")));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void installFromRemoteUrl_whenExistingJarPresentAndLoadFailed_shouldKeepExistingJar(@TempDir Path tempDir) throws IOException {
        when(pluginProperties.getDir()).thenReturn(tempDir.toString());
        PluginManagerService spyService = spy(service);
        doThrow(new RuntimeException("mock load failed")).when(spyService).loadAndInstallPlugin(anyString());

        Path existingJar = tempDir.resolve("demo.jar");
        byte[] original = "original-remote-jar".getBytes(StandardCharsets.UTF_8);
        Files.write(existingJar, original);

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/demo.jar", exchange -> {
            byte[] body = new byte[]{7, 8, 9};
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/demo.jar";
            RuntimeException ex = assertThrows(RuntimeException.class, () -> spyService.installFromRemoteUrl(url));
            assertTrue(ex.getMessage().toLowerCase().contains("failed"));

            assertTrue(Files.exists(existingJar));
            assertArrayEquals(original, Files.readAllBytes(existingJar));
            assertFalse(Files.exists(tempDir.resolve("demo-1.jar")));
            assertFalse(Files.exists(tempDir.resolve("demo.jar.download")));
            assertFalse(Files.exists(tempDir.resolve("demo-1.jar.download")));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void installPlugin_whenZipOnlyDisabledAndJarUploaded_shouldInstallLegacyJar(@TempDir Path tempDir) throws Exception {
        when(pluginProperties.isUploadZipOnly()).thenReturn(false);
        when(pluginProperties.getDir()).thenReturn(tempDir.toString());
        when(pluginManager.loadPlugin(any(Path.class))).thenReturn("demo-plugin");
        when(pluginManager.getPlugin("demo-plugin")).thenReturn(pluginWrapper);
        when(pluginWrapper.getDescriptor()).thenReturn(pluginDescriptor);
        when(pluginDescriptor.getPluginId()).thenReturn("demo-plugin");
        when(pluginDescriptor.getVersion()).thenReturn("1.0.0");
        when(pluginDescriptor.getProvider()).thenReturn("MagicBoot Team");
        when(pluginDescriptor.getPluginClass()).thenReturn("org.ssssssss.magicboot.demo.DemoPlugin");
        when(pluginDescriptor.getDependencies()).thenReturn(null);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo-plugin.jar",
                "application/java-archive",
                createJarWithPluginPropertiesBytes(Map.of(
                        "plugin.name", "Demo Plugin From Properties",
                        "plugin.provider", "Properties Team"
                ))
        );

        PluginInfo result = service.installPlugin(file);

        assertEquals("demo-plugin", result.getPluginId());
        assertEquals("LEGACY_JAR", result.getPackageType());
        assertEquals("UPLOAD_JAR", result.getInstallSource());
        ArgumentCaptor<PluginInfo> captor = ArgumentCaptor.forClass(PluginInfo.class);
        verify(pluginInfoMapper, times(1)).insert(captor.capture());
        PluginInfo inserted = captor.getValue();
        assertEquals("Demo Plugin From Properties", inserted.getPluginName());
        assertEquals("Properties Team", inserted.getAuthor());
        assertEquals("MagicBoot Team", inserted.getProvider());
        assertEquals("org.ssssssss.magicboot.demo.DemoPlugin", inserted.getPluginClass());
        assertEquals("1.0.0", inserted.getVersion());
    }

    @Test
    void installPlugin_whenZipOnlyDisabledAndInvalidFileType_shouldFail() {
        when(pluginProperties.isUploadZipOnly()).thenReturn(false);
        MockMultipartFile file = new MockMultipartFile("file", "bad.txt", "text/plain", new byte[]{1});

        PluginInstallException ex = assertThrows(PluginInstallException.class, () -> service.installPlugin(file));
        assertEquals(PluginInstallErrorCode.PLUGIN_UPLOAD_INVALID_TYPE, ex.getErrorCode());
    }

    @Test
    void installPlugin_whenLegacyJarInstallFails_shouldNotOverwriteExistingJar(@TempDir Path tempDir) throws Exception {
        when(pluginProperties.isUploadZipOnly()).thenReturn(false);
        when(pluginProperties.getDir()).thenReturn(tempDir.toString());
        when(pluginManager.loadPlugin(any(Path.class))).thenThrow(new RuntimeException("load failed"));

        Path existingJar = tempDir.resolve("demo-plugin.jar");
        byte[] original = "original-jar".getBytes(StandardCharsets.UTF_8);
        Files.write(existingJar, original);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo-plugin.jar",
                "application/java-archive",
                "new-upload".getBytes(StandardCharsets.UTF_8)
        );

        assertThrows(PluginInstallException.class, () -> service.installPlugin(file));

        assertTrue(Files.exists(existingJar));
        assertArrayEquals(original, Files.readAllBytes(existingJar));
    }

    @Test
    void initMissingPluginsFromRuntime_whenPluginPropertiesMissing_shouldFallbackDescriptor(@TempDir Path tempDir) throws IOException {
        Path missingJar = tempDir.resolve("missing-demo.jar");

        when(pluginInfoMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());
        when(pluginManager.getPlugins()).thenReturn(List.of(pluginWrapper));
        when(pluginWrapper.getPluginId()).thenReturn("demo-plugin");
        when(pluginWrapper.getPluginState()).thenReturn(PluginState.STARTED);
        when(pluginWrapper.getPluginPath()).thenReturn(missingJar);
        when(pluginWrapper.getDescriptor()).thenReturn(pluginDescriptor);
        when(pluginDescriptor.getPluginId()).thenReturn("demo-plugin");
        when(pluginDescriptor.getVersion()).thenReturn("1.0.0");
        when(pluginDescriptor.getProvider()).thenReturn("MagicBoot Team");
        when(pluginDescriptor.getPluginClass()).thenReturn("org.ssssssss.magicboot.demo.DemoPlugin");
        when(pluginDescriptor.getDependencies()).thenReturn(null);

        service.initMissingPluginsFromRuntime();

        ArgumentCaptor<PluginInfo> captor = ArgumentCaptor.forClass(PluginInfo.class);
        verify(pluginInfoMapper, times(1)).insert(captor.capture());
        PluginInfo inserted = captor.getValue();
        assertEquals("demo-plugin", inserted.getPluginName());
        assertEquals("MagicBoot Team", inserted.getAuthor());
        assertEquals("MagicBoot Team", inserted.getProvider());
        assertEquals("org.ssssssss.magicboot.demo.DemoPlugin", inserted.getPluginClass());
        assertEquals("1.0.0", inserted.getVersion());
    }

    private byte[] createJarWithPluginPropertiesBytes(Map<String, String> props) throws IOException {
        Path tempJar = Files.createTempFile("plugin-props-", ".jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(tempJar))) {
            jar.putNextEntry(new JarEntry("plugin.properties"));
            Properties properties = new Properties();
            properties.putAll(new LinkedHashMap<>(props));
            properties.store(jar, null);
            jar.closeEntry();
        }
        byte[] bytes = Files.readAllBytes(tempJar);
        Files.deleteIfExists(tempJar);
        return bytes;
    }
}
