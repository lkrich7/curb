package curb.server.dto;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询数据容器
 *
 * @param <T>
 */
public class Paged<T> {

    private int pageSize;
    private int pageNo;
    private int totalCount;
    private List<T> pageList;

    public Paged() {
    }

    public Paged(int pageNo) {
        this(pageNo, 0);
    }

    public Paged(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0);
    }

    public Paged(int pageNo, int pageSize, int totalCount) {
        this(pageNo, pageSize, totalCount, Collections.emptyList());
    }

    public Paged(int pageNo, int pageSize, int totalCount, List<T> pageList) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.pageList = pageList;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getPageList() {
        return pageList;
    }

    public void setPageList(List<T> pageList) {
        this.pageList = pageList;
    }

    public int getTotalPage() {
        if (totalCount <= 0) {
            return 0;
        } else {
            return totalCount / pageSize + (totalCount % pageSize == 0 ? 0 : 1);
        }
    }

    public int offset() {
        return (pageNo - 1) * pageSize;
    }

    @Override
    public String toString() {
        return "Paged{" +
                "pageSize=" + pageSize +
                ", pageNo=" + pageNo +
                ", totalCount=" + totalCount +
                ", pageList=" + pageList +
                '}';
    }
}
