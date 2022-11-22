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

package com.xiaomi.youpin.gateway.netty;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.Utils;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * <p>
 * group filter中的任务,如果是post,那么body必须是json格式的
 */
public class Body extends HashMap<String, Object> {

    public Body(FullHttpRequest request) {
        byte[] body = HttpRequestUtils.getRequestBody(request);
        if (null != body && body.length > 0) {
            Map m = Utils.getGson().fromJson(new String(body), new TypeToken<Map<String,Object>>(){}.getType());
            this.putAll(m);
        }
    }
}
