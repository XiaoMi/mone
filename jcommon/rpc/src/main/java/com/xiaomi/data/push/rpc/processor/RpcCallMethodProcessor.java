package com.xiaomi.data.push.rpc.processor;

import com.xiaomi.data.push.common.RpcCovertUtils;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.uds.codes.CodeType;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import com.xiaomi.youpin.docean.common.MethodReq;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2023/1/8 00:01
 * 用来调用本地方法的(让rpc调用变得更简单)
 */
public class RpcCallMethodProcessor implements NettyRequestProcessor {

    private final Function<MethodReq, Object> beanFactory;

    public RpcCallMethodProcessor(Function<MethodReq, Object> beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        byte[] body = request.getBody();
        ICodes codes = CodesFactory.getCodes(CodeType.PROTOSTUFF);
        MethodReq mr = codes.decode(body, MethodReq.class);
        //找到要执行的类
        Object obj = beanFactory.apply(mr);
        //执行指定方法
        Object res = null;
        HashMap<String, String> ext = new HashMap<>();
        ext.put("code", "0");
        try {
            res = ReflectUtils.invokeMethod(mr, obj, (paramTypes, params) -> RpcCovertUtils.convert(paramTypes, params));
        } catch (Throwable ex) {
            ext.put("code", "500");
            ext.put("message", ex.getMessage());
        }
        RemotingCommand response = RemotingCommand.createResponseCommand(RpcCmd.callMethodRes);
        response.setExtFields(ext);
        response.setBody(codes.encode(res));
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
