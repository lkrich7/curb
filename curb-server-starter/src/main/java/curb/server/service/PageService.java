package curb.server.service;

import curb.core.AccessLevel;
import curb.core.ErrorEnum;
import curb.core.util.JsonUtil;
import curb.server.bo.Pagination;
import curb.server.dao.PageBodyDAO;
import curb.server.dao.PageDAO;
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

/**
 * 页面管理服务
 */
@Service
public class PageService {

    /**
     * 系统内置页面
     */
    private final Map<String, CurbPage> innerPages;

    private PageDAO pageDAO;

    private PageBodyDAO pageBodyDAO;

    @Autowired
    public PageService(PageDAO pageDAO, PageBodyDAO pageBodyDAO) {
        this.pageDAO = pageDAO;
        this.pageBodyDAO = pageBodyDAO;
        List<CurbPage> pages = CoreDataUtil.loadPages("curb/core-data/pages.json");
        innerPages = new LinkedHashMap<>();
        for (CurbPage page : pages) {
            if (page.getLevel() == null) {
                page.setLevel(AccessLevel.PERMISSION);
            }
            innerPages.put(page.getPath(), page);
        }
    }

    /**
     * 取指定页面最新版本数据
     *
     * @param appId 页面所属应用ID
     * @param url   页面地址路径
     * @return
     */
    public CurbPage getPage(int appId, String url) {
        return getPage(appId, url, null);
    }

    /**
     * 获取指定页面的指定版本数据
     *
     * @param appId   页面所属应用ID
     * @param url     页面地址路径
     * @param version 指定页面版本号
     * @return
     */
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

    public PagePO checkPage(int pageId, int appId) {
        PagePO po = pageDAO.get(pageId);
        if (po == null || !po.getAppId().equals(appId)) {
            throw ErrorEnum.NOT_FOUND.toCurbException();
        }
        return po;
    }

    public Pagination<PagePO> paginationListPage(int appId, int pn, int ps) {
        int count = pageDAO.countByCondition(appId);
        if (count == 0) {
            return new Pagination<>(pn, ps);
        }
        Pagination<PagePO> ret = new Pagination<>(pn, ps, count);
        List<PagePO> items = pageDAO.listByCondition(appId, ps, ret.offset());
        ret.setItems(items);
        return ret;
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
        PagePO existed = checkPage(pageId, appId);
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
        checkPage(pageId, appId);

        int rows = pageDAO.updateState(pageId, state.getCode());
        if (rows != 1) {
            throw ErrorEnum.SERVER_ERROR.toCurbException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(int pageId, int appId) {
        checkPage(pageId, appId);
        pageDAO.delete(pageId);
        pageBodyDAO.deleteByPageId(pageId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByAppId(int appId) {
        pageDAO.deleteByAppId(appId);
        List<PagePO> list = pageDAO.listByCondition(appId, Integer.MAX_VALUE, 0);
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
        ret.setItems(rows);
        return ret;
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveBody(PageBodyEditVO vo, Integer appId, Integer userId) {
        PagePO po = checkPage(vo.getPageId(), appId);
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

    @Transactional(rollbackFor = Exception.class)
    public void rollback(int pageId, int version) {
    }

    /**
     * 检查要保存的页面内容格式，并与当前版本内容对比，如果没有变化返回true
     *
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
        if (bodyPO == null) {
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
        page = toCurbPage(po, bodyPO);
        return page;
    }

    private void checkUriConflict(int appId, String path, Integer pageId) {
        PagePO existed = pageDAO.getByAppIdPath(appId, path);
        if (existed == null || existed.getPageId().equals(pageId)) {
            return;
        }
        throw ErrorEnum.PARAM_ERROR.toCurbException("页面地址路径已存在");
    }

    public static CurbPage toCurbPage(PagePO po, PageBodyPO bodyPO) {
        if (po == null) {
            return null;
        }
        PageType type = PageType.valueOfCode(po.getType(), PageType.AMIS);
        AccessLevel level = AccessLevel.valueOfCode(po.getAccessLevel(), AccessLevel.PERMISSION);
        CurbPage ret = new CurbPage();
        ret.setPath(po.getPath());
        ret.setName(po.getName());
        ret.setType(type);
        ret.setLevel(level);
        ret.setSign(po.getSign());
        ret.setVersion(po.getVersion());
        if (bodyPO != null) {
            ret.setBody(bodyPO.getBody());
        }
        return ret;
    }

}
