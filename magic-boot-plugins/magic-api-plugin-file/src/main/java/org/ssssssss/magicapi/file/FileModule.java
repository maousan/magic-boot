package org.ssssssss.magicapi.file;

import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.get.GetFilePretreatment;
import org.dromara.x.file.storage.core.get.ListFilesPretreatment;
import org.dromara.x.file.storage.core.get.RemoteFileInfo;
import org.dromara.x.file.storage.core.get.ListFilesResult;
import org.dromara.x.file.storage.core.copy.CopyPretreatment;
import org.dromara.x.file.storage.core.move.MovePretreatment;
import org.ssssssss.magicapi.core.annotation.MagicModule;
import org.ssssssss.magicapi.file.event.FileEventPublisher;
import org.ssssssss.magicapi.file.model.FileUploadResult;
import org.ssssssss.magicapi.file.service.MagicDynamicFileClient;
import org.ssssssss.script.annotation.Comment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件存储模块
 * 提供给 magic-api 脚本使用的文件操作 API
 */
@MagicModule("file")
public class FileModule {

    private final MagicDynamicFileClient magicDynamicFileClient;
    private FileStorageService fileStorageService;
    private FileEventPublisher eventPublisher;
    private String storageKey;

    public FileModule(MagicDynamicFileClient magicDynamicFileClient) {
        this.magicDynamicFileClient = magicDynamicFileClient;
    }

