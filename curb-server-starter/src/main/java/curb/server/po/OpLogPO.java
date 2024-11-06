package curb.server.po;

import java.util.Date;
import java.util.StringJoiner;

/**
 * 操作日志
 */
public class OpLogPO {

    /**
     * 操作日志ID(主键)
     */
    private Long logId;

    /**
     * 操作发生时间
     */
    private Date eventTime;

    /**
     * 操作者用户名
     */
    private String username;

    /**
     * 操作者IP
     */
    private String ip;

    /**
     * 项目组ID
     */
    private Integer groupId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 操作的HTTP方法
     */
    private String method;

    /**
     * 操作的URL
     */
    private String url;

    /**
     * 操作耗时(单位毫秒)
     */
    private Integer cost;

    /**
     * 操作执行状态
     * @see curb.core.AccessState
     */
    private Integer state;

    /**
     * 执行结果
     */
    private String result;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OpLogPO.class.getSimpleName() + "[", "]")
                .add("logId=" + logId)
                .add("eventTime=" + eventTime)
                .add("username='" + username + "'")
                .add("ip='" + ip + "'")
                .add("groupId=" + groupId)
                .add("appId=" + appId)
                .add("method='" + method + "'")
                .add("url='" + url + "'")
                .add("cost=" + cost)
                .add("state=" + state)
                .add("result='" + result + "'")
                .toString();
    }
}