package com.xiaomi.youpin.tesla.test.script

import com.xiaomi.youpin.gateway.db.ScriptTable
import com.xiaomi.youpin.gateway.dubbo.Dubbo
import com.xiaomi.youpin.gateway.dubbo.MethodInfo
import com.xiaomi.youpin.gateway.nacos.Nacos
import com.xiaomi.youpin.gateway.nacos.NacosConfig
import org.nutz.dao.Dao

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/15 14:52
 */


def a() {
    println "d"

    Dao dao = null
    list = dao.fetch(ScriptTable.class)


    Nacos nacos = null
    NacosConfig config = new NacosConfig()
    config.setDataId("gateway_detail")
    config.setGroupId("DEFAULT_GROUP")
    nacos.getConfig(config)


    Dubbo dubbo = null
    MethodInfo methodInfo = new MethodInfo()
    methodInfo.setGroup("")
    methodInfo.setMethodName("ping")
    methodInfo.setServiceName("com.youpin.xiaomi.tesla.service.TeslaGatewayService")
    methodInfo.setParameterTypes(new String[]{})
    methodInfo.setArgs(new Object[]{})
    dubbo.call(methodInfo)

}


a()
