package com.xiaomi.data.push.schedule.task.graph;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by zhangzhiyong on 08/06/2018.
 */
public class GraphTaskConfig {


    private List<TaskVertexData> taskList= Lists.newArrayList();

    private List<TaskEdgeData> dependList = Lists.newArrayList();


    public List<TaskVertexData> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskVertexData> taskList) {
        this.taskList = taskList;
    }

    public List<TaskEdgeData> getDependList() {
        return dependList;
    }

    public void setDependList(List<TaskEdgeData> dependList) {
        this.dependList = dependList;
    }


    public void toConfig(String path) {
        String config = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        try {
            Files.write(Paths.get(path), config.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
