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

package com.xiaomi.youpin.jcommon.log;

import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author goodjava@qq.com
 */
public class TalosClient {

    @Setter
    private String accessKey;

    @Setter
    private String accessSecret;

    @Setter
    private String topicName;

    @Setter
    private String endpoint;

    private AtomicBoolean initSuccess = new AtomicBoolean(false);

    public TalosClient() {
    }

    public TalosClient(String accessKey, String accessSecret, String topicName, String endpoint) {
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
        this.topicName = topicName;
        this.endpoint = endpoint;
    }

    public void init() {
        Properties properties = new Properties();
        properties.setProperty("galaxy.talos.service.endpoint", endpoint);
    }

    public void shutdown() {
        initSuccess.set(false);
    }




    public boolean sendMsg(String msgStr) {

        return true;
    }


}
