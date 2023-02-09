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

package com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dingpei
 * @author goodjava@qq.com
 */
@Data
public class Traffic implements Serializable {

    private String id;

    /**
     * 录制配置id
     */
    private int recordingConfigId;

    /**
     * 流量来源
     */
    private int sourceType;

    /**
     * http流量
     */
    private HttpTraffic httpTraffic;

    /**
     * dubbo流量
     */
    private DubboTraffic dubboTraffic;

    /**
     * 调用结果
     */
    private String response;

    /**
     * 调用开始时间
     */
    private long invokeBeginTime;

    /**
     * 调用结束时间
     */
    private long invokeEndTime;

    private String traceId;

    private long uid;

    /**
     * 流量保存时间
     */
    private int saveDays;

    private long createTime;

    private long updateTime;

    private String creator;

    private String updater;

}
