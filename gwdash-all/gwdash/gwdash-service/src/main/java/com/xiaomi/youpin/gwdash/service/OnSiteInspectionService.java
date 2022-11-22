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

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.Pair;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.AppDeployStatus;
import com.xiaomi.youpin.gwdash.common.DeployTypeEnum;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.tesla.agent.po.DockerInfo;
import com.xiaomi.youpin.vulcanus.api.bo.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author zhangjunyi
 * created on 2020/5/20 11:18 上午
 * @modify zhangzhiyong
 */
@Service
@Slf4j
public class OnSiteInspectionService {
    @Autowired
    Dao dao;
    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectEnvService projectEnvService;

    @Autowired
    UserService userService;

    @Autowired
    NacosNaming namingService;

    @Autowired
    private PipelineService pipelineService;


    @Autowired
    private Redis redis;

    private static final String REDIS_DOCKER_INFO_MAPS = "docker_info_maps";


    private static final String AGENT_SERVICE_NAME = "tesla_agent";
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() throws InterruptedException {
        EnvUsage2Redis();
        EnvUsage2Mysql();
    }

    public List<UsageRecord> getDailyRecord(long envId) {
        return dao.query(UsageRecord.class, Cnd.where("envId", "=", envId));
    }

    public Result<Object> getDeployInfosByTime(Long startTime, Long endTime) {
        List<ProjectPipeline> list = dao.query(ProjectPipeline.class, Cnd.where("utime", "between", new Object[]{startTime, endTime}).desc("utime"));
        /**
         * 根据deployInfo status 区分失败 成功
         */
        OnSiteInspection onSiteInspection = new OnSiteInspection();
        onSiteInspection.setSuccess(new ArrayList<>());
        onSiteInspection.setFail(new ArrayList<>());
        list.stream().forEach(it -> {
            if (it.getDeployInfo() == null) {
                onSiteInspection.getFail().add(this.ProjectPipeline2Vo(it));
            } else {
                if (it.getDeployInfo().getStatus() == TaskStatus.success.ordinal()) {
                    onSiteInspection.getSuccess().add(this.ProjectPipeline2Vo(it));
                } else if (it.getDeployInfo().getStatus() == TaskStatus.failure.ordinal()) {
                    onSiteInspection.getFail().add(this.ProjectPipeline2Vo(it));
                }
            }
        });

        return Result.success(onSiteInspection);
    }

    private OnSiteInspectionItem ProjectPipeline2Vo(ProjectPipeline projectPipeline) {
        OnSiteInspectionItem item = new OnSiteInspectionItem();
        item.setDeployTime(projectPipeline.getUtime());
        item.setPipelineId(projectPipeline.getId());
        item.setErrorMessage(projectPipeline.getErrorMessage());
        Account account = userService.queryUserByName(projectPipeline.getUsername());
        if (account != null) {
            item.setDeployUser(account.getName());
        } else {
            item.setDeployUser(projectPipeline.getUsername());
        }

        Result<Project> projectResult = projectService.getProjectById(projectPipeline.getProjectId());
        Project p = projectResult.getData();
        if (null != p) {
            //去掉展示无关的字段
            p.setProjectGen(null);
            item.setProject(p);
        }
        Result<ProjectEnv> projectEnvResult = projectEnvService.getProjectEnvById(projectPipeline.getEnvId());
        item.setProjectEnv(projectEnvResult.getData());
        /**
         * deployInfo为空标记为失败
         */
        if (projectPipeline.getDeployInfo() == null) {
            item.setDeploySucceed(false);
        } else {
            item.setDeploySucceed(projectPipeline.getDeployInfo().getStatus() == TaskStatus.success.ordinal());
        }
        return item;

    }

