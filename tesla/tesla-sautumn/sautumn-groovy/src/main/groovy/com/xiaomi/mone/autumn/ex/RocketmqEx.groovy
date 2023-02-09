package com.xiaomi.mone.autumn.ex

import com.site.lookup.util.StringUtils
import com.xiaomi.youpin.gateway.rocketmq.RocketMq
import groovy.util.logging.Slf4j

/**
 * @Author dingpei* @Date 2021/3/22 15:22
 */
@Slf4j
class RocketmqEx {

    def call(Map m, RocketMq rocketMq) {
        String cmd = m.get("cmd")
        if ("send".equals(cmd)) {
            if (StringUtils.isEmpty(m.get("topic"))) {
                return "error topic"
            }
            return rocketMq.send(m.get("topic"), m.get("data"))
        }
        return "dont support this rocketmq cmd"
    }

}
