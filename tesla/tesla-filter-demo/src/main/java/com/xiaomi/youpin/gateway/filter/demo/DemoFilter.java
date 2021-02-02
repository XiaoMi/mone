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

package com.xiaomi.youpin.gateway.filter.demo;

import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.dubbo.Dubbo;
import com.xiaomi.youpin.gateway.filter.CustomRequestFilter;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.nacos.Nacos;
import com.xiaomi.youpin.gateway.nacos.NacosConfig;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author goodjava@qq.com
 * 示例filter
 */
@FilterOrder(2010)
@Slf4j
public class DemoFilter extends CustomRequestFilter {

    @Override
    public FullHttpResponse execute(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request) {
        //用户选择了此filter
        //打印设置的参数
        Redis redis = this.getBean(Redis.class);
        Dubbo dubbo = this.getBean(Dubbo.class);
        Nacos nacos = this.getBean(Nacos.class);
        try {
            String content = nacos.getConfig(new NacosConfig("DEFAULT_GROUP", "test", 1000));
            log.info("content:{}", content);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        log.info("{},{}", redis, dubbo);
        log.info("params:{} {} {}", this.getFilterParams(apiInfo), redis, dubbo);
        return HttpResponseUtils.create("demo filter");
    }

    public static void main(String... args) {
        InputStream is = new DemoFilter().getClass().getResourceAsStream("/FilterDef");
        System.out.println(is);

    }
}
