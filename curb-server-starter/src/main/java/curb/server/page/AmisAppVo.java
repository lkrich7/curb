package curb.server.page;

import curb.server.po.AppPO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AmisAppVo implements Serializable {

    private String type = "button";

    private String actionType = "url";

    private String label;

    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static List<AmisAppVo> fromAppPoList(List<AppPO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<AmisAppVo> ret = new ArrayList<>(list.size());
        for (AppPO po : list) {
            AmisAppVo vo = new AmisAppVo();
            vo.label = po.getName();
            vo.url = po.getUrl();
            ret.add(vo);
        }

        return ret;
    }

}
