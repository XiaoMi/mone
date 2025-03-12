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

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.data.push.bo.ClientInfo;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.common.Service;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.common.RpcClientVersion;
import com.xiaomi.data.push.rpc.netty.NettyClientConfig;
import com.xiaomi.data.push.rpc.netty.NettyRemotingClient;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangzhiyong
 * @date 30/05/2018
 */
@Data
public class RpcClient implements Service {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private NettyRemotingClient client;

    private final String nacosAddrs;

    private final String serverName;

    private String serverIp;

    private int serverPort;

    @Getter
    private NacosNaming nacosNaming;

    @Setter
    private List<Pair<Integer, NettyRequestProcessor>> processorList = Lists.newLinkedList();

    private AtomicReference<String> serverAddrs = new AtomicReference<>("");

    private AtomicReference<List<String>> serverList = new AtomicReference<>(Lists.newArrayList());

    private int pooSize = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private ExecutorService defaultPool = new ThreadPoolExecutor(pooSize, pooSize,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000));

    private ScheduledExecutorService pool;

    private AtomicBoolean init = new AtomicBoolean(false);

    @Setter
    private boolean clearServerAddr = true;

    @Getter
    private String clientIp;

    @Getter
    private int clientPort;

    @Setter
    private List<Task> tasks = Lists.newArrayList();

    @Setter
    private boolean reconnection = true;

    @Setter
    private ClientInfo clientInfo;

    /**
     * 使用nacos获取服务器信息
     */
    @Setter
    private boolean useNacos = true;

    public static CountDownLatch startLatch = new CountDownLatch(1);

    @SneakyThrows
    public void waitStarted() {
        startLatch.await();
    }


    public RpcClient(String nacosAddrs, String serverName) {
        logger.info("rpc client version:{}", new RpcVersion());
        this.nacosAddrs = nacosAddrs;
        this.serverName = serverName;
        nacosNaming = new NacosNaming();
    }

    /**
     * 支持直接裸连server 服务器
     *
     * @param serverAddr
     */
    public RpcClient(String serverAddr) {
        this.serverAddrs.set(serverAddr);
        this.useNacos = false;
        this.serverName = "";
        this.nacosAddrs = "";
    }


    public String getServerAddrs() {
        String address = this.serverAddrs.get();
        return address;
    }

    public void start(Consumer<NettyClientConfig> consumer) {
        logger.info("client version:{}", new RpcClientVersion());
        NettyClientConfig config = new NettyClientConfig();
        config.setReconnection(this.reconnection);
        consumer.accept(config);
        client = new NettyRemotingClient(config);
        client.setGetAddrsFunc((str) -> getServerAddrs());
        //注册processor
        registerProcessor();
        //并没有connect 只是初始化了下(init)
        client.start();

        if (useNacos) {
            logger.info("nacos addr:{}", this.nacosAddrs);

            nacosNaming.setServerAddr(this.nacosAddrs);
            nacosNaming.init();

            logger.info("refresh server addrs");
            refreshServerAddrs();

            logger.info("reg");
            Executors.newSingleThreadExecutor().submit(() -> reg());
        }
    }

    @Override
    public void start() {
        start(config -> {
        });
    }


    /**
     * 需要二次注册的时候
     */
    public void registerProcessor() {
        processorList.forEach(it -> client.registerProcessor(it.getObject1(), it.getObject2(), defaultPool));
    }


    private void reg() {
        Optional.ofNullable(this.clientInfo).ifPresent(it -> {
            try {
                Instance instance = new Instance();
                instance.setIp(it.getIp());
                instance.setPort(it.getPort());
                Map<String, String> meta = Maps.newHashMap();
                meta.put("ctime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                meta.put("version", new RpcVersion().toString() + ":" + it.getVersion());
                SafeRun.run(() -> meta.put("hostname", InetAddress.getLocalHost().getHostName()));
                instance.setMetadata(meta);
                nacosNaming.registerInstance(it.getName(), instance);
                logger.info("reg:{}", it.getName());
            } catch (NacosException e) {
                logger.error(e.getMessage());
            }
        });
    }


    private void initAddr() {
        try {
            //在nacos查找ip列表并且是enable的
            List<Instance> list = nacosNaming.getAllInstances(serverName)
                    .stream().filter(it -> it.isHealthy() && it.isEnabled()).collect(Collectors.toList());

            if (list.size() > 0) {
                List<String> addrList = list.stream().map(it -> {
                    String addr = it.getIp() + ":" + it.getPort();
                    return addr;
                }).collect(Collectors.toList());
                this.serverList.set(addrList);
            }

            if (list.size() > 0) {
                String serverIp = list.get(0).getIp();
                int serverPort = list.get(0).getPort();
                logger.info("serverIp:{} serverPort:{}", serverIp, serverPort);
                String old = this.serverAddrs.get();
                this.serverAddrs.set(serverIp + ":" + serverPort);
                if (StringUtils.isNotEmpty(old) && !old.equals(this.serverAddrs.get())) {
                    //老的ip已经不在了,或者下线了,则选择切换新的ip
                    if (!list.stream().filter(it -> (it.getIp() + ":" + it.getPort()).equals(old)).findAny().isPresent()) {
                        logger.info("server ip change:{}->{}", old, this.serverAddrs.get());
                        //关闭链接尝试连接新的ip
                        RpcClient.this.logout();
                    } else {
                        this.serverAddrs.set(old);
                    }
                }
            } else {
                if (clearServerAddr) {
                    logger.warn("server size = 0,serverName:{}", serverName);
                    this.serverAddrs.set("");
                }
            }
        } catch (Throwable e) {
            logger.warn("initAddr erro:" + e.getMessage());
        }
    }

    //异步刷新
    private void refreshServerAddrs() {
        new Thread(() -> {
            while (true) {
                initAddr();
                sleep(5);
            }
        }).start();
    }


    private static void sleep(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
    }


    /**
     * 默认客户端直接拉起
     */
    @Override
    @PostConstruct
    public void init() {
        int size = this.tasks.size();
        if (size > 0) {
            if (init.compareAndSet(false, true)) {
                pool = Executors.newScheduledThreadPool(size);
                this.tasks.forEach(task -> {
                    pool.scheduleAtFixedRate(() -> {
                        try {
                            task.getRunnable().run();
                        } catch (Throwable ex) {
                            logger.warn(ex.getMessage());
                        }
                    }, 5, task.getDelay(), TimeUnit.SECONDS);
                });
            }
        }
    }


    @Override
    public void shutdown() {
        if (null != this.client) {
            try {
                pool.shutdown();
                client.shutdown();
            } catch (Exception ex) {
                logger.info("rpc client shutdwon error:{}", ex.getMessage());
            }
        }
    }

    /**
     * 异步发送消息
     *
     * @param addr
     * @param code
     * @param message
     */
    public void sendMessage(String addr, int code, String message) {
        if (StringUtils.isEmpty(addr)) {
            logger.warn("addr is empty");
            return;
        }
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            req.setBody(message.getBytes());
            this.client.invokeAsync(addr, req, TimeUnit.SECONDS.toMillis(3), responseFuture -> {
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * one way
     * 不需要返回结果
     *
     * @param req
     */
    public void sendMessage(RemotingCommand req) {
        String addr = this.serverAddrs.get();
        if (StringUtils.isEmpty(addr)) {
            logger.warn("send message addr is null");
            return;
        }
        try {
            this.client.invokeOneway(addr, req, req.getTimeout());
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    /**
     * Send messages to all server nodes.
     *
     * @param req
     */
    public void sendToAllMessage(RemotingCommand req) {
        this.serverList.get().stream().forEach(addr -> {
            if (StringUtils.isEmpty(addr)) {
                logger.warn("send message addr is null");
                return;
            }
            try {
                this.client.invokeOneway(addr, req, req.getTimeout());
            } catch (Exception e) {
                throw new RpcException(e.getMessage(), e);
            }
        });
    }

    public void sendToAllMessage(int code, byte[] body, InvokeCallback invokeCallback) {
        this.serverList.get().stream().forEach(addr -> {
            if (StringUtils.isEmpty(addr)) {
                logger.warn("send message addr is null");
                return;
            }
            try {
                RemotingCommand req = RemotingCommand.createRequestCommand(code);
                req.setBody(body);
                this.client.invokeAsync(addr, req, req.getTimeout(), invokeCallback);
            } catch (Exception e) {
                throw new RpcException(e.getMessage(), e);
            }
        });
    }

    /**
     * Send messages according to your own routing rules.
     *
     * @param code
     * @param body
     * @param function
     * @param invokeCallback
     */
    public void sendMessageWithSelect(int code, byte[] body, Function<List<String>, String> function, InvokeCallback invokeCallback) {
        List<String> list = this.serverList.get();
        String addr = function.apply(list);
        if (StringUtils.isEmpty(addr)) {
            logger.warn("send message addr is null");
            return;
        }
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            req.setBody(body);
            this.client.invokeAsync(addr, req, req.getTimeout(), invokeCallback);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    public void tell(String addr, int code, String message) {
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            req.setBody(message.getBytes());
            this.client.invokeOneway(addr, req, req.getTimeout());
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

    public void tell(String addr, int code, byte[] message, Consumer<RemotingCommand> consumer) {
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            consumer.accept(req);
            req.setBody(message);
            this.client.invokeOneway(addr, req, req.getTimeout());
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    public void oneway(String addr, RemotingCommand req) {
        try {
            this.client.createChannel(addr, false);
            this.client.invokeOneway(addr, req, 1000L);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    public RemotingCommand sendMessage(String addr, int code, String message, long timeout) {
        return sendMessage(addr, code, message, timeout, false);

    }

    public RemotingCommand sendMessage(String addr, int code, String message, long timeout, boolean createChannel) {
        return sendMessage(addr, code, message, timeout, createChannel, req -> {
        });
    }

    public RemotingCommand sendMessage(String addr, int code, String message, long timeout, boolean createChannel, Consumer<RemotingCommand> consumer) {
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            consumer.accept(req);
            req.setBody(message.getBytes());
            return this.client.invokeSync(addr, req, timeout, createChannel);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public RemotingCommand sendMessage(String addr, int code, byte[] message, long timeout, boolean createChannel) {
        return sendMessage(addr, code, message, timeout, createChannel, (req) -> {
        });
    }

    public RemotingCommand sendMessage(String addr, int code, byte[] message, long timeout, boolean createChannel, Consumer<RemotingCommand> consumer) {
        try {
            RemotingCommand req = RemotingCommand.createRequestCommand(code);
            consumer.accept(req);
            req.setBody(message);
            return this.client.invokeSync(addr, req, timeout, createChannel);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public void sendMessage(String addr, RemotingCommand req, InvokeCallback callback) {
        try {
            this.client.invokeAsync(addr, req, TimeUnit.SECONDS.toMillis(3), callback);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 向服务器发送ping信息(有连接则直接发,没有就先新建连接)
     *
     * @param addr
     */
    public void ping(String addr) {
        this.sendMessage(addr, RpcCmd.pingReq, "ping");
    }


    /**
     * 关闭所有建立的连接
     */
    public void logout() {
        logger.info("client logout");
        this.client.closeAllChannel();
    }

    public NettyRemotingClient getClient() {
        return client;
    }


    public NettyRequestProcessor getProcessor(Integer cmd) {
        return this.processorList.stream().filter(it -> it.getObject1().equals(cmd)).map(it -> it.getObject2()).findAny()
                .orElse(new NettyRequestProcessor() {
                    @Override
                    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
                        return null;
                    }

                    @Override
                    public boolean rejectRequest() {
                        return false;
                    }
                });
    }

    @Override
    public RemotingCommand call(RemotingCommand command) {
        return null;
    }
}
