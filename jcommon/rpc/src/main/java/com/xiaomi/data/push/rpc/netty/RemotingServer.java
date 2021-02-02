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

package com.xiaomi.data.push.rpc.netty;

import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.exception.RemotingSendRequestException;
import com.xiaomi.data.push.rpc.exception.RemotingTimeoutException;
import com.xiaomi.data.push.rpc.exception.RemotingTooMuchRequestException;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;


public interface RemotingServer extends RemotingService {

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);


    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);


    int localListenPort();


    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);


    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
                               final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
            RemotingTimeoutException;


    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;


    void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException;

}
