/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.xiaomi.mione.prometheus.redis.monitor;

import lombok.Data;
import redis.clients.jedis.Jedis;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/7/11 5:37 下午
 */
@Data
public class AttachInfo implements Serializable {

    private String hostName;
    private int port;
    private int dbIndex;

    public AttachInfo(){

    }
    public AttachInfo(Jedis jedis){
        if(jedis != null && jedis.getClient() != null){

            this.hostName = jedis.getClient().getHost();
            this.port = jedis.getClient().getPort();
            this.dbIndex = jedis.getClient().getDB().intValue();

        }
    }

}
