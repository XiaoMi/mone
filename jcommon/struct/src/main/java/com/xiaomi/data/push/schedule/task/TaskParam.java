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
    private String alarmUsername;

    //告警组名称
    private String alarmGroup;
    //告警级别 100=P0、101=P1、102=P2
    private Integer alarmLevel;

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

    public String getAlarmGroup() {
        return alarmGroup;
    }

    public void setAlarmGroup(String alarmGroup) {
        this.alarmGroup = alarmGroup;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
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
