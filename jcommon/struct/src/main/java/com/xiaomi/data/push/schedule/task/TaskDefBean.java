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

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class TaskDefBean implements Serializable {

    private String type;

    private int retryNum;

    private int errorRetryNum;

    private long timeOut;

    private String name;

    /**
     * cron 表达式
     */
    private String cron;


    public TaskDefBean() {
    }

    public TaskDefBean(ITaskDef taskDef) {
        this.type = taskDef.type();
        this.retryNum = taskDef.retryNum();
        this.errorRetryNum = taskDef.errorRetryNum();
        this.timeOut = taskDef.timeOut();
        this.name = taskDef.name();
    }

}
