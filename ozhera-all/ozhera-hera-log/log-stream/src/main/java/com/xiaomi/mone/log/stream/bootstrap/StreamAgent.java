/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.stream.bootstrap;

import com.alibaba.nacos.client.config.utils.SnapShotSwitch;
import com.xiaomi.mone.log.stream.config.ConfigManager;
import com.xiaomi.mone.log.stream.config.MilogConfigListener;
import com.xiaomi.mone.log.stream.plugin.es.EsPlugin;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Boolean.FALSE;

@Service
@Slf4j
public class StreamAgent {

    @Resource
    private ConfigManager configManager;

    public void init() {
        try {
            log.info("start");
            if (EsPlugin.InitEsConfig()) {
                SnapShotSwitch.setIsSnapShot(FALSE);
                configManager.listenMilogStreamConfig();
            } else {
                System.exit(1);
            }
            graceShutdown();
        } catch (Exception e) {
            log.error("服务初始化异常", e);
        }
    }

    private void graceShutdown() {
        //关闭操作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stream shutdown!");
            ConcurrentHashMap<Long, MilogConfigListener> listeners = configManager.getListeners();
            listeners.values().forEach(milogConfigListener -> {
                milogConfigListener.getJobManager().stopAllJob();
            });
        }));
    }

}
