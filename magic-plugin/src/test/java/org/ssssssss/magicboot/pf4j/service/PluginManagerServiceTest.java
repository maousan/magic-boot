package org.ssssssss.magicboot.pf4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @DisplayName("listAllPlugins_运行态存在但数据库不存在时_应返回运行态插件")
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
    @DisplayName("listAllPlugins_数据库与运行态同ID时_应合并且不重复")
    void listAllPlugins_whenDbAndRuntimeHaveSamePlugin_shouldMergeWithoutDuplicate() {
        PluginInfo dbPlugin = new PluginInfo();
        dbPlugin.setPluginId("demo-plugin");
        dbPlugin.setPluginName("demo-plugin");
        dbPlugin.setVersion("1.0.0");
        dbPlugin.setStatus("CREATED");
        dbPlugin.setJarPath("D:/IdeaProjects/magic-boot/plugins/magic-plugin-demo.jar");

        when(pluginInfoMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(dbPlugin));
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
        assertEquals("CREATED", plugin.get("statusEnum"));
    }

    @Test
    @DisplayName("initMissingPluginsFromRuntime_DB缺失插件时_应补录")
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
        assertEquals("D:/IdeaProjects/magic-boot/plugins/magic-plugin-demo.jar", inserted.getJarPath().replace("\\", "/"));
    }

    @Test
    @DisplayName("initMissingPluginsFromRuntime_DB已存在插件时_应跳过")
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
}
