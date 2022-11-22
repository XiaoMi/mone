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

package com.youpin.xiaomi.tesla.plugin.handler;


import com.youpin.xiaomi.tesla.plugin.bo.Request;
import com.youpin.xiaomi.tesla.plugin.bo.RequestContext;
import com.youpin.xiaomi.tesla.plugin.bo.Response;

/**
 * @author goodjava@qq.com
 */
public interface IHandler<Data> {

    /**
     * 入口
     *
     * @param request
     * @return
     */
    Response<Data> execute(RequestContext context, Request request);

    /**
     * 路由的url
     *
     * @return
     */
    String url();

    /**
     * handler的版本号
     *
     * @return
     */
    default String version() {
        return "0.0.1";
    }

}
