package com.xiaomi.mone.autumn.ex

import com.site.lookup.util.StringUtils
import com.xiaomi.data.push.redis.Redis

/**
 * @Author dingpei* @Date 2021/3/22 15:22
 */
class RedisEx {

    def call(Map m, Redis redis) {
        String cmd = m.get("cmd")
        List params = m.get("params")
        if (StringUtils.isEmpty(cmd)) {
            return "redis cmd is empty"
        }

        def objs = params.collect { it.getClass() } as Object[]

        def mm = redis.metaClass.getMetaMethod(cmd, objs)

        if (null != mm) {
            return mm.invoke(redis, params as Object[])
        }

        return "dont support this redis cmd:" + cmd


    }

}
