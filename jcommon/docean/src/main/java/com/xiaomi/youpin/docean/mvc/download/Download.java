package com.xiaomi.youpin.docean.mvc.download;

import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcRequest;
import com.xiaomi.youpin.docean.mvc.MvcResponse;
import com.xiaomi.youpin.docean.mvc.download.DownloadService;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 * @date 2022/4/9 15:40
 */
@Slf4j
public class Download {

    public static void download(MvcContext context, MvcRequest request, MvcResponse response) {
        String name = request.getParams().getOrDefault("name", "");
        if (StringUtils.isEmpty(name)) {
            response.writeAndFlush(context, HttpResponseStatus.NOT_FOUND, "");
            return;
        }
        String id = UUID.randomUUID().toString();
        try {
            new DownloadService().download(context.getHandlerContext(), context.getRequest(), name, id);
        } catch (IOException e) {
            log.error("download:{} error:{}", name, e.getMessage());
        }
    }

}
