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

package com.xiaomi.youpin.gwdash.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.common.InvokeCallback;
import com.xiaomi.data.push.rpc.netty.ResponseFuture;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.gwdash.agent.processor.IProjectDeploymentService;
import com.xiaomi.youpin.gwdash.bo.DeployInfo;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.*;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.mischedule.api.service.bo.HealthParam;
import com.xiaomi.youpin.mischedule.api.service.bo.PipelineInfo;
import com.xiaomi.youpin.mischedule.api.service.bo.Task;
import com.xiaomi.youpin.qps.Qps;
import com.xiaomi.youpin.quota.bo.ModifyQuotaRes;
import com.xiaomi.youpin.quota.bo.QuotaInfo;
import com.xiaomi.youpin.quota.bo.ResourceBo;
import com.xiaomi.youpin.quota.service.QuotaService;
import com.xiaomi.youpin.quota.service.ResourceService;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.*;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.Reference;
import org.glassfish.jersey.internal.guava.Sets;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tsingfu
 * @modify zhangzhiyong
 * <p>
 * 这里的许多方法是需要拿到锁才能执行的
 */
@Service
@Slf4j
public class ProjectDeploymentService implements IProjectDeploymentService {
    @Autowired
    private GwCache gwCache;

    @Autowired
    private Dao dao;

    @Autowired
    private AgentManager agentManager;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private LogService logService;

    @Autowired
    private MachineManagementService machineManagementService;

    @Autowired
    private DockerMachineSelector dockerMachineSelector;

    @Autowired
    private DockerMachineSelector2 dockerMachineSelector2;


    @Autowired
    private HealthServiceImp healthService;


    @Autowired
    private LockUtils lockUtils;


    @Autowired
    private MyScheduleService myScheduleService;

    @Reference(group = "${ref.quota.service.group}", interfaceClass = QuotaService.class, check = false)
    private QuotaService quotaService;


    @Reference(group = "${ref.quota.service.group}", interfaceClass = ResourceService.class, check = false)
    private ResourceService resourceService;

    @Autowired
    private FeiShuService feiShuService;

//    @Autowired
//    private BroadcastService broadcastService;

    @Autowired
    private GwCache cache;


    @Autowired
    private ApiServerBillingService billingService;


    /**
     * 获取负载信息
     *
     * @param envId
     * @return
     */
    @Override
    public int uptime(String envId) {

        if (StringUtils.isEmpty(envId)) {
            return Integer.MAX_VALUE;
        }

        Result<ProjectPipeline> res = pipelineService.getProjectPipelineOfEnv(Long.valueOf(envId));

        ProjectPipeline pipeline = res.getData();
        if (null == pipeline) {
            return Integer.MAX_VALUE;
        }

        String labels = pipeline.getDeploySetting().getDockerLabels();

        String uptime = LabelUtils.getLabelValue(labels, "uptime");

        if (StringUtils.isEmpty(uptime)) {
            return Integer.MAX_VALUE;
        }

        return Integer.valueOf(uptime);
    }


