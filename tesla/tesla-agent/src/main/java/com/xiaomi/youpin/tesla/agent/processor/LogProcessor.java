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
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.mone.file.FileUtils;
import com.xiaomi.mone.file.MoneFile;
import com.xiaomi.mone.file.ReadResult;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.LogReq;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component
public class LogProcessor implements NettyRequestProcessor {


    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws Exception {
        LogReq req = remotingCommand.getReq(LogReq.class);

        log.info("LogProcessor req:{}", req);

        String path = req.getPath();

        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.logRes);

        switch (req.getCmd()) {
            case "list": {
                List<MoneFile> res = FileUtils.list(path);
                response.setBody(new Gson().toJson(res).getBytes());
                break;
            }

            case "log": {
                ReadResult res = FileUtils.readFile(path, req.getPointer(), req.getLineNum());
                response.setBody(new Gson().toJson(res).getBytes());
                break;
            }
        }

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }


    @Override
    public int cmdId() {
        return AgentCmd.logReq;
    }
}
