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

package com.xiaomi.youpin.mischedule.docker;

import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.schedule.task.TaskContext;
import com.xiaomi.youpin.mischedule.bo.DockerResData;

import java.util.concurrent.TimeUnit;

public class YPBuildImageResultCallback extends BuildImageResultCallback {

    private TaskContext taskContext;
    private String mqTag;
    private Stopwatch sw;
    private DockerResData dockerResData;

    public YPBuildImageResultCallback (TaskContext taskContext, String mqTag, Stopwatch sw, DockerResData dockerResData) {
        this.taskContext = taskContext;
        this.mqTag = mqTag;
        this.sw = sw;
        this.dockerResData = dockerResData;
    }

    @Override
    public  void onNext(BuildResponseItem item) {
        super.onNext(item);
        dockerResData.setMsg(item.getStream());
        dockerResData.setTime(sw.elapsed(TimeUnit.MILLISECONDS));
        taskContext.notifyMsg(mqTag, new Gson().toJson(dockerResData));
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }
}
