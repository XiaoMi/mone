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

package com.xiaomi.youpin.quota.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@Data
public class QuotaInfo implements Serializable {
    /**
     * 业务方的唯一id
     */
    private long bizId;

    private long projectId;

    private int num = 1;

    private int cpu = 1;

    private int originCpu = 1;

    private long mem;

    private Set<Integer> ports;

    private String ip;

    /**
     * 指定迁移的目标ip(如果为空,则自己选择)
     */
    private String targetIp;

    private Map<String, String> labels;

    private String operator;

}
