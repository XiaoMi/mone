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

package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.plugin.dubbo.DubboPlugin;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/6/25
 */
public class DubboPluginTest {

    @Test
    public void testPlugin() throws InterruptedException {
        String ip = "" ;
        Map<String,String> properties = ImmutableMap.<String,String>builder()
                .put("appName","testApp")
                .put("regAddress","nacos://"+ip+":80")
                .build();
        List<Bean> beans = Lists.newArrayList();
        DubboPlugin plugin = new DubboPlugin();
        plugin.init(new HashSet<>(), null);
        Thread.currentThread().join();
    }
}
