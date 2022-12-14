package curb.core.model;

import java.io.Serializable;

/**
 * 项目组信息
 */
public class Group implements Serializable {

    /**
     * 项目组ID
     */
    private int groupId;

    /**
     * 项目组名称
     */
    private String name;

    /**
     * 项目组根域名
     */
    private String domain;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
