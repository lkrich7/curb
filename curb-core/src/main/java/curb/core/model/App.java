package curb.core.model;

import java.io.Serializable;
import java.net.URI;

/**
 * 应用基本信息
 */
public class App implements Serializable {

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 所属项目组
     */
    private Integer groupId;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用网址
     */
    private URI url;

    public String parsePath(String path) {
        if (path == null) {
            return null;
        }
        if (path.startsWith(this.url.getPath())) {
            return path.substring(this.url.getPath().length());
        } else {
            return null;
        }
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "App{" +
                "appId=" + appId +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", url=" + url +
                '}';
    }
}
