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

package com.xiaomi.youpin.demo.pd;

import com.xiaomi.youpin.tesla.plug.BaseHandler;
import com.youpin.xiaomi.tesla.plugin.bo.Request;
import com.youpin.xiaomi.tesla.plugin.bo.RequestContext;
import com.youpin.xiaomi.tesla.plugin.bo.Response;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;


/**
 * @author goodjava@qq.com
 */
@Slf4j
@Extension
public class PdHandler extends BaseHandler<Object> {


    @Override
    public Response<Object> execute(RequestContext context, Request request) {
        return Response.success("plugin demo success:" + System.currentTimeMillis());
    }

    @Override
    public String url() {
        return "/mtop/test/pd";
    }
}
