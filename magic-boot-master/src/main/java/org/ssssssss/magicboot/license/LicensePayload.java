package org.ssssssss.magicboot.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 授权文件载荷与规范化序列化。
 * 签名覆盖规范化后的载荷（键名字典序），签发与验签两侧必须使用同一序列化实现。
 */
public class LicensePayload {

    private static final ObjectMapper CANONICAL_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    private String licenseId;
    private String customer;
    private String issuedAt;       // yyyy-MM-dd
    private String expireAt;       // yyyy-MM-dd，含当天
    private List<String> fingerprints; // [系统盘, 主板, 网卡MAC] 各自 sha256
    private int formatVersion = 1;
    private String notes = "";
    private Integer graceDays;     // 宽限天数（签入签名，客户改配置无效；null=未包含，按 7 处理）
    private String signature;      // Ed25519, base64

    /**
     * 规范化载荷（不含 signature）：键名字典序序列化，作为签名/验签的字节源。
     */
    public byte[] canonicalBytes() {
        try {
            Map<String, Object> sorted = new TreeMap<>();
            sorted.put("licenseId", licenseId);
            sorted.put("customer", customer);
            sorted.put("issuedAt", issuedAt);
            sorted.put("expireAt", expireAt);
            sorted.put("fingerprints", fingerprints);
            sorted.put("formatVersion", formatVersion);
            sorted.put("notes", notes == null ? "" : notes);
            if (graceDays != null) {
                sorted.put("graceDays", graceDays); // 签名覆盖宽限天数，防止客户改配置延长
            }
            return CANONICAL_MAPPER.writeValueAsBytes(sorted);
        } catch (Exception e) {
            throw new IllegalStateException("license payload serialize failed", e);
        }
    }

    /**
     * 完整文件内容（载荷原序 + signature 字段），供签发下载。
     */
    public byte[] toFileBytes() {
        try {
            Map<String, Object> ordered = new LinkedHashMap<>();
            ordered.put("licenseId", licenseId);
            ordered.put("customer", customer);
            ordered.put("issuedAt", issuedAt);
            ordered.put("expireAt", expireAt);
            ordered.put("fingerprints", fingerprints);
            ordered.put("formatVersion", formatVersion);
            ordered.put("notes", notes);
            if (graceDays != null) {
                ordered.put("graceDays", graceDays);
            }
            ordered.put("signature", signature);
            return CANONICAL_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(ordered);
        } catch (Exception e) {
            throw new IllegalStateException("license file serialize failed", e);
        }
    }

    public String getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(String licenseId) {
        this.licenseId = licenseId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    public List<String> getFingerprints() {
        return fingerprints;
    }

    public void setFingerprints(List<String> fingerprints) {
        this.fingerprints = fingerprints;
    }

    public int getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(int formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getGraceDays() {
        return graceDays;
    }

    public void setGraceDays(Integer graceDays) {
        this.graceDays = graceDays;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
