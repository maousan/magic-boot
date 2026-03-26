package org.ssssssss.magicapi.file;

import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.copy.CopyPretreatment;
import org.dromara.x.file.storage.core.get.GetFilePretreatment;
import org.dromara.x.file.storage.core.get.ListFilesPretreatment;
import org.dromara.x.file.storage.core.get.ListFilesResult;
import org.dromara.x.file.storage.core.get.RemoteFileInfo;
import org.dromara.x.file.storage.core.move.MovePretreatment;
import org.ssssssss.magicapi.core.annotation.MagicModule;
import org.ssssssss.magicapi.file.event.FileEventPublisher;
import org.ssssssss.magicapi.file.model.FileUploadResult;
import org.ssssssss.magicapi.file.model.StorageInfo;
import org.ssssssss.magicapi.file.model.SysFile;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;
import org.ssssssss.magicapi.file.service.SysFileService;
import org.ssssssss.script.annotation.Comment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件存储模块，提供给 magic-api 脚本使用。
 */
@MagicModule("file")
public class FileModule {

    private final MagicDynamicFileClient magicDynamicFileClient;
    private FileStorageService fileStorageService;
    private FileEventPublisher eventPublisher;
    private SysFileService sysFileService;
    private String storageKey;

    public FileModule(MagicDynamicFileClient magicDynamicFileClient) {
        this.magicDynamicFileClient = magicDynamicFileClient;
    }

    public FileModule(FileStorageService fileStorageService) {
        this.magicDynamicFileClient = null;
        this.fileStorageService = fileStorageService;
    }

    @Comment("获取所有文件存储平台")
    public List<StorageInfo> getAllFilePlatforms() {
        return magicDynamicFileClient.getStorageInfoList();
    }

    @Comment("获取默认文件存储平台")
    public String getDefaultPlatform() {
        return magicDynamicFileClient.getDefaultKey();
    }

