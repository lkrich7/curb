package curb.server.converter;

import curb.server.po.PermissionPO;
import curb.server.vo.PermissionVO;

import java.util.List;

/**
 * PermissionVO转换器
 */
public enum PermissionVOConverter {
    ;

    public static PermissionVO convert(PermissionPO po) {
        if (po == null) {
            return null;
        }
        PermissionVO ret = new PermissionVO();
        ret.setPermId(po.getPermId());
        ret.setSign(po.getSign());
        ret.setName(po.getName());
        ret.setState(po.getState());
        return ret;
    }

    public static List<PermissionVO> convert(List<PermissionPO> list) {
        return ListConverter.convert(list, PermissionVOConverter::convert);
    }

}
