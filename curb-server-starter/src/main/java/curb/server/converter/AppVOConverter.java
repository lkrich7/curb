package curb.server.converter;

import curb.server.po.AppPO;
import curb.server.vo.AppVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum AppVOConverter {
    ;

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

}
