package com.xiaomi.youpin.jcommon.log;

import ch.qos.logback.classic.PatternLayout;

import static com.xiaomi.youpin.jcommon.log.LogConstants.TID;

public class TraceIdPatternLayout extends PatternLayout {
    static {
        defaultConverterMap.put(TID, LogbackPatternConverter.class.getName());
    }
}
