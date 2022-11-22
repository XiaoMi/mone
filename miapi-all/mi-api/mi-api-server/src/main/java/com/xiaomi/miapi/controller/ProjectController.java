package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.common.bo.ApiEnvBo;
import com.xiaomi.miapi.common.bo.ProjectGroupBo;
import com.xiaomi.miapi.common.pojo.ApiEnv;
import com.xiaomi.miapi.common.pojo.Project;
import com.xiaomi.miapi.service.ProjectService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.vo.BusProjectVo;
import com.xiaomi.youpin.hermes.entity.ProjectGroup;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目控制器
 */
@Controller
@RequestMapping("/Project")
public class ProjectController {
    @Resource
    private ProjectService projectService;

    @Autowired
    private LoginService loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @DubboReference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    /**
     * 新建项目
     *
     * @param request
     * @param project
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addProject", method = RequestMethod.POST)
    public Result<Boolean> addProject(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Project project) throws IOException {
        if (project.getProjectName() == null || project.getProjectName().length() < 1 || project.getProjectName().length() > 32) {
            return Result.fail(CommonError.InvalidParamError);
        } else {
            SessionAccount account = loginService.getAccountFromSession(request);

            if (null == account) {
                LOGGER.warn("[AccountController.addProject] current user not have valid account info in session");
                response.sendError(401, "未登录或者无权限");
                return null;
            }
            if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
                LOGGER.warn("[ProjectController.addProject] not authorized to create project");
                return Result.fail(CommonError.UnAuthorized);
            }

            return projectService.addProject(project, account.getId().intValue(), account.getUsername());
        }
    }

    /**
     * 关注项目
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/focusProject", method = RequestMethod.POST)
    public Result<Boolean> focusProject(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Integer projectID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ProjectController.focusProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.addProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        boolean ok = projectService.focusProject(projectID, account.getId().intValue());
        if (ok) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * 关注项目
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/unFocusProject", method = RequestMethod.POST)
    public Result<Boolean> unFocusProject(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Integer projectID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ProjectController.unFocusProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.addProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return projectService.unFocusProject(projectID, account.getId().intValue());
    }

    /**
     * 关注项目
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getFocusProjects", method = RequestMethod.GET)
    public Result<List<BusProjectVo>> getFocusProjects(HttpServletRequest request,
                                                       HttpServletResponse response
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ProjectController.getFocusProjects] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        List<BusProjectVo> busProjectVos = projectService.getFocusProject(account.getId().intValue());

        return Result.success(busProjectVos);
    }

    /**
     *
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getMyProjects", method = RequestMethod.GET)
    public Result<Map<String,Object>> getMyProjects(HttpServletRequest request,
                                                       HttpServletResponse response
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ProjectController.getMyProjects] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

         return projectService.getMyProjects(account.getId().intValue());
    }

    /**
     * 删除项目
     *
     * @param request
     * @param projectID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteProject", method = RequestMethod.POST)
    public Result<Boolean> deleteProject(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.deleteProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ProjectController.deleteProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        boolean ok = projectService.deleteProject(projectID, account.getId().intValue(), account.getUsername());
        if (ok) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * 根据项目组id获取项目列表
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getProjectListByProjectGroupId", method = RequestMethod.POST)
    public Result<List<BusProjectVo>> getProjectListByProjectGroupId(HttpServletRequest request,
                                                                     HttpServletResponse response,
                                                                     Integer projectGroupID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getProjectListByProjectGroupId] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return projectService.getProjectListByProjectGroupId(projectGroupID, account.getId().intValue(), account.getUsername());
    }

    /**
     * 修改项目
     *
     * @param request
     * @param project
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editProject", method = RequestMethod.POST)
    public Result<Boolean> editProject(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Project project) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.editProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, project.getProjectID(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        boolean ok = projectService.editProject(project, account.getUsername());
        if (ok) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * 获取项目信息
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getProject", method = RequestMethod.POST)
    public Result<Map<String, Object>> getProject(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  Integer projectID) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        }
        return projectService.getProject(projectID, account.getId().intValue());
    }

    @ResponseBody
    @RequestMapping(value = "/getRecentlyProjectList", method = RequestMethod.GET)
    public Result<List<BusProjectVo>> getRecentlyProjectList(HttpServletRequest request,
                                                             HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getRecentlyApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<BusProjectVo> result = projectService.getRecentlyProjectList(account.getId().intValue());
        return Result.success(result);
    }

    /**
     * 获取项目日志列表
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getProjectLogList", method = RequestMethod.POST)
    public Result<Map<String, Object>> getProjectLogList(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         Integer projectID, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getProjectLogList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<Map<String, Object>> result = projectService.getProjectLogList(projectID, page, pageSize);
        int logCount = projectService.getProjectLogCount(projectID, 7);
        Map<String, Object> map = new HashMap<String, Object>();
        if (result != null) {
            map.put("statusCode", "000000");
            map.put("logCount", logCount);
            map.put("logList", result);
            return Result.success(map);
        } else {
            map.put("statusCode", "130000");
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * 获取接口数量
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getApiNum", method = RequestMethod.POST)
    public Result<Map<String, Object>> getApiNum(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (projectID == null || projectID < 0) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (null == account) {
            LOGGER.warn("[AccountController.getProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Integer apiNum = projectService.getApiNum(projectID);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("num", apiNum);
        return Result.success(map);
    }

    @ResponseBody
    @RequestMapping(value = "/addProjectGroup", method = RequestMethod.POST)
    public Result<Boolean> addProjectGroup(HttpServletRequest request,
                                           HttpServletResponse response,
                                           ProjectGroupBo projectGroupBo) throws IOException {
        if (projectGroupBo.getGroupName() == null || projectGroupBo.getGroupName().length() < 1 || projectGroupBo.getGroupName().length() > 32) {
            return Result.fail(CommonError.InvalidParamError);
        } else {
            SessionAccount account = loginService.getAccountFromSession(request);

            if (null == account) {
                LOGGER.warn("[ProjectController.addProjectGroup] current user not have valid account info in session");
                response.sendError(401, "未登录或者无权限");
                return null;
            }
            if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
                LOGGER.warn("[ProjectController.addProjectGroup] not authorized to create project");
                return Result.fail(CommonError.UnAuthorized);
            }
            return projectService.createProjectGroup(projectGroupBo,account.getId().intValue());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/editProjectGroup", method = RequestMethod.POST)
    public Result<Boolean> editProjectGroup(HttpServletRequest request,
                                            HttpServletResponse response,
                                            ProjectGroupBo projectGroupBo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.editProjectGroup] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editProjectGroup] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return projectService.updateProjectGroup(projectGroupBo);
    }


    @ResponseBody
    @RequestMapping(value = "/getAllProjectGroups", method = RequestMethod.GET)
    public Result<List<ProjectGroup>> getAllProjectGroups(HttpServletRequest request,
                                                          HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getAllProjectGroups] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getAllProjectGroups] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        return Result.success(projectService.getAllAccessableProjectGroup(account.getId().intValue()));
    }

    @ResponseBody
    @RequestMapping(value = "/getProjectGroupById", method = RequestMethod.POST)
    public Result<ProjectGroup> getProjectGroupById(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    Integer projectGroupID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getProjectGroupById] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getProjectGroupById] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return projectService.getProjectGroupById(projectGroupID);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteProjectGroupById", method = RequestMethod.POST)
    public Result<Boolean> deleteProjectGroupById(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  Integer projectGroupID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.deleteProjectGroupById] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ProjectController.getProjectList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return projectService.deleteProjectGroup(projectGroupID, account.getUsername());
    }

    @ResponseBody
    @RequestMapping(value = "/indexSearch", method = RequestMethod.POST)
    public Result<Map<String, List<Map<String, Object>>>> indexSearch(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      String keyword) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.indexSearch] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.indexSearch] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return projectService.indexSearch(keyword);
    }

    /**
     * 添加API环境
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addApiEnv", method = RequestMethod.POST)
    public Result<Boolean> addApiEnv(HttpServletRequest request,
                                     HttpServletResponse response,
                                     ApiEnvBo bo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.addApiEnv] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.addApiEnv] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, bo.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        }
        return projectService.addApiEnv(bo, account.getUsername());
    }

    /**
     * 编辑API环境
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editApiEnv", method = RequestMethod.POST)
    public Result<Boolean> editApiEnv(HttpServletRequest request,
                                     HttpServletResponse response,
                                     ApiEnvBo bo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.editApiEnv] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editApiEnv] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, bo.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        }
        return projectService.editApiEnv(bo, account.getUsername());
    }

    /**
     * 删除API环境
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteApiEnv", method = RequestMethod.POST)
    public Result<Boolean> deleteApiEnv(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Integer envID,Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.deleteApiEnv] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.deleteApiEnv] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        }
        return projectService.deleteApiEnv(envID, account.getUsername());
    }

    /**
     * 获取环境详情
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getApiEnvById", method = RequestMethod.POST)
    public Result<ApiEnv> getApiEnvById(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Integer envID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getApiEnvById] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getApiEnvById] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return projectService.getApiEnv(envID);
    }


    /**
     * 获取项目下环境列表
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getApiEnvListByProjectId", method = RequestMethod.POST)
    public Result<List<ApiEnv>> getApiEnvListByProjectId(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getApiEnvListByProjectId] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getApiEnvListByProjectId] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return projectService.getApiEnvList(projectID);
    }

}
