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

package com.xiaomi.youpin.tesla.agent.processor;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.po.ShellReq;
import com.xiaomi.youpin.tesla.agent.po.ShellRes;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author goodjava@qq.com
 * 执行shell 命令
 */
@Slf4j
@Component
public class ShellProcessor implements NettyRequestProcessor {

    private static final String key = "authorized_keys";

    private static final String clear = "__clear__";

    private static final String init = "__init__";


    @Value("$sshKey")
    private String sshKey;


    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        ShellReq req = remotingCommand.getReq(ShellReq.class);
        log.info("req:{}", new Gson().toJson(req));

        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.shellRes);

        if (req.getShellCmd().startsWith("cd")) {
            String[] ss = req.getShellCmd().split("\\s+");
            req.setPath(ss[1]);
        }

        //清除环境配置
        if (req.getShellCmd().equals(clear)) {
            log.info("clear");
            String cmd = String.format("sed -i '/d' " + key, req.getUser(), req.getPath(), key);
            log.info("clear:{}", cmd);
            ProcessUtils.process(req.getPath() + ".ssh/", cmd);
            return response;
        }

        //初始化一些环境
        if (req.getShellCmd().equals(init)) {
            log.info("init");
            req.setUser("root");

            if (!Files.exists(Paths.get(req.getPath() + ".ssh/"))) {
                String cmd = String.format("su - %s -c 'mkdir -p %s.ssh/'", req.getUser(), req.getPath(), key);
                log.info("create .ssh {}", cmd);
                ProcessUtils.process(req.getPath(), cmd);
            }

            if (!new File(req.getPath() + ".ssh/" + key).exists()) {
                String cmd = String.format("su - %s -c 'touch %s.ssh/%s'", req.getUser(), req.getPath(), key);
                log.info("create:{} {}", cmd, key);
                Pair<Integer, List<String>> res = ProcessUtils.process(req.getPath(), cmd);
                log.info("create key:{}", res);
            }

            String body = req.getCmd();

            if (StringUtils.isEmpty(body)) {
                body = sshKey;
            }

            try {
                Files.write(Paths.get(req.getPath() + ".ssh/tmp"), (System.lineSeparator() + body).getBytes());
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            ProcessUtils.process(req.getPath() + ".ssh/", "cat tmp >> " + key);
            ProcessUtils.process(req.getPath() + ".ssh/", "rm tmp");
            return response;
        }


        Pair<Integer, List<String>> res = ProcessUtils.process(req.getPath(), req.getShellCmd());
        ShellRes sr = ShellRes.builder().code(res.getKey()).data(res.getValue()).build();
        sr.setPath(req.getPath());
        response.setBody(new Gson().toJson(sr).getBytes());
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return AgentCmd.shellReq;
    }
}
