package org.ssssssss.magicboot.zintis.led;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginWrapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ZintisLedPluginLifecycleTest {

    @Test
    void stop_shouldCloseApplicationContext() {
        ZintisLedPlugin plugin = createPlugin();
        plugin.createApplicationContext();
        assertTrue(plugin.isApplicationContextActive());

        plugin.stop();

        assertFalse(plugin.isApplicationContextActive());
    }

    private ZintisLedPlugin createPlugin() {
        PluginWrapper wrapper = mock(PluginWrapper.class);
        PluginDescriptor descriptor = mock(PluginDescriptor.class);
        when(wrapper.getDescriptor()).thenReturn(descriptor);
        when(descriptor.getPluginId()).thenReturn("zintis-led-plugin");
        when(descriptor.getVersion()).thenReturn("1.0.0");
        return new ZintisLedPlugin(wrapper);
    }
}