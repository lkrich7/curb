package curb.server.vo;

import curb.server.vo.OptionVO;

import java.util.Collection;

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
