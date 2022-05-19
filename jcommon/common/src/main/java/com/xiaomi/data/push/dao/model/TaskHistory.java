package com.xiaomi.data.push.dao.model;

/**
 * 　　* @description: TODO
 * 　　* @author zhenghao
 *
 */
public class TaskHistory {

    private Integer id;

    private long taskId;

    private String taskContent;

    private long ctime;

    private Integer status;

    private String uid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskHistory{");
        sb.append("id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", taskContent='").append(taskContent).append('\'');
        sb.append(", ctime=").append(ctime);
        sb.append(", status=").append(status);
        sb.append(", uid='").append(uid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
