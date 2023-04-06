package curb.server.converter;

import curb.server.po.AppPO;
import curb.server.vo.AppVO;

import java.util.List;

/**
 * AppVO转换器
 */
public enum AppVOConverter {
    ;

    public static AppVO convert(AppPO po) {
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

    public static List<AppVO> convert(List<AppPO> list) {
        return ListConverter.convert(list, AppVOConverter::convert);
    }

}
