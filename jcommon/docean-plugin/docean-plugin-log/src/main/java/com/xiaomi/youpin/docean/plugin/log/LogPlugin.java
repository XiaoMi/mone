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

package com.xiaomi.youpin.docean.plugin.log;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/7/4
 */

@Slf4j
@DOceanPlugin
public class LogPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init log plugin");
        Config config = ioc.getBean(Config.class);
        Log log = new Log();
        if (config.get("log_type", "console").equals("file")) {
            LogWriter lw = new LogWriter(config.get("log_path", "/tmp/log"));
            lw.init(1024 * 1024 * 20);
            log.setLogWriter(lw);
        }
        log.init();
        ioc.putBean(log);
    }


    @Override
    public String version() {
        return "0.0.2:2020-07-09:goodjava@qq.com";
    }
}
