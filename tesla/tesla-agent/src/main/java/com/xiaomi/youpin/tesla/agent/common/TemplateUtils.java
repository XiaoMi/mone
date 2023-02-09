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

package com.xiaomi.youpin.tesla.agent.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class TemplateUtils {

    public static String renderTemplate(String template, Map<String, Object> m) {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            Template t = gt.getTemplate(template);
            m.forEach((k, v) -> t.binding(k, v));
            String str = t.render();
            return str;
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
        return "";
    }


    public static String getTemplate(String name) {
        InputStream is = TemplateUtils.class.getClassLoader().getResourceAsStream("template/" + name);
        try {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return "";
    }

}
