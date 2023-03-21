package curb.server.vo;

import curb.server.po.PagePO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PageListItemVO {
    private Integer pageId;
    private String name;
    private String path;
    private Integer type;
    private Integer accessLevel;
    private Integer state;
    private Date updateTime;

    public Integer getPageId() {
        return pageId;
    }

    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public static PageListItemVO fromPO(PagePO po) {
        if (po == null) {
            return null;
        }
        PageListItemVO ret = new PageListItemVO();
        ret.setPageId(po.getPageId());
        ret.setPath(po.getPath());
        ret.setName(po.getName());
        ret.setType(po.getType());
        ret.setAccessLevel(po.getAccessLevel());
        ret.setUpdateTime(po.getUpdateTime());
        ret.setState(po.getState());
        return ret;
    }

    public static List<PageListItemVO> fromPO(List<PagePO> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        List<PageListItemVO> ret = new ArrayList<>(poList.size());
        for (PagePO po : poList) {
            PageListItemVO item = PageListItemVO.fromPO(po);
            if (item != null) {
                ret.add(item);
            }
        }
        return ret;
    }
}
