package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.bo.ProjectGroupBo;
import com.xiaomi.miapi.pojo.BusProjectGroup;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.bo.ApiEnvBo;
import com.xiaomi.miapi.pojo.ApiEnv;
import com.xiaomi.miapi.bo.Project;
import com.xiaomi.miapi.service.ProjectService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.vo.BusProjectVo;
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
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with project request
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
            return projectService.addProject(project,account.getUsername());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/focusProject", method = RequestMethod.POST)
    public Result<Boolean> focusProject(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[ProjectController.focusProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        boolean ok = projectService.focusProject(projectID, account.getUsername());
        if (ok) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

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

        return projectService.unFocusProject(projectID, account.getUsername());
    }

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

        List<BusProjectVo> busProjectVos = projectService.getFocusProject(account.getUsername());

        return Result.success(busProjectVos);
    }

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

         return projectService.getMyProjects(account.getUsername());
    }

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
        boolean ok = projectService.deleteProject(projectID,account.getUsername());
        if (ok) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getProjectListByProjectGroupId", method = RequestMethod.POST)
    public Result<List<BusProjectVo>> getProjectListByProjectGroupId(HttpServletRequest request,
                                                                     HttpServletResponse response,
                                                                     Integer projectGroupID,
                                                                     Integer orderBy) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getProjectListByProjectGroupId] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return projectService.getProjectListByProjectGroupId(projectGroupID, account.getUsername());
    }

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
        boolean ok = projectService.editProject(project, account.getUsername());
        if (ok) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

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
        return projectService.getProject(projectID, account.getUsername());
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
        List<BusProjectVo> result = projectService.getRecentlyProjectList(account.getUsername());
        return Result.success(result);
    }

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

    @ResponseBody
    @RequestMapping(value = "/getApiNum", method = RequestMethod.POST)
    public Result<Map<String, Object>> getApiNum(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getProject] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (projectID == null || projectID < 0) {
            return Result.fail(CommonError.InvalidParamError);
        }
        Integer apiNum = projectService.getApiNum(projectID);
        Map<String, Object> map = new HashMap<>();
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
            return projectService.createProjectGroup(projectGroupBo,account.getUsername());
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
        return projectService.updateProjectGroup(projectGroupBo);
    }


    @ResponseBody
    @RequestMapping(value = "/getAllProjectGroups", method = RequestMethod.GET)
    public Result<List<BusProjectGroup>> getAllProjectGroups(HttpServletRequest request,
                                                             HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getAllProjectGroups] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return Result.success(projectService.getAllProjectGroup());
    }

    @ResponseBody
    @RequestMapping(value = "/getProjectGroupById", method = RequestMethod.POST)
    public Result<BusProjectGroup> getProjectGroupById(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    Integer projectGroupID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getProjectGroupById] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
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
        return projectService.indexSearch(keyword);
    }

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
        return projectService.addApiEnv(bo, account.getUsername());
    }

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
        return projectService.editApiEnv(bo, account.getUsername());
    }

    @ResponseBody
    @RequestMapping(value = "/deleteApiEnv", method = RequestMethod.POST)
    public Result<Boolean> deleteApiEnv(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Integer envID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.deleteApiEnv] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return projectService.deleteApiEnv(envID, account.getUsername());
    }

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
        return projectService.getApiEnv(envID);
    }


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
        return projectService.getApiEnvList(projectID);
    }

}
