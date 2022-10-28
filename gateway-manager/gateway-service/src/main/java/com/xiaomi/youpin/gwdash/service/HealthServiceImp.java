///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.dianping.cat.Cat;
//import com.google.common.base.Stopwatch;
//import com.google.common.reflect.TypeToken;
//import com.google.gson.Gson;
//import com.xiaomi.data.push.client.Pair;
//import com.xiaomi.data.push.nacos.NacosNaming;
//import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
//import com.xiaomi.data.push.schedule.task.TaskDefBean;
//import com.xiaomi.data.push.schedule.task.TaskParam;
//import com.xiaomi.youpin.gwdash.bo.DeployInfo;
//import com.xiaomi.youpin.gwdash.bo.DeployMachine;
//import com.xiaomi.youpin.gwdash.bo.RemotingCommandBo;
//import com.xiaomi.youpin.gwdash.common.AppDeployStatus;
//import com.xiaomi.youpin.gwdash.common.HttpUtils;
//import com.xiaomi.youpin.gwdash.common.LabelUtils;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import com.xiaomi.youpin.hermes.bo.response.Account;
//import com.xiaomi.youpin.infra.rpc.Result;
//import com.xiaomi.youpin.mischedule.MethodInfo;
//import com.xiaomi.youpin.mischedule.STaskDef;
//import com.xiaomi.youpin.mischedule.api.service.bo.HealthParam;
//import com.xiaomi.youpin.mischedule.api.service.bo.Task;
//import com.xiaomi.youpin.tesla.agent.po.AgentResult;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.ApplicationConfig;
//import org.apache.dubbo.config.RegistryConfig;
//import org.apache.dubbo.config.annotation.Service;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author goodjava@qq.com
// * <p>
// * 健康监测服务
// */
//@Slf4j
//@Service(interfaceClass = HealthService.class, retries = 0, group = "${dubbo.group}")
//public class HealthServiceImp implements HealthService {
//
//
//    @Autowired
//    private MyScheduleService myScheduleService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private ProjectDeploymentService projectDeploymentService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private FeiShuService feiShuService;
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private ApplicationConfig applicationConfig;
//
//    @Autowired
//    private RegistryConfig registryConfig;
//
//    @Autowired
//    private NacosNaming nacosNaming;
//
//    @Value("${dubbo.group}")
//    private String dubboGroup;
//
//    @Value("${server.serverEnv}")
//    private String serverEnv;
//
//    private ConcurrentHashMap<Long, Object> allMachineHealthChecks = new ConcurrentHashMap<>();
//
//    private static final int TOTAL_FAIL_NUM = 5;
//
//
//    /**
//     * 判断是否使用auto scaling
//     *
//     * @param projectEnvDeploySetting
//     * @return　true　如果qps label存在
//     */
//    private Pair<Boolean, Integer> isAutoScalingOn(ProjectEnvDeploySetting projectEnvDeploySetting) {
//        if (projectEnvDeploySetting == null) {
//            return Pair.of(false, -1);
//        }
//        String labels = projectEnvDeploySetting.getLabels();
//        //在label中指定qps
//        String value = LabelUtils.getLabelValue(labels, "qps");
//        if (StringUtils.isEmpty(value)) {
//            return Pair.of(false, -1);
//        }
//        int qps = Integer.parseInt(value);
//        return Pair.of(qps > 0, qps);
//    }
//
//
//    /**
//     * 启动健康监测的任务
//     * <p>
//     * 如果没有则创建 create
//     * 有则修改 modify
//     *
//     * <p>
//     * 返回的是任务的id
//     *
//     * @return
//     */
//    public Integer startHealthCheck(int taskId, long envId, HealthParam healthParam) {
//        ProjectEnvDeploySetting projectEnvDeploySetting = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId));
//        Pair<Boolean, Integer> isAutoScaling = isAutoScalingOn(projectEnvDeploySetting);
//
//        log.info("---->startHealthCheck: {}, {}", taskId, healthParam);
//        TaskParam taskParam = new TaskParam();
//        taskParam.setTaskId(taskId);
//        taskParam.put(TaskParam.PARAM, new Gson().toJson(healthParam));
//        taskParam.setNotify("mqNotify");
//
//        if (isAutoScaling.getKey()) {
//            taskParam.setCron("0/5 * * * * ?");
//        } else {
//            taskParam.setCron("0/20 * * * * ?");
//        }
//        TaskDefBean tdf = new TaskDefBean(STaskDef.HealthyTask);
//
//        if (serverEnv.contains("staging") || serverEnv.contains("local") || serverEnv.contains("dev")) {
//            tdf.setErrorRetryNum(20);
//        } else {
//            tdf.setErrorRetryNum(Integer.MAX_VALUE);
//        }
//
//        taskParam.setTaskDef(tdf);
//        taskParam.setBizId("health_check_" + envId);
//        Result<Integer> res = myScheduleService.submitTask(taskParam);
//        com.xiaomi.youpin.infra.rpc.Result<Boolean> result = myScheduleService.start(taskId);
//        if (result != null && !result.getData()) {
//            result = myScheduleService.start(taskId);
//            if (result != null && !result.getData()) {
//                Cat.logEvent("mischedule start failed", String.valueOf(taskId), "0", "envId: " + envId);
//            }
//        }
//        if (res.getCode() == 0) {
//            return res.getData();
//        }
//        return -1;
//    }
//
//    /**
//     * 通知mischedule开启定时调度，监测物理机app健康状态
//     *
//     * @param envId
//     * @return
//     */
//    public boolean startAppHealthCheck(long envId) {
//        log.info("HealthServiceImp#startAppHealthCheck: {}", envId);
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        return startAppHealthCheck(projectEnv);
//    }
//
//    public boolean startAppHealthCheck(ProjectEnv projectEnv) {
//        if (null == projectEnv) {
//            return false;
//        }
//        long envId = projectEnv.getId();
//        log.info("HealthServiceImp#startAppHealthCheck env: {}", projectEnv);
//        // 新建健康监测
//        TaskParam taskParam = new TaskParam();
//        taskParam.setTaskId(projectEnv.getHealthCheckTaskId());
//        taskParam.setCron("0/10 * * * * ?");
//        MethodInfo methodInfo = new MethodInfo();
//        methodInfo.setServiceName("com.xiaomi.youpin.gwdash.service.HealthService");
//        methodInfo.setMethodName("doAppHealthCheck");
//        methodInfo.setGroup(dubboGroup);
//        String[] parameterTypes = {"java.lang.Long"};
//        methodInfo.setParameterTypes(parameterTypes);
//        Object[] args = {new Long(envId)};
//        methodInfo.setArgs(args);
//        taskParam.put(TaskParam.PARAM, new Gson().toJson(methodInfo));
//        taskParam.setTaskDef(new TaskDefBean(STaskDef.DubboTask));
//        taskParam.setBizId("health_check_" + envId);
//        int taskId = myScheduleService.submitTask(taskParam).getData();
//        projectEnv.setHealthCheckTaskId(taskId);
//        dao.update(projectEnv);
//        return true;
//    }
//
//    /**
//     * 通知mischedule关闭应用定时调度
//     *
//     * @param envId
//     * @return
//     */
//    public Boolean stopAppHealthCheck(long envId) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return false;
//        }
//        int taskId = projectEnv.getHealthCheckTaskId();
//        Task task = myScheduleService.getTaskInfo(taskId).getData();
//        if (null == task) {
//            return false;
//        }
//        log.info("stopAppHealthCheck: envId:{} taskId:{}", envId, taskId);
//        return myScheduleService.pause(taskId).getData();
//    }
//
//    /**
//     * mischedule定时调度监测应用健康逻辑
//     *
//     * @param envId
//     */
//    @Override
//    public void doAppHealthCheck(Long envId) {
//        log.info("doAppHealthCheck: {}", envId);
//        Stopwatch sw = Stopwatch.createStarted();
//        Object obj = allMachineHealthChecks.putIfAbsent(envId, new Object());
//        if (null == obj) {
//            try {
//                ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
//                if (null == projectPipeline) {
//                    return;
//                }
//                DeployInfo deployInfo = projectPipeline.getDeployInfo();
//                if (null == deployInfo) {
//                    return;
//                }
//                String healthCheckUrl = projectPipeline.getDeploySetting().getHealthCheckUrl();
//                if (StringUtils.isEmpty(healthCheckUrl)) {
//                    doAppHealthCheckByPid(envId, projectPipeline, deployInfo);
//                } else if (healthCheckUrl.startsWith("dubbo:")) {
//                    doAppHealthCheckByDubbo(envId, projectPipeline, deployInfo, healthCheckUrl);
//                }
//                dao.update(projectPipeline);
//            } catch (Exception e) {
//                log.error("doAppHealthCheck exception: ", e);
//            } finally {
//                log.info("doAppHealthCheck time: {}", sw.elapsed(TimeUnit.MILLISECONDS));
//                allMachineHealthChecks.remove(envId);
//            }
//        } else {
//            log.info("doAppHealthCheck discard:", envId);
//        }
//    }
//
//    /**
//     * 通过进程id监测健康状态
//     *
//     * @param envId
//     * @param projectPipeline
//     * @param deployInfo
//     */
//    private void doAppHealthCheckByPid(long envId, ProjectPipeline projectPipeline, DeployInfo deployInfo) {
//        log.info("HealthServiceImp#doAppHealthCheckByPid: {}", envId);
//        deployInfo.getDeployBatches().parallelStream().forEach(it -> {
//            it.getDeployMachineList().parallelStream().forEach(dm -> {
//                final int appDeployStatus = dm.getAppDeployStatus();
//                if (appDeployStatus == AppDeployStatus.ONLINE.getId()) {
//                    final int failNum = dm.getFailNum() + 1;
//                    RemotingCommandBo remotingCommand = projectDeploymentService.physicalMachineInfo(projectPipeline, dm.getIp()).getData();
//                    if (null != remotingCommand) {
//                        byte[] body = remotingCommand.getBody();
//                        if (null != body) {
//                            AgentResult<List<String>> res = new Gson().fromJson(new String(body), new TypeToken<AgentResult<List<String>>>() {
//                            }.getType());
//                            // 有进程信息, 健康监测成功
//                            if (res.getData().size() != 0) {
//                                dm.setFailNum(0);
//                                return;
//                            }
//                        }
//                    }
//                    // 健康监测失败处理
//                    log.info("HealthServiceImp#doAppHealthCheckByPid fail: envId-{} ip-{}", envId, dm.getIp());
//                    if (failNum < TOTAL_FAIL_NUM) {
//                        dm.setFailNum(failNum);
//                    } else {
//                        // 重启，emil报警
//                        projectDeploymentService.physicalMachineOnline(projectPipeline, dm.getIp(), -1);
//                        try {
//                            notify(envId, dm);
//                        } catch (Exception e) {
//                            log.error(" when ip = {} pid health check send msg, notify project owner failed", dm.getIp(), e);
//                        }
//                        dm.setFailNum(0);
//                    }
//                }
//            });
//        });
//    }
//
//    /**
//     * 通过dubbo方式监测健康状况
//     *
//     * @param envId
//     * @param projectPipeline
//     * @param deployInfo
//     * @param healthCheckUrl
//     */
//    private void doAppHealthCheckByDubbo(long envId, ProjectPipeline projectPipeline, DeployInfo deployInfo, String healthCheckUrl) {
//        log.info("HealthServiceImp#doAppHealthCheckByDubbo: {}", envId);
//        try {
//            String[] ss = healthCheckUrl.split("://|/");
//            MethodInfo mi = new MethodInfo();
//            mi.setIp(ss[1]);
//            mi.setServiceName(ss[2]);
//            mi.setGroup(ss[3]);
//            mi.setMethodName(ss[4]);
//            // serviceName: providers:com.xiaomi.youpin.zzytest.api.service.DubboHealthService:staging
//            final List<Instance> instances = nacosNaming.getAllInstances("providers:" + mi.getServiceName() + ":" + mi.getGroup());
//            deployInfo.getDeployBatches().parallelStream().forEach(it -> {
//                it.getDeployMachineList().parallelStream().forEach(dm -> {
//                    final int appDeployStatus = dm.getAppDeployStatus();
//                    if (appDeployStatus == AppDeployStatus.ONLINE.getId()) {
//                        final int failNum = dm.getFailNum() + 1;
//                        Optional<Instance> optional = instances.stream().filter(instance -> instance.getIp().equals(dm.getIp()) && instance.isHealthy()).findFirst();
//                        if (optional.isPresent()) {
//                            dm.setFailNum(0);
//                            return;
//                        }
//                        log.info("HealthServiceImp#doAppHealthCheckByDubbo fail: {}", dm.getIp());
//                        if (failNum < TOTAL_FAIL_NUM) {
//                            dm.setFailNum(failNum);
//                        } else {
//                            // 重启，emil报警
//                            projectDeploymentService.physicalMachineOnline(projectPipeline, dm.getIp(), -1);
//                            try {
//                                notify(envId, dm);
//                            } catch (Exception e) {
//                                log.error(" when ip = {} dubbo health check send msg, notify project owner failed", dm.getIp(), e);
//                            }
//                            dm.setFailNum(0);
//                        }
//                    }
//                });
//            });
//        } catch (NacosException e) {
//            log.warn("HealthServiceImp#doAppHealthCheck nacos exception: ", e.getMessage());
//        }
//    }
//
//    /**
//     * 邮件通知项目owner
//     *
//     * @param envId
//     * @param dm
//     */
//    private void notify(long envId, DeployMachine dm) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null != projectEnv
//            && 0 != projectEnv.getHealthCheckTaskId()) {
//            Project project = projectService.getProjectById(projectEnv.getProjectId()).getData();
//            if (null != project) {
//                String title = "米效应用健康报警";
//                String content = "你好, 项目" + project.getName() + "在机器("
//                    + dm.getIp() + ")上处于非健康状态, 已尝试重试启动, 请查" +
//                    "看项目是否处于健康状态。";
//                // 健康监测报警
//                StringBuffer sb = new StringBuffer();
//                sb.append("应用健康报警");
//                sb.append("\n项目: " + project.getId() + "-" + project.getName());
//                sb.append("\n机器: " + dm.getHostname() + "[" + dm.getIp() + "]");
//                sb.append("\n已尝试重启");
//                feiShuService.sendMsg("", sb.toString());
//                List<ProjectRole> members = dao.query(ProjectRole.class, Cnd.where("projectId", "=", project.getId()).and("roleType", "=", RoleType.Owner.ordinal()));
//                members.parallelStream().forEach(member -> {
//                    Account account = userService.queryUserById(member.getAccountId());
//                    if (null != account) {
//                        try {
//                            HttpUtils.sendEmail("http://support.d.xiaomi.net/mail/send?mailType=OTHER",
//                                    account.getEmail(), title, content);
//                        } catch (Exception e) {
//                            log.error("health check send email failed , email = {} , content = {}", account.getEmail(), content, e);
//                        }
//                        try {
//                            feiShuService.sendMsg2Person(account.getUserName(),sb.toString());
//                        } catch (Exception e) {
//                            log.error("health check send feishu msg failed , userName = {} , content = {}", account.getUserName(), sb.toString(), e);
//                        }
//                    }
//                });
//            }
//        }
//    }
//}
