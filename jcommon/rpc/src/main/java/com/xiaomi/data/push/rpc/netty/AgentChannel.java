package com.xiaomi.data.push.rpc.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;

public class AgentChannel implements Channel {

    private Channel channel;

    /**
     * 外网ip
     */
    private String ip;

    /**
     * 远程地址
     */
    private String remoteAddr;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public ChannelId id() {
        return this.channel.id();
    }

    @Override
    public EventLoop eventLoop() {
        return this.channel.eventLoop();
    }

    @Override
    public Channel parent() {
        return this.channel.parent();
    }

    @Override
    public ChannelConfig config() {
        return this.channel.config();
    }

    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return this.channel.isRegistered();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public ChannelMetadata metadata() {
        return this.channel.metadata();
    }

    @Override
    public SocketAddress localAddress() {
        return this.channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public ChannelFuture closeFuture() {
        return this.channel.closeFuture();
    }

    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public long bytesBeforeUnwritable() {
        return this.channel.bytesBeforeUnwritable();
    }

    @Override
    public long bytesBeforeWritable() {
        return this.channel.bytesBeforeWritable();
    }

    @Override
    public Unsafe unsafe() {
        return this.channel.unsafe();
    }

    @Override
    public ChannelPipeline pipeline() {
        return this.channel.pipeline();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.channel.alloc();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress) {
        return this.channel.bind(socketAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress) {
        return this.channel.connect(socketAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
        return this.channel.connect(socketAddress,socketAddress1);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.channel.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.channel.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.channel.deregister();
    }

    @Override
    public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return this.channel.bind(socketAddress,channelPromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
        return this.channel.connect(socketAddress,channelPromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
        return this.channel.connect(socketAddress,socketAddress1,channelPromise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise channelPromise) {
        return this.channel.disconnect(channelPromise);
    }

    @Override
    public ChannelFuture close(ChannelPromise channelPromise) {
        return this.channel.close(channelPromise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise channelPromise) {
        return this.channel.deregister(channelPromise);
    }

    @Override
    public Channel read() {
        return this.channel.read();
    }

    @Override
    public ChannelFuture write(Object o) {
        return this.channel.write(o);
    }

    @Override
    public ChannelFuture write(Object o, ChannelPromise channelPromise) {
        return this.channel.write(o,channelPromise);
    }

    @Override
    public Channel flush() {
        return this.channel.flush();
    }

    @Override
    public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
        return this.channel.writeAndFlush(o,channelPromise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object o) {
        return this.channel.writeAndFlush(o);
    }

    @Override
    public ChannelPromise newPromise() {
        return this.channel.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return this.channel.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return this.channel.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable throwable) {
        return this.channel.newFailedFuture(throwable);
    }

    @Override
    public ChannelPromise voidPromise() {
        return this.channel.voidPromise();
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
        return this.channel.attr(attributeKey);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
        return this.channel.hasAttr(attributeKey);
    }

    @Override
    public int compareTo(Channel o) {
        return this.channel.compareTo(o);
    }
}
