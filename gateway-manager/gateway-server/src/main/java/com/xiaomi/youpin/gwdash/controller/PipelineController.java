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
//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.youpin.gwdash.annotation.OperationLog;
//import com.xiaomi.youpin.gwdash.bo.DeployInfo;
//import com.xiaomi.youpin.gwdash.bo.DeployMachine;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.BizUtils;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.HttpService;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.NginxService;
//import com.xiaomi.youpin.gwdash.service.PipelineService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import com.xiaomi.youpin.quota.bo.ExpansionBo;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author tsingfu
// *
// * 需要确保第一个参数是projectId
// * AOP会使用
// *
// * 项目编译部署相关
// */
//@RestController
//@Slf4j
//public class PipelineController {
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private NginxService nginxService;
//
//    @RequestMapping(value = "/api/pipeline/present", method = RequestMethod.POST)
//    public Result<ProjectPipeline> getPipeline(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId) {
//        return pipelineService.getProjectPipeline(projectId, envId);
//    }
//
//    @RequestMapping(value = "/api/pipeline/info", method = RequestMethod.GET)
//    public Result<ProjectPipeline> getProjectPipelineById(
//            // aop权限校验
//            @RequestParam("projectId") long projectId,
//            @RequestParam("id") long id) {
//        return pipelineService.getProjectPipelineById(id);
//    }
//
//    @OperationLog(type = OperationLog.LogType.ADD,exclusion = OperationLog.Column.RESULT)
//    @RequestMapping(value = "/api/pipeline/startPipeline", method = RequestMethod.POST)
//    public Result<ProjectPipeline> startPipeline(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam(name = "commitId", required = false) String commitId,
//            @RequestParam(value = "pipelineId", required = false, defaultValue = "0") long pipelineId,
//            @RequestParam(value = "force", required = false) boolean force) {
//        SessionAccount account = loginService.getAccountFromSession();
//        return pipelineService.startPipeline(projectId, envId, pipelineId, commitId, account,force,false);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/pipeline/closePipeline", method = RequestMethod.POST)
//    public Result<Boolean> closePipeline(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam("cmd") String cmd) {
//        SessionAccount account = loginService.getAccountFromSession();
//        return pipelineService.closePipeline(projectId, envId, cmd);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE,exclusion = OperationLog.Column.RESULT)
//    @RequestMapping(value = "/api/pipeline/startCompile", method = RequestMethod.POST)
//    public Result<ProjectPipeline> startCompile(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId) {
//        SessionAccount account = loginService.getAccountFromSession();
//        return pipelineService.startCompile(account.getUsername(), projectId, envId);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE,exclusion = OperationLog.Column.RESULT)
//    @RequestMapping(value = "/api/pipeline/startCodeCheck", method = RequestMethod.POST)
//    public Result<ProjectPipeline> startCodeCheck(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId) {
//        SessionAccount account = loginService.getAccountFromSession();
//        return pipelineService.startCodeCheck(projectId, envId, account);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE,exclusion = OperationLog.Column.RESULT)
//    @RequestMapping(value = "/api/pipeline/startDeploy", method = RequestMethod.POST)
//    public Result<ProjectPipeline> startDeploy(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId) {
//        return pipelineService.startDeploy(projectId, envId);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/pipeline/docker/scale", method = RequestMethod.POST)
//    public Result<Boolean> dockerScale (
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam("replicate") long replicate) {
//        SessionAccount account = loginService.getAccountFromSession();
//        if (!(BizUtils.isSuperRole(account))
//                &&replicate > Consts.INSTANCE_LIMIT) {
//            return new Result<>(1, "副本数超出限制，请联系效能团队添加", false);
//        }
//        return pipelineService.dockerScale(projectId, envId, replicate, true);
//    }
//
//    @RequestMapping(value = "/api/pipeline/docker/scaleNum", method = RequestMethod.POST)
//    public Result<ExpansionBo> dockerScaleNum (
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId) {
//        return pipelineService.dockerScaleNum(projectId, envId);
//    }
//
//    //判断是否需要部署人填写 动态验证码、sidList
//    @RequestMapping(value = "/api/pipeline/preFortCheck", method = RequestMethod.POST)
//    public Result<Boolean> preFortCheck(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId) {
//        return pipelineService.preFortCheck(projectId, envId);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE,exclusion = OperationLog.Column.RESULT)
//    @RequestMapping(value = "/api/pipeline/startFort", method = RequestMethod.POST)
//    public Result<ProjectPipeline> startFort(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam(value="mfa", required=false) String mfa,
//            //多个sid用逗号分隔
//            @RequestParam(value="sidList", required=false) String sidList) {
//        SessionAccount account = loginService.getAccountFromSession();
//
//        return pipelineService.startFort(projectId, envId, account.getUsername(), mfa, sidList);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE,exclusion = OperationLog.Column.RESULT)
//    @RequestMapping(value = "/api/pipeline/startBatch", method = RequestMethod.POST)
//    public Result<ProjectPipeline> startBatch(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam("batchNum") int batchNum) {
//        return pipelineService.startBatch(projectId, envId, batchNum);
//    }
//
//    @OperationLog(type = OperationLog.LogType.UPDATE,exclusion = OperationLog.Column.RESULT)
//    @RequestMapping(value = "/api/pipeline/retryBatch", method = RequestMethod.POST)
//    public Result<ProjectPipeline> retryBatch(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam("batchNum") int batchNum) {
//        return pipelineService.retryBatch(projectId, envId, batchNum);
//    }
//
//    /**
//     * 用户反馈部署成功或者失败
//     * @param projectId
//     * @param envId
//     * @param batchNum
//     * @param operation 0失败 1成功
//     * @return
//     *
//     */
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/pipeline/userBatch", method = RequestMethod.POST)
//    public Result<Boolean> userBatch(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam("pipelineId") long pipelineId,
//            @RequestParam("batchNum") int batchNum,
//            @RequestParam("operation") int operation) {
//        return pipelineService.userBatch(projectId, envId, pipelineId, batchNum, operation);
//    }
//
//
//
//    /**
//     * 更新nginx代码
//     * @return
//     */
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/pipeline/deploy/nginx", method = RequestMethod.POST)
//    public Result<Boolean> deployNginx(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId) {
//        HttpService httpService = nginxService.getNginxServiceByEnvId(envId).getData();
//        if (null == httpService) {
//            return new Result<>(0, "nginx配置不存在", false);
//        }
//        ProjectPipeline pipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
//        if (null == pipeline) {
//            return new Result<>(0, "部署pipeline不存在", false);
//        }
//        DeployInfo deployInfo = pipeline.getDeployInfo();
//        if (null == deployInfo) {
//            return new Result<>(0, "部署信息不存在", false);
//        }
//        List<DeployMachine> deployMachines = deployInfo.getDockerMachineList();
//        if (null == deployMachines || deployMachines.size() <= 0 ) {
//            return new Result<>(0, "部署机器列表空", false);
//        }
//        List<String> updateIpList = deployMachines.stream().map(it -> it.getIp().trim() + ":" + httpService.getPort()).collect(Collectors.toList());
//        String newConfig = nginxService.snycConfig2NacosByIpList(httpService.getId(), updateIpList);
//        if (null == newConfig) {
//            return new Result<>(0, "newConfig空", false);
//        }
//        log.info("deployNginx: {}", newConfig);
//        nginxService.deployConfig2NginxById(httpService.getId() ,newConfig);
//        return Result.success(true);
//    }
//
//    /**
//     * 初始化project_pipeline中的tag
//     * @param key
//     * @return
//     */
//    @OperationLog(type = OperationLog.LogType.UPDATE)
//    @RequestMapping(value = "/api/project/env/initPipelineTag", method = RequestMethod.GET)
//    public Result<Integer> initPipelineTag(@RequestParam("key") String key) {
//        if (StringUtils.isEmpty(key) || !"mi@110955".equals(key)) {
//            return Result.success(new Integer(0));
//        }
//        return pipelineService.initPipelineTag();
//    }
//
//    /**
//     * 获取正在部署的机器的日志快照
//     * @param projectId
//     * @param envId
//     * @param ip
//     * @return
//     */
//    @RequestMapping(value = "/api/pipeline/deploy/log/snapshot", method = RequestMethod.POST)
//    public Result<String> getLogSnapshot(
//            @RequestParam("projectId") long projectId,
//            @RequestParam("envId") long envId,
//            @RequestParam("pipelineId") long pipelineId,
//            @RequestParam("ip") String ip,
//            @RequestParam(value = "lines", defaultValue = "50", required = false) String lines) {
//        return pipelineService.getDeployLogSnapshot(pipelineId, ip, lines);
//    }
//
//}
