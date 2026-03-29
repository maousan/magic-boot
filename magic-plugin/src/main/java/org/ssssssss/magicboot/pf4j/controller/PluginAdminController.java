package org.ssssssss.magicboot.pf4j.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ssssssss.magicboot.pf4j.model.PluginInstallErrorCode;
import org.ssssssss.magicboot.pf4j.model.PluginInstallException;
import org.ssssssss.magicboot.pf4j.service.PluginManagerService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plugin/admin")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginAdminController {

    private final PluginManagerService pluginManagerService;

    @GetMapping("/list")
    public ResponseEntity<?> listPlugins() {
        try {
            List<Map<String, Object>> plugins = pluginManagerService.listAllPlugins();
            return ResponseEntity.ok(success(plugins));
        } catch (Exception e) {
            return ResponseEntity.ok(error(e.getMessage()));
        }
    }

    @GetMapping("/info/{pluginId}")
    public ResponseEntity<?> getPluginInfo(@PathVariable String pluginId) {
        try {
            Map<String, Object> info = pluginManagerService.getPluginInfo(pluginId);
            return ResponseEntity.ok(success(info));
        } catch (Exception e) {
            return ResponseEntity.ok(error(e.getMessage()));
        }
    }

    @GetMapping("/runtime/summary")
    public ResponseEntity<?> getRuntimeSummary() {
        try {
            return ResponseEntity.ok(success(pluginManagerService.getRuntimeSummary()));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Get runtime summary failed: " + e.getMessage()));
        }
    }

    @GetMapping("/runtime/{pluginId}")
    public ResponseEntity<?> getRuntimePluginInfo(@PathVariable String pluginId) {
        try {
            return ResponseEntity.ok(success(pluginManagerService.getRuntimePluginInfo(pluginId)));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Get runtime plugin info failed: " + e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPlugin(@RequestParam("file") MultipartFile file) {
        try {
            pluginManagerService.installPlugin(file);
            return ResponseEntity.ok(success("Upload and install success"));
        } catch (PluginInstallException e) {
            return ResponseEntity.ok(error(e.getErrorCode(), "Install failed: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(error(PluginInstallErrorCode.PLUGIN_MANIFEST_INVALID, "Install failed: invalid params, " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.ok(error(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED, "Install failed: upload io error, " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(error(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED, "Install failed: upload install error, " + e.getMessage()));
        }
    }

    @PostMapping("/install")
    public ResponseEntity<?> installPlugin(@RequestBody InstallRequest request) {
        try {
            if (request == null) {
                return ResponseEntity.ok(error("Install failed: request body is required"));
            }
            Map<String, Object> result = pluginManagerService.installPluginBySource(
                    request.getSource(),
                    request.getJarPath(),
                    request.getUrl()
            );
            return ResponseEntity.ok(success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(error(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED, "Install failed: invalid params, " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(error(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED, "Install failed: " + e.getMessage()));
        }
    }

    @PostMapping("/uninstall/{pluginId}")
    public ResponseEntity<?> uninstallPlugin(@PathVariable String pluginId) {
        try {
            pluginManagerService.uninstallPlugin(pluginId);
            return ResponseEntity.ok(success("Plugin uninstalled"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Plugin uninstall failed: " + e.getMessage()));
        }
    }

    @PostMapping("/start/{pluginId}")
    public ResponseEntity<?> startPlugin(@PathVariable String pluginId) {
        try {
            pluginManagerService.startPlugin(pluginId);
            return ResponseEntity.ok(success("Plugin started"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Plugin start failed: " + e.getMessage()));
        }
    }

    @PostMapping("/stop/{pluginId}")
    public ResponseEntity<?> stopPlugin(@PathVariable String pluginId) {
        try {
            pluginManagerService.stopPlugin(pluginId);
            return ResponseEntity.ok(success("Plugin stopped"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Plugin stop failed: " + e.getMessage()));
        }
    }

    @PostMapping("/reload/{pluginId}")
    public ResponseEntity<?> reloadPlugin(@PathVariable String pluginId) {
        try {
            pluginManagerService.reloadPlugin(pluginId);
            return ResponseEntity.ok(success("Plugin reloaded"));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Plugin reload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/enable/{pluginId}")
    public ResponseEntity<?> enablePlugin(@PathVariable String pluginId) {
        try {
            return ResponseEntity.ok(success(pluginManagerService.enablePlugin(pluginId)));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Plugin enable failed: " + e.getMessage()));
        }
    }

    @PostMapping("/disable/{pluginId}")
    public ResponseEntity<?> disablePlugin(@PathVariable String pluginId) {
        try {
            return ResponseEntity.ok(success(pluginManagerService.disablePlugin(pluginId)));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Plugin disable failed: " + e.getMessage()));
        }
    }

    @PostMapping("/rescan")
    public ResponseEntity<?> rescanPlugins() {
        try {
            List<String> loadedPlugins = pluginManagerService.rescanPlugins();
            Map<String, Object> result = new HashMap<>();
            result.put("loadedPlugins", loadedPlugins);
            result.put("count", loadedPlugins.size());
            return ResponseEntity.ok(success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Rescan plugins failed: " + e.getMessage()));
        }
    }

    @PostMapping("/scan-new")
    public ResponseEntity<?> scanNewPlugins() {
        try {
            return ResponseEntity.ok(success(pluginManagerService.scanNewPlugins()));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Scan new plugins failed: " + e.getMessage()));
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncPlugins() {
        try {
            return ResponseEntity.ok(success(pluginManagerService.syncPlugins()));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Sync plugins failed: " + e.getMessage()));
        }
    }

    @PostMapping("/init-sync")
    public ResponseEntity<?> initSyncPlugins() {
        try {
            Map<String, Object> result = pluginManagerService.initMissingPluginsFromRuntime();
            return ResponseEntity.ok(success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Init sync failed: " + e.getMessage()));
        }
    }

    @PostMapping("/reconcile")
    public ResponseEntity<?> reconcilePlugins(@RequestBody(required = false) ReconcileRequest request) {
        try {
            boolean dryRun = request == null || request.getDryRun() == null || request.getDryRun();
            return ResponseEntity.ok(success(pluginManagerService.reconcilePlugins(dryRun)));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Reconcile plugins failed: " + e.getMessage()));
        }
    }

    @GetMapping("/health/{pluginId}")
    public ResponseEntity<?> getPluginHealth(@PathVariable String pluginId) {
        try {
            return ResponseEntity.ok(success(pluginManagerService.getPluginHealth(pluginId)));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Get plugin health failed: " + e.getMessage()));
        }
    }

    @GetMapping("/audit/list")
    public ResponseEntity<?> listAuditLogs(@RequestParam(name = "limit", defaultValue = "100") Integer limit,
                                           @RequestParam(name = "pluginId", required = false) String pluginId,
                                           @RequestParam(name = "action", required = false) String action) {
        try {
            return ResponseEntity.ok(success(pluginManagerService.listAuditLogs(limit, pluginId, action)));
        } catch (Exception e) {
            return ResponseEntity.ok(error("Query audit logs failed: " + e.getMessage()));
        }
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("errorCode", PluginInstallErrorCode.SUCCESS.name());
        result.put("message", "success");
        result.put("data", data);
        return result;
    }

    private Map<String, Object> error(String message) {
        return error(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED, message);
    }

    private Map<String, Object> error(PluginInstallErrorCode errorCode, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("errorCode", errorCode.name());
        result.put("message", message);
        return result;
    }

    @Data
    public static class InstallRequest {
        private String source;
        private String jarPath;
        private String url;
    }

    @Data
    public static class ReconcileRequest {
        private Boolean dryRun;
    }
}
