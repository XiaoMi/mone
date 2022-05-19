package com.xiaomi.data.push.context;

import com.xiaomi.data.push.rpc.netty.AgentChannel;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 */
public class AgentContext {

    public ConcurrentHashMap<String, AgentChannel> map = new ConcurrentHashMap<>();

    private AgentContext(){

    }


    private static class LazyHolder {
        private static final AgentContext ins = new AgentContext();
    }


    public static final AgentContext ins() {
        return LazyHolder.ins;
    }

}
