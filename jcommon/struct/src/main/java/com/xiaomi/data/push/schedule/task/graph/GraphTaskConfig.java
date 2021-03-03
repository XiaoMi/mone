/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
