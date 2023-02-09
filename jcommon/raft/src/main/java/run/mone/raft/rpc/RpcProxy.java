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

package run.mone.raft.rpc;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.raft.RaftPeer;
import run.mone.raft.RaftPeerSet;
import run.mone.raft.common.AddressUtils;
import run.mone.raft.common.RunningConfig;
import run.mone.raft.common.UtilsAndCommons;
import run.mone.raft.misc.HttpClient;
import run.mone.raft.pojo.RaftReq;
import run.mone.raft.pojo.RaftRes;
import run.mone.raft.pojo.RpcCmd;
import run.mone.raft.rpc.client.DoceanRpcClient;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author goodjava@qq.com
 * @date 5/10/22
 * 支持长连接和短连接
 */
@Slf4j
public class RpcProxy {

    public static final String API_VOTE = UtilsAndCommons.MONE_NAMING_CONTEXT + "/raft/vote";

    public static final String API_BEAT = UtilsAndCommons.MONE_NAMING_CONTEXT + "/raft/beat";

    public static final String API_GET_PEER = UtilsAndCommons.MONE_NAMING_CONTEXT + "/raft/peer";

    private static Gson gson = new Gson();

    /**
     * 是否使用长连接
     */
    private static boolean docean = true;

    public static void beat(String server, String data, RaftPeerSet peers, DoceanRpcClient client) {
        RaftPeer p = peers.get(server);
        try {
            if (docean) {
                RaftReq req = new RaftReq();
                req.setCmd(RaftReq.BEAT);
                req.setBeat(data);
                client.req(AddressUtils.getRpcAddr(server), RpcCmd.raftReq, gson.toJson(req), responseFuture -> {
                    String data1 = new String(responseFuture.getResponseCommand().getBody());
                    RaftRes raftRes = gson.fromJson(data1, RaftRes.class);
                    RaftPeer peer = gson.fromJson(raftRes.getRes(), RaftPeer.class);
                    peer.setHealth(true);
                    log.info("send beat to server:{} success", server);
                    peers.update(peer);
                });
            } else {
                final String url = buildURL(server, API_BEAT);
                HttpClient.asyncHttpPostLarge(url, null, data, new AsyncCompletionHandler<Integer>() {
                    @Override
                    public Integer onCompleted(Response response) throws Exception {
                        if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
                            log.error("MONE-RAFT beat failed: {}, peer: {}",
                                    response.getResponseBody(), server);
                            return 1;
                        }
                        log.info("send beat to server:{} success", server);
                        RaftPeer peer = JSON.parseObject(response.getResponseBody(), RaftPeer.class);
                        peer.setHealth(true);
                        peers.update(peer);
                        log.debug("receive beat response from: {}", url);
                        return 0;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        log.info("send beat to server:{} failure error:{}", server, t.getMessage());
                    }
                });
            }
        } catch (Throwable ex) {
            log.info("send beat to server:{} failure error:{}", server, ex.getMessage());
            p.setHealth(false);
        }
    }

    public static void vote(String server, Map<String, String> params, RaftPeerSet peers, DoceanRpcClient client) {
        try {
            if (docean) {
                RaftReq req = new RaftReq();
                req.setCmd(RaftReq.VOTE);
                req.setVote(params.get("vote"));
                client.req(AddressUtils.getRpcAddr(server), RpcCmd.raftReq, gson.toJson(req), responseFuture -> {
                    String data = new String(responseFuture.getResponseCommand().getBody());
                    RaftRes raftRes = gson.fromJson(data, RaftRes.class);
                    if (raftRes.getCode() == 0) {
                        RaftPeer peer = gson.fromJson(raftRes.getRes(), RaftPeer.class);
                        peers.decideLeader(peer);
                    } else {
                        log.error(raftRes.getMessage());
                    }
                });
            } else {
                final String url = buildURL(server, API_VOTE);
                HttpClient.asyncHttpPost(url, null, params, new AsyncCompletionHandler<Integer>() {
                    @Override
                    public Integer onCompleted(Response response) throws Exception {
                        if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
                            log.error("MONE-RAFT vote failed: {}, url: {}", response.getResponseBody(), url);
                            return 1;
                        }
                        RaftPeer peer = gson.fromJson(response.getResponseBody(), RaftPeer.class);
                        log.info("received approve from peer: {}", gson.toJson(peer));
                        peers.decideLeader(peer);
                        return 0;
                    }
                });
            }
        } catch (Exception e) {
            log.warn("error while sending vote to server: {}", server);
        }
    }


    public static void getPeer(RaftPeer peer, RaftPeer candidate, RaftPeerSet raftPeerSet, DoceanRpcClient client) {
        try {
            //不是自己并且只获取曾经leader的信息
            if (!Objects.equals(peer, candidate) && peer.state == RaftPeer.State.LEADER) {
                if (docean) {
                    log.info("get peer:{}", peer.ip);
                    RaftReq req = new RaftReq();
                    req.setCmd(RaftReq.PEER);
                    try {
                        client.req(AddressUtils.getRpcAddr(peer.ip), RpcCmd.raftReq, gson.toJson(req), responseFuture -> {
                            String data = new String(responseFuture.getResponseCommand().getBody());
                            RaftRes raftRes = gson.fromJson(data, RaftRes.class);
                            if (raftRes.getCode() == 0) {
                                RaftPeer p = gson.fromJson(raftRes.getRes(), RaftPeer.class);
                                raftPeerSet.update(p);
                            }
                        });
                    } catch (Throwable ex) {
                        log.error(ex.getMessage());
                        peer.state = RaftPeer.State.FOLLOWER;
                    }
                } else {
                    try {
                        String url = buildURL(peer.ip, API_GET_PEER);
                        HttpClient.asyncHttpGet(url, null, new HashMap<>(), new AsyncCompletionHandler<Integer>() {
                            @Override
                            public Integer onCompleted(Response response) throws Exception {
                                if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
                                    log.error("[MONE-RAFT] get peer failed: {}, peer: {}",
                                            response.getResponseBody(), peer.ip);
                                    peer.state = RaftPeer.State.FOLLOWER;
                                    return 1;
                                }
                                raftPeerSet.update(JSON.parseObject(response.getResponseBody(), RaftPeer.class));
                                return 0;
                            }
                        });
                    } catch (Exception e) {
                        peer.state = RaftPeer.State.FOLLOWER;
                        log.error("[MONE-RAFT] error while getting peer from peer: {}", peer.ip);
                    }
                }
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }


    public static String buildURL(String ip, String api) {
        if (!ip.contains(UtilsAndCommons.IP_PORT_SPLITER)) {
            ip = ip + UtilsAndCommons.IP_PORT_SPLITER + RunningConfig.getServerPort();
        }
        return "http://" + ip + RunningConfig.getContextPath() + api;
    }


}
