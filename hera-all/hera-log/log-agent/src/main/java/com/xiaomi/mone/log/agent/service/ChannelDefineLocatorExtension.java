package com.xiaomi.mone.log.agent.service;

import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.api.model.meta.LogPattern;

public interface ChannelDefineLocatorExtension {

    Output getOutPutByMQConfigType(LogPattern logPattern);
}
