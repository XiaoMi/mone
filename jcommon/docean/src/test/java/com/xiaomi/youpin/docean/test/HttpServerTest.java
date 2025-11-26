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
//        aop();
        log.info("http server");
        Ioc ioc = Ioc.ins();
        ioc.putBean(ioc).init("com.xiaomi.youpin.docean");

        ErrorReport er = Ioc.ins().getBean(ErrorReport.class);
        if (er.isError()) {
            log.error(er.getMessage());
            System.exit(-1);
        }
        Ioc.ins().putBean("$response-original-value", "true").putBean("$openStaticFile", "true").putBean("$staticFilePath", "/tmp/");
//        Ioc.ins().putBean("$ssl_domain", "zzy.com");
//        Ioc.ins().putBean("$ssl_self_sign", "false");
//        Ioc.ins().putBean("$ssl_certificate","/Users/zhangzhiyong/key/zzy.com/certificate.crt");
//        Ioc.ins().putBean("$ssl_cprivateKey","/Users/zhangzhiyong/key/zzy.com/privateKey.key");

        Mvc.ins();
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder()
                .httpVersion(HttpServerConfig.HttpVersion.http1)
                .ssl(false)
                .port(8899)
                .websocket(false)
                .uploadDir("/tmp/v").upload(false)
                .build());
        server.start();
    }

    private static void aop() {
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
    }


    //websocket server
    @SneakyThrows
    @Test
    public void testWebSocketServer() {
        Ioc ioc = Ioc.ins();
        ioc.putBean(ioc).init("com.xiaomi.youpin.docean");
        Mvc.ins();
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder()
                .httpVersion(HttpServerConfig.HttpVersion.http1)
                .port(8899)
                .websocket(true)
                .useWs(true)
                .build());
        server.start();
    }

    //用OkHttp写一个websocket客户端,然后发一个ok过去
    @SneakyThrows
    @Test
    public void testWebSocketClient() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://127.0.0.1:8899/ws").build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
//                webSocket.send("ok");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("Received: " + text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                System.out.println("Closing: " + code + " / " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.err.println("Error: " + t.getMessage());
            }
        };

        WebSocket ws = client.newWebSocket(request, listener);
        ws.send("ok2");
        System.in.read();
//        client.dispatcher().executorService().shutdown();
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
        IntStream.range(0, 100).forEach(i -> {
            try {
                Response response = client.newCall(request).execute();
                System.out.println("---->" + response.body().string());
            } catch (Throwable ex) {

            }
        });
    }


    public static String s() {
        // Create a ByteBuffer to store the content of the SETTINGS frame.
        ByteBuffer buffer = ByteBuffer.allocate(6);

        // Identifier for writing into SETTINGS_MAX_CONCURRENT_STREAMS (0x3)
        buffer.putShort((short) 0x3);

        // The value written into SETTINGS_MAX_CONCURRENT_STREAMS (e.g., 100)
        buffer.putInt(100);

        // Convert ByteBuffer to byte array
        byte[] settingsFrame = buffer.array();

        // rate limited or exceeded quota
        String http2Settings = Base64.getUrlEncoder().encodeToString(settingsFrame);

        return http2Settings;
    }


}
