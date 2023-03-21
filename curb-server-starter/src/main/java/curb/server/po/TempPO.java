package curb.server.po;

import java.util.Set;

public class TempPO {
    Integer roleId;
    Set<Integer> permIds;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Set<Integer> getPermIds() {
        return permIds;
    }

    public void setPermIds(Set<Integer> permIds) {
        this.permIds = permIds;
    }
}
