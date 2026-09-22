package org.ssssssss.magicboot.license;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

/**
 * License 状态机与执法器。
 *
 * <p>状态：missing（未授权）/ ok / warning（≤30 天）/ grace（宽限期内）/ expired / abnormal（时钟回拨）。
 * missing/expired/abnormal 阻断业务；登录与授权导入接口由 Filter 白名单放行，保证恢复闭环：
 * 无授权也能看到机器指纹 → 签发 → 导入，不会死锁。
 *
 * <p>到期判定 = max(当前时间, effectiveMax) > expireAt；时钟回拨判定 = now < effectiveMax - 5 分钟。
 * 导入任意有效新授权立即恢复正常态（effectiveMax 保留，防回拨继续生效）。
 */
@Component
public class LicenseManager {

    private static final Logger log = LoggerFactory.getLogger(LicenseManager.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final long DAY_MILLIS = 24L * 60 * 60 * 1000L;

    @Autowired
    private LicenseProperties properties;

    @Autowired
    private MachineFingerprintService fingerprintService;

    @Autowired
    private AntiRollbackService antiRollbackService;

    @Autowired
    private org.ssssssss.magicboot.model.Global global;

    @Autowired
    private org.springframework.core.env.Environment environment;

    @Autowired(required = false)
    private PluginManager pluginManager;

    private volatile LicensePayload current;
    private volatile LicenseStatus status = LicenseStatus.MISSING;
    private volatile String message = LicenseStatus.MISSING.getDefaultMessage();
    private volatile boolean enabled = true;
    /** 时钟异常导入新授权后的抑制水位：effectiveMax 超过它才重新武装异常判定 */
    private volatile long abnormalSuppressedMax = 0L;
    private volatile boolean lastBlocked = false;

    @PostConstruct
    public void init() {
        // 闸门硬化：dongxinheping 业务 profile 在跑时默认强制开启（客户偷改配置关不掉）。
        // 例外：显式配置 license.enabled（办公签发实例 start-issue.bat 用 false 起签发模式，
        // 前提是那台机器装着签发私钥——客户机器无私钥，改了也只会关掉自己的业务，无利可图）。
        this.enabled = properties.getEnabled() != null
                ? properties.getEnabled()
                : (hasBusinessProfile() || properties.resolveEnabled(isDevProfile()));
        if (!this.enabled) {
            status = LicenseStatus.DISABLED;
            log.info("license check disabled (no business profile, license.enabled=false)");
            return;
        }
        log.info("license gate enabled (profiles={})", String.join(",", environment.getActiveProfiles()));
        antiRollbackService.init();
        loadPersistedLicense();
        runCheck();
    }

    /**
     * 巡检/启动统一入口：读取合并时间戳 → 刷新状态 → 执行联动。
     */
    public synchronized void runCheck() {
        if (!enabled) {
            return;
        }
        long now = System.currentTimeMillis();
        long effectiveMax = antiRollbackService.readAndMerge(now);

        LicenseStatus old = status;
        if (current == null) {
            status = LicenseStatus.MISSING;
            message = LicenseStatus.MISSING.getDefaultMessage();
        } else if (now < effectiveMax - LicenseConstants.ROLLBACK_TOLERANCE_MILLIS
                && effectiveMax > abnormalSuppressedMax) {
            status = LicenseStatus.ABNORMAL;
            message = "检测到系统时间异常（晚于上次记录时间被回拨），业务已暂停。请校正系统时间后等待自动恢复，或导入有效授权文件立即恢复";
        } else {
            long expireAtMillis = expireAtMillis(current.getExpireAt());
            long threshold = Math.max(now, effectiveMax);
            long overdue = threshold - expireAtMillis;
            long remain = expireAtMillis - threshold;
            if (overdue > effectiveGraceDays() * DAY_MILLIS) {
                status = LicenseStatus.EXPIRED;
                message = "系统授权已过期（" + current.getExpireAt() + "），请导入有效授权文件";
            } else if (overdue > 0) {
                status = LicenseStatus.GRACE;
                message = "系统授权已过期，宽限期剩余 " + properties.getGraceDays()
                        + " - " + (overdue / DAY_MILLIS) + " 天，请尽快导入新授权";
            } else if (remain <= LicenseConstants.WARNING_THRESHOLD_MILLIS) {
                status = LicenseStatus.WARNING;
                message = "系统授权剩余 " + (remain / DAY_MILLIS) + " 天，请及时续期";
            } else {
                status = LicenseStatus.OK;
                message = LicenseStatus.OK.getDefaultMessage();
            }
        }

        // 时钟追平后自动解除异常抑制水位
        if (now >= effectiveMax - LicenseConstants.ROLLBACK_TOLERANCE_MILLIS) {
            abnormalSuppressedMax = 0;
        }

        boolean blocked = status.isBlocking();
        if (blocked != lastBlocked) {
            applyPluginEnforcement(blocked);
            lastBlocked = blocked;
        }
        if (old != status) {
            log.info("license status: {} -> {} ({})", old, status, message);
        }
    }

    public boolean shouldBlock() {
        return enabled && status.isBlocking();
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * magic-api 侧拦截的响应体（JsonBean 兼容前端）。
     */
    public org.ssssssss.magicapi.core.model.JsonBean blockResponse() {
        return new org.ssssssss.magicapi.core.model.JsonBean<>(403, message);
    }

    /**
     * 导入新授权：验签 + 指纹 3中2 + 格式版本校验；通过即持久化、热加载并立即刷新状态。
     * 允许导入已过期文件（按新文件语义重判状态），防止旧文件锁死系统无法恢复。
     */
    public synchronized LicenseStatus importLicense(byte[] content) throws Exception {
        LicensePayload payload = LicenseVerifier.parseAndVerify(content);
        if (payload.getFormatVersion() > 1) {
            throw new IllegalArgumentException("不支持的授权文件格式版本: " + payload.getFormatVersion());
        }
        if (!fingerprintService.verify(payload.getFingerprints())) {
            throw new IllegalArgumentException("机器指纹不匹配：该授权文件不适用于本机（3 项指纹至少需命中 2 项）");
        }
        // 持久化到 {upload.dir}/.license/current.lic，重启后自动加载
        persistCurrentLicense(content);
        this.current = payload;
        // 导入有效新授权立即恢复：抑制时钟异常（水位抬到当前 effectiveMax）
        this.abnormalSuppressedMax = antiRollbackService.getEffectiveMax();
        runCheck();
        log.info("license imported: {}", LicenseVerifier.describe(payload));
        return status;
    }

    /**
     * 状态视图（管理页/登录横幅用，白名单接口可匿名访问）。
     */
    public StatusView statusView() {
        StatusView view = new StatusView();
        view.enabled = enabled;
        view.status = status.getKey();
        view.message = message;
        view.graceDays = effectiveGraceDays();
        view.fingerprintCode = fingerprintService.fingerprintCode();
        view.serverTime = LocalDateTime.now().withNano(0).toString();
        if (current != null) {
            view.customer = current.getCustomer();
            view.expireAt = current.getExpireAt();
            long threshold = Math.max(System.currentTimeMillis(), antiRollbackService.getEffectiveMax());
            view.remainDays = (expireAtMillis(current.getExpireAt()) - threshold) / DAY_MILLIS;
        }
        return view;
    }

    private void loadPersistedLicense() {
        try {
            Path path = currentLicensePath();
            if (Files.exists(path)) {
                current = LicenseVerifier.parseAndVerify(Files.readAllBytes(path));
                if (!fingerprintService.verify(current.getFingerprints())) {
                    log.warn("persisted license fingerprint mismatch (machine changed?), ignored");
                    current = null;
                } else {
                    log.info("persisted license loaded: {}", LicenseVerifier.describe(current));
                }
            }
        } catch (Exception e) {
            log.warn("persisted license load failed: {}", e.getMessage());
            current = null;
        }
    }

    private void persistCurrentLicense(byte[] content) throws Exception {
        Path path = currentLicensePath();
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, content);
    }

    private Path currentLicensePath() {
        return Path.of(global.getUserFilesBaseDir(), LicenseConstants.MARK_FILE_DIR,
                LicenseConstants.CURRENT_LICENSE_FILE_NAME);
    }

    private void applyPluginEnforcement(boolean blocked) {
        if (pluginManager == null) {
            return;
        }
        for (String pluginId : properties.getStopPlugins().split(",")) {
            String id = pluginId.trim();
            if (id.isEmpty()) {
                continue;
            }
            try {
                PluginWrapper wrapper = pluginManager.getPlugin(id);
                if (wrapper == null) {
                    continue;
                }
                if (blocked && wrapper.getPluginState() == PluginState.STARTED) {
                    pluginManager.stopPlugin(id);
                    log.info("license enforcement: plugin {} stopped", id);
                } else if (!blocked && wrapper.getPluginState() == PluginState.STOPPED) {
                    pluginManager.startPlugin(id);
                    log.info("license recovery: plugin {} started", id);
                }
            } catch (Exception e) {
                log.warn("license enforcement plugin {} failed: {}", id, e.getMessage());
            }
        }
    }

    /**
     * expireAt 按东八区当天 23:59:59.999 折算阈值，不依赖服务器时区。
     */
    static long expireAtMillis(String expireAt) {
        LocalDate date = LocalDate.parse(expireAt, DATE_FMT);
        return LocalDateTime.of(date, LocalTime.MAX).atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
    }

    private boolean hasBusinessProfile() {
        for (String profile : environment.getActiveProfiles()) {
            if ("dongxinheping".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 宽限天数：优先取授权文件内签名保护的 graceDays（客户改配置无效），无则用配置默认 7。
     */
    private int effectiveGraceDays() {
        if (current != null && current.getGraceDays() != null) {
            return current.getGraceDays();
        }
        return properties.getGraceDays();
    }

    private boolean isDevProfile() {
        for (String profile : environment.getActiveProfiles()) {
            if ("dev".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 授权状态视图（管理页/登录横幅展示 + 恢复闭环的指纹来源）。
     */
    public static class StatusView {
        public boolean enabled;
        public String status;
        public String message;
        public String customer;
        public String expireAt;
        public Long remainDays;
        public int graceDays;
        public String fingerprintCode; // 机器指纹码（base64url 打包的 3 项指纹）
        public String serverTime;
    }
}
