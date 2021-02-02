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

package com.xiaomi.youpin.gwdash.agent.processor;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.processor.PingProcessor;
import com.xiaomi.youpin.gwdash.bo.MachineBo;
import com.xiaomi.youpin.gwdash.bo.MachineLabels;
import com.xiaomi.youpin.gwdash.service.MachineManagementService;
import com.xiaomi.youpin.tesla.agent.po.PingAppInfo;
import com.xiaomi.youpin.tesla.agent.po.PingReq;
import com.xiaomi.youpin.tesla.agent.po.SreLabel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class GwPingProcessor extends PingProcessor {

    public GwPingProcessor(final MachineManagementService machineManagementService, final IProjectDeploymentService projectDeploymentService) {
        super(msg -> {

            String ip = "";

            List<Long> envIds = null;

            if (null != msg) {
                long now = System.currentTimeMillis();
                PingReq req = new Gson().fromJson(msg, PingReq.class);
                int cpu = req.getCpu();
                long mem = req.getMem();
                ip = req.getIp();
                int useCpu = req.getUseCpu();
                long useMem = req.getUseMem();
                boolean docker = req.isDocker();
                Set<Integer> ports = req.getPorts();

                //插入新机器
                addNewMachine(machineManagementService, ip, req);

                //更新label信息
                updateLabels(machineManagementService, ip, now, req, cpu, mem, useCpu, useMem, docker, ports);

                //自动扩容
                envIds = autoScale(projectDeploymentService, req);

                MachineLabels res = machineManagementService.queryMachineLabels(ip);

                //向qutoa服务器更新资源信息
                updateResourceInfo(projectDeploymentService, req, ip, cpu, envIds, res);

                //机器未受管理的报警
                machineAlarm(req, projectDeploymentService);
            }

            //返回结果
            return getRes(machineManagementService, ip);
        });
    }

    private static void machineAlarm(PingReq req, IProjectDeploymentService projectDeploymentService) {
        List<PingAppInfo> infos = req.getAppInfos();
        if (null != infos) {
            log.info("machineAlarm infos:{} {}", req.getIp(), new Gson().toJson(infos));
            infos.stream().forEach(it -> {
                try {
                    long envId = Long.valueOf(it.getEnvId());
                    String ip = it.getIp();
                    List<MachineBo> list = projectDeploymentService.getMachineListFromCache(envId);
                    boolean find = list.stream().filter(m -> m.getIp().equals(ip)).findAny().isPresent();
                    if (!find) {
                        log.error("{} {} {} 未受治理", it.getAppName(), it.getEnvId(), ip);
                        projectDeploymentService.sendFeiShuMsg("appName:" + it.getAppName() + " envId:" + envId + " ip:" + ip + " 未受治理");
                    }
                } catch (Throwable ex) {
                    log.error(ex.getMessage());
                }
            });
        }
    }

    private static void updateResourceInfo(IProjectDeploymentService projectDeploymentService, PingReq req, String ip, int cpu, List<Long> envIds, Map<String, String> labels) {
        log.info("updateResourceInfo:{}", ip);
        try {
//            if (req.isDocker()) {
                log.info("update resource info");
                long mem = req.getMem();
                Set<Integer> ports = req.getPorts();
                projectDeploymentService.updateResourceInfo(ip, 0, cpu, mem, ports, labels);
//            }
        } catch (Throwable ex) {
            log.error("ex:{}", ex.getMessage());
        }
    }

    /**
     * 可以获取到部署的说有docker环境的env id 列表
     *
     * @param projectDeploymentService
     * @param req
     * @return
     */
    private static List<Long> autoScale(IProjectDeploymentService projectDeploymentService, PingReq req) {
        List<Long> res = Lists.newArrayList();
        try {
            String upTimeList = req.getContainerUptime();
            if (StringUtils.isNotEmpty(upTimeList)) {
                Arrays.stream(upTimeList.split(",")).map(it -> {
                    String[] ss = it.split(":");
                    return Pair.of(ss[0], Double.parseDouble(ss[1]));
                }).forEach(it -> {
                    String envId = it.getKey();
                    if (it.getValue() > projectDeploymentService.uptime(envId)) {
                        //进行扩容
                        projectDeploymentService.autoScale(envId, "expansion");
                    }
                    if (StringUtils.isNotEmpty(envId)) {
                        res.add(Long.valueOf(envId));
                    }
                });
            }
        } catch (Throwable ex) {
            log.warn("error:{}", ex.getMessage());
        }
        return Lists.newArrayList();
    }

    private static String getRes(MachineManagementService machineManagementService, String ip) {
        String resStr = "{}";
        MachineLabels res = machineManagementService.queryMachineLabels(ip);
        Gson gson = new Gson();
        if (null != res) {
            resStr = gson.toJson(res);
        }
        return resStr;
    }

    private static void updateLabels(MachineManagementService machineManagementService, String ip, long now, PingReq req, int cpu, long mem, int useCpu, long useMem, boolean docker, Set<Integer> ports) {
        try {


            machineManagementService.setMachineLabels(
                    machineManagementService.setMachineLableSql(ip, MachineLabels.Utime, String.valueOf(now)),
                    machineManagementService.setMachineLableSql(ip, MachineLabels.Cpu, String.valueOf(cpu)),
                    machineManagementService.setMachineLableSql(ip, MachineLabels.Mem, String.valueOf(mem)),
                    machineManagementService.setMachineLableSql(ip, MachineLabels.Ip, String.valueOf(ip)),
                    machineManagementService.setMachineLableSql(ip, MachineLabels.UseCpu, String.valueOf(useCpu)),
                    machineManagementService.setMachineLableSql(ip, MachineLabels.UseMem, String.valueOf(useMem)),

                    machineManagementService.setMachineLableSql(ip, MachineLabels.Ports,
                            ports.stream().map(it -> String.valueOf(it)).collect(Collectors.joining(","))),

                    machineManagementService.setMachineLableSql(ip, MachineLabels.Apps, req.getApps().stream().collect(Collectors.joining(",")))
            );

            SreLabel sreLabel = req.getSreLabel();
            if (Optional.ofNullable(sreLabel).isPresent()) {
                machineManagementService.setMachineLabels(
                        machineManagementService.
                                setMachineLableSql(ip, MachineLabels.Keycenter, String.valueOf(sreLabel.isKeycenter())),
                        machineManagementService.
                                setMachineLableSql(ip, MachineLabels.Outbound, String.valueOf(sreLabel.isOutbound()))
                );
            }

            machineManagementService.updateLastUpdateTime(ip, now);
        } catch (Throwable ex) {
            log.info("updateLabels: {}", ex.getMessage());
        }
    }

    private static void addNewMachine(MachineManagementService machineManagementService, String ip, PingReq req) {
        int count = machineManagementService.countWithIp(ip);
        if (count <= 0) {
            MachineBo bo = new MachineBo();
            bo.setIp(ip);
            bo.setHostname(req.getHostName());
            bo.setLabels(new MachineLabels());
            bo.setPrepareLabels(new MachineLabels());
            machineManagementService.addMachine(bo);
        } else {
            // 更新hostname
            MachineBo bo = new MachineBo();
            bo.setIp(ip);
            bo.setHostname(req.getHostName());
            machineManagementService.updateMachine(bo);
        }
    }
}
