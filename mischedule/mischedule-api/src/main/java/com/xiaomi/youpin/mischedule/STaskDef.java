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

package com.xiaomi.youpin.mischedule;

import com.xiaomi.data.push.schedule.task.ITaskDef;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * <p>
 * schedule 任务定义
 */
public enum STaskDef implements ITaskDef {
    //生成代码任务
    GeneratorCodeTask("generatorCodeTask", 1, 1, TimeUnit.SECONDS.toMillis(10L)),
    //代码前置检查任务
    PreCheckTask("preCheckTask", 1, 1, TimeUnit.SECONDS.toMillis(10L)),
    //sql任务
    SqlTask("sqlTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    //shell任务
    ShellTask("shellTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    //脚本任务
    ScriptTask("scriptTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    //编译任务
    CloudCompileTask("cloudCompileTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    //测试任务
    MiTestTask("miTestTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    //图任务
    GraphTask("graphTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    //http任务
    HttpTask("httpTask", 1, 100, TimeUnit.SECONDS.toMillis(10)),
    //dubbo任务
    DubboTask("dubboTask", 1, 100, TimeUnit.SECONDS.toMillis(10)),
    //代码检测任务
    CodeCheckTask("codeCheckTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    HealthyTask("healthyTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    DockerTask("dockerTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),
    JavaDocTask("javaDocTask", 1, 1, TimeUnit.SECONDS.toMillis(10)),

    //开机任务
    PowerOnTask("powerOnTask", 30, 1, TimeUnit.SECONDS.toMillis(10)),
    ;


    STaskDef(String type, int retryNum, int errorRetryNum, long timeOut) {
        this.type = type;
        this.retryNum = retryNum;
        this.errorRetryNum = errorRetryNum;
        this.timeOut = timeOut;
    }


    public String type;
    public int retryNum;
    public int errorRetryNum;
    public long timeOut;

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
