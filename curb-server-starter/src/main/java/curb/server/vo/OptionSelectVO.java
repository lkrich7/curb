package curb.server.vo;

import java.util.Collection;

/**
 * 选项列表
 */
public class OptionSelectVO {
    private Collection<OptionVO> options;
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
