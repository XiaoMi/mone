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

package com.xiaomi.mone.log.agent.channel;

import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.agent.input.Input;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
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
     * todo filter、script配置
     */
    private List<FilterConf> filters;
    /**
     * 只有当前机器为k8s且日志类型为opentelemetry类型日志时才有作用，这个机器上存活的pod
     */
    private List<String> podNames;

    /**
     * 单个配置数据，默认该机器下的全量配置
     */
    private Boolean singleMetaData;

    private String podType;

}
