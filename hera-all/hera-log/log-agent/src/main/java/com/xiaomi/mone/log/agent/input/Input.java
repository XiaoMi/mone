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

package com.xiaomi.mone.log.agent.input;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shanwb
 * @date 2021-07-19
 */
@Data
@EqualsAndHashCode
public abstract class Input {
    /**
     * LogTypeEnum.name()，
     * 可以根据这个类型，决定是单行收集还是多行收集(支持java异常栈)
     *
     * @see com.xiaomi.mone.log.api.enums.LogTypeEnum
     */
    private String type;

    /**
     * 日志路径格式
     * 当前支持 单文件、多文件、目录层级通配模式
     * <p>
     * 单文件: /home/work/log/xxapp/server.log
     * 多文件: /home/work/log/neo-logs/(cxx01|cxx022)/server.log
     * 目录层级通配:/home/work/log/xxapp/ * /server.log
     */
    private String logPattern;

    /**
     *
     */
    private String patternCode;

    private String logSplitExpress;
    /**
     * 用户自定义的行首正则，默认是""
     */
    private String linePrefix;

}
