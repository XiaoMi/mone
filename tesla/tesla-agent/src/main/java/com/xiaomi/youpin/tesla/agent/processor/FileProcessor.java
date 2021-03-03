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

import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.ks3.KsyunService;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.FileReq;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * 文件处理相关processor
 */
@Slf4j
@Component
@Deprecated
public class FileProcessor implements NettyRequestProcessor {


    private KsyunService ksyunService;


    public FileProcessor() {
        ksyunService = new KsyunService();
        ksyunService.setAccessKeyID("");
        ksyunService.setAccessKeySecret("");
        ksyunService.init();
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        FileReq req = remotingCommand.getReq(FileReq.class);
        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.fileRes);
        switch (req.getCmd()) {
            //下载文件(从云盘)
            case "download": {
                byte[] data = ksyunService.getFileByKey(req.getKey());
                try {
                    Files.write(Paths.get(req.getPath()), data);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                response.setBody("download".getBytes());
                return response;
            }
            //上传文件(上传到云盘)
            case "upload": {
                File file = new File(req.getPath());
                ksyunService.uploadFile(req.getKey(), file, (int) TimeUnit.DAYS.toSeconds(360));
                response.setBody("upload".getBytes());
                return response;
            }
        }
        response.setBody("unsupport".getBytes());

        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }


    @Override
    public int cmdId() {
        return AgentCmd.fileReq;
    }
}
