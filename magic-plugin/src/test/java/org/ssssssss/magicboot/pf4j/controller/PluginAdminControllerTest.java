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
    @DisplayName("initSyncPlugins_成功时应返回200")
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
    @DisplayName("initSyncPlugins_异常时应返回业务错误")
    void initSyncPlugins_whenException_shouldReturnErrorResponse() {
        doThrow(new RuntimeException("mock failure"))
                .when(pluginManagerService).initMissingPluginsFromRuntime();

        ResponseEntity<?> response = controller.initSyncPlugins();
        Map<?, ?> body = (Map<?, ?>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(500, body.get("code"));
    }
}

