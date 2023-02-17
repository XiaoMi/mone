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
