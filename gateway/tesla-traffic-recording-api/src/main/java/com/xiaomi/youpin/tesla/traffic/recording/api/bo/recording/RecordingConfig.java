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

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class RecordingConfig implements Serializable {

    /**
     * 录制配置id
     */
    @HttpApiDocClassDefine(value = "id", required = false, description = "录制配置id", defaultValue = "1")
    private int id;

    /**
     * 配置名
     */
    @HttpApiDocClassDefine(value = "name", required = false, description = "配置名", defaultValue = "配置名字")
    private String name;

    /**
     * 录制流量来源
     * 网关、dubbo
     */
    @HttpApiDocClassDefine(value = "sourceType", required = false, description = "录制流量来源", defaultValue = "1")
    private int sourceType;

    /**
     * 网关来源信息
     */
    @HttpApiDocClassDefine(value = "gatewaySource", required = false, description = "网关来源信息", defaultValue = "")
    private GatewaySource gatewaySource;

    /**
     * dubbo来源信息
     */
    @HttpApiDocClassDefine(value = "dubboSource", required = false, description = "dubbo来源信息", defaultValue = "")
    private DubboSource dubboSource;

    /**
     * 录制策略
     * 按百分比录制、按header录制、按uid录制、按参数录制、按返回结果code值
     */
    @HttpApiDocClassDefine(value = "recordingStrategy", required = false, description = "录制策略", defaultValue = "1")
    private int recordingStrategy;

    @HttpApiDocClassDefine(value = "percentage", required = false, description = "按百分比录制", defaultValue = "10")
    private int percentage;

    @HttpApiDocClassDefine(value = "headers", required = false, description = "按header录制", defaultValue = "")
    private Map<String, String> headers;

    @HttpApiDocClassDefine(value = "uid", required = false, description = "按uid录制", defaultValue = "1002134")
    private long uid;

    @HttpApiDocClassDefine(value = "recordingStrategyOperator", required = false, description = "操作符", defaultValue = "=")
    private String recordingStrategyOperator;

    @HttpApiDocClassDefine(value = "resCode", required = false, description = "按返回结果code值", defaultValue = "200")
    private String resCode;

    /**
     * 状态：待录制 = 2、录制中 = 1
     */
    @HttpApiDocClassDefine(value = "status", required = false, description = "状态：待录制 = 0、录制中 = 1", defaultValue = "1")
    private int status;

    /**
     * 流量保存时间
     */
    @HttpApiDocClassDefine(value = "saveDays", required = false, description = "流量保存时间", defaultValue = "7")
    private int saveDays;

    @HttpApiDocClassDefine(value = "createTime", required = false, description = "创建时间", defaultValue = "12133294710")
    private long createTime;

    @HttpApiDocClassDefine(value = "updateTime", required = false, description = "更新时间", defaultValue = "14324998236")
    private long updateTime;

    @HttpApiDocClassDefine(value = "creator", required = false, description = "创建人", defaultValue = "dingpei")
    private String creator;

    @HttpApiDocClassDefine(value = "updater", required = false, description = "更新人", defaultValue = "dingpei")
    private String updater;

}
