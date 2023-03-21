package curb.server.vo;

import java.util.Collection;

/**
 * 分页VO
 *
 * @param <T>
 */
public class PaginationVO<T> {

    /**
     * 当前页数据
     */
    private Collection<T> rows;

    /**
     * 数据总条数
     */
    private Integer total;

    public PaginationVO() {
    }

    public PaginationVO(Collection<T> rows, Integer total) {
        this.rows = rows;
        this.total = total;
    }

    public Collection<T> getRows() {
        return rows;
    }

    public void setRows(Collection<T> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
