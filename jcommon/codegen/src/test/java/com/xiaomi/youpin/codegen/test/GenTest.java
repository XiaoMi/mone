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

package com.xiaomi.youpin.codegen.test;

import com.xiaomi.youpin.codegen.FilterGen;
import com.xiaomi.youpin.codegen.PluginGen;
import com.xiaomi.youpin.codegen.SpringBootProGen;
import  com.xiaomi.youpin.codegen.DoceanProGen;
import org.junit.Test;

public class GenTest {


    @Test
    public void testFilter() throws Exception{
        FilterGen filterGen = new FilterGen();
        filterGen.generateAndZip("/Users/dingpei/workspace", "filter1", "com.xiaomi.youpin", "com.xiaomi.youpin.filter1", "dp", "0.0.1", "1000", "[]", "Filter1", "desc", "address", "true");
    }

    @Test
    public void testPlugin() throws Exception{
        PluginGen filterGen = new PluginGen();
        filterGen.generateAndZip("/Users/dingpei/workspace", "plugin1", "com.xiaomi.youpin", "com.xiaomi.youpin.plugin1", "dp", "0.0.1", "url");
    }

    @Test
    public void testPro() throws Exception{
        SpringBootProGen springBootProGen = new SpringBootProGen();
        springBootProGen.generateAndZip("/Users/dingpei/workspace", "project1204one", "com.xiaomi.youpin", "com.xiaomi.youpin.project1204one", "dfz", "0.0.1", false, "aaaa", "bbbb", 3);
    }
    @Test
    public void testDoceanPro() throws Exception{
        DoceanProGen doceanProGen = new DoceanProGen();
        doceanProGen.generateAndZip("/Users/zhangjunyi/workspace", "projectzjy", "com.xiaomi.youpin", "com.xiaomi.youpin.projectzjy", "zhangjunyi_gen", "0.0.1");
    }
}