    public void setEventPublisher(FileEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setSysFileService(SysFileService sysFileService) {
        this.sysFileService = sysFileService;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public FileModule use(String key) {
        FileStorageService service = magicDynamicFileClient.getClient(key);
        FileModule module = new FileModule(service);
        module.setEventPublisher(this.eventPublisher);
        module.setSysFileService(this.sysFileService);
        module.setStorageKey(key);
        return module;
    }

    @Comment("上传文件")
    public FileUploadResult upload(@Comment(name = "file", value = "文件对象(MultipartFile/byte[]/InputStream)") Object file) {
        return upload(null, file, null);
    }

    @Comment("上传文件到指定路径")
    public FileUploadResult upload(
            @Comment(name = "path", value = "目标路径，如 /images/2024/") String path,
            @Comment(name = "file", value = "文件对象") Object file) {
        return upload(path, file, null);
    }

    @Comment("上传文件（可指定文件名）")
    public FileUploadResult upload(
            @Comment(name = "path", value = "目标路径") String path,
            @Comment(name = "file", value = "文件对象") Object file,
            @Comment(name = "fileName", value = "文件名") String fileName) {
        FileStorageService service = getFileStorageService();
        var upload = service.of(file);
        if (path != null && !path.isEmpty()) {
            upload.setPath(path);
        }
        if (fileName != null && !fileName.isEmpty()) {
            upload.setSaveFilename(fileName);
        }
        FileInfo fileInfo = upload.upload();

        String fullPath = toFullPath(fileInfo);
        String effectiveStorageKey = currentStorageKey();

        if (eventPublisher != null && fileInfo != null) {
            String operator = getCurrentUser();
            eventPublisher.publishUploadEvent(
                    effectiveStorageKey,
                    fullPath,
                    fileInfo.getOriginalFilename(),
                    fileInfo.getSize(),
                    fileInfo.getContentType(),
                    fileInfo.getUrl(),
                    null,
                    operator
            );
        }

        return convertToFileUploadResult(fileInfo, fullPath, effectiveStorageKey);
    }

    @Comment("创建目录")
    public boolean mkdir(
            @Comment(name = "parentPath", value = "父目录路径") String parentPath,
            @Comment(name = "dirName", value = "目录名称") String dirName) {
        String normalizedParentPath = normalizeDirPath(parentPath);
        String normalizedDirName = normalizeName(dirName);
        String dirPath = normalizedParentPath + normalizedDirName + "/";

        String effectiveStorageKey = currentStorageKey();
        if (sysFileService != null) {
            if (sysFileService.existsByPath(effectiveStorageKey, dirPath)) {
                return false;
            }
            sysFileService.createDirectory(effectiveStorageKey, normalizedParentPath, normalizedDirName, getCurrentUser());
            return true;
        }

        if (eventPublisher != null) {
            eventPublisher.publishMkdirEvent(effectiveStorageKey, dirPath, normalizedDirName, getCurrentUser());
            return true;
        }
        return false;
    }

    @Comment("下载文件")
    public byte[] download(@Comment(name = "path", value = "文件路径") String path) {
        return getFileStorageService().download(path).bytes();
    }

    @Comment("下载文件（返回 InputStream）")
    public InputStream downloadAsStream(@Comment(name = "path", value = "文件路径") String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getFileStorageService().download(path).inputStream(is -> {
            try {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
            } catch (Exception e) {
                throw new RuntimeException("下载文件失败", e);
            }
        });
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Comment("获取文件信息")
    public RemoteFileInfo info(@Comment(name = "path", value = "文件路径") String path) {
        GetFilePretreatment pretreatment = getFileStorageService().getFile();
        pretreatment.setPath(path);
        return pretreatment.getFile();
    }

    @Comment("判断文件是否存在")
    public boolean exists(@Comment(name = "path", value = "文件路径") String path) {
        String effectiveStorageKey = currentStorageKey();
        if (sysFileService != null) {
            return sysFileService.existsByPath(effectiveStorageKey, path);
        }
        RemoteFileInfo fileInfo = info(path);
        return fileInfo != null;
    }

    @Comment("删除文件或目录")
    public boolean delete(@Comment(name = "path", value = "文件路径") String path) {
        String effectiveStorageKey = currentStorageKey();
        SysFile node = sysFileService == null ? null : sysFileService.findByPath(effectiveStorageKey, path);
        if (node != null && Objects.equals(node.getFileType(), SysFile.TYPE_DIR)) {
            return sysFileService.softDeleteSubtree(effectiveStorageKey, node.getId(), getCurrentUser());
        }

        boolean result = getFileStorageService().delete(path);
        if (sysFileService != null) {
            sysFileService.deleteByPath(effectiveStorageKey, path, getCurrentUser());
        } else if (result && eventPublisher != null) {
            eventPublisher.publishDeleteEvent(effectiveStorageKey, path, getCurrentUser());
        }
        return result;
    }

    @Comment("列出文件")
    public List<RemoteFileInfo> list(@Comment(name = "path", value = "文件夹路径") String path) {
        ListFilesPretreatment pretreatment = getFileStorageService().listFiles();
        pretreatment.setPath(path);
        ListFilesResult result = pretreatment.listFiles();
        if (result != null && result.getFileList() != null) {
            return result.getFileList();
        }
        return new ArrayList<>();
    }

    @Comment("复制文件")
    public FileInfo copy(
            @Comment(name = "sourcePath", value = "源文件路径") String sourcePath,
            @Comment(name = "targetPath", value = "目标文件路径") String targetPath) {
        CopyPretreatment pretreatment = getFileStorageService().copy(sourcePath);
        pretreatment.setPath(targetPath);
        return pretreatment.copy();
    }

    @Comment("移动文件")
    public FileInfo move(
            @Comment(name = "sourcePath", value = "源文件路径") String sourcePath,
            @Comment(name = "targetPath", value = "目标文件路径") String targetPath) {
        String effectiveStorageKey = currentStorageKey();
        SysFile source = sysFileService == null ? null : sysFileService.findByPath(effectiveStorageKey, sourcePath);

        if (source != null && Objects.equals(source.getFileType(), SysFile.TYPE_DIR)) {
            String targetParentPath = extractParentPath(targetPath);
            SysFile targetParent = "/".equals(targetParentPath) ? null : sysFileService.findByPath(effectiveStorageKey, targetParentPath);
            String targetParentId = targetParent == null ? null : targetParent.getId();
            sysFileService.move(effectiveStorageKey, sourcePath, targetParentId, getCurrentUser());
            return null;
        }

        MovePretreatment pretreatment = getFileStorageService().move(sourcePath);
        pretreatment.setPath(targetPath);
        FileInfo moved = pretreatment.move();

        if (sysFileService != null && source != null) {
            String targetParentPath = extractParentPath(targetPath);
            SysFile targetParent = "/".equals(targetParentPath) ? null : sysFileService.findByPath(effectiveStorageKey, targetParentPath);
            String targetParentId = targetParent == null ? null : targetParent.getId();
            sysFileService.move(effectiveStorageKey, sourcePath, targetParentId, getCurrentUser());
        }

        return moved;
    }

    @Comment("获取文件访问URL")
    public String getUrl(@Comment(name = "path", value = "文件路径") String path) {
        RemoteFileInfo fileInfo = info(path);
        return fileInfo != null ? fileInfo.getUrl() : null;
    }

    private FileStorageService getFileStorageService() {
        if (fileStorageService != null) {
            return fileStorageService;
        }
        if (magicDynamicFileClient != null && !magicDynamicFileClient.isEmpty()) {
            return magicDynamicFileClient.getClient(magicDynamicFileClient.getDefaultKey());
        }
        throw new IllegalStateException("FileStorageService 未初始化，请先配置文件存储");
    }

    private String getCurrentUser() {
        return "system";
    }

    private String currentStorageKey() {
        if (storageKey != null && !storageKey.isEmpty()) {
            return storageKey;
        }
        if (magicDynamicFileClient != null && magicDynamicFileClient.getDefaultKey() != null) {
            return magicDynamicFileClient.getDefaultKey();
        }
        return "default";
    }

    private FileUploadResult convertToFileUploadResult(FileInfo fileInfo, String fullPath, String effectiveStorageKey) {
        if (fileInfo == null) {
            return null;
        }
        FileUploadResult result = new FileUploadResult();
        result.setId(fileInfo.getId());
        result.setStorageKey(effectiveStorageKey);
        result.setFilePath(fullPath);
        result.setFileName(fileInfo.getOriginalFilename());
        result.setFileSize(fileInfo.getSize());
        result.setContentType(fileInfo.getContentType());
        result.setUrl(fileInfo.getUrl());
        return result;
    }

    private String toFullPath(FileInfo fileInfo) {
        if (fileInfo == null) {
            return null;
        }
        String path = fileInfo.getPath() == null ? "" : fileInfo.getPath();
        String filename = fileInfo.getFilename() == null ? "" : fileInfo.getFilename();
        return normalizePath(path + filename);
    }

    private String normalizeDirPath(String path) {
        if (path == null || path.isBlank() || "/".equals(path.trim())) {
            return "/";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        return normalized;
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("目录名称不能为空");
        }
        return name.trim();
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        return normalized;
    }

    private String extractParentPath(String fullPath) {
        if (fullPath == null || fullPath.isBlank() || "/".equals(fullPath)) {
            return "/";
        }
        String target = fullPath;
        if (target.endsWith("/")) {
            target = target.substring(0, target.length() - 1);
        }
        int slash = target.lastIndexOf('/');
        if (slash <= 0) {
            return "/";
        }
        return target.substring(0, slash + 1);
    }
}
