package org.ssssssss.magicapi.liteflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicapi.liteflow.model.FlowDefinition;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

/**
 * Flow storage manager with in-memory cache and filesystem persistence.
 * Listens to changes on flow/component directories and refreshes cache automatically.
 */
@Service
public class FlowStorageManager implements FlowStorageService {
    private static final Logger log = LoggerFactory.getLogger(FlowStorageManager.class);

    private final Map<String, FlowDefinition> flowCache = new HashMap<>();
    private final Map<String, String> componentCache = new HashMap<>();

    private final Path flowDir = Paths.get(FLOW_ROOT);
    private final Path componentDir = Paths.get(COMPONENT_ROOT);
    private final Path flowBackupDir = Paths.get(FLOW_ROOT + ".bak");
    private final Path evidenceLog = Paths.get(".sisyphus/evidence/task-4-storage-test.log");

    public FlowStorageManager() {
        try {
            initDirs();
            loadAllFlows();
            loadAllComponents();
            startWatching();
            logEvidence("FlowStorageManager initialized");
        } catch (IOException e) {
            log.error("Failed to initialize FlowStorageManager", e);
        }
    }

    private void initDirs() throws IOException {
        if (!Files.exists(flowDir)) Files.createDirectories(flowDir);
        if (!Files.exists(componentDir)) Files.createDirectories(componentDir);
        if (!Files.exists(flowBackupDir)) Files.createDirectories(flowBackupDir);
    }

    // Load existing definitions into memory at startup
    private void loadAllFlows() throws IOException {
        flowCache.clear();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(flowDir)) {
            for (Path p : ds) {
                if (Files.isRegularFile(p)) {
                    FlowDefinition def = new FlowDefinition(p.getFileName().toString(), readFile(p));
                    flowCache.put(p.getFileName().toString(), def);
                }
            }
        }
    }

    private void loadAllComponents() throws IOException {
        componentCache.clear();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(componentDir)) {
            for (Path p : ds) {
                if (Files.isRegularFile(p)) {
                    componentCache.put(p.getFileName().toString(), readFile(p));
                }
            }
        }
    }

    // CRUD for flows
    @Override
    public void createFlow(String fileName, String content) throws IOException {
        ensureDir(flowDir);
        Path target = flowDir.resolve(fileName);
        if (Files.exists(target)) throw new IOException("Flow file already exists: " + fileName);
        writeFile(target, content);
        flowCache.put(fileName, new FlowDefinition(fileName, content));
        logEvidence("Created flow: " + fileName);
    }

    @Override
    public String readFlow(String fileName) throws IOException {
        FlowDefinition def = flowCache.get(fileName);
        if (def != null) return def.getContent();
        Path p = flowDir.resolve(fileName);
        String content = readFile(p);
        flowCache.put(fileName, new FlowDefinition(fileName, content));
        return content;
    }

    @Override
    public void updateFlow(String fileName, String content) throws IOException {
        Path target = flowDir.resolve(fileName);
        if (!Files.exists(target)) throw new IOException("Flow file not found: " + fileName);
        backupIfExists(target);
        writeFile(target, content);
        flowCache.put(fileName, new FlowDefinition(fileName, content));
        logEvidence("Updated flow: " + fileName);
    }

    @Override
    public void deleteFlow(String fileName) throws IOException {
        Path target = flowDir.resolve(fileName);
        if (Files.exists(target)) {
            backupIfExists(target);
            Files.delete(target);
            flowCache.remove(fileName);
            logEvidence("Deleted flow: " + fileName);
        } else {
            throw new IOException("Flow file not found: " + fileName);
        }
    }

    @Override
    public List<String> listFlows() {
        return new ArrayList<>(flowCache.keySet());
    }

    // Components
    @Override
    public void createComponent(String fileName, String content) throws IOException {
        ensureDir(componentDir);
        Path target = componentDir.resolve(fileName);
        if (Files.exists(target)) throw new IOException("Component file already exists: " + fileName);
        writeFile(target, content);
        componentCache.put(fileName, content);
        logEvidence("Created component: " + fileName);
    }

    @Override
    public String readComponent(String fileName) throws IOException {
        String content = componentCache.get(fileName);
        if (content != null) return content;
        Path p = componentDir.resolve(fileName);
        content = readFile(p);
        componentCache.put(fileName, content);
        return content;
    }

    @Override
    public void updateComponent(String fileName, String content) throws IOException {
        Path target = componentDir.resolve(fileName);
        if (!Files.exists(target)) throw new IOException("Component file not found: " + fileName);
        backupIfExists(target);
        writeFile(target, content);
        componentCache.put(fileName, content);
        logEvidence("Updated component: " + fileName);
    }

    @Override
    public void deleteComponent(String fileName) throws IOException {
        Path target = componentDir.resolve(fileName);
        if (Files.exists(target)) {
            backupIfExists(target);
            Files.delete(target);
            componentCache.remove(fileName);
            logEvidence("Deleted component: " + fileName);
        } else {
            throw new IOException("Component file not found: " + fileName);
        }
    }

    @Override
    public List<String> listComponents() {
        return new ArrayList<>(componentCache.keySet());
    }

    @Override
    public void refresh() throws IOException {
        loadAllFlows();
        loadAllComponents();
        logEvidence("Cache refreshed");
    }

    // Helpers
    private void ensureDir(Path dir) throws IOException {
        if (!Files.exists(dir)) Files.createDirectories(dir);
    }

    private String readFile(Path p) throws IOException {
        try (FileInputStream in = new FileInputStream(p.toFile())) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        }
    }

    private void writeFile(Path p, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        try (FileOutputStream out = new FileOutputStream(p.toFile())) {
            out.write(bytes);
        }
    }

    private void backupIfExists(Path p) throws IOException {
        if (Files.exists(p)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            Path backupDir = flowBackupDir;
            if (!Files.exists(backupDir)) Files.createDirectories(backupDir);
            String fileName = p.getFileName().toString();
            Path backupFile = backupDir.resolve(fileName + "." + timestamp + ".bak");
            Files.copy(p, backupFile, StandardCopyOption.REPLACE_EXISTING);
            logEvidence("Backup created: " + backupFile.toString());
        }
    }

    private void logEvidence(String message) {
        try {
            if (!Files.exists(evidenceLog.getParent())) {
                Files.createDirectories(evidenceLog.getParent());
            }
            String line = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message + "\n";
            Files.write(evidenceLog, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignore) {
        }
    }

    private void startWatching() {
        // Use a dedicated executor to avoid blocking startup
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try {
                watchDirectories();
            } catch (Exception e) {
                log.error("Error while watching liteflow directories", e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void watchDirectories() throws IOException {
        // Simplified lightweight watcher to refresh in-memory data on changes
        // This does not require exact integration with Spring's lifecycle for demo purposes.
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            flowDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            componentDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            WatchKey key;
            // Poll with timeout to avoid blocking
            key = watchService.poll();
            if (key != null) {
                for (WatchEvent<?> ev : key.pollEvents()) {
                    @SuppressWarnings("unchecked") WatchEvent<Path> e = (WatchEvent<Path>) ev;
                    Path filename = e.context();
                    Path dir = (Path) key.watchable();
                    // When an event happens, reload the affected entry
                    if (dir.equals(flowDir)) {
                        refresh();
                        log.info("Flow storage refreshed due to file event: " + filename);
                    } else if (dir.equals(componentDir)) {
                        refresh();
                        log.info("Component storage refreshed due to file event: " + filename);
                    }
                }
                key.reset();
            }
        }
    }
}
