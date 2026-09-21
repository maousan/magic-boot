package org.ssssssss.magicboot.license;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * License 配置项。
 * license.enabled：授权总开关。dev profile 默认 false（办公开发豁免），现场 profile 默认 true。
 */
@ConfigurationProperties(prefix = "license")
public class LicenseProperties {

    /**
     * 授权总开关；未显式配置时按 profile 推断（含 dev 即关闭）。
     */
    private Boolean enabled;

    /**
     * 过期宽限天数，默认 7 天：宽限期内业务可用 + 三重提醒，超出全面阻断。
     */
    private int graceDays = 7;

    /**
     * NTP 服务器（逗号分隔），默认空 = 关闭。任何失败直接跳过，零误伤。
     */
    private String ntpServers = "";

    /**
     * 白名单追加（Ant 风格，逗号分隔），代码内置默认白名单之外使用。
     */
    private String permitPatterns = "";

    /**
     * 签发功能开关（仅办公实例开启；配套私钥路径存在才有意义）。
     */
    private boolean issueEnabled = false;

    /**
     * 签发私钥路径（可被环境变量 LICENSE_PRIVATE_KEY_PATH 覆盖语义上等同，二者取一）。
     */
    private String privateKeyPath = "";

    /**
     * 阻断时联动停止的业务插件（逗号分隔），掐掉 LED 心跳等非 HTTP 后台线程。
     */
    private String stopPlugins = "zintis-led-plugin,zintis-rfid-plugin";

    public boolean resolveEnabled(boolean devProfile) {
        if (enabled != null) {
            return enabled;
        }
        return !devProfile;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public int getGraceDays() {
        return graceDays;
    }

    public void setGraceDays(int graceDays) {
        this.graceDays = graceDays;
    }

    public String getNtpServers() {
        return ntpServers;
    }

    public void setNtpServers(String ntpServers) {
        this.ntpServers = ntpServers;
    }

    public String getPermitPatterns() {
        return permitPatterns;
    }

    public void setPermitPatterns(String permitPatterns) {
        this.permitPatterns = permitPatterns;
    }

    public boolean isIssueEnabled() {
        return issueEnabled;
    }

    public void setIssueEnabled(boolean issueEnabled) {
        this.issueEnabled = issueEnabled;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getStopPlugins() {
        return stopPlugins;
    }

    public void setStopPlugins(String stopPlugins) {
        this.stopPlugins = stopPlugins;
    }
}
