package org.ssssssss.magicboot.license;

/**
 * License 静态常量。
 * 公钥是整个授权体系的信任根：验签只认这一把公钥，客户自造密钥对签出的文件必然验签失败。
 * 私钥仅存办公签发服务器（环境变量 LICENSE_PRIVATE_KEY_PATH），与代码分离。
 */
public final class LicenseConstants {

    private LicenseConstants() {
    }

    /**
     * Ed25519 公钥（X.509 SPKI, base64）。签发私钥由乙方保管，泄露处置见部署文档。
     */
    public static final String LICENSE_PUBLIC_KEY_B64 = "MCowBQYDK2VwAyEA/wFselWaytxbmO0Oc9x0quj8nMBtYHcrC2/efr717Yk=";

    /**
     * 防回拨标记 HMAC 密钥派生种子（拆分存放，运行期拼接派生）。
     * 定位是抬高篡改门槛，不防逆向（范围外，见规格 §1）。
     */
    public static final String MARK_SEED_A = "mgBt~lic#2026#dxhm";

    public static final String MARK_SEED_B = "v1:time-mark:sign";

    /**
     * 防回拨标记默认键/节点名。
     */
    public static final String MARK_REDIS_KEY = "license:time:mark";

    public static final String MARK_REGISTRY_NODE = "org/ssssssss/magicboot/license";

    public static final String MARK_FILE_DIR = ".license";

    public static final String MARK_FILE_NAME = "mark.dat";

    public static final String INSTALL_ID_FILE_NAME = "install.id";

    public static final String CURRENT_LICENSE_FILE_NAME = "current.lic";

    /**
     * 时钟回拨判定容差（毫秒）：仅影响是否告警，不构成作弊空间——effectiveMax 只增不减。
     */
    public static final long ROLLBACK_TOLERANCE_MILLIS = 5 * 60 * 1000L;

    /**
     * 到期预警阈值（毫秒）：剩余不足 30 天进入黄色预警。
     */
    public static final long WARNING_THRESHOLD_MILLIS = 30L * 24 * 60 * 60 * 1000L;
}
