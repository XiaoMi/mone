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

package run.mone.docean.plugin.sidecar.interceptor;

import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/23/21
 */
@Slf4j
public abstract class AbstractInterceptor implements MethodInterceptor {

    private Ioc ioc;

    private Config config;

    @Setter
    private ExceptionProcessor exceptionProcessor = new ExceptionProcessor();

    @Setter
    private ResultProcessor resultProcessor = new ResultProcessor();

    @Setter
    private ParamProcessor paramProcessor = new ParamProcessor();


    public AbstractInterceptor(Ioc ioc, Config config) {
        this.ioc = ioc;
        this.config = config;
    }

    public abstract void intercept0(Context ctx, UdsCommand req, Object obj, Method method, Object[] params);

    public void intercept1(Context ctx, UdsCommand req, Object o) {

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

        UdsCommand command = UdsCommand.createRequest();
        Context ctx = new Context();
        command.setApp(config.get("uds_app", ""));
        command.setMethodName(method.getName());

        this.intercept0(ctx, command, obj, method, objects);
        if (ctx.getData().getOrDefault("skip_code", "false").equals("false")) {
            command.setParamTypes(Arrays.stream(method.getParameterTypes()).map(it -> it.getName()).toArray(String[]::new));
            ICodes codes = CodesFactory.getCodes(command.getSerializeType());
            command.setByteParams(Arrays.stream(objects).map(it -> codes.encode(it)).toArray(byte[][]::new));
        }
        this.intercept1(ctx, command, obj);
        //信息发送server(mesh)层
        UdsClient client = ioc.getBean("sideCarClient");

        UdsCommand res = null;
        int retries = command.getRetries();
        int i = 0;
        for (; ; ) {
            try {
                if (command.isAsync()) {
                    command.setReturnClass(method.getReturnType());
                }
                res = client.call(command);
                if (command.isAsync()) {
                    return res.getCompletableFuture();
                }
                //调用发生了错误,这里处理(曝出异常)
                exceptionProcessor.processException(res);
                break;
            } catch (Throwable ex) {
                if (++i > retries) {
                    throw ex;
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }
        }
        //处理参数
        paramProcessor.processResult(res,objects);

        //处理结果
        return resultProcessor.processResult(command, res, method);
    }
}
