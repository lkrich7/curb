package curb.server.vo;

import curb.server.po.PermissionPO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionVO implements Serializable {
    private Integer permId;
    private String sign;
    private String name;
    private Integer state;

    public Integer getPermId() {
        return permId;
    }

    public void setPermId(Integer permId) {
        this.permId = permId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public static PermissionVO fromPO(PermissionPO po) {
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

    public static List<PermissionVO> fromPO(List<PermissionPO> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        ArrayList<PermissionVO> ret = new ArrayList<>();
        for (PermissionPO po : list) {
            ret.add(fromPO(po));
        }
        return ret;
    }
}