    public Map<String, Object> getEnvList(int page, int pageSize) {

        if (page < 1) {
            page = 1;
        }
        if (pageSize <= 0) {
            pageSize = 1;
        }

        Pair<Integer, List<ProjectEnvBo>> pair = projectEnvService.getAllDockerEnv();
        String cache = redis.get(REDIS_DOCKER_INFO_MAPS);
//        log.info("getEnvList cache:{}", cache);
        List<Map<String, Object>> maps = new Gson().fromJson(cache, new TypeToken<List<Map<String, Object>>>() {
        }.getType());

        Integer total = pair.getKey();
        List<ProjectEnvBo> projectEnvBos = pair.getValue();
        projectEnvBos.forEach(projectEnvBo -> {
            int envId = projectEnvBo.getEnvId();
            maps.forEach(item -> {
                if (envId == ((int) ((Double) item.get("envId")).doubleValue())) {
                    projectEnvBo.setCpuNum((Double) item.get("cpu"));
                    projectEnvBo.setDockerCount((Double) item.get("dockerCount"));
                    if (item.get("dockerInfo") != null) {
                        ArrayList status = (ArrayList) ((Map) item.get("dockerInfo")).get("status");
                        Double cpuUsage = 0D;
                        Double memoryUsage = 0D;
                        try {
                            cpuUsage = (Double) NumberFormat.getPercentInstance().parse((String) status.get(0));
                        } catch (Exception e) {
//                            log.warn("getEnvList parse error",status);
                        }
                        try {
                            memoryUsage = (Double) NumberFormat.getPercentInstance().parse((String) status.get(1));
                        } catch (Exception e) {
//                            log.warn("getEnvList parse error",status);
                        }

                        projectEnvBo.setCpuUsage(cpuUsage);
                        projectEnvBo.setMemoryUsage(memoryUsage);
                    }
                }
            });
        });

        projectEnvBos.sort(new Comparator<ProjectEnvBo>() {
            @Override
            public int compare(ProjectEnvBo o1, ProjectEnvBo o2) {
                return (int) (o2.getCpuUsage() * 10000 - o1.getCpuUsage() * 10000);
            }
        });
        List<ProjectEnvBo> projectEnvBos1 = projectEnvBos.subList(pageSize * (page - 1), Math.min(pageSize * page, total));
        projectEnvBos.stream().forEach(it -> {
            List<Integer> accountIds = projectService.getOwnerByProjectId(it.getProjectId());
            List<String> owners = accountIds.stream().map(accountId -> userService.queryUserById(accountId).getName()).collect(Collectors.toList());
            it.setOwner(owners);
        });
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", projectEnvBos1);
        return map;
    }


    public List<Map<String, Object>> getEnvUsage(List<Long> envIds) throws InterruptedException {

        //多线程请求
        List<Callable<Map<String, Object>>> tasks = envIds
                .stream()
                .map(it -> (Callable<Map<String, Object>>) () -> {
                    return getEnvUsage(it);
                })
                .collect(Collectors.toList());

        List<Future<Map<String, Object>>> resFutrue = pool.invokeAll(tasks);

        return resFutrue.stream().map(mapFuture -> {
            try {
                return mapFuture.get();
            } catch (Exception e) {
                log.error("getEnvUsage error:{}", e.toString());
            }
            return null;
        }).collect(Collectors.toList());
    }

    /**
     * 获取docker机器的cpu memory信息
     * 耗时很长 所以定时获取然后存到redis，动态取
     */
    public void EnvUsage2Redis() throws InterruptedException {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            //只看docker机器的
            List<Long> envIds = this.getDockerEnvIds();
            List<Map<String, Object>> maps = null;
            try {
                maps = this.getEnvUsage(envIds);
                log.info("EnvUsage2Redis: maps.size: {}", maps.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            redis.set(REDIS_DOCKER_INFO_MAPS, new Gson().toJson(maps));
        }, 0, 1, TimeUnit.MINUTES);

    }

    /**
     * 定时清理useageRecord
     */
    public int deleteFromUsageRecord() {
        long oneDay = 24 * 3600 * 1000L;


        long twoDayAgo = System.currentTimeMillis() - 1 * oneDay;
        int len = dao.clear(UsageRecord.class, Cnd.where("ctime", "<", twoDayAgo));
        log.info("deleteFromUsageRecord:time:{},len:{}", twoDayAgo, len);
        return len;
    }

