package curb.server.vo;

import java.util.Collection;

public class PageListVO extends PaginationVO<PageListItemVO> {

    private AppVO app;

    public PageListVO(AppVO app) {
        this.app = app;
    }

    public PageListVO(Collection<PageListItemVO> rows, int total, AppVO app) {
        super(rows, total);
        this.app = app;
    }

    public AppVO getApp() {
        return app;
    }

    public void setApp(AppVO app) {
        this.app = app;
    }
}
