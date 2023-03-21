package curb.core.model;

import java.io.Serializable;
import java.net.URI;

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
     * 项目组网址
     */
    private URI url;

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

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
