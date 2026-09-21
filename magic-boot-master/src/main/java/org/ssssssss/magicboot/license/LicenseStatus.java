package org.ssssssss.magicboot.license;

/**
 * 授权状态机。
 * missing：未导入授权文件；warning：剩余 ≤30 天；grace：过期但在宽限期内；
 * expired：过期且超出宽限期；abnormal：时钟回拨超容差。
 * missing/expired/abnormal 三态阻断业务；warning/grace 提醒放行。
 */
public enum LicenseStatus {

    DISABLED("disabled", "授权校验未启用"),
    OK("ok", "授权有效"),
    WARNING("warning", "授权即将到期"),
    GRACE("grace", "授权已过期，处于宽限期"),
    EXPIRED("expired", "授权已过期"),
    ABNORMAL("abnormal", "检测到系统时间异常"),
    MISSING("missing", "系统未授权");

    private final String key;
    private final String defaultMessage;

    LicenseStatus(String key, String defaultMessage) {
        this.key = key;
        this.defaultMessage = defaultMessage;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * 是否阻断业务接口（授权管理/登录接口由白名单放行，不受此影响）。
     */
    public boolean isBlocking() {
        return this == EXPIRED || this == ABNORMAL || this == MISSING;
    }
}
