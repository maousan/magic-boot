package org.ssssssss.magicboot.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志文件下载控制器
 * 提供当前日志文件和历史归档日志的下载功能
 */
@RestController
@RequestMapping("/logs")
public class LogDownloadController {

    private static final Logger logger = LoggerFactory.getLogger(LogDownloadController.class);

    @Value("${logging.file.path:./logs}")
    private String logPath;

    // 支持的日志类型
    private static final Map<String, String> LOG_FILES = new HashMap<>();
    static {
        LOG_FILES.put("application", "all.log");
        LOG_FILES.put("error", "error.log");
    }

    /**
     * 下载日志文件
     *
     * @param type      日志类型（application 或 error）
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate   结束日期（可选，格式：yyyy-MM-dd）
     * @param request   HTTP请求
     * @return 日志文件内容
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadLog(
            @RequestParam(value = "type", defaultValue = "application") String type,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            HttpServletRequest request) {

        try {
            // 验证用户是否登录
            if (!StpUtil.isLogin()) {
                logger.warn("未登录用户尝试下载日志文件，IP: {}", getClientIp(request));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("未授权访问，请先登录"));
            }

            String userId = StpUtil.getLoginIdAsString();
            logger.info("用户 {} 请求下载日志文件，类型: {}, 日期范围: {} ~ {}", userId, type, startDate, endDate);

            // 验证日志类型
            if (!LOG_FILES.containsKey(type)) {
                logger.warn("无效的日志类型: {}", type);
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("无效的日志类型，仅支持: application, error"));
            }

            // 如果没有指定日期范围，下载当前日志文件
            if (startDate == null || startDate.trim().isEmpty()) {
                return downloadCurrentLogFile(type, userId);
            }

            // 如果指定了日期范围，下载历史归档日志
            return downloadArchivedLogs(type, startDate, endDate, userId);

        } catch (Exception e) {
            logger.error("下载日志文件失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("下载日志文件失败: " + e.getMessage()));
        }
    }

    /**
     * 下载当前日志文件
     */
    private ResponseEntity<?> downloadCurrentLogFile(String type, String userId) throws IOException {
        String fileName = LOG_FILES.get(type);
        Path filePath = Paths.get(logPath, fileName);

        if (!Files.exists(filePath)) {
            logger.warn("日志文件不存在: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        logger.info("用户 {} 下载当前日志文件: {}", userId, filePath);

        // 读取文件内容
        byte[] content = Files.readAllBytes(filePath);

        // 设置响应头
        HttpHeaders headers = createDownloadHeaders(fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }

    /**
     * 下载历史归档日志（按日期范围）
     */
    private ResponseEntity<?> downloadArchivedLogs(String type, String startDate, String endDate, String userId) throws IOException {
        // 解析日期
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate end = (endDate != null && !endDate.trim().isEmpty())
                ? LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                : start;

        logger.info("用户 {} 下载归档日志，类型: {}, 日期范围: {} ~ {}", userId, type, start, end);

        // 查找匹配的归档日志文件
        List<File> archivedFiles = findArchivedLogFiles(type, start, end);

        if (archivedFiles.isEmpty()) {
            logger.warn("未找到匹配的归档日志文件，类型: {}, 日期范围: {} ~ {}", type, start, end);
            return ResponseEntity.notFound().build();
        }

        // 如果只有一个文件，直接返回
        if (archivedFiles.size() == 1) {
            File file = archivedFiles.get(0);
            byte[] content = Files.readAllBytes(file.toPath());
            HttpHeaders headers = createDownloadHeaders(file.getName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(content);
        }

        // 如果有多个文件，合并内容返回
        return downloadMultipleFiles(archivedFiles, type, start, end);
    }

    /**
     * 查找指定日期范围内的归档日志文件
     */
    private List<File> findArchivedLogFiles(String type, LocalDate start, LocalDate end) {
        List<File> files = new ArrayList<>();

        // 归档日志路径
        Path archivedPath = Paths.get(logPath, "archived");
        if (!Files.exists(archivedPath)) {
            logger.warn("归档日志目录不存在: {}", archivedPath);
            return files;
        }

        // 遍历日期范围
        LocalDate current = start;
        while (!current.isAfter(end)) {
            String dateStr = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 查找匹配的文件
            // error日志归档格式：error.yyyy-MM-dd.0.log.gz
            // all日志归档格式：magic-boot-yyyyMMdd.log
            String pattern = type.equals("error")
                    ? "error." + dateStr + ".*\\.log(\\.gz)?"
                    : "magic-boot-" + current.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "\\.log";

            File[] matchedFiles = archivedPath.toFile().listFiles((dir, name) -> {
                return name.matches(pattern);
            });

            if (matchedFiles != null) {
                for (File file : matchedFiles) {
                    files.add(file);
                }
            }

            current = current.plusDays(1);
        }

        return files;
    }

    /**
     * 下载多个文件（合并为一个文件）
     */
    private ResponseEntity<?> downloadMultipleFiles(List<File> files, String type, LocalDate start, LocalDate end) throws IOException {
        // 创建临时合并文件
        StringBuilder content = new StringBuilder();
        content.append("# 日志归档下载\n");
        content.append("# 类型: ").append(type).append("\n");
        content.append("# 日期范围: ").append(start).append(" ~ ").append(end).append("\n");
        content.append("# 文件数量: ").append(files.size()).append("\n");
        content.append("# 下载时间: ").append(LocalDate.now()).append("\n\n");

        for (File file : files) {
            content.append("\n\n========== 文件: ").append(file.getName()).append(" ==========\n\n");
            byte[] fileContent = Files.readAllBytes(file.toPath());
            content.append(new String(fileContent, StandardCharsets.UTF_8));
        }

        String fileName = String.format("%s_%s_%s.log", type, start, end);
        HttpHeaders headers = createDownloadHeaders(fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(content.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建下载响应头
     */
    private HttpHeaders createDownloadHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
        } catch (Exception e) {
            logger.error("编码文件名失败", e);
        }
        return headers;
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 列出可用的日志文件
     */
    @GetMapping("/list")
    public ResponseEntity<?> listLogs(HttpServletRequest request) {
        try {
            // 验证用户是否登录
            if (!StpUtil.isLogin()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("未授权访问，请先登录"));
            }

            String userId = StpUtil.getLoginIdAsString();
            logger.info("用户 {} 请求列出日志文件", userId);

            List<Map<String, Object>> logFiles = new ArrayList<>();

            // 添加当前日志文件
            for (Map.Entry<String, String> entry : LOG_FILES.entrySet()) {
                Path filePath = Paths.get(logPath, entry.getValue());
                if (Files.exists(filePath)) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("type", entry.getKey());
                    fileInfo.put("fileName", entry.getValue());
                    fileInfo.put("size", Files.size(filePath));
                    fileInfo.put("lastModified", Files.getLastModifiedTime(filePath).toMillis());
                    logFiles.add(fileInfo);
                }
            }

            // 添加归档日志文件（最近7天）
            Path archivedPath = Paths.get(logPath, "archived");
            if (Files.exists(archivedPath)) {
                File[] archivedFiles = archivedPath.toFile().listFiles();
                if (archivedFiles != null) {
                    for (File file : archivedFiles) {
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("type", "archived");
                        fileInfo.put("fileName", file.getName());
                        fileInfo.put("size", file.length());
                        fileInfo.put("lastModified", file.lastModified());
                        logFiles.add(fileInfo);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logFiles);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("列出日志文件失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("列出日志文件失败: " + e.getMessage()));
        }
    }
}
