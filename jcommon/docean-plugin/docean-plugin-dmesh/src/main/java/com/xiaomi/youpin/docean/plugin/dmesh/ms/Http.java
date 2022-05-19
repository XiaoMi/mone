package com.xiaomi.youpin.docean.plugin.dmesh.ms;

import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @author dingpei@xiaomi.com
 * @date 1/11/21
 * Http 操作
 */
@MeshMsService(interfaceClass = Http.class, name = "http")
public interface Http {

    /**
     * get 调用
     * @param url
     * @return
     */
    String get(String url);

    /**
     * post 调用
     */
    String post(String url, String body, Map<String, String> headers);


}
