package com.xiaomi.data.push.context;

import com.xiaomi.data.push.bo.ServerType;
import com.xiaomi.data.push.service.state.Fsm;
import com.xiaomi.data.push.service.state.impl.FollowerState;
import com.xiaomi.data.push.service.state.impl.LeaderState;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangzhiyong
 * @date 30/05/2018
 */
@Component
public class ServerContext {

    private static final Logger logger = LoggerFactory.getLogger(ServerContext.class);

    @Getter
    private AtomicReference<ServerType> serverType;

    @Getter
    private AtomicReference<String> leaderIp;

    @Autowired
    private Fsm fsm;


    public static final String STANDALONE = "standalone";


    /**
     * 单机模式 standalone
     * 集群模式 cluster
     */
    @Getter
    @Value("${schedule.server.type}")
    private String type;

    @Getter
    @Value("${server.name}")
    private String serverName;

    /**
     * 选举策略
     */
    @Getter
    @Value("${election.type}")
    private String electionType;


    public ServerContext() {
        this.serverType = new AtomicReference(ServerType.Follower);
        this.leaderIp = new AtomicReference("");
    }


    public boolean isLeader() {
        if (type.equals(STANDALONE)) {
            return true;
        }
        return this.serverType.get() == ServerType.Leader;
    }

    public void changeToLeader() {
        if (this.serverType.compareAndSet(ServerType.Follower, ServerType.Leader)) {
            logger.info("change to leader");
            fsm.changeState("leader");
        }
    }


    public void changeToFollower() {
        if (this.serverType.compareAndSet(ServerType.Leader, ServerType.Follower)) {
            logger.info("change to follower");
            fsm.changeState("follower");
        }
    }


    /**
     * 服务器关闭(kill 的时候会触发)
     */
    @PreDestroy
    public void shutdown() {
        logger.info("server context shutdown begin");
        logger.info("server context shutdown finish");
    }


}
