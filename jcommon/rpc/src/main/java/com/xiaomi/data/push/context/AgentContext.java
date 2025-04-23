/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.context;

import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.netty.AgentEventListener;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 */
public class AgentContext {

    public ConcurrentHashMap<String, AgentChannel> map = new ConcurrentHashMap<>();

    private static final List<AgentEventListener> listeners = new ArrayList<>();

    private AgentContext() {

    }

    public List<AgentChannel> list() {
        return new ArrayList<>(map.values());
    }


    private static class LazyHolder {
        private static final AgentContext ins = new AgentContext();
    }


    public static final AgentContext ins() {
        return LazyHolder.ins;
    }

    public void addListener(AgentEventListener listener) {
        listeners.add(listener);
    }

    public void put(String remoteAddr, AgentChannel agentChannel) {
        map.put(remoteAddr, agentChannel);
        if (!CollectionUtils.isEmpty(listeners)) {
            listeners.forEach(lis -> SafeRun.run(() -> lis.onPut(remoteAddr, agentChannel)));
        }
    }

    public AgentChannel remove(String remoteAddr) {
        AgentChannel agentChannel = map.remove(remoteAddr);
        if (!CollectionUtils.isEmpty(listeners)) {
            listeners.forEach(lis -> SafeRun.run(() -> lis.onRemove(remoteAddr, agentChannel)));
        }
        return agentChannel;
    }
}
