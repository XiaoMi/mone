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

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.Pair;
import com.xiaomi.data.push.micloud.MiCloud;
import com.xiaomi.data.push.micloud.bo.response.ControlResponse;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.schedule.task.TaskDefBean;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.gitlab.bo.GitlabCommit;
import com.xiaomi.youpin.gwdash.agent.AgentManager;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.bo.openApi.ReplicateBo;
import com.xiaomi.youpin.gwdash.common.*;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.mischedule.STaskDef;
import com.xiaomi.youpin.mischedule.api.service.bo.PowerOnResult;
import com.xiaomi.youpin.mischedule.api.service.bo.Task;
import com.xiaomi.youpin.quota.bo.QuotaInfo;
import com.xiaomi.youpin.quota.bo.ResourceBo;
import com.xiaomi.youpin.quota.service.QuotaService;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.DockerInfo;
import com.xiaomi.youpin.tesla.agent.po.ManagerReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.trans.Trans;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author tsingfu
 */
@Service
@Slf4j
public class ProjectEnvService {

    @Autowired
    private Dao dao;

    @Autowired
    private AgentManager agentManager;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private MachineManagementServiceImp machineManagementService;

    @Autowired
    private ProjectDeploymentService projectDeploymentService;

    @Autowired
    private MyScheduleService myScheduleService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private Redis redis;

    @Autowired
    private MiCloud miCloud;

    @NacosValue("${access.key}")
    private String accessKey;

    @NacosValue("${secret.key}")
    private String secretKey;


    @Reference(check = false, interfaceClass = QuotaService.class, retries = 0, group = "${ref.quota.service.group}")
    private QuotaService quotaService;


    @Autowired
    private GwCache cache;

    @Autowired
    private BroadcastService broadcastService;


    @Autowired
    private ApiServerBillingService billingService;

    @Value("${project.env.heap.ratio}")
    private int heapRatio;

    private static final String REDIS_DOCKER_INFO_MAPS = "docker_info_maps";

