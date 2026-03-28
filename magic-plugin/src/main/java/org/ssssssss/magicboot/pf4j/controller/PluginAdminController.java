package org.ssssssss.magicboot.pf4j.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ssssssss.magicboot.pf4j.service.PluginManagerService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件管理 Controller
 */
@RestController
@RequestMapping("/plugin/admin")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginAdminController {

    private final PluginManagerService pluginManagerService;

    /**
     * 获取所有插件列表
     */
    @GetMapping("/list")
    public ResponseEntity<?> listPlugins() {
        try {
            List<Map<String, Object>> plugins = pluginManagerService.listAllPlugins();
            return ResponseEntity.ok(success(plugins));
        } catch (Exception e) {
            return ResponseEntity.ok(error(e.getMessage()));
        }
    }

    /**
     * 获取插件详情
     */
    @GetMapping("/info/{pluginId}")
    public ResponseEntity<?> getPluginInfo(@PathVariable String pluginId) {
        try {
            Map<String, Object> info = pluginManagerService.getPluginInfo(pluginId);
            return ResponseEntity.ok(success(info));
        } catch (Exception e) {
            return ResponseEntity.ok(error(e.getMessage()));
        }
    }

    /**
     * 安装插件
     */
    @PostMapping("/install")
    public ResponseEntity<?> installPlugin(@RequestParam("file") MultipartFile file) {
        try {
            // 检查权限
//            StpUtil.checkLogin();
            pluginManagerService.installPlugin(file);
            return ResponseEntity.ok(success("插件安装成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(error(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.ok(error("插件安装失败：" + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(error("插件安装失败：" + e.getMessage()));
        }
    }

    /**
     * 卸载插件
     */
    @PostMapping("/uninstall/{pluginId}")
    public ResponseEntity<?> uninstallPlugin(@PathVariable String pluginId) {
        try {
//            StpUtil.checkLogin();
            pluginManagerService.uninstallPlugin(pluginId);
            return ResponseEntity.ok(success("插件卸载成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("插件卸载失败：" + e.getMessage()));
        }
    }

    /**
     * 启动插件
     */
    @PostMapping("/start/{pluginId}")
    public ResponseEntity<?> startPlugin(@PathVariable String pluginId) {
        try {
//            StpUtil.checkLogin();
            pluginManagerService.startPlugin(pluginId);
            return ResponseEntity.ok(success("插件启动成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("插件启动失败：" + e.getMessage()));
        }
    }

    /**
     * 停止插件
     */
    @PostMapping("/stop/{pluginId}")
    public ResponseEntity<?> stopPlugin(@PathVariable String pluginId) {
        try {
//            StpUtil.checkLogin();
            pluginManagerService.stopPlugin(pluginId);
            return ResponseEntity.ok(success("插件停止成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("插件停止失败：" + e.getMessage()));
        }
    }

    /**
     * 重新加载插件
     */
    @PostMapping("/reload/{pluginId}")
    public ResponseEntity<?> reloadPlugin(@PathVariable String pluginId) {
        try {
//            StpUtil.checkLogin();
            pluginManagerService.reloadPlugin(pluginId);
            return ResponseEntity.ok(success("插件重新加载成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("插件重新加载失败：" + e.getMessage()));
        }
    }

    /**
     * 重新扫描插件目录
     */
    @PostMapping("/rescan")
    public ResponseEntity<?> rescanPlugins() {
        try {
//            StpUtil.checkLogin();
            List<String> loadedPlugins = pluginManagerService.rescanPlugins();
            Map<String, Object> result = new HashMap<>();
            result.put("loadedPlugins", loadedPlugins);
            result.put("count", loadedPlugins.size());
            return ResponseEntity.ok(success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(error("重新扫描插件失败：" + e.getMessage()));
        }
    }

    /**
     * 手动触发运行态插件增量同步到数据库
     */
    @PostMapping("/init-sync")
    public ResponseEntity<?> initSyncPlugins() {
        try {
//            StpUtil.checkLogin();
            Map<String, Object> result = pluginManagerService.initMissingPluginsFromRuntime();
            return ResponseEntity.ok(success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(error("初始化插件同步失败：" + e.getMessage()));
        }
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", message);
        return result;
    }
}
