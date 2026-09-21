package org.ssssssss.magicboot.license;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 授权文件解析与 Ed25519 验签。
 * 验签只认硬编码公钥：客户自造密钥对签出的文件公钥不匹配必然失败；
 * 从公钥/已签文件反推私钥计算不可行。
 */
public class LicenseVerifier {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static volatile PublicKey cachedPublicKey;

    private LicenseVerifier() {
    }

    /**
     * 解析 .lic 文件内容并验签。任何异常都抛出，由调用方决定处置。
     */
    public static LicensePayload parseAndVerify(byte[] content) throws Exception {
        LicensePayload payload = MAPPER.readValue(content, LicensePayload.class);
        if (payload.getSignature() == null || payload.getSignature().isEmpty()) {
            throw new IllegalArgumentException("license signature missing");
        }
        if (payload.getLicenseId() == null || payload.getCustomer() == null
                || payload.getExpireAt() == null || payload.getFingerprints() == null) {
            throw new IllegalArgumentException("license payload incomplete");
        }
        Signature signature = Signature.getInstance("Ed25519");
        signature.initVerify(publicKey());
        signature.update(payload.canonicalBytes());
        boolean ok = signature.verify(Base64.getDecoder().decode(payload.getSignature()));
        if (!ok) {
            throw new IllegalArgumentException("license signature invalid");
        }
        return payload;
    }

    /**
     * 用内置私钥（签发侧）对载荷签名。
     */
    public static String sign(LicensePayload payload, byte[] privateKeyPkcs8) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");
        var privateKey = keyFactory.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(privateKeyPkcs8));
        Signature signature = Signature.getInstance("Ed25519");
        signature.initSign(privateKey);
        signature.update(payload.canonicalBytes());
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private static PublicKey publicKey() throws Exception {
        if (cachedPublicKey == null) {
            byte[] encoded = Base64.getDecoder().decode(LicenseConstants.LICENSE_PUBLIC_KEY_B64);
            cachedPublicKey = KeyFactory.getInstance("Ed25519")
                    .generatePublic(new X509EncodedKeySpec(encoded));
        }
        return cachedPublicKey;
    }

    /**
     * 载荷文本摘要（日志/展示用，不含敏感内容）。
     */
    public static String describe(LicensePayload payload) {
        return "licenseId=" + payload.getLicenseId() + ", customer=" + payload.getCustomer()
                + ", expireAt=" + payload.getExpireAt();
    }

    @SuppressWarnings("unused")
    private static byte[] utf8(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
