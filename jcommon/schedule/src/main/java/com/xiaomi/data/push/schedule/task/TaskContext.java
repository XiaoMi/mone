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
import com.xiaomi.data.push.dao.model.TaskWithBLOBs;
import com.xiaomi.data.push.schedule.task.notify.Notify;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class TaskContext {

    private Map<String, String> map = Maps.newHashMap();

    @Getter
    @Setter
    private transient Notify notify;

    @Getter
    @Setter
    private transient TaskWithBLOBs taskData;

    /**
     * 每个任务只能制定一个拦截器
     * 拦截器
     */
    public static final String INTERCEPTOR = "interceptor";

    public static final String UPDATE = "update";

    public static final String FALSE = "false";


    public static final String CACHE = "cache";

    /**
     * 延时处理
     */
    private long delay;

    /**
     * 只执行一次
     */
    private boolean once;

    private String responseCode;
    private String statusCode;
    private String email;
    private String feishu;


    public TaskContext() {
    }

    public int getInt(String key) {
        String value = this.map.get(key);
        return null == value ? 0 : Integer.valueOf(value);
    }

    public void putInt(String key, int value) {
        this.map.put(key, String.valueOf(value));
    }

    public void put(String key, String value) {
        this.map.put(key, value);
    }

    public void remove(String key) {
        this.map.remove(key);
    }

    public String get(String key) {
        return this.map.get(key);
    }

    public String getString(String key) {
        String str = this.map.get(key);
        return null == str ? "" : str;
    }

    /**
     * 发送通知信息
     *
     * @param msg
     */
    public void notifyMsg(String type, String msg) {
        if (null != notify) {
            notify.notify(type, msg);
        }
    }

    /**
     * 发送通知信息
     *
     * @param msg
     */
    public void notifyMsg(String type, String msg, int shardingKey) {
        if (null != notify) {
            notify.notify(type, msg, shardingKey);
        }
    }

}
