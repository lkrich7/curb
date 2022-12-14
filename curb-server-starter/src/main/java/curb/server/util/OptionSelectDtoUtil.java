package curb.server.util;

import com.google.common.base.Joiner;
import curb.server.vo.OptionVO;
import curb.server.vo.OptionSelectVO;
import curb.server.po.RolePO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OptionSelectDtoUtil {

    private OptionSelectDtoUtil() {
    }

    public static OptionSelectVO fromRolePO(Collection<RolePO> roles, Collection<Integer> roleIds) {
        Collection<OptionVO> options = toOptions(roles);
        String value = Joiner.on(',').skipNulls().join(roleIds);

        OptionSelectVO ret = new OptionSelectVO();
        ret.setOptions(options);
        ret.setValue(value);
        return ret;
    }

    private static Collection<OptionVO> toOptions(Collection<RolePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        List<OptionVO> ret = new ArrayList<>(poList.size());
        for (RolePO po : poList) {
            String label = String.format("%s(%s)", po.getName(), po.getSign());
            String value = String.valueOf(po.getRoleId());
            ret.add(new OptionVO(label, value));
        }
        return ret;
    }
}
