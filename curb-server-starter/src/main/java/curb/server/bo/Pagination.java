package curb.server.bo;

import java.util.Collections;
import java.util.List;

/**
 * 分页查询数据容器
 *
 * @param <T>
 */
public class Pagination<T> {

    /**
     * 分页大小
     */
    private int size;

    /**
     * 当前页号
     */
    private int no;

    /**
     * 数据总条数
     */
    private int total;

    /**
     * 当前分页数据
     */
    private List<T> rows;

    public Pagination(int no, int size) {
        this(no, size, 0);
    }

    public Pagination(int no, int size, int total) {
        this(no, size, total, Collections.emptyList());
    }

    public Pagination(int no, int size, int total, List<T> rows) {
        if (no < 1) {
            throw new IllegalArgumentException("\"no\" should be greater than 0");
        }
        if (size < 1) {
            throw new IllegalArgumentException("\"size\" should be greater than 0");
        }
        this.no = no;
        this.size = size;
        this.total = total;
        this.rows = rows;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
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
            return total / size + (total % size == 0 ? 0 : 1);
        }
    }

    /**
     * 计算当前分页的数据偏移量
     *
     * @return 当前分页的数据偏移量
     */
    public int offset() {
        return (no - 1) * size;
    }

    @Override
    public String toString() {
        return "Pagination{" +
                "size=" + size +
                ", no=" + no +
                ", total=" + total +
                ", rows=" + rows +
                '}';
    }
}
