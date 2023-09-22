package com.xiaomi.youpin.docean.mvc.upload;

import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.listener.event.Event;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcRequest;
import com.xiaomi.youpin.docean.mvc.MvcResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/8/28 16:41
 */
public class MvcUpload {

    public static void upload(Mvc mvc, MvcRequest request, MvcResponse response, MvcContext context) {
        Map<String, String> m = new HashMap<>(2);
        if (null != request.getParams()) {
            m.putAll(request.getParams());
        }
        m.put("fileName", new String(request.getBody()));
        mvc.getIoc().publishEvent(new Event(EventType.mvcUploadFinish, m));
        response.writeAndFlush(context, "upload success");
    }


}
