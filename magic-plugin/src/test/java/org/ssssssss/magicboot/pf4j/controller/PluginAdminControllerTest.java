package org.ssssssss.magicboot.pf4j.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.ssssssss.magicboot.pf4j.service.PluginManagerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PluginAdminControllerTest {

    @Mock
    private PluginManagerService pluginManagerService;

    private PluginAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new PluginAdminController(pluginManagerService);
    }

    @Test
    @DisplayName("install 接口 LOCAL_PATH 安装成功")
    void installPlugin_whenLocalPath_shouldReturnSuccess() {
        PluginAdminController.InstallRequest request = new PluginAdminController.InstallRequest();
        request.setSource("LOCAL_PATH");
        request.setJarPath("D:/tmp/demo.jar");

        Map<String, Object> serviceResult = Map.of("pluginId", "demo-plugin");
        when(pluginManagerService.installPluginBySource("LOCAL_PATH", "D:/tmp/demo.jar", null))
                .thenReturn(serviceResult);

        ResponseEntity<?> response = controller.installPlugin(request);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(200, body.get("code"));
        assertEquals(serviceResult, body.get("data"));
    }

    @Test
    @DisplayName("install 接口 REMOTE_URL 安装成功")
    void installPlugin_whenRemoteUrl_shouldReturnSuccess() {
        PluginAdminController.InstallRequest request = new PluginAdminController.InstallRequest();
        request.setSource("REMOTE_URL");
        request.setUrl("https://example.com/demo.jar");

        Map<String, Object> serviceResult = Map.of("pluginId", "demo-plugin");
        when(pluginManagerService.installPluginBySource("REMOTE_URL", null, "https://example.com/demo.jar"))
                .thenReturn(serviceResult);

        ResponseEntity<?> response = controller.installPlugin(request);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(200, body.get("code"));
        assertEquals(serviceResult, body.get("data"));
    }

    @Test
    @DisplayName("install 接口非法 source 返回业务错误")
    void installPlugin_whenInvalidSource_shouldReturnError() {
        PluginAdminController.InstallRequest request = new PluginAdminController.InstallRequest();
        request.setSource("UNKNOWN");

        doThrow(new IllegalArgumentException("source 非法"))
                .when(pluginManagerService).installPluginBySource("UNKNOWN", null, null);

        ResponseEntity<?> response = controller.installPlugin(request);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(500, body.get("code"));
    }

    @Test
    @DisplayName("initSyncPlugins 成功时应返回200")
    void initSyncPlugins_whenSuccess_shouldReturnSuccessResponse() {
        Map<String, Object> syncResult = new HashMap<>();
        syncResult.put("runtimeTotal", 1);
        syncResult.put("inserted", 1);
        syncResult.put("skipped", 0);
        when(pluginManagerService.initMissingPluginsFromRuntime()).thenReturn(syncResult);

        ResponseEntity<?> response = controller.initSyncPlugins();
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(200, body.get("code"));
        assertEquals("success", body.get("message"));
        assertEquals(syncResult, body.get("data"));
    }

    @Test
    @DisplayName("initSyncPlugins 异常时应返回业务错误")
    void initSyncPlugins_whenException_shouldReturnErrorResponse() {
        doThrow(new RuntimeException("mock failure"))
                .when(pluginManagerService).initMissingPluginsFromRuntime();

        ResponseEntity<?> response = controller.initSyncPlugins();
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(500, body.get("code"));
    }

    @Test
    @DisplayName("getRuntimeSummary 成功时返回200")
    void getRuntimeSummary_whenSuccess_shouldReturnSuccess() {
        Map<String, Object> summary = Map.of("total", 1, "started", 1);
        when(pluginManagerService.getRuntimeSummary()).thenReturn(summary);

        ResponseEntity<?> response = controller.getRuntimeSummary();
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, body.get("code"));
        assertEquals(summary, body.get("data"));
    }

    @Test
    @DisplayName("reconcilePlugins 默认 dryRun=true")
    void reconcilePlugins_whenRequestNull_shouldUseDryRunTrue() {
        Map<String, Object> result = Map.of("dryRun", true, "fixedCount", 0);
        when(pluginManagerService.reconcilePlugins(true)).thenReturn(result);

        ResponseEntity<?> response = controller.reconcilePlugins(null);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, body.get("code"));
        assertEquals(result, body.get("data"));
    }

    @Test
    @DisplayName("listAuditLogs 成功时返回列表")
    void listAuditLogs_whenSuccess_shouldReturnList() {
        List<Map<String, Object>> logs = List.of(Map.of("source", "logs/all.log", "content", "plugin test"));
        when(pluginManagerService.listAuditLogs(50, "demo-plugin", "start")).thenReturn(logs);

        ResponseEntity<?> response = controller.listAuditLogs(50, "demo-plugin", "start");
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, body.get("code"));
        assertEquals(logs, body.get("data"));
    }
}
