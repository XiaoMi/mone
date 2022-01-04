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
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.common.*;
import com.xiaomi.youpin.tesla.agent.po.*;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @author dingpei
 * @author renqingfu
 * 处理docker 相关业务
 * 虚拟化部署
 */
@Slf4j
@Component
public class DockerProcessor implements NettyRequestProcessor {

    @Resource
    private RpcClient client;

    private ScheduledExecutorService pool;

    public DockerProcessor(RpcClient client) {
        this.client = client;
    }

    private ExecutorService dockerProcessorPool = Executors.newFixedThreadPool(10);

    public DockerProcessor() {
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        Stopwatch sw = Stopwatch.createStarted();
        DockerReq req = remotingCommand.getReq(DockerReq.class);
        //执行命令的上下文
        DockerContext context = new DockerContext();
        new DockerReqInit().initReq(req);

        String deployInfoName = CommonUtils.getName(req.getContainerName());
        DeployInfo deployInfo = DeployService.ins().get(deployInfoName);
        deployInfo.setUtime(System.currentTimeMillis());

        log.info("name:{} id:{} req => {}", deployInfo.getName(), deployInfo.getId(), new Gson().toJson(req));

        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.dockerRes);

        if (!req.getCmd().equals(DockerCmd.info.name())) {
            boolean lock = LockUtils.tryLock(lockName(deployInfo));
            if (!lock) {
                log.warn("get lock error {} {}", deployInfo.getId(), deployInfo.getName());

                if (req.getCmd().equals(DockerCmd.nuke.name())) {
                    NukeRes r = new NukeRes();
                    r.setCode(501);
                    r.setMessage("get lock error");
                    response.setBody(new Gson().toJson(r).getBytes());
                }

                return response;
            }
        }
        try {
            //构建 or 拉取
            if (req.getCmd().equals(DockerCmd.build.name())) {
                buildOrPull(sw, req, deployInfo);
            }

            //创建容器
            if (req.getCmd().equals(DockerCmd.create.name())) {
                create(context, sw, req, deployInfo);
            }

            //停止容器
            if (req.getCmd().equals(DockerCmd.stop.name())) {
                stop(context, sw, req, deployInfo);
            }

            //启动容器
            if (req.getCmd().equals(DockerCmd.start.name())) {
                start(context, sw, req, deployInfo, response);
            }

            //获取信息
            if (req.getCmd().equals(DockerCmd.info.name())) {
                new DockerInfoCal().info(req, response);
            }

            //重启
            if (req.getCmd().equals(DockerCmd.restart.name())) {
                return restart(sw, req, deployInfo);
            }

            //停机
            if (req.getCmd().equals(DockerCmd.shutdown.name())) {
                shutdown(context, sw, req, deployInfo);
            }

            //停机并删除
            if (req.getCmd().equals(DockerCmd.nuke.name())) {
                nuke(context, sw, req, response, deployInfo);
            }

            //获取日志快照
            if (req.getCmd().equals(DockerCmd.log.name())) {
                String linesStr = req.getAttachments().get("lines");
                Integer lines = 50;
                if (StringUtils.isNotEmpty(linesStr)) {
                    lines = new Integer(linesStr);
                }
                DockerLog.logSnapshot(sw, req.getContainerName(), response, lines);
            }

            //获取docker inspect
            if (req.getCmd().equals(DockerCmd.inspect.name())) {
                String isRunning = req.getAttachments().get("isRunning");
                if (StringUtils.isNotEmpty(isRunning) && "true".equals(isRunning)) {
                    DockerInspect.dockerInspectWithRunning(sw, CommonUtils.getName(req.getContainerName()), response);
                } else {
                    DockerInspect.dockerInspect(sw, req.getContainerName(), response);
                }
            }
        } catch (Throwable ex) {
            log.error("docker processor " + deployInfo.getName() + " error:" + ex.getMessage(), ex);
            deployInfo.setState(DeployInfo.DeployState.failure.ordinal());
            notifyServer(new NotifyMsg(NotifyMsg.STATUS_FAIL, 0, "error", "[ERROR] " + ex.getMessage() + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()), req.getReqSource());
        } finally {
            if (!req.getCmd().equals(DockerCmd.info.name())) {
                LockUtils.unLock(lockName(deployInfo));
                DeployService.ins().createOrUpdate(deployInfo);
                log.info("docker processor {} {} use time:{}", deployInfo.getName(), deployInfo.getId(), sw.elapsed(TimeUnit.MILLISECONDS));
            }
        }
        return response;
    }

    private void sidecar(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo) {
        new DockerSideCar().create(context, sw, req, deployInfo, null);
    }

    private String lockName(DeployInfo deployInfo) {
        return Stream.of(deployInfo.getName(), String.valueOf(deployInfo.getType())).collect(Collectors.joining("_"));
    }

    private void nuke(DockerContext context, Stopwatch sw, DockerReq req, RemotingCommand response, DeployInfo deployInfo) {
        long time = sw.elapsed(TimeUnit.MILLISECONDS);
        new DockerNuke().nuke(context, sw, req, response, deployInfo, this.dockerProcessorPool, () -> notifyServer(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 0, "stop", "[INFO] stop container finish(" + time + "ms)", time, req.getId(), req.getAttachments()), req.getReqSource()));
    }

    /**
     * 容器挂了,从新拉起
     *
     * @param sw
     * @param req
     * @param deployInfo
     * @return
     */
    private RemotingCommand restart(Stopwatch sw, DockerReq req, DeployInfo deployInfo) {
        return new DockerRestart().restart(sw, req, deployInfo, msg -> notifyServer(msg, req.getReqSource()));
    }

    private void stop(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo) {
        shutdown(context, sw, req, deployInfo);
        req.setCmd(DockerCmd.start.name());
    }

    /**
     * 不会流转(单纯的停机)
     *
     * @param sw
     * @param req
     * @param deployInfo
     */
    private void shutdown(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo) {
        long time = sw.elapsed(TimeUnit.MILLISECONDS);
        new DockerNuke().shutdown(context, sw, req, deployInfo, () -> notifyServer(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 0, "stop", "[INFO] stop container finish(" + time + "ms)", time, req.getId(), req.getAttachments()), req.getReqSource()));
    }

    private void start(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo, RemotingCommand response) {
        new DockerStart().start(context, sw, req, deployInfo, response, this.dockerProcessorPool, msg -> notifyServer(msg, req.getReqSource()));
    }

    private void create(DockerContext context, Stopwatch sw, DockerReq req, DeployInfo deployInfo) {
        new DockerCreate().create(context, sw, req, deployInfo, msg -> notifyServer(msg, req.getReqSource()));
    }

    private void buildOrPull(Stopwatch sw, DockerReq req, DeployInfo deployInfo) throws InterruptedException {
        new DockerBuildOrPull().buildOrPull(sw, req, deployInfo, msg -> notifyServer(msg, req.getReqSource()));
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    private void notifyServer(NotifyMsg notifyMsg, String reqSource) {
        CommonUtils.notifyServer(this.client, notifyMsg, reqSource);
    }

    @Override
    public int cmdId() {
        return AgentCmd.dockerReq;
    }
}
