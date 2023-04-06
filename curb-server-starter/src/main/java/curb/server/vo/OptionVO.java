package curb.server.vo;

/**
 * 选项列表条目
 */
public class OptionVO {

    /**
     * 选项显示文本
     */
    private String label;
    /**
     * 选项值
     */
    private String value;

    public OptionVO() {
    }

    public OptionVO(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
