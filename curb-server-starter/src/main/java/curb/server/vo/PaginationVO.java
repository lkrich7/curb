package curb.server.vo;

import java.util.Collection;

public class PaginationVO<T> {

    private Collection<T> rows;
    private int total;

    public PaginationVO() {
    }

    public PaginationVO(Collection<T> rows, int total) {
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

    public void setTotal(int total) {
        this.total = total;
    }

}
