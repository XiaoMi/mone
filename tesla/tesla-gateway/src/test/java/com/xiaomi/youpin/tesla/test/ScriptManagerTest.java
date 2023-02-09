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

import com.google.common.collect.Maps;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.xiaomi.youpin.gateway.common.ScriptManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


@Slf4j
public class ScriptManagerTest {


    @Test
    public void testInvoke() throws IOException {
        ScriptManager sm = new ScriptManager();

        String script = new String(Files.readAllBytes(Paths.get("src/main/resources/script/test.groovy")));
        System.out.println(script);

        Object res = sm.invoke(script, "sum", new HashMap<>(), 11, 22);


        System.out.println(res);

    }


//    @Test
//    public void testInvokeBefore() throws IOException {
//        ScriptManager sm = new ScriptManager();
//        String script = new String(Files.readAllBytes(Paths.get("src/main/resources/script/test2.groovy")));
//        System.out.println(script);
//        ApiInfo apiInfo = new ApiInfo();
//        HashMap<String, Object> m = Maps.newHashMap();
//        m.put("log", log);
//        sm.invokeBefore(script, apiInfo, null, m);
//        System.out.println(apiInfo.getId());
//    }


//    @Test
//    public void testInvokeAfter() throws IOException {
//        ScriptManager sm = new ScriptManager();
//        String script = new String(Files.readAllBytes(Paths.get("src/main/resources/script/test2.groovy")));
//        System.out.println(script);
//        ApiInfo apiInfo = new ApiInfo();
//        Object res = sm.invokeAfter(script, apiInfo, null, "abc", Maps.newHashMap());
//        System.out.println(res);
//    }
}


