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

package com.xiaomi.youpin.gateway.filter;

import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class RequestFilter {

    public static final String DEFAULT_MACHINE_GROUP = "*";

    protected FilterDef def = new FilterDef(0, "", "", "");

    private Function<Class, Object> getBeanFunction;

    public abstract FullHttpResponse doFilter(FilterContext context, Invoker invoker, ApiInfo apiInfo, FullHttpRequest request);

    /**
     * 获取fiter定义
     */
    public FilterDef getDef() {
        return def;
    }

    public String getName() {
        String name = this.getDef().getName();
        if (StringUtils.isEmpty(name)) {
            return this.toString();
        }
        return name;
    }

    public List<String> getGroups() {
        String groups = this.getDef().getGroups();
        if (StringUtils.isEmpty(groups)) {
            return Arrays.asList(DEFAULT_MACHINE_GROUP);
        }

        return Arrays.asList(groups.split(","));
    }

    public void setDef(FilterDef def) {
        this.def = def;
    }

    public <T> T getBean(Class clazz) {
        return (T) getBeanFunction.apply(clazz);
    }

    public void setGetBeanFunction(Function<Class, Object> getBeanFunction) {
        this.getBeanFunction = getBeanFunction;
    }

    public boolean allow(ApiInfo apiInfo) {
        if (null == apiInfo) {
            log.info("filter error filter id:{} name:{}", this.def.getId(), this.def.getName());
            return false;
        }

        if (null == apiInfo.getFilterInfoMap()) {
            return false;
        }
        return apiInfo.getFilterInfoMap().containsKey(this.getDef().getName());
    }

    public Map<String, String> getFilterParams(ApiInfo apiInfo) {
        return apiInfo.getFilterInfoMap().get(this.getDef().getName()).getParams();
    }


    /**
     * filter 停止
     */
    public void stop() {
        log.info("filter stop:{}", this.def);
    }

    /**
     * filter 初始化
     */
    public void init() {
        log.info("filter init:{}", this.def);
    }

    /**
     * 是否是rpc filter
     * @return
     */
    public boolean rpcFilter() {
        return false;
    }

}
