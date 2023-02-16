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

package com.xiaomi.mone.log.api.model.meta;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author shanwb
 * @date 2021-07-19
 */
@Data
public class LogPattern implements Serializable {
    /**
     * logtail主键ID
     */
    private Long logtailId;

    private String tailName;
    /**
     * ip信息，k8s中一个node中可能有多个pod,导致一个tail有多个ip，因此ip顺序必须和路径的顺序对应
     */
    private List<String> ips;

    /**
     * LogTypeEnum.name()，
     * 可以根据这个类型，决定是单行收集还是多行收集(支持java异常栈)
     *
     * @see com.xiaomi.mone.log.api.enums.LogTypeEnum
     */
    private Integer logType;

    /**
     * 日志路径，
     * 暂时只支持单个日志文件
     */
    private String logPattern;
    /**
     * 日志切分表达式
     */
    private String logSplitExpress;

    /**
     * 每个pathCode对应不同的mq tag；
     * 由app + logPath 组合生成，用于消息隔离
     */
    private String patternCode;
    /**
     * 默认不是删除
     */
    private OperateEnum operateEnum;

    private List<FilterDefine> filters;

    /**
     * mq配置
     */
    private MQConfig mQConfig;
}
