package com.xiaomi.mone.log.agent.factory;

import com.xiaomi.mone.log.agent.service.OutPutService;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.youpin.docean.Ioc;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/7 9:59 AM
 */
public class OutPutServiceFactory {

    private static String defaultServiceName;

    static{
        defaultServiceName = Config.ins().get("default.output.service", "RocketMQService");
    }

    public static OutPutService getOutPutService(String serviceName){
        OutPutService bean = Ioc.ins().getBean(serviceName);
        return bean == null ? Ioc.ins().getBean(defaultServiceName) : bean;
    }
}
