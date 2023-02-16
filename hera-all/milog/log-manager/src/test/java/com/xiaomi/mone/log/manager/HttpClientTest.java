package com.xiaomi.mone.log.manager;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV6;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/25 15:58
 */
@Slf4j
public class HttpClientTest {

    @Test
    public void test() {
        Gson gson = new Gson();

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("projectName", "mifaas_space_all");
        paramsMap.put("appId", 91441);
        paramsMap.put("appName", "testperf");
        paramsMap.put("appCreator", "xxx");
        paramsMap.put("appCreatTime", System.currentTimeMillis());
        paramsMap.put("funcId", 222);
        paramsMap.put("funcName", "eqrere");
        paramsMap.put("logPath", "/home/work/log/mifaas/server.log");
        paramsMap.put("appType", 4);
        paramsMap.put("appTypeText", "serverLess");
        paramsMap.put("machineRoom", "cn");

        String api = "http://127.0.0.1:7788/open/api/milog/access/mifass";
        String params = gson.toJson(paramsMap);
        HashMap<String, String> hashMap = Maps.newHashMap();
        hashMap.put("Content-Type", "application/json");
        String pr = HttpClientV6.post(api, params, hashMap);
        log.warn("hera log create, param:{}, result:{}", params, pr);

    }
}
