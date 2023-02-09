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

package com.xiaomi.youpin.tesla.agent.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyMsg {
    private int status;

    private int step;

    private String type;

    private String message;

    private long time;

    /**
     * 部署的id
     */
    private long bizId;

    private Map<String, String> attachments;

    public static final int STATUS_PROGRESS = 0;
    public static final int STATUS_SUCESSS = 1;
    public static final int STATUS_FAIL = 2;

}
