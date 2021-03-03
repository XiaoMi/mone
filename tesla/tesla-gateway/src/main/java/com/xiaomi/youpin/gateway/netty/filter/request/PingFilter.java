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

package com.xiaomi.youpin.gateway.netty.filter.request;

import com.xiaomi.youpin.gateway.common.ByteBufUtils;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 用来测试性能
 *
 * @author goodjava@qq.com
 * @author 丁佩
 */
@Component
@Slf4j
@FilterOrder(2000)
public class PingFilter extends RequestFilter {

    @Autowired
    private ConfigService configService;

    private final String URL = "/mtop/arch/ping";

    @Override
    public FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        if (apiInfo.getUrl().equals(URL)) {
            int time = sleep();
            long begin = System.currentTimeMillis();
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            List<String> list = decoder.parameters().get("n");
            int n = 1;
            if (null != list && list.size() > 0) {
                n = Integer.valueOf(list.get(0).trim());
            }
            String res = "pong:" + System.currentTimeMillis() + ":" + n + ":" + time;

            StringBuilder sb = new StringBuilder();
            IntStream.range(0, n).forEach(i -> sb.append(res));

            ByteBuf buf = ByteBufUtils.createBuf(context, sb.toString(), configService.isAllowDirectBuf());
            long useTime = System.currentTimeMillis() - begin;
            log.info("ping filter use time:{}", useTime);
            return HttpResponseUtils.create(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf));
        }
        return invoker.doInvoker(context, apiInfo, request);
    }

    @Override
    public void init() {
        this.def.setAuthor("goodjava@qq.com;丁佩");
        this.def.setName("ping_filter");
        this.def.setVersion("0.0.1");
    }


    /**
     * 用来模拟更真实的调用
     *
     * @return
     */
    private int sleep() {
        int sleepTime = configService.getPingSleepTime();
        Random random = new Random();
        try {
            TimeUnit.MILLISECONDS.sleep(20 + random.nextInt(sleepTime));
            return sleepTime;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
