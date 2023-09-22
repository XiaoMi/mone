package com.xiaomi.hera.trace.etl.util;

import com.xiaomi.hera.tspandata.TAttributeType;
import com.xiaomi.hera.tspandata.TValue;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

public class ThriftUtil {

    public static final TProtocolFactory PROTOCOL_FACTORY = new TCompactProtocol.Factory();

    public static String getStringValue(TValue value, TAttributeType type){
        switch (type){
            case DOUBLE:
                return String.valueOf(value.getDoubleValue());
            case LONG:
                return String.valueOf(value.getLongValue());
            case BOOLEAN:
                return String.valueOf(value.isBoolValue());
            default:
                return value.getStringValue();
        }
    }
}
