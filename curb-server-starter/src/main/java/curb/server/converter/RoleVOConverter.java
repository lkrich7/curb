package curb.server.converter;

import curb.server.po.RolePO;
import curb.server.vo.RoleVO;

import java.util.Collection;
import java.util.List;

/**
 * RoleVO转换器
 */
public enum RoleVOConverter {
    ;

    public static RoleVO convert(RolePO po) {
        if (po == null) {
            return null;
        }
        RoleVO ret = new RoleVO();
        ret.setRoleId(po.getRoleId());
        ret.setSign(po.getSign());
        ret.setName(po.getName());
        ret.setDescription(po.getDescription());
        ret.setState(po.getState());
        return ret;
    }

    public static List<RoleVO> convert(Collection<RolePO> list) {
        return ListConverter.convert(list, RoleVOConverter::convert);
    }
}
