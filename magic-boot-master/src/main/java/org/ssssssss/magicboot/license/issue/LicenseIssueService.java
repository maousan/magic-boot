package org.ssssssss.magicboot.license.issue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicboot.license.LicensePayload;
import org.ssssssss.magicboot.license.LicenseVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

/**
 * License 签发服务（仅办公实例装配：license.issue.enabled=true 且配置私钥路径）。
 * 客户现场未配置时 Bean 不存在，签发接口 404，暴露面为零。
 */
@Service
public class LicenseIssueService {

    private static final Logger log = LoggerFactory.getLogger(LicenseIssueService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    private volatile byte[] privateKey;
    private volatile boolean initFailed;

    private byte[] privateKey() throws Exception {
        if (privateKey != null) {
            return privateKey;
        }
        if (initFailed) {
            throw new IllegalStateException("签发私钥未就绪（此前加载失败，请检查 LICENSE_PRIVATE_KEY_PATH）");
        }
        String path = environment.getProperty("LICENSE_PRIVATE_KEY_PATH",
                environment.getProperty("license.issue.private-key-path", ""));
        if (path == null || path.isBlank()) {
            initFailed = true;
            throw new IllegalStateException("未配置签发私钥路径（环境变量 LICENSE_PRIVATE_KEY_PATH）");
        }
        Path keyPath = Path.of(path);
        if (!Files.exists(keyPath)) {
            initFailed = true;
            throw new IllegalStateException("签发私钥文件不存在: " + path);
        }
        privateKey = Base64.getDecoder().decode(Files.readString(keyPath, StandardCharsets.UTF_8).trim());
        return privateKey;
    }

    /**
     * 签发：指纹块（3 行）解析 → 载荷构建 → Ed25519 签名 → 返回 .lic 文件字节并留痕。
     */
    public byte[] issue(String customer, String expireAt, String fingerprintBlock, String notes, String operator)
            throws Exception {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("客户名称不能为空");
        }
        if (expireAt == null || !expireAt.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("有效期格式应为 yyyy-MM-dd");
        }
        LocalDate.parse(expireAt); // 格式校验
        java.util.List<String> fingerprints =
                org.ssssssss.magicboot.license.MachineFingerprintService.parseFingerprintBlock(
                        fingerprintBlock == null ? "" : fingerprintBlock);
        int provided = 0;
        for (String fp : fingerprints) {
            if (fp != null && fp.matches("[0-9a-f]{64}")) {
                provided++;
            }
        }
        if (provided < 2) {
            throw new IllegalArgumentException("指纹块无效：至少需要 2 项有效的 64 位十六进制指纹（3 项中机器不可用项可标 unavailable）");
        }

        LicensePayload payload = new LicensePayload();
        payload.setLicenseId(UUID.randomUUID().toString().replace("-", ""));
        payload.setCustomer(customer.trim());
        payload.setIssuedAt(LocalDate.now().toString());
        payload.setExpireAt(expireAt);
        payload.setFingerprints(fingerprints);
        payload.setFormatVersion(1);
        payload.setNotes(notes == null ? "" : notes.trim());
        payload.setSignature(LicenseVerifier.sign(payload, privateKey()));

        byte[] fileBytes = payload.toFileBytes();

        // 签发留痕（办公库；表不存在不影响签发，仅告警）
        try {
            jdbcTemplate.update("insert into t_license_issue_log (license_id, customer, expire_at, fingerprints, notes, operator, create_time) values (?,?,?,?,?,?,now())",
                    payload.getLicenseId(), payload.getCustomer(), payload.getExpireAt(),
                    String.join("\n", fingerprints), payload.getNotes(), operator);
        } catch (Exception e) {
            log.warn("license issue log failed (run t_license_issue_log DDL): {}", e.getMessage());
        }
        log.info("license issued: {}", LicenseVerifier.describe(payload));
        return fileBytes;
    }

    @SuppressWarnings("unused")
    private static String pretty(byte[] bytes) throws Exception {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
                new ObjectMapper().readTree(new String(bytes, StandardCharsets.UTF_8)));
    }
}
