package curb.server.page;

import curb.core.model.Menu;
import curb.core.util.UrlCodec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 主页菜单数据
 */
class AmisMenuVo implements Serializable {
    private String label;
    private String url;
    private String icon;
    private List<AmisMenuVo> children;
    private Boolean visible;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<AmisMenuVo> getChildren() {
        return children;
    }

    public void setChildren(List<AmisMenuVo> children) {
        this.children = children;
    }

    public String getSchemaApi() {
        if (url == null || url.isEmpty()) {
            return null;
        }
        return String.format("get:/system/api/page/schema/get?url=%s", UrlCodec.encodeUtf8(url));
    }

    public void setSchemaApi(String schemaApi) {
        // do onthing
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public static AmisMenuVo fromMenu(Menu menu) {
        if (menu == null) {
            return null;
        }
        AmisMenuVo ret = new AmisMenuVo();
        ret.setLabel(menu.getName());

        String url = menu.getUrl();
        if (url != null) {
            if (menu.getUrlType() == 1) {
                ret.setUrl(url);
            } else {
                ret.setUrl(CurbPage.asWrapPage(url));
            }
        }
        ret.setChildren(fromMenuList(menu.getChildren()));

        String icon = menu.getIcon();
        if (icon == null || icon.isEmpty()) {
            if (menu.getChildren() == null || menu.getChildren().isEmpty()) {
                icon = "fa fa-circle-o";
            } else {
                icon = "fa fa-th";
            }
        }
        ret.setIcon(icon);
        return ret;
    }

    public static List<AmisMenuVo> fromMenuList(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        List<AmisMenuVo> ret = new ArrayList<>(menus.size());
        for (Menu menu : menus) {
            AmisMenuVo vo = fromMenu(menu);
            ret.add(vo);
        }
        return ret;
    }

}
