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

package run.mone.raft.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import run.mone.raft.RaftPeer;
import run.mone.raft.controller.RaftController;
import run.mone.raft.pojo.RaftReq;
import run.mone.raft.pojo.RaftRes;
import run.mone.raft.pojo.RpcCmd;
import run.mone.raft.rpc.client.DoceanRpcClient;

import java.nio.charset.StandardCharsets;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class RaftProcessor implements NettyRequestProcessor {

    private RaftController raftController;

    private DoceanRpcClient rpcClient;

    private Gson gson = new Gson();

    public RaftProcessor(RaftController raftController, DoceanRpcClient rpcClient) {
        this.raftController = raftController;
        this.rpcClient = rpcClient;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
        RaftReq req = remotingCommand.getReq(RaftReq.class);
        String res = "";
        log.debug("raft processor cmd:{} {}", req.getCmd(), req);

        RemotingCommand response = RemotingCommand.createResponseCommand(RpcCmd.raftRes);
        RaftRes nacosRes = new RaftRes();
        nacosRes.setCmd(req.getCmd());
        try {
            //投票过来
            if (req.getCmd().equals(RaftReq.VOTE)) {
                RaftPeer peer = gson.fromJson(req.getVote(), RaftPeer.class);
                res = gson.toJson(raftController.vote(peer));
            }

            //leader发送过来的心跳包
            if (req.getCmd().equals(RaftReq.BEAT)) {
                JSONObject beat = JSON.parseObject(req.getBeat());
                res = raftController.beat(beat).toJSONString();
            }

            //获取peer信息
            if (req.getCmd().equals(RaftReq.PEER)) {
                res = raftController.getPeer().toJSONString();
            }
        } catch (Throwable ex) {
            res = "";
            nacosRes.setCode(500);
            nacosRes.setMessage(ex.getMessage());
        }

        nacosRes.setRes(res);
        response.setBody(new Gson().toJson(nacosRes).getBytes(StandardCharsets.UTF_8));
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int poolSize() {
        return 20;
    }
}
