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

package com.xiaomi.youpin.docean.plugin.log;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author goodjava@qq.com
 */
@Data
public class LogRecord {

    private String line;
    private String message;
    private String tag;
    private String threadName;
    private String level;
    private String traceId;
    private String time;
    private String className;
    private String methodName;
    private String pid;
    private String ip;
    private String appName;
    private String group;
    private long timestamp;
    private String errorInfo;
    private String params;
    private String result;
    private String code;
    private String owner;
    private long costTime;
    private String errorSource;

    private LocalDateTime ltime;

    /**
     * 扩展字段
     */
    private String extra;

}
