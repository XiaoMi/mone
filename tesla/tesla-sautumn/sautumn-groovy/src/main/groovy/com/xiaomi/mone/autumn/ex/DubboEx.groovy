package com.xiaomi.mone.autumn.ex

import com.xiaomi.youpin.gateway.dubbo.MethodInfo
import com.xiaomi.youpin.gateway.dubbo.Dubbo

/**
 * @Author dingpei* @Date 2021/3/22 15:22
 */
class DubboEx {

    def call(Map m, Dubbo dubbo) {

        String[] types = m.get("paramTypes")
        Object[] params = m.get("params")

        MethodInfo methodInfo = new MethodInfo()
        methodInfo.setServiceName(m.get("serviceName"))
        methodInfo.setMethodName(m.get("methodName"))
        methodInfo.setParameterTypes(types)
        methodInfo.setArgs(params)
        methodInfo.setTimeout(m.get("timeout", 1000))
        methodInfo.setGroup(m.get("group", ""))
        methodInfo.setVersion(m.get("version", ""))

        return dubbo.call(methodInfo)
    }

}
