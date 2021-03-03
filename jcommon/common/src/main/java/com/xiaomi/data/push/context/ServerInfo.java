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

package com.xiaomi.data.push.context;

import com.xiaomi.data.push.common.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 */
@Component
public class ServerInfo {

    private String ip;

    @Value("${server.port}")
    private String port;

    public ServerInfo() {
        this.ip = Utils.getIp();
    }

    public String getIp() {
        return ip;
    }


    public String getPort() {
        return port;
    }


    @Override
    public String toString() {
        return this.ip + ":" + this.port;
    }
}
