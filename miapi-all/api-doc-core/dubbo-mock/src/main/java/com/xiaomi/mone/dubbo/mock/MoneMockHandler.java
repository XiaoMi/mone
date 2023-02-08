/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.dubbo.mock;

import com.xiaomi.mone.dubbo.mock.util.ClassHelper;
import com.xiaomi.mone.dubbo.mock.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcResult;

import java.lang.reflect.Type;

@Slf4j
public class MoneMockHandler<T> {

    public static <T> Result invoke(Invoker<T> invoker, Invocation invocation, String interfaceName, String methodName) {
        //mock switch
        if (!MockConfig.INSTANCE.isMockEnable()) {
            return invoker.invoke(invocation);
        }

        if (MockConfig.INSTANCE.isInvocationMockEnable(interfaceName, methodName)) {
            String mockUrl = MockConfig.INSTANCE.buildMockRequestUrl(interfaceName, methodName);
            log.debug("mockUrl:{}", mockUrl);
            try {
                String mockValue = HttpUtil.doGet(mockUrl);
                Type[] returnTypes = ClassHelper.getReturnTypes(interfaceName, methodName, invocation.getParameterTypes());
                Object obj = MockValueResolver.resolve(mockValue, returnTypes);
                return new RpcResult(obj);
            } catch (Exception e) {
                log.error("interface:{} method:{}mock失败,转为正常调用", interfaceName, methodName, e);
            }
        }
        //normal invoke
        return invoker.invoke(invocation);
    }


}
