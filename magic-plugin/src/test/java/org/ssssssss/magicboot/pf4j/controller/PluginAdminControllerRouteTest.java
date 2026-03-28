package org.ssssssss.magicboot.pf4j.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.ssssssss.magicboot.pf4j.entity.PluginInfo;
import org.ssssssss.magicboot.pf4j.service.PluginManagerService;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PluginAdminControllerRouteTest {

    private final PluginManagerService pluginManagerService = mock(PluginManagerService.class);
    private final PluginAdminController controller = new PluginAdminController(pluginManagerService);
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Test
    @DisplayName("old by-path install route should return 404")
    void oldByPathInstallRoute_shouldReturn404() throws Exception {
        mockMvc.perform(post("/plugin/admin/install/by-path")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"jarPath\":\"D:/tmp/demo.jar\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("new install route should be available")
    void newInstallRoute_shouldBeAvailable() throws Exception {
        when(pluginManagerService.installPluginBySource(anyString(), any(), any()))
                .thenReturn(Map.of("pluginId", "demo-plugin"));
        mockMvc.perform(post("/plugin/admin/install")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"source\":\"LOCAL_PATH\",\"jarPath\":\"D:/tmp/demo.jar\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("upload route should be available")
    void uploadRoute_shouldBeAvailable() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "demo.jar", "application/java-archive", new byte[]{1, 2, 3});
        when(pluginManagerService.installPlugin(any())).thenReturn(new PluginInfo());
        mockMvc.perform(multipart("/plugin/admin/upload").file(file))
                .andExpect(status().isOk());
    }
}
