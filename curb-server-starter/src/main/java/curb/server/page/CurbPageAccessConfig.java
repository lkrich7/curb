package curb.server.page;

import curb.core.AccessLevel;
import curb.core.CurbAccessConfig;
import curb.core.PermissionResolver;
import org.apache.commons.lang3.StringUtils;

class CurbPageAccessConfig implements CurbAccessConfig {

    private static final CurbPageAccessConfig INDEX_PAGE_ACCESS_CONFIG;
    private static final CurbPageAccessConfig DEFAULT_PAGE_ACCESS_CONFIG;

    private static final String[] NO_IGNORE_PARAM_NAMES = new String[0];

    static {
        INDEX_PAGE_ACCESS_CONFIG = new CurbPageAccessConfig(AccessLevel.LOGIN, null, null);
        DEFAULT_PAGE_ACCESS_CONFIG = new CurbPageAccessConfig(AccessLevel.PERMISSION, null, null);
    }

    private final AccessLevel level;

    private final String sign;
    private final String path;

    private CurbPageAccessConfig(AccessLevel level, String sign, String path) {
        this.level = level;
        this.sign = sign;
        this.path = path;
    }

    private CurbPageAccessConfig(CurbAccessConfig that, String path) {
        this.level = that.getLevel();
        this.sign = that.getSign();
        this.path = path;
    }
//
//    public static CurbPageAccessConfig parse(String configStr, String path) {
//        PageAccessConfigVO vo = PageAccessConfigVO.parse(configStr);
//        if (vo == null) {
//            if (CurbPage.isIndex(path)) {
//                return INDEX_PAGE_ACCESS_CONFIG;
//            }
//            if (path == null || path.trim().isEmpty()) {
//                return DEFAULT_PAGE_ACCESS_CONFIG;
//            }
//            return new CurbPageAccessConfig(DEFAULT_PAGE_ACCESS_CONFIG, path);
//        } else {
//            return newPageAccessConfig(vo, path);
//        }
//    }

    @Override
    public AccessLevel getLevel() {
        return level;
    }

    @Override
    public String getSign() {
        return sign;
    }

    @Override
    public String getPath() {
        return path;
    }

}
