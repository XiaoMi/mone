package com.xiaomi.youpin.docean.common;

/**
 * @author goodjava@qq.com
 * @date 2023/3/30 17:03
 */
public interface InvokeMethodCallback {

    void before(Object[] params);

    void after(Object res);


}
