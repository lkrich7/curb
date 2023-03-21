package curb.server.service;

import curb.server.dao.AppSecretDAO;
import curb.server.enums.AppState;
import curb.core.ErrorEnum;
import curb.server.dao.AppDAO;
import curb.server.po.AppPO;
import curb.core.model.App;
import curb.core.model.Group;
import curb.server.util.CurbServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

/**
 */
@Service
public class AppService {

    @Autowired
    private AppDAO appDAO;

    @Autowired
    private AppSecretDAO appSecretDAO;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PageService pageService;

    public AppPO checkApp(Integer appId, App app, Group group) {
        if (appId == null || app.getAppId().equals(appId)) {
            // 缺省或是当前APP下
            appId = app.getAppId();
        } else if (!CurbServerUtil.isGroupApp(group, app)) {
            // 在非项目组主App下，不能访问其他App
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        return checkApp(appId, group.getGroupId());
    }

    public AppPO checkApp(Integer appId, Integer groupId) {
        AppPO app = get(appId);
        if (app == null) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("应用不存在");
        }
        if (!app.getGroupId().equals(groupId)) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("应用不存在");
        }
        return app;
    }

    public AppPO get(Integer id) {
        return appDAO.get(id);
    }

    public AppPO getByUrl(String url) {
        return appDAO.getByUrl(url);
    }

    public String getSecret(int appId) {
        return appSecretDAO.get(appId);
    }

    public List<AppPO> listByGroupId(int groupId, AppState state) {
        return appDAO.listByGroupId(groupId, state == null ? null : state.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(AppPO app) {
        checkUrlConflict(app.getAppId(), app.getUrl());
        app.setState(AppState.ENABLED.getCode());
        int row = appDAO.insert(app);
        if (row != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
        Integer appId = app.getAppId();
        String secret = CurbServerUtil.generateSecret();
        appSecretDAO.insert(appId, secret);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(AppPO app) {
        AppPO existed = checkApp(app.getAppId(), app.getGroupId());
        checkUrlConflict(app.getAppId(), app.getUrl());

        existed.setUrl(app.getUrl());
        existed.setName(app.getName());

        int row = appDAO.update(existed);
        if (row != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateState(int appId, int groupId, AppState state) {
        checkApp(appId, groupId);
        int rows = appDAO.updateState(appId, state.getCode());
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(int appId, int groupId) {
        AppPO appPO = checkApp(appId, groupId);
        if (!AppState.DISABLED.codeEquals(appPO.getState())) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("当前应用不可删除，如想删除，请先禁用应用");
        }
        appDAO.delete(appId);
        permissionService.deletByAppId(appId);
        pageService.deleteByAppId(appId);
    }

    private void checkUrlConflict(Integer appId, String url) {
        AppPO app = appDAO.getByUrl(url);
        if (app == null || app.getAppId().equals(appId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("网址已存在");
    }
}
