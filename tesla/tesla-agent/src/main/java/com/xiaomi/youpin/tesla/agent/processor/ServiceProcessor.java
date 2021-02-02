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
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.tesla.agent.common.*;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.*;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * 服务管理
 * 物理机部署
 * start
 * stop
 * info
 */
@Slf4j
@Component
@Deprecated
public class ServiceProcessor implements NettyRequestProcessor {

    @Resource
    private RpcClient client;

    private static final String DEFAULT_HEAP_SIZE = "1024";

    private ScheduledExecutorService pool;


    public ServiceProcessor(RpcClient client) {
        this.client = client;
        pool = Executors.newSingleThreadScheduledExecutor();
    }

    public ServiceProcessor() {
        pool = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext channelHandlerContext, RemotingCommand remotingCommand) {
        RemotingCommand response = RemotingCommand.createResponseCommand(AgentCmd.serviceRes);
        ServiceReq req = remotingCommand.getReq(ServiceReq.class);
        log.info("service process req:{}", new Gson().toJson(req));
        switch (ServiceCmd.valueOf(req.getCmd())) {
            case start: {
                return start(response, req, true);
            }
            case shutdown: {
                return stop(response, req, true);
            }

            case info: {
                return info2(response, req);
            }
            case nuke: {
                return nuke(response, req, true);
            }
            case rollback: {
                return rollback(response, req, true);
            }
        }

        return response;
    }

    private RemotingCommand rollback(RemotingCommand response, ServiceReq req, boolean needlock) {
        log.info("rollback req:{}", new Gson().toJson(req));
        String key = getProjectName(req.getJarName());
        try {
            if (needlock) {
                boolean lock = LockUtils.tryLock(key);
                if (!lock) {
                    needlock = false;
                    log.warn("rollback get lock failure key:{}", key);
                    return response;
                }
            }

            DeployInfo deployInfo = new DeployInfo();
            deployInfo.setType(0);
            deployInfo.setDeployPath(req.getServicePath());
            deployInfo.setName(req.getJarName());
            Stopwatch sw = Stopwatch.createStarted();
            try {
                start0(req, deployInfo, sw, response);
                return response;
            } catch (Exception e) {
                log.error(e.getMessage());
                deployInfo.setStep(5);
                response.setBody(new Gson().toJson(AgentResult.builder().cmd("rollback").code(500).build()).getBytes());
                return response;
            } finally {
                DeployService.ins().createOrUpdate(deployInfo);
            }
        } finally {
            if (needlock) {
                LockUtils.unLock(key);
            }
        }
    }

    private RemotingCommand nuke(RemotingCommand response, ServiceReq req, boolean needlock) {
        String key = getProjectName(req.getJarName());
        try {
            if (needlock) {
                boolean lock = LockUtils.tryLock(key);
                if (!lock) {
                    needlock = false;
                    log.warn("nuke get lock failue key:{}", key);
                    return response;
                }
            }

            log.info("nuke:{}", new Gson().toJson(req));
            Safe.execute(() -> stop(response, req, false));
            Safe.execute(() -> FileUtils.forceDelete(new File(req.getServicePath())));
            response.setBody("{}".getBytes());
            return response;
        } finally {
            if (needlock) {
                LockUtils.unLock(key);
            }
        }
    }

    private RemotingCommand info(RemotingCommand response, ServiceReq req) {
        String tml = TemplateUtils.getTemplate("info.tml");
        Map<String, Object> m = Maps.newHashMap();
        m.put("jar", req.getJarName());
        m.put("java_bin_path", Config.ins().get(Config.JAVA_BIN_PATH, Config.DEFAULT_JAVA_BIN_PATH));
        String cmd = TemplateUtils.renderTemplate(tml, m);
        List<String> list = ProcessUtils.process(req.getServicePath(), cmd).getValue();
        AgentResult<Object> res = AgentResult.builder().code(0).message("success").data(list).build();
        response.setBody(new Gson().toJson(res).getBytes());
        return response;
    }

    /**
     * 使用ps来查询
     *
     * @param response
     * @param req
     * @return
     */
    private RemotingCommand info2(RemotingCommand response, ServiceReq req) {
        String tml = TemplateUtils.getTemplate("info2.tml");
        Map<String, Object> m = Maps.newHashMap();
        m.put("jar", req.getJarName());
        String cmd = TemplateUtils.renderTemplate(tml, m);
        List<String> list = ProcessUtils.process(req.getServicePath(), cmd).getValue();
        AgentResult<Object> res = AgentResult.builder().code(0).message("success").data(list).build();
        String resStr = new Gson().toJson(res);
        log.info("info2 {}:{}", req.getJarName(), resStr);
        response.setBody(resStr.getBytes());
        return response;
    }

