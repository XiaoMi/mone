package com.xiaomi.data.push.schedule.task.graph;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.data.push.schedule.task.impl.TaskDef;

import java.util.List;

/**
 *
 * @author zhangzhiyong
 * @date 08/06/2018
 */
public class TaskVertexData<D> {

    private int index;
    private TaskDefBean taskDef;
    private TaskParam taskParam;
    private int taskId;

    private List<Integer> dependList = Lists.newArrayList();

    private int status;

    private Object taskResult;

    private D data;

    public TaskDefBean getTaskDef() {
        return taskDef;
    }

    public void setTaskDef(TaskDefBean taskDef) {
        this.taskDef = taskDef;
    }

    public TaskParam getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(TaskParam taskParam) {
        this.taskParam = taskParam;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public synchronized int getStatus() {
        return status;
    }

    public synchronized void setStatus(int status) {
        this.status = status;
    }

    public List<Integer> getDependList() {
        return dependList;
    }

    public void setDependList(List<Integer> dependList) {
        this.dependList = dependList;
    }

    public synchronized Object getTaskResult() {
        return taskResult;
    }

    public synchronized void setTaskResult(Object taskResult) {
        this.taskResult = taskResult;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
