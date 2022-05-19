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
package run.mone.raft;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.SortedBag;
import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import run.mone.raft.cluster.Server;
import run.mone.raft.cluster.ServerChangeListener;
import run.mone.raft.cluster.ServerListManager;
import run.mone.raft.common.RunningConfig;
import run.mone.raft.common.SystemUtils;
import run.mone.raft.misc.NetUtils;
import run.mone.raft.rpc.RpcProxy;
import run.mone.raft.rpc.client.DoceanRpcClient;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static run.mone.raft.common.SystemUtils.STANDALONE_MODE;


/**
 * @author mone
 * 这个会注册到ServerListManager(用来收取server list变更)
 */
@Component
@DependsOn("serverListManager")
@Slf4j
public class RaftPeerSet implements ServerChangeListener, ApplicationContextAware {

    @Autowired
    private ServerListManager serverListManager;

    @Autowired
    private DoceanRpcClient client;

    private ApplicationContext applicationContext;

    private AtomicLong localTerm = new AtomicLong(0L);

    private RaftPeer leader = null;

    private Map<String, RaftPeer> peers = new HashMap<>();

    private Set<String> sites = new HashSet<>();

    private volatile boolean ready = false;

    public RaftPeerSet() {

    }

    public void setLeader(RaftPeer leader) {
        this.leader = leader;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        serverListManager.listen(this);
    }

    public RaftPeer getLeader() {
        if (STANDALONE_MODE) {
            return local();
        }
        return leader;
    }

    public Set<String> allSites() {
        return sites;
    }

    public boolean isReady() {
        return ready;
    }

    public void remove(List<String> servers) {
        for (String server : servers) {
            peers.remove(server);
        }
    }

    public RaftPeer update(RaftPeer peer) {
        peers.put(peer.ip, peer);
        return peer;
    }

    public boolean isLeader(String ip) {
        if (STANDALONE_MODE) {
            return true;
        }

        if (leader == null) {
            log.warn("[IS LEADER] no leader is available now!");
            return false;
        }

        return StringUtils.equals(leader.ip, ip);
    }

    public Set<String> allServersIncludeMyself() {
        return peers.keySet();
    }

    public Set<String> allServersWithoutMySelf() {
        Set<String> servers = new HashSet<String>(peers.keySet());

        // exclude myself
        servers.remove(local().ip);

        return servers;
    }

    public Collection<RaftPeer> allPeers() {
        return peers.values();
    }

    public int size() {
        return peers.size();
    }

    public RaftPeer decideLeader(RaftPeer candidate) {
        log.info("decideLeader:{} local:{}", candidate, local());
        peers.put(candidate.ip, candidate);

        SortedBag ips = new TreeBag();
        int maxApproveCount = 0;
        String maxApprovePeer = null;
        for (RaftPeer peer : peers.values()) {
            if (StringUtils.isEmpty(peer.voteFor)) {
                continue;
            }

            ips.add(peer.voteFor);
            //每次都记录票数最多的那个
            if (ips.getCount(peer.voteFor) > maxApproveCount) {
                maxApproveCount = ips.getCount(peer.voteFor);
                maxApprovePeer = peer.voteFor;
            }
        }

        /**
         * 超过1半就认为选出leader了
         */
        if (maxApproveCount >= majorityCount()) {
            RaftPeer peer = peers.get(maxApprovePeer);
            peer.state = RaftPeer.State.LEADER;

            if (!Objects.equals(leader, peer)) {
                leader = peer;
                log.info("{} has become the LEADER", leader.ip);
            }
        }

        return leader;
    }

    public RaftPeer makeLeader(RaftPeer candidate) {
        if (!Objects.equals(leader, candidate)) {
            leader = candidate;
            log.info("{} has become the LEADER, local: {}, leader: {}",
                    leader.ip, JSON.toJSONString(local()), JSON.toJSONString(leader));
        }
        for (final RaftPeer peer : peers.values()) {
            RpcProxy.getPeer(peer, candidate, this, client);
        }
        return update(candidate);
    }

    public RaftPeer local() {
        RaftPeer peer = peers.get(NetUtils.localServer());
        if (peer == null && SystemUtils.STANDALONE_MODE) {
            RaftPeer localPeer = new RaftPeer();
            localPeer.ip = NetUtils.localServer();
            localPeer.term.set(localTerm.get());
            peers.put(localPeer.ip, localPeer);
            return localPeer;
        }
        if (peer == null) {
            throw new IllegalStateException("unable to find local peer: " + NetUtils.localServer() + ", all peers: "
                    + Arrays.toString(peers.keySet().toArray()));
        }

        return peer;
    }

    public RaftPeer get(String server) {
        return peers.get(server);
    }

    public int majorityCount() {
        return peers.size() / 2 + 1;
    }

    public void reset() {

        leader = null;

        for (RaftPeer peer : peers.values()) {
            peer.voteFor = null;
        }
    }

    public void setTerm(long term) {
        localTerm.set(term);
    }

    public long getTerm() {
        return localTerm.get();
    }

    public boolean contains(RaftPeer remote) {
        return peers.containsKey(remote.ip);
    }

    @Override
    public void onChangeServerList(List<Server> latestMembers) {

        Map<String, RaftPeer> tmpPeers = new HashMap<>(8);
        for (Server member : latestMembers) {

            if (peers.containsKey(member.getKey())) {
                tmpPeers.put(member.getKey(), peers.get(member.getKey()));
                continue;
            }

            RaftPeer raftPeer = new RaftPeer();
            raftPeer.ip = member.getKey();

            // first time meet the local server:
            if (NetUtils.localServer().equals(member.getKey())) {
                raftPeer.term.set(localTerm.get());
            }

            tmpPeers.put(member.getKey(), raftPeer);
        }

        // replace raft peer set:
        peers = tmpPeers;

        if (RunningConfig.getServerPort() > 0) {
            ready = true;
        }

        log.info("raft peers changed: " + latestMembers);
    }

}
