package com.xiaomi.mone.dubbo.mock;

import com.google.gson.Gson;
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
        //mock总开关
        if (!MockConfig.INSTANCE.isMockEnable()) {
            return invoker.invoke(invocation);
        }

        if (MockConfig.INSTANCE.isInvocationMockEnable(interfaceName, methodName)) {
            String mockUrl = MockConfig.INSTANCE.buildMockRequestUrl(interfaceName, methodName);
            log.debug("mockUrl:{}", mockUrl);
            try {
                //通过easymock返回mock数据
                String mockValue = HttpUtil.doGet(mockUrl);
                //通过反射拿到返回的类型
                Type[] returnTypes = ClassHelper.getReturnTypes(interfaceName, methodName, invocation.getParameterTypes());
                Object obj = MockValueResolver.resolve(mockValue, returnTypes);
                return new RpcResult(obj);
            } catch (Exception e) {
                // TODO: 2020-02-08 同样的异常处理
                log.error("interface:{} method:{}mock失败,转为正常调用", interfaceName, methodName, e);
            }
        }
        //正常调用
        return invoker.invoke(invocation);
    }


}
