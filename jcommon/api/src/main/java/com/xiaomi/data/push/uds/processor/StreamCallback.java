package com.xiaomi.data.push.uds.processor;

/**
 * @author goodjava@qq.com
 * @date 2024/11/7 11:56
 */
public interface StreamCallback {

    void onContent(String content);

    void onComplete();

    void onError(Throwable error);

}
