package com.xiaomi.miapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xiaomi.miapi.common.pojo.*;
import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.miapi.common.bo.ApiEnvBo;
import com.xiaomi.miapi.common.bo.ProjectGroupBo;
import com.xiaomi.miapi.service.ProjectService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.vo.BusProjectVo;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.youpin.hermes.entity.BusProject;
import com.xiaomi.youpin.hermes.entity.ProjectGroup;
import com.xiaomi.youpin.hermes.service.AccountService;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 项目[业务处理层]
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "java.lang.Exception")
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private RedisUtil redis;

    @Autowired
    private ApiGroupMapper apiGroupMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private ProjectOperationLogMapper projectOperationLogMapper;
    @Autowired
    private ApiMapper apiMapper;
    @Autowired
    private ApiCacheMapper apiCacheMapper;

    @Autowired
    private EoDubboApiInfoMapper dubboApiInfoMapper;

    @Autowired
    private GatewayApiInfoMapper gatewayApiInfoMapper;

    @Autowired
    private ApiEnvMapper apiEnvMapper;
    @Autowired
    private ProjectFocusMapper projectFocusMapper;

    @DubboReference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    @DubboReference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}", timeout = 4000)
    private AccountService accountService;

    private static final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    /**
     * 新建项目
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public Result<Boolean> addProject(Project project, Integer userID, String username) {
        LOGGER.info("[ProjectService.addProject],userId:{},project:{}", userID, project.toString());
        Date date = new Date();
        Timestamp updateTime = new Timestamp(date.getTime());
        project.setProjectUpdateTime(updateTime);
        BusProject busProject = new BusProject();
        busProject.setName(project.getProjectName());
        busProject.setCtime(System.currentTimeMillis());
        busProject.setUtime(System.currentTimeMillis());
        if (project.getDesc() != null) {
            busProject.setDescription(project.getDesc());
        } else {
            busProject.setDescription("");
        }
        busProject.setIsPublic(project.getIsPublic());
        busProject.setBusGroupID(project.getProjectGroupID());
        BusProject busProjectRt = busProjectService.createBusProject(busProject, userID);
        if (busProjectRt == null) {
            LOGGER.info("[ProjectService.addProject] failed,userId:{},project:{}", userID, project.toString());
            return Result.fail(CommonError.RpcCallError);
        }
        if (busProjectRt.getDescription() != null && busProjectRt.getDescription().equals("ProjectAlreadyExist")) {
            return Result.fail(CommonError.ProjectAlreadyExist);
        }
        String groupName = "默认分组";

        // 添加接口默认分组
        ApiGroup apiGroup = new ApiGroup();
        apiGroup.setGroupName(groupName);
        apiGroup.setProjectID(busProjectRt.getId());
        apiGroup.setSystemGroup(true);
        int rt = apiGroupMapper.addApiGroup(apiGroup);
        if (rt < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        //添加默认环境
        ApiEnv apiEnv = new ApiEnv();
        apiEnv.setEnvName("默认环境");
        apiEnv.setProjectId(busProjectRt.getId());
        apiEnv.setEnvDesc("自动创建的默认环境");
        apiEnv.setHttpDomain("http://127.0.0.1:8080");
        apiEnv.setSysDefault(true);
        int rt2 = apiEnvMapper.insert(apiEnv);
        if (rt2 < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        // 添加操作记录
        ProjectOperationLog projectOperationLog = new ProjectOperationLog();
        projectOperationLog.setOpProjectID(busProjectRt.getId());
        projectOperationLog.setOpDesc("创建项目");
        projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT);
        projectOperationLog.setOpTargetID(busProjectRt.getId());
        projectOperationLog.setOpTime(updateTime);
        projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
        projectOperationLog.setOpUsername(username);
        int rt3 = projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
        if (rt3 < 0) {
            return Result.fail(CommonError.UnknownError);
        }
        // 返回信息
        return Result.success(true);
    }

    @Override
    public boolean focusProject(Integer projectId, Integer accountId) {
        BusProject busProject = busProjectService.queryBusProjectById(projectId);
        if (busProject == null) {
            return false;
        }
        ProjectFocus projectFocus = new ProjectFocus();
        projectFocus.setBusprojectid(projectId);
        projectFocus.setUserid(accountId);
        int result = projectFocusMapper.insert(projectFocus);
        return result >= 0;
    }

    @Override
    public Result<Boolean> unFocusProject(Integer projectId, Integer accountId) {
        ProjectFocusExample example = new ProjectFocusExample();
        ProjectFocusExample.Criteria criteria = example.createCriteria();
        criteria.andBusprojectidEqualTo(projectId).andUseridEqualTo(accountId);
        List<ProjectFocus> projectFocusList = projectFocusMapper.selectByExample(example);
        if (projectFocusList == null || projectFocusList.size() == 0) {
            return Result.fail(CommonError.InvalidIDParamError);
        }

        ProjectFocus projectFocus = projectFocusList.get(0);
        int result = projectFocusMapper.deleteByPrimaryKey(projectFocus.getId());
        if (result > 0) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    public Result<Map<String, Object>> getMyProjects(Integer userId) {
        Map<String, Object> resultMap = new HashMap<>();
        List<BusProjectVo> focusProjects = getFocusProject(userId);
        resultMap.put("myFocus", focusProjects);


        List<BusProject> adminProjectList = busProjectService.getMyAdminProjects(userId);
        List<BusProjectVo> adminProjectVos = new ArrayList<>(adminProjectList.size());
        for (BusProject b :
                adminProjectList) {
            BusProjectVo vo = new BusProjectVo();
            BeanUtils.copyProperties(b, vo);
            vo.setApiCount(getApiNum(b.getId()));
            adminProjectVos.add(vo);
        }
        resultMap.put("myAdmin", adminProjectVos);
        return Result.success(resultMap);
    }

    @Override
    public List<BusProjectVo> getFocusProject(Integer accountId) {
        ProjectFocusExample example = new ProjectFocusExample();
        ProjectFocusExample.Criteria criteria = example.createCriteria();
        criteria.andUseridEqualTo(accountId);
        List<ProjectFocus> projectFocusList = projectFocusMapper.selectByExample(example);

        List<Integer> busProjectIds = new ArrayList<>(projectFocusList.size());
        for (ProjectFocus projectFocus :
                projectFocusList) {
            busProjectIds.add(projectFocus.getBusprojectid());
        }
        List<BusProject> busProjects = busProjectService.getBusProjectsByIds(busProjectIds);
        List<BusProjectVo> busProjectVos = new ArrayList<>(busProjects.size());
        for (BusProject b :
                busProjects) {
            BusProjectVo vo = new BusProjectVo();
            BeanUtils.copyProperties(b, vo);
            vo.setApiCount(getApiNum(b.getId()));
            busProjectVos.add(vo);
        }
        return busProjectVos;
    }

    /**
     * 删除项目
     */
    @Override
    @Transactional
    public boolean deleteProject(Integer projectID, Integer userId, String username) {
        ProjectOperationLog projectOperationLog = new ProjectOperationLog();
        projectOperationLog.setOpProjectID(projectID);
        projectOperationLog.setOpUsername(username);
        projectOperationLog.setOpTargetID(userId);
        projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT);
        projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
        projectOperationLog.setOpDesc("删除项目:" + projectID);

        this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
        if (busProjectService.deleteBusProject(projectID, userId)) {
            List<Api> apis = apiMapper.getAllApiByProjectID(projectID);
            if (apis.isEmpty()) {
                return true;
            }
            List<Integer> apiIDs = new ArrayList<>();
            for (Api api :
                    apis) {
                if (api.getApiProtocol() != Consts.HTTP_API_TYPE) {
                    switch (api.getApiProtocol()) {
                        case Consts.DUBBO_API_TYPE:
                            //删dubbo api
                            dubboApiInfoMapper.deleteByPrimaryKey(api.getDubboApiId());

                        case Consts.GATEWAY_API_TYPE:
                            //删网关api
                            gatewayApiInfoMapper.deleteByPrimaryKey(api.getGatewayApiId().longValue());
                    }
                }
                apiIDs.add(api.getApiID());
            }
            apiMapper.deleteApi(projectID, apiIDs);
            apiMapper.batchDeleteApiHeader(apiIDs);
            apiMapper.batchDeleteRequestParam(apiIDs);
            apiMapper.batchDeleteResultParam(apiIDs);
        }
        return true;
    }

    /**
     * 获取项目列表
     */
    @Override
    public Result<List<BusProjectVo>> getProjectList(Integer userId) {

        List<BusProject> busProjects = busProjectService.getAllBusProjects();
        LOGGER.info("[ProjectService.busProjectService.getAllBusProjects],userId:{},projects:{}", userId, busProjects);

        if (null == busProjects) {
            return Result.success(null);
        }
        List<BusProjectVo> focusProjects = getFocusProject(userId);

        List<BusProjectVo> busProjectVos = new ArrayList<>(busProjects.size());
        for (BusProject p :
                busProjects) {
            BusProjectVo vo = new BusProjectVo();
            BeanUtils.copyProperties(p, vo);
            vo.setFocus(false);
            for (BusProjectVo b :
                    focusProjects) {
                if (p.getId() == b.getId()) {
                    vo.setFocus(true);
                }
            }
            busProjectVos.add(vo);
        }
        return Result.success(busProjectVos);
    }

    @Override
    public Result<Map<String, List<Map<String, Object>>>> indexSearch(String keyword) {

        Map<String, List<Map<String, Object>>> resultMap = new HashMap<>();
        try {
            //搜索项目关键字相关
            List<BusProject> projects = busProjectService.searchBusProjectsByKeyword(keyword);
            List<Map<String, Object>> projectList = new ArrayList<>(projects.size());

            for (BusProject p :
                    projects) {
                Map<String, Object> map = JSON.parseObject(gson.toJson(p), Map.class);
                ProjectGroup projectGroup = busProjectService.getProjectGroupById(p.getBusGroupID());
                if (Objects.nonNull(projectGroup)) {
                    map.put("projectGroupName", projectGroup.getGroupName());
                }
                projectList.add(map);
            }
            resultMap.put("projectList", projectList);
            //搜索api关键字相关
            List<Api> apis = apiMapper.searchAllApi(keyword);
            List<Map<String, Object>> apiList = new ArrayList<>(apis.size());

            for (Api api :
                    apis) {
                Map<String, Object> map = JSON.parseObject(gson.toJson(api), Map.class);
                List<Integer> id = new ArrayList<>();
                id.add(api.getProjectID());

                if (!busProjectService.getBusProjectsByIds(id).isEmpty()) {
                    BusProject project = busProjectService.getBusProjectsByIds(id).get(0);
                    map.put("projectName", project.getName());
                }

                apiList.add(map);
            }
            resultMap.put("apiList", apiList);

            //搜索文档关键字
            List<DocumentDTO> documentDTOS = documentMapper.searchAllDocument(keyword);
            List<Map<String, Object>> documentList = new ArrayList<>(documentDTOS.size());
            for (DocumentDTO document :
                    documentDTOS) {
                Map<String, Object> map = JSON.parseObject(gson.toJson(document), Map.class);
                documentList.add(map);
            }
            resultMap.put("documentList", documentList);
        } catch (Exception e) {
            LOGGER.error("search info error");
            return Result.fail(CommonError.UnknownError);
        }

        return Result.success(resultMap);

    }

    @Override
    public List<BusProjectVo> getRecentlyProjectList(Integer userId) {
        List<BusProjectVo> busProjects = new ArrayList<>();
        List<String> projectIdsStr = redis.lRange(Consts.genRecentlyProjectsKey(userId), 0, 8);
        if (projectIdsStr == null || projectIdsStr.size() == 0) {
            return busProjects;
        }
        List<Integer> projectIds = new ArrayList<>();
        for (String id :
                projectIdsStr) {
            projectIds.add(Integer.parseInt(id));
        }
        List<BusProject> busProjectList = busProjectService.getBusProjectsByIds(projectIds);

        //用户关注项目列表
        List<BusProjectVo> focusProjects = getFocusProject(userId);

        for (BusProject p :
                busProjectList) {
            BusProjectVo vo = new BusProjectVo();
            BeanUtils.copyProperties(p, vo);

            vo.setFocus(false);
            for (BusProjectVo b :
                    focusProjects) {
                if (p.getId() == b.getId()) {
                    vo.setFocus(true);
                }
            }
            busProjects.add(vo);
        }
        return busProjects;
    }

    @Override
    public Result<List<BusProjectVo>> getProjectListByProjectGroupId(Integer projectGroupID, Integer userId, String username) {
        List<BusProject> busProjects = busProjectService.getBusProjectInfosByProjectGroupId(projectGroupID);
        LOGGER.info("[ProjectService.busProjectService.getAllBusProjects],userId:{},projects:{}", userId, busProjects);

        if (null == busProjects) {
            return Result.success(new ArrayList<>());
        }
        List<BusProjectVo> focusProjects = getFocusProject(userId);

        List<BusProjectVo> busProjectVos = new ArrayList<>(busProjects.size());
        for (BusProject p :
                busProjects) {
            BusProjectVo vo = new BusProjectVo();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String updateTime = dateFormat.format(p.getUtime());
            vo.setProjectUpdateTime(updateTime);
            BeanUtils.copyProperties(p, vo);
            vo.setApiCount(apiMapper.getApiCount(p.getId()));

            vo.setFocus(false);
            for (BusProjectVo b :
                    focusProjects) {
                if (p.getId() == b.getId()) {
                    vo.setFocus(true);
                }
            }

            if (p.getIsPublic() == 0 && !busProjectService.isMember(Consts.PROJECT_NAME, p.getId(), username)) {
                continue;
            }
            busProjectVos.add(vo);
        }

        return Result.success(busProjectVos);
    }

    /**
     * 修改项目
     */
    @Override
    public boolean editProject(Project project, String username) {
        BusProject busProject = new BusProject();
        busProject.setId(project.getProjectID());
        busProject.setUtime(System.currentTimeMillis());
        busProject.setDescription(project.getDesc());
        busProject.setName(project.getProjectName());
        busProject.setIsPublic(project.getIsPublic());
        Boolean ok = busProjectService.updateBusProject(busProject);
        if (ok) {
            Date date = new Date();
            Timestamp updateTime = new Timestamp(date.getTime());
            ProjectOperationLog projectOperationLog = new ProjectOperationLog();
            projectOperationLog.setOpProjectID(project.getProjectID());
            projectOperationLog.setOpUsername(username);
            projectOperationLog.setOpTime(updateTime);
            projectOperationLog.setOpTargetID(project.getProjectID());
            projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT);
            projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
            projectOperationLog.setOpDesc("编辑项目:" + project.getProjectName());

            int rt = projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
            if (rt < 0) {
                return false;
            }
            //处理老项目没有默认环境问题
            ApiEnvExample example = new ApiEnvExample();
            example.createCriteria().andProjectIdEqualTo(project.getProjectID()).andSysDefaultEqualTo(true);
            List<ApiEnv> envList = apiEnvMapper.selectByExample(example);
            if (Objects.isNull(envList) || envList.isEmpty()) {
                ApiEnv apiEnv = new ApiEnv();
                apiEnv.setEnvName("默认环境");
                apiEnv.setProjectId(project.getProjectID());
                apiEnv.setEnvDesc("自动创建的默认环境");
                apiEnv.setHttpDomain("http://127.0.0.1:8080");
                apiEnv.setSysDefault(true);
                apiEnvMapper.insert(apiEnv);
            }
        }
        return ok;
    }

    /**
     * 获取项目信息
     */
    @Override
    public Result<Map<String, Object>> getProject(Integer projectID, Integer userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        BusProject busProject = busProjectService.queryBusProjectById(projectID);
        if (busProject == null) {
            Result.fail(CommonError.ProjectDoNotExist);
        }
        map.put("projectId", busProject.getId());
        map.put("projectName", busProject.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String updateTime = dateFormat.format(busProject.getUtime());
        map.put("projectUpdateTime", updateTime);
        map.put("desc", busProject.getDescription());
        map.put("apiCount", apiMapper.getApiCount(projectID));
        map.put("memberCount", busProject.getMemberNum());
        map.put("isPublic", busProject.getIsPublic());
        map.put("busGroupID", busProject.getBusGroupID());
        Integer dayOffset = 1;
        map.put("logCount", projectOperationLogMapper.getLogCount(projectID, dayOffset));

        redis.recordRecently10Projects(userId, projectID);
        return Result.success(map);
    }

    /**
     * 获取项目日志列表
     */
    @Override
    public List<Map<String, Object>> getProjectLogList(Integer projectID, Integer page, Integer pageSize) {
        Integer dayOffset = 7;
        page = (page - 1) * pageSize;
        List<Map<String, Object>> logList = projectOperationLogMapper.getProjectLogList(projectID, page, pageSize,
                dayOffset);
        {
            return logList;
        }
    }

    /**
     * 获取项目日志列表
     */
    @Override
    public int getProjectLogCount(Integer projectID, int dayOffset) {
        return projectOperationLogMapper.getLogCount(projectID, dayOffset);
    }

    /**
     * 获取接口数量
     */
    @Override
    public int getApiNum(Integer projectID) {
        return apiMapper.getApiCount(projectID);
    }

    @Override
    public Result<Boolean> createProjectGroup(ProjectGroupBo projectGroupBo,int userId) {
        ProjectGroup projectGroup = new ProjectGroup();

        BeanUtils.copyProperties(projectGroupBo, projectGroup);
        projectGroup.setUserId(userId);
        ProjectGroup projectGroupRt = busProjectService.createProjectGroup(projectGroup);
        if (projectGroupRt == null) {
            return Result.fail(CommonError.UnknownError);
        } else if ("ProjectGroupAlreadyExist".equals(projectGroupRt.getGroupDesc())) {
            return Result.fail(CommonError.ProjectGroupAlreadyExist);
        }
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateProjectGroup(ProjectGroupBo projectGroupBo) {
        ProjectGroup projectGroup = new ProjectGroup();
        BeanUtils.copyProperties(projectGroupBo, projectGroup);
        projectGroup.setGroupId(projectGroupBo.getGroupID());
        Boolean ok = busProjectService.updateProjectGroup(projectGroup);
        if (!ok) {
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(true);
    }

    @Override
    public List<ProjectGroup> getAllProjectGroup() {
        return busProjectService.getAllProjectGroup();
    }

    @Override
    public List<ProjectGroup> getAllAccessableProjectGroup(int accountId) {
        return busProjectService.getAllAccessableProjectGroup(accountId);
    }

    @Override
    public Result<ProjectGroup> getProjectGroupById(Integer id) {
        ProjectGroup projectGroup = busProjectService.getProjectGroupById(id);
        return Result.success(projectGroup);
    }

    @Override
    public Result<Boolean> deleteProjectGroup(Integer projectGroupId, String userName) {
        com.xiaomi.youpin.hermes.bo.Result<Boolean> result = busProjectService.deleteProjectGroup(projectGroupId, userName);
        if (result.getCode() != CommonError.Success.getCode()) {
            return Result.fail(CommonError.valueOf(result.getMessage()));
        }
        //todo 删除项目
        return Result.success(true);
    }

    @Override
    public Result<Boolean> addApiEnv(ApiEnvBo bo, String opUsername) {
        ApiEnv env = new ApiEnv();
        BeanUtils.copyProperties(bo, env);
        env.setProjectId(bo.getProjectID());
        env.setSysDefault(false);

        if (apiEnvMapper.insert(env) > 0) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    public Result<Boolean> editApiEnv(ApiEnvBo bo, String opUsername) {
        ApiEnv oldApiEnv = apiEnvMapper.selectByPrimaryKey(bo.getId());
        if (Objects.isNull(oldApiEnv)) {
            return Result.fail(CommonError.InvalidIDParamError);
        }
        oldApiEnv.setHttpDomain(bo.getHttpDomain());
        oldApiEnv.setEnvName(bo.getEnvName());
        oldApiEnv.setEnvDesc(bo.getEnvDesc());
        oldApiEnv.setReqParamFormData(bo.getReqParamFormData());
        oldApiEnv.setHeaders(bo.getHeaders());
        oldApiEnv.setReqParamRaw(bo.getReqParamRaw());
        if (apiEnvMapper.updateByPrimaryKeyWithBLOBs(oldApiEnv) > 0) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    public Result<Boolean> deleteApiEnv(Integer envID, String opUsername) {
        if (apiEnvMapper.deleteByPrimaryKey(envID) > 0) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    public Result<ApiEnv> getApiEnv(Integer envID) {
        return Result.success(apiEnvMapper.selectByPrimaryKey(envID));
    }

    @Override
    public Result<List<ApiEnv>> getApiEnvList(Integer projectID) {
        ApiEnvExample example = new ApiEnvExample();
        example.createCriteria().andProjectIdEqualTo(projectID);
        List<ApiEnv> envList = apiEnvMapper.selectByExample(example);
        return Result.success(envList);
    }
}
