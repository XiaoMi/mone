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

package com.xiaomi.youpin.mischedule.service;

import com.alipay.remoting.rpc.RpcServer;
import com.alipay.sofa.jraft.Lifecycle;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public class ElectionNode implements Lifecycle<ElectionNodeOptions> {

    // private static final Logger LOG = LoggerFactory.getLogger(ElectionNode.class);

    private RaftGroupService raftGroupService;
    private Node node;
    private ElectionOnlyStateMachine fsm;

    private boolean started;

    @Setter
    private LeaderStateListener leaderStateListener;

    @Override
    public boolean init(final ElectionNodeOptions opts) {
        if (this.started) {
            log.info("[ElectionNode: {}] already started.");
            return true;
        }
        // node options
        NodeOptions nodeOpts = opts.getNodeOptions();
        if (nodeOpts == null) {
            nodeOpts = new NodeOptions();
        }
        this.fsm = new ElectionOnlyStateMachine();

        if (null != this.leaderStateListener) {
            this.fsm.addLeaderStateListener(this.leaderStateListener);
        }

        nodeOpts.setFsm(this.fsm);
        final Configuration initialConf = new Configuration();
        if (!initialConf.parse(opts.getInitialServerAddressList())) {
            throw new IllegalArgumentException("Fail to parse initConf: " + opts.getInitialServerAddressList());
        }
        // 设置初始集群配置
        nodeOpts.setInitialConf(initialConf);
        final String dataPath = opts.getDataPath();
        try {
            FileUtils.forceMkdir(new File(dataPath));
        } catch (final IOException e) {
            log.error("Fail to make dir for dataPath {}.", dataPath);
            return false;
        }
        // 设置存储路径
        // 日志, 必须
        nodeOpts.setLogUri(Paths.get(dataPath, "log").toString());
        // 元信息, 必须
        nodeOpts.setRaftMetaUri(Paths.get(dataPath, "meta").toString());
        // 纯选举场景不需要设置 snapshot, 不设置可避免启动 snapshot timer
        // nodeOpts.setSnapshotUri(Paths.get(dataPath, "snapshot").toString());

        final String groupId = opts.getGroupId();
        final PeerId serverId = new PeerId();
        if (!serverId.parse(opts.getServerAddress())) {
            throw new IllegalArgumentException("Fail to parse serverId: " + opts.getServerAddress());
        }
        final RpcServer rpcServer = new RpcServer(serverId.getPort());
        // 注册 raft 处理器
        RaftRpcServerFactory.addRaftRequestProcessors(rpcServer);
        // 初始化 raft group 服务框架
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOpts, rpcServer);
        // 启动
        this.node = this.raftGroupService.start();
        if (this.node != null) {
            this.started = true;
        }
        return this.started;
    }

    @Override
    public void shutdown() {
        if (!this.started) {
            return;
        }
        if (this.raftGroupService != null) {
            this.raftGroupService.shutdown();
            try {
                this.raftGroupService.join();
            } catch (final InterruptedException e) {
                log.error(e.getMessage());
            }
        }
        this.started = false;
        log.info("[RegionEngine] shutdown successfully: {}.", this);
    }

    public Node getNode() {
        return node;
    }

    public ElectionOnlyStateMachine getFsm() {
        return fsm;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isLeader() {
        return this.fsm.isLeader();
    }

    public void addLeaderStateListener(final LeaderStateListener listener) {
        this.fsm.addLeaderStateListener(listener);
    }
}