    private RemotingCommand stop(RemotingCommand response, ServiceReq req, boolean needLock) {
        log.info("stop :{}", new Gson().toJson(req));
        String key = getProjectName(req.getJarName());
        try {
            if (needLock) {
                boolean lock = LockUtils.tryLock(key);
                if (!lock) {
                    needLock = false;
                    log.warn("get lock failure key:{}", key);
                    return response;
                }
            }

            String savePath = req.getServicePath();
            File file = new File(savePath + "pid");
            if (file.exists()) {
                try {
                    String pid = new String(Files.readAllBytes(Paths.get(file.getPath()))).replaceAll("\n", "");
                    log.info("kill by pid:{} {}", pid, ProcessUtils.process(req.getServicePath(), "kill " + pid));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                file.delete();
            }
            String pid = getPid(req);
            if (StringUtils.isNotEmpty(pid)) {
                kill(req, "kill ", pid);
            }
            response.setBody(new Gson().toJson(AgentResult.builder().cmd("stop").code(0).build()).getBytes());
            return response;
        } finally {
            if (needLock) {
                LockUtils.unLock(key);
            }
        }
    }

    private void kill(ServiceReq req, String kill, String pid) {
        log.info("kill pid:{}", pid);
        ProcessUtils.process(req.getServicePath(), kill + pid);
    }


    private String getProjectName(String jarName) {
        return jarName.split("-20")[0];
    }


    private String getPid(ServiceReq req) {
        String javaBinPath = Config.ins().get(Config.JAVA_BIN_PATH, Config.DEFAULT_JAVA_BIN_PATH);

        String cmd = "";
        if (StringUtils.isEmpty(req.getLanguage())) {
            String tml = TemplateUtils.getTemplate("stop.tml");
            Map<String, Object> m = Maps.newHashMap();
            m.put("jar", this.getProjectName(req.getJarName()));
            m.put("java_bin_path", javaBinPath);
            cmd = TemplateUtils.renderTemplate(tml, m);
        } else {
            String tml = TemplateUtils.getTemplate("go_stop.tml");
            Map<String, Object> m = Maps.newHashMap();
            m.put("jar", this.getProjectName(req.getJarName()));
            cmd = TemplateUtils.renderTemplate(tml, m);
        }

        log.info("stop cmd:{}", cmd);

        List<String> list = ProcessUtils.process(req.getServicePath(), cmd).getValue();
        log.info("list:{}", list);
        if (list.size() > 0) {
            return list.get(0);
        }
        return "";
    }


    private RemotingCommand start(RemotingCommand response, ServiceReq req, boolean needLock) {
        //用项目名,构成唯一key
        String key = this.getProjectName(req.getJarName());
        try {
            //不允许流程中,同时启动多个相同项目
            if (needLock) {
                boolean lock = LockUtils.tryLock(key);
                if (!lock) {
                    needLock = false;
                    log.info("start failure key:{}", key);
                    return response;
                }
            }
            long now = System.currentTimeMillis();
            log.info("start:{}", new Gson().toJson(req));

            DeployInfo deployInfo = new DeployInfo();
            deployInfo.setType(0);
            deployInfo.setDeployPath(req.getServicePath());
            deployInfo.setName(req.getJarName());
            deployInfo.setCtime(now);
            deployInfo.setUtime(now);
            if (null != req.getAttachments()) {
                deployInfo.setAttachment(new Gson().toJson(req.getAttachments()));
            }
            deployInfo.setDownloadKey(deployInfo.getDownloadKey());
            deployInfo.setUtime(System.currentTimeMillis());

            Stopwatch sw = Stopwatch.createStarted();
            deployInfo.setStep(0);
            notifyServer(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 0, "begin", "[INFO] start deploy\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
            String downloadKey = req.getDownloadKey();
            int code = 0;
            try {
                long downloadBegin = System.currentTimeMillis();

                if (!new File(req.getServicePath()).exists()) {
                    Files.createDirectories(Paths.get(req.getServicePath()));
                }
                log.info("chmod:{}", ProcessUtils.process(req.getServicePath(), "chown -R work " + req.getServicePath()));

                File downloadFile = Paths.get(req.getServicePath() + "/" + req.getJarName()).toFile();

                deployInfo.setStep(1);
                //改用边下边存的模式
                DownloadUtils.download(downloadKey, req.getDownloadUrl(), 180, downloadFile);

                long fileLength = downloadFile.length();

                notifyServer(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 1, "download", "[INFO] download file finish size:" + fileLength + "  time:" + (System.currentTimeMillis() - downloadBegin) / 1000 + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));


                deployInfo.setStep(2);
                notifyServer(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 2, "saveFile", "[INFO] save file finish\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));

                log.info("touch:{}", ProcessUtils.process(req.getServicePath(), "touch start.sh"));

                start0(req, deployInfo, sw, response);

                notifyServer(new NotifyMsg(NotifyMsg.STATUS_SUCESSS, 4, "startFinish", "[SUCCESS] deploy completed successfully ", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                code = 500;
                deployInfo.setStep(5);
                notifyServer(new NotifyMsg(NotifyMsg.STATUS_FAIL, 5, "startError", "[ERROR]" + e.getMessage() + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
            } finally {
                DeployService.ins().createOrUpdate(deployInfo);
            }
            response.setBody(new Gson().toJson(AgentResult.builder().cmd("start").code(code).build()).getBytes());
            return response;
        } finally {
            if (needLock) {
                LockUtils.unLock(key);
            }
        }
    }

    private boolean supportJvmParams(ServiceReq req) {
        return StringUtils.isNotEmpty(req.getJvmParams());
    }

    private void start0(ServiceReq req, DeployInfo deployInfo, Stopwatch sw, RemotingCommand response) {
        String cmd = "";

        if (StringUtils.isEmpty(req.getLanguage())) {
            String tml = TemplateUtils.getTemplate("start.tml");
            if(supportJvmParams(req)) {
                int sIndex = tml.indexOf("-Xms${heapSize}M");
                int eIndex = tml.indexOf("${jar}");
                tml = tml.replace(tml.substring(sIndex, eIndex), req.getJvmParams() + " ");
            }
            Map<String, Object> m = Maps.newHashMap();
            m.put("jar", req.getJarName());
            m.put("gc_name",
                    getProjectName(req.getJarName()) + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            m.put("servicePath", req.getServicePath());
            m.put("java_bin_path", Config.ins().get(Config.JAVA_BIN_PATH, Config.DEFAULT_JAVA_BIN_PATH));
            m.put("heapSize", getHeapSize(req));
            cmd = TemplateUtils.renderTemplate(tml, m);
        } else if (req.getLanguage().equals("golang")) {
            String tml = TemplateUtils.getTemplate("go_start.tml");
            Map<String, Object> m = Maps.newHashMap();
            m.put("jar", req.getJarName());
            m.put("servicePath", req.getServicePath());
            cmd = TemplateUtils.renderTemplate(tml, m);
        }

        log.info("start cmd:{}", cmd);
        deployInfo.setStep(3);
        notifyServer(new NotifyMsg(NotifyMsg.STATUS_PROGRESS, 3, "startBegin", "[INFO] create script finish" + "\n", sw.elapsed(TimeUnit.MILLISECONDS), req.getId(), req.getAttachments()));
        log.info("write start.sh:{}", ProcessUtils.process(req.getServicePath(), cmd));
        log.info("chmod:{}", ProcessUtils.process(req.getServicePath(), "chmod 777 " + req.getJarName()));
        log.info("chmod:{}", ProcessUtils.process(req.getServicePath(), "chmod 777 start.sh"));


        try {
            stop(response, req, false);
        } catch (Throwable ex) {
            log.warn("ex:{}", ex.getMessage());
        }
        doStart(req);
        log.info("start finish");
    }

    private void chmodDirWork() {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        if (!isWindows) {
            if (new File("xxxx/log").exists()) {
                log.info("chmodDirWork:{}, {}", ProcessUtils.process("xxxx", "chown -R work:work log"), "chown -R work:work log");
            }
            if (new File("xxxx/data").exists()) {
                log.info("chmodDirWork:{}, {}", ProcessUtils.process("xxxx", "chown -R work:work data"), "chown -R work:work data");
            }
        }
    }

    private void doStart(ServiceReq req) {
        for (int i = 0; i < 30; i++) {
            String pid = getPid(req);
            log.info("pid:{}", pid);
            if (StringUtils.isEmpty(pid)) {
                if (i == 20) {
                    //强制杀死
                    kill(req, "kill -9 ", pid);
                }

                String cmd = "./start.sh";
                if (("work").equals(req.getUserRight())) {
                    chmodDirWork();
                    cmd = req.getServicePath().endsWith(File.separator)
                            ? "su -c \"/bin/sh " + req.getServicePath() + "start.sh\" work"
                            : "su -c \"/bin/sh " + req.getServicePath() + "/start.sh\" work";
                }
                log.info("do start.sh:{}, {}", ProcessUtils.process(req.getServicePath(), cmd), cmd);
                break;
            }
            CommonUtils.sleep(1);
        }
    }


    private String getHeapSize(ServiceReq req) {
        String heapSize = req.getHeapSize();
        if (StringUtils.isEmpty(heapSize)) {
            return DEFAULT_HEAP_SIZE;
        }
        return heapSize;
    }


    private void notifyServer(NotifyMsg notifyMsg) {
        try {
            log.info("notify:{}", new Gson().toJson(notifyMsg));
            RemotingCommand msg = RemotingCommand.createMsgPackRequest(AgentCmd.notifyMsgReq, notifyMsg);
            this.client.sendMessage(msg);
        } catch (Throwable ex) {
            log.warn("notifyServer error:{}", ex.getMessage());
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    @Override
    public int cmdId() {
        return AgentCmd.serviceReq;
    }
}
