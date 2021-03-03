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

package com.xiaomi.data.push.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xiaomi.data.push.common.TaskHistoryData;
import com.xiaomi.data.push.common.TaskHistoryEnum;
import com.xiaomi.data.push.dao.mapper.TaskHistoryMapper;
import com.xiaomi.data.push.dao.model.TaskHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 　　* @description: TODO
 * 　　* @author zhenghao
 *
 */
@Service
public class TaskHistoryService {

    @Autowired
    private TaskHistoryMapper taskHistoryMapper;

    public void taskSuccessHistory(Integer taskId, String message, String uid) {
        List<TaskHistory> list = taskHistoryMapper.getTaskHistory(taskId, TaskHistoryEnum.NORMAL.getCode());
        //TaskHistoryDo taskHistoryDo = dao.fetch(TaskHistoryDo.class, Cnd.where("task_id", "=", taskId).and("status", "=", TaskHistoryEnum.NORMAL.getCode()));
        if (list.size() > 0) {
            this.doTaskHistory(list.get(0), true, taskId, message, uid);
        } else {
            this.doTaskHistory(null, true, taskId, message, uid);
        }

    }

    public void taskFailHistory(Integer taskId, String message, String uid) {
        List<TaskHistory> list = taskHistoryMapper.getTaskHistory(taskId, TaskHistoryEnum.NORMAL.getCode());
        //TaskHistoryDo taskHistoryDo = dao.fetch(TaskHistoryDo.class, Cnd.where("task_id", "=", taskId).and("status", "=", TaskHistoryEnum.NORMAL.getCode()));
        if (list.size() > 0) {
            this.doTaskHistory(list.get(0), false, taskId, message, uid);
        } else {
            this.doTaskHistory(null, false, taskId, message, uid);
        }
    }

    public void doTaskHistory(TaskHistory taskHistoryDo, boolean result, Integer taskId, String message, String uid) {
        if (taskHistoryDo == null) {
            TaskHistory historyDo = new TaskHistory();
            historyDo.setTaskId(taskId);

            historyDo.setCtime(System.currentTimeMillis());
            historyDo.setStatus(1);

            TaskHistoryData task = new TaskHistoryData();
            task.setTime(System.currentTimeMillis());
            task.setResult(result);
            task.setMessage(message);
            List<TaskHistoryData> list = new ArrayList<>();
            list.add(task);
            String s = new Gson().toJson(list);
            historyDo.setTaskContent(s);
            historyDo.setUid(uid);
            taskHistoryMapper.insert(historyDo);
        } else {

            Gson gson = new Gson();
            if (taskHistoryDo.getTaskContent() != null) {
                JsonParser jsonParser = new JsonParser();
                //获取JsonArray对象
                JsonArray jsonElements = jsonParser.parse(taskHistoryDo.getTaskContent()).getAsJsonArray();
                List<TaskHistoryData> beans = new ArrayList<>();
                for (JsonElement bean : jsonElements) {
                    //解析
                    TaskHistoryData TaskHistoryDataBean = gson.fromJson(bean, TaskHistoryData.class);
                    beans.add(TaskHistoryDataBean);
                }
                TaskHistoryData taskHistoryData = new TaskHistoryData();
                taskHistoryData.setTime(System.currentTimeMillis());
                taskHistoryData.setResult(result);
                taskHistoryData.setMessage(message);
                beans.add(taskHistoryData);
                String s = new Gson().toJson(beans);
                taskHistoryDo.setTaskContent(s);
                taskHistoryDo.setUid(uid);
                taskHistoryMapper.update(taskHistoryDo);
            } else {
                TaskHistory historyDo = new TaskHistory();
                historyDo.setTaskId(taskId);
                historyDo.setCtime(System.currentTimeMillis());
                historyDo.setStatus(1);
                TaskHistoryData task = new TaskHistoryData();
                task.setTime(System.currentTimeMillis());
                task.setResult(result);
                task.setMessage(message);
                List<TaskHistoryData> list = new ArrayList<>();
                list.add(task);
                String s = new Gson().toJson(list);
                historyDo.setTaskContent(s);
                historyDo.setUid(uid);
                taskHistoryMapper.update(historyDo);
            }

        }
    }


}
