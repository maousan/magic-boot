package org.ssssssss.magicboot.pf4j.frontend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件前端页面控制器
 * 处理 /plugin/{pluginId}/** 请求
 */
@Slf4j
@RestController
@RequestMapping("/plugin")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginFrontendController {

    private final FrontendExtensionProcessor processor;
    private final PluginManager pluginManager;

    /**
     * 获取所有插件的前端元数据
     */
    @GetMapping("/frontend/metadata")
    public Map<String, Object> getAllMetadata() {
        Collection<FrontendMetadata> allMetadata = processor.getAllMetadata();
        Map<String, Object> result = new HashMap<>();
        result.put("total", allMetadata.size());
        result.put("plugins", allMetadata);
        return result;
    }

    /**
     * 获取单个插件的前端元数据
     */
    @GetMapping("/{pluginId}/metadata")
    public Object getMetadata(@PathVariable String pluginId) {
        FrontendMetadata metadata = processor.getMetadata(pluginId);
        if (metadata == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Plugin not found or has no frontend");
            error.put("pluginId", pluginId);
            return error;
        }
        return metadata;
    }

    /**
     * 返回插件首页
     */
    @GetMapping(value = "/{pluginId}", produces = "text/html;charset=UTF-8")
    public String getPluginHome(@PathVariable String pluginId) {
        return renderPluginPage(pluginId);
    }

    /**
     * 处理插件子路由（返回相同的 HTML 容器，由前端路由处理）
     */
    @GetMapping(value = "/{pluginId}/**", produces = "text/html;charset=UTF-8")
    public String getPluginPage(@PathVariable String pluginId) {
        return renderPluginPage(pluginId);
    }

    /**
     * 渲染插件 HTML 容器
     */
    private String renderPluginPage(String pluginId) {
        FrontendMetadata metadata = processor.getMetadata(pluginId);
        if (metadata == null) {
            // 检查插件是否存在
            PluginWrapper plugin = pluginManager.getPlugin(pluginId);
            if (plugin == null) {
                log.warn("插件页面请求失败：插件 [{}] 不存在", pluginId);
                return generateNotFoundPage("插件不存在");
            } else {
                log.debug("插件 [{}] 没有前端扩展", pluginId);
                return generateNotFoundPage("插件没有前端页面");
            }
        }

        log.debug("渲染插件 [{}] 页面", pluginId);
        return generatePluginHTML(pluginId, metadata);
    }

    /**
     * 生成插件 HTML 容器（支持独立运行，不再依赖外部 Vue）
     */
    private String generatePluginHTML(String pluginId, FrontendMetadata metadata) {
        String displayName = escapeHtml(metadata.getDisplayName());

        return """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    html, body { width: 100%%; height: 100%%; }
                    #app { width: 100%%; height: 100vh; }
                </style>
            </head>
            <body>
                <div id="app">加载中...</div>
                <script src="/plugin/%s/static/%s"></script>
            </body>
            </html>
            """.formatted(displayName, pluginId, metadata.getEntryScript());
    }

    /**
     * 生成 404 页面
     */
    private String generateNotFoundPage(String message) {
        return """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>插件未找到</title>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background: #f5f5f5; }
                    .container { text-align: center; padding: 40px; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                    h1 { color: #e74c3c; margin-bottom: 16px; }
                    p { color: #666; margin-bottom: 20px; }
                    a { color: #3498db; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>404</h1>
                    <p>%s</p>
                    <a href="/">返回首页</a>
                </div>
            </body>
            </html>
            """.formatted(escapeHtml(message));
    }

    /**
     * 转义 HTML 特殊字符
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
