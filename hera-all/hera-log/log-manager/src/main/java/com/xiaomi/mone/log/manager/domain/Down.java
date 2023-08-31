/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.youpin.docean.mvc.ContextHolder;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcRequest;
import com.xiaomi.youpin.docean.mvc.MvcResponse;
import com.xiaomi.youpin.docean.mvc.download.Download;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Down {
    public static void down(String path) throws IOException {
        ChannelHandlerContext handlerContext = ContextHolder.getContext().get().getHandlerContext();
        ContextHolder context = ContextHolder.getContext();
        MvcContext mvcContext = context.get();
        MvcResponse mvcResponse = new MvcResponse();
        mvcResponse.setCtx(handlerContext);
        MvcRequest mvcRequest = new MvcRequest();
        Map<String, String> param = new HashMap<>();
        param.put("name", path);
        mvcRequest.setParams(param);
        new Download().download(mvcContext, mvcRequest, mvcResponse);
    }

}
