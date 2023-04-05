package curb.server.util;

import curb.core.ErrorEnum;
import curb.core.model.App;
import curb.core.model.Group;

import java.util.UUID;

/**
 *
 */
public enum CurbServerUtil {
    ;

    public static final int CURB_APP_ID = 1;
    public static final int CURB_GROUP_ID = 1;

    /**
     * 判断当前应用是否是项目组主应用
     *
     * @param group
     * @param app
     * @return
     */
    public static boolean isGroupApp(Group group, App app) {
        return isSystemAppId(app.getAppId()) || group.getUrl().equals(app.getUrl());
    }

    /**
     * 判断是否为系统应用ID
     *
     * @param appId
     * @return
     */
    public static boolean isSystemAppId(int appId) {
        return CURB_APP_ID == appId;
    }

    /**
     * 判断是否为系统项目组ID
     *
     * @param groupId
     * @return
     */
    public static boolean isSystemGroupId(int groupId) {
        return CURB_GROUP_ID == groupId;
    }

    public static Integer checkGroupId(Integer groupId, Group group) {
        if (groupId == null) {
            groupId = group.getGroupId();
        } else {
            if (!isSystemGroupId(group.getGroupId()) && !groupId.equals(group.getGroupId())) {
                ErrorEnum.NOT_FOUND.toCurbException();
            }
        }
        return groupId;
    }

    public static String generateSecret() {
        return UUID.randomUUID().toString();
    }

    /**
     * 判断一个域名是否和另一个域名相同或是另一个域名的子域名
     *
     * @param subDomain
     * @param domain
     * @return
     */
    private static boolean isEqualOrSub(String subDomain, String domain) {
        if (!subDomain.endsWith(domain)) {
            return false;
        }
        int i = subDomain.length() - domain.length();
        if (i < 0) {
            return false;
        }
        if (i == 0) {
            return true;
        }
        return subDomain.charAt(i - 1) == '.';
    }

}
