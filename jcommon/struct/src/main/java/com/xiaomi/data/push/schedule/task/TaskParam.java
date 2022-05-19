package com.xiaomi.data.push.schedule.task;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangzhiyong
 * @date 29/05/2018
 * 任务参数
 */
@Data
public class TaskParam implements Serializable {

    private TaskDefBean taskDef;

    private int taskId;

    private String bizId;

    private String url;

    public Map<String, String> param = Maps.newHashMap();

    private String source;

    private long beginTime;

    /**
     * 指定具体的执行时间
     */
    private long executeTime;

    private String cron;

    /**
     * 通知类型
     */
    private String notify;

    /**
     * 设置超时时间
     */
    private long timeout;

    private String roleId;

    /**
     * 分组信息
     */
    private Integer gid;

    /**
     * 告警接收人 多个以,分隔
     */
    private String alarmUsername="";

    /**
     * 是否忽略失败 false:达到失败次数则任务暂停
     */
    private boolean ignoreError;

    private String creator;


    /**
     * 通用的参数名称
     */
    public static final String PARAM = "param";



    public TaskParam() {
    }

    public TaskDefBean getTaskDef() {
        return taskDef;
    }

    public void setTaskDef(TaskDefBean taskDef) {
        this.taskDef = taskDef;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getInt(String key) {
        if (param.containsKey(key)) {
            return Integer.valueOf(param.get(key));
        }
        return 0;
    }

    public String get(String key) {
        return param.get(key);
    }

    public void putInt(String key, long value) {
        this.param.put(key, String.valueOf(value));
    }

    public void put(String key, String value) {
        this.param.put(key, value);
    }


    public void putParams(Map<String, String> params) {
        this.param.putAll(params);
    }


    public List<String> values() {
        return new ArrayList<>(this.param.values());
    }

    @Override
    public String toString() {
        return "TaskParam{" +
                "taskDef=" + taskDef +
                ", taskId=" + taskId +
                ", alarmUsername=" + alarmUsername +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
