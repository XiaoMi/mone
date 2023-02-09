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

package run.mone.sidecar.service;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import run.mone.docean.plugin.sidecar.bo.Ping;
import run.mone.docean.plugin.sidecar.bo.SideCarApp;
import run.mone.docean.plugin.sidecar.service.SideCarInfoService;

/**
 * @author goodjava@qq.com
 * @date 2022/6/19
 */
@Service(name = "sideCarInfoService")
public class SideCarInfoServiceImpl implements SideCarInfoService {

    @Value("$app")
    private String app;

    @Override
    public SideCarApp getSideCarApp() {
        SideCarApp sideCarApp = new SideCarApp();
        sideCarApp.setApp(app);
        return sideCarApp;
    }

    @Override
    public Ping getPingData() {
        Ping ping = new Ping();
        ping.setData("pingping");
        return ping;
    }
}
