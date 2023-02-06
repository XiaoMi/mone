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

package com.xiaomi.youpin.tesla.test;

import com.youpin.xiaomi.tesla.plugin.bo.Request;
import com.youpin.xiaomi.tesla.plugin.bo.Response;
import com.xiaomi.youpin.gateway.plugin.TeslaPluginManager;
import org.junit.Test;

public class PluginTest {


    @Test
    public void testCall() {
        TeslaPluginManager m = new TeslaPluginManager();
        m.init();
        m.load();
        Response res = m.call("/demo/go", null, new Request());
        System.out.println(res.getData());
    }


    @Test
    public void testStart() {
        TeslaPluginManager m = new TeslaPluginManager();
        m.init();
        m.startPlugin("/tmp/plugin/tesla-plugin-1.0.0-SNAPSHOT.jar", "demo-plugin","");
        Response res = m.call("/demo/go", null, new Request());
        System.out.println(res.getData());
    }

    @Test
    public void testStop() {
        TeslaPluginManager m = new TeslaPluginManager();
        m.init();
        m.startPlugin("/tmp/plugin/tesla-plugin-1.0.0-SNAPSHOT.jar", "demo-plugin","");
        m.stopPlugin("demo-plugin","");

    }
}
