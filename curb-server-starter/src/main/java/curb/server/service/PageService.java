package curb.server.service;

import curb.core.ErrorEnum;
import curb.core.util.JsonUtil;
import curb.server.dao.PageBodyDAO;
import curb.server.dao.PageDAO;
import curb.server.bo.Pagination;
import curb.server.enums.PageState;
import curb.server.enums.PageType;
import curb.server.page.CurbPage;
import curb.server.po.PageBodyPO;
import curb.server.po.PagePO;
import curb.server.util.CoreDataUtil;
import curb.server.vo.PageBodyEditVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    /**
     * 系统内置页面
     */
    private final Map<String, CurbPage> innerPages;

    private PageDAO pageDAO;

    @Autowired
    private PageBodyDAO pageBodyDAO;

    @Autowired
    public PageService(PageDAO pageDAO, PageBodyDAO pageBodyDAO) {
        this.pageDAO = pageDAO;
        this.pageBodyDAO = pageBodyDAO;
        List<CurbPage> pages = CoreDataUtil.loadPages("curb/core-data/pages.json");
        innerPages = new LinkedHashMap<>();
        for (CurbPage page : pages) {
            innerPages.put(page.getPath(), page);
        }
    }

    public CurbPage getPage(int appId, String url) {
        return getPage(appId, url, null);
    }
    public CurbPage getPage(int appId, String url, Integer version) {
        CurbPage page = CurbPage.buildWrapPage(url);
        if (page != null) {
            CurbPage p = findPage(appId, page.getPath(), version);
            return p == null ? page : p;
        }

        String path = CurbPage.stripViewPagePath(url);
        if (path != null) {
            return findPage(appId, path, version);
        }

        path = CurbPage.parsePath(url);
        if (path == null) {
            return null;
        }
        page = findPage(appId, path, version);
        if (page == null && CurbPage.isIndex(path)) {
            page = CurbPage.INDEX;
        }
        return page;
    }

    public List<PagePO> listEditables(int appId) {
        return pageDAO.listByAppId(appId);
    }

    public PagePO checkEditablePage(int pageId, int appId) {
        PagePO po = pageDAO.get(pageId);
        if (po == null || !po.getAppId().equals(appId)) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        return po;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(PagePO po) {
        checkUriConflict(po.getAppId(), po.getPath(), null);
        po.setState(PageState.ENABLED.getCode());
        pageDAO.insert(po);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(PagePO po) {
        int pageId = po.getPageId();
        int appId = po.getAppId();
        String path = po.getPath();
        PagePO existed = checkEditablePage(pageId, appId);
        checkUriConflict(appId, path, pageId);

        existed.setPath(path);
        existed.setName(po.getName());
        existed.setType(po.getType());
        existed.setAccessLevel(po.getAccessLevel());
        existed.setSign(po.getSign());
        pageDAO.update(existed);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateState(int pageId, int appId, PageState state) {
        checkEditablePage(pageId, appId);

        int rows = pageDAO.updateState(pageId, state.getCode());
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(int pageId, int appId) {
        checkEditablePage(pageId, appId);
        pageDAO.delete(pageId);
        pageBodyDAO.deleteByPageId(pageId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByAppId(int appId) {
        List<PagePO> list = pageDAO.listByAppId(appId);
        pageDAO.deleteByAppId(appId);
        for (PagePO po : list) {
            pageBodyDAO.deleteByPageId(po.getPageId());
        }
    }
    public PageBodyPO getBody(int pageId, int version) {
        return pageBodyDAO.get(pageId, version);
    }

    public Pagination<PageBodyPO> pagedListBodyHistory(int pageId, Integer pn, Integer ps) {
        int count = pageBodyDAO.countByPageId(pageId);
        Pagination<PageBodyPO> ret = new Pagination<>(pn, ps, count);
        List<PageBodyPO> rows = pageBodyDAO.listByPageId(pageId, ps, ret.offset());
        ret.setRows(rows);
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBody(PageBodyEditVO vo, Integer appId, Integer userId) {
        PagePO po = checkEditablePage(vo.getPageId(), appId);
        boolean unmodified = checkBody(po, vo.getBody());
        if (unmodified) {
            return;
        }
        int version = po.getVersion();
        int newVersion = version + 1;
        int row = pageDAO.updateVersion(po.getPageId(), newVersion, version);
        if (row != 1) {
            throw ErrorEnum.PARAM_ERROR.toCurbException("数据已发生变化，请重试");
        }
        PageBodyPO bodyPO = new PageBodyPO();
        bodyPO.setPageId(vo.getPageId());
        bodyPO.setVersion(newVersion);
        bodyPO.setBody(vo.getBody());
        bodyPO.setUserId(userId);
        pageBodyDAO.insert(bodyPO);
    }

    /**
     * 检查要保存的页面内容格式，并与当前版本内容对比，如果没有变化返回true
     * @param po
     * @param body
     * @return
     */
    private boolean checkBody(PagePO po, String body) {
        if (PageType.AMIS.codeEquals(po.getType())) {
            try {
                JsonUtil.parseObject(body);
            } catch (Exception e) {
                throw ErrorEnum.PARAM_ERROR.toCurbException("页面内容格式错误: 应为JSON格式", e);
            }
        }
        PageBodyPO bodyPO = pageBodyDAO.get(po.getPageId(), po.getVersion());
        if(bodyPO == null) {
            return false;
        }
        return body.equals(bodyPO.getBody());
    }

    private CurbPage findPage(int appId, String path, Integer version) {
        CurbPage page = innerPages.get(path);
        if (page != null) {
            return page;
        }
        PagePO po = pageDAO.getByAppIdPath(appId, path);
        if (po == null || PageState.DISABLED.codeEquals(po.getState())) {
            return null;
        }
        if (version == null) {
            version = po.getVersion();
        }
        PageBodyPO bodyPO = pageBodyDAO.get(po.getPageId(), version);
        page = CurbPage.fromPO(po, bodyPO);
        return page;
    }

    private void checkUriConflict(int appId, String path, Integer pageId) {
        PagePO existed = pageDAO.getByAppIdPath(appId, path);
        if (existed == null || existed.getPageId().equals(pageId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("页面地址路径已存在");
    }

}
