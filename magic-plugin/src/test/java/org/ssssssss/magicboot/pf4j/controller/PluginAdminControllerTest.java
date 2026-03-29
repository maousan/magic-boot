package org.ssssssss.magicboot.pf4j.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.ssssssss.magicboot.pf4j.model.PluginInstallErrorCode;
import org.ssssssss.magicboot.pf4j.model.PluginInstallException;
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
    @DisplayName("install LOCAL_PATH should return success")
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
        assertEquals("SUCCESS", body.get("errorCode"));
        assertEquals(serviceResult, body.get("data"));
    }

    @Test
    @DisplayName("install invalid source should return standardized error code")
    void installPlugin_whenInvalidSource_shouldReturnError() {
        PluginAdminController.InstallRequest request = new PluginAdminController.InstallRequest();
        request.setSource("UNKNOWN");

        doThrow(new IllegalArgumentException("invalid source")).when(pluginManagerService)
                .installPluginBySource("UNKNOWN", null, null);

        ResponseEntity<?> response = controller.installPlugin(request);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(500, body.get("code"));
        assertEquals("PLUGIN_INSTALL_FAILED", body.get("errorCode"));
    }

    @Test
    @DisplayName("upload install exception should return mapped error code")
    void uploadPlugin_whenInstallException_shouldReturnErrorCode() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bad.zip", "application/zip", new byte[]{1});
        doThrow(new PluginInstallException(
                PluginInstallErrorCode.PLUGIN_CHECKSUM_MISMATCH,
                "Plugin checksum mismatch"
        )).when(pluginManagerService).installPlugin(file);

        ResponseEntity<?> response = controller.uploadPlugin(file);
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(500, body.get("code"));
        assertEquals("PLUGIN_CHECKSUM_MISMATCH", body.get("errorCode"));
    }

    @Test
    @DisplayName("initSync should return success")
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
        assertEquals("SUCCESS", body.get("errorCode"));
        assertEquals("success", body.get("message"));
        assertEquals(syncResult, body.get("data"));
    }

    @Test
    @DisplayName("listAuditLogs should return list")
    void listAuditLogs_whenSuccess_shouldReturnList() {
        List<Map<String, Object>> logs = List.of(Map.of("source", "logs/all.log", "content", "plugin test"));
        when(pluginManagerService.listAuditLogs(50, "demo-plugin", "start")).thenReturn(logs);

        ResponseEntity<?> response = controller.listAuditLogs(50, "demo-plugin", "start");
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, body.get("code"));
        assertEquals("SUCCESS", body.get("errorCode"));
        assertEquals(logs, body.get("data"));
    }
}
