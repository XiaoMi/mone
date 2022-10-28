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
//import com.google.gson.Gson;
//import com.xiaomi.data.push.redis.Redis;
//import com.xiaomi.youpin.docker.Safe;
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.bo.openApi.ProjectEnvBo;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Keys;
//import com.xiaomi.youpin.gwdash.common.LabelUtils;
//import com.xiaomi.youpin.gwdash.dao.mapper.ApiInfoMapper;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.hermes.bo.response.Account;
//import com.xiaomi.youpin.hermes.service.AccountService;
//import com.xiaomi.youpin.infra.rpc.Result;
//import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;
//import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
//import com.xiaomi.youpin.mischedule.api.service.bo.HealthResult;
//import com.xiaomi.youpin.mischedule.api.service.bo.ServiceInfo;
//import com.youpin.xiaomi.tesla.bo.Flag;
//import com.youpin.xiaomi.tesla.bo.FlagCal;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.compress.utils.Lists;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.dubbo.config.annotation.Reference;
//import org.apache.dubbo.config.annotation.Service;
//import org.apache.logging.log4j.util.PropertiesUtil;
//import org.nutz.dao.Chain;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.nutz.dao.Sqls;
//import org.nutz.dao.pager.Pager;
//import org.nutz.dao.sql.Sql;
//import org.nutz.dao.util.cri.SqlExpressionGroup;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.util.CollectionUtils;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
///**
// * @author zhenghao
// * @description: gw对外提供dubbo接口
// */
////@org.springframework.stereotype.Service
//@Slf4j
//@Service(interfaceClass = GwdashApiService.class, retries = 0, group = "${dubbo.group}")
//public class GwdashApiServiceImpl implements GwdashApiService {
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private ApiInfoMapper apiInfoMapper;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private BillingService billing;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ApiInfoService apiInfoService;
//
//    @Autowired
//    private Redis redis;
//
//    @Value("${tester.top}")
//    private String tester;
//
//    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}")
//    private AccountService accountService;
//
//    @Autowired
//    ApiGroupInfoService apiGroupInfoService;
//
//    @Override
//    public Result<List<String>> getPhysicalIpByEnvId(long envId) {
//        log.info("getPhysicalIpByEnvId envId:{},", envId);
//        List<String> list = new ArrayList<>();
//        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(Long.valueOf(envId)).getData();
//
//        DeployInfo deployInfo = projectPipeline.getDeployInfo();
//        if (deployInfo != null) {
//            deployInfo.getDeployBatches().stream().forEach(it -> {
//                it.getDeployMachineList().forEach(machine -> {
//                    list.add(machine.getIp());
//                });
//            });
//        }
//        return Result.success(list);
//    }
//
//    /**
//     * 每日计算doccker数量，可以重复执行，补数据也可以执行
//     *
//     * @return
//     */
//    @Override
//    public Result<Object> dockerCalculation() {
//        List<ProjectPipeline> projectPipelineList = dao.query(ProjectPipeline.class, Cnd.where("1", "=", "1").andNot("deploy_info", "is not", null).orderBy("id", "desc"));
//
//        log.info("GwdashApiServiceImpl dockerCalculation projectPipelineList size:{}", projectPipelineList.size());
//        Map<Long, Integer> map = new HashMap<>();
//        AtomicReference<Integer> count = new AtomicReference<>(0);
//        projectPipelineList.forEach(it -> {
//            if (map.get(it.getEnvId()) == null) {
//                if (it.getDeployInfo() != null) {
//                    if (it.getDeployInfo().getDockerMachineList() != null) {
//                        map.put(it.getEnvId(), it.getDeployInfo().getDockerMachineList().size());
//                        count.updateAndGet(v -> v + it.getDeployInfo().getDockerMachineList().size());
//                    }
//
//                }
//            }
//        });
//
//        log.info("GwdashApiServiceImpl dockerCalculation projectPipelineList count:{}", count);
//
//        GwStatistics gwStatistics = dao.fetch(GwStatistics.class, Cnd.where("gw_key", "=", Consts.DOCKER_COUNT_KEY));
//        if (gwStatistics == null) {
//            GwStatistics gwStatisticsInsert = new GwStatistics();
//            gwStatisticsInsert.setCtime(System.currentTimeMillis());
//            gwStatisticsInsert.setKey(Consts.DOCKER_COUNT_KEY);
//            gwStatisticsInsert.setValue(String.valueOf(count));
//            dao.insert(gwStatisticsInsert);
//        } else {
//            dao.update(GwStatistics.class, Chain.make("gw_value", count).add("ctime", System.currentTimeMillis()), Cnd.where("gw_key", "=", Consts.DOCKER_COUNT_KEY));
//        }
//        return Result.success(true);
//    }
//
//    @Override
//    public Result<Boolean> offline(List<Long> ids, boolean offline) {
//        log.info("offline api:{} {}", ids, offline);
//        ids.stream().forEach(id -> {
//            ApiInfo apiInfo = apiInfoMapper.selectByPrimaryKey(id);
//            Optional.ofNullable(apiInfo).ifPresent(it -> {
//                FlagCal flagCal = new FlagCal(it.getFlag());
//                if (offline) {
//                    flagCal.enable(Flag.OFF_LINE);
//                } else {
//                    flagCal.disable(Flag.OFF_LINE);
//                }
//                apiInfo.setFlag(flagCal.getFlag());
//                apiInfoMapper.updateByPrimaryKey(apiInfo);
//            });
//        });
//        return Result.success(true);
//    }
//
//    @Override
//    public Result<List<EnvInfo>> envInfoList(List<Long> envIds) {
//        List<EnvInfo> list = envIds.stream().map(envId -> {
//            ProjectEnvDeploySetting setting = projectEnvService.getDeployment(envId).getData();
//            EnvInfo envInfo = new EnvInfo();
//            envInfo.setEnvId(envId);
//            Map<String, String> labels = LabelUtils.convert(setting.getLabels());
//            envInfo.setLabels(labels);
//            long projectId = projectEnvService.getProjectEnvById(envId).getData().getProjectId();
//            envInfo.setProjectId(projectId);
//            return envInfo;
//        }).collect(Collectors.toList());
//        return Result.success(list);
//    }
//
//    @Override
//    public Result<List<String>> getAppNameByUsername(Integer accountId) {
//        com.xiaomi.youpin.gwdash.common.Result<List<ProjectRole>> projectRoleResult = projectService.getProjectByAccountId(accountId);
//        List<Integer> projectIds = projectRoleResult.getData().stream().map(projectRole -> (int)(projectRole.getProjectId())).collect(Collectors.toList());
//        List<String> appNames = projectService.getProjectByIdS(projectIds).stream().map(Project::getName).collect(Collectors.toList());
//
//        return Result.success(appNames);
//    }
//    @Override
//    public Result<List<ProjectEnvBo>> getAppListByUsername(Integer accountId) {
//        com.xiaomi.youpin.gwdash.common.Result<List<ProjectRole>> projectRoleResult = projectService.getProjectByAccountId(accountId);
//        if(!CollectionUtils.isEmpty(projectRoleResult.getData())){
//            List<Integer> projectIds = projectRoleResult.getData().stream().map(projectRole -> (int)(projectRole.getProjectId())).collect(Collectors.toList());
//            List<Project> projectList = projectService.getProjectByIdS(projectIds);
//            if(!CollectionUtils.isEmpty(projectList)){
//                List<ProjectEnvBo> boList = projectList.stream().map(project -> {
//                            ProjectEnvBo bo = new ProjectEnvBo();
//                            BeanUtils.copyProperties(project, bo);
//                            return bo;
//                        }
//                ).collect(Collectors.toList());
//                return Result.success(boList);
//            }
//        }
//        return Result.success(null);
//    }
//
//    @Override
//    public Result<List<Integer>> getUsersByAppName(String appName) {
//        Project project;
//        project = projectService.getProjectByName(appName);
//        if (project == null){
//            if (appName.contains("_")){
//                appName = appName.replaceAll("_","-");
//                project = projectService.getProjectByName(appName);
//                if (project == null){
//                    return Result.fail(GeneralCodes.ParamError,"无该应用");
//                }
//            }else {
//                return Result.fail(GeneralCodes.ParamError,"无该应用");
//            }
//        }
//        List<Integer> userIds = projectService.getMembersByProjectId(project.getId());
//        return Result.success(userIds);
//    }
//
//
//    @Override
//    public Result<Object> qps() {
//        Safe.run(() -> {
//
//            List<Project> projectList = projectService.getProjects();
//            Map<String, Long> resultMap = new HashMap<>();
//            projectList.forEach(it -> {
//                List<ProjectEnv> projectEnvList = projectEnvService.getProjectEnv(it.getId());
//                projectEnvList.forEach(envIt->{
//                    com.xiaomi.youpin.gwdash.common.Result<Map<String, Object>> result = projectEnvService.getDockerStatus(envIt.getId());
//                    Map<String, Object> map = result.getData();
//                    if (map != null) {
//                        if (map.get("healthResult") != null) {
//                            HealthResult healthResult = (HealthResult)map.get("healthResult");
//                            List<ServiceInfo> serviceInfoList = healthResult.getServiceInfoList();
//                            serviceInfoList.forEach(serviceInfo -> {
//                                long qps = serviceInfo.getQps();
//                                if (qps == -1) {
//                                    qps = 0;
//                                }
//                                if (resultMap.get(it.getName()) == null) {
//                                    resultMap.put(it.getName(), qps);
//                                } else {
//                                    resultMap.put(it.getName(), resultMap.get(it.getName()) + qps);
//                                }
//
//                            });
//                        }
//
//                    }
//
//                });
//            });
//            Map sortMap = Consts.sortByValue(resultMap);
//            Map<Object, Object> tenMap = new HashMap<>();
//            sortMap.forEach((key, value) -> {
//                if (tenMap.size() != 10) {
//                    tenMap.put(key, value);
//                }
//            });
//            log.info("qps:{}", tenMap);
//
//            GwStatistics gwStatistics = dao.fetch(GwStatistics.class, Cnd.where("gw_key", "=", Consts.QPS_COUNT_KEY));
//            if (gwStatistics == null) {
//                GwStatistics gwStatisticsInsert = new GwStatistics();
//                gwStatisticsInsert.setCtime(System.currentTimeMillis());
//                gwStatisticsInsert.setKey(Consts.QPS_COUNT_KEY);
//                gwStatisticsInsert.setValue(tenMap.toString());
//                dao.insert(gwStatisticsInsert);
//            } else {
//                dao.update(GwStatistics.class, Chain.make("gw_value", tenMap.toString()).add("ctime", System.currentTimeMillis()), Cnd.where("gw_key", "=", Consts.QPS_COUNT_KEY));
//            }
//
//
//        });
//
//        return Result.success(true);
//    }
//
//    @Override
//    public Result<GatewayApiInfo> getGatewayApiInfo(String url) {
//        List<ApiInfo> apiInfos = apiInfoService.getApiInfoDetailByUrl(url);
//        if (apiInfos == null || apiInfos.size() == 0){
//            return Result.fail(GeneralCodes.NotFound,"该url不存在");
//        }
//        GatewayApiInfo gatewayApiInfo = new GatewayApiInfo();
//        try {
//            ApiInfo it = apiInfos.get(0);
//            BeanUtils.copyProperties(it,gatewayApiInfo);
//            FlagCal cal = new FlagCal(it.getFlag());
//            gatewayApiInfo.setAllowMock(cal.isAllow(Flag.ALLOW_MOCK));
//            String mockData = redis.get(Keys.mockKey(it.getId()));
//            if (StringUtils.isEmpty(mockData)) {
//                mockData = "";
//            }
//            gatewayApiInfo.setMockData(mockData);
//
//            String mockDataDesc = redis.get(Keys.mockDescKey(it.getId()));
//            if (StringUtils.isBlank(mockDataDesc)) {
//                mockDataDesc = "";
//            }
//
//            gatewayApiInfo.setMockDataDesc(mockDataDesc);
//
//        } catch (BeansException e) {
//            return Result.fail(GeneralCodes.InternalError,"复制api信息出错");
//        }
//        return Result.success(gatewayApiInfo);
//    }
//
//    @Override
//    public Result<Object> billingTopTenTask() {
//        Safe.run(() -> {
//            billing.billingTopTenTask();
//        });
//        return Result.success(true);
//    }
//
//    public Result<Map<String, Object>> getAppsByUserName(String username,String appName,Boolean isShowAll,Integer page,Integer pageSize){
//
//        Map<String, Object> result = new LinkedHashMap<>();
//
//        Cnd cnd = Cnd.where("status", "!=", ProjectStatusEnum.DELETE.getId());
//        if (StringUtils.isNotBlank(appName)) {
//            SqlExpressionGroup searchSql = Cnd.exps("name", "like", "%" + appName + "%");
//            cnd = cnd.and(searchSql);
//        }
//
//        // 显示所有
//        if (isShowAll) {
//            result.put("total", dao.count(Project.class, cnd));
//            result.put("list", dao.query(Project.class, cnd, new Pager(page, pageSize)));
//
//        }else{
//            try {
//                Account account = userService.queryUserByName(username);
//                if(account == null){
//                    log.info("getAppsByUserName no Account found username:{}",username);
//                    result.put("total", 0);
//                    result.put("list", Lists.newArrayList());
//                    return Result.success(result);
//                }
//
//                result = new LinkedHashMap<>();
//
//                // 显示自己拥有的
//                String sqlStr = "SELECT distinct(pr.projectId) FROM project_role as pr left outer join project as p on p.id = pr.projectId " +
//                        "WHERE p.status != @status " +
//                        "and ((accountId=@accountId and roleType in (" + RoleType.Owner.ordinal() + ", " + RoleType.Member.ordinal() + ")) " +
//                        "or (accountId in (" + account.getGid().replace("_", ",") + ") and roleType=" + RoleType.Member_Group.ordinal() + "))";
//                Sql sql = Sqls.create(sqlStr);
//                sql.params().set("status", ProjectStatusEnum.DELETE.getId());
//                sql.params().set("accountId", account.getId());
//                sql.setCallback((Connection conn, ResultSet rs, Sql sql1) -> {
//                    List<String> list = new ArrayList<>();
//                    while (rs.next()) {
//                        list.add(rs.getString("pr.projectId"));
//                    }
//                    return list;
//                });
//                List<String> projectIds = dao.execute(sql).getList(String.class);
//                int size = projectIds.size();
//                result.put("total", size);
//                if (size > 0) {
//                    cnd.and("id", "in", projectIds.stream().reduce((a, b) -> a + "," + b).get());
//                    result.put("list", dao.query(Project.class, cnd, new Pager(0, size)));
//                } else {
//                    result.put("list", Lists.newArrayList());
//                }
//            } catch (Exception e) {
//                log.error("getAppsByUserName error,param username : {},error : {}",username,e.getMessage());
//                return Result.fromException(e);
//            }
//        }
//
//        return Result.success(result);
//
//    }
//
//    public Result<Object> getProjectId(Long projectId) {
//        com.xiaomi.youpin.gwdash.common.Result<Project> projectResult = projectService.getProjectById(projectId);
//
//
//        // 测试审核
//        List<Account> testers = accountService.queryUserByGroupName("测试审核组");
//        LinkedList list = new LinkedList();
//        List<ProjectRole> projectTesters = projectService.getTesters(projectId);
//
//        if (CollectionUtils.isEmpty(projectTesters)) {
//            /**
//             * 把tester置顶
//             */
//            for (Account account:testers) {
//                if (account.getUserName().contains(tester)) {
//                    list.addFirst(account);
//                } else {
//                    list.add(account);
//                }
//
//            }
//
//        }
//        if (list.size() == 0) {
//
//            List<Integer> accountIds;
//            if (CollectionUtils.isEmpty(testers)) {
//                accountIds=projectTesters.stream().map(e->e.getAccountId()).collect(Collectors.toList());
//            }else{
//                Set<Long> testIds = testers.stream().map(e -> e.getId()).collect(Collectors.toSet());
//                accountIds=projectTesters.stream().filter(e->!testIds.contains(Long.valueOf(e.getAccountId()))).map(e->e.getAccountId()).collect(Collectors.toList());
//            }
//            for (Integer accountId : accountIds) {
//                Account account = accountService.queryUserById(accountId);
//                if (account == null) {
//                    log.error("account id error: {}", accountId);
//                    continue;
//                }
//                testers.add(account);
//
//            }
//
//            /**
//             * 把tester置顶
//             */
//            for (Account account:testers) {
//                if (account.getUserName().contains(tester)) {
//                    list.addFirst(account);
//                } else {
//                    list.add(account);
//                }
//
//            }
//
//        }
//
//
//        // 项目成员
//        List<ProjectRole> data = dao.query(ProjectRole.class, Cnd.where("projectId", "=", projectId));
//
//        // 环境分组
//        List<List<ProjectPipeline>> objectList = new LinkedList<>();
//        com.xiaomi.youpin.gwdash.common.Result<List<ProjectEnv>> projectEnvServiceList = projectEnvService.getList(projectId);
//
//        // 上一个时间戳
//        LocalDateTime localDateTime = LocalDateTime.now().plusMonths(-5);
//        Long time = localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
//
//        for (ProjectEnv projectEnv : projectEnvServiceList.getData()) {
//            Cnd cnd = (Cnd) Cnd.where("env_id", "=", projectEnv.getId()).and("ctime" ,">=", time);
//            List<ProjectPipeline> projectPipelineList = dao.query(ProjectPipeline.class, cnd);
//            objectList.add(projectPipelineList);
//        }
//
//        Map map = new HashMap();
//
//        Gson gson = new Gson();
//
//        // 项目相关
//        Project data1 = projectResult.getData();
//        map.put("project", gson.toJson(data1));
//        // 测试审核
//        map.put("tester", gson.toJson(list));
//        // 项目组员
//        map.put("member", gson.toJson(data));
//        // 环境分组
//        map.put("group", gson.toJson(projectEnvServiceList.getData()));
//        // 构建历史
//        map.put("history", gson.toJson(objectList));
//
//        return Result.success(map);
//    }
//
//    @Override
//    public Result<List<ProjectEnvBo>> getProjectEnvListByProjectId(Long projectId) {
//        com.xiaomi.youpin.gwdash.common.Result<List<ProjectEnv>> result = projectEnvService.getList(projectId);
//        if(!CollectionUtils.isEmpty(result.getData())){
//            List<ProjectEnvBo> list = result.getData().stream().map(env -> {
//                ProjectEnvBo projectEnvBo = new ProjectEnvBo();
//                BeanUtils.copyProperties(env, projectEnvBo);
//                return projectEnvBo;
//            }).collect(Collectors.toList());
//            return Result.success(list);
//        }
//        return Result.success(null);
//    }
//
//    @Override
//    public Result<Map<String,Object>> getApiGroupsByUserName(String userName){
//        Map<String,Object> apiGroupByUserName = apiGroupInfoService.getApiGroupByUserName(userName);
//        return Result.success(apiGroupByUserName);
//    }
//
//}
