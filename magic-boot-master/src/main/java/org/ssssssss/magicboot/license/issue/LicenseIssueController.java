package org.ssssssss.magicboot.license.issue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * License 签发接口（仅办公实例装配：license.issue.enabled=true）。
 * 客户现场未开启时 Bean 不注册，/system/license/issue 404，暴露面为零。
 * 权限：依赖管理端登录（办公实例），生产建议仅超管可见菜单。
 */
@RestController
@RequestMapping({"/system/license/issue", "/api/system/license/issue"})
@ConditionalOnProperty(name = "license.issue.enabled", havingValue = "true")
public class LicenseIssueController {

    private static final Logger log = LoggerFactory.getLogger(LicenseIssueController.class);

    private final LicenseIssueService issueService;

    public LicenseIssueController(LicenseIssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/download")
    public ResponseEntity<?> issue(@RequestParam("customer") String customer,
                                   @RequestParam("expireAt") String expireAt,
                                   @RequestParam("fingerprintCode") String fingerprintCode,
                                   @RequestParam(value = "graceDays", required = false) Integer graceDays,
                                   @RequestParam(value = "notes", required = false) String notes,
                                   @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // file 参数兼容前端 n-upload 直接传文件；有文件时从文件名/内容外无额外信息，指纹仍走文本块
            byte[] content = issueService.issue(customer, expireAt, fingerprintCode, graceDays, notes, "admin");
            String fileName = customer.replaceAll("[^\\w\\u4e00-\\u9fa5-]", "") + "-" + expireAt + ".lic";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception e) {
            log.warn("license issue failed: {}", e.getMessage());
            Map<String, Object> body = new HashMap<>();
            body.put("code", 400);
            body.put("message", e.getMessage());
            body.put("data", null);
            return ResponseEntity.status(HttpStatus.OK).body(body);
        }
    }
}
