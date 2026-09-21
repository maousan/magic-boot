package org.ssssssss.magicboot.license;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 授权状态与导入接口（客户侧恢复通道）。
 * 双前缀映射：/system/license 与 /api/system/license（前端 axios baseURL=/api）。
 * 两者均在闸门白名单内：无授权/过期时依然可达，保证「看指纹→导入→恢复」闭环不死锁。
 */
@RestController
@RequestMapping({"/system/license", "/api/system/license"})
public class LicenseController {

    private final LicenseManager licenseManager;

    public LicenseController(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return ok(licenseManager.statusView());
    }

    @PostMapping("/import")
    public Map<String, Object> importLicense(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return error("请选择授权文件");
        }
        try {
            licenseManager.importLicense(file.getBytes());
            return ok(licenseManager.statusView());
        } catch (Exception e) {
            return error("授权导入失败：" + e.getMessage());
        }
    }

    private static Map<String, Object> ok(Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 200);
        body.put("message", "success");
        body.put("data", data);
        return body;
    }

    private static Map<String, Object> error(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 400);
        body.put("message", message);
        body.put("data", null);
        return body;
    }
}
