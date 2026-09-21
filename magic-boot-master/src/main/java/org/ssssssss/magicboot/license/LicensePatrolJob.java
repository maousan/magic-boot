package org.ssssssss.magicboot.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * License 周期巡检。
 * 每 60 秒：推进 effectiveMax（至少到当前时间）、刷新授权状态、执行阻断/恢复联动。
 * 每小时：NTP 校验（可选，配置了 license.ntp-servers 才生效；失败即跳过）。
 * 明确不放 magic-api-plugin-job：Quartz 脚本任务可被用户增删，不能承载执法逻辑。
 */
@Component
public class LicensePatrolJob {

    private static final Logger log = LoggerFactory.getLogger(LicensePatrolJob.class);

    private final LicenseManager licenseManager;
    private final AntiRollbackService antiRollbackService;
    private final LicenseNtpService ntpService;
    private final LicenseProperties properties;

    public LicensePatrolJob(LicenseManager licenseManager, AntiRollbackService antiRollbackService,
                            LicenseNtpService ntpService, LicenseProperties properties) {
        this.licenseManager = licenseManager;
        this.antiRollbackService = antiRollbackService;
        this.ntpService = ntpService;
        this.properties = properties;
    }

    @Scheduled(initialDelay = 10_000, fixedDelay = 60_000)
    public void patrol() {
        try {
            licenseManager.runCheck();
        } catch (Exception e) {
            log.warn("license patrol failed: {}", e.getMessage());
        }
    }

    @Scheduled(initialDelay = 20_000, fixedDelay = 3_600_000)
    public void ntpCheck() {
        String servers = properties.getNtpServers();
        if (servers == null || servers.isBlank()) {
            return;
        }
        for (String server : servers.split(",")) {
            Long time = ntpService.query(server, 2000);
            if (time != null) {
                antiRollbackService.observe(time);
                log.debug("license ntp time observed: {} from {}", time, server);
                return;
            }
        }
        log.debug("license ntp check skipped: no server reachable");
    }
}
