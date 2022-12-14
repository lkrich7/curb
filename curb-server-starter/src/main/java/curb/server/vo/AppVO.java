package curb.server.vo;

import curb.server.po.AppPO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppVO {
    private Integer appId;
    private String domain;
    private String name;
    private String description;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public static AppVO fromPO(AppPO po) {
        if (po == null) {
            return null;
        }
        AppVO ret = new AppVO();
        ret.setAppId(po.getAppId());
        ret.setName(po.getName());
        ret.setDomain(po.getDomain());
        ret.setDescription(po.getDescription());
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
}
