package org.ssssssss.magicboot.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.NetworkIF;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * 机器指纹采集（OSHI）：系统盘序列号 / 主板序列号 / 物理网卡 MAC 集合，各自规范化后 sha256（取前 32 位 hex）。
 * 校验采用 3 项命中 ≥2（K-of-N）：单标识变更不误伤，整套搬机至少两项同变即失效。
 * 指纹是机器静态属性，启动采集一次并缓存。
 *
 * <p>对外只呈现一个「机器指纹码」：3 项指纹打包成 base64url(JSON) 的单一编码，
 * 客户整块复制、签发侧整块解码还原 3 项——用户不需要理解内部有哪几项。
 */
@Component
public class MachineFingerprintService {

    private static final Logger log = LoggerFactory.getLogger(MachineFingerprintService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final int ITEM_HEX_LEN = 32; // sha256 截断为 128 位/项，碰撞风险可忽略

    private List<String> cached; // 与授权文件 fingerprints 顺序一一对应；采集失败项为 null

    /**
     * 采集当前机器指纹（缓存）。返回列表长度恒为 3，顺序 [系统盘, 主板, 网卡MAC]，
     * 每项为 32 位 hex；采集失败项为 null。
     */
    public synchronized List<String> collect() {
        if (cached != null) {
            return cached;
        }
        List<String> result = new ArrayList<>(3);
        result.add(null);
        result.add(null);
        result.add(null);
        try {
            SystemInfo si = new SystemInfo();
            var hal = si.getHardware();

            // 1. 系统盘序列号：找挂载根分区（Linux "/"，Windows "C:\"）所在磁盘
            for (HWDiskStore disk : hal.getDiskStores()) {
                for (HWPartition part : disk.getPartitions()) {
                    String mount = part.getMountPoint();
                    if (mount != null && (mount.equals("/") || mount.toUpperCase().startsWith("C:"))) {
                        result.set(0, sha256Lower(disk.getSerial()));
                        break;
                    }
                }
                if (result.get(0) != null) {
                    break;
                }
            }

            // 2. 主板序列号
            result.set(1, sha256Lower(hal.getComputerSystem().getBaseboard().getSerialNumber()));

            // 3. 物理网卡 MAC 集合（排除回环/虚拟/全零，小写规范化后排序拼接）
            List<String> macs = new ArrayList<>();
            for (NetworkIF nif : hal.getNetworkIFs()) {
                String mac = nif.getMacaddr();
                if (mac == null) {
                    continue;
                }
                String normalized = mac.toLowerCase().replace(":", "").replace("-", "");
                if (normalized.isEmpty() || normalized.matches("0+")) {
                    continue;
                }
                if (nif.getName() != null && nif.getName().startsWith("lo")) {
                    continue;
                }
                macs.add(normalized);
            }
            Collections.sort(macs);
            if (!macs.isEmpty()) {
                result.set(2, sha256Lower(String.join("|", macs)));
            }

            // 序列号占位值清洗：部分虚拟机主板序列号为通用默认值（区分度低，靠三项组合补偿）
            for (int i = 0; i < 3; i++) {
                if (result.get(i) != null && isPlaceholder(result.get(i))) {
                    log.info("license fingerprint: item {} is a known placeholder, treat as unavailable", i);
                    result.set(i, null);
                }
            }
            // 截断为固定长度
            for (int i = 0; i < 3; i++) {
                if (result.get(i) != null) {
                    result.set(i, result.get(i).substring(0, ITEM_HEX_LEN));
                }
            }
            cached = result;
            log.info("license fingerprint collected: disk={}, board={}, mac={}",
                    mask(result.get(0)), mask(result.get(1)), mask(result.get(2)));
        } catch (Throwable e) {
            // OSHI 初始化失败等极端情况：返回空指纹，验签按不可用处理
            log.warn("license fingerprint collect failed: {}", e.getMessage());
            cached = result;
        }
        return cached;
    }

    /**
     * 与授权文件指纹比对：3 项命中 ≥2 通过。
     * licenseFingerprints 为空/位数不足按不可用处理，直接不通过。
     */
    public boolean verify(List<String> licenseFingerprints) {
        if (licenseFingerprints == null || licenseFingerprints.size() < 3) {
            return false;
        }
        List<String> local = collect();
        int available = 0;
        int matched = 0;
        for (int i = 0; i < 3; i++) {
            if (local.get(i) != null && licenseFingerprints.get(i) != null
                    && !licenseFingerprints.get(i).trim().isEmpty()) {
                available++;
                if (local.get(i).equals(licenseFingerprints.get(i).trim().toLowerCase())) {
                    matched++;
                }
            }
        }
        // 剩余项不足 3 时不允许再降容差：仍需命中 2
        return available >= 2 && matched >= 2;
    }

    /**
     * 机器指纹码：3 项指纹打包成 base64url(JSON) 单一编码。
     * 客户在「系统授权」页看到并复制的只有这一个码。
     */
    public String fingerprintCode() {
        List<String> local = collect();
        try {
            List<String> packed = new ArrayList<>(3);
            for (String item : local) {
                packed.add(item == null ? "" : item);
            }
            byte[] json = MAPPER.writeValueAsBytes(packed);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception e) {
            throw new IllegalStateException("fingerprint code encode failed", e);
        }
    }

    /**
     * 解析签发表单粘贴的机器指纹码 → 3 项指纹列表（空串表示该项不可用）。
     * 至少 2 项有效，否则抛出异常。
     */
    public static List<String> parseFingerprintCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("机器指纹码不能为空");
        }
        List<?> parsed;
        try {
            byte[] json = Base64.getUrlDecoder().decode(code.trim());
            parsed = MAPPER.readValue(json, List.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("机器指纹码格式无效，请完整复制客户「系统授权」页展示的指纹码");
        }
        if (parsed.size() != 3) {
            throw new IllegalArgumentException("机器指纹码格式无效");
        }
        List<String> result = new ArrayList<>(3);
        int valid = 0;
        for (Object item : parsed) {
            String value = item == null ? "" : String.valueOf(item).trim().toLowerCase();
            if (!value.isEmpty()) {
                if (!value.matches("[0-9a-f]{32}")) {
                    throw new IllegalArgumentException("机器指纹码内容无效（指纹应为 32 位十六进制）");
                }
                valid++;
            }
            result.add(value);
        }
        if (valid < 2) {
            throw new IllegalArgumentException("机器指纹码中有效指纹不足 2 项，无法签发");
        }
        return result;
    }

    private static String sha256Lower(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.toLowerCase().replace(" ", "").replace("-", "").trim();
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(normalized.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 常见虚拟机占位序列号（通用默认值无区分度）。
     */
    private static boolean isPlaceholder(String sha) {
        // "None"/"Default string"/"0"*n/"To Be Filled" 等清洗后的哈希无法逐一枚举，
        // 这里对原始值已在规范化阶段清洗，仅保留防御：全 0 哈希视为占位
        return sha.equals(sha256Lower("none")) || sha.equals(sha256Lower("0"))
                || sha.equals(sha256Lower("tobefilledbyo.e.m."));
    }

    private static String mask(String v) {
        return v == null ? "unavailable" : v.substring(0, Math.min(8, v.length())) + "…";
    }
}
