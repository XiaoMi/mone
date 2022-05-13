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

package com.xiaomi.mione.graalvm.bootstrap;

import com.xiaomi.mione.graalvm.anno.LogAnno;
import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * @Author goodjava@qq.com
 * @Date 2021/5/30 16:10
 */
@Slf4j
public class Bootstrap {

    /**
     * 用来存储依赖关系
     */
    private static boolean save = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        initAop();
        saveOrLoad();
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().port(8999).websocket(true).build());
        server.start();
    }

    private static void saveOrLoad() {
        if (save) {
            Ioc.ins().init("com.xiaomi.mione.graalvm");
            Ioc.ins().saveSnapshot();
        } else {
            Ioc.ins().init("");
            Ioc.ins().loadSnapshot();
        }
    }

    private static void initAop() {
        LinkedHashMap<Class, EnhanceInterceptor> m = new LinkedHashMap<>();
        m.put(LogAnno.class, new EnhanceInterceptor() {
            @Override
            public void before(AopContext aopContext, Method method, Object[] args) {
                log.info("begin");
            }

            @Override
            public Object after(AopContext context, Method method, Object res) {
                Object r = super.after(context, method, res);
                log.info("after");
                return r;
            }
        });
        Aop.ins().init(m);
    }

}
