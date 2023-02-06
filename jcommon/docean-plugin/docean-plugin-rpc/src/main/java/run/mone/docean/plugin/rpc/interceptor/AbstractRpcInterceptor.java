/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.docean.plugin.rpc.interceptor;

import com.xiaomi.data.push.common.Service;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.RpcServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.uds.codes.CodeType;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.MethodReq;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import run.mone.docean.plugin.rpc.context.RpcContext;
import run.mone.docean.plugin.rpc.context.RpcContextHolder;
import run.mone.docean.plugin.rpc.processor.ExceptionProcessor;
import run.mone.docean.plugin.rpc.processor.ResultProcessor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/23/21
 */
@Slf4j
@Data
public abstract class AbstractRpcInterceptor implements MethodInterceptor {

    private Ioc ioc;

    private Config config;

    @Setter
    private ExceptionProcessor exceptionProcessor = new ExceptionProcessor();

    @Setter
    private ResultProcessor resultProcessor = new ResultProcessor();

    private String side = "";

    public AbstractRpcInterceptor(Ioc ioc, Config config) {
        this.ioc = ioc;
        this.config = config;
    }

    public abstract void intercept0(Context ctx, RemotingCommand req, Object obj, Method method, Object[] params);

    public void intercept1(Context ctx, RemotingCommand req, Object o) {

    }


    @Override
    public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String mn = method.getName();
        if ("getClass".equals(mn)) {
            return methodProxy.invokeSuper(obj, objects);
        }
        if ("hashCode".equals(mn)) {
            return methodProxy.invokeSuper(obj, objects);
        }
        if ("toString".equals(mn)) {
            return methodProxy.invokeSuper(obj, objects);
        }
        if ("equals".equals(mn)) {
            if (objects.length == 1) {
                return methodProxy.invokeSuper(obj, objects);
            }
            throw new IllegalArgumentException("Invoke method [" + mn + "] argument number error.");
        }

        RemotingCommand command = RemotingCommand.createRequestCommand(RpcCmd.callMethodReq);

        Context ctx = new Context();

        command.setApp(config.get("uds_app", ""));
        command.setMethodName(method.getName());

        this.intercept0(ctx, command, obj, method, objects);
        if (ctx.getData().getOrDefault("skip_code", "false").equals("false")) {
            MethodReq methodReq = new MethodReq();
            methodReq.setServiceName(ctx.getData().get("serviceName").toString());
            methodReq.setMethodName(mn);
            methodReq.setParamTypes(Arrays.stream(method.getParameterTypes()).map(it -> it.getName()).toArray(String[]::new));
            ICodes codes = CodesFactory.getCodes(CodeType.PROTOSTUFF);
            methodReq.setByteParams(Arrays.stream(objects).map(it -> codes.encode(it)).toArray(byte[][]::new));
            command.setBody(codes.encode(methodReq));
        }
        this.intercept1(ctx, command, obj);
        //信息发送server(mesh)层
        Service service = ioc.getBean(side.equals("server") ? "" : RpcServer.class.getName(), RpcClient.class.getName());

        //thread local
        RpcContext context = RpcContextHolder.getContext().get();
        if (null != context) {
            command.setAddress(context.getAddress());
        }

        RemotingCommand res = null;
        int retries = command.getRetries();
        int i = 0;
        for (; ; ) {
            try {
                res = service.call(command);
                //调用发生了错误,这里处理(曝出异常)
                exceptionProcessor.processException(res);
                break;
            } catch (Throwable ex) {
                if (++i >= retries) {
                    throw ex;
                }
                exceptionProcessor.processException(ex);
                TimeUnit.MILLISECONDS.sleep(200);
            }
        }

        //处理结果
        return resultProcessor.processResult(command, res, method);
    }
}
