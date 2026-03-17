package org.ssssssss.magicboot.pf4j.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
public class PluginManagerService {

    private final PluginManager pluginManager;
    private final PluginInfoMapper pluginInfoMapper;

    @Value("${plugin.dir:D:/mb/plugins/}")
    private String pluginDir;

    public PluginManagerService(PluginManager pluginManager, PluginInfoMapper pluginInfoMapper) {
        this.pluginManager = pluginManager;
        this.pluginInfoMapper = pluginInfoMapper;
    }

    /**
     * 获取所有插件列表
     */
    public List<Map<String, Object>> listAllPlugins() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 从数据库获取已安装的插件
        List<PluginInfo> dbPlugins = pluginInfoMapper.selectList(new LambdaQueryWrapper<>());
        Set<String> dbPluginIds = new HashSet<>();

        for (PluginInfo info : dbPlugins) {
            dbPluginIds.add(info.getPluginId());
            Map<String, Object> pluginMap = toPluginMap(info);
            result.add(pluginMap);
        }

        // 检查是否有新插件（在 plugins 目录但不在数据库中）
        List<Path> pluginJars = getPluginJars();
        for (Path jarPath : pluginJars) {
            String jarName = jarPath.getFileName().toString();
            String pluginId = extractPluginIdFromJarName(jarName);
            if (!dbPluginIds.contains(pluginId)) {
                // 尝试加载插件
                try {
                    String loadedPluginId = pluginManager.loadPlugin(jarPath);
                    PluginWrapper wrapper = pluginManager.getPlugin(loadedPluginId);
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

                    Map<String, Object> pluginMap = toPluginMap(info);
                    result.add(pluginMap);
                } catch (Exception e) {
                    log.warn("加载插件失败：{}", jarPath, e);
                }
            }
        }

        return result;
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

    /**
     * 安装插件
     */
    @Transactional(rollbackFor = Exception.class)
    public PluginInfo installPlugin(MultipartFile file) throws IOException {
        // 确保插件目录存在
        Path pluginPath = Paths.get(pluginDir).toAbsolutePath();
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

        // 获取插件描述符
        PluginWrapper wrapper = pluginManager.getPlugin(loadedPluginId);
        PluginDescriptor descriptor = wrapper.getDescriptor();

        // 保存插件信息到数据库
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setPluginId(descriptor.getPluginId());
        pluginInfo.setPluginName(descriptor.getPluginId());
        pluginInfo.setVersion(descriptor.getVersion());
        pluginInfo.setDescription("");
        pluginInfo.setAuthor(descriptor.getProvider());
        pluginInfo.setPluginClass(descriptor.getPluginClass());
        pluginInfo.setDependencies(descriptor.getDependencies().stream()
                .map(dep -> dep.getPluginId())
                .collect(Collectors.joining(",")));
        pluginInfo.setProvider(descriptor.getProvider());
        pluginInfo.setStatus(PluginStatus.CREATED.name());
        pluginInfo.setJarPath(jarPath.toString());
        pluginInfo.setCreateTime(LocalDateTime.now());
        pluginInfo.setUpdateTime(LocalDateTime.now());

        pluginInfoMapper.insert(pluginInfo);
        log.info("插件安装成功：{}", pluginInfo.getPluginId());

        return pluginInfo;
    }

    /**
     * 卸载插件
     */
    @Transactional(rollbackFor = Exception.class)
    public void uninstallPlugin(String pluginId) {
        PluginInfo info = pluginInfoMapper.selectOne(new LambdaQueryWrapper<PluginInfo>()
                .eq(PluginInfo::getPluginId, pluginId));

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
        Path pluginPath = Paths.get(pluginDir).toAbsolutePath();

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
     * 从 JAR 文件名提取插件 ID
     */
    private String extractPluginIdFromJarName(String jarName) {
        // 简单实现：去掉 -version.jar 后缀
        int index = jarName.lastIndexOf("-");
        if (index > 0) {
            return jarName.substring(0, index);
        }
        return jarName.replace(".jar", "");
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
