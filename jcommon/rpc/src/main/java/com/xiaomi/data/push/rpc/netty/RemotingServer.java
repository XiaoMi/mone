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
