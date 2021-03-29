package com.xiaomi.mone.autumn.ex

import com.xiaomi.youpin.gateway.nacos.NacosConfig
import com.xiaomi.youpin.gateway.nacos.Nacos


/**
 * @Author dingpei* @Date 2021/3/22 15:22
 */
class NacosEx {

    def call(Map m, Nacos nacos) {
        String cmd = m.get("cmd")
        if ("getConfig".equals(cmd)) {
            String groupId = m.get("groupId")
            String dataId = m.get("dataId")
            Integer timeout = m.get("timeout", 1000)
            NacosConfig nacosConfig = new NacosConfig(groupId, dataId, timeout)
            return nacos.getConfig(nacosConfig)
        }

        return "dont support this nacos cmd"
    }

}
