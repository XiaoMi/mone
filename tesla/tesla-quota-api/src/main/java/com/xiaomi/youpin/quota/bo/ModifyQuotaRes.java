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
import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Data
public class ModifyQuotaRes implements Serializable {

    /**
     * 改变的
     */
    private List<ResourceBo> ips;

    /**
     * 当前的所有
     */
    private List<ResourceBo> currIps;

    private String type;


    private int subType;

    private boolean success;

    /**
     * 总cpu
     */
    private int cpu;

    /**
     * 使用的cpu
     */
    private int useCpu;


    public enum SubType {
        none,
        //升配
        upgrade,
        //降配
        downgrade
    }

}
