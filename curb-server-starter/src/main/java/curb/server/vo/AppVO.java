package curb.server.vo;

import curb.server.po.AppPO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static AppVO fromPO(AppPO po) {
        if (po == null) {
            return null;
        }
        AppVO ret = new AppVO();
        ret.setAppId(po.getAppId());
        ret.setName(po.getName());
        ret.setUrl(po.getUrl());
        ret.setState(po.getState());
        return ret;
    }

    public static List<AppVO> fromPO(List<AppPO> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        ArrayList<AppVO> ret = new ArrayList<>();
        for (AppPO po : list) {
            ret.add(fromPO(po));
        }
        return ret;
    }

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
