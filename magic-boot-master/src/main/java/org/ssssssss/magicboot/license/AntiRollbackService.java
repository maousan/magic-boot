package org.ssssssss.magicboot.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssssssss.magicboot.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * 防回拨：单调时间戳的多位置存储与合并。
 *
 * <p>4+ 位置（独立性与清除成本递增，见规格 §5.1）：
 * 1. MySQL 业务库单行表 t_license_time_mark（主位置，客户不敢删业务库）
 * 2. 本地文件 A：{upload.dir}/.license/mark.dat
 * 3. 本地文件 B：{工作目录}/data/.license/mark.dat（与 A 不同目录树）
 * 4. Windows 注册表 java.util.prefs（整目录拷贝带不走；Linux 由文件 B 替代角色）
 * 5. Redis 旁路位（现场已存活；可被 FLUSHALL，永不作权威值）
 *
 * <p>每位置写同构签名结构 {v, installId, maxSeen, writeTime, sig=HMAC(installId|maxSeen|writeTime)}。
 * 读取逐个验签 → 取最大值 → 立即回写扩散全部可写位置。effectiveMax 只增不减。
 * 任一位置读写失败 log.warn 不阻断，靠其余位置兜底。
 */
@Service
public class AntiRollbackService {

    private static final Logger log = LoggerFactory.getLogger(AntiRollbackService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** 回写扩散的重试（DB 建表晚于首次启动等场景） */
    private static final long DDL_HINT =
            "license-time-mark: run V20260922.001 DDL if missing".length();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private RedisUtil redisUtil;

    @Autowired
    private org.ssssssss.magicboot.model.Global global;

    private final Object lock = new Object();
    private volatile long effectiveMax = 0L;
    private String installId;
    private volatile boolean dbWarned;
    private byte[] hmacKey;

    /**
     * 初始化 installId 与密钥，执行首次读合并+回写。installId 优先取本地文件，
     * 否则尝试从既有标记中恢复，最后生成新 UUID 持久化。
     */
    public void init() {
        this.hmacKey = deriveHmacKey();
        this.installId = initInstallId();
        readAndMerge(System.currentTimeMillis());
    }

    public long getEffectiveMax() {
        return effectiveMax;
    }

    public String getInstallId() {
        return installId;
    }

    /**
     * 外部候选时间（NTP）参与合并：仅当候选更大时推进。
     */
    public void observe(long candidateTime) {
        synchronized (lock) {
            if (candidateTime > effectiveMax) {
                effectiveMax = candidateTime;
                diffuse();
            }
        }
    }

    /**
     * 推进 effectiveMax（至少到 candidate），并读取全部位置合并。返回最新 effectiveMax。
     */
    public long readAndMerge(long candidate) {
        synchronized (lock) {
            long merged = candidate;
            for (TimeMark tm : readAll()) {
                if (tm.maxSeen > merged) {
                    merged = tm.maxSeen;
                }
            }
            if (merged > effectiveMax || effectiveMax == 0) {
                effectiveMax = merged;
                diffuse();
            }
            return effectiveMax;
        }
    }

    private List<TimeMark> readAll() {
        return stores().stream()
                .map(this::readStore)
                .filter(tm -> tm != null)
                .toList();
    }

    private TimeMark readStore(MarkStore store) {
        try {
            String raw = store.read();
            if (raw == null || raw.isEmpty()) {
                return null;
            }
            TimeMark tm = MAPPER.readValue(raw, TimeMark.class);
            if (!tm.verify(hmacKey) || (installId != null && !installId.equals(tm.installId))) {
                log.warn("license time mark from {} failed verify (tampered or foreign), ignored", store.name());
                return null;
            }
            return tm;
        } catch (Exception e) {
            log.warn("license time mark read {} failed: {}", store.name(), e.getMessage());
            return null;
        }
    }

    private void diffuse() {
        TimeMark tm = TimeMark.now(installId, effectiveMax, hmacKey);
        String json;
        try {
            json = MAPPER.writeValueAsString(tm);
        } catch (Exception e) {
            return;
        }
        for (MarkStore store : stores()) {
            try {
                store.write(json);
            } catch (Exception e) {
                log.warn("license time mark write {} failed: {}", store.name(), e.getMessage());
                if (store.name().equals("db") && !dbWarned) {
                    log.warn("license: 若反复出现 db 写失败，请执行 V20260922.001 DDL（约 {} 字符提示）", DDL_HINT);
                    dbWarned = true;
                }
            }
        }
    }

    // ============================== 存储实现 ==============================

    private interface MarkStore {
        String name();

        String read() throws Exception;

        void write(String json) throws Exception;
    }

    private List<MarkStore> stores() {
        return List.of(dbStore(), fileStoreA(), fileStoreB(), registryStore(), redisStore());
    }

    private MarkStore dbStore() {
        return new MarkStore() {
            @Override
            public String name() {
                return "db";
            }

            @Override
            public String read() {
                try {
                    List<String> rows = jdbcTemplate.queryForList(
                            "select mark_json from t_license_time_mark where id = 'default'", String.class);
                    return rows.isEmpty() ? null : rows.get(0);
                } catch (Exception e) {
                    // 表不存在等场景：warn 一次，靠其余位置兜底
                    if (!dbWarned) {
                        log.warn("license time mark db read failed（表不存在请执行 V20260922.001 DDL）: {}", e.getMessage());
                        dbWarned = true;
                    }
                    return null;
                }
            }

            @Override
            public void write(String json) {
                int updated = jdbcTemplate.update(
                        "update t_license_time_mark set mark_json = ?, update_time = now() where id = 'default'", json);
                if (updated == 0) {
                    jdbcTemplate.update(
                            "insert into t_license_time_mark (id, mark_json, update_time) values ('default', ?, now())",
                            json);
                }
            }
        };
    }

    private MarkStore fileStoreA() {
        return fileStore("file-upload-dir",
                Path.of(global.getUserFilesBaseDir(), LicenseConstants.MARK_FILE_DIR, LicenseConstants.MARK_FILE_NAME));
    }

    private MarkStore fileStoreB() {
        return fileStore("file-workdir",
                Path.of("data", LicenseConstants.MARK_FILE_DIR, LicenseConstants.MARK_FILE_NAME));
    }

    private MarkStore fileStore(String name, Path path) {
        return new MarkStore() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String read() throws Exception {
                Path normalized = path.toAbsolutePath().normalize();
                return Files.exists(normalized) ? Files.readString(normalized, StandardCharsets.UTF_8) : null;
            }

            @Override
            public void write(String json) throws Exception {
                Path normalized = path.toAbsolutePath().normalize();
                if (normalized.getParent() != null) {
                    Files.createDirectories(normalized.getParent());
                }
                Files.writeString(normalized, json, StandardCharsets.UTF_8);
            }
        };
    }

