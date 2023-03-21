package curb.server.vo;

import curb.server.po.GroupPO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupVO {
    private Integer groupId;
    private String name;
    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static GroupVO fromPO(GroupPO po) {
        if (po == null) {
            return null;
        }
        GroupVO ret = new GroupVO();
        ret.setGroupId(po.getGroupId());
        ret.setName(po.getName());
        ret.setUrl(po.getUrl());
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
