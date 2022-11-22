package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.ApiGroupInfoService;
import com.xiaomi.youpin.gwdash.service.AuditingServiceAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @author liuyuchong
 */
@Slf4j
@RestController
public class OpenApiController {

    @Resource
    private ApiGroupInfoService groupService;

    @Resource
    private AuditingServiceAPI auditingServiceAPI;
    /**
     * 审核相关处理
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/open/auditing/handler", method = RequestMethod.POST, consumes = {"application/json"})
    public Map<String, Object> auditingHandler(HttpServletRequest request,
                                               @RequestBody String param) {
        log.info("auditingHandler param:[{}]",param);
        return auditingServiceAPI.groupApply(param);
    }

    @RequestMapping(value = "init/group", method = RequestMethod.GET)
    public Result<Boolean> initGroup(HttpServletRequest request)  {
        return groupService.initGroup();
    }

    @RequestMapping(value = "/init/gid", method = RequestMethod.GET)
    public Result<Boolean> initGid(HttpServletRequest request) {
        return groupService.initGid();
    }

    @RequestMapping("/healthCheck")
    public Map<String,String> health(){
        Map<String,String> result = new HashMap<>();
        result.put("code","200");

        return result;
    }

//    @Autowired
//    private ResourceService resourceService;
//
//    @Autowired
//    private AgentManagerServiceWrapper agentManager;
//
//    @Autowired
//    private WebSSHService webSSHService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private PipelineService pipelineService;

//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private GwCache cache;
//
//    /**
//     * 获取docker资源列表
//     *
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/docker/list", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Map<String, Object>> resourceList(@RequestBody RequestParam param) {
//        com.xiaomi.youpin.quota.bo.Result<Map<String, Object>> result = resourceService.getResourceList(1, 100, 0,"", Maps.newHashMap());
//        if (result.getCode() == 0) {
//            return Result.success(result.getData());
//        }
//        return Result.success(Maps.newHashMap());
//    }
//
//
//    /**
//     * 获取所有机器列表
//     *
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/machine/list", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<List<String>> machineList(@RequestBody RequestParam param) {
//        List<String> list = agentManager.clientList();
//        return Result.success(list);
//    }
//
//
//    @RequestMapping(value = "/open/plantsshkey", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> plantSshKey(@RequestBody RequestParam param) {
//        webSSHService.plantSshKey(param.getAddress());
//        return Result.success(true);
//    }
//
//
//    @RequestMapping(value = "/open/removesshkey", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> removeSshKey(@RequestBody RequestParam param) {
//        webSSHService.removeSshKey(param.getAddress());
//        return Result.success(true);
//    }
//
//    /**
//     * 用来测试接口
//     *
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/test", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<String> test(@RequestBody RequestParam param) {
//        return Result.success(new GwDashVersion().toString());
//    }
//
//
//    /**
//     * docker nuke
//     */
//    @RequestMapping(value = "/open/docker/nuke", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> nuke(@RequestBody RequestParam param,
//                               @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
//                               @org.springframework.web.bind.annotation.RequestParam("ip") String ip) {
//        log.info("open api nuke params: envId: {}, ip: {}", envId, ip);
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return Result.fail(InvalidParamError);
//        }
//        return projectEnvService.setDockerNuke(envId, ip);
//    }
//
//    /**
//     * docker shutdown
//     */
//    @RequestMapping(value = "/open/docker/shutdown", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> shutdown(@RequestBody RequestParam param,
//                                @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
//                                @org.springframework.web.bind.annotation.RequestParam("ip") String ip) {
//        log.info("open api shutdown params: envId: {}, ip: {}", envId, ip);
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return Result.fail(InvalidParamError);
//        }
//        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip,true);
//    }
//
//
//    /**
//     * docker online
//     */
//    @RequestMapping(value = "/open/docker/online", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> online(@RequestBody RequestParam param,
//                                @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
//                                @org.springframework.web.bind.annotation.RequestParam("ip") String ip) {
//        log.info("open api online params: envId: {}, ip: {}", envId, ip);
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return Result.fail(InvalidParamError);
//        }
//        return projectEnvService.shutDownOrOnlineDockerMachine(envId, ip,false);
//    }
//
//    /**
//     * docker 扩容 缩容
//     *
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/docker/scale", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> scale(@RequestBody RequestParam param,
//                                @org.springframework.web.bind.annotation.RequestParam("projectId") long projectId,
//                                @org.springframework.web.bind.annotation.RequestParam("envId") long envId,
//                                @org.springframework.web.bind.annotation.RequestParam("replicate") long replicate) {
//        return pipelineService.dockerScale(projectId, envId, replicate);
//    }
//
//
//    /**
//     * 根据项目名称或者项目id获取环境列表id （envId）
//     */
//    @RequestMapping(value = "/open/env/list", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<List<ProjectEnv>> envList(@RequestBody RequestParam param,
//                                            @org.springframework.web.bind.annotation.RequestParam(required = false) Long projectId,
//                                            @org.springframework.web.bind.annotation.RequestParam(required = false) String projectName) {
//        log.info("params for open env list, projectId: {}, projectName: {}", projectId, projectName);
//        if (projectId == null && StringUtils.isEmpty(projectName)) {
//            return Result.fail(InvalidParamError);
//        }
//        if (projectId == null) {
//            Project project = projectService.getProjectByName(projectName);
//            if (project == null) {
//                log.error("project not exist, projectName: {}", projectName);
//                return Result.fail(InvalidParamError);
//            }
//            projectId = project.getId();
//        }
//        return projectEnvService.getList(projectId);
//    }
//
//    /**
//     * docker 根据envId获取机器列表
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/docker/machines", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Map<String, Object>> machines(@RequestBody RequestParam param,
//                                    @org.springframework.web.bind.annotation.RequestParam("envId") long envId) {
//        return projectEnvService.getDockerStatus(envId);
//    }
//
//
//    /**
//     * docker 更新容器配置
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/open/docker/settings/save", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<Boolean> save(@RequestBody RequestParam<ProjectEnvDeploySetting> param) {
//
//        ProjectEnvDeploySetting projectEnvDeploySetting = param.getParam();
//        if (projectEnvDeploySetting == null) {
//            return Result.fail(InvalidParamError);
//        }
//
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(projectEnvDeploySetting.getEnvId()).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "环境不存在", false);
//        }
//        if (DeployTypeEnum.isDocker(projectEnv.getDeployType())) {
//            String logPath = projectEnvDeploySetting.getLogPath();
//            if (null == logPath || org.springframework.util.StringUtils.isEmpty(logPath.trim())) {
//                return new Result<>(6, "需配置日志路径", null);
//            }
//
//            if (!logPath.startsWith(Consts.DOCKER_LOG_PATH_PREFIX)) {
//                return new Result<>(1, "日志路径请以 '" + Consts.DOCKER_LOG_PATH_PREFIX + "' 开头", null);
//            }
//            String jvmParams = projectEnvDeploySetting.getJvmParams();
//            if (!org.springframework.util.StringUtils.isEmpty(jvmParams)) {
//                projectEnvDeploySetting.setJvmParams(jvmParams.trim());
//            }
//            long maxReplicate = projectEnvDeploySetting.getMaxReplicate();
//            long replicate = projectEnvDeploySetting.getReplicate();
//            if (maxReplicate < replicate) {
//                return new Result<>(1, "最大实例不能小于实例数", false);
//            }
//            ProjectPipeline pipeline = pipelineService.getProjectPipelineOfEnv(projectEnv.getId()).getData();
//            if (null != pipeline) {
//                long curReplicate = pipeline.getDeploySetting().getDockerReplicate();
//                if (maxReplicate < curReplicate) {
//                    return new Result<>(1, "最大实例不能小于当前部署已有实例数", false);
//                }
//            }
//        }
//        return projectEnvService.saveDeployment(projectEnvDeploySetting);
//    }

}
