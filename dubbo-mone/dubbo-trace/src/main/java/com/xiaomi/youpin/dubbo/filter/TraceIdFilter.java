package com.xiaomi.youpin.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.dubbo.common.Constants.TRACE_ID;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;

/**
 * @author goodjava@qq.com
 */
@Activate(group = PROVIDER, order = -9999)
public class TraceIdFilter implements Filter {

    private static final TraceIdUtils traceIdUtils = TraceIdUtils.ins();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String traceId = invocation.getAttachment(TRACE_ID, traceIdUtils.uuid());

        //传递给consumer
        RpcContext.getContext().setAttachment(TRACE_ID, traceId);

        Result res = null;
        boolean supportServerAsync = false;
        try {
            res = invoker.invoke(invocation);
            supportServerAsync = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.ASYNC_KEY, false);
            return res;
        } catch (Throwable ex) {
            throw ex;
        } finally {
            if (!supportServerAsync && null != res && null != res.getValue() && (res.getValue() instanceof com.xiaomi.youpin.infra.rpc.Result)) {
                com.xiaomi.youpin.infra.rpc.Result r = (com.xiaomi.youpin.infra.rpc.Result) res.getValue();
                //放入traceId 和 附加数据
                modifyRes(traceId, r);
            }
        }
    }

    private void modifyRes(String traceId, com.xiaomi.youpin.infra.rpc.Result r) {
        try {
            r.setTraceId(traceId);
            //只有在业务没有指定的情况下,会放入新的
            if (!Optional.ofNullable(r.getAttachments()).isPresent()) {
                Map<String, String> attachments = new HashMap<>(1);
                r.setAttachments(attachments);
            }
            r.getAttachments().put("timestamp", String.valueOf(System.currentTimeMillis()));
        } catch (Throwable ex) {
            //ignore
        }
    }
}
