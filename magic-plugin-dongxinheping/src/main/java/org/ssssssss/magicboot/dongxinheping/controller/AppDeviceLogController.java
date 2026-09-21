package org.ssssssss.magicboot.dongxinheping.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * PDA App 运行日志下载
 * zip 由 magic-api 脚本上传落盘 {upload.dir}/app-log/{deviceId}/{fileName}，
 * 该目录在 userfiles 公网映射之外，只能经本控制器（管理端）下载。
 * 列表/删除走 magic-api /api/app/log/*（有 db 访问），这里只做文件流输出。
 */
@RestController
@RequestMapping("/applog")
public class AppDeviceLogController {

    @Value("${upload.dir:D:/mb/}")
    private String uploadDir;

    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam("deviceId") String deviceId,
                                      @RequestParam("fileName") String fileName) {
        // 两个参数都会拼进文件路径，客户端可控，必须白名单防路径穿越
        if (deviceId == null || !deviceId.matches("[A-Za-z0-9_-]{1,64}")) {
            return badRequest("非法的deviceId");
        }
        if (fileName == null || !fileName.matches("[A-Za-z0-9_.-]{1,128}")
                || !fileName.endsWith(".zip")) {
            return badRequest("非法的文件名");
        }

        File file = new File(uploadDir, "app-log/" + deviceId + "/" + fileName);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorBody("日志文件不存在或已被清理"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                    .replaceAll("\\+", "%20");
            headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
        } catch (Exception e) {
            headers.add("Content-Disposition", "attachment; filename=" + fileName);
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity.badRequest().body(errorBody(message));
    }

    private Map<String, Object> errorBody(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 400);
        body.put("message", message);
        body.put("data", null);
        return body;
    }
}
