package curb.server.vo;

import curb.server.po.RolePO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 角色
 */
public class RoleVO implements Serializable {
    private Integer roleId;
    private String sign;
    private String name;
    private String description;
    private Integer state;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public static RoleVO fromPO(RolePO po) {
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

    public static List<RoleVO> fromPO(List<RolePO> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        ArrayList<RoleVO> ret = new ArrayList<>();
        for (RolePO po : list) {
            ret.add(fromPO(po));
        }
        return ret;
    }
}
