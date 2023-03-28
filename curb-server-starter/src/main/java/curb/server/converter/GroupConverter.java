package curb.server.converter;

import curb.core.model.Group;
import curb.server.po.GroupPO;

import java.net.URI;

public enum GroupConverter {
    ;

    public static Group fromPO(GroupPO po) {
        Group ret = new Group();
        ret.setGroupId(po.getGroupId());
        ret.setName(po.getName());
        ret.setUrl(URI.create(po.getUrl()));
        return ret;
    }
}
