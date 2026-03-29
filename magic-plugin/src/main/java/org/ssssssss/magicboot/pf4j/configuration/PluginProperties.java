package org.ssssssss.magicboot.pf4j.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * PF4J plugin system properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "plugin")
public class PluginProperties {

    /**
     * Enable plugin subsystem.
     */
    private boolean enabled = true;

    /**
     * Plugin directory.
     */
    private String dir = "./plugins/";

    /**
     * Auto load plugins on startup.
     */
    private boolean autoLoad = true;

    /**
     * Auto start plugins after loading.
     */
    private boolean autoStart = true;

    /**
     * Sync runtime plugins into DB after startup.
     */
    private boolean initSyncOnStartup = true;

    /**
     * Runtime magic-boot version, used by requiresMagicBoot check.
     */
    private String runtimeVersion = "";

    /**
     * Whether upload API only accepts ZIP package.
     */
    private boolean uploadZipOnly = true;

    /**
     * Whether ZIP signature validation is mandatory.
     */
    private boolean signatureRequired = true;

    /**
     * Whether to enforce signature validation in upload flow.
     * false means signature check is skipped (for compatibility / gray rollout).
     */
    private boolean signatureForceVerify = true;

    /**
     * Signature algorithm, e.g. SHA256withRSA.
     */
    private String signatureAlgorithm = "SHA256withRSA";

    /**
     * Signature public key (PEM or Base64 X509).
     */
    private String signaturePublicKey = "";
}
