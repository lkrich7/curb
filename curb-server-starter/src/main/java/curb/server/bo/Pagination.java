package curb.server.bo;

import curb.server.vo.PaginationVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 分页查询数据容器
 *
 * @param <T>
 */
public class Pagination<T> {

    /**
     * 分页大小
     */
    private int ps;

    /**
     * 当前页号
     */
    private int pn;

    /**
     * 数据总条数
     */
    private int total;

    /**
     * 当前分页数据
     */
    private List<T> items;

    public Pagination(int pn, int ps) {
        this(pn, ps, 0);
    }

    public Pagination(int pn, int ps, int total) {
        this(pn, ps, total, Collections.emptyList());
    }

    public Pagination(int pn, int ps, int total, List<T> items) {
        if (pn < 1) {
            throw new IllegalArgumentException("\"no\" should be greater than 0");
        }
        if (ps < 1) {
            throw new IllegalArgumentException("\"size\" should be greater than 0");
        }
        this.pn = pn;
        this.ps = ps;
        this.total = total;
        this.items = items;
    }

    public int getPn() {
        return pn;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * 计算总页数
     *
     * @return 总页数
     */
    public int pages() {
        if (total <= 0) {
            return 0;
        } else {
            return total / ps + (total % ps == 0 ? 0 : 1);
        }
    }

    /**
     * 计算当前分页的数据偏移量
     *
     * @return 当前分页的数据偏移量
     */
    public int offset() {
        return (pn - 1) * ps;
    }

    public <V> PaginationVO<V> toVO(Function<T, V> function) {
        PaginationVO<V> ret = new PaginationVO<>();
        fillVO(function, ret);
        return ret;
    }

    public <V, P extends PaginationVO<V>> void fillVO(Function<T, V> function, P ret) {
        List<V> items = new ArrayList<>(this.items.size());
        for (T row : this.items) {
            items.add(function.apply(row));
        }
        ret.setItems(items);
        ret.setTotal(total);
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "size=" + ps +
                ", no=" + pn +
                ", total=" + total +
                ", rows=" + items +
                '}';
    }

}
