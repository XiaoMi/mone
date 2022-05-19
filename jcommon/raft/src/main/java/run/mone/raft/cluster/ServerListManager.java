/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package run.mone.raft.cluster;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import run.mone.raft.common.RunningConfig;
import run.mone.raft.common.SystemUtils;
import run.mone.raft.common.UtilsAndCommons;
import run.mone.raft.misc.GlobalExecutor;
import run.mone.raft.misc.NetUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

import static run.mone.raft.common.SystemUtils.*;


/**
 * @author mone
 */
@Slf4j
@Component("serverListManager")
public class ServerListManager {

    private List<ServerChangeListener> listeners = new ArrayList<>();

    private List<Server> servers = new ArrayList<>();

    private List<Server> healthyServers = new ArrayList<>();

    @Resource
    private RunningConfig runningConfig;


    public void listen(ServerChangeListener listener) {
        listeners.add(listener);
    }

    @PostConstruct
    public void init() {
        GlobalExecutor.registerServerListUpdater(new ServerListUpdater());
    }

    private List<Server> refreshServerList() {
        List<Server> result = new ArrayList<>();
        if (STANDALONE_MODE) {
            Server server = new Server();
            server.setIp(NetUtils.getLocalAddress());
            server.setServePort(RunningConfig.getServerPort());
            result.add(server);
            return result;
        }

        List<String> serverList = readClusterConf();
        log.debug("SERVER-LIST from mone_raft.conf: {}", result);
        //use system env(127.0.0.1:80,127.0.0.1:81,127.0.0.1:82) mone_raft_cluster_ips
        if (CollectionUtils.isEmpty(serverList)) {
            serverList = SystemUtils.getIPsBySystemEnv(UtilsAndCommons.MONE_RAFT_CLUSTER_IPS_ENV);
            log.debug("SERVER-LIST from system variable: {}", serverList);
        }

        log.debug("SERVER-LIST:{}", serverList);

        if (!CollectionUtils.isEmpty(serverList)) {
            for (int i = 0; i < serverList.size(); i++) {
                String ip;
                int port;
                String server = serverList.get(i);
                if (server.contains(UtilsAndCommons.IP_PORT_SPLITER)) {
                    ip = server.split(UtilsAndCommons.IP_PORT_SPLITER)[0];
                    port = Integer.parseInt(server.split(UtilsAndCommons.IP_PORT_SPLITER)[1]);
                } else {
                    ip = server;
                    port = RunningConfig.getServerPort();
                }

                Server member = new Server();
                member.setIp(ip);
                member.setServePort(port);
                result.add(member);
            }
        }

        return result;
    }


    public List<Server> getServers() {
        return servers;
    }

    private void notifyListeners() {
        GlobalExecutor.notifyServerListChange(() -> listeners.stream().forEach(it -> it.onChangeServerList(servers)));
    }

    /**
     * 读取配置文件或者env中的服务器列表
     */
    public class ServerListUpdater implements Runnable {

        @Override
        public void run() {
            try {
                //查看spring tomcat容器中的port是否暴露出来了
                if (RunningConfig.getServerPort() == 0) {
                    return;
                }

                List<Server> refreshedServers = null;
                if (STANDALONE_MODE) {
                    refreshedServers = new ArrayList<>();
                    Server server = new Server();
                    server.setIp(NetUtils.localServer());
                    server.setServePort(RunningConfig.getServerPort());
                    refreshedServers.add(server);
                } else {
                    //从磁盘加载server列表或者env
                    refreshedServers = refreshServerList();
                }

                if (CollectionUtils.isEmpty(refreshedServers)) {
                    log.warn("refresh raft server list is empty");
                    return;
                }

                List<Server> oldServers = servers;

                boolean changed = false;

                List<Server> newServers = (List<Server>) CollectionUtils.subtract(refreshedServers, oldServers);
                if (CollectionUtils.isNotEmpty(newServers)) {
                    servers.addAll(newServers);
                    changed = true;
                    log.info("server list is updated, new: {} servers: {}", newServers.size(), newServers);
                }

                List<Server> deadServers = (List<Server>) CollectionUtils.subtract(oldServers, refreshedServers);
                if (CollectionUtils.isNotEmpty(deadServers)) {
                    servers.removeAll(deadServers);
                    changed = true;
                    log.info("server list is updated, dead: {}, servers: {}", deadServers.size(), deadServers);
                }
                if (changed) {
                    //通知注册过来的listener(RaftPeerSet)
                    notifyListeners();
                }
            } catch (Exception e) {
                log.info("error while updating server list.", e);
            }
        }
    }


}
