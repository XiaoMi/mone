package com.xiaomi.mione.prometheus.redis.monitor;

import lombok.Data;
import redis.clients.jedis.Jedis;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/7/11 5:37 下午
 */
@Data
public class AttachInfo implements Serializable {

    private String hostName;
    private int port;
    private int dbIndex;

    public AttachInfo(){

    }
    public AttachInfo(Jedis jedis){
        if(jedis != null && jedis.getClient() != null){

            this.hostName = jedis.getClient().getHost();
            this.port = jedis.getClient().getPort();
            this.dbIndex = jedis.getClient().getDB().intValue();

        }
    }

}