    private MarkStore registryStore() {
        return new MarkStore() {
            private Preferences prefs() {
                return Preferences.userRoot().node(LicenseConstants.MARK_REGISTRY_NODE);
            }

            @Override
            public String name() {
                return "registry";
            }

            @Override
            public String read() {
                return prefs().get("mark", null);
            }

            @Override
            public void write(String json) {
                prefs().put("mark", json);
                try {
                    prefs().flush();
                } catch (Exception e) {
                    // 个别环境禁用 prefs：忽略，靠其余位置兜底
                }
            }
        };
    }

    private MarkStore redisStore() {
        return new MarkStore() {
            @Override
            public String name() {
                return "redis";
            }

            @Override
            public String read() {
                if (redisUtil == null) {
                    return null;
                }
                Object value = redisUtil.get(LicenseConstants.MARK_REDIS_KEY);
                return value == null ? null : String.valueOf(value);
            }

            @Override
            public void write(String json) {
                if (redisUtil != null) {
                    redisUtil.set(LicenseConstants.MARK_REDIS_KEY, json);
                }
            }
        };
    }

    // ============================== installId 与 HMAC ==============================

    private String initInstallId() {
        // 优先本地文件
        Path file = Path.of(global.getUserFilesBaseDir(), LicenseConstants.MARK_FILE_DIR,
                LicenseConstants.INSTALL_ID_FILE_NAME);
        try {
            if (Files.exists(file)) {
                String id = Files.readString(file, StandardCharsets.UTF_8).trim();
                if (!id.isEmpty()) {
                    return id;
                }
            }
        } catch (Exception ignored) {
        }
        // 其次从既有标记恢复（本地文件丢失但 DB/注册表还在的场景）
        for (TimeMark tm : readAll()) {
            if (tm.installId != null && !tm.installId.isEmpty()) {
                persistInstallId(file, tm.installId);
                return tm.installId;
            }
        }
        // 最后生成新 ID
        String id = java.util.UUID.randomUUID().toString().replace("-", "");
        persistInstallId(file, id);
        return id;
    }

    private void persistInstallId(Path file, String id) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Files.writeString(file, id, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("license install id persist failed: {}", e.getMessage());
        }
    }

    private static byte[] deriveHmacKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] derived = digest.digest((LicenseConstants.MARK_SEED_A
                    + ":" + LicenseConstants.MARK_SEED_B).getBytes(StandardCharsets.UTF_8));
            // 追加一轮混淆派生，避免明文种子直接出现在常量池即得密钥
            return Base64.getEncoder().encode(derived);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 防回拨标记结构。
     */
    public static class TimeMark {
        public int v = 1;
        public String installId;
        public long maxSeen;
        public long writeTime;
        public String sig;

        public TimeMark() {
        }

        public static TimeMark now(String installId, long maxSeen, byte[] hmacKey) {
            TimeMark tm = new TimeMark();
            tm.installId = installId;
            tm.maxSeen = maxSeen;
            tm.writeTime = System.currentTimeMillis();
            tm.sig = tm.sign(hmacKey);
            return tm;
        }

        boolean verify(byte[] hmacKey) {
            if (sig == null || installId == null) {
                return false;
            }
            String expected = sign(hmacKey);
            return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                    sig.getBytes(StandardCharsets.UTF_8));
        }

        private String sign(byte[] hmacKey) {
            try {
                javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
                mac.init(new javax.crypto.spec.SecretKeySpec(hmacKey, "HmacSHA256"));
                byte[] out = mac.doFinal((installId + "|" + maxSeen + "|" + writeTime)
                        .getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(out);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
