package org.ssssssss.magicboot.pf4j.frontend;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

/**
 * 插件静态资源控制器
 * 从插件 ClassLoader 加载资源
 */
@Slf4j
@RestController
@RequestMapping("/plugin/{pluginId}/static")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginStaticResourceController {

    private final PluginManager pluginManager;

    /**
     * 服务插件静态资源
     */
    @GetMapping("/**")
    public ResponseEntity<InputStreamResource> serveStaticResource(
            @PathVariable String pluginId,
            HttpServletRequest request) {

        // 获取插件
        PluginWrapper plugin = pluginManager.getPlugin(pluginId);
        if (plugin == null) {
            log.warn("静态资源请求失败：插件 [{}] 不存在", pluginId);
            return ResponseEntity.notFound().build();
        }

        // 提取资源路径
        String requestPath = request.getRequestURI();
        String staticPrefix = "/plugin/" + pluginId + "/static/";
        int staticIndex = requestPath.indexOf(staticPrefix);

        if (staticIndex == -1) {
            return ResponseEntity.badRequest().build();
        }

        String resourcePath = requestPath.substring(staticIndex + staticPrefix.length());

        // 安全检查：防止路径穿越攻击
        if (resourcePath.contains("..") || resourcePath.contains("\\")) {
            log.warn("检测到非法路径访问尝试: {}", resourcePath);
            return ResponseEntity.badRequest().build();
        }

        // 从 ClassLoader 加载资源
        ClassLoader classLoader = plugin.getPluginClassLoader();
        String fullPath = "static/" + resourcePath;
        InputStream inputStream = classLoader.getResourceAsStream(fullPath);

        if (inputStream == null) {
            log.debug("插件 [{}] 静态资源不存在: {}", pluginId, fullPath);
            return ResponseEntity.notFound().build();
        }

        // 确定内容类型
        MediaType contentType = determineContentType(resourcePath);

        log.debug("服务插件 [{}] 静态资源: {}", pluginId, fullPath);

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000") // 1 年缓存
                .body(new InputStreamResource(inputStream));
    }

    /**
     * 根据文件扩展名确定内容类型
     */
    private MediaType determineContentType(String path) {
        if (path.endsWith(".js")) {
            return MediaType.parseMediaType("application/javascript");
        }
        if (path.endsWith(".css")) {
            return MediaType.parseMediaType("text/css");
        }
        if (path.endsWith(".html")) {
            return MediaType.TEXT_HTML;
        }
        if (path.endsWith(".json")) {
            return MediaType.APPLICATION_JSON;
        }
        if (path.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (path.endsWith(".svg")) {
            return MediaType.parseMediaType("image/svg+xml");
        }
        if (path.endsWith(".woff") || path.endsWith(".woff2")) {
            return MediaType.parseMediaType("font/woff2");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
