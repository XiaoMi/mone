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
import com.xiaomi.data.push.rpc.exception.RemotingConnectException;
import com.xiaomi.data.push.rpc.exception.RemotingSendRequestException;
import com.xiaomi.data.push.rpc.exception.RemotingTimeoutException;
import com.xiaomi.data.push.rpc.exception.RemotingTooMuchRequestException;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;

import java.util.concurrent.ExecutorService;


public interface RemotingClient extends RemotingService {


    public RemotingCommand invokeSync(final String addr, final RemotingCommand request,
                                      final long timeoutMillis) throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException;


    public void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
                            final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    public void invokeAsync(final RemotingCommand request, final long timeoutMillis,
                            final InvokeCallback invokeCallback);


    public void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingConnectException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;


    public void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                                  final ExecutorService executor);


    public boolean isChannelWriteable(final String addr);
}
