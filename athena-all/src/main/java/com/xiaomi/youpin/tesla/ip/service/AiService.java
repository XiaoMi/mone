package com.xiaomi.youpin.tesla.ip.service;

import com.google.gson.JsonObject;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.ResourceUtils;

/**
 * @author goodjava@qq.com
 * @date 2023/12/5 22:51
 */
public class AiService {


    //同时支持远程和本地的(本地的被限流的次数会更少)
    public static JsonObject call(String req, long timeout, boolean vip, boolean jsonResult) {
        if (LabelUtils.open(Const.OPEN_AI_TEST)) {
            return LocalAiService.call(req, timeout);
        }
        return ProxyAiService.call(req, timeout, vip, jsonResult);

    }


    public static JsonObject call(String req, long timeout, boolean vip) {
        return call(req, timeout, vip, true);
    }


}