    /**
     * 构建发布信息
     */
    public Result<ProjectPipeline> createDeployInfo(ProjectPipeline projectPipeline, boolean docker) {
        long now = System.currentTimeMillis();
        DeployInfo deployInfo = null;
        if (docker) {
            deployInfo = projectPipeline.getDeployInfo();
        } else {
            deployInfo = new DeployInfo();
        }

        deployInfo.setCtime(now);
        deployInfo.setUtime(now);
        deployInfo.setStep(1);
        deployInfo.setStatus(TaskStatus.running.ordinal());

        // 构建发布批次
        final DeploySetting deploySetting = projectPipeline.getDeploySetting();
        final long envId = projectPipeline.getEnvId();
        // 计算当前部署机器和已部署机器差异
        List<DeployMachine> lastDeployMachineList = new ArrayList<>();
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null != projectEnv) {
            ProjectPipeline lastProjectPipeline = pipelineService.getProjectPipelineById(projectPipeline.getRollbackId()).getData();
            if (null != lastProjectPipeline
                && null != lastProjectPipeline.getDeploySetting()
                && null != lastProjectPipeline.getDeploySetting().getEnvMachineBo()) {
                lastProjectPipeline.getDeploySetting().getEnvMachineBo().stream().forEach(it -> {
                    DeployMachine dm = new DeployMachine();
                    /**
                     * 兼容来逻辑
                     */
                    MachineBo machineBo = it.getMachineBo();
                    if (null != machineBo) {
                        it.setIp(machineBo.getIp());
                        it.setMachineId(machineBo.getId());
                        it.setName(machineBo.getName());
                        it.setHostname(machineBo.getHostname());
                    }
                    dm.setIp(it.getIp());
                    dm.setName(it.getName());
                    dm.setHostname(it.getHostname());
                    dm.setId(it.getId());
                    dm.setAppDeployStatus(AppDeployStatus.ONLINE.getId());
                    lastDeployMachineList.add(dm);
                });
            }
        }
        deployInfo.setLastDeployOfflineMachine(lastDeployMachineList);
        List<EnvMachineBo> machineBos = deploySetting.getEnvMachineBo().stream().filter(it -> {
            return it.isUsed();
        }).collect(Collectors.toList());
        List<DeployMachine> deployMachineList = machineBos.stream().map(it -> {
            // 删除本次部署在的机器
            lastDeployMachineList.removeIf(machine -> {
                return machine.getIp().equals(it.getIp());
            });
            DeployMachine dm = new DeployMachine();
            dm.setIp(it.getIp());
            dm.setName(it.getName());
            dm.setHostname(it.getHostname());
            dm.setId(it.getId());
            dm.setAppDeployStatus(AppDeployStatus.ONLINE.getId());
            dm.setStatus(DeployMachineStatusEnum.WAIT.getId());
            dm.setCpuCore(it.getCpuCore());
            return dm;
        }).collect(Collectors.toList());
        List<DeployBatch> deployBatches = new ArrayList<>();
        // 构建发布批次
        int len = deployMachineList.size();
        int batchNum = deploySetting.getPolicyBatchNum();
        if (batchNum <= 0) {
            batchNum = 2;
        }
        // 堡垒批次
        if (len > 0) {
            DeployBatch deployBatch = new DeployBatch();
            deployBatch.setDeployMachineList(new ArrayList<>());
            deployBatch.setBatch(0);
            deployBatch.setFort(true);
            deployBatch.setStatus(DeployBatchStatusEnum.WAIT.getId());
            deployBatch.getDeployMachineList().add(deployMachineList.get(0));
            deployBatches.add(deployBatch);
        }
        // 其他批次
        for (int i = 1; i < len; i++) {
            int batch = (i - 1) % batchNum + 1;
            int size = deployBatches.size();
            if (batch >= size) {
                DeployBatch tmpBatch = new DeployBatch();
                tmpBatch.setBatch(batch);
                tmpBatch.setFort(false);
                tmpBatch.setStatus(DeployBatchStatusEnum.WAIT.getId());
                tmpBatch.setDeployMachineList(new ArrayList<>());
                tmpBatch.getDeployMachineList().add(deployMachineList.get(i));
                deployBatches.add(tmpBatch);
            } else {
                DeployBatch tmpBatch = deployBatches.get(batch);
                tmpBatch.getDeployMachineList().add(deployMachineList.get(i));
            }
        }
        deployInfo.setDeployBatches(deployBatches);
        projectPipeline.setDeployInfo(deployInfo);
        return Result.success(projectPipeline);
    }

    public Result<ProjectPipeline> startBatch(ProjectPipeline projectPipeline, int batch) {
        return envLockRun("startBatch", projectPipeline.getEnvId(), () -> deployTheBatch(projectPipeline, batch, false));
    }


    @Qps
    @Override
    public void autoScale(String envId, String type) {
        ScaleType scaleType = ScaleType.valueOf(type);

        envLockRun("autoScale", Long.valueOf(envId), () -> {
            log.info("audoScale envId:{}", envId);

            long now = System.currentTimeMillis();

            ProjectEnv env = dao.fetch(ProjectEnv.class, Cnd.where("id", "=", envId));

            if (now - env.getLastAutoScaleTime() < TimeUnit.MINUTES.toMillis(1)) {
                log.warn("auto scale fast envId:{}", envId);
                return null;
            }

            dao.update(ProjectEnv.class, Chain.make("last_auto_scale_time", now), Cnd.where("id", "=", env.getId()));

            long pipelineId = env.getPipelineId();

            ProjectPipeline pipeline = dao.fetch(ProjectPipeline.class, pipelineId);
            dao.fetchLinks(pipeline, null);

            ProjectEnvDeploySetting setting = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId));

            DeploySetting deploySetting = pipeline.getDeploySetting();

            log.info("autoScale DockerReplicate:{} MaxReplicate:{} type:{}", deploySetting.getDockerReplicate(), setting.getMaxReplicate(), type);


            if (scaleType.equals(ScaleType.shrink) && deploySetting.getDockerReplicate() > 1) {
                long replicate = deploySetting.getDockerReplicate() - 1;
                dockerScale0(pipeline, replicate);
                return null;
            }


            if (scaleType.equals(ScaleType.expansion) && deploySetting.getDockerReplicate() < setting.getMaxReplicate()) {
                long replicate = deploySetting.getDockerReplicate() + 1;
                dockerScale0(pipeline, replicate);
            }

            return null;
        });


    }

    private boolean envLock(long envId) {
        String key = LockUtils.deployKey(envId);
        boolean lock = lockUtils.tryLock(key);
        return lock;
    }


    private void envUnLock(long envId) {
        String key = LockUtils.deployKey(envId);
        lockUtils.unLock(key);
    }


    /**
     * 拿到锁才可以执行
     *
     * @param name
     * @param envId
     * @param callable
     * @param <R>
     * @return
     */
    public <R> R envLockRun(String name, long envId, Callable<R> callable) {
        return envLockRun(name, envId, callable, true, 3);
    }


    public <R> R envLockRun(String name, long envId, Callable<R> callable, boolean needLock, int retry) {
        if (!needLock) {
            try {
                return callable.call();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        String key = LockUtils.deployKey(envId);
        return lockUtils.lockAndRun(name, key, 1000 * retry, callable);
    }


    /**
     * 锁是不可重入的
     *
     * @param projectPipeline
     * @param replicate
     * @param forceShrink     true＝可以强制缩容
     * @return
     */
    @Qps
    public Result<Boolean> dockerScale(ProjectPipeline projectPipeline, long replicate, boolean forceShrink) {
        return envLockRun("dockerScale", projectPipeline.getEnvId(), () -> dockerScale0(projectPipeline, replicate, forceShrink));
    }

    public Result<Boolean> dockerScale0(ProjectPipeline projectPipeline, long replicate) {
        return dockerScale0(projectPipeline, replicate, false);
    }

    /**
     * 查看能否把机器数量设为replicate
     * 如果forceShrink是true,　可以缩容.
     * 如果forceShrink是false, 最近一个小时以内没有扩容过现在才能缩容.
     * replicate必须要大于或等于最小机器数量
     *
     * @param projectEnvDeploySetting 环境信息
     * @param replicate               目标机器数量.
     * @param forceShrink             　可以强制缩容
     * @param envId                   　环境id
     * @return true如果可以缩容
     */
    private boolean canShrinkCluster(ProjectEnvDeploySetting projectEnvDeploySetting, long replicate, boolean forceShrink, long envId) {
        if (forceShrink) {
            return true;
        }
        if (projectEnvDeploySetting == null) {
            return false;
        }
        long minReplicate = projectEnvDeploySetting.getReplicate();
        return minReplicate >= 0 && replicate >= minReplicate && gwCache.get(GwCache.HOUR, GwCache.expansionKey(envId)) == null;
    }

    /**
     * docker机器扩容或者缩容
     *
     * @param projectPipeline
     * @param forceShrink     true＝可以强制缩容
     * @return
     */

    public Result<Boolean> dockerScale0(ProjectPipeline projectPipeline, long replicate, boolean forceShrink) {
        long envId = projectPipeline.getEnvId();
        try {
            log.info("dockerScale pipeline:{} replicate:{}", new Gson().toJson(projectPipeline), replicate);
            long now = System.currentTimeMillis();
            ProjectEnvDeploySetting projectEnvDeploySetting = dao.fetch(ProjectEnvDeploySetting.class,
                Cnd.where("env_id", "=", envId));

            DeploySetting deploySetting = projectPipeline.getDeploySetting();
            final boolean isDocker = DeployTypeEnum.isDocker(deploySetting.getDeployType());

            if (isDocker) {
                ProjectEnv env = dao.fetch(ProjectEnv.class, envId);

                //当前机器数量
                long oldReplicate = deploySetting.getDockerReplicate();

                if (replicate <= 0) {
                    replicate = 0;
                    log.info("replicate == 0");
                }

                sendFeiShuMsg(projectPipeline.getUsername(), deploySetting, env, env.getId() + ":应用扩容或缩容:" + oldReplicate + "->" + replicate);

                stopHealthCheck(env);

                if (replicate > oldReplicate) {
                    gwCache.put(GwCache.HOUR, GwCache.expansionKey(envId), true);
                    //扩容 (把副本数替换为实际的)
                    replicate = dockerOnline(projectPipeline, deploySetting, true, replicate).getKey();
                    deploySetting.setDockerReplicate(replicate);
                } else if ((replicate < oldReplicate || replicate == 0) &&
                    canShrinkCluster(projectEnvDeploySetting, replicate, forceShrink, envId)) {
                    //缩容
                    dockerOffline(projectPipeline, replicate, now, deploySetting, oldReplicate);
                }


                if (null != projectEnvDeploySetting) {
                    long maxReplicate = projectEnvDeploySetting.getMaxReplicate();
                    if (replicate > maxReplicate) {
                        projectEnvDeploySetting.setMaxReplicate(replicate);
                    }
                    dao.update(projectEnvDeploySetting);
                }

                Optional.ofNullable(projectPipeline.getDeployInfo()).ifPresent(it -> it.setHealthCheckStatus(0));
                //统一更新pipeline
                log.info("update pipeline");
                projectPipeline.setUtime(now);

                dao.update(projectPipeline);


                env.setPipelineId(projectPipeline.getId());
                env.setUtime(now);
                dao.update(env);

                int taskId = env.getHealthCheckTaskId();

                startHealthCheck(taskId, projectPipeline);

            } else {
                log.warn("dockerScale error: is not docker");
            }
            return Result.success(true);
        } catch (Throwable ex) {
            log.error("error:{}", ex.getMessage());
            throw ex;
        } finally {
            envUnLock(envId);
        }
    }


    /**
     * 按照版本更新,并且能按照你给定的字段更新,并且在乐观锁下更新
     *
     * @param pipeline
     * @param consumer
     * @param actived
     */
    public void updatePipeline(ProjectPipeline pipeline, Consumer<ProjectPipeline> consumer, String actived) {
        int n = 20;
        while (n-- > 0) {
            consumer.accept(pipeline);
            int v = dao.updateWithVersion(pipeline, FieldFilter.create(ProjectPipeline.class, actived));
            if (v > 0) {
                break;
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(100 + new Random().nextInt(50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pipeline = dao.fetch(ProjectPipeline.class, pipeline.getId());
        }
    }


    private void dockerOffline(ProjectPipeline projectPipeline, long replicate, long now, DeploySetting deploySetting, long oldReplicate) {
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setBizId(projectPipeline.getEnvId());
        quotaInfo.setNum((int) replicate);
        quotaInfo.setProjectId(projectPipeline.getProjectId());
        quotaInfo.setCpu(deploySetting.getDockerCup());
        quotaInfo.setMem(deploySetting.getDockerMem());
        quotaInfo.setPorts(getPorts(deploySetting));
        quotaInfo.setProjectId(projectPipeline.getProjectId());
        ModifyQuotaRes modifyRes = quotaService.modifyQuota(quotaInfo);

        if (!modifyRes.getType().equals("remove")) {
            log.info("dockerOffline:{}", modifyRes.getType());
            return;
        }

        List<DeployMachine> list = modifyRes.getIps().stream().map(ip -> {
            DeployMachine dm = new DeployMachine();
            dm.setIp(ip.getIp());
            return dm;
        }).collect(Collectors.toList());

        long limit = oldReplicate - replicate;
        if (replicate == 0) {
            limit = list.size();
        }

        log.info("dockerScale limit num:{}", limit);


        List<String> ips = list.stream().limit(limit).map(it -> {
            dockerMachineOffline(projectPipeline, it.getIp(), true);
            projectPipeline.getDeployInfo().offlineDockerMachine(it.getIp());
            return it.getIp();
        }).collect(Collectors.toList());


        BillingReq billingReq = new BillingReq();
        billingReq.setResourceKeyList(ips);
        billingReq.setType(BillingReq.BillingType.offline.ordinal());
        billingReq.setEnvId(projectPipeline.getEnvId());
        billingReq.setProjectId(projectPipeline.getProjectId());
        billingService.offline(billingReq);


        sendFeiShuMsg(projectPipeline.getUsername(), deploySetting, null, "应用缩容成功 ip:" + ips);


        //容错处理
        if (replicate == 0) {
            projectPipeline.getDeployInfo().getDockerMachineList().clear();
        }

        /*
        有些机器下线了，所以在这里要更新dockerMachineList
        modifyRes.getCurrIps()是QuotaService返回的,当前集群的所有ip地址
         */
        List<DeployMachine> dockerMachineList = projectPipeline.getDeployInfo().getDockerMachineList();
        if (dockerMachineList == null) {
            dockerMachineList = new ArrayList<>();
            projectPipeline.getDeployInfo().setDockerMachineList(dockerMachineList);
        }
        List<DeployMachine> dml = modifyRes.getCurrIps().stream().map(it -> {
            DeployMachine dm = new DeployMachine();
            dm.setIp(it.getIp());
            return dm;
        }).collect(Collectors.toList());
        dockerMachineList.clear();
        dockerMachineList.addAll(dml);


        projectPipeline.setUtime(now);
        //替换为实际的副本数量
        replicate = modifyRes.getCurrIps().size();
        deploySetting.setDockerReplicate(replicate);
    }

    public void stopHealthCheck(ProjectEnv env) {
        int taskId = env.getHealthCheckTaskId();
        if (taskId > 0) {
            try {
                myScheduleService.pause(taskId);
            } catch (Throwable ex) {
                log.error("error:{}", ex.getMessage());
            }
        }
    }


    /**
     * docker机器部署
     * <p>
     * docker deploy
     *
     * @return
     */
    @Qps
    public Result<Pair<Boolean, List<MachineBo>>> dockerDeploy(ProjectPipeline projectPipeline) {
        return envLockRun("dockerDeploy", projectPipeline.getEnvId(), () -> {
            log.info("dockerDeploy:{}", new Gson().toJson(projectPipeline));
            DeploySetting deploySetting = projectPipeline.getDeploySetting();
            final boolean isDocker = DeployTypeEnum.isDocker(deploySetting.getDeployType());
            if (isDocker) {
                Pair<Integer, List<MachineBo>> pair = dockerOnline(projectPipeline, deploySetting, false, deploySetting.getDockerReplicate());
                deploySetting.setDockerReplicate(pair.getKey());
                return Result.success(Pair.of(true, pair.getValue()));
            } else {
                log.warn("dockerDeploy is not docker");
            }
            return Result.success(Pair.of(false, Lists.newArrayList()));
        });
    }


    private void updateEnvHealthCheckTaskId(int taskId, long envId) {
        dao.update(ProjectEnv.class, Chain.make("health_check_task_id", taskId), Cnd.where("id", "=", envId));
    }


    /**
     * 从新初始化健康监测
     *
     * @param envId
     */
    @Qps
    public boolean initHealthCheck(long envId) {
        return envLockRun("initHealthCheck", envId, () -> {
            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
            ProjectEnv env = projectEnvService.getProjectEnvById(envId).getData();
            if (projectPipeline == null || env == null) {
                return false;
            }
            int taskId = env.getHealthCheckTaskId();
            if (0 == taskId) {
                return true;
            }
            int rid = startHealthCheck0(taskId, projectPipeline, it -> getUrls(it));
            Optional.ofNullable(projectPipeline.getDeployInfo()).ifPresent(it -> {
                it.setHealthCheckStatus(0);
                dao.update(projectPipeline);
            });
            //尝试启动任务
            MSafe.execute(() -> myScheduleService.start(taskId));
            log.info("initHealthCheck taskId:{}", rid);
            return true;
        });
    }


    private int fixHealthCheckTask(ProjectEnv projectEnv, ProjectPipeline projectPipeline) {
        log.info("closeHealthCheck fix health check task");
        int taskId = startHealthCheck0(0, projectPipeline, (info) -> Lists.newArrayList());
        projectEnv.setHealthCheckTaskId(taskId);
        dao.update(projectEnv);
        return taskId;
    }

    /**
     * 清空健康监测
     */
    @Qps
    public boolean clearHealthCheck(long envId) {
        return envLockRun("clearHealthCheck", envId, () -> {
            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
            ProjectEnv env = projectEnvService.getProjectEnvById(envId).getData();
            if (projectPipeline == null || env == null) {
                return false;
            }
            int taskId = env.getHealthCheckTaskId();
            if (0 == taskId) {
                return true;
            }

            Task data = myScheduleService.getTaskInfo(taskId).getData();
            //数据有问题了,这里进行修复
            if (null == data) {
                log.info("clearHealthCheck task is null fix {} {}", envId, taskId);
                taskId = fixHealthCheckTask(env, projectPipeline);
            } else {
                taskId = startHealthCheck0(taskId, projectPipeline, (info) -> Lists.newArrayList());
            }
            int _taskId = taskId;
            log.info("clearHealthCheck taskId:{}", _taskId);
            Optional.ofNullable(projectPipeline.getDeployInfo()).ifPresent(it -> {
                it.setHealthCheckStatus(1);
                dao.update(projectPipeline);
            });
            //尝试停止任务
            MSafe.execute(() -> myScheduleService.pause(_taskId));
            return true;
        });
    }

    /**
     * @param envId
     * @return false 未开启
     * true 已经开启
     */
    public Integer getDockerHealthCheckStatus(long envId) {
        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
        if (null == projectEnv || 0 == projectEnv.getHealthCheckTaskId()) {
            return -1;
        }
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(projectEnv.getPipelineId()).getData();
        if (null == projectPipeline) {
            return -1;
        }
        return Optional.ofNullable(projectPipeline.getDeployInfo()).map(it -> it.getHealthCheckStatus()).orElse(-1);
    }


    public List<String> getUrls(ProjectPipeline projectPipeline) {
        ProjectEnvDeploySetting envDeploySetting = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", projectPipeline.getEnvId()));
        String healthCheckUrl = envDeploySetting.getHealthCheckUrl();
        if (StringUtils.isEmpty(healthCheckUrl)) {
            return Lists.newArrayList();
        }
        DeployInfo info = projectPipeline.getDeployInfo();
        return info.getDockerMachineList().stream()
            .filter(it -> it.getStatus() == 0)
            .map(it -> {
                String ip = it.getIp();
                return String.format(healthCheckUrl, ip);
            }).collect(Collectors.toList());
    }


    public int startHealthCheck(int oldTaskId, ProjectPipeline projectPipeline) {
        return startHealthCheck0(oldTaskId, projectPipeline, it -> getUrls(it));
    }


    private int startHealthCheck0(int oldTaskId, ProjectPipeline projectPipeline, Function<ProjectPipeline, List<String>> urlFunc) {
        String healthCheckUrl = projectPipeline.getDeploySetting().getHealthCheckUrl();
        if (StringUtils.isEmpty(healthCheckUrl)) {
            log.info("healthCheckUrl is empty");
            return -1;
        }

        HealthParam healthParam = new HealthParam();
        PipelineInfo pipelineInfo = new PipelineInfo();
        pipelineInfo.setEnvId(projectPipeline.getEnvId());
        pipelineInfo.setPipelineId(projectPipeline.getId());
        pipelineInfo.setProjectId(projectPipeline.getProjectId());
        healthParam.setPipelineInfo(pipelineInfo);

        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            log.info("pipeline's deployinfo is null， pipeline id:{}", projectPipeline.getId());
            return oldTaskId;
        }

        log.info("start health check pipeline id:{} machine list:{}", projectPipeline.getId(), deployInfo.getDockerMachineList());

        if (null == deployInfo.getDockerMachineList()) {
            log.warn("machine list is null");
            return oldTaskId;
        }

        List<String> urls = urlFunc.apply(projectPipeline);
        healthParam.setUrls(urls);

        int taskId = healthService.startHealthCheck(oldTaskId, projectPipeline.getEnvId(), healthParam);

        log.info("health check taskId:{}", taskId);

        if (0 != taskId && taskId != oldTaskId) {
            //只会修改env
            updateEnvHealthCheckTaskId(taskId, projectPipeline.getEnvId());
        }

        return taskId;
    }


    /**
     * 获取指定需要开启的端口(http dubbo gson_dubbo)
     *
     * @param deploySetting
     * @return
     */
    private Set<Integer> getPorts(DeploySetting deploySetting) {
        String httpPort = LabelUtils.getLabelValue(deploySetting.getDockerLabels(), "http_port");
        String dubboPort = LabelUtils.getLabelValue(deploySetting.getDockerLabels(), "dubbo_port");
        String gsonDubboPort = LabelUtils.getLabelValue(deploySetting.getDockerLabels(), "gson_dubbo_port");

        Set<Integer> ports = Sets.newHashSet();
        if (StringUtils.isNotEmpty(httpPort)) {
            ports.add(Integer.valueOf(httpPort));
        }

        if (StringUtils.isNotEmpty(dubboPort)) {
            ports.add(Integer.valueOf(dubboPort));
        }

        if (StringUtils.isNotEmpty(gsonDubboPort)) {
            ports.add(Integer.valueOf(gsonDubboPort));
        }
        return ports;
    }


    /**
     * @param projectPipeline
     * @param deploySetting   如果不是扩展,则所有机器都执行发布流程
     */
    private Pair<Integer, List<MachineBo>> dockerOnline(ProjectPipeline projectPipeline, DeploySetting deploySetting, boolean expansion, long replicate) {

        initDockerOnline(projectPipeline);
        String labels = deploySetting.getDockerLabels();
        Map<String, String> labelMap = getLabelMap(labels);

        Set<Integer> ports = getPorts(deploySetting);

        //构造筛选参数
        DockerQueryParam param = getParam(projectPipeline, deploySetting, expansion, replicate, labelMap, ports);

        //获取到可用的实例数量
        List<MachineBo> list = dockerMachineSelector2.select(param).stream().collect(Collectors.toList());

        log.info("select machine list:{} num:{}", list, list.size());

        DeployInfo deployInfo = projectPipeline.getDeployInfo();

        final List<DeployMachine> dockerMachineList = deployInfo.getDockerMachineList() == null ? Lists.newArrayList() : deployInfo.getDockerMachineList();


        //下线多余的机器(基本都是自己手动扩容的)
        List<MachineBo> removeMachines = param.getRemoveMachines();
        Optional.ofNullable(removeMachines).ifPresent(ms -> ms.stream().forEach(it -> {
            dockerMachineOffline(projectPipeline, it.getIp());
        }));

        if (expansion) {
            //加入扩容的机器
            list.stream().forEach(it -> {
                DeployMachine dm = new DeployMachine();
                dm.setName(it.getName());
                dm.setIp(it.getIp());
                dm.setHostname(it.getHostname());
                dockerMachineList.add(dm);
            });
        } else {
            //发布所有机器
            List<DeployMachine> dml = list.stream().map(it -> {
                DeployMachine dm = new DeployMachine();
                dm.setName(it.getName());
                dm.setIp(it.getIp());
                dm.setHostname(it.getHostname());
                return dm;
            }).collect(Collectors.toList());
            dockerMachineList.clear();
            dockerMachineList.addAll(dml);
        }

        log.info("deployInfo machine pipelineId:{} size:{} list:{}", projectPipeline.getId(), dockerMachineList.size(), dockerMachineList);
        deployInfo.setDockerMachineList(dockerMachineList);


        long envId = projectPipeline.getEnvId();
        ProjectEnv env = dao.fetch(ProjectEnv.class, envId);
        env.setPipelineId(projectPipeline.getId());
        dao.update(env);


        if (expansion) {
            //扩容直接发布
            log.info("docker deploy expansion instance num:{}", list.size());
            List<String> ips = list.stream().map(machine -> {
                dockerMachineOnline(projectPipeline, machine, machine.getIp(), -1);
                return machine.getIp();
            }).collect(Collectors.toList());

            BillingReq billingReq = new BillingReq();
            billingReq.setResourceKeyList(ips);
            billingReq.setType(BillingReq.BillingType.online.ordinal());
            billingReq.setEnvId(envId);
            billingReq.setProjectId(projectPipeline.getProjectId());
            billingService.online(billingReq);


            sendFeiShuMsg(projectPipeline.getUsername(), deploySetting, env, "应用扩容成功 ip:" + ips);
        }

        return Pair.of(param.getRealNum(), list);
    }

    public void sendFeiShuMsg(String userName, DeploySetting deploySetting, ProjectEnv env, String msg) {
        // broadcastService.sendMsg(userName, msg);

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(msg);
            sb.append("\n时间: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            sb.append("\n部署项目: " + deploySetting.getGitGroup() + "/" + deploySetting.getGitName());
            sb.append("\n部署环境: " + (null == env ? "" : env.getName()));
            feiShuService.sendMsg("", sb.toString());
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    private Map<String, String> getLabelMap(String labels) {
        Map<String, String> labelMap = Maps.newHashMap();
        if (StringUtils.isNotEmpty(labels)) {
            Arrays.stream(labels.split(",")).forEach(it -> {
                String[] ss = it.split("=");
                labelMap.put(ss[0], ss[1]);
            });
        }
        return labelMap;
    }

    private DockerQueryParam getParam(ProjectPipeline projectPipeline, DeploySetting deploySetting, boolean expansion, long replicate, Map<String, String> labelMap, Set<Integer> ports) {
        return DockerQueryParam.builder()
            .envId(projectPipeline.getEnvId())
            .cpuNum(deploySetting.getDockerCup())
            .mem(deploySetting.getDockerMem())
            .ports(ports)
            .projectId(projectPipeline.getProjectId())
            .expansion(expansion)
            .num(replicate)
            .labels(labelMap)
            .pair(Pair.of("docker", "true"))
            .build();
    }

    private void initDockerOnline(ProjectPipeline projectPipeline) {
        if (null == projectPipeline.getDeployInfo()) {
            DeployInfo di = new DeployInfo();
            List<DeployMachine> dmList = new ArrayList<>();
            di.setDockerMachineList(dmList);
            projectPipeline.setDeployInfo(di);
        }


        if (projectPipeline.getDeployInfo().getDockerMachineList() == null) {
            projectPipeline.getDeployInfo().setDockerMachineList(Lists.newArrayList());
        }
    }

    public Result<ProjectPipeline> retryBatch(ProjectPipeline projectPipeline, int batch) {
        return deployTheBatch(projectPipeline, batch, true);
    }

    public Result<RemotingCommand> physicalMachineInfo(ProjectPipeline projectPipeline, String ip) {
        ServiceReq serviceReq = getServiceReq(projectPipeline, "info");
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            return Result.success(agentManager.send(optional.get(), AgentCmd.serviceReq, new Gson().toJson(serviceReq), 8000));
        }
        return Result.success(null);
    }

    public Result<RemotingCommand> dockerMachineInfo(ProjectPipeline projectPipeline, String ip) {
        DockerReq dockerReq = getDockerReq(projectPipeline, "info");
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            return Result.success(agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 8000));
        }
        return Result.success(null);
    }

    public Result<RemotingCommand> getLogSnapshot(ProjectPipeline projectPipeline, String ip) {
        DockerReq dockerReq = getDockerReq(projectPipeline, "log");
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            return Result.success(agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 8000));
        }
        return Result.success(null);
    }


    public Result<Boolean> physicalMachineOnline(ProjectPipeline projectPipeline, String ip, int batch) {
        ServiceReq serviceReq = getServiceReq(projectPipeline, "start");
        serviceReq.getAttachments().put("ip", ip);
        serviceReq.getAttachments().put("msgType", DataHubServiceImp.MachineDeployment);
        serviceReq.getAttachments().put("batch", String.valueOf(batch));
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            agentManager.send(optional.get(), AgentCmd.serviceReq, new Gson().toJson(serviceReq), 35000, null);
            return Result.success(true);
        }
        return Result.success(false);
    }

    public Result<Boolean> dockerMachineOnline(ProjectPipeline projectPipeline, MachineBo machine, String ip, int batch) {
        DockerReq dockerReq = getDockerReq(projectPipeline, "build");

        //设置选中的cpu
        if (null != machine.getCpuCore()) {
            dockerReq.setCpu(machine.getCpuCore().stream().map(it -> it.toString()).collect(Collectors.joining(",")));
        }

        dockerReq.getAttachments().put("ip", ip);
        dockerReq.getAttachments().put("msgType", DataHubServiceImp.DockerDeployment);
        dockerReq.getAttachments().put("batch", String.valueOf(batch));

        Optional<String> optional = agentManager.getClientAddress(ip);
        log.info("docker machine online ip:{}", optional);
        if (optional.isPresent()) {
            agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 3000, responseFuture -> {
                RemotingCommand res = responseFuture.getResponseCommand();
                log.info("------>dockerMachineOnline res: {}", res);
            });
            return Result.success(true);
        }
        return Result.success(false);
    }

    public Result<Boolean> physicalMachineNuke(ProjectPipeline projectPipeline, String ip) {
        ServiceReq serviceReq = getServiceReq(projectPipeline, "nuke");
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            agentManager.send(optional.get(), AgentCmd.serviceReq, new Gson().toJson(serviceReq), 3000, null);
            return Result.success(true);
        }
        return Result.success(false);
    }

    /**
     * nuke 掉 docker 的服务
     *
     * @return
     */
    public Result<Boolean> dockerMachineNuke(ProjectPipeline projectPipeline, ProjectEnv projectEnv, String ip) {
        log.info("dockerMachineNuke ip:{} envId:{}", ip, projectPipeline.getEnvId());
        List<DeployMachine> list = projectPipeline.getDeployInfo().getDockerMachineList();
        list = list.stream().filter(it -> !it.getIp().equals(ip)).collect(Collectors.toList());
        projectPipeline.getDeployInfo().setDockerMachineList(list);
        DeploySetting deploySetting = projectPipeline.getDeploySetting();
        deploySetting.setDockerReplicate(list.size());

        DockerReq dockerReq = getDockerReq(projectPipeline, "nuke");
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            RemotingCommand res = agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 20000);
            byte[] body = res.getBody();
            NukeRes nukeRes = new Gson().fromJson(new String(body), NukeRes.class);
            if (nukeRes.getCode() == 0) {
                //清除掉配额
                com.xiaomi.youpin.quota.bo.Result<Boolean> removeQuotaRes = quotaService.removeQuota(ip, projectPipeline.getEnvId(), projectEnv.getProjectId());
                log.info("remove qutoa res:{}", removeQuotaRes.getCode());
            } else {
                log.error("nuke {} error:{}", nukeRes.getName(), nukeRes.getMessage());
                return Result.success(false);
            }
            dao.update(projectPipeline);
            int taskId = startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
            log.info("start health check taskId:{}", taskId);
            return Result.success(true);
        }
        return Result.success(false);
    }


    /**
     * 根据ip下线一台docker服务
     * 下线此ip的健康监测
     *
     * @return
     */
    public Result<Boolean> dockerMachineShutdownWithIp(ProjectPipeline projectPipeline, ProjectEnv projectEnv, String ip) {
        List<DeployMachine> list = projectPipeline.getDeployInfo().getDockerMachineList();
        boolean has = list.stream().filter(it -> it.getIp().equals(ip) && it.getStatus() == 0).findAny().isPresent();
        if (!has) {
            log.warn("ip is null");
            Result.success(false);
        }

        list = list.stream().map(it -> {
            if (it.getIp().equals(ip)) {
                it.setStatus(2);
            }
            return it;
        }).collect(Collectors.toList());

        projectPipeline.getDeployInfo().setDockerMachineList(list);
        DockerReq dockerReq = getDockerReq(projectPipeline, DockerCmd.shutdown.name());
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 3000, null);
            //修改健康监测
            int taskId = startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
            log.info("start health check taskId:{}", taskId);
            dao.update(projectPipeline);
            return Result.success(true);
        }
        return Result.success(false);
    }


    /**
     * 根据ip上线一台docker服务
     * 上线此ip的健康监测
     *
     * @param projectPipeline
     * @param projectEnv
     * @param ip
     * @return
     */
    public Result<Boolean> dockerMachineOnlineWithIp(ProjectPipeline projectPipeline, ProjectEnv projectEnv, String ip) {
        List<DeployMachine> list = projectPipeline.getDeployInfo().getDockerMachineList();
        boolean has = list.stream().filter(it -> it.getIp().equals(ip) && it.getStatus() == 2).findAny().isPresent();
        //有这样一台曾经被下线的docker服务
        if (has) {
            list = list.stream().map(it -> {
                if (it.getIp().equals(ip)) {
                    it.setStatus(0);
                }
                return it;
            }).collect(Collectors.toList());
            projectPipeline.getDeployInfo().setDockerMachineList(list);

            DockerReq dockerReq = getDockerReq(projectPipeline, DockerCmd.start.name());
            Optional<String> optional = agentManager.getClientAddress(ip);
            if (optional.isPresent()) {
                log.info("dockerMachineOnlineWithIp ip:{}", ip);
                agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 3000, new InvokeCallback() {
                    @Override
                    public void operationComplete(ResponseFuture responseFuture) {
                        if (null != responseFuture.getResponseCommand()) {
                            log.info("dockerMachineOnlineWithIp:{} {}", ip, responseFuture.getResponseCommand().getCode());
                        }
                    }
                });
                int taskId = startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
                log.info("start health check taskId:{}", taskId);
                dao.update(projectPipeline);
            }

        } else {
            log.warn("ip is null");
        }
        return Result.success(true);
    }


    public Result<RemotingCommand> physicalMachineOffline(ProjectPipeline projectPipeline, String ip) {
        ServiceReq serviceReq = getServiceReq(projectPipeline, "shutdown");
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            return Result.success(agentManager.send(optional.get(), AgentCmd.serviceReq, new Gson().toJson(serviceReq), 8000));
        }
        return Result.success(null);
    }

    public Result<RemotingCommand> dockerMachineOffline(ProjectPipeline projectPipeline, String ip) {
        return dockerMachineOffline(projectPipeline, ip, false);
    }

    public Result<RemotingCommand> dockerMachineOffline(ProjectPipeline projectPipeline, String ip, boolean async) {
        // 构建docker发布命令
        try {
            log.info("docker machine offline:{}", ip);
            DockerReq dockerReq = getDockerReq(projectPipeline, "shutdown");
            Optional<String> optional = agentManager.getClientAddress(ip);
            if (optional.isPresent()) {
                if (async) {
                    agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 8000, responseFuture -> {
                        if (null == responseFuture.getResponseCommand() || responseFuture.getResponseCommand().getCode() != 0) {
                            feiShuService.sendMsg("", "shutdown ip:" + ip + " failure  envId:" + projectPipeline.getEnvId());
                        }
                        log.info("docker machine offline:{}", responseFuture);
                    });
                    return Result.success(null);
                } else {
                    return Result.success(agentManager.send(optional.get(), AgentCmd.dockerReq, new Gson().toJson(dockerReq), 8000));
                }

            }
        } catch (Throwable ex) {
            feiShuService.sendMsg("", "docker machine offline fail pipelineId" + projectPipeline.getId() + " ip:" + ip);
            log.error("dockerMachineOffline {} error:{}", ip, ex.getMessage());
        }
        return Result.success(null);
    }

    private Result<ProjectPipeline> deployTheBatch(ProjectPipeline projectPipeline, int batch, boolean isRetry) {
        // 部署批次
        log.info("ProjectDeploymentService deployTheBatch info: {}, {}, {}", projectPipeline, batch, isRetry);
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        List<DeployBatch> deployBatchList = deployInfo.getDeployBatches();
        if (batch >= 0 && deployBatchList.size() > batch) {
            if (batch > 0) {
                DeployBatch lastDeployBatch = deployBatchList.get(batch - 1);
                int lastStatus = lastDeployBatch.getStatus();
                if (lastStatus != DeployBatchStatusEnum.ALL_SUCCESS.getId()
                    && lastStatus != DeployBatchStatusEnum.PART_SUCCESS.getId()
                    && lastStatus != DeployBatchStatusEnum.PART_FAIL.getId()) {
                    return new Result<>(1, "需要等待上批次部署完毕", null);
                }
            }
            deployInfo.setStep(1);
            deployInfo.setStatus(TaskStatus.running.ordinal());

            DeployBatch deployBatch = deployBatchList.get(batch);
            deployBatch.setStatus(DeployBatchStatusEnum.RUNNING.getId());
            List<DeployMachine> deployMachineList = deployBatch.getDeployMachineList();
            deployMachineList.stream()
                .filter(machine -> !isRetry || machine.getStatus() == NotifyMsg.STATUS_FAIL)
                .forEach(machine -> {
                    machine.setStatus(NotifyMsg.STATUS_PROGRESS);
                });
            dao.update(projectPipeline);
            Iterator<DeployMachine> dmit = deployMachineList.stream()
                .filter(machine -> machine.getStatus() == NotifyMsg.STATUS_PROGRESS)
                .collect(Collectors.toList()).iterator();
            while (dmit.hasNext()) {
                DeployMachine machine = dmit.next();
                String ip = machine.getIp();
                boolean isDocker = DeployTypeEnum.isDocker(projectPipeline.getDeploySetting().getDeployType());
                boolean isSuccess;
                if (isDocker) {
                    isSuccess = dockerMachineOnline(projectPipeline, Optional.of(machine).map(it -> {
                        MachineBo mb = new MachineBo();
                        mb.setIp(it.getIp());
                        mb.setCpuCore(it.getCpuCore());
                        return mb;
                    }).get(), machine.getIp(), batch).getData();
                } else {
                    isSuccess = physicalMachineOnline(projectPipeline, ip, batch).getData();
                }

                if (!isSuccess) {
                    deployBatch.setStatus(DeployBatchStatusEnum.PART_FAIL.getId());
                    machine.setStatus(NotifyMsg.STATUS_FAIL);
                    dao.update(projectPipeline);
                    logService.saveLog(LogService.ProjectDeployment + ip, projectPipeline.getId(), "[ERROR] ip " + ip + " offline\n");
                }
            }
        }
        log.info("ProjectDeploymentService deployTheBatch info end: {}", projectPipeline);
        return Result.success(projectPipeline);
    }

    private DockerReq getDockerReq(ProjectPipeline projectPipeline, String cmd) {
        final ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
        final DeploySetting setting = projectPipeline.getDeploySetting();
        int deployType = setting.getDeployType();
        // 构建docker发布命令
        DockerReq dockerReq = new DockerReq();
        dockerReq.setCmd(cmd);
        dockerReq.setId(projectPipeline.getId());
        dockerReq.setMem(setting.getDockerMem());
        dockerReq.setCpu(String.valueOf(setting.getDockerCup()));
        dockerReq.setLogPath(setting.getDockerLogPath());
        dockerReq.setHeapSize(String.valueOf(setting.getDeploySettingHeapSize()));
        dockerReq.setServicePath(setting.getDeploySettingPath());
        dockerReq.setJvmParams(setting.getJvmParams());
        long projectId = projectPipeline.getProjectId();
        long envId = projectPipeline.getEnvId();
        dockerReq.setProjectId(projectId);
        dockerReq.setEnvId(envId);
        dockerReq.setNetwork("p" + projectId + "e" + envId);
        dockerReq.setLabels(projectPipeline.getDeploySetting().getDockerLabels());
        dockerReq.setAttachments(new HashMap<>(0));
        dockerReq.getAttachments().put("volume", setting.getDockerVolume());
        dockerReq.setBlkioWeight(500);
        String jarName = projectCompileRecord.getJarName();
        if (deployType == DeployTypeEnum.DOCKERFILE.getId()) {
            String name = (jarName.split("/mixiao/")[1]).replace(":", "-");
            dockerReq.setImageName(jarName.toLowerCase());
            dockerReq.setJarName(name);
            dockerReq.setContainerName(name.toLowerCase());
            dockerReq.setPull(true);
        } else {
            String name = jarName.endsWith(".jar") ?
                jarName.substring(0, jarName.length() - 4) :
                jarName;
            dockerReq.setJarName(jarName);
            dockerReq.setDownloadKey(projectCompileRecord.getJarKey());
            dockerReq.setImageName(name.toLowerCase());
            dockerReq.setContainerName(name.toLowerCase());
            dockerReq.setPull(false);
        }
        log.info("getDockerReq:{}", dockerReq);
        return dockerReq;
    }

    private ServiceReq getServiceReq(ProjectPipeline projectPipeline, String cmd) {
        final ProjectCompileRecord projectCompileRecord = projectPipeline.getProjectCompileRecord();
        DeploySetting setting = projectPipeline.getDeploySetting();
        // 构造物理机发布命令
        ServiceReq serviceReq = new ServiceReq();
        serviceReq.setCmd(cmd);
        serviceReq.setId(projectPipeline.getId());
        String jarName = projectCompileRecord.getJarName();
        serviceReq.setDownloadKey(projectCompileRecord.getJarKey());
        serviceReq.setDownloadUrl(projectCompileRecord.getUrl());
        serviceReq.setJarName(jarName);
        serviceReq.setJvmParams(setting.getJvmParams());
        serviceReq.setUserRight(setting.getDeploymentAuthorityName());
        if ("info".equals(cmd) && StringUtils.isNotEmpty(jarName)) {
            // 正则匹配进程
            serviceReq.setJarName("'" + jarName.replaceAll("-[0-9]+\\.jar$", "-[0-9]*\\\\.jar") + "'");
        }
        if (null != setting) {
            serviceReq.setHeapSize(String.valueOf(setting.getDeploySettingHeapSize()));
            serviceReq.setServicePath(setting.getDeploySettingPath());
        } else {
            ProjectEnvDeploySetting projectEnvDeploySetting = dao.fetch(ProjectEnvDeploySetting.class, projectPipeline.getEnvId());
            if (null != projectEnvDeploySetting) {
                serviceReq.setHeapSize(String.valueOf(projectEnvDeploySetting.getHeapSize()));
                serviceReq.setServicePath(projectEnvDeploySetting.getPath());
            }
        }
        serviceReq.setAttachments(new HashMap<>());
        return serviceReq;
    }

    public Result<Boolean> offlineLastMachine(ProjectPipeline projectPipeline) {
        ProjectPipeline lastProjectPipeline = pipelineService.getProjectPipelineById(projectPipeline.getRollbackId()).getData();
        if (null == lastProjectPipeline) {
            return new Result<>(1, "回退的pipeline找不到", false);
        }
        List<DeployMachine> lastDeployOfflineMachine = projectPipeline.getDeployInfo().getLastDeployOfflineMachine();
        ProjectCompileRecord lastProjectCompileRecord = lastProjectPipeline.getProjectCompileRecord();
        DeploySetting lastDeploySetting = lastProjectPipeline.getDeploySetting();
        if (null != lastProjectCompileRecord
            && null != lastDeployOfflineMachine
            && null != lastDeploySetting) {
            lastDeployOfflineMachine.stream().forEach(it -> {
                physicalMachineOffline(lastProjectPipeline, it.getIp());
            });
        }
        return Result.success(true);
    }


    @Override
    public void updateResourceInfo(String ip, long bizId, int cpu, long mem, Set<Integer> ports, Map<String, String> labels) {
        ResourceBo resourceBo = new ResourceBo();
        resourceBo.setIp(ip);
        resourceBo.setCpu(cpu);
        resourceBo.setMem(mem);
        resourceBo.setPorts(ports);
        resourceBo.setLables(labels);
        resourceService.updateResource(resourceBo);
    }

    @Override
    public List<MachineBo> getMachineList(long envId) {
        List<DeployMachine> list = projectEnvService.getCurrentMachineList(envId).getData();
        if (null != list) {
            return list.stream().map(it -> {
                MachineBo machine = new MachineBo();
                machine.setIp(it.getIp());
                return machine;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }


    @Override
    public List<MachineBo> getMachineListFromCache(final long envId) {
        List<MachineBo> res = cache.get("machine_list_" + envId, () -> getMachineList(envId));
        if (null == res) {
            return Lists.newArrayList();
        }
        return res;
    }

    @Override
    public void sendFeiShuMsg(String msg) {
        this.feiShuService.sendMsg("", msg);
    }
}
