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

package com.xiaomi.youpin.tesla.common.test;

import org.beetl.core.*;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BeetlTest {


    @Test
    public void testTemplate() throws IOException {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        Template t = gt.getTemplate("hello,${name}");
        t.binding("name", "beetl");
        String str = t.render();
        System.out.println(str);
    }


    @Test
    public void testFunction() throws IOException {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        //注册函数
        gt.registerFunction("zzy", (paras, ctx) -> "zzy:" + paras[0] + ":zzy");
        gt.registerFunction("zzy_print", new Function() {
            @Override
            public Object call(Object[] paras, Context ctx) {
                try {
                    ctx.byteWriter.write("gogogo!".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }
        });
        Template t = gt.getTemplate("<% var name2 = zzy('xiaomi');zzy_print();%>hello,${name} name:${name2}");
        t.binding("name", "beetl");
        String str = t.render();
        System.out.println(str);
    }


    @Test
    public void testPlugin() throws IOException {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);

        byte[] data = Files.readAllBytes(Paths.get("/Users/zhangzhiyong/IdeaProjects/tesla/tesla-common/src/main/resources/test/DemoHandler.java"));

        Template t = gt.getTemplate(new String(data));
        t.binding("package", "com.xiaomi.youpin.demo.plugin");
        t.binding("author", "goodjava@qq.com");
        t.binding("version", "0.0.1");
        t.binding("url", "/demo/go");
        String str = t.render();
        System.out.println(str);
    }
}
