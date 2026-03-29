package org.ssssssss.magicboot.demo;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoPluginSchedulerTest {

    @Test
    void start_shouldActivateSchedulerTask() {
        DemoPlugin plugin = createPluginWithMockedWrapper();

        plugin.start();

        assertTrue(plugin.isInfoTaskActive(), "task should be active after start");
        plugin.stop();
    }

    @Test
    void stop_shouldDeactivateSchedulerTask() {
        DemoPlugin plugin = createPluginWithMockedWrapper();
        plugin.start();

        plugin.stop();

        assertFalse(plugin.isInfoTaskActive(), "task should stop after disable or stop");
    }

    private DemoPlugin createPluginWithMockedWrapper() {
        PluginWrapper wrapper = mock(PluginWrapper.class);
        PluginDescriptor descriptor = mock(PluginDescriptor.class);
        when(wrapper.getDescriptor()).thenReturn(descriptor);
        when(wrapper.getPluginState()).thenReturn(PluginState.STARTED);
        when(descriptor.getPluginId()).thenReturn("demo-plugin");
        when(descriptor.getVersion()).thenReturn("1.0.0");

        return new DemoPlugin(wrapper);
    }
}