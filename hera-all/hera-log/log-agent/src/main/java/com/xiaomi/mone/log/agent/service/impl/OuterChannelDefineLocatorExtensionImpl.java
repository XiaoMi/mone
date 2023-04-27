package com.xiaomi.mone.log.agent.service.impl;

import com.xiaomi.mone.log.agent.factory.OutPutServiceFactory;
import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.agent.service.ChannelDefineLocatorExtension;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.model.meta.LogPattern;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/27 12:20 PM
 */
@Service(name = "OuterChannelDefineLocatorExtensionImpl")
@Slf4j
public class OuterChannelDefineLocatorExtensionImpl implements ChannelDefineLocatorExtension {
    @Override
    public Output getOutPutByMQConfigType(LogPattern logPattern) {
        if (null != logPattern.getMQConfig()) {
            String typeName = logPattern.getMQConfig().getType();
            MiddlewareEnum middlewareEnum = MiddlewareEnum.queryByName(typeName);
            if (null != middlewareEnum) {
                return OutPutServiceFactory.getOutPutService(middlewareEnum.getServiceName()).configOutPut(logPattern);
            }
        }
        return null;
    }
}
