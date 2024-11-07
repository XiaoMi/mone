package com.xiaomi.data.push.uds.handler;

/**
 * @author goodjava@qq.com
 * @date 2024/11/7 10:35
 */
public interface ClientStreamCallback {

    void onContent(String content);

    void onComplete();

    void onError(Throwable error);

}
