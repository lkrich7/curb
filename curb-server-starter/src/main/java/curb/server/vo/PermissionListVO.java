package curb.server.vo;

public class PermissionListVO extends PaginationVO<PermissionVO> {

    private AppVO app;

    public AppVO getApp() {
        return app;
    }

    public void setApp(AppVO app) {
        this.app = app;
    }
}
