package org.ssssssss.magicboot.dongxinheping.extension;

import org.pf4j.Extension;
import org.ssssssss.magicboot.plugin.api.frontend.EmbedType;
import org.ssssssss.magicboot.plugin.api.frontend.FrontendExtension;
import org.ssssssss.magicboot.plugin.api.frontend.PluginMenuItem;
import org.ssssssss.magicboot.plugin.api.frontend.PluginRoute;

@Extension
public class DongxinhepingFrontendExtension implements FrontendExtension {

    private static final String PLUGIN_ID = "dongxinheping-plugin";
    private static final String PDA_INDEX = "/plugin/dongxinheping-plugin/static/pda/index.html";

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    @Override
    public String getDisplayName() {
        return "东信和平 PDA 页面";
    }

    @Override
    public String getEntryScript() {
        return "console.js";
    }

    @Override
    public PluginRoute[] getRoutes() {
        return new PluginRoute[] {
                new PluginRoute("/pda", "DongxinhepingPda", "PDA 首页", "mobile", true)
        };
    }

    @Override
    public PluginMenuItem[] getMenuItems() {
        return new PluginMenuItem[] {
                new PluginMenuItem("dongxinheping-pda", null, "东信和平 PDA", "mobile", "/pda", 200)
        };
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[] { "plugin:dongxinheping:view" };
    }

    @Override
    public EmbedType getEmbedType() {
        return EmbedType.IFRAME;
    }

    @Override
    public String getExternalUrl() {
        return PDA_INDEX;
    }
}
