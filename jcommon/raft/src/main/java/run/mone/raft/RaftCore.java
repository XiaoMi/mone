/*
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
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.raft.misc.GlobalExecutor;
import run.mone.raft.misc.NetUtils;
import run.mone.raft.rpc.RpcProxy;
import run.mone.raft.rpc.client.DoceanRpcClient;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static run.mone.raft.common.SystemUtils.STANDALONE_MODE;


/**
 * @author mone
 */
@Component
@Slf4j
public class RaftCore {

    private Gson gson = new Gson();

    @Autowired
    private RaftPeerSet peers;

    @Autowired
    private DoceanRpcClient client;


    @PostConstruct
    public void init() throws Exception {
        log.info("initializing Raft sub-system");
        GlobalExecutor.registerMasterElection(new MasterElection());
        GlobalExecutor.registerHeartbeat(new HeartBeat());
        log.info("timer started: leader timeout ms: {}, heart-beat timeout ms: {}",
                GlobalExecutor.LEADER_TIMEOUT_MS, GlobalExecutor.HEARTBEAT_INTERVAL_MS);
    }


    /**
     * 选举
     */
    public class MasterElection implements Runnable {
        @Override
        public void run() {
            try {
                if (!peers.isReady()) {
                    return;
                }

                if (STANDALONE_MODE) {
                    peers.local().setState(RaftPeer.State.LEADER);
                }

                RaftPeer local = peers.local();
                //收到心跳包后,这个时间一直会修改,造成其实不会进行选举
                local.leaderDueMs -= GlobalExecutor.TICK_PERIOD_MS;

                if (local.leaderDueMs > 0) {
                    return;
                }

                // reset timeout
                local.resetLeaderDue();
                local.resetHeartbeatDue();

                //进行选主
                sendVote();
            } catch (Exception e) {
                log.warn("[RAFT] error while master election {}", e);
            }

        }

        public void sendVote() {
            if (STANDALONE_MODE) {
                return;
            }
            RaftPeer local = peers.get(NetUtils.localServer());
            log.info("leader timeout, start voting,leader: {}, term: {}",
                    gson.toJson(getLeader()), local.term);

            peers.reset();

            local.term.incrementAndGet();
            //投票先推荐自己
            local.voteFor = local.ip;
            local.state = RaftPeer.State.CANDIDATE;

            Map<String, String> params = new HashMap<>(1);
            params.put("vote", gson.toJson(local));

            for (final String server : peers.allServersWithoutMySelf()) {
                RpcProxy.vote(server, params, peers, client);
            }
        }
    }

    /**
     * 收到投票的处理
     *
     * @param remote
     * @return
     */
    public synchronized RaftPeer receivedVote(RaftPeer remote) {
        if (!peers.contains(remote)) {
            throw new IllegalStateException("can not find peer: " + remote.ip);
        }

        RaftPeer local = peers.get(NetUtils.localServer());
        //如果对方的term小于我,我就建议对方投我推荐人的票
        if (remote.term.get() <= local.term.get()) {
            String msg = "received illegitimate vote" +
                    ", voter-term:" + remote.term + ", votee-term:" + local.term;

            log.info(msg);
            //如果我没有推荐人,则直接推荐我自己
            if (StringUtils.isEmpty(local.voteFor)) {
                local.voteFor = local.ip;
            }

            return local;
        }

        local.resetLeaderDue();

        local.state = RaftPeer.State.FOLLOWER;
        //投票给对方
        local.voteFor = remote.ip;
        local.term.set(remote.term.get());
        log.info("vote {} as leader, term: {}", remote.ip, remote.term);
        return local;
    }

    public class HeartBeat implements Runnable {
        @Override
        public void run() {
            try {
                if (!peers.isReady()) {
                    return;
                }

                if (STANDALONE_MODE) {
                    peers.local().setState(RaftPeer.State.LEADER);
                }

                RaftPeer local = peers.local();
                local.heartbeatDueMs -= GlobalExecutor.TICK_PERIOD_MS;
                if (local.heartbeatDueMs > 0) {
                    return;
                }

                local.resetHeartbeatDue();

                sendBeat();
            } catch (Exception e) {
                log.warn("[RAFT] error while sending beat {}", e);
            }

        }

        public void sendBeat() throws IOException {
            if (STANDALONE_MODE) {
                log.info("leader send beat:{} {}", peers.local(), "standlone");
                return;
            }
            RaftPeer local = peers.local();
            //只有leader可以发beta包
            if (local.state != RaftPeer.State.LEADER) {
                return;
            }
            log.info("send beat begin");
            local.resetLeaderDue();
            for (final String server : peers.allServersWithoutMySelf()) {
                RpcProxy.beat(server, JSON.toJSONString(local), peers, client);
            }
        }
    }

    public RaftPeer receivedBeat(JSONObject beat) throws Exception {
        log.info("receivedBeat:{}", beat);
        if (!peers.isReady()) {
            return null;
        }
        final RaftPeer local = peers.local();
        final RaftPeer remote = new RaftPeer();
        remote.ip = beat.getString("ip");
        remote.state = RaftPeer.State.valueOf(beat.getString("state"));
        remote.term.set(beat.getLongValue("term"));
        remote.heartbeatDueMs = beat.getLongValue("heartbeatDueMs");
        remote.leaderDueMs = beat.getLongValue("leaderDueMs");
        remote.voteFor = beat.getString("voteFor");

        if (remote.state != RaftPeer.State.LEADER) {
            log.info("[RAFT] invalid state from master, state: {}, remote peer: {}",
                    remote.state, JSON.toJSONString(remote));
            throw new IllegalArgumentException("invalid state from master, state: " + remote.state);
        }

        if (local.term.get() > remote.term.get()) {
            log.info("[RAFT] out of date beat, beat-from-term: {}, beat-to-term: {}, remote peer: {}, and leaderDueMs: {}"
                    , remote.term.get(), local.term.get(), gson.toJson(remote), local.leaderDueMs);
            throw new IllegalArgumentException("out of date beat, beat-from-term: " + remote.term.get()
                    + ", beat-to-term: " + local.term.get());
        }

        if (local.state != RaftPeer.State.FOLLOWER) {
            log.info("[RAFT] make remote as leader, remote peer: {}", JSON.toJSONString(remote));
            // mk follower
            local.state = RaftPeer.State.FOLLOWER;
            local.voteFor = remote.ip;
        }
        //重置选举时间,避免自己这边发送投票
        local.resetLeaderDue();
        local.resetHeartbeatDue();
        peers.makeLeader(remote);

        if (local.term.get() + 100 > remote.term.get()) {
            getLeader().term.set(remote.term.get());
            local.term.set(getLeader().term.get());
        } else {
            local.term.addAndGet(100);
        }

        return local;
    }


    public void setTerm(long term) {
        peers.setTerm(term);
    }

    public boolean isLeader(String ip) {
        return peers.isLeader(ip);
    }

    public boolean isLeader() {
        return peers.isLeader(NetUtils.localServer());
    }


    public RaftPeer getLeader() {
        return peers.getLeader();
    }

    public List<RaftPeer> getPeers() {
        return new ArrayList<>(peers.allPeers());
    }

    public RaftPeerSet getPeerSet() {
        return peers;
    }

    public void setPeerSet(RaftPeerSet peerSet) {
        peers = peerSet;
    }


}
