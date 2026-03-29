package org.ssssssss.magicboot.pf4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;
import org.ssssssss.magicboot.pf4j.entity.PluginInfo;
import org.ssssssss.magicboot.pf4j.mapper.PluginInfoMapper;
import org.ssssssss.magicboot.pf4j.model.PluginInstallErrorCode;
import org.ssssssss.magicboot.pf4j.model.PluginInstallException;
import org.ssssssss.magicboot.pf4j.model.PluginStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**

 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginManagerService {

    private final PluginManager pluginManager;
    private final PluginInfoMapper pluginInfoMapper;
    private final PluginProperties pluginProperties;
    private final ZipPluginInstaller zipPluginInstaller;

    public PluginManagerService(PluginManager pluginManager,
                                PluginInfoMapper pluginInfoMapper,
                                PluginProperties pluginProperties) {
        this.pluginManager = pluginManager;
        this.pluginInfoMapper = pluginInfoMapper;
        this.pluginProperties = pluginProperties;
        this.zipPluginInstaller = new ZipPluginInstaller(pluginProperties);
    }

    // ====================

    /**


     */
    public List<Map<String, Object>> listAllPlugins() {
        List<PluginInfo> dbPlugins = pluginInfoMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Map<String, Object>> mergedPlugins = new LinkedHashMap<>();


        dbPlugins.stream()
                .map(this::toPluginMap)
                .forEach(item -> mergedPlugins.put((String) item.get("pluginId"), item));


        pluginManager.getPlugins().forEach(wrapper -> {
            String pluginId = wrapper.getPluginId();
            Map<String, Object> runtimePlugin = toRuntimePluginMap(wrapper);
            Map<String, Object> dbPlugin = mergedPlugins.get(pluginId);
            if (dbPlugin == null) {
                mergedPlugins.put(pluginId, runtimePlugin);
            } else {
                dbPlugin.put("runtimeState", runtimePlugin.get("runtimeState"));

                if (dbPlugin.get("statusEnum") == null) {
                    dbPlugin.put("statusEnum", runtimePlugin.get("statusEnum"));
                    dbPlugin.put("status", runtimePlugin.get("status"));
                }
            }
        });

        return new ArrayList<>(mergedPlugins.values());
    }

    public Map<String, Object> getRuntimeSummary() {
        List<PluginWrapper> plugins = pluginManager.getPlugins();
        Map<String, Long> stateCount = plugins.stream()
                .collect(Collectors.groupingBy(wrapper -> wrapper.getPluginState().name(), Collectors.counting()));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", plugins.size());
        result.put("started", stateCount.getOrDefault(PluginState.STARTED.name(), 0L));
        result.put("stopped", stateCount.getOrDefault(PluginState.STOPPED.name(), 0L));
        result.put("disabled", stateCount.getOrDefault(PluginState.DISABLED.name(), 0L));
        result.put("created", stateCount.getOrDefault(PluginState.CREATED.name(), 0L));
        result.put("resolved", stateCount.getOrDefault(PluginState.RESOLVED.name(), 0L));
        result.put("unresolved", 0L);
        result.put("runtimeTime", LocalDateTime.now());
        return result;
    }

    public Map<String, Object> getRuntimePluginInfo(String pluginId) {
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        if (wrapper == null) {
            throw new RuntimeException("Plugin runtime not found: " + pluginId);
        }
        PluginDescriptor descriptor = wrapper.getDescriptor();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pluginId", descriptor.getPluginId());
        result.put("version", descriptor.getVersion());
        result.put("provider", descriptor.getProvider());
        result.put("pluginClass", descriptor.getPluginClass());
        result.put("runtimeState", wrapper.getPluginState().name());
        result.put("pluginPath", wrapper.getPluginPath() == null ? "" : wrapper.getPluginPath().toString());
        result.put("classLoader", wrapper.getPluginClassLoader() == null ? "" : wrapper.getPluginClassLoader().getClass().getName());
        result.put("dependencies", descriptor.getDependencies());
        result.put("lastError", "");
        return result;
    }

    /**


     */
    public List<String> scanNewPlugins() {
        List<String> newPlugins = new ArrayList<>();
        Set<String> dbPluginIds = pluginInfoMapper.selectList(new LambdaQueryWrapper<>())
                .stream()
                .map(PluginInfo::getPluginId)
                .collect(Collectors.toSet());

        List<Path> pluginJars = getPluginJars();
        for (Path jarPath : pluginJars) {
            try {

                String pluginId = pluginManager.loadPlugin(jarPath);
                pluginManager.unloadPlugin(pluginId);

                if (!dbPluginIds.contains(pluginId)) {
                    newPlugins.add(jarPath.toString());
                }
            } catch (Exception e) {
                log.warn("Scan plugin failed: {}", jarPath, e);
            }
        }

        return newPlugins;
    }

    /**


     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loadAndInstallPlugin(String jarPath) {
        Path path = Paths.get(jarPath);
        String pluginId = pluginManager.loadPlugin(path);
        PluginInfo info = savePluginToDatabase(pluginId, path);
        return toPluginMap(info);
    }

    /**

     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> installPluginBySource(String source, String jarPath, String url) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("source cannot be blank, supported values: LOCAL_PATH or REMOTE_URL");
        }

        String normalized = source.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "LOCAL_PATH" -> {
                if (jarPath == null || jarPath.isBlank()) {
                    throw new IllegalArgumentException("source=LOCAL_PATH requires jarPath");
                }
                yield loadAndInstallPlugin(jarPath);
            }
            case "REMOTE_URL" -> {
                if (url == null || url.isBlank()) {
                    throw new IllegalArgumentException("source=REMOTE_URL requires url");
                }
                yield installFromRemoteUrl(url);
            }
            default -> throw new IllegalArgumentException("invalid source, only LOCAL_PATH or REMOTE_URL is supported");
        };
    }

    /**

     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> installFromRemoteUrl(String url) {
        URI uri = parseAndValidateRemoteUri(url);
        String fileName = resolveRemoteJarName(uri);

        Path tempFile = null;
        Path finalFile = null;
        try {
            Path pluginDir = ensurePluginDirectoryExists();
            tempFile = pluginDir.resolve(fileName + ".download");
            finalFile = pluginDir.resolve(fileName);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(60))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Remote download failed, HTTP status: " + response.statusCode());
            }

            Files.write(tempFile, response.body());
            Files.move(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING);
            return loadAndInstallPlugin(finalFile.toString());
        } catch (IllegalArgumentException e) {
            cleanupDownloadedFiles(tempFile, finalFile);
            throw e;
        } catch (Exception e) {
            cleanupDownloadedFiles(tempFile, finalFile);
            throw new RuntimeException("Remote download install failed: " + e.getMessage(), e);
        }
    }

    /**


     */
    public List<Map<String, Object>> syncPlugins() {
        List<Map<String, Object>> newPlugins = new ArrayList<>();
        Set<String> dbPluginIds = pluginInfoMapper.selectList(new LambdaQueryWrapper<>())
                .stream()
                .map(PluginInfo::getPluginId)
                .collect(Collectors.toSet());

        List<Path> pluginJars = getPluginJars();
        for (Path jarPath : pluginJars) {
            try {
                String pluginId = pluginManager.loadPlugin(jarPath);
                if (!dbPluginIds.contains(pluginId)) {
                    PluginInfo info = savePluginToDatabase(pluginId, jarPath);
                    newPlugins.add(toPluginMap(info));
                }
            } catch (Exception e) {
                log.warn("Load plugin failed: {}", jarPath, e);
            }
        }

        return newPlugins;
    }

    /**


     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> initMissingPluginsFromRuntime() {
        List<PluginInfo> dbPlugins = pluginInfoMapper.selectList(new LambdaQueryWrapper<>());
        Set<String> dbPluginIds = dbPlugins.stream()
                .map(PluginInfo::getPluginId)
                .collect(Collectors.toSet());

        int inserted = 0;
        int skipped = 0;

        for (PluginWrapper wrapper : pluginManager.getPlugins()) {
            String pluginId = wrapper.getPluginId();
            if (dbPluginIds.contains(pluginId)) {
                skipped++;
                continue;
            }
            PluginInfo info = buildPluginInfoFromRuntime(wrapper);
            pluginInfoMapper.insert(info);
            dbPluginIds.add(pluginId);
            inserted++;
            log.info("Startup sync inserted plugin record: {} -> {}", pluginId, info.getJarPath());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("runtimeTotal", pluginManager.getPlugins().size());
        result.put("inserted", inserted);
        result.put("skipped", skipped);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reconcilePlugins(boolean dryRun) {
        List<PluginInfo> dbPlugins = pluginInfoMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, PluginInfo> dbByPluginId = dbPlugins.stream()
                .collect(Collectors.toMap(PluginInfo::getPluginId, p -> p, (a, b) -> a, LinkedHashMap::new));

        List<PluginWrapper> runtimePlugins = pluginManager.getPlugins();
        Map<String, PluginWrapper> runtimeByPluginId = runtimePlugins.stream()
                .collect(Collectors.toMap(PluginWrapper::getPluginId, p -> p, (a, b) -> a, LinkedHashMap::new));

        Set<String> missingInDb = runtimeByPluginId.keySet().stream()
                .filter(id -> !dbByPluginId.containsKey(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> missingInRuntime = dbByPluginId.keySet().stream()
                .filter(id -> !runtimeByPluginId.containsKey(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<String> missingInDir = dbPlugins.stream()
                .filter(this::hasJarPath)
                .filter(info -> !Files.exists(Paths.get(info.getJarPath())))
                .map(PluginInfo::getPluginId)
                .collect(Collectors.toList());

        int fixedCount = 0;
        List<String> fixedActions = new ArrayList<>();
        List<String> failedActions = new ArrayList<>();

        if (!dryRun) {
            for (String pluginId : missingInDb) {
                try {
                    PluginInfo info = buildPluginInfoFromRuntime(runtimeByPluginId.get(pluginId));
                    pluginInfoMapper.insert(info);
                    fixedCount++;
                    fixedActions.add("insert-db:" + pluginId);
                } catch (Exception ex) {
                    failedActions.add("insert-db:" + pluginId + ":" + ex.getMessage());
                }
            }

            for (String pluginId : missingInRuntime) {
                PluginInfo info = dbByPluginId.get(pluginId);
                if (info == null || !hasJarPath(info)) {
                    failedActions.add("load-runtime:" + pluginId + ":jarPath-empty");
                    continue;
                }
                try {
                    Path jarPath = Paths.get(info.getJarPath());
                    if (!Files.exists(jarPath)) {
                        failedActions.add("load-runtime:" + pluginId + ":jar-not-found");
                        continue;
                    }
                    pluginManager.loadPlugin(jarPath);
                    if (pluginProperties.isAutoStart()) {
                        pluginManager.startPlugin(pluginId);
                    }
                    fixedCount++;
                    fixedActions.add("load-runtime:" + pluginId);
                } catch (Exception ex) {
                    failedActions.add("load-runtime:" + pluginId + ":" + ex.getMessage());
                }
            }

            for (String pluginId : missingInDir) {
                try {
                    PluginInfo info = dbByPluginId.get(pluginId);
                    if (info != null && !PluginStatus.ERROR.name().equals(info.getStatus())) {
                        info.setStatus(PluginStatus.ERROR.name());
                        info.setUpdateTime(LocalDateTime.now());
                        pluginInfoMapper.updateById(info);
                        fixedCount++;
                        fixedActions.add("mark-error:" + pluginId);
                    }
                } catch (Exception ex) {
                    failedActions.add("mark-error:" + pluginId + ":" + ex.getMessage());
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dryRun", dryRun);
        result.put("missingInDb", new ArrayList<>(missingInDb));
        result.put("missingInRuntime", new ArrayList<>(missingInRuntime));
        result.put("missingInDir", missingInDir);
        result.put("fixedCount", fixedCount);
        result.put("fixedActions", fixedActions);
        result.put("failedActions", failedActions);
        return result;
    }

    /**

     */
    public Map<String, Object> getPluginInfo(String pluginId) {
        PluginInfo info = pluginInfoMapper.selectOne(new LambdaQueryWrapper<PluginInfo>()
                .eq(PluginInfo::getPluginId, pluginId));
        if (info == null) {
            throw new RuntimeException("Plugin not found: " + pluginId);
        }
        return toPluginMap(info);
    }

    // ====================

    /**

     */
    @Transactional(rollbackFor = Exception.class)
    public PluginInfo installPlugin(MultipartFile file) throws IOException {
        if (pluginProperties.isUploadZipOnly()) {
            ZipPluginInstaller.InstallPackage prepared = zipPluginInstaller.prepare(file);
            InstallGovernanceMeta governance = InstallGovernanceMeta.forZipUpload(prepared);
            return installLoadedPlugin(prepared.jarPath(), prepared.pluginId(), prepared.version(), governance);
        }

        String originalName = Optional.ofNullable(file == null ? null : file.getOriginalFilename()).orElse("");
        String lowerName = originalName.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".zip")) {
            ZipPluginInstaller.InstallPackage prepared = zipPluginInstaller.prepare(file);
            InstallGovernanceMeta governance = InstallGovernanceMeta.forZipUpload(prepared);
            return installLoadedPlugin(prepared.jarPath(), prepared.pluginId(), prepared.version(), governance);
        }
        if (!lowerName.endsWith(".jar")) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_INVALID_TYPE, "Only ZIP or JAR plugin packages are supported");
        }
        if (file == null || file.isEmpty()) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_MANIFEST_INVALID, "Upload file is required");
        }

        Path pluginDir = ensurePluginDirectoryExists();
        String safeJarName = sanitizeUploadedJarName(originalName);
        Path targetJar = resolveNonConflictingTargetPath(pluginDir, safeJarName);
        file.transferTo(targetJar.toFile());

        InstallGovernanceMeta governance = InstallGovernanceMeta.forLegacyJarUpload();
        return installLoadedPlugin(targetJar, null, null, governance);
    }

    @Transactional(rollbackFor = Exception.class)
    public void uninstallPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);

        if (info == null) {
            throw new RuntimeException("Plugin not found: " + pluginId);
        }


        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STARTED) {
            pluginManager.stopPlugin(pluginId);
        }


        pluginManager.unloadPlugin(pluginId);


        try {
            Files.deleteIfExists(Paths.get(info.getJarPath()));
        } catch (IOException e) {
            log.warn("Failed to delete plugin file: {}", info.getJarPath(), e);
        }


        pluginInfoMapper.deleteById(info.getId());
        log.info("Plugin uninstalled successfully: {}", pluginId);
    }

    // ====================

    /**

     */
    public void startPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);
        if (info == null) {
            throw new RuntimeException("Plugin not found: " + pluginId);
        }

        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STARTED) {
            throw new RuntimeException("Plugin already started: " + pluginId);
        }

        PluginState newState = pluginManager.startPlugin(pluginId);
        if (newState != PluginState.STARTED) {
            throw new RuntimeException("Plugin start failed: " + newState);
        }

        info.setStatus(PluginStatus.STARTED.name());
        info.setUpdateTime(LocalDateTime.now());
        pluginInfoMapper.updateById(info);
        log.info("Plugin started successfully: {}", pluginId);
    }

    /**

     */
    public void stopPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);
        if (info == null) {
            throw new RuntimeException("Plugin not found: " + pluginId);
        }

        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STOPPED || state == PluginState.CREATED) {
            throw new RuntimeException("Plugin already stopped: " + pluginId);
        }

        pluginManager.stopPlugin(pluginId);

        info.setStatus(PluginStatus.STOPPED.name());
        info.setUpdateTime(LocalDateTime.now());
        pluginInfoMapper.updateById(info);
        log.info("Plugin stopped successfully: {}", pluginId);
    }

    /**

     */
    @Transactional(rollbackFor = Exception.class)
    public void reloadPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);
        if (info == null) {
            throw new RuntimeException("Plugin not found: " + pluginId);
        }


        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STARTED) {
            pluginManager.stopPlugin(pluginId);
        }


        pluginManager.unloadPlugin(pluginId);


        Path jarPath = Paths.get(info.getJarPath());
        pluginManager.loadPlugin(jarPath);


        pluginManager.startPlugin(pluginId);

        info.setStatus(PluginStatus.STARTED.name());
        info.setUpdateTime(LocalDateTime.now());
        pluginInfoMapper.updateById(info);
        log.info("Plugin reloaded successfully: {}", pluginId);
    }

    /**

     */
    public Map<String, Object> enablePlugin(String pluginId) {
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        if (wrapper == null) {
            throw new RuntimeException("Plugin not found: " + pluginId);
        }
        pluginManager.enablePlugin(pluginId);

        PluginWrapper updated = pluginManager.getPlugin(pluginId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pluginId", pluginId);
        result.put("runtimeState", updated == null ? "UNKNOWN" : updated.getPluginState().name());
        result.put("note", "runtime-only enable");
        return result;
    }

    public Map<String, Object> disablePlugin(String pluginId) {
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        if (wrapper == null) {
            throw new RuntimeException("Plugin not found: " + pluginId);
        }
        if (wrapper.getPluginState() == PluginState.STARTED) {
            pluginManager.stopPlugin(pluginId);
        }
        pluginManager.disablePlugin(pluginId);

        PluginWrapper updated = pluginManager.getPlugin(pluginId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pluginId", pluginId);
        result.put("runtimeState", updated == null ? "UNKNOWN" : updated.getPluginState().name());
        result.put("note", "runtime-only disable");
        return result;
    }

    public List<String> rescanPlugins() {
        List<String> loadedPlugins = new ArrayList<>();
        List<Path> pluginJars = getPluginJars();

        for (Path jarPath : pluginJars) {
            try {
                pluginManager.loadPlugin(jarPath);
                loadedPlugins.add(jarPath.getFileName().toString());
            } catch (Exception e) {
                log.warn("Load plugin failed: {}", jarPath, e);
            }
        }

        log.info("Rescan completed, loaded {} plugins", loadedPlugins.size());
        return loadedPlugins;
    }

    // ====================

    /**

     */
    public Map<String, Object> getPluginHealth(String pluginId) {
        PluginInfo dbInfo = getPluginInfoByPluginId(pluginId);
        PluginWrapper runtime = pluginManager.getPlugin(pluginId);

        boolean inDb = dbInfo != null;
        boolean inRuntime = runtime != null;
        String jarPath = inDb
                ? Optional.ofNullable(dbInfo.getJarPath()).orElse("")
                : (runtime != null && runtime.getPluginPath() != null ? runtime.getPluginPath().toString() : "");
        boolean jarExists = !jarPath.isBlank() && Files.exists(Paths.get(jarPath));

        String health;
        if (inRuntime && jarExists) {
            health = "UP";
        } else if (inDb || inRuntime) {
            health = "WARN";
        } else {
            health = "DOWN";
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pluginId", pluginId);
        result.put("health", health);
        result.put("inDb", inDb);
        result.put("inRuntime", inRuntime);
        result.put("runtimeState", runtime == null ? "NOT_LOADED" : runtime.getPluginState().name());
        result.put("jarPath", jarPath);
        result.put("jarExists", jarExists);
        result.put("checkedAt", LocalDateTime.now());
        return result;
    }

    public List<Map<String, Object>> listAuditLogs(int limit, String pluginId, String action) {
        int safeLimit = Math.max(1, Math.min(limit, 500));
        List<String> candidates = List.of("logs/all.log", "logs/error.log");
        Deque<Map<String, Object>> deque = new ArrayDeque<>();

        for (String file : candidates) {
            Path logPath = Paths.get(file);
            if (!Files.exists(logPath)) {
                continue;
            }
            try {
                List<String> lines = Files.readAllLines(logPath, StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (!isAuditLine(line)) {
                        continue;
                    }
                    if (pluginId != null && !pluginId.isBlank() && !line.contains(pluginId)) {
                        continue;
                    }
                    if (action != null && !action.isBlank() && !line.toLowerCase(Locale.ROOT).contains(action.toLowerCase(Locale.ROOT))) {
                        continue;
                    }
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("source", file);
                    item.put("content", line);
                    deque.addLast(item);
                    while (deque.size() > safeLimit) {
                        deque.removeFirst();
                    }
                }
            } catch (Exception e) {
                log.warn("Read audit log failed: {}", file, e);
            }
        }
        return new ArrayList<>(deque);
    }

    private PluginInfo savePluginToDatabase(String pluginId, Path jarPath) {
        return savePluginToDatabase(pluginId, jarPath, InstallGovernanceMeta.none());
    }

    private PluginInfo savePluginToDatabase(String pluginId, Path jarPath, InstallGovernanceMeta governance) {
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginDescriptor descriptor = wrapper.getDescriptor();

        PluginInfo info = new PluginInfo();
        info.setPluginId(descriptor.getPluginId());
        info.setPluginName(descriptor.getPluginId());
        info.setVersion(descriptor.getVersion());
        info.setDescription("");
        info.setAuthor(descriptor.getProvider());
        info.setPluginClass(descriptor.getPluginClass());
        info.setStatus(PluginStatus.CREATED.name());
        info.setJarPath(jarPath.toString());
        info.setCreateTime(LocalDateTime.now());
        info.setUpdateTime(LocalDateTime.now());
        info.setDependencies(descriptor.getDependencies() == null ? "" : descriptor.getDependencies().toString());
        info.setProvider(descriptor.getProvider());
        info.setPackageType(governance.packageType);
        info.setPackageChecksum(governance.packageChecksum);
        info.setManifestVersion(governance.manifestVersion);
        info.setManifestJson(governance.manifestJson);
        info.setRequiresMagicBoot(governance.requiresMagicBoot);
        info.setPermissions(governance.permissions);
        info.setInstallSource(governance.installSource);
        info.setInstallTime(governance.installTime);

        pluginInfoMapper.insert(info);
        return info;
    }

    private PluginInfo buildPluginInfoFromRuntime(PluginWrapper wrapper) {
        PluginDescriptor descriptor = wrapper.getDescriptor();
        PluginStatus status = mapRuntimeState(wrapper.getPluginState());

        PluginInfo info = new PluginInfo();
        info.setPluginId(descriptor.getPluginId());
        info.setPluginName(descriptor.getPluginId());
        info.setVersion(descriptor.getVersion());
        info.setDescription("");
        info.setAuthor(descriptor.getProvider());
        info.setPluginClass(descriptor.getPluginClass());
        info.setStatus(status.name());
        Path pluginPath = wrapper.getPluginPath();
        info.setJarPath(pluginPath == null ? "" : pluginPath.toString());
        info.setCreateTime(LocalDateTime.now());
        info.setUpdateTime(LocalDateTime.now());
        info.setDependencies(descriptor.getDependencies() == null ? "" : descriptor.getDependencies().toString());
        info.setProvider(descriptor.getProvider());
        return info;
    }

    /**

     */
    private PluginInfo getPluginInfoByPluginId(String pluginId) {
        return pluginInfoMapper.selectOne(new LambdaQueryWrapper<PluginInfo>()
                .eq(PluginInfo::getPluginId, pluginId));
    }

    /**

     */
    private List<Path> getPluginJars() {
        List<Path> result = new ArrayList<>();
        Path pluginPath = Paths.get(pluginProperties.getDir()).toAbsolutePath();

        if (Files.exists(pluginPath) && Files.isDirectory(pluginPath)) {
            try {
                Files.list(pluginPath)
                        .filter(path -> path.toString().endsWith(".jar"))
                        .forEach(result::add);
            } catch (IOException e) {
                log.error("Failed to read plugin directory", e);
            }
        }

        return result;
    }

    private Path ensurePluginDirectoryExists() throws IOException {
        Path pluginPath = Paths.get(pluginProperties.getDir()).toAbsolutePath();
        if (!Files.exists(pluginPath)) {
            Files.createDirectories(pluginPath);
        }
        return pluginPath;
    }

    private String sanitizeUploadedJarName(String fileName) {
        String fallback = "upload-" + System.currentTimeMillis() + ".jar";
        if (fileName == null || fileName.isBlank()) {
            return fallback;
        }
        String candidate = Paths.get(fileName).getFileName().toString();
        String safe = candidate.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (!safe.toLowerCase(Locale.ROOT).endsWith(".jar")) {
            safe = safe + ".jar";
        }
        return safe.isBlank() ? fallback : safe;
    }

    private Path resolveNonConflictingTargetPath(Path parent, String fileName) {
        String candidate = (fileName == null || fileName.isBlank()) ? ("upload-" + System.currentTimeMillis() + ".jar") : fileName;
        int dot = candidate.lastIndexOf('.');
        String base = dot > 0 ? candidate.substring(0, dot) : candidate;
        String ext = dot > 0 ? candidate.substring(dot) : "";
        Path target = parent.resolve(candidate);
        int index = 1;
        while (Files.exists(target)) {
            target = parent.resolve(base + "-" + index + ext);
            index++;
        }
        return target;
    }

    private PluginInfo installLoadedPlugin(Path jarPath,
                                           String expectedPluginId,
                                           String expectedVersion,
                                           InstallGovernanceMeta governance) throws IOException {
        String loadedPluginId = null;
        try {
            loadedPluginId = pluginManager.loadPlugin(jarPath);
            PluginWrapper wrapper = pluginManager.getPlugin(loadedPluginId);
            if (wrapper == null || wrapper.getDescriptor() == null) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED, "Plugin descriptor not found after loading");
            }
            PluginDescriptor descriptor = wrapper.getDescriptor();
            if (expectedPluginId != null && expectedVersion != null) {
                if (!Objects.equals(expectedPluginId, descriptor.getPluginId())
                        || !Objects.equals(expectedVersion, descriptor.getVersion())) {
                    throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_DESCRIPTOR_MISMATCH,
                            "Manifest pluginId/version mismatch with plugin.properties");
                }
            }

            PluginInfo pluginInfo = savePluginToDatabase(loadedPluginId, jarPath, governance);
            log.info("Plugin installed successfully: {}, source={}, type={}",
                    pluginInfo.getPluginId(), governance.installSource, governance.packageType);
            return pluginInfo;
        } catch (Exception ex) {
            if (loadedPluginId != null) {
                try {
                    pluginManager.unloadPlugin(loadedPluginId);
                } catch (Exception unloadEx) {
                    log.warn("Failed to unload plugin after install error: {}", loadedPluginId, unloadEx);
                }
            }
            try {
                Files.deleteIfExists(jarPath);
            } catch (IOException deleteEx) {
                log.warn("Failed to delete plugin file after install error: {}", jarPath, deleteEx);
            }
            if (ex instanceof IOException ioEx) {
                throw ioEx;
            }
            if (ex instanceof PluginInstallException pluginInstallException) {
                throw pluginInstallException;
            }
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED,
                    "Plugin install failed: " + ex.getMessage(), ex);
        }
    }

    private URI parseAndValidateRemoteUri(String url) {
        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new IllegalArgumentException("url must use http or https scheme");
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("url format is invalid");
        }
    }

    private String resolveRemoteJarName(URI uri) {
        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Remote URL path is empty");
        }
        String fileName = Paths.get(path).getFileName().toString();
        if (fileName.isBlank() || !fileName.toLowerCase(Locale.ROOT).endsWith(".jar")) {
            throw new IllegalArgumentException("Remote URL must end with .jar");
        }
        return fileName;
    }

    private void cleanupDownloadedFiles(Path tempFile, Path finalFile) {
        try {
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
            }
            if (finalFile != null) {
                Files.deleteIfExists(finalFile);
            }
        } catch (IOException ex) {
            log.warn("Failed to cleanup downloaded files", ex);
        }
    }

    /**

     */
    private Map<String, Object> toPluginMap(PluginInfo info) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pluginId", info.getPluginId());
        map.put("pluginName", info.getPluginName());
        map.put("version", info.getVersion());
        map.put("description", info.getDescription());
        map.put("author", info.getAuthor());
        map.put("pluginClass", info.getPluginClass());
        map.put("status", PluginStatus.valueOf(info.getStatus()).toZh());
        map.put("statusEnum", info.getStatus());
        map.put("jarPath", info.getJarPath());
        map.put("createTime", info.getCreateTime());
        map.put("updateTime", info.getUpdateTime());
        map.put("dependencies", info.getDependencies());
        map.put("provider", info.getProvider());
        map.put("packageType", info.getPackageType());
        map.put("packageChecksum", info.getPackageChecksum());
        map.put("manifestVersion", info.getManifestVersion());
        map.put("manifestJson", info.getManifestJson());
        map.put("requiresMagicBoot", info.getRequiresMagicBoot());
        map.put("permissions", info.getPermissions());
        map.put("installSource", info.getInstallSource());
        map.put("installTime", info.getInstallTime());


        try {
            PluginWrapper runtimeWrapper = pluginManager.getPlugin(info.getPluginId());
            PluginState runtimeState = (runtimeWrapper != null) ? runtimeWrapper.getPluginState() : PluginState.CREATED;
            map.put("runtimeState", runtimeState.name());
        } catch (Exception e) {
            map.put("runtimeState", "UNKNOWN");
        }

        return map;
    }

    /**

     */
    private Map<String, Object> toRuntimePluginMap(PluginWrapper wrapper) {
        PluginDescriptor descriptor = wrapper.getDescriptor();
        PluginStatus status = mapRuntimeState(wrapper.getPluginState());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pluginId", descriptor.getPluginId());
        map.put("pluginName", descriptor.getPluginId());
        map.put("version", descriptor.getVersion());
        map.put("description", "");
        map.put("author", descriptor.getProvider());
        map.put("pluginClass", descriptor.getPluginClass());
        map.put("status", status.toZh());
        map.put("statusEnum", status.name());
        map.put("jarPath", "");
        map.put("createTime", null);
        map.put("updateTime", null);
        map.put("dependencies", descriptor.getDependencies());
        map.put("provider", descriptor.getProvider());
        map.put("packageType", null);
        map.put("packageChecksum", null);
        map.put("manifestVersion", null);
        map.put("manifestJson", null);
        map.put("requiresMagicBoot", null);
        map.put("permissions", null);
        map.put("installSource", null);
        map.put("installTime", null);
        map.put("runtimeState", wrapper.getPluginState().name());
        return map;
    }

    private PluginStatus mapRuntimeState(PluginState state) {
        if (state == null) {
            return PluginStatus.CREATED;
        }
        return switch (state) {
            case STARTED -> PluginStatus.STARTED;
            case STOPPED -> PluginStatus.STOPPED;
            case DISABLED -> PluginStatus.DISABLED;
            default -> PluginStatus.CREATED;
        };
    }

    private boolean hasJarPath(PluginInfo info) {
        return info.getJarPath() != null && !info.getJarPath().isBlank();
    }

    private boolean isAuditLine(String line) {
        if (line == null || line.isBlank()) {
            return false;
        }
        String lower = line.toLowerCase(Locale.ROOT);
        return lower.contains("plugin")
                || lower.contains("install")
                || lower.contains("uninstall")
                || lower.contains("start plugin")
                || lower.contains("stop plugin")
                || lower.contains("reload")
                || lower.contains("rescan")
                || lower.contains("init-sync")
                || lower.contains("sync")
                || lower.contains("enable")
                || lower.contains("disable");
    }

    private static final class InstallGovernanceMeta {
        private final String packageType;
        private final String packageChecksum;
        private final String manifestVersion;
        private final String manifestJson;
        private final String requiresMagicBoot;
        private final String permissions;
        private final String installSource;
        private final LocalDateTime installTime;

        private InstallGovernanceMeta(String packageType,
                                      String packageChecksum,
                                      String manifestVersion,
                                      String manifestJson,
                                      String requiresMagicBoot,
                                      String permissions,
                                      String installSource,
                                      LocalDateTime installTime) {
            this.packageType = packageType;
            this.packageChecksum = packageChecksum;
            this.manifestVersion = manifestVersion;
            this.manifestJson = manifestJson;
            this.requiresMagicBoot = requiresMagicBoot;
            this.permissions = permissions;
            this.installSource = installSource;
            this.installTime = installTime;
        }

        private static InstallGovernanceMeta forZipUpload(ZipPluginInstaller.InstallPackage prepared) {
            return new InstallGovernanceMeta(
                    "ZIP",
                    prepared.packageChecksum(),
                    prepared.manifestVersion(),
                    prepared.manifestJson(),
                    prepared.requiresMagicBoot(),
                    prepared.permissionsJson(),
                    "UPLOAD_ZIP",
                    LocalDateTime.now()
            );
        }

        private static InstallGovernanceMeta forLegacyJarUpload() {
            return new InstallGovernanceMeta(
                    "LEGACY_JAR",
                    null,
                    null,
                    null,
                    null,
                    null,
                    "UPLOAD_JAR",
                    LocalDateTime.now()
            );
        }

        private static InstallGovernanceMeta none() {
            return new InstallGovernanceMeta(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }
}
