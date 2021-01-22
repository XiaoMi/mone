package com.xiaomi.youpin.tesla.rcurve.proxy.egress;

import com.alibaba.nacos.api.exception.NacosException;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;

import com.xiaomi.youpin.docean.plugin.nacos.NacosConfig;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author dingpei@xiaomi.com
 * @date 1/11/21
 * nacos 对外访问
 */
@Slf4j
@Component
public class NacosEntry implements UdsProcessor {

    private Gson gson = new Gson();

    //todo 需要处理dataid和group，现在是写死的
    @Resource
    private NacosConfig nacosConfig;

    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put(cmd(), this);
    }

    @Override
    public String cmd() {
        return "nacos";
    }

    @Override
    public void processRequest(UdsCommand req) {
        UdsCommand res = UdsCommand.createResponse(req.getId());
        try {
            switch (req.getMethodName()) {
                case "getConfigStr": {
                    String value = nacosConfig.getConfigStr(req.getParams()[0], req.getParams()[1], Integer.valueOf(req.getParams()[2]));
                    res.setData(value);
                    Send.send(req.getChannel(), res);
                    break;
                }
                case "publishConfig": {
                    boolean r = nacosConfig.publishConfig(req.getParams()[0], req.getParams()[1], req.getParams()[2]);
                    res.setData(String.valueOf(r));
                    Send.send(req.getChannel(), res);
                    break;
                }
                case "deleteConfig": {
                    boolean r = nacosConfig.deleteConfig(req.getParams()[0], req.getParams()[1]);
                    res.setData(String.valueOf(r));
                    Send.send(req.getChannel(), res);
                    break;
                }
            }
        } catch (NacosException ex) {
            log.error(ex.getMessage(), ex);
            res.setCode(500);
            res.setMessage(ex.getMessage());
            Send.send(req.getChannel(), res);
        }

    }
}
