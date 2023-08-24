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

import com.google.common.collect.Lists;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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


        Ioc.ins().putBean("$response-original-value", "true");
        Ioc.ins().putBean("$ssl_domain", "zzy.com");
        Ioc.ins().putBean("$ssl_self_sign", "false");
//        Ioc.ins().putBean("$ssl_certificate","/Users/zhangzhiyong/key/zzy.com/certificate.crt");
//        Ioc.ins().putBean("$ssl_cprivateKey","/Users/zhangzhiyong/key/zzy.com/privateKey.key");

        Mvc.ins();
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder()
//                .httpVersion(HttpServerConfig.HttpVersion.http2)
//                .ssl(true)
                .port(8999)
                .websocket(false)
                .uploadDir("/tmp/v").upload(false)
                .build());
        server.start();
    }


    @Test
    public void testClient() {
        HttpClient.call("https://zzy.com:8999/a");
    }


    @Test
    public void testH2c() {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(1000, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool())
//                .protocols(Lists.newArrayList(Protocol.H2_PRIOR_KNOWLEDGE))
                .build();

        Request request = new Request.Builder()
                .addHeader("upgrade", "true")
                .url("http://zzy.com:8999/a")
                .build();

        Call call = client.newCall(request);
        try {
            Response res = call.execute();
            System.out.println(res.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @SneakyThrows
    @Test
    public void testH2c2() {
        OkHttpClient client = new OkHttpClient.Builder().followRedirects(true).build();

        Request request = new Request.Builder()
                .url("http://zzy.com:8999/a")
                .header("Connection", "Upgrade, HTTP2-Settings")
                .header("Upgrade", "h2c")
                .header("HTTP2-Settings", s())
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("----><" + response.body().string());



        System.in.read();
    }

    @Test
    public void testH2c3() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().protocols(Lists.newArrayList(Protocol.H2_PRIOR_KNOWLEDGE)).build();
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8999/a")
                .build();
        IntStream.range(0,100).forEach(i->{
            try {
                Response response = client.newCall(request).execute();
                System.out.println("---->" + response.body().string());
            } catch (Throwable ex) {

            }
        });
    }


    public static String s() {
        // 创建一个ByteBuffer，用于存储SETTINGS帧的内容
        ByteBuffer buffer = ByteBuffer.allocate(6);

        // 写入SETTINGS_MAX_CONCURRENT_STREAMS的标识符（0x3）
        buffer.putShort((short) 0x3);

        // 写入SETTINGS_MAX_CONCURRENT_STREAMS的值（例如，100）
        buffer.putInt(100);

        // 将ByteBuffer转换为字节数组
        byte[] settingsFrame = buffer.array();

        // 对字节数组进行Base64编码
        String http2Settings = Base64.getUrlEncoder().encodeToString(settingsFrame);

        return http2Settings;
    }


}
