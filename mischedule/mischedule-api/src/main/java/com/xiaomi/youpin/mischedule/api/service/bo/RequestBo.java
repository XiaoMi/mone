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

package com.xiaomi.youpin.mischedule.api.service.bo;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * @author gaoyibo
 */
@Data
public class RequestBo implements Serializable {

    public RequestBo () {}

    public RequestBo (String taskName) {
        this.taskName = taskName;
    }

    /**
     * task名称,用来寻找task定义
     */
    @NonNull
    private String taskName;

    /**
     * 参数(json序列化后的)
     */
    private String param;

    private long beginTime;

    private String source;

    /**
     * 需要定时调度的
     */
    private String cron;

    private int errorRetryNum;
}
