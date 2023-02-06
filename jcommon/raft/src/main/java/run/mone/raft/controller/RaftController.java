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
package run.mone.raft.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.mone.raft.RaftCore;
import run.mone.raft.RaftPeer;
import run.mone.raft.common.IoUtils;
import run.mone.raft.common.UtilsAndCommons;
import run.mone.raft.common.WebUtils;
import run.mone.raft.misc.NetUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author mone
 * <p>
 * web 的raft入口  投票+投票接受(vote)   心跳+心跳接受(beat)   peer
 * /mone/raft
 */
@RestController
@RequestMapping({UtilsAndCommons.MONE_SERVER_CONTEXT + "/raft"})
public class RaftController {

    @Autowired
    private RaftCore raftCore;

    /**
     * 收到投票
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/vote")
    public JSONObject vote(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RaftPeer peer = raftCore.receivedVote(
                JSON.parseObject(WebUtils.required(request, "vote"), RaftPeer.class));
        return JSON.parseObject(JSON.toJSONString(peer));
    }

    public RaftPeer vote(RaftPeer raftPeer) throws Exception {
        RaftPeer peer = raftCore.receivedVote(raftPeer);
        return peer;
    }

    /**
     * /mone/raft/version
     *
     * @return
     */
    @GetMapping("/version")
    public String version() {
        return "0.0.1:2022-05-09";
    }

    /**
     * 收到心跳包
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/beat")
    public JSONObject beat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String entity = IoUtils.toString(request.getInputStream(), "UTF-8");
        JSONObject beat = JSON.parseObject(entity);
        RaftPeer peer = raftCore.receivedBeat(beat);
        return JSON.parseObject(JSON.toJSONString(peer));
    }

    public JSONObject beat(JSONObject beat) throws Exception {
        RaftPeer peer = raftCore.receivedBeat(beat);
        return JSON.parseObject(JSON.toJSONString(peer));
    }

    @GetMapping("/peer")
    public JSONObject getPeer(HttpServletRequest request, HttpServletResponse response) {
        return getPeer();
    }

    public JSONObject getPeer() {
        List<RaftPeer> peers = raftCore.getPeers();
        RaftPeer peer = null;
        for (RaftPeer peer1 : peers) {
            if (StringUtils.equals(peer1.ip, NetUtils.localServer())) {
                peer = peer1;
            }
        }
        if (peer == null) {
            peer = new RaftPeer();
            peer.ip = NetUtils.localServer();
        }
        return JSON.parseObject(JSON.toJSONString(peer));
    }


    @GetMapping("/state")
    public JSONObject state(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "application/json; charset=" + getAcceptEncoding(request));
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Content-Encode", "gzip");
        JSONObject result = new JSONObject();
        result.put("peers", raftCore.getPeers());
        return result;
    }


    @GetMapping("/leader")
    public JSONObject getLeader(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        result.put("leader", JSONObject.toJSONString(raftCore.getLeader()));
        return result;
    }


    public static String getAcceptEncoding(HttpServletRequest req) {
        String encode = StringUtils.defaultIfEmpty(req.getHeader("Accept-Charset"), "UTF-8");
        encode = encode.contains(",") ? encode.substring(0, encode.indexOf(",")) : encode;
        return encode.contains(";") ? encode.substring(0, encode.indexOf(";")) : encode;
    }
}
