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
import org.ssssssss.magicboot.pf4j.model.PluginStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件管理核心服务
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "plugin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PluginManagerService {

    private final PluginManager pluginManager;
    private final PluginInfoMapper pluginInfoMapper;
    private final PluginProperties pluginProperties;

    public PluginManagerService(PluginManager pluginManager,
                                PluginInfoMapper pluginInfoMapper,
                                PluginProperties pluginProperties) {
        this.pluginManager = pluginManager;
        this.pluginInfoMapper = pluginInfoMapper;
        this.pluginProperties = pluginProperties;
    }

    // ==================== 查询方法 ====================

    /**
     * 获取所有已安装的插件列表
     * 注意：此方法只负责查询，不会触发任何加载操作
     */
    public List<Map<String, Object>> listAllPlugins() {
        List<PluginInfo> dbPlugins = pluginInfoMapper.selectList(new LambdaQueryWrapper<>());
        return dbPlugins.stream()
                .map(this::toPluginMap)
                .collect(Collectors.toList());
    }

    /**
     * 扫描插件目录，返回未安装的新插件列表
     * 注意：此方法只扫描不加载，返回 JAR 文件路径列表
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
                // 临时加载以获取 pluginId，然后立即卸载
                String pluginId = pluginManager.loadPlugin(jarPath);
                pluginManager.unloadPlugin(pluginId);

                if (!dbPluginIds.contains(pluginId)) {
                    newPlugins.add(jarPath.toString());
                }
            } catch (Exception e) {
                log.warn("扫描插件失败：{}", jarPath, e);
            }
        }

        return newPlugins;
    }

    /**
     * 加载并安装插件
     * @param jarPath JAR 文件路径
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loadAndInstallPlugin(String jarPath) {
        Path path = Paths.get(jarPath);
        String pluginId = pluginManager.loadPlugin(path);
        PluginInfo info = savePluginToDatabase(pluginId, path);
        return toPluginMap(info);
    }

    /**
     * 同步插件：扫描目录并安装所有新插件
     * @return 新安装的插件列表
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
                log.warn("加载插件失败：{}", jarPath, e);
            }
        }

        return newPlugins;
    }

    /**
     * 获取插件详细信息
     */
    public Map<String, Object> getPluginInfo(String pluginId) {
        PluginInfo info = pluginInfoMapper.selectOne(new LambdaQueryWrapper<PluginInfo>()
                .eq(PluginInfo::getPluginId, pluginId));
        if (info == null) {
            throw new RuntimeException("插件不存在：" + pluginId);
        }
        return toPluginMap(info);
    }

    // ==================== 安装/卸载方法 ====================

    /**
     * 安装插件
     */
    @Transactional(rollbackFor = Exception.class)
    public PluginInfo installPlugin(MultipartFile file) throws IOException {
        // 确保插件目录存在
        Path pluginPath = Paths.get(pluginProperties.getDir()).toAbsolutePath();
        if (!Files.exists(pluginPath)) {
            Files.createDirectories(pluginPath);
        }

        // 保存 JAR 文件
        String jarName = file.getOriginalFilename();
        if (!jarName.endsWith(".jar")) {
            throw new IllegalArgumentException("只能安装 JAR 格式的插件");
        }

        Path jarPath = pluginPath.resolve(jarName);
        file.transferTo(jarPath.toFile());

        // 加载插件
        String loadedPluginId = pluginManager.loadPlugin(jarPath);

        // 保存到数据库
        PluginInfo pluginInfo = savePluginToDatabase(loadedPluginId, jarPath);

        log.info("插件安装成功：{}", pluginInfo.getPluginId());
        return pluginInfo;
    }

    /**
     * 卸载插件
     */
    @Transactional(rollbackFor = Exception.class)
    public void uninstallPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);

        if (info == null) {
            throw new RuntimeException("插件不存在：" + pluginId);
        }

        // 停止插件
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STARTED) {
            pluginManager.stopPlugin(pluginId);
        }

        // 卸载插件
        pluginManager.unloadPlugin(pluginId);

        // 删除 JAR 文件
        try {
            Files.deleteIfExists(Paths.get(info.getJarPath()));
        } catch (IOException e) {
            log.warn("删除插件文件失败：{}", info.getJarPath(), e);
        }

        // 删除数据库记录
        pluginInfoMapper.deleteById(info.getId());
        log.info("插件卸载成功：{}", pluginId);
    }

    // ==================== 生命周期控制方法 ====================

    /**
     * 启动插件
     */
    public void startPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);
        if (info == null) {
            throw new RuntimeException("插件不存在：" + pluginId);
        }

        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STARTED) {
            throw new RuntimeException("插件已启动：" + pluginId);
        }

        PluginState newState = pluginManager.startPlugin(pluginId);
        if (newState != PluginState.STARTED) {
            throw new RuntimeException("插件启动失败：" + newState);
        }

        info.setStatus(PluginStatus.STARTED.name());
        info.setUpdateTime(LocalDateTime.now());
        pluginInfoMapper.updateById(info);
        log.info("插件启动成功：{}", pluginId);
    }

    /**
     * 停止插件
     */
    public void stopPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);
        if (info == null) {
            throw new RuntimeException("插件不存在：" + pluginId);
        }

        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STOPPED || state == PluginState.CREATED) {
            throw new RuntimeException("插件已停止：" + pluginId);
        }

        pluginManager.stopPlugin(pluginId);

        info.setStatus(PluginStatus.STOPPED.name());
        info.setUpdateTime(LocalDateTime.now());
        pluginInfoMapper.updateById(info);
        log.info("插件停止成功：{}", pluginId);
    }

    /**
     * 重新加载插件
     */
    @Transactional(rollbackFor = Exception.class)
    public void reloadPlugin(String pluginId) {
        PluginInfo info = getPluginInfoByPluginId(pluginId);
        if (info == null) {
            throw new RuntimeException("插件不存在：" + pluginId);
        }

        // 停止插件
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        PluginState state = (wrapper != null) ? wrapper.getPluginState() : PluginState.CREATED;
        if (state == PluginState.STARTED) {
            pluginManager.stopPlugin(pluginId);
        }

        // 卸载插件
        pluginManager.unloadPlugin(pluginId);

        // 重新加载
        Path jarPath = Paths.get(info.getJarPath());
        pluginManager.loadPlugin(jarPath);

        // 启动插件
        pluginManager.startPlugin(pluginId);

        info.setStatus(PluginStatus.STARTED.name());
        info.setUpdateTime(LocalDateTime.now());
        pluginInfoMapper.updateById(info);
        log.info("插件重新加载成功：{}", pluginId);
    }

    /**
     * 重新扫描插件目录
     */
    public List<String> rescanPlugins() {
        List<String> loadedPlugins = new ArrayList<>();
        List<Path> pluginJars = getPluginJars();

        for (Path jarPath : pluginJars) {
            try {
                pluginManager.loadPlugin(jarPath);
                loadedPlugins.add(jarPath.getFileName().toString());
            } catch (Exception e) {
                log.warn("加载插件失败：{}", jarPath, e);
            }
        }

        log.info("重新扫描插件完成，加载 {} 个插件", loadedPlugins.size());
        return loadedPlugins;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 保存插件到数据库
     */
    private PluginInfo savePluginToDatabase(String pluginId, Path jarPath) {
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

        pluginInfoMapper.insert(info);
        return info;
    }

    /**
     * 获取插件信息
     */
    private PluginInfo getPluginInfoByPluginId(String pluginId) {
        return pluginInfoMapper.selectOne(new LambdaQueryWrapper<PluginInfo>()
                .eq(PluginInfo::getPluginId, pluginId));
    }

    /**
     * 获取所有插件 JAR 文件
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
                log.error("获取插件目录失败", e);
            }
        }

        return result;
    }

    /**
     * 转换为 Map
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

        // 获取 PF4J 运行时状态
        try {
            PluginWrapper runtimeWrapper = pluginManager.getPlugin(info.getPluginId());
            PluginState runtimeState = (runtimeWrapper != null) ? runtimeWrapper.getPluginState() : PluginState.CREATED;
            map.put("runtimeState", runtimeState.name());
        } catch (Exception e) {
            map.put("runtimeState", "UNKNOWN");
        }

        return map;
    }
}
