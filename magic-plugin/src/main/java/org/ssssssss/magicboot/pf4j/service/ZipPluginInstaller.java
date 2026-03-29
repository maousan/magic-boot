package org.ssssssss.magicboot.pf4j.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import org.ssssssss.magicboot.pf4j.configuration.PluginProperties;
import org.ssssssss.magicboot.pf4j.model.PluginInstallErrorCode;
import org.ssssssss.magicboot.pf4j.model.PluginInstallException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.util.Base64;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ZipPluginInstaller {

    private static final String MANIFEST_FILE_NAME = "Manifest.json";
    private static final String DEFAULT_ENTRY_JAR = "plugin.jar";

    private final PluginProperties pluginProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    ZipPluginInstaller(PluginProperties pluginProperties) {
        this.pluginProperties = pluginProperties;
    }

    InstallPackage prepare(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_MANIFEST_INVALID, "Upload file is required");
        }

        String packageName = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        if (!packageName.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_INVALID_TYPE, "Only ZIP plugin packages are supported");
        }

        Path pluginPath = ensurePluginDirectoryExists();
        Path tempDir = Files.createTempDirectory(pluginPath, "upload-").normalize();
        Path uploadZip = tempDir.resolve("plugin.zip");

        try {
            file.transferTo(uploadZip.toFile());
            extractZipArchive(uploadZip, tempDir);

            Path manifestPath = tempDir.resolve(MANIFEST_FILE_NAME).normalize();
            if (!Files.exists(manifestPath)) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID, "ZIP package is missing Manifest.json");
            }
            PluginPackageManifest manifest = parseManifest(manifestPath);
            validateManifest(manifest);
            validateRuntimeVersion(manifest.getRequiresMagicBoot());

            String entryJarName = Optional.ofNullable(manifest.getEntryJar())
                    .filter(s -> !s.isBlank())
                    .orElse(DEFAULT_ENTRY_JAR);
            Path entryJar = tempDir.resolve(entryJarName).normalize();
            if (!entryJar.startsWith(tempDir)) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID, "entryJar path is invalid");
            }
            if (!Files.exists(entryJar) || Files.isDirectory(entryJar)) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID, "entryJar does not exist: " + entryJarName);
            }
            if (!entryJar.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID, "entryJar must point to a .jar file");
            }

            String actualChecksum = calculateSha256(entryJar);
            if (!actualChecksum.equalsIgnoreCase(manifest.getChecksumSha256().trim())) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_CHECKSUM_MISMATCH, "Plugin checksum mismatch");
            }
            verifySignatureIfRequired(manifest, actualChecksum);

            String targetJarName = buildInstalledJarFileName(manifest, entryJar.getFileName().toString());
            Path installedJar = resolveNonConflictingTargetPath(pluginPath, targetJarName);
            Files.copy(entryJar, installedJar);

            String manifestJson = Files.readString(manifestPath, StandardCharsets.UTF_8);
            String permissionsJson = objectMapper.writeValueAsString(manifest.getPermissions());
            String manifestVersion = Optional.ofNullable(manifest.getManifestVersion()).filter(s -> !s.isBlank()).orElse("1.0");
            return new InstallPackage(
                    installedJar,
                    manifest.getPluginId(),
                    manifest.getVersion(),
                    actualChecksum,
                    manifestVersion,
                    manifestJson,
                    manifest.getRequiresMagicBoot(),
                    permissionsJson
            );
        } finally {
            deleteDirectoryQuietly(tempDir);
        }
    }

    private Path ensurePluginDirectoryExists() throws IOException {
        Path pluginPath = Paths.get(pluginProperties.getDir()).toAbsolutePath();
        if (!Files.exists(pluginPath)) {
            Files.createDirectories(pluginPath);
        }
        return pluginPath;
    }

    private PluginPackageManifest parseManifest(Path manifestPath) {
        try {
            return objectMapper.readValue(manifestPath.toFile(), PluginPackageManifest.class);
        } catch (IOException e) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_MANIFEST_INVALID, "Manifest.json parse failed: " + e.getMessage(), e);
        }
    }

    private void validateManifest(PluginPackageManifest manifest) {
        if (manifest == null) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_MANIFEST_INVALID, "Manifest.json is required");
        }
        requireNotBlank(manifest.getPluginId(), "Manifest.pluginId is required");
        requireNotBlank(manifest.getVersion(), "Manifest.version is required");
        requireNotBlank(manifest.getDisplayName(), "Manifest.displayName is required");
        requireNotBlank(manifest.getRequiresMagicBoot(), "Manifest.requiresMagicBoot is required");
        requireNotBlank(manifest.getChecksumSha256(), "Manifest.checksumSha256 is required");
        if (isSignatureVerificationEnabled()
                && (manifest.getSignature() == null || manifest.getSignature().isBlank())) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_SIGNATURE_INVALID, "Manifest.signature is required");
        }
        if (manifest.getPermissions() == null) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_MANIFEST_INVALID, "Manifest.permissions is required");
        }
    }

    private void verifySignatureIfRequired(PluginPackageManifest manifest, String actualChecksum) {
        if (!isSignatureVerificationEnabled()) {
            return;
        }
        String publicKeyText = Optional.ofNullable(pluginProperties.getSignaturePublicKey()).orElse("").trim();
        if (publicKeyText.isBlank()) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_SIGNATURE_INVALID, "plugin.signaturePublicKey is required");
        }

        try {
            PublicKey publicKey = parsePublicKey(publicKeyText);
            String algorithm = Optional.ofNullable(pluginProperties.getSignatureAlgorithm())
                    .filter(s -> !s.isBlank())
                    .orElse("SHA256withRSA");
            Signature verifier = Signature.getInstance(algorithm);
            verifier.initVerify(publicKey);
            verifier.update(actualChecksum.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = Base64.getDecoder().decode(manifest.getSignature().trim());
            if (!verifier.verify(signatureBytes)) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_SIGNATURE_INVALID, "Plugin signature verify failed");
            }
        } catch (PluginInstallException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_SIGNATURE_INVALID,
                    "Plugin signature verify failed: " + ex.getMessage(), ex);
        }
    }

    private boolean isSignatureVerificationEnabled() {
        return pluginProperties.isSignatureRequired() && pluginProperties.isSignatureForceVerify();
    }

    private PublicKey parsePublicKey(String rawKey) throws Exception {
        String normalized = rawKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    private void extractZipArchive(Path zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path output = resolveSafeZipPath(targetDir, entry);
                if (entry.isDirectory()) {
                    Files.createDirectories(output);
                } else {
                    Files.createDirectories(output.getParent());
                    try (OutputStream os = Files.newOutputStream(output)) {
                        zis.transferTo(os);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private Path resolveSafeZipPath(Path targetDir, ZipEntry entry) {
        String entryName = Optional.ofNullable(entry.getName()).orElse("");
        if (entryName.isBlank()) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID, "ZIP entry path is empty");
        }
        String normalizedName = entryName.replace("\\", "/");
        Path output = targetDir.resolve(normalizedName).normalize().toAbsolutePath();
        Path normalizedTargetDir = targetDir.toAbsolutePath().normalize();
        if (!output.startsWith(normalizedTargetDir)) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_UPLOAD_ZIP_STRUCTURE_INVALID, "ZIP entry path is invalid: " + entryName);
        }
        return output;
    }

    private String calculateSha256(Path filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream inputStream = Files.newInputStream(filePath)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_INSTALL_FAILED, "Failed to calculate SHA-256: " + e.getMessage(), e);
        }
    }

    private void validateRuntimeVersion(String requiresMagicBoot) {
        String runtimeVersion = Optional.ofNullable(pluginProperties.getRuntimeVersion()).orElse("").trim();
        String requiredVersion = Optional.ofNullable(requiresMagicBoot).orElse("").trim();
        if (runtimeVersion.isBlank() || requiredVersion.isBlank() || "*".equals(requiredVersion)) {
            return;
        }
        if (requiredVersion.startsWith(">=")) {
            String minimalVersion = requiredVersion.substring(2).trim();
            if (compareVersion(runtimeVersion, minimalVersion) < 0) {
                throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_VERSION_INCOMPATIBLE, "Runtime version does not satisfy: " + requiredVersion);
            }
            return;
        }
        if (!runtimeVersion.equals(requiredVersion)) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_VERSION_INCOMPATIBLE, "Runtime version does not satisfy: " + requiredVersion);
        }
    }

    private int compareVersion(String left, String right) {
        String[] leftParts = left.split("\\.");
        String[] rightParts = right.split("\\.");
        int max = Math.max(leftParts.length, rightParts.length);
        for (int i = 0; i < max; i++) {
            int lv = i < leftParts.length ? parseVersionPart(leftParts[i]) : 0;
            int rv = i < rightParts.length ? parseVersionPart(rightParts[i]) : 0;
            if (lv != rv) {
                return Integer.compare(lv, rv);
            }
        }
        return 0;
    }

    private int parseVersionPart(String part) {
        String value = Optional.ofNullable(part).orElse("").trim();
        int end = 0;
        while (end < value.length() && Character.isDigit(value.charAt(end))) {
            end++;
        }
        if (end == 0) {
            return 0;
        }
        return Integer.parseInt(value.substring(0, end));
    }

    private String buildInstalledJarFileName(PluginPackageManifest manifest, String fallbackJarName) {
        String pluginId = sanitizeFilePart(manifest.getPluginId());
        String version = sanitizeFilePart(manifest.getVersion());
        if (!pluginId.isBlank() && !version.isBlank()) {
            return pluginId + "-" + version + ".jar";
        }
        return fallbackJarName;
    }

    private String sanitizeFilePart(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private Path resolveNonConflictingTargetPath(Path parent, String fileName) {
        String candidate = Optional.ofNullable(fileName).filter(s -> !s.isBlank())
                .orElse("upload-" + System.currentTimeMillis() + ".jar");
        int dot = candidate.lastIndexOf('.');
        String base = dot > 0 ? candidate.substring(0, dot) : candidate;
        String ext = dot > 0 ? candidate.substring(dot) : "";
        Path target = parent.resolve(candidate);
        int index = 1;
        while (Files.exists(target)) {
            target = parent.resolve(base + "-" + index + ext);
            index++;
        }
        return target;
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new PluginInstallException(PluginInstallErrorCode.PLUGIN_MANIFEST_INVALID, message);
        }
    }

    private void deleteDirectoryQuietly(Path dir) {
        if (dir == null || !Files.exists(dir)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }

    record InstallPackage(
            Path jarPath,
            String pluginId,
            String version,
            String packageChecksum,
            String manifestVersion,
            String manifestJson,
            String requiresMagicBoot,
            String permissionsJson
    ) {
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PluginPackageManifest {
        private String pluginId;
        private String version;
        private String manifestVersion;
        private String displayName;
        private String entryJar;
        private String requiresMagicBoot;
        private Object permissions;
        private String checksumSha256;
        private String signature;
    }
}
