package com.xiaomi.miapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xiaomi.miapi.bo.ApiEnvBo;
import com.xiaomi.miapi.bo.Project;
import com.xiaomi.miapi.bo.ProjectGroupBo;
import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.pojo.*;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.miapi.service.ProjectService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.vo.BusProjectVo;
import com.xiaomi.miapi.mapper.*;
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
 * @author dongzhenxing
 * @date 2023/02/08
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
    private EoDubboApiInfoMapper dubboApiInfoMapper;

    @Autowired
    private GatewayApiInfoMapper gatewayApiInfoMapper;

    @Autowired
    private ApiEnvMapper apiEnvMapper;
    @Autowired
    private ProjectFocusMapper projectFocusMapper;

    @Autowired
    private BusProjectMapper busProjectMapper;

    @Autowired
    private BusProjectGroupMapper busProjectGroupMapper;

    private static final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result<Boolean> addProject(Project project, String username) {
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
        busProject.setIsPublic(project.getIsPublic() == 1);
        busProject.setBusGroupId(project.getProjectGroupID());
        busProject.setStatus(0);
        busProject.setVersion(project.getProjectVersion());
        busProjectMapper.insert(busProject);
        String groupName = "默认分组";

        // add api default group
        ApiGroup apiGroup = new ApiGroup();
        apiGroup.setGroupName(groupName);
        apiGroup.setProjectID(busProject.getId());
        apiGroup.setSystemGroup(true);
        int rt = apiGroupMapper.addApiGroup(apiGroup);
        if (rt < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        //add default env
        ApiEnv apiEnv = new ApiEnv();
        apiEnv.setEnvName("默认环境");
        apiEnv.setProjectId(busProject.getId());
        apiEnv.setEnvDesc("自动创建的默认环境");
        apiEnv.setHttpDomain("http://127.0.0.1:8080");
        apiEnv.setSysDefault(true);
        int rt2 = apiEnvMapper.insert(apiEnv);
        if (rt2 < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        ProjectOperationLog projectOperationLog = new ProjectOperationLog();
        projectOperationLog.setOpProjectID(busProject.getId());
        projectOperationLog.setOpDesc("创建项目");
        projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT);
        projectOperationLog.setOpTargetID(busProject.getId());
        projectOperationLog.setOpTime(updateTime);
        projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
        projectOperationLog.setOpUsername(username);
        int rt3 = projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
        if (rt3 < 0) {
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(true);
    }

    @Override
    public boolean focusProject(Integer projectId, String username) {
        BusProject busProject = busProjectMapper.selectByPrimaryKey(projectId);
        if (busProject == null) {
            return false;
        }
        ProjectFocus projectFocus = new ProjectFocus();
        projectFocus.setBusprojectid(projectId);
        projectFocus.setUsername(username);
        int result = projectFocusMapper.insert(projectFocus);
        return result >= 0;
    }

    @Override
    public Result<Boolean> unFocusProject(Integer projectId, String username) {
        ProjectFocusExample example = new ProjectFocusExample();
        ProjectFocusExample.Criteria criteria = example.createCriteria();
        criteria.andBusprojectidEqualTo(projectId).andUsernameEqualTo(username);
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
    public Result<Map<String, Object>> getMyProjects(String username) {
        Map<String, Object> resultMap = new HashMap<>();
        List<BusProjectVo> focusProjects = getFocusProject(username);
        resultMap.put("myFocus", focusProjects);
        return Result.success(resultMap);
    }

    @Override
    public List<BusProjectVo> getFocusProject(String username) {
        ProjectFocusExample example = new ProjectFocusExample();
        ProjectFocusExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<ProjectFocus> projectFocusList = projectFocusMapper.selectByExample(example);

        List<Integer> busProjectIds = new ArrayList<>(projectFocusList.size());
        for (ProjectFocus projectFocus :
                projectFocusList) {
            busProjectIds.add(projectFocus.getBusprojectid());
        }
        List<BusProjectVo> busProjectVos = new ArrayList<>();
        if (busProjectIds.size() != 0){
            BusProjectExample example1 = new BusProjectExample();
            example1.createCriteria().andIdIn(busProjectIds);
            List<BusProject> busProjects = busProjectMapper.selectByExample(example1);
            for (BusProject b :
                    busProjects) {
                BusProjectVo vo = new BusProjectVo();
                BeanUtils.copyProperties(b, vo);
                vo.setApiCount(getApiNum(b.getId()));
                busProjectVos.add(vo);
            }
        }
        return busProjectVos;
    }

    @Override
    @Transactional
    public boolean deleteProject(Integer projectID,String username) {
        ProjectOperationLog projectOperationLog = new ProjectOperationLog();
        projectOperationLog.setOpProjectID(projectID);
        projectOperationLog.setOpUsername(username);
        projectOperationLog.setOpTargetID(0);
        projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT);
        projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
        projectOperationLog.setOpDesc("删除项目:" + projectID);

        this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
        if (busProjectMapper.deleteByPrimaryKey(projectID) > 0) {
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
                            //delete dubbo api
                            dubboApiInfoMapper.deleteByPrimaryKey(api.getDubboApiId());

                        case Consts.GATEWAY_API_TYPE:
                            //delete gateway api
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

    @Override
    public Result<List<BusProjectVo>> getProjectList(String username) {

        BusProjectExample busProjectExample = new BusProjectExample();
        busProjectExample.createCriteria().andNameIsNotNull();
        List<BusProject> busProjects = busProjectMapper.selectByExample(busProjectExample);
        LOGGER.info("[ProjectService.busProjectService.getAllBusProjects],username:{},projects:{}", username, busProjects);

        if (null == busProjects) {
            return Result.success(null);
        }
        List<BusProjectVo> focusProjects = getFocusProject(username);

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
            BusProjectExample example = new BusProjectExample();
            example.createCriteria().andNameLike("%"+keyword+"%");
            List<BusProject> projects = busProjectMapper.selectByExample(example);
            List<Map<String, Object>> projectList = new ArrayList<>(projects.size());

            for (BusProject p :
                    projects) {
                Map<String, Object> map = JSON.parseObject(gson.toJson(p), Map.class);
                BusProjectGroup projectGroup = busProjectGroupMapper.selectByPrimaryKey(p.getBusGroupId());
                if (Objects.nonNull(projectGroup)) {
                    map.put("projectGroupName", projectGroup.getGroupName());
                }
                projectList.add(map);
            }
            resultMap.put("projectList", projectList);
            List<Api> apis = apiMapper.searchAllApi(keyword);
            List<Map<String, Object>> apiList = new ArrayList<>(apis.size());

            for (Api api : apis) {
                Map<String, Object> map = JSON.parseObject(gson.toJson(api), Map.class);

                BusProject project = busProjectMapper.selectByPrimaryKey(api.getProjectID());
                if (project != null) {
                    map.put("projectName", project.getName());
                }

                apiList.add(map);
            }
            resultMap.put("apiList", apiList);

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
    public List<BusProjectVo> getRecentlyProjectList(String username) {
        List<BusProjectVo> busProjects = new ArrayList<>();
        List<String> projectIdsStr = redis.lRange(Consts.genRecentlyProjectsKey(username), 0, 8);
        if (projectIdsStr == null || projectIdsStr.size() == 0) {
            return busProjects;
        }
        List<Integer> projectIds = new ArrayList<>();
        for (String id :
                projectIdsStr) {
            projectIds.add(Integer.parseInt(id));
        }
        BusProjectExample example = new BusProjectExample();
        example.createCriteria().andIdIn(projectIds);
        List<BusProject> busProjectList = busProjectMapper.selectByExample(example);

        List<BusProjectVo> focusProjects = getFocusProject(username);

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
    public Result<List<BusProjectVo>> getProjectListByProjectGroupId(Integer projectGroupID, String username) {

        BusProjectExample example = new BusProjectExample();
        example.createCriteria().andBusGroupIdEqualTo(projectGroupID);
        List<BusProject> busProjects = busProjectMapper.selectByExample(example);

        if (null == busProjects) {
            return Result.success(new ArrayList<>());
        }
        List<BusProjectVo> focusProjects = getFocusProject(username);

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
            busProjectVos.add(vo);
        }

        return Result.success(busProjectVos);
    }

    @Override
    public boolean editProject(Project project, String username) {
        BusProject busProject = busProjectMapper.selectByPrimaryKey(project.getProjectID());
        busProject.setUtime(System.currentTimeMillis());
        busProject.setDescription(project.getDesc());
        busProject.setName(project.getProjectName());
        busProject.setIsPublic(project.getIsPublic() == 1);
        if (busProjectMapper.updateByPrimaryKey(busProject) > 0) {
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
            return rt >= 0;
        }
        return true;
    }

    @Override
    public Result<Map<String, Object>> getProject(Integer projectID, String username) {
        Map<String, Object> map = new HashMap<String, Object>();
        BusProject busProject = busProjectMapper.selectByPrimaryKey(projectID);
        if (busProject == null) {
            return Result.fail(CommonError.ProjectDoNotExist);
        }
        map.put("projectId", busProject.getId());
        map.put("projectName", busProject.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String updateTime = dateFormat.format(busProject.getUtime());
        map.put("projectUpdateTime", updateTime);
        map.put("desc", busProject.getDescription());
        map.put("apiCount", apiMapper.getApiCount(projectID));
        map.put("isPublic", busProject.getIsPublic());
        map.put("busGroupID", busProject.getBusGroupId());
        Integer dayOffset = 1;
        map.put("logCount", projectOperationLogMapper.getLogCount(projectID, dayOffset));

        redis.recordRecently10Projects(username, projectID);
        return Result.success(map);
    }

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

    @Override
    public int getProjectLogCount(Integer projectID, int dayOffset) {
        return projectOperationLogMapper.getLogCount(projectID, dayOffset);
    }

    @Override
    public int getApiNum(Integer projectID) {
        return apiMapper.getApiCount(projectID);
    }

    @Override
    public Result<Boolean> createProjectGroup(ProjectGroupBo projectGroupBo, String username) {
        BusProjectGroup projectGroup = new BusProjectGroup();

        projectGroup.setGroupDesc(projectGroupBo.getGroupDesc());
        projectGroup.setStatus(true);
        projectGroup.setGroupName(projectGroupBo.getGroupName());
        if (projectGroupBo.isPubGroup()){
            projectGroup.setPubGroup(1);
        }else {
            projectGroup.setPubGroup(0);
        }
        int projectGroupId = busProjectGroupMapper.insert(projectGroup);
        if (projectGroupId == 0) {
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(true);
    }

    @Override
    public Result<Boolean> updateProjectGroup(ProjectGroupBo projectGroupBo) {
        BusProjectGroup projectGroup = new BusProjectGroup();
        projectGroup.setGroupName(projectGroupBo.getGroupName());
        projectGroup.setGroupDesc(projectGroupBo.getGroupDesc());
        projectGroup.setGroupId(projectGroupBo.getGroupID());
        if (projectGroupBo.isPubGroup()){
            projectGroup.setPubGroup(1);
        }else {
            projectGroup.setPubGroup(0);
        }
        if (busProjectGroupMapper.updateByPrimaryKey(projectGroup) > 0) {
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(true);
    }

    @Override
    public List<BusProjectGroup> getAllProjectGroup() {
        BusProjectGroupExample example = new BusProjectGroupExample();
        example.createCriteria().andGroupNameIsNotNull();
        return busProjectGroupMapper.selectByExample(example);
    }

    @Override
    public Result<BusProjectGroup> getProjectGroupById(Integer id) {
        BusProjectGroup projectGroup = busProjectGroupMapper.selectByPrimaryKey(id);
        return Result.success(projectGroup);
    }

    @Override
    public Result<Boolean> deleteProjectGroup(Integer projectGroupId, String userName) {
        busProjectGroupMapper.deleteByPrimaryKey(projectGroupId);
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
