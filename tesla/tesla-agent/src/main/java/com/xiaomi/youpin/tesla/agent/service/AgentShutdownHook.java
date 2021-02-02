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

package com.xiaomi.youpin.tesla.agent.service;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.interceptor.Log;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2020/6/18
 */
@Slf4j
@Component(desc = "shutdown hook service")
public class AgentShutdownHook implements IService {

    @Log
    @Override
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("shutdown hook begin");
            DeployService.ins().save();
            Ioc.ins().destory();
            log.info("shutdown hook finish");
        }));
    }

}
