package com.xiaomi.mone.log.agent.service;

import com.xiaomi.mone.log.agent.export.MsgExporter;
import com.xiaomi.mone.log.agent.output.Output;
import com.xiaomi.mone.log.api.model.meta.LogPattern;

public interface OutPutService{

    boolean compare(Output oldOutPut, Output newOutPut);

    void preCheckOutput(Output output);

    MsgExporter exporterTrans(Output output) throws Exception;

    void removeMQ(Output output);

    Output configOutPut(LogPattern logPattern);
}
