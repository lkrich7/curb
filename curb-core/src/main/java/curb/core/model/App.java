package curb.core.model;

import java.io.Serializable;

/**
 * 应用基本信息
 */
public class App implements Serializable {

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 应用的域名
     */
    private String domain;

    /**
     * 应用首页路径
     */
    private String mainPage;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用描述
     */
    private String description;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMainPage() {
        return mainPage;
    }

    public void setMainPage(String mainPage) {
        this.mainPage = mainPage;
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

    @Override
    public String toString() {
        return "App{" +
                "appId=" + appId +
                ", domain='" + domain + '\'' +
                ", mainPage='" + mainPage + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
