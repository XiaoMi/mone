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

package com.youpin.xiaomi.tesla.plugin.bo;

import lombok.Data;

@Data
public class RedisDatasource extends TeslaDatasource {

    /**
     * redis地址，ip+port，逗号分隔
     */
    private String redisHosts;

    /**
     * 单机1 or 集群2
     */
    private String redisType;
    public static String redisTypeOne = "dev";
    public static String redisTypeCluster = "cluster";

    public RedisDatasource() {
        this.setType("redis");
    }
}
