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

package com.xiaomi.youpin.gateway.task;

/**
 *
 * @author zhangzhiyong
 * @date 29/05/2018
 * 任务结果
 */
public class TaskResult {

    private int code;
    private String message;
    private String data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public TaskResult() {
    }

    public TaskResult(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static TaskResult Success() {
        return new TaskResult(TaskStatus.Success.code, "Success", "");
    }

    public static TaskResult Retry() {
        return new TaskResult(TaskStatus.Retry.code, "Retry", "");
    }

    public static TaskResult Failure(String... data) {
        if (data.length > 0) {
            return new TaskResult(TaskStatus.Failure.code, "Failure", data[0]);
        }
        return new TaskResult(TaskStatus.Failure.code, "Failure", "");
    }


    @Override
    public String toString() {
        return "TaskResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
