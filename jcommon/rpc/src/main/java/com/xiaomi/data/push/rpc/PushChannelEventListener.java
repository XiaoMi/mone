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

package com.xiaomi.data.push.rpc;

import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.common.RemotingUtil;
import com.xiaomi.data.push.rpc.exception.RemotingSendRequestException;
import com.xiaomi.data.push.rpc.exception.RemotingTimeoutException;
import com.xiaomi.data.push.rpc.exception.RemotingTooMuchRequestException;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.netty.ChannelEventListener;
import com.xiaomi.data.push.rpc.netty.NettyRemotingServer;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by zhangzhiyong on 30/05/2018.
 */
public class PushChannelEventListener implements ChannelEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PushChannelEventListener.class);



    /**
     * 客户端的数量
     *
     * @return
     */
    public int clientNum() {
        return AgentContext.ins().map.size();
    }


    public Collection<Channel> clients() {
        return AgentContext.ins().map.values().stream().map(it->it.getChannel()).collect(Collectors.toList());
    }


    /**
     * 同步发送
     *
     * @param server
     * @param command
     */
    public void sendMessageToAll(NettyRemotingServer server, RemotingCommand command) {
        if (AgentContext.ins().map.size() > 0) {
            AgentContext.ins().map.forEach((k, v) -> {
                try {
                    RemotingCommand res = server.invokeSync(v.getChannel(), command, TimeUnit.SECONDS.toMillis(1));
                    logger.info("res----->{}", new String(res.getBody()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemotingSendRequestException e) {
                    e.printStackTrace();
                } catch (RemotingTimeoutException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 异步发送
     *
     * @param server
     * @param command
     * @param timeout
     * @param callback
     */
    public void sendMessageToAll(NettyRemotingServer server, RemotingCommand command, long timeout, InvokeCallback callback) {
        if (AgentContext.ins().map.size() > 0) {
            AgentContext.ins().map.forEach((k, v) -> {
                try {
                    server.invokeAsync(v.getChannel(), command, timeout, callback);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemotingSendRequestException e) {
                    e.printStackTrace();
                } catch (RemotingTimeoutException e) {
                    e.printStackTrace();
                } catch (RemotingTooMuchRequestException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void send(Consumer<Channel> consumer) {
        AgentContext.ins().map.forEach((k, v) -> consumer.accept(v.getChannel()));
    }


    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        logger.info("onChannelConnect:{}", remoteAddr);
        AgentChannel ac = new AgentChannel();
        ac.setChannel(channel);
        ac.setRemoteAddr(remoteAddr);
        AgentContext.ins().map.put(remoteAddr, ac);
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        logger.info("onChannelClose:{}", remoteAddr);
        AgentContext.ins().map.remove(remoteAddr);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        logger.info("onChannelException:{}",remoteAddr);
        AgentContext.ins().map.remove(remoteAddr);
        RemotingUtil.closeChannel(channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
    }

    @Override
    public Channel channel(String remoteAddr) {
        return AgentContext.ins().map.get(remoteAddr).getChannel();
    }
}