    public Object getDockInfoFromRedis(Long envId) {
        String cache = redis.get(REDIS_DOCKER_INFO_MAPS);
        List<Map<String, Object>> maps = new Gson().fromJson(cache, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        return maps.stream().filter(it -> it.get("envId").equals(envId.doubleValue())).findAny();
    }

    public Pair<Integer, List<ProjectEnvBo>> getAllDockerEnv() {
        List<ProjectEnvBo> projectEnvBos = new ArrayList<>();
        //get all projects
        List<Project> projects = dao.query(Project.class, null);
        //get all project env
        List<ProjectEnv> list = new ArrayList<>();
        projects.stream().forEach(it -> {
            list.addAll(projectEnvService.getList(it.getId()).getData());
        });
        list.parallelStream().forEach(env -> {
            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(env.getPipelineId()).getData();
            if (null != projectPipeline && projectPipeline.getDeployInfo() != null) {
                DeployInfo deployInfo = projectPipeline.getDeployInfo();
                if (DeployTypeEnum.isDocker(env.getDeployType())) {
                    //docker env check
                    if (CollectionUtils.isNotEmpty(deployInfo.getDockerMachineList())) {
                        ProjectEnvBo projectEnvBo = new ProjectEnvBo();
                        Project p = projects.stream().filter(it -> it.getId() == env.getProjectId()).findAny().orElse(null);
                        if (p != null) {
                            projectEnvBo.setProjectName(p.getName());
                        }
                        projectEnvBo.setProjectId((int) env.getProjectId());
                        projectEnvBo.setEnvId((int) env.getId());
                        projectEnvBo.setEnvName(env.getName());
                        projectEnvBos.add(projectEnvBo);
                    }
                }
            }
        });

        Pair<Integer, List<ProjectEnvBo>> pair = new Pair(projectEnvBos.size(), projectEnvBos);
        return pair;
    }

    public int getCount(String sqlQuery) {
        Sql sql = Sqls.fetchInt(sqlQuery);
        sql.params().set("env_status", ProjectEnvStatusEnum.DELETE.getId());
        return dao.execute(sql).getInt();
    }

    public Result<List<ProjectEnv>> getList(long projectId) {
        return Result.success(dao.query(ProjectEnv.class,
            Cnd.where("project_id", "=", projectId)
                .and("status", "!=", ProjectEnvStatusEnum.DELETE.getId())));
    }

    public Result<ProjectEnv> getProjectEnvById(long envId) {
        return Result.success(dao.fetch(ProjectEnv.class,
            Cnd.where("id", "=", envId)
                .and("status", "!=", ProjectEnvStatusEnum.DELETE.getId())));
    }

    public Result<Boolean> add(ProjectEnv projectEnvBo) {
        long now = System.currentTimeMillis();
        // projectEnvBo.setProfile(projectEnvBo.getGroup());
        projectEnvBo.setDeployType(projectEnvBo.getDeployType());
        projectEnvBo.setBranch(projectEnvBo.getBranch());
        projectEnvBo.setCtime(now);
        projectEnvBo.setUtime(now);
        long projectId = projectEnvBo.getProjectId();
        Project project = dao.fetch(Project.class, Cnd.where("id", "=", projectId));
        if (null == project) {
            return new Result<>(0, "未找到项目", false);
        }
        String gitName = project.getGitName();
        Trans.exec(() -> {
            // todo: 默认初始化配置参数 & 策略
            dao.insert(projectEnvBo);
            long envId = projectEnvBo.getId();
            // 初始化构建设置
            ProjectEnvBuildSetting buildSetting = new ProjectEnvBuildSetting();
            buildSetting.setEnvId(envId);
            buildSetting.setBuildDir("");
            buildSetting.setXmlSetting(1);
            buildSetting.setJarDir(gitName + "-server");
            dao.insertOrUpdate(buildSetting);
            // 初始化部署策略
            ProjectEnvPolicy policy = new ProjectEnvPolicy();
            policy.setBatchNum(2);
            policy.setEnvId(envId);
            policy.setDeployment("oneByone");
            policy.setStop("each");
            policy.setCtime(now);
            policy.setUtime(now);
            dao.insertOrUpdate(policy);
            // 初始化部署设置
            ProjectEnvDeploySetting deploySetting = new ProjectEnvDeploySetting();
            deploySetting.setEnvId(envId);
            deploySetting.setPath("xxxx/" + gitName + "/");
            deploySetting.setHeapSize(1024 * heapRatio);
            deploySetting.setCpu("4");
            deploySetting.setJvmParams("");
            deploySetting.setLogPath(Consts.DOCKER_LOG_PATH_PREFIX + gitName + "/");
            deploySetting.setMemory(1024 * heapRatio);
            deploySetting.setReplicate(1);
            deploySetting.setMaxReplicate(1);
            deploySetting.setVolume("");
            deploySetting.setHealthCheckUrl("");
            deploySetting.setLabels("");
            dao.insertOrUpdate(deploySetting);
        });
        return Result.success(true);
    }

    public Result<Boolean> edit(ProjectEnv projectEnvBo) {
        ProjectEnv projectEnv = dao.fetch(projectEnvBo);
        if (null == projectEnv) {
            return new Result<>(1, "环境不存在", false);
        }
        projectEnv.setName(projectEnvBo.getName());
        // projectEnv.setDeployType(projectEnvBo.getDeployType());
        projectEnv.setAuthority(projectEnvBo.getAuthority());
        projectEnv.setBranch(projectEnvBo.getBranch());
        projectEnv.setProfile(projectEnvBo.getProfile());
        projectEnv.setUtime(System.currentTimeMillis());
        dao.update(projectEnv);
        return Result.success(true);
    }

    public Result<ProjectEnv> getProjectEnvIgnoreStatusById(long id) {
        return Result.success(dao.fetch(ProjectEnv.class, id));
    }

    public Result<Boolean> delete(ProjectEnv projectEnv) {
        //env check
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(projectEnv.getId()).getData();
        if (null != projectPipeline) {
            DeployInfo deployInfo = projectPipeline.getDeployInfo();
            if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
                //docker env check
                if (deployInfo != null && (CollectionUtils.isNotEmpty(deployInfo.getDockerMachineList()))) {
                    return new Result<>(1, "必须先nuke掉已部署机器", false);
                }
            } else {
                //physicalMachine env check
                if (deployInfo != null && CollectionUtils.isNotEmpty(deployInfo.getDeployBatches())) {
                    for (DeployBatch e : deployInfo.getDeployBatches()) {
                        if (CollectionUtils.isNotEmpty(e.getDeployMachineList())) {
                            long count = e.getDeployMachineList().stream().filter(machine -> machine.getAppDeployStatus() != AppDeployStatus.NUKE.getId()).count();
                            if (count > 0) {
                                return new Result<>(1, "必须先nuke掉已部署机器", false);
                            }
                        }
                    }
                }
            }
        }

        // 处理健康监测相关
        int taskId = projectEnv.getHealthCheckTaskId();
        myScheduleService.pause(taskId);
        projectEnv.setStatus(ProjectEnvStatusEnum.DELETE.getId());
        dao.update(projectEnv);
        return Result.success(true);
    }

    public Result<Boolean> savePolicy(ProjectEnvPolicy projectEnvPolicyBo) {
        dao.insertOrUpdate(projectEnvPolicyBo);
        return Result.success(true);
    }

    public Result<ProjectEnvPolicy> getPolicy(long envId) {
        return Result.success(dao.fetch(ProjectEnvPolicy.class, Cnd.where("env_id", "=", envId)));
    }

    public Result<List<EnvMachineBo>> getMachines(long envId) {
        return machineManagementService.listMachinesOfEnv(envId);
    }

