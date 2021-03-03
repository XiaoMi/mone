package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.http.Http;
import com.xiaomi.youpin.docean.plugin.http.Response;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @author dingpei@xiaomi.com
 * @date 1/11/21
 * http 对外访问
 */
@Slf4j
@Component
public class HttpEgress implements UdsProcessor {

    @Resource
    private Http http;

    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put("http", this);
    }

    @Override
    public void processRequest(UdsCommand req) {
        UdsCommand res = UdsCommand.createResponse(req);
        switch (req.getMethodName()) {
            case "get": {
                String url = new Gson().fromJson(req.getParams()[0], String.class);
                Response r = http.get(url, Maps.newHashMap(), req.getTimeout());
                res.setData(new String(r.getData()));
                Send.send(req.getChannel(), res);
                break;
            }
            case "post": {
                String url = new Gson().fromJson(req.getParams()[0], String.class);
                String body = new Gson().fromJson(req.getParams()[1], String.class);
                Map<String, String> headers = new Gson().fromJson(req.getParams()[2], new TypeToken<HashMap<String, String>>(){}.getType());
                Response r = http.post(url, body, headers, req.getTimeout());
                res.setData(new String(r.getData()));
                Send.send(req.getChannel(), res);
                break;
            }
        }

    }
}
