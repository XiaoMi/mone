package com.xiaomi.data.push.schedule.task.graph;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by zhangzhiyong on 08/06/2018.
 */
public class GraphTaskContext<D> {

    private List<TaskVertexData<D>> taskList = Lists.newArrayList();

    private List<TaskEdgeData> dependList = Lists.newArrayList();


    public List<TaskVertexData<D>> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskVertexData<D>> taskList) {
        this.taskList = taskList;
    }

    public List<TaskEdgeData> getDependList() {
        return dependList;
    }

    public void setDependList(List<TaskEdgeData> dependList) {
        this.dependList = dependList;
    }
}
