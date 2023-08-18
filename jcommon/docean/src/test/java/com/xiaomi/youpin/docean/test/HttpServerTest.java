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

package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.aop.AopContext;
import com.xiaomi.youpin.docean.aop.EnhanceInterceptor;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.session.HttpSession;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import com.xiaomi.youpin.docean.test.demo.ErrorReport;
import com.xiaomi.youpin.docean.test.interceptor.TAInterceptor;
import com.xiaomi.youpin.docean.test.ssl.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Slf4j
public class HttpServerTest {

    @Test
    public void testHttpServer() throws InterruptedException {
        LinkedHashMap<Class, EnhanceInterceptor> m = new LinkedHashMap<>();
        m.put(RequestMapping.class, new EnhanceInterceptor() {
            @Override
            public void before(AopContext aopContext, Method method, Object[] args) {
                MvcContext mvcContext = ContextHolder.getContext().get();
                HttpSession session = mvcContext.session();
                if (session.getAttribute("name") == null) {
                    log.info("set name");
                    session.setAttribute("name", "zzy:" + new Date() + ":" + System.currentTimeMillis());
                }
            }
        });
        Aop.ins().init(m);
        Aop.ins().getInterceptorMap().put(TAnno.class, new TAInterceptor());
        Ioc ioc = Ioc.ins();
        ioc.putBean(ioc).init("com.xiaomi.youpin.docean");

        ErrorReport er = Ioc.ins().getBean(ErrorReport.class);
        if (er.isError()) {
            log.error(er.getMessage());
            System.exit(-1);
        }


        Ioc.ins().putBean("$response-original-value","true");
        Ioc.ins().putBean("$ssl_domain","zzy.com");
        Ioc.ins().putBean("$ssl_self_sign","false");

        Mvc.ins();
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().port(8999).websocket(false).ssl(false)
                .uploadDir("/tmp/v").upload(true)
                .build());
        server.start();
    }


    @Test
    public void testClient() {
        HttpClient.call();
    }
}
