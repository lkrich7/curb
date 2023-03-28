package curb.server.converter;

import com.google.common.base.Joiner;
import curb.server.po.RolePO;
import curb.server.vo.OptionSelectVO;
import curb.server.vo.OptionVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum OptionVOConverter {
    ;

    public static OptionSelectVO fromRolePO(Collection<RolePO> roles, Collection<Integer> roleIds) {
        Collection<OptionVO> options = fromRolePO(roles);
        String value = Joiner.on(',').skipNulls().join(roleIds);

        OptionSelectVO ret = new OptionSelectVO();
        ret.setOptions(options);
        ret.setValue(value);
        return ret;
    }

    private static Collection<OptionVO> fromRolePO(Collection<RolePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        List<OptionVO> ret = new ArrayList<>(poList.size());
        for (RolePO po : poList) {
            OptionVO optionVO = fromRolePO(po);
            ret.add(optionVO);
        }
        return ret;
    }

    private static OptionVO fromRolePO(RolePO po) {
        String label = String.format("%s(%s)", po.getName(), po.getSign());
        String value = String.valueOf(po.getRoleId());
        return new OptionVO(label, value);
    }
}
