package curb.server.vo;

import java.util.Collection;

/**
 * 选项列表View Object
 */
public class OptionSelectVO {

    /**
     * 选项列表
     */
    private Collection<OptionVO> options;

    /**
     * 选中的值, 多个值用逗号分隔
     */
    private String value;

    public Collection<OptionVO> getOptions() {
        return options;
    }

    public void setOptions(Collection<OptionVO> options) {
        this.options = options;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
