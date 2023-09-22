/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.channel;

import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
import com.xiaomi.mone.log.api.model.meta.LogPattern;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author shanwb
 * @date 2021-07-20
 */
@Data
public class ChannelDefine implements Serializable {

    private Long channelId;

    private String tailName;

    private Long appId;

    private String appName;

    private Input input;

    private Output output;

    private OperateEnum operateEnum;

    private List<String> ips;
    /**
     * Relationship between IP and directory
     */
    private List<LogPattern.IPRel> ipDirectoryRel;

    /**
     * filter and script configuration
     */
    private List<FilterConf> filters;
    /**
     * Only when the current machine is k8s and the log type is opentelemetry type log, it will take effect, and the pods alive on this machine.
     */
    private List<String> podNames;

    /**
     * Individual configuration data, default full configuration under this machine.
     */
    private Boolean singleMetaData;

    private String podType;
    /**
     * The log collection in the directory that needs to be deleted when a machine goes offline only has a value when a machine of a certain application goes offline.
     */
    private String delDirectory;

}
