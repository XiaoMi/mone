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

package com.xiaomi.youpin.docean.plugin.dmesh.interceptor;

import com.google.gson.Gson;
import com.xiaomi.data.push.common.UdsException;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 1/23/21
 */
@Slf4j
public abstract class AbstractInterceptor implements MethodInterceptor {

    private Ioc ioc;

    private Config config;

    private MeshMsService reference;

    public AbstractInterceptor(Ioc ioc, Config config, MeshMsService reference) {
        this.ioc = ioc;
        this.config = config;
        this.reference = reference;
    }

    public abstract void intercept0(UdsCommand req);


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String mn = method.getName();
        if ("getClass".equals(mn)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if ("hashCode".equals(mn)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if ("toString".equals(mn)) {
            return methodProxy.invokeSuper(o, objects);
        }
        if ("equals".equals(mn)) {
            if (objects.length == 1) {
                return methodProxy.invokeSuper(o, objects);
            }
            throw new IllegalArgumentException("Invoke method [" + mn + "] argument number error.");
        }

        //信息发送server(mesh)层
        UdsClient client = ioc.getBean(UdsClient.class);
        UdsCommand command = UdsCommand.createRequest();
        command.setApp(config.get("uds_app", ""));

        if (null != reference) {
            command.setCmd(reference.name());
            command.setServiceName(reference.name());
            command.setRemoteApp(reference.app());
            command.setTimeout(reference.timeout());
        }

        command.setMethodName(method.getName());
        command.setParamTypes(Arrays.stream(method.getParameterTypes()).map(it -> it.getName()).toArray(String[]::new));
        command.setParams(Arrays.stream(objects).map(it -> new Gson().toJson(it)).toArray(String[]::new));
        ICodes codes = CodesFactory.getCodes(command.getSerializeType());
        command.setByteParams(Arrays.stream(objects).map(it -> codes.encode(it)).toArray(byte[][]::new));


        this.intercept0(command);

        UdsCommand res = client.call(command);

        //调用发生了错误
        if (res.getCode() != 0) {
            throw new UdsException(res.getMessage());
        }

        if (method.getReturnType().equals(void.class)) {
            return null;
        }

        Object r = res.getData(method.getReturnType());
        log.info("call uds receive:{}", r);
        return r;
    }
}
