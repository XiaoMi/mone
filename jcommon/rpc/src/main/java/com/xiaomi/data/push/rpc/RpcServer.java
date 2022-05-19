package com.xiaomi.data.push.rpc;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.data.push.bo.User;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.data.push.common.Service;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.common.Pair;
import com.xiaomi.data.push.rpc.common.RemotingUtil;
import com.xiaomi.data.push.rpc.common.RpcServerVersion;
import com.xiaomi.data.push.rpc.netty.AgentChannel;
import com.xiaomi.data.push.rpc.netty.NettyRemotingServer;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.netty.NettyServerConfig;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import io.netty.channel.Channel;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangzhiyong
 * @date 30/05/2018
 */
public class RpcServer implements Service {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private final String name;

    private final String nacosAddrs;

    private NettyRemotingServer server;

    private PushChannelEventListener listener = new PushChannelEventListener();

    private ExecutorService defaultPool;

    @Setter
    private List<Task> tasks = Lists.newArrayList();

    @Setter
    private List<Pair<Integer, NettyRequestProcessor>> processorList = Lists.newArrayList();

    private ScheduledExecutorService pool;

    private NacosNaming nacosNaming;

    @Setter
    private int listenPort;

    /**
     * 注册到nacos
     */
    private boolean regNacos = true;


    public RpcServer(String nacosAddrs, String name) {
        this(nacosAddrs, name, true);
    }

    public RpcServer(String nacosAddrs, String name, boolean regNacos) {
        logger.info("rpc server version:{}", new RpcVersion());
        this.nacosAddrs = nacosAddrs;
        this.name = name;
        this.regNacos = regNacos;
        this.defaultPool = creatThreadPool(200);
        if (regNacos) {
            nacosNaming = new NacosNaming();
        }
    }

