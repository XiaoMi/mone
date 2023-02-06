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

package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Data
public class BillingReq {

    /**
     * 如果是机器则是ip
     */
    private List<String> resourceKeyList;

    /**
     * 0 online
     * 1 offline
     */
    private int type;

    /**
     * 0 下线服务
     * 1 下线机器
     */
    private int subType;


    private long projectId;

    private long envId;

    /**
     * 总cpu
     */
    private int cpu;

    /**
     * 使用的cpu
     */
    private int useCpu;



    public enum BillingType {
        online,
        offline,
        upgrade,
        downgrade,
    }

    public enum SubType {
        service,
        machine
    }

}
