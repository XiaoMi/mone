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

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.nginx.NginxUtils;
import com.xiaomi.youpin.nginx.NginxUtilsV2;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.common.Config;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.po.NginxInfo;
import com.xiaomi.youpin.tesla.agent.po.NginxReq;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @author dingpei
 * 支持nginx操作
 * <p>
 * support
 * upstream 添加 服务器
 * upstream 删除 服务器
 */
@Slf4j
@Component
public class NginxProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) throws IOException {
        NginxReq req = remotingCommand.getReq(NginxReq.class);
        log.info("nginx processor:{}", new Gson().toJson(req));

        switch (req.getCmd()) {
            //修改配置
            case NginxReq.modifyConfig: {
                log.info("modify nginx config:{}", new Gson().toJson(req));
                String configPath = req.getConfigPath();
                log.info("newConfig:{}", req.getConfigStr());
                String oldConfig = new String(Files.readAllBytes(Paths.get(configPath)));
                log.info("oldConfig:{}", oldConfig);
                if (StringUtils.isEmpty(req.getConfigStr()) || !req.getConfigStr().contains("server")) {
                    //当配置不包含server，ip列表为空
                    log.info("NginxProcessor.processRequest, nginx config is: {}", req.getConfigStr());
                    return null;
                }

                //备份
                backUpConfigFile(configPath);
                //写入新的config
                writeConfigFile(configPath, req.getConfigStr());

                if (req.isNeedReload()) {
                    reload();
                }

                return RemotingCommand.createResponseCommand(AgentCmd.nginxRes, new Gson().toJson(Pair.of("config", req.getConfigStr())).getBytes());
            }
            case NginxReq.reload: {
                Pair<Integer, List<String>> pair = reload();
                return RemotingCommand.createResponseCommand(AgentCmd.nginxRes, new Gson().toJson(pair).getBytes());
            }
            case NginxReq.stop: {
                Pair<Integer, List<String>> pair = ProcessUtils.process("/tmp/", "nginx -s stop");
                log.info("stop :{}", pair);
                return RemotingCommand.createResponseCommand(AgentCmd.nginxRes, new Gson().toJson(pair).getBytes());
            }
            case NginxReq.start: {
                Pair<Integer, List<String>> pair = ProcessUtils.process("/tmp/", "nginx");
                log.info("start :{}", pair);
                return RemotingCommand.createResponseCommand(AgentCmd.nginxRes, new Gson().toJson(pair).getBytes());
            }
            case NginxReq.info: {
                NginxInfo nginxInfo = new NginxInfo();
                try {
                    nginxInfo = info(req);
                } catch (Exception e) {
                    nginxInfo = new NginxInfo();
                    nginxInfo.setCode(500);
                    nginxInfo.setMsg(e.getMessage());
                }
                return RemotingCommand.createResponseCommand(AgentCmd.nginxRes, new Gson().toJson(nginxInfo).getBytes());
            }
        }

        return RemotingCommand.createResponseCommand(AgentCmd.nginxRes, new Gson().toJson(Pair.of("num", "-1")).getBytes());
    }

    private Pair<Integer, List<String>> reload() {
        Pair<Integer, List<String>> pair = ProcessUtils.process("/tmp/", "xxxx/nginx/sbin/nginx -s reload");
        log.info("reload:{}", pair);
        return pair;
    }

    private NginxInfo info(NginxReq req) throws IOException {
        Stopwatch sw = Stopwatch.createStarted();
        Pair<Integer, List<String>> pair = ProcessUtils.process("/tmp/", "ps -ef|grep \"nginx.*master.*process\"|grep -v \"grep\"|wc -l");

        boolean started = false;
        if (pair.getValue().size() > 0) {
            int num = Integer.parseInt(pair.getValue().get(0).trim());
            log.info("info num:{}", num);
            started = true;
        }

        String config = new String(Files.readAllBytes(Paths.get(req.getConfigPath())));
        List<String> list = NginxUtils.getServers(config, req.getUpstreamName());
        NginxInfo nginxInfo = new NginxInfo();
        nginxInfo.setAddrList(list);
        nginxInfo.setStarted(started);
        log.info("info use time:{} data:{}", sw.elapsed(TimeUnit.MILLISECONDS), nginxInfo);
        return nginxInfo;
    }

    private void writeConfigFile(String configPath, String configStr) {
        FileLock lock = null;
        FileOutputStream fileOutputStream = null;
        FileChannel channel = null;
        try {
            File file = new File(configPath);
            fileOutputStream = new FileOutputStream(file);
            channel = fileOutputStream.getChannel();
            while (lock == null) {
                lock = channel.tryLock();//无参lock()为独占锁
                Thread.sleep(1000);
            }
            fileOutputStream.write(configStr.getBytes("utf-8"));
        } catch (FileNotFoundException e) {
            log.warn("file not found error:{}", e.getMessage());
        } catch (IOException e) {
            log.warn("copy config file error:{}", e.getMessage());
        } catch (Exception e) {
            log.warn("writeConfigFile error:{}", e.getMessage());
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void backUpConfigFile(String configPath) {
        try {
            FileUtils.copyFile(new File(configPath), new File(configPath + ".bak." + System.currentTimeMillis()));
        } catch (IOException e) {
            log.warn("copy config file error:{}", e.getMessage());
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return AgentCmd.nginxReq;
    }
}
