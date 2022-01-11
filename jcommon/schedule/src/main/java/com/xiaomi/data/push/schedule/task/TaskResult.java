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

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author goodjava@qq.com
 */
@Data
public class TaskResult {

    private int code;
    private String message;
    private String data;
    private long useTime;
    private String lastInvokeTime;
    private String ip;

    public TaskResult() {
    }

    public TaskResult(int code, String message, String data, String lastInvokeTime) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.lastInvokeTime = lastInvokeTime;
    }

    public TaskResult(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.lastInvokeTime = getLastInvokeTime();
    }

    public static TaskResult Success() {
        String now = getCurrentTimeFormated();
        return new TaskResult(TaskStatus.Success.code, "Success", "", now);
    }

    public static TaskResult Retry() {
        String now = getCurrentTimeFormated();
        return new TaskResult(TaskStatus.Retry.code, "Retry", "", now);
    }

    public static TaskResult Failure(String... data) {
        String now = getCurrentTimeFormated();
        return data.length > 0 ? new TaskResult(TaskStatus.Failure.code, "Failure", data[0], now) : new TaskResult(TaskStatus.Failure.code, "Failure", "", now);
    }

    private static String getCurrentTimeFormated() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

}
