package com.xiaomi.youpin.dubbo.filter;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.rpc.RpcContext;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.dubbo.common.Constants.SPAN_ID;
import static org.apache.dubbo.common.Constants.TRACE_ID;
import static org.apache.dubbo.common.constants.CommonConstants.APPLICATION_KEY;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class TraceIdUtils {

    private String pid = "";

    private TraceIdUtils() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        pid = name.split("@")[0];
    }


    private static class LazyHolder {
        private static TraceIdUtils ins = new TraceIdUtils();
    }


    public static TraceIdUtils ins() {
        return LazyHolder.ins;
    }

    public String ip() {
        return NetUtils.getLocalHost();
    }


    public String pid() {
        return pid;
    }

    public String uuid() {
        return getUUID();
    }

    public String uuid2() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    public String traceId() {
        String traceId = RpcContext.getContext().getAttachment(TRACE_ID);
        if (null == traceId) {
            //尝试从context 中获取
            return Optional.ofNullable(RpcContext.getContext().get(TRACE_ID)).orElse("").toString();
        }
        return traceId;
    }


    public void setTraceIdAndSpanId(String traceId, String spanId) {
        RpcContext.getContext().setObjectAttachment(TRACE_ID, traceId);
        RpcContext.getContext().setAttachment(SPAN_ID, spanId);

    }


    public static String getKey(String traceId, String spanId) {
        return traceId + "_" + spanId;
    }


    public static void traceLog(long begin, String application, String interfaceName, String methodName, String traceId, String spanId, String argumentsStr, String resStr, String exceptionStr, int code) {
        try {
            Gson gson = new Gson();
            TraceLog traceLog = new TraceLog();
            traceLog.setServiceName(interfaceName);
            traceLog.setMethodName(methodName);
            traceLog.setTraceId(traceId);
            traceLog.setRpcId(spanId);
            traceLog.setEndTime(System.currentTimeMillis());
            traceLog.setStartTime(begin);
            traceLog.setUri(interfaceName + "." + methodName);
            traceLog.setIp(TraceIdUtils.ins().ip());
            traceLog.setPid(TraceIdUtils.ins().pid());
            traceLog.setDuration(traceLog.getEndTime() - traceLog.getStartTime());

            Map<String, String> kvLogs = Maps.newHashMap();
            kvLogs.put(APPLICATION_KEY, application);
            kvLogs.put("arguments", argumentsStr);
            kvLogs.put("result", resStr);
            kvLogs.put("exception", exceptionStr);
            traceLog.setKvLogs(kvLogs);
            traceLog.setLogType(LogType.CLIENT.name());
            traceLog.setStatus(code);
            log.info(gson.toJson(traceLog));
        } catch (Throwable ex) {
            //ignore
        }
    }


    public static String getUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long lsb = random.nextLong();
        long msb = random.nextLong();
        byte[] buf = new byte[32];
        formatUnsignedLong(lsb, buf, 20, 12);
        formatUnsignedLong(lsb >>> 48, buf, 16, 4);
        formatUnsignedLong(msb, buf, 12, 4);
        formatUnsignedLong(msb >>> 16, buf, 8, 4);
        formatUnsignedLong(msb >>> 32, buf, 0, 8);
        return new String(buf, Charsets.UTF_8);
    }


    private static void formatUnsignedLong(long val, byte[] buf, int offset, int len) {
        int charPos = offset + len;
        int radix = 1 << 4;
        int mask = radix - 1;
        do {
            buf[--charPos] = DIGITS[((int) val) & mask];
            val >>>= 4;
        } while (charPos > offset);
    }


    final static byte[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z'
    };

}
