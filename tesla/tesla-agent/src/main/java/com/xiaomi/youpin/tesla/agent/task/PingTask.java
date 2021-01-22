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

package com.xiaomi.youpin.tesla.agent.task;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.management.OperatingSystemMXBean;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.RpcCmd;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.task.Task;
import com.xiaomi.youpin.docker.UseInfo;
import com.xiaomi.youpin.docker.YpDockerClient;
import com.xiaomi.youpin.jmonitor.Jmonitor;
import com.xiaomi.youpin.tesla.agent.common.NetUtils;
import com.xiaomi.youpin.tesla.agent.common.ProcessUtils;
import com.xiaomi.youpin.tesla.agent.po.PingAppInfo;
import com.xiaomi.youpin.tesla.agent.po.PingReq;
import com.xiaomi.youpin.tesla.agent.service.DeployService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class PingTask extends Task {

    private static final AtomicBoolean docker = new AtomicBoolean(false);

    public static final AtomicBoolean stop = new AtomicBoolean(false);

    private static final AtomicReference<String> aip = new AtomicReference<>("");

    private static String getIp(RpcClient client) {
        try {
            if (StringUtils.isNotEmpty(aip.get())) {
                return aip.get();
            }
            String ip = client.getClient().getAddress().get().split(":")[0].replace("/", "");
            aip.compareAndSet("", ip);
            return aip.get();
        } catch (Throwable ex) {
            log.warn("get ip ex:{}", ex.getMessage());
            return "";
        }
    }

    public PingTask(RpcClient client) {
        super(() -> {

            if (stop.get()) {
                return;
            }

            try {
                PingReq ping = new PingReq();
                String ip = getIp(client);
                ping.setIp(ip);
                ping.setTime(System.currentTimeMillis());
                ping.setMessage("ping");
                ping.setHostName(NetUtils.getHostName());
                ping.setUptime(String.valueOf(getUptime()));
                ping.setSreLabel(DeployService.ins().getSreLabel());

                //判断是否是docker机器
                boolean isDocker = docker.get() && StringUtils.isNotEmpty(YpDockerClient.ins().version());
                ping.setDocker(isDocker);

                if (isDocker) {

                    //docker虚拟机
                    UseInfo useInfo = YpDockerClient.ins().containerUseInfo(ip);
                    ping.setUseCpu(useInfo.getUseCpuNum());
                    ping.setUseMem(useInfo.getUseMemNum());

                    Info info = YpDockerClient.ins().info();
                    ping.setCpu(info.getNCPU());
                    ping.setMem(info.getMemTotal());

                    //获取app的详尽信息
                    if (useInfo.getAppInfos() != null) {
                        log.info("app infs:{}", new Gson().toJson(useInfo.getAppInfos()));
                        ping.setAppInfos(useInfo.getAppInfos().stream().map(it -> {
                            PingAppInfo pi = new PingAppInfo();
                            pi.setAppName(it.getAppName());
                            pi.setIp(it.getIp());
                            pi.setEnvId(it.getEnvId());
                            return pi;
                        }).collect(Collectors.toList()));
                    }

                    ping.setApps(useInfo.getApps());
                    ping.setContainerUptime(containerUptimeList());
                } else {
                    //物理机
                    ping.setUseCpu(-1);
                    ping.setUseMem(freePhysicalMemorySize());

                    ping.setCpu(DeployService.ins().cpuNum);
                    ping.setMem(DeployService.ins().mem);
                    ping.setApps(physicalApps());
                }

                ping.setPorts(NetUtils.getPorts("/tmp"));

                RemotingCommand req = RemotingCommand.createRequestCommand(RpcCmd.pingReq);
                req.setBody(new Gson().toJson(ping).getBytes());
                client.sendMessage(client.getServerAddrs(), req, responseFuture -> {
                    if (null == responseFuture.getResponseCommand()) {
                        return;
                    }
                    String body = new String(responseFuture.getResponseCommand().getBody());
                    log.info("{}", body);
                    if (null != body) {
                        Type typeOfT = new TypeToken<Map<String, String>>() {
                        }.getType();
                        Map<String, String> labels = new Gson().fromJson(body, typeOfT);
                        if (labels.getOrDefault("type", "physical").equals("docker")) {
                            docker.set(true);
                        } else {
                            docker.set(false);
                        }
                    }
                });

            } catch (Exception ex) {
                log.error("ping error:{}", ex.getMessage());
            }
        }, 10);
    }


    private static String containerUptimeList() {
        try {
            List<Container> list = YpDockerClient.ins().listContainers(Lists.newArrayList(), false);
            return list.stream().map(it -> {
                String id = it.getId();
                Map<String, String> labels = it.getLabels();
                if (null == labels || !labels.containsKey("ENV_ID")) {
                    return "";
                }

                String envId = labels.get("ENV_ID");

                String res = "";
                try {
                    res = YpDockerClient.ins().exec(id, "uptime", 2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return envId + ":" + getUpdate0(res);
            }).filter(it -> StringUtils.isNotEmpty(it)).collect(Collectors.joining(","));
        } catch (Throwable ex) {
            log.warn("error:{}", ex.getMessage());
            return "";
        }
    }


    private static long freePhysicalMemorySize() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osmxb.getFreePhysicalMemorySize();
    }

    private static Double getUptime() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return 0.0;
        }

        List<String> list = ProcessUtils.process("/tmp/", "uptime").getValue();
        String info = list.get(0);
        return getUpdate0(info);
    }

    private static Double getUpdate0(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0.0;
        }
        try {
            return Double.parseDouble(str.split("averages?")[1].trim().split(",|\\s")[1].trim());
        } catch (Throwable ex) {
            log.warn("error:{}", ex.getMessage());
            return 0.0;
        }

    }


    private static Set<String> physicalApps() {
        try {
            Jmonitor jmonitor = new Jmonitor();
            List<Jmonitor.JInfo> list = jmonitor.getJpsInfo();
            return list.stream().map(it -> it.getName()).collect(Collectors.toSet());
        } catch (Exception ex) {
            log.warn("apps error:{}", ex.getMessage());
            return Sets.newHashSet();
        }
    }

}
