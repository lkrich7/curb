package curb.server.vo;

import java.io.Serializable;

public class AppVO implements Serializable {

    /**
     * 应用ID
     */
    private Integer appId;
    /**
     * 应用名称
     */
    private String name;
    /**
     * 应用网址
     */
    private String url;
    /**
     * 应用状态
     *
     * @see curb.server.enums.AppState
     */
    private Integer state;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AppVO{" +
                "appId=" + appId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", state=" + state +
                '}';
    }
}
