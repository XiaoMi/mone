package com.xiaomi.mone.autumn

import com.xiaomi.mone.autumn.ex.MysqlEx
import com.xiaomi.mone.autumn.ex.HttpEx
import com.xiaomi.mone.autumn.ex.DubboEx
import com.xiaomi.mone.autumn.ex.RedisEx
import com.xiaomi.mone.autumn.ex.RocketmqEx
import com.xiaomi.mone.autumn.ex.NacosEx
import groovy.util.logging.Slf4j
import org.nutz.dao.Dao
import com.xiaomi.youpin.gateway.http.Http
import com.xiaomi.youpin.gateway.dubbo.Dubbo
import com.xiaomi.data.push.redis.Redis
import com.xiaomi.youpin.gateway.rocketmq.RocketMq
import com.xiaomi.youpin.gateway.nacos.Nacos

/**
 * @Author goodjava@qq.com
 * @Author shanwenbang@xiaomi.com
 * @Author small榆荚* @Date 2021/3/20 17:31
 */
@Slf4j
class AutumnEx {

    def static call(Map m, def v) {
        log.info("AutumxEx call m:{} v:{}", m, v.getClass().getSimpleName())
        switch (v.getClass().getSimpleName()) {
        //sql 操作
            case "NutDao":
                Dao dao = v
                return new MysqlEx().call(m, dao)
        //http 操作
            case "HttpImp":
                Http http = (Http) v
                return new HttpEx().call(m, http)
        //dubbo
            case "DubboClient":
                Dubbo dubbo = v
                return new DubboEx().call(m, dubbo)
        //redis
            case "Redis":
                Redis redis = v
                return new RedisEx().call(m, redis)
            case "RocketMqImp":
                RocketMq rocketMq = v
                return new RocketmqEx().call(m, rocketMq)
            case "ConfigService":
                Nacos nacos = v
                return new NacosEx().call(m, nacos)
            default:
                return "don't support"
        }
        return "autumn"
    }

}
