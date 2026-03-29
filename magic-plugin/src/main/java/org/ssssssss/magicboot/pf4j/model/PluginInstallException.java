package org.ssssssss.magicboot.pf4j.model;

/**
 * 鎻掍欢瀹夎寮傚父锛屾惡甯︽爣鍑嗛敊璇爜銆? */
public class PluginInstallException extends RuntimeException {

    private final PluginInstallErrorCode errorCode;

    public PluginInstallException(PluginInstallErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PluginInstallException(PluginInstallErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public PluginInstallErrorCode getErrorCode() {
        return errorCode;
    }
}
