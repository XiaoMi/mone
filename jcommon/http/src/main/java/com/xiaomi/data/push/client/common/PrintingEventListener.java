package com.xiaomi.data.push.client.common;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 * @date 2023/8/18 09:35
 */
@Slf4j
public class PrintingEventListener extends EventListener {

    long callStartNanos;

    long callId;

    private Stopwatch sw;

    public PrintingEventListener(long callId, long callStartNanos) {
        this.callId = callId;
        this.callStartNanos = callStartNanos;
        sw = Stopwatch.createStarted();
    }


    public static final Factory FACTORY = new Factory() {

        final AtomicLong nextCallId = new AtomicLong(1L);

        @Override
        public EventListener create(Call call) {
            long callId = nextCallId.getAndIncrement();
            log.debug("{} {}", callId, call.request().url());
            return new PrintingEventListener(callId, System.nanoTime());
        }
    };

    private void printEvent(String name) {
        long nowNanos = System.nanoTime();
        if (name.equals("callStart")) {
            callStartNanos = nowNanos;
        }
        log.debug("{} {} {}", callId, sw.elapsed(TimeUnit.MILLISECONDS), name);
    }

    @Override
    public void callStart(Call call) {
        printEvent("callStart");
    }

    @Override
    public void proxySelectStart(Call call, HttpUrl url) {
        printEvent("proxySelectStart");
    }

    @Override
    public void proxySelectEnd(Call call, HttpUrl url, List<Proxy> proxies) {
        printEvent("proxySelectEnd");
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        printEvent("dnsStart");
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        printEvent("dnsEnd");
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        printEvent("connectStart----------------->");
    }

    @Override
    public void secureConnectStart(Call call) {
        printEvent("secureConnectStart");
    }

    @Override
    public void secureConnectEnd(Call call, Handshake handshake) {
        printEvent("secureConnectEnd");
    }

    @Override
    public void connectEnd(
            Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        printEvent("connectEnd");
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy,
                              Protocol protocol, IOException ioe) {
        printEvent("connectFailed");
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        printEvent("connectionAcquired");
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        printEvent("connectionReleased");
    }

    @Override
    public void requestHeadersStart(Call call) {
        printEvent("requestHeadersStart");
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        printEvent("requestHeadersEnd");
    }

    @Override
    public void requestBodyStart(Call call) {
        printEvent("requestBodyStart");
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        printEvent("requestBodyEnd");
    }

    @Override
    public void requestFailed(Call call, IOException ioe) {
        printEvent("requestFailed");
    }

    @Override
    public void responseHeadersStart(Call call) {
        printEvent("responseHeadersStart");
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        printEvent("responseHeadersEnd");
    }

    @Override
    public void responseBodyStart(Call call) {
        printEvent("responseBodyStart");
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        printEvent("responseBodyEnd");
    }

    @Override
    public void responseFailed(Call call, IOException ioe) {
        printEvent("responseFailed");
    }

    @Override
    public void callEnd(Call call) {
        printEvent("callEnd");
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        printEvent("callFailed");
    }

    @Override
    public void canceled(Call call) {
        printEvent("canceled");
    }

}
