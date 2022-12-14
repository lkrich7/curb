package curb.core.model;

import java.io.Serializable;

/**
 * 用户权限
 */
public class UserPermission implements Serializable {
    /**
     * 权限标识
     */
    private String sign;

    /**
     * 用户是否有权限
     */
    private boolean userChecked;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean isUserChecked() {
        return userChecked;
    }

    public void setUserChecked(boolean userChecked) {
        this.userChecked = userChecked;
    }

    @Override
    public String toString() {
        return "UserPermission{" + "sign='" + sign + '\'' + ", userChecked=" + userChecked + '}';
    }
}
