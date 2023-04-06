package curb.server.converter;

import curb.server.po.GroupPO;
import curb.server.vo.GroupVO;

import java.util.List;

/**
 * GroupVO转换器
 */
public enum GroupVOConverter {
    ;

    public static GroupVO convert(GroupPO po) {
        if (po == null) {
            return null;
        }
        GroupVO ret = new GroupVO();
        ret.setGroupId(po.getGroupId());
        ret.setName(po.getName());
        ret.setUrl(po.getUrl());
        return ret;
    }

    public static List<GroupVO> convert(List<GroupPO> list) {
        return ListConverter.convert(list, GroupVOConverter::convert);
    }
}
