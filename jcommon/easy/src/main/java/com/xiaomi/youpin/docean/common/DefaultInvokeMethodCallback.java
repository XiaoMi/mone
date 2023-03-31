package com.xiaomi.youpin.docean.common;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/3/30 17:07
 */
public class DefaultInvokeMethodCallback implements InvokeMethodCallback {

    public boolean isDubboCall(Map<String, String> attachments) {
        if (null != attachments) {
            String type = attachments.getOrDefault("call_type", "");
            if (type.equals("dubbo")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void before(Map<String, String> map, Object[] params) {

    }

    @Override
    public void after(Map<String, String> map, Object res) {

    }
}