    private ThreadPoolExecutor creatThreadPool(int size) {
        return new ThreadPoolExecutor(size, size,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1000));
    }

    @Override
    @PostConstruct
    public void init() {
        int size = this.tasks.size();
        if (size > 0) {
            pool = Executors.newScheduledThreadPool(size);
            this.tasks.forEach(task -> {
                pool.scheduleWithFixedDelay(() -> {
                    try {
                        logger.info("rpc server client num:{}", listener.clientNum());
                        task.getRunnable().run();
                    } catch (Throwable ex) {
                        //ignore
                    }
                }, 5, task.getDelay(), TimeUnit.SECONDS);
            });

        }
    }

    public void start(Consumer<NettyServerConfig> consumer) {
        logger.info("rpc server start version:{}", new RpcServerVersion());
        NettyServerConfig config = new NettyServerConfig();
        if (this.listenPort != 0) {
            config.setListenPort(this.listenPort);
        }
        consumer.accept(config);
        server = new NettyRemotingServer(config, listener);
        processorList.stream().forEach(it -> {
            ExecutorService p = defaultPool;
            if (it.getObject2().poolSize() > 0) {
                p = creatThreadPool(it.getObject2().poolSize());
            }
            server.registerProcessor(it.getObject1(), it.getObject2(), p);
        });
        server.start();

        //判断是否需要注册到nacos
        if (regNacos) {
            regNacos();
        }

    }


    @Override
    public void start() {
        start(config -> {
        });
    }

    private void regNacos() {
        logger.info("reg service to nacos");
        nacosNaming.setServerAddr(this.nacosAddrs);
        nacosNaming.init();
        //只需要执行一遍,nacos client 会自己重试
        new Thread(() -> Stream
                .generate(() -> true).limit(1)
                .forEach(it -> SafeRun.run(
                        () -> registerInstance(),
                        "reg service", 2000L))).start();
    }

    /**
     * 注册到nacos
     */
    public boolean registerInstance() {
        logger.info("registerInstance");
        try {
            Instance instance = new Instance();
            instance.setEnabled(true);
            instance.setHealthy(true);
            instance.setIp(server.getRegHost());
            instance.setPort(server.getPort());
            instance.setServiceName(name);
            Map<String, String> metaData = Maps.newHashMap();
            metaData.put("ctime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            metaData.put("version", new RpcServerVersion().toString());
            SafeRun.run(() -> metaData.put("hostname", InetAddress.getLocalHost().getHostName()));
            instance.setMetadata(metaData);
            nacosNaming.registerInstance(name, instance);
            logger.info("reg service {} {}:{} success", name, server.getHost(), server.getPort());
            return true;
        } catch (NacosException e) {
            logger.warn("registerInstance error:{}", e.getMessage());
            return false;
        }
    }


    public void sendMessageToAll(RemotingCommand remotingCommand, InvokeCallback callback) {
        listener.sendMessageToAll(this.server, remotingCommand, 1000, callback);
    }

    /**
     * 同步发送
     *
     * @param channel
     * @param req
     * @return
     */
    public RemotingCommand sendMessage(Channel channel, RemotingCommand req) {
        return sendMessage(channel, req, 1000);
    }

    public void tell(Predicate<User> predicate, RemotingCommand req) {
        Channel ch = this.listener.channel(predicate);
        Optional.ofNullable(ch).ifPresent(c -> {
            try {
                this.server.invokeOneway(ch, req, 1000);
            } catch (Throwable ex) {
                logger.error(ex.getMessage());
            }
        });
    }


    public RemotingCommand sendMessage(Channel channel, RemotingCommand req, long timeOut) {
        try {
            return this.server.invokeSync(channel, req, timeOut);
        } catch (Throwable e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    /**
     * 同步调用
     *
     * @param address
     * @param req
     * @param timeOut
     * @return
     */
    public RemotingCommand sendMessage(final String address, RemotingCommand req, final long timeOut) {
        Channel channel = this.server.getChannelEventListener().channel(address);
        if (null == channel) {
            logger.warn("channel is null address:{}", address);
            return null;
        }
        try {
            return this.server.invokeSync(channel, req, timeOut);
        } catch (Throwable e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    /**
     * 发送请求
     *
     * @param address
     * @param req
     * @param timeout
     * @param callback
     */
    public void send(String address, RemotingCommand req, long timeout, InvokeCallback callback) {
        Channel channel = this.server.getChannelEventListener().channel(address);
        if (null != channel) {
            try {
                this.server.invokeAsync(channel, req, timeout, callback);
            } catch (Throwable e) {
                throw new RpcException(e.getMessage(), e);
            }
        }
    }


    public void send(Consumer<Channel> consumer) {
        listener.send(consumer);
    }

    public int clientNum() {
        return listener.clientNum();
    }

    public ArrayList<Channel> clients() {
        return new ArrayList(listener.clients());
    }

    @Override
    public void shutdown() {
        logger.info("rpcserver:{} shutdown", name);
        deregisterInstance();
        if (null != pool) {
            pool.shutdown();
        }
        if (null != server) {
            server.shutdown();
        }
    }


    /**
     * 从nacos上撤销注册
     */
    public void deregisterInstance() {
        try {
            nacosNaming.deregisterInstance(name, server.getHost(), server.getPort());
        } catch (NacosException e) {
            logger.warn("nacos dergister error:{}", e.getMessage());
        }
    }


    public NettyRemotingServer getServer() {
        return server;
    }


    /**
     * 列出客户端列表
     *
     * @return
     */
    public List<String> clientList() {
        return listener.clients().stream().map(it -> {
            Channel c = it;
            if (c.remoteAddress() instanceof InetSocketAddress) {
                InetSocketAddress ia = (InetSocketAddress) it.remoteAddress();
                return ia.getAddress() + ":" + ia.getPort();
            }
            return it.remoteAddress().toString();
        }).collect(Collectors.toList());
    }

    public void closeClient(String address) {
        logger.info("close client:{}", address);
        AgentChannel ch = AgentContext.ins().map.remove(address);
        if (null != ch) {
            RemotingUtil.closeChannel(ch.getChannel());
        }
    }

}
