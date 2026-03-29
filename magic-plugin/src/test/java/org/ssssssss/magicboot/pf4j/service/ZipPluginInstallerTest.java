package org.ssssssss.magicboot.pf4j.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;
import org.ssssssss.magicboot.pf4j.model.PluginInstallErrorCode;
import org.ssssssss.magicboot.pf4j.model.PluginInstallException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipPluginInstallerTest {

    @Test
    @DisplayName("non zip upload should be rejected with invalid type")
    void prepare_whenNonZip_shouldFail(@TempDir Path tempDir) {
        PluginProperties properties = buildProperties(tempDir, null);
        ZipPluginInstaller installer = new ZipPluginInstaller(properties);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "demo.jar",
                "application/java-archive",
                new byte[]{1, 2, 3}
        );

        PluginInstallException ex = assertThrows(PluginInstallException.class, () -> installer.prepare(file));
        assertEquals(PluginInstallErrorCode.PLUGIN_UPLOAD_INVALID_TYPE, ex.getErrorCode());
    }

    @Test
    @DisplayName("missing manifest should be rejected with zip structure error")
    void prepare_whenMissingManifest_shouldFail(@TempDir Path tempDir) throws IOException {
        PluginProperties properties = buildProperties(tempDir, null);
        ZipPluginInstaller installer = new ZipPluginInstaller(properties);

        byte[] zipBytes = buildZip(Map.of("plugin.jar", new byte[]{1, 2, 3}));
        MockMultipartFile file = new MockMultipartFile("file", "demo.zip", "application/zip", zipBytes);

        PluginInstallException ex = assertThrows(PluginInstallException.class, () -> installer.prepare(file));
        assertEquals(PluginInstallErrorCode.PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID, ex.getErrorCode());
    }

    @Test
    @DisplayName("checksum mismatch should be rejected with checksum error")
    void prepare_whenChecksumMismatch_shouldFail(@TempDir Path tempDir) throws Exception {
        KeyPair pair = generateKeyPair();
        PluginProperties properties = buildProperties(tempDir, pair);
        ZipPluginInstaller installer = new ZipPluginInstaller(properties);

        byte[] jar = new byte[]{1, 2, 3, 4};
        String signature = sign(sha256(jar), pair);
        String manifest = """
                {
                  "pluginId":"demo-plugin",
                  "version":"1.0.0",
                  "displayName":"Demo",
                  "entryJar":"plugin.jar",
                  "requiresMagicBoot":"1.0.0",
                  "permissions":["plugin:view"],
                  "checksumSha256":"deadbeef",
                  "signature":"%s"
                }
                """.formatted(signature);

        byte[] zipBytes = buildZip(new LinkedHashMap<>() {{
            put("Manifest.json", manifest.getBytes(StandardCharsets.UTF_8));
            put("plugin.jar", jar);
        }});
        MockMultipartFile file = new MockMultipartFile("file", "demo.zip", "application/zip", zipBytes);

        PluginInstallException ex = assertThrows(PluginInstallException.class, () -> installer.prepare(file));
        assertEquals(PluginInstallErrorCode.PLUGIN_CHECKSUM_MISMATCH, ex.getErrorCode());
    }

    @Test
    @DisplayName("signature mismatch should fail with signature code")
    void prepare_whenSignatureMismatch_shouldFail(@TempDir Path tempDir) throws Exception {
        KeyPair pair = generateKeyPair();
        PluginProperties properties = buildProperties(tempDir, pair);
        ZipPluginInstaller installer = new ZipPluginInstaller(properties);

        byte[] jar = new byte[]{9, 8, 7, 6};
        String checksum = sha256(jar);
        String badSignature = Base64.getEncoder().encodeToString("bad-signature".getBytes(StandardCharsets.UTF_8));
        String manifest = """
                {
                  "pluginId":"demo-plugin",
                  "version":"1.0.0",
                  "displayName":"Demo",
                  "entryJar":"plugin.jar",
                  "requiresMagicBoot":"1.0.0",
                  "permissions":["plugin:view"],
                  "checksumSha256":"%s",
                  "signature":"%s"
                }
                """.formatted(checksum, badSignature);

        byte[] zipBytes = buildZip(new LinkedHashMap<>() {{
            put("Manifest.json", manifest.getBytes(StandardCharsets.UTF_8));
            put("plugin.jar", jar);
        }});
        MockMultipartFile file = new MockMultipartFile("file", "demo.zip", "application/zip", zipBytes);

        PluginInstallException ex = assertThrows(PluginInstallException.class, () -> installer.prepare(file));
        assertEquals(PluginInstallErrorCode.PLUGIN_SIGNATURE_INVALID, ex.getErrorCode());
    }

    @Test
    @DisplayName("runtime version mismatch should fail with incompatible code")
    void prepare_whenRuntimeVersionMismatch_shouldFail(@TempDir Path tempDir) throws Exception {
        KeyPair pair = generateKeyPair();
        PluginProperties properties = buildProperties(tempDir, pair);
        properties.setRuntimeVersion("1.0.0");
        ZipPluginInstaller installer = new ZipPluginInstaller(properties);

        byte[] jar = new byte[]{9, 8, 7, 6};
        String checksum = sha256(jar);
        String signature = sign(checksum, pair);
        String manifest = """
                {
                  "pluginId":"demo-plugin",
                  "version":"1.0.0",
                  "displayName":"Demo",
                  "entryJar":"plugin.jar",
                  "requiresMagicBoot":">=2.0.0",
                  "permissions":["plugin:view"],
                  "checksumSha256":"%s",
                  "signature":"%s"
                }
                """.formatted(checksum, signature);

        byte[] zipBytes = buildZip(new LinkedHashMap<>() {{
            put("Manifest.json", manifest.getBytes(StandardCharsets.UTF_8));
            put("plugin.jar", jar);
        }});
        MockMultipartFile file = new MockMultipartFile("file", "demo.zip", "application/zip", zipBytes);

        PluginInstallException ex = assertThrows(PluginInstallException.class, () -> installer.prepare(file));
        assertEquals(PluginInstallErrorCode.PLUGIN_VERSION_INCOMPATIBLE, ex.getErrorCode());
    }

    @Test
    @DisplayName("valid zip should pass signature verification")
    void prepare_whenValidZip_shouldSucceed(@TempDir Path tempDir) throws Exception {
        KeyPair pair = generateKeyPair();
        PluginProperties properties = buildProperties(tempDir, pair);
        properties.setRuntimeVersion("1.2.0");
        ZipPluginInstaller installer = new ZipPluginInstaller(properties);

        byte[] jar = new byte[]{9, 8, 7, 6};
        String checksum = sha256(jar);
        String signature = sign(checksum, pair);
        String manifest = """
                {
                  "pluginId":"demo-plugin",
                  "version":"1.0.0",
                  "manifestVersion":"1.0",
                  "displayName":"Demo",
                  "entryJar":"plugin.jar",
                  "requiresMagicBoot":">=1.0.0",
                  "permissions":["plugin:view"],
                  "checksumSha256":"%s",
                  "signature":"%s"
                }
                """.formatted(checksum, signature);

        byte[] zipBytes = buildZip(new LinkedHashMap<>() {{
            put("Manifest.json", manifest.getBytes(StandardCharsets.UTF_8));
            put("plugin.jar", jar);
        }});
        MockMultipartFile file = new MockMultipartFile("file", "demo.zip", "application/zip", zipBytes);

        ZipPluginInstaller.InstallPackage result = installer.prepare(file);

        assertEquals("demo-plugin", result.pluginId());
        assertEquals("1.0.0", result.version());
        assertEquals(checksum, result.packageChecksum());
        assertEquals("1.0", result.manifestVersion());
        assertTrue(result.manifestJson().contains("\"pluginId\":\"demo-plugin\""));
        assertEquals(">=1.0.0", result.requiresMagicBoot());
        assertTrue(result.permissionsJson().contains("plugin:view"));
        assertTrue(Files.exists(result.jarPath()));
    }

    @Test
    @DisplayName("signature force verify disabled should skip signature check")
    void prepare_whenSignatureForceVerifyDisabled_shouldSkipSignatureValidation(@TempDir Path tempDir) throws Exception {
        KeyPair pair = generateKeyPair();
        PluginProperties properties = buildProperties(tempDir, pair);
        properties.setSignatureForceVerify(false);
        properties.setRuntimeVersion("1.2.0");
        ZipPluginInstaller installer = new ZipPluginInstaller(properties);

        byte[] jar = new byte[]{9, 8, 7, 6};
        String checksum = sha256(jar);
        String manifest = """
                {
                  "pluginId":"demo-plugin",
                  "version":"1.0.0",
                  "manifestVersion":"1.0",
                  "displayName":"Demo",
                  "entryJar":"plugin.jar",
                  "requiresMagicBoot":">=1.0.0",
                  "permissions":["plugin:view"],
                  "checksumSha256":"%s"
                }
                """.formatted(checksum);

        byte[] zipBytes = buildZip(new LinkedHashMap<>() {{
            put("Manifest.json", manifest.getBytes(StandardCharsets.UTF_8));
            put("plugin.jar", jar);
        }});
        MockMultipartFile file = new MockMultipartFile("file", "demo.zip", "application/zip", zipBytes);

        ZipPluginInstaller.InstallPackage result = installer.prepare(file);
        assertEquals("demo-plugin", result.pluginId());
        assertTrue(Files.exists(result.jarPath()));
    }

    private PluginProperties buildProperties(Path tempDir, KeyPair pair) {
        PluginProperties properties = new PluginProperties();
        properties.setDir(tempDir.toString());
        properties.setSignatureRequired(true);
        properties.setSignatureForceVerify(true);
        properties.setSignatureAlgorithm("SHA256withRSA");
        if (pair != null) {
            properties.setSignaturePublicKey(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
        }
        return properties;
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private String sign(String payload, KeyPair pair) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(pair.getPrivate());
        signature.update(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private byte[] buildZip(Map<String, byte[]> entries) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                zos.write(entry.getValue());
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    private String sha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(data));
    }
}
