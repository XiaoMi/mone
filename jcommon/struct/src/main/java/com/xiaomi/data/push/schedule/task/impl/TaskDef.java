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

package com.xiaomi.data.push.schedule.task.impl;

import com.xiaomi.data.push.schedule.task.ITaskDef;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhiyong on 29/05/2018.
 */
public enum TaskDef implements ITaskDef {

    DemoTask("demoTask", 10, 3, TimeUnit.SECONDS.toMillis(10)),
    HttpTask("httpTask", 10, 3, TimeUnit.MILLISECONDS.toMillis(300)),
    CompileTask("compileTask", 10, 3,TimeUnit.SECONDS.toMillis(10))
    ;

    //任务的类型
    public String type;
    //正常情况下重试次数
    public int retryNum;
    //失败后重试的次数
    public int errorRetryNum;
    //任务的执行超时时间
    public long timeOut;

    TaskDef(String type, int retryNum, int errorRetryNum, long timeOut) {
        this.type = type;
        this.retryNum = retryNum;
        this.errorRetryNum = errorRetryNum;
        this.timeOut = timeOut;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public int retryNum() {
        return retryNum;
    }

    @Override
    public int errorRetryNum() {
        return errorRetryNum;
    }

    @Override
    public long timeOut() {
        return timeOut;
    }
}
