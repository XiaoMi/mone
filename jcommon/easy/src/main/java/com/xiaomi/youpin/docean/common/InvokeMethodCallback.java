package com.xiaomi.youpin.docean.common;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/3/30 17:03
 */
public interface InvokeMethodCallback {

    /**
     * @param map    额外的参数,用来当context使用
     * @param params
     */
    void before(Map<String, String> map, Object[] params);

    void after(Map<String, String> map, Object res);


}