    public Result<Boolean> setMachines(List<EnvMachineBo> list) {
        list.stream().forEach(it -> {
            dao.update(ProjectEnvMachine.class,
                Chain.make("used", it.isUsed()),
                Cnd.where("id", "=", it.getId()));
        });
        return Result.success(true);
    }

    public Result<Boolean> saveDeployment(ProjectEnvDeploySetting projectEnvDeploySetting) {
        dao.insertOrUpdate(projectEnvDeploySetting);
        return Result.success(true);
    }

    public Result<ProjectEnvDeploySetting> getDeployment(long envId) {
        return Result.success(dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId)));
    }

    public Result<Map<String, Object>> getEnvDeployList(long envId, int page, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        Cnd cnd = Cnd.where("env_id", "=", envId);
        map.put("total", dao.count(ProjectPipeline.class, cnd));
        cnd.desc("id");
        List<ProjectPipeline> list = dao.query(ProjectPipeline.class, cnd, new Pager(page, pageSize));
        list.stream().forEach(it -> {
            dao.fetchLinks(it, null);
        });
        map.put("list", list);
        return Result.success(map);
    }

    public Result<List<EnvMachineBo>> getExtensionMachines(long envId) {
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
        if (null == projectPipeline) {
            log.warn("ProjectEnvService#getExtensionMachines projectPipeline is null, envId = {}", envId);
            return Result.success(new ArrayList<>());
        }
        List<EnvMachineBo> envMachineBos = getMachines(envId).getData();
        if (null == envMachineBos
            || 0 == envMachineBos.size()) {
            log.warn("ProjectEnvService#getExtensionMachines envMachineBos is empty, envId = {}", envId);
            return Result.success(new ArrayList<>());
        }
        envMachineBos = envMachineBos.parallelStream().filter(it -> it.isUsed()).collect(Collectors.toList());
        List<DeployBatch> deployBatches = projectPipeline.getDeployInfo().getDeployBatches();
        for (DeployBatch deployBatch : deployBatches) {
            for (DeployMachine dm : deployBatch.getDeployMachineList()) {
                envMachineBos.removeIf(it -> it.getIp().equals(dm.getIp()));
            }
        }
        return Result.success(envMachineBos);
    }

    public Result<Boolean> extendMachines(ExtensionMachineBo extensionMachineBo) {
        long envId = extensionMachineBo.getEnvId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
        if (null == projectPipeline) {
            log.warn("ProjectEnvService#extendMachines projectPipeline is null, envId = {}", envId);
            return Result.success(false);
        }
        List<MachineBo> machineBoList = extensionMachineBo.getMachineBoList();
        // 构建新的部署批次
        List<DeployMachine> deployMachineList = machineBoList.parallelStream().map(machineBo -> {
            // 保存信息
            DeployMachine dm = new DeployMachine();
            dm.setHostname(machineBo.getHostname());
            dm.setIp(machineBo.getIp());
            dm.setName(machineBo.getName());
            dm.setAppDeployStatus(AppDeployStatus.OFFLINE.getId());
            return dm;
        }).collect(Collectors.toList());
        List<DeployBatch> deployBatches = projectPipeline.getDeployInfo().getDeployBatches();
        int batch = deployBatches.size();
        DeployBatch newDeployBatch = new DeployBatch();
        newDeployBatch.setDeployMachineList(deployMachineList);
        newDeployBatch.setFort(false);
        newDeployBatch.setBatch(batch);
        deployBatches.add(newDeployBatch);
        // 开始该批次发布
        projectDeploymentService.startBatch(projectPipeline, batch);
        dao.update(projectPipeline);
        return Result.success(true);
    }

    public Result<List<Map<String, Object>>> getReleaseStatus(long envId) {
        List<Map<String, Object>> list = new ArrayList<>();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
        if (null == projectPipeline) {
            return new Result<>(0, "pipeline不存在", list);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(0, "部署信息不存在", list);
        }
        deployInfo.getDeployBatches().stream().forEach(it -> {
            it.getDeployMachineList().forEach(machine -> {
                RemotingCommand remotingCommand;
                remotingCommand = projectDeploymentService.physicalMachineInfo(projectPipeline, machine.getIp()).getData();
                Map<String, Object> map = new HashMap<>();
                map.put("ip", machine.getIp());
                map.put("name", machine.getName());
                map.put("hostname", machine.getHostname());
                Optional<String> ipWithPort = agentManager.getClientAddress(machine.getIp());
                if (ipWithPort.isPresent()) {
                    String port = ipWithPort.get().split(":")[1];
                    map.put("agentPort", port);
                }
                if (null != remotingCommand
                    && null != remotingCommand.getBody()) {
                    map.put("info", new String(remotingCommand.getBody()));
                }
                list.add(map);
            });
        });

        return Result.success(list);
    }


    public ReplicateBo getReplicateInfo(long envId) {
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
        if (projectPipeline == null) {
            throw new IllegalArgumentException("没有找到部署信息");
        }

        ProjectEnvDeploySetting deploySetting = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId));

        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        int currentReplicates = deployInfo == null || deployInfo.getDockerMachineList() == null ? -1 : deployInfo.getDockerMachineList().size();
        int minReplicates = deploySetting == null ? -1 : (int) deploySetting.getReplicate();
        int maxReplicates = deploySetting == null ? -1 : (int) deploySetting.getMaxReplicate();

        ReplicateBo replicateBo = new ReplicateBo();
        replicateBo.setCurrentReplicates(currentReplicates);
        replicateBo.setMinReplicates(minReplicates);
        replicateBo.setMaxReplicates(maxReplicates);
        return replicateBo;
    }


    public Result<Map<String, Object>> getDockerStatus(long envId) {
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv) {
            return new Result<>(0, "环境信息不存在", null);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(0, "没有找到部署信息", null);
        }

        DeploySetting deploySetting = projectPipeline.getDeploySetting();
        Map<String, Object> map = new HashMap<>();

        if (deploySetting == null) {
            map.put("cpu", 0);
            map.put("replicate", 0);
        } else {
            map.put("cpu", deploySetting.getDockerCup());
            map.put("replicate", deploySetting.getDockerReplicate());
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();

        map.put("healthResult", projectEnv.getHealthResult());
        if (null != deployInfo && null != deployInfo.getDockerMachineList()) {
            deployInfo.getDockerMachineList().parallelStream().forEach(it -> {

                Machine machine = cache.get("machine_" + it.getIp(), () -> {
                    Machine machine1 = dao.fetch(Machine.class,
                        Cnd.where("ip", "=", it.getIp()));
                    return machine1;
                });

                if (null != machine) {
                    it.setName(machine.getName());
                    it.setHostname(machine.getHostname());
                }
            });
        }
        map.put("deployInfo", deployInfo);
        return Result.success(map);
    }

    public Result<List<DeployMachine>> getCurrentMachineList(long envId) {
        ProjectEnv projectEnv = getProjectEnvById(envId).getData();
        if (null != projectEnv) {
            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
            if (null != projectPipeline) {
                return Result.success(projectPipeline.getDeployInfo().getDockerMachineList());
            }
        }
        return Result.success(null);
    }

    public Result<Boolean> setCurrentOnline(long envId, String ip) {
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv || 0 == projectEnv.getPipelineId()) {
            return new Result<>(1, "部署信息不存在", false);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "部署信息不存在", false);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(1, "部署信息不存在", false);
        }
        AtomicBoolean isIp = new AtomicBoolean(false);
        deployInfo.getDeployBatches().stream().forEach(it -> {
            it.getDeployMachineList().forEach(machine -> {
                if (machine.getIp().equals(ip)) {
                    machine.setAppDeployStatus(AppDeployStatus.ONLINE.getId());
                    isIp.set(true);
                }
            });
        });
        if (!isIp.get()) {
            return new Result<>(2, "操作ip不在部署列表中", false);
        }
        dao.update(projectPipeline);
        return projectDeploymentService.physicalMachineOnline(projectPipeline, ip, -1);
    }

    public Result<Map<String, Object>> setCurrentOffline(long envId, String ip) {
        Map<String, Object> result = new HashMap<>();
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv || 0 == projectEnv.getPipelineId()) {
            return new Result<>(1, "部署信息不存在", null);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "部署信息不存在", null);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(1, "部署信息不存在", null);
        }
        AtomicBoolean isIp = new AtomicBoolean(false);
        deployInfo.getDeployBatches().stream().forEach(it -> {
            it.getDeployMachineList().forEach(machine -> {
                if (machine.getIp().equals(ip)) {
                    machine.setAppDeployStatus(AppDeployStatus.OFFLINE.getId());
                    isIp.set(true);
                }
            });
        });
        if (!isIp.get()) {
            return new Result<>(2, "操作ip不在部署列表中", null);
        }
        dao.update(projectPipeline);
        RemotingCommand remotingCommand = projectDeploymentService.physicalMachineOffline(projectPipeline, ip).getData();
        result.put("ip", ip);
        if (null != remotingCommand
            && null != remotingCommand.getBody()) {
            result.put("info", new String(remotingCommand.getBody()));
        }

        return Result.success(result);
    }

    public Result<Boolean> setCurrentNuke(long envId, String ip) {
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv || 0 == projectEnv.getPipelineId()) {
            return new Result<>(1, "部署信息不存在", null);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "部署信息不存在", null);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(1, "部署信息不存在", null);
        }
        AtomicBoolean isIp = new AtomicBoolean(false);
        deployInfo.getDeployBatches().stream().forEach(it -> {
            it.getDeployMachineList().forEach(machine -> {
                if (machine.getIp().equals(ip)) {
                    machine.setAppDeployStatus(AppDeployStatus.NUKE.getId());
                    isIp.set(true);
                }
            });
        });
        if (!isIp.get()) {
            return new Result<>(2, "操作ip不在部署列表中", null);
        }
        dao.update(projectPipeline);
        return projectDeploymentService.physicalMachineNuke(projectPipeline, ip);
    }


    public Result<Boolean> setPoweroffStatus(long envId, String ip) {
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv || 0 == projectEnv.getPipelineId()) {
            return new Result<>(1, "部署信息不存在", null);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "部署信息不存在", null);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(1, "部署信息不存在", null);
        }
        AtomicBoolean isIp = new AtomicBoolean(false);
        deployInfo.getDockerMachineList().stream().forEach(it -> {
            if (it.getIp().equals(ip)) {
                isIp.set(true);
                it.setAppDeployStatus(AppDeployStatus.POWEROFF.ordinal());
            }

        });
        if (!isIp.get()) {
            return new Result<>(2, "操作ip不在部署列表中", null);
        }
        dao.update(projectPipeline);
        return Result.success(true);
    }


    /**
     * 修改机器状态 (关机到开机)
     *
     * @param envId
     * @param ip
     * @return
     */
    public Result<Boolean> setPoweronStatus(long envId, String ip) {
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv || 0 == projectEnv.getPipelineId()) {
            return new Result<>(1, "部署信息不存在", null);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "部署信息不存在", null);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(1, "部署信息不存在", null);
        }

        AtomicBoolean isIp = new AtomicBoolean(false);
        deployInfo.getDockerMachineList().stream().forEach(it -> {
            if (it.getIp().equals(ip)) {
                isIp.set(true);
                it.setAppDeployStatus(AppDeployStatus.ONLINE.ordinal());
            }
        });
        if (!isIp.get()) {
            return new Result<>(2, "操作ip不在部署列表中", null);
        }
        dao.update(projectPipeline);
        return Result.success(true);
    }


    public Result<Boolean> setDockerNuke(long envId, String ip) {
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv || 0 == projectEnv.getPipelineId()) {
            return new Result<>(1, "部署信息不存在", null);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "部署信息不存在", null);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(1, "部署信息不存在", null);
        }
        return projectDeploymentService.dockerMachineNuke(projectPipeline, projectEnv, ip);
    }

    public Result<Boolean> shutDownOrOnlineDockerMachine(long envId, String ip, boolean shutDown) {
        ProjectEnv projectEnv = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnv || 0 == projectEnv.getPipelineId()) {
            return new Result<>(1, "部署信息不存在", null);
        }
        final long pipelineId = projectEnv.getPipelineId();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(pipelineId).getData();
        if (null == projectPipeline) {
            return new Result<>(1, "部署信息不存在", null);
        }
        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (null == deployInfo) {
            return new Result<>(1, "部署信息不存在", null);
        }
        if (shutDown) {
            return projectDeploymentService.dockerMachineShutdownWithIp(projectPipeline, projectEnv, ip);
        }
        return projectDeploymentService.dockerMachineOnlineWithIp(projectPipeline, projectEnv, ip);
    }

    public Result<Boolean> saveBuildSetting(ProjectEnvBuildSettingBo buildSettingBo) {
        ProjectEnvBuildSetting projectEnvBuildSetting = dao.fetch(ProjectEnvBuildSetting.class, Cnd.where("env_id", "=", buildSettingBo.getEnvId()));
        if (null != projectEnvBuildSetting) {
            projectEnvBuildSetting.setBuildDir(buildSettingBo.getBuildDir());
            projectEnvBuildSetting.setEnvId(buildSettingBo.getEnvId());
            projectEnvBuildSetting.setJarDir(buildSettingBo.getJarDir());
            projectEnvBuildSetting.setCustomParams(buildSettingBo.getCustomParams());
            projectEnvBuildSetting.setXmlSetting(buildSettingBo.getXmlSetting());
            dao.update(projectEnvBuildSetting);
            return Result.success(true);
        }
        projectEnvBuildSetting = new ProjectEnvBuildSetting();
        projectEnvBuildSetting.setBuildDir(buildSettingBo.getBuildDir());
        projectEnvBuildSetting.setEnvId(buildSettingBo.getEnvId());
        projectEnvBuildSetting.setJarDir(buildSettingBo.getJarDir());
        projectEnvBuildSetting.setCustomParams(buildSettingBo.getCustomParams());
        projectEnvBuildSetting.setXmlSetting(buildSettingBo.getXmlSetting());
        dao.insert(projectEnvBuildSetting);
        return Result.success(true);
    }

    public Result<ProjectEnvBuildSettingBo> getBuildSetting(long envId) {
        ProjectEnvBuildSetting projectEnvBuildSetting = dao.fetch(ProjectEnvBuildSetting.class, Cnd.where("env_id", "=", envId));
        if (null == projectEnvBuildSetting) {
            return Result.success(null);
        }
        ProjectEnvBuildSettingBo buildSettingBo = new ProjectEnvBuildSettingBo();
        buildSettingBo.setId(projectEnvBuildSetting.getId());
        buildSettingBo.setBuildDir(projectEnvBuildSetting.getBuildDir());
        buildSettingBo.setEnvId(projectEnvBuildSetting.getEnvId());
        buildSettingBo.setJarDir(projectEnvBuildSetting.getJarDir());
        buildSettingBo.setXmlSetting(projectEnvBuildSetting.getXmlSetting());
        buildSettingBo.setCustomParams(projectEnvBuildSetting.getCustomParams());
        return Result.success(buildSettingBo);
    }

    public Result<List<GitlabCommit>> getAuditCommits(SessionAccount account, long projectId, String branch) throws GitAPIException, IOException {
        Project project = projectService.getProjectById(projectId).getData();
        if (null == project) {
            return new Result<>(2, "环境对用的项目不存在", null);
        }
        List<GitlabCommit> list = projectService.getGitlabCommits(project.getGitGroup(), project.getGitName(), branch, account).getData();
        return Result.success(list);
    }

    public Result<List<GitlabCommit2>> getAllowCommits(SessionAccount account, long envId) throws GitAPIException, IOException {
        ProjectEnv projectEnvBo = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnvBo) {
            return new Result<>(1, "环境不存在", null);
        }

        Project project = projectService.getProjectById(projectEnvBo.getProjectId()).getData();
        if (null == project) {
            return new Result<>(2, "环境对用的项目不存在", null);
        }

        List<GitlabCommit> list = projectService.getGitlabCommits(project.getGitGroup(), project.getGitName(), projectEnvBo.getBranch(), account).getData();

        if (CollectionUtils.isEmpty(list)) {
            return Result.success(new ArrayList<>());
        }

        List<GitlabCommit2> gitlabCommits = new ArrayList<>(list.size());
        for (GitlabCommit e : list) {
            GitlabCommit2 gitlabCommit2 = new GitlabCommit2();
            BeanUtils.copyProperties(e, gitlabCommit2);
            gitlabCommit2.setReview(reviewService.getReviewStatus(project.getId(), e.getId()));
            gitlabCommits.add(gitlabCommit2);
        }


        return Result.success(gitlabCommits.stream().sorted(Comparator.comparing(GitlabCommit::getCommitted_date).reversed()).collect(Collectors.toList()));
    }

    public Result<List<GitlabCommit2>> getHistoryCommits(SessionAccount account, long envId) throws GitAPIException, IOException {
        ProjectEnv projectEnvBo = dao.fetch(ProjectEnv.class, envId);
        if (null == projectEnvBo) {
            return new Result<>(1, "环境不存在", null);
        }

        Project project = projectService.getProjectById(projectEnvBo.getProjectId()).getData();
        if (null == project) {
            return new Result<>(2, "环境对用的项目不存在", null);
        }

        List<HistoryCommit> historyCommits = dao.query(HistoryCommit.class, Cnd.orderBy().desc("id"), new Pager(1, 20));
        if (CollectionUtils.isEmpty(historyCommits)) {
            return Result.success(new ArrayList<>());
        }

        List<GitlabCommit2> gitlabCommits = new ArrayList<>(10);
        Set<String> set = new HashSet<>();
        for (HistoryCommit it : historyCommits) {
            String commitId = it.getDeploySetting().getCommitId();
            if (set.size() <= 10) {
                if (!set.contains(commitId)) {
                    set.add(commitId);
                    GitlabCommit2 gitlabCommit2 = new GitlabCommit2();
                    GitlabCommit gitlabCommit = projectService.getGitlabCommit(project.getGitGroup(), project.getGitName(), commitId, account).getData();
                    if (null != gitlabCommit) {
                        BeanUtils.copyProperties(gitlabCommit, gitlabCommit2);
                        gitlabCommit2.setReview(reviewService.getReviewStatus(project.getId(), gitlabCommit.getId()));
                        gitlabCommits.add(gitlabCommit2);
                    }
                }
            } else {
                break;
            }
        }

        return Result.success(gitlabCommits.stream().sorted(Comparator.comparing(GitlabCommit::getCommitted_date).reversed()).collect(Collectors.toList()));
    }

    public boolean isOnline(String group) {
        return group.equals("intranet") || group.equals("c3") || group.equals("c4") || group.equals("production");
    }

    public Result<Boolean> openHealthCheck(long envId) {
        ProjectEnv projectEnv = getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.success(false);
        }
        int taskId = projectEnv.getHealthCheckTaskId();
        return Result.success(myScheduleService.start(taskId).getData());
    }

    public Result<Boolean> closeHealthCheck(long envId) {
        ProjectEnv projectEnv = getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.success(false);
        }
        int taskId = projectEnv.getHealthCheckTaskId();
        return Result.success(myScheduleService.pause(taskId).getData());
    }


    public Result<String> getHealthCheckStatus(long envId) {
        ProjectEnv projectEnv = getProjectEnvById(envId).getData();
        if (null == projectEnv) {
            return Result.success("");
        }
        int taskId = projectEnv.getHealthCheckTaskId();
        Task task = myScheduleService.getTaskInfo(taskId).getData();
        if (null == task) {
            return Result.success("");
        }
        return Result.success(task.getStatus());
    }

    /**
     * @param envId
     * @param ip       当前的机器
     * @param targetIp 可以指定迁移到那台机器
     * @return
     */
    public Result<Boolean> drift(long envId, String ip, String targetIp) {
        ProjectEnv projectEnv = getProjectEnvById(envId).getData();
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setProjectId(projectEnv.getProjectId());
        quotaInfo.setBizId(envId);
        quotaInfo.setIp(ip);
        quotaInfo.setTargetIp(targetIp);
        ResourceBo resourceBo = quotaService.drift(quotaInfo).getData();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
        if (resourceBo != null && projectPipeline != null && projectEnv != null) {
            projectDeploymentService.stopHealthCheck(projectEnv);
            projectDeploymentService.dockerMachineOffline(projectPipeline, ip);
            MachineBo machine = new MachineBo();
            machine.setCpuCore(resourceBo.getCpuCore());
            projectPipeline.getDeployInfo().offlineDockerMachine(ip);
            DeployMachine deployMachine = new DeployMachine();
            deployMachine.setIp(resourceBo.getIp());
            deployMachine.setHostname(resourceBo.getHostName());
            deployMachine.setName(resourceBo.getName());
            deployMachine.setCpuCore(resourceBo.getCpuCore());
            projectPipeline.getDeployInfo().onlineDockerMachine(deployMachine);
            dao.update(projectPipeline);
            projectDeploymentService.dockerMachineOnline(projectPipeline, machine, resourceBo.getIp(), -1);
            projectDeploymentService.startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
            return Result.success(true);
        }

        return Result.fail(CommonError.DriftError);
    }

    /**
     * 从新初始化健康监测
     *
     * @param envId
     */
    public boolean initHealthCheck(long envId) {
        return projectDeploymentService.initHealthCheck(envId);
    }

    /**
     * 清空健康监测
     */
    public boolean clearHealthCheck(long envId) {
        return projectDeploymentService.clearHealthCheck(envId);
    }

    /**
     * 获取健康监测状态
     */
    public Integer getDockerHealthCheckStatus(long envId) {
        return projectDeploymentService.getDockerHealthCheckStatus(envId);
    }

    public Result<DockerInfo> getDockerUsageRate(long envId, String ip) {
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
        if (null == projectPipeline) {
            return new Result<>(0, "信息不存在", null);
        }
        RemotingCommand remotingCommand = projectDeploymentService.dockerMachineInfo(projectPipeline, ip).getData();
        if (null != remotingCommand) {
            DockerInfo di = new Gson().fromJson(new String(remotingCommand.getBody()), DockerInfo.class);
            return Result.success(di);
        }
        return new Result<>(0, "信息不存在", null);
    }

    /**
     * 关机操作(关闭容器后,调用云平台接口直接关闭机器)
     *
     * @return
     */
    public Result<Boolean> powerOff(String ip, long envId, long projectId) {
        Optional<String> optional = agentManager.getClientAddress(ip);
        if (optional.isPresent()) {
            ManagerReq req = new ManagerReq();
            req.setCmd(ManagerReq.CmdType.powerOff.name());
            RemotingCommand res = agentManager.send(optional.get(), AgentCmd.managerReq, new Gson().toJson(req), 10000);

            log.info("powerOff addr:{} res:{} body:{}", optional.get(), res.getCode(), res.getBody());

            if (null != res.getBody()) {
                log.info("power off res :{}", new String(res.getBody()));
            }

            Result<Boolean> r = Result.success(Retry.run(() -> {
                //调用云平台接口
                String hostname;
                try {
                    hostname = InetAddress.getByName(ip).getHostName();
                } catch (UnknownHostException e) {
                    log.error(e.toString());
                    return false;
                }

                ControlResponse response = miCloud.powerOff(accessKey, secretKey, "poweroff", new String[]{
                    hostname
                });
                log.info("power off micloud res:{}", response);
                return response != null && response.getCode() == 0 && response.getData() != null && response.getData().size() >= 1 && response.getData().get(0).isSuccess();

            }, 3));
            if (r.getData()) {
                //清除掉client
                agentManager.closeClient(optional.get());
                //修改显示状态
                Result<Boolean> spRes = setPoweroffStatus(envId, ip);
                //通知billing停止计费
                BillingReq billingReq = new BillingReq();
                billingReq.setResourceKeyList(Lists.newArrayList(ip));
                billingReq.setType(BillingReq.BillingType.offline.ordinal());
                billingReq.setEnvId(envId);
                billingReq.setProjectId(projectId);
                billingReq.setSubType(BillingReq.SubType.machine.ordinal());
                billingService.offline(billingReq);
                log.info("setPowerOffstatus:{}", spRes.getCode());
            }

            return r;
        }
        return Result.fail(CommonError.UnknownError);
    }

    /**
     * 启动开机任务
     *
     * @param ip
     * @return
     */
    public Result<Boolean> startPowerOnTask(String userName, String ip, long envId, long projectId) {
        log.info("start power on task ip:{}", ip);
        String hostname;
        try {
            hostname = InetAddress.getByName(ip).getHostName();
        } catch (UnknownHostException e) {
            log.error(e.toString());
            return Result.fail(CommonError.UnknownError);
        }

        TaskParam taskParam = new TaskParam();
        taskParam.setNotify("mqNotify");
        taskParam.setBizId("power_on_" + ip);
        Map<String, String> param = new HashMap<>();
        param.put("accessKey", accessKey);
        param.put("secretKey", secretKey);
        param.put("hostname", hostname);
        param.put("ip", ip);
        param.put("userName", userName);
        param.put("envId", String.valueOf(envId));
        param.put("projectId", String.valueOf(projectId));

        taskParam.setParam(param);
        taskParam.setTaskDef(new TaskDefBean(STaskDef.PowerOnTask));
        com.xiaomi.youpin.infra.rpc.Result<Integer> res = myScheduleService.submitTask(taskParam);
        log.info("startPowerOnTask task id:{}", res.getData());

        return Result.success(true);
    }


    /**
     * 开机操作(task任务(调用云平台)后,会调用到这里)
     *
     * @return
     */
    public Result<Boolean> powerOn(PowerOnResult powerOnResult) {
        log.info("powerOn :{}", new Gson().toJson(powerOnResult));
        int step = powerOnResult.getStep();

        sendNotifyMessage(powerOnResult);

        if (step < 3) {
            return Result.success(true);
        }

        final String ip = powerOnResult.getIp();
        new Thread(() -> {
            log.info("power on ip:{}", ip);
            boolean getIp = Retry.run(() -> {
                Optional<String> optional = agentManager.getClientAddress(ip);
                return optional.isPresent();
            }, 6, 5);
            if (getIp) {
                Optional<String> optional = agentManager.getClientAddress(ip);
                log.info("power on get ip :{}", optional.get());
                if (optional.isPresent()) {
                    ManagerReq req = new ManagerReq();
                    req.setCmd(ManagerReq.CmdType.powerOn.name());
                    try {
                        RemotingCommand res = agentManager.send(optional.get(), AgentCmd.managerReq, new Gson().toJson(req), 10000);
                        log.info("power on code:{} body:{}", res.getCode(), res.getBody());
                        if (null != res.getBody()) {
                            log.info("power on res :{}", new String(res.getBody()));
                        }

                        //目前有并发问题,先忽略掉
                        Result<Boolean> psRes = setPoweronStatus(powerOnResult.getEnvId(), ip);

                        //开始计费
                        BillingReq billingReq = new BillingReq();
                        billingReq.setResourceKeyList(Lists.newArrayList(ip));
                        billingReq.setType(BillingReq.BillingType.online.ordinal());
                        billingReq.setEnvId(powerOnResult.getEnvId());

                        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(powerOnResult.getEnvId()).getData();
                        billingReq.setProjectId(projectEnv.getProjectId());
                        billingReq.setSubType(BillingReq.SubType.machine.ordinal());
                        billingService.online(billingReq);

                        log.info("setPoweronStatus code:{}", psRes.getCode());

                    } catch (Throwable ex) {
                        log.error("power on error:" + ex.getMessage(), ex);
                    }
                }
            } else {
                log.info("power on  get ip:{} fail", ip);
            }
        }).start();
        return Result.success(true);
    }

    private void sendNotifyMessage(PowerOnResult powerOnResult) {
        if (StringUtils.isNotEmpty(powerOnResult.getUserName())) {
            BroadCastData broadCastData = new BroadCastData();
            broadCastData.setMessage(powerOnResult.getMessage());
            broadCastData.setOperation("receive");
            broadCastData.setName(powerOnResult.getUserName());
            broadCastData.setUsername("System");
            broadcastService.sendMsg(powerOnResult.getUserName(), new Gson().toJson(broadCastData));
        }
    }

    public Result<String> getLogSnapshot(long envId, String ip) {
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
        if (null == projectPipeline) {
            return new Result<>(0, "部署信息不存在", null);
        }
        RemotingCommand remotingCommand = projectDeploymentService.getLogSnapshot(projectPipeline, ip).getData();
        if (null != remotingCommand && null != remotingCommand.getBody()) {
            return Result.success(new String(remotingCommand.getBody()));
        }
        return new Result<>(0, "信息不存在", null);
    }
}
