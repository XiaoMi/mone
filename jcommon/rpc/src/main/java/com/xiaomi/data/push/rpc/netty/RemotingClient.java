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
