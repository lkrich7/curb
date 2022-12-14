package curb.server.util;

import curb.core.model.Group;
import curb.server.po.GroupPO;

public final class GroupUtil {
    private GroupUtil() {
    }

    public static Group fromPO(GroupPO po) {
        Group ret = new Group();
        ret.setGroupId(po.getGroupId());
        ret.setName(po.getName());
        ret.setDomain(po.getDomain());
        return ret;
    }
}
