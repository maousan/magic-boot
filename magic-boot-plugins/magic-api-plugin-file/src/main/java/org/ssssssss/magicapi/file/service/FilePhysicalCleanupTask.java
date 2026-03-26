package org.ssssssss.magicapi.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.file.model.SysFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 软删文件的异步物理清理任务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FilePhysicalCleanupTask {

    private final SysFileService sysFileService;
    private final MagicDynamicFileClient magicDynamicFileClient;

    @Value("${magic.file.cleanup.retention-days:7}")
    private int retentionDays;

    @Value("${magic.file.cleanup.batch-size:100}")
    private int batchSize;

    @Scheduled(cron = "${magic.file.cleanup.cron:0 */10 * * * ?}")
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(Math.max(retentionDays, 0));
        List<SysFile> candidates = sysFileService.listPendingPhysicalDelete(cutoff, Math.max(batchSize, 1));
        if (candidates.isEmpty()) {
            return;
        }

        for (SysFile file : candidates) {
            try {
                boolean deleted = magicDynamicFileClient.getClient(file.getStorageKey()).delete(file.getFilePath());
                if (deleted) {
                    sysFileService.markPhysicalDeleteDone(file.getId());
                }
            } catch (Exception ex) {
                log.warn("物理清理失败: storageKey={}, path={}, id={}", file.getStorageKey(), file.getFilePath(), file.getId(), ex);
            }
        }
    }
}
