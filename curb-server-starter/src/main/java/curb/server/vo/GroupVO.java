package curb.server.vo;

import curb.server.po.GroupPO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupVO {
    private Integer groupId;
    private String name;
    private String domain;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public static GroupVO fromPO(GroupPO po) {
        if (po == null) {
            return null;
        }
        GroupVO ret = new GroupVO();
        ret.setGroupId(po.getGroupId());
        ret.setName(po.getName());
        ret.setDomain(po.getDomain());
        return ret;
    }

    public static List<GroupVO> fromPO(List<GroupPO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<GroupVO> ret = new ArrayList<>(list.size());
        for (GroupPO po : list) {
            GroupVO item = fromPO(po);
            ret.add(item);
        }
        return ret;
    }
}
