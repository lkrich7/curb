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
    private Collection<T> items;

    /**
     * 数据总条数
     */
    private Integer total;

    public PaginationVO() {
    }

    public PaginationVO(Collection<T> items, Integer total) {
        this.items = items;
        this.total = total;
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