    public FileModule(FileStorageService fileStorageService) {
        this.magicDynamicFileClient = null;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 设置事件发布器
     */
    public void setEventPublisher(FileEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 设置存储标识
     */
    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    /**
     * 切换存储源
     *
     * @param key 存储标识
     * @return FileModule 实例
     */
    public FileModule use(String key) {
        FileStorageService service = magicDynamicFileClient.getClient(key);
        FileModule module = new FileModule(service);
        module.setEventPublisher(this.eventPublisher);
        module.setStorageKey(key);
        return module;
    }

    /**
     * 上传文件
     *
     * @param file 文件对象（MultipartFile 或 byte[]）
     * @return 文件信息
     */
    @Comment("上传文件")
    public FileUploadResult upload(@Comment(name = "file", value = "文件对象(MultipartFile/byte[]/InputStream)") Object file) {
        return upload(null, file, null);
    }

    /**
     * 上传文件
     *
     * @param path 目标路径
     * @param file 文件对象
     * @return 文件信息
     */
    @Comment("上传文件到指定路径")
    public FileUploadResult upload(
            @Comment(name = "path", value = "目标路径，如 /images/2024/") String path,
            @Comment(name = "file", value = "文件对象") Object file) {
        return upload(path, file, null);
    }

    /**
     * 上传文件
     *
     * @param path     目标路径
     * @param file     文件对象
     * @param fileName 文件名
     * @return 文件信息
     */
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

        // 发布文件上传事件
        if (eventPublisher != null && fileInfo != null) {
            String operator = getCurrentUser();
            eventPublisher.publishUploadEvent(
                    storageKey != null ? storageKey : "default",
                    fileInfo.getPath(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getSize(),
                    fileInfo.getContentType(),
                    fileInfo.getUrl(),
                    null, // MD5 需要单独计算
                    operator
            );
        }

        return convertToFileUploadResult(fileInfo);
    }

    /**
     * 创建目录
     *
     * @param parentPath 父目录路径
     * @param dirName    目录名称
     * @return 是否成功
     */
    @Comment("创建目录")
    public boolean mkdir(
            @Comment(name = "parentPath", value = "父目录路径") String parentPath,
            @Comment(name = "dirName", value = "目录名称") String dirName) {
        // 确保父路径以 / 结尾
        if (parentPath == null || parentPath.isEmpty()) {
            parentPath = "/";
        }
        if (!parentPath.endsWith("/")) {
            parentPath = parentPath + "/";
        }

        // 构建完整目录路径
        String dirPath = parentPath + dirName + "/";

        // 创建空文件作为目录标记
        FileStorageService service = getFileStorageService();
        ByteArrayInputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        var upload = service.of(emptyStream);
        upload.setPath(dirPath);
        upload.setSaveFilename(".folder");
        FileInfo fileInfo = upload.upload();

        // 发布创建目录事件
        if (eventPublisher != null && fileInfo != null) {
            String operator = getCurrentUser();
            eventPublisher.publishMkdirEvent(
                    storageKey != null ? storageKey : "default",
                    dirPath,
                    dirName,
                    operator
            );
        }

        return fileInfo != null;
    }

    /**
     * 下载文件
     *
     * @param path 文件路径
     * @return 文件字节数组
     */
    @Comment("下载文件")
    public byte[] download(@Comment(name = "path", value = "文件路径") String path) {
        return getFileStorageService().download(path).bytes();
    }

    /**
     * 下载文件（返回 InputStream）
     *
     * @param path 文件路径
     * @return InputStream
     */
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

    /**
     * 获取文件信息
     *
     * @param path 文件路径
     * @return 文件信息
     */
    @Comment("获取文件信息")
    public RemoteFileInfo info(@Comment(name = "path", value = "文件路径") String path) {
        GetFilePretreatment pretreatment = getFileStorageService().getFile();
        pretreatment.setPath(path);
        return pretreatment.getFile();
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    @Comment("判断文件是否存在")
    public boolean exists(@Comment(name = "path", value = "文件路径") String path) {
        RemoteFileInfo fileInfo = info(path);
        return fileInfo != null;
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return 是否成功
     */
    @Comment("删除文件")
    public boolean delete(@Comment(name = "path", value = "文件路径") String path) {
        boolean result = getFileStorageService().delete(path);

        // 发布删除文件事件
        if (result && eventPublisher != null) {
            String operator = getCurrentUser();
            eventPublisher.publishDeleteEvent(path, operator);
        }

        return result;
    }

    /**
     * 列出文件
     *
     * @param path 文件夹路径
     * @return 文件列表
     */
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

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 文件信息
     */
    @Comment("复制文件")
    public FileInfo copy(
            @Comment(name = "sourcePath", value = "源文件路径") String sourcePath,
            @Comment(name = "targetPath", value = "目标文件路径") String targetPath) {
        CopyPretreatment pretreatment = getFileStorageService().copy(sourcePath);
        pretreatment.setPath(targetPath);
        return pretreatment.copy();
    }

    /**
     * 移动文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return 文件信息
     */
    @Comment("移动文件")
    public FileInfo move(
            @Comment(name = "sourcePath", value = "源文件路径") String sourcePath,
            @Comment(name = "targetPath", value = "目标文件路径") String targetPath) {
        MovePretreatment pretreatment = getFileStorageService().move(sourcePath);
        pretreatment.setPath(targetPath);
        return pretreatment.move();
    }

    /**
     * 获取文件访问URL
     *
     * @param path 文件路径
     * @return 访问URL
     */
    @Comment("获取文件访问URL")
    public String getUrl(@Comment(name = "path", value = "文件路径") String path) {
        RemoteFileInfo fileInfo = info(path);
        return fileInfo != null ? fileInfo.getUrl() : null;
    }

    /**
     * 获取文件存储服务
     */
    private FileStorageService getFileStorageService() {
        if (fileStorageService != null) {
            return fileStorageService;
        }
        if (magicDynamicFileClient != null && !magicDynamicFileClient.isEmpty()) {
            return magicDynamicFileClient.getClient(magicDynamicFileClient.getDefaultKey());
        }
        throw new IllegalStateException("FileStorageService 未初始化，请先配置文件存储");
    }

    /**
     * 获取当前用户
     */
    private String getCurrentUser() {
        // 默认返回 system，子类可重写此方法集成认证框架
        return "system";
    }

    /**
     * 转换为 FileUploadResult
     */
    private FileUploadResult convertToFileUploadResult(FileInfo fileInfo) {
        if (fileInfo == null) {
            return null;
        }
        FileUploadResult result = new FileUploadResult();
        result.setId(fileInfo.getId());
        result.setFilePath(fileInfo.getPath());
        result.setFileName(fileInfo.getOriginalFilename());
        result.setFileSize(fileInfo.getSize());
        result.setContentType(fileInfo.getContentType());
        result.setUrl(fileInfo.getUrl());
        return result;
    }
}