    /**
     * 定时把redis cpu等信息同步到数据库中
     * 每天删除
     *
     * @throws InterruptedException
     */
    public void EnvUsage2Mysql() throws InterruptedException {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            //直接从redis里面拿 不需要重新取新的
            String cache = redis.get(REDIS_DOCKER_INFO_MAPS);
//            log.info("getEnvList cache:{}", cache);
            List<Map<String, Object>> maps = new Gson().fromJson(cache, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            long current = System.currentTimeMillis();
            try {
                List<UsageRecord> usageRecords = maps.stream().map(it -> getUsageFromEnvUsage(it)).collect(Collectors.toList());
                usageRecords.forEach(usageRecord -> {
                    usageRecord.setCtime(current);
                    usageRecord.setUtime(current);
//                    log.info("insert usageRecord:{}",usageRecord);
                    dao.insert(usageRecord);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 2, TimeUnit.MINUTES);

    }

    public List<Long> getDockerEnvIds() {
        List<Long> envIds = new ArrayList();
        //get all projects
        List<Project> projects = dao.query(Project.class, null);
        //get all project env
        List<ProjectEnv> list = new ArrayList<>();
        projects.stream().forEach(it -> {
            list.addAll(projectEnvService.getList(it.getId()).getData());
        });

        for (ProjectEnv env : list) {
            ProjectPipeline projectPipeline = pipelineService.getProjectPipelineById(env.getPipelineId()).getData();
            if (null != projectPipeline && projectPipeline.getDeployInfo() != null) {
                DeployInfo deployInfo = projectPipeline.getDeployInfo();
                if (DeployTypeEnum.isDocker(env.getDeployType())) {
                    //docker env check
                    if (CollectionUtils.isNotEmpty(deployInfo.getDockerMachineList())) {
                        envIds.add(env.getId());
                    }
                }
            }
        }
        return envIds;
    }

    private UsageRecord getUsageFromEnvUsage(Map<String, Object> envUsage) {
        UsageRecord usageRecord = new UsageRecord();
        long envId = (long) (((Double) envUsage.get("envId")).doubleValue());
        int cpuCount = (int) ((Double) envUsage.get("cpu")).doubleValue();
        usageRecord.setEnvId(envId);
        usageRecord.setCpuCount(cpuCount);
        if (envUsage.get("dockerInfo") != null) {
            ArrayList status = (ArrayList) ((Map) envUsage.get("dockerInfo")).get("status");
            Double cpuUsage = 0D;
            Double memoryUsage = 0D;
            try {
                cpuUsage = (Double) NumberFormat.getPercentInstance().parse((String) status.get(0));
            } catch (Exception e) {
//                            log.warn("getEnvList parse error",status);
            }

            try {
                memoryUsage = (Double) NumberFormat.getPercentInstance().parse((String) status.get(1));
            } catch (Exception e) {
//                            log.warn("getEnvList parse error",status);
            }

            usageRecord.setCpuUsage(cpuUsage);
            usageRecord.setMemoryUsage(memoryUsage);
        } else {
            usageRecord.setCpuUsage(0D);
            usageRecord.setMemoryUsage(0D);
        }
        return usageRecord;
    }

    private Map<String, Object> getEnvUsage(long envId) {
        /**
         * mock
         */

        Map<String, Object> map = new HashMap<>();
        map.put("envId", envId);
        map.put("dockerInfo", null);
        map.put("cpu", 1);
        //test
//        if (envId == 93) {
//            DockerInfo dockerInfo = new DockerInfo();
//            dockerInfo.setServerVersion("1.1.1");
//            ArrayList<String> a = new ArrayList();
//            a.add("77%");
//            a.add("0.00%");
//            dockerInfo.setStatus(a);
//            map.put("dockerInfo", dockerInfo);
//            map.put("cpu",4);
//            return map;
//        }
        Result<Map<String, Object>> result = projectEnvService.getDockerStatus(envId);
        if (result == null || result.getData() == null || result.getData().get("deployInfo") == null) {
            return map;
        }
        DeployInfo deployInfo = (DeployInfo) result.getData().get("deployInfo");
        int cpu = (int) result.getData().get("cpu");
        map.put("cpu", cpu);

        List<DeployMachine> dockerMachineList = deployInfo.getDockerMachineList();
        if (dockerMachineList == null) {
            return map;
        }
        int size = dockerMachineList.size();
        if (size == 0) {
            return map;
        }
        map.put("dockerCount", dockerMachineList.size());
        int random = (int) (Math.random() * size);
        int i = 0;
        for (DeployMachine deployMachine : dockerMachineList) {
            if (i == random) {
                Result<DockerInfo> getDockerUsageRate = projectEnvService.getDockerUsageRate(envId, deployMachine.getIp());
                map.put("dockerInfo", getDockerUsageRate.getData());
                break;
            }
            i++;
        }
        return map;
    }

    public Result<AgentInspectionBo> getAgentInfo() {
        AgentInspectionBo agentInspectionBo = new AgentInspectionBo();
        //只查询最近一分半更新过的机器
        // nacos返回的agent 列表
        agentInspectionBo.setAgent(getTeslaAgentList());
        log.info("getTeslaAgentSize:{}", agentInspectionBo.getAgent().size());
        Long isAliveTime = System.currentTimeMillis() - 1000 * 90;
        //, Cnd.where("utime", ">", isAliveTime)
        List<Machine> machines = dao.query(Machine.class, null);
        log.info("getAgentInfo machine.size:{}", machines.size());
        List<Machine> aliveMachines = new ArrayList<>();
        List<Machine> deadMachines = new ArrayList<>();
        List<Machine> dockerMachines = new ArrayList<>();
        List<Machine> physicalMachines = new ArrayList<>();
        machines.stream().forEach(machine -> {
            if (machine.getUtime() < isAliveTime) {
                deadMachines.add(machine);
            } else {
                aliveMachines.add(machine);
            }
            if (Objects.equals(machine.getLabels().get("type"), "docker")) {
                dockerMachines.add(machine);
            } else {
                physicalMachines.add(machine);
            }

        });
        agentInspectionBo.setAliveMachine(aliveMachines);
        agentInspectionBo.setDeadMachine(deadMachines);
        agentInspectionBo.setDockerMachine(dockerMachines);
        agentInspectionBo.setPhysicalMachine(physicalMachines);
        return Result.success(agentInspectionBo);
    }

    /**
     * 获取可用的tesla agent数量
     *
     * @return
     */
    private List<Instance> getTeslaAgentList() {

        try {
            return namingService.getAllInstances(AGENT_SERVICE_NAME);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
        return new ArrayList<>();
    }

}