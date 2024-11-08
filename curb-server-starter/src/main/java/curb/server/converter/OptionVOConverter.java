package curb.server.converter;

import curb.core.util.StringUtil;
import curb.server.po.RolePO;
import curb.server.vo.OptionSelectVO;
import curb.server.vo.OptionVO;

import java.util.Collection;
import java.util.List;

/**
 * OptionVO转换器
 */
public enum OptionVOConverter {
    ;

    public static OptionSelectVO convert(Collection<RolePO> roles, Collection<Integer> roleIds) {
        Collection<OptionVO> options = convert(roles);
        String value = StringUtil.join(roleIds, ",", true);

        OptionSelectVO ret = new OptionSelectVO();
        ret.setOptions(options);
        ret.setValue(value);
        return ret;
    }

    public static OptionVO convert(RolePO po) {
        String label = String.format("%s(%s)", po.getName(), po.getSign());
        String value = String.valueOf(po.getRoleId());
        return new OptionVO(label, value);
    }

    public static List<OptionVO> convert(Collection<RolePO> poList) {
        return ListConverter.convert(poList, OptionVOConverter::convert);
    }
}
