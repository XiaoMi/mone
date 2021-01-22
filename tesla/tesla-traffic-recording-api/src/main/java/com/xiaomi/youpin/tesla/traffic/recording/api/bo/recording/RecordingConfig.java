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

package com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class RecordingConfig implements Serializable {

    /**
     * 录制配置id
     */
    private int id;

    /**
     * 配置名
     */
    private String name;

    /**
     * 录制流量来源
     * 网关、dubbo
     */
    private int sourceType;

    /**
     * 网关来源信息
     */
    private GatewaySource gatewaySource;

    /**
     * dubbo来源信息
     */
    private DubboSource dubboSource;

    /**
     * 录制策略
     * 按百分比录制、按header录制、按uid录制、按参数录制
     */
    private int recordingStrategy;

    private int percentage;
    private Map<String, String> headers;
    private long uid;

    /**
     * 状态：待录制 = 0、录制中 = 1
     */
    private int status;

    /**
     * 流量保存时间
     */
    private int saveDays;

    private long createTime;

    private long updateTime;

    private String creator;

    private String updater;

}
