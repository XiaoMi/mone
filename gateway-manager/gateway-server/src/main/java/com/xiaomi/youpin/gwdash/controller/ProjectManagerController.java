//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.mone.miflow.api.model.vo.projectmanager.*;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.ProjectManagerService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author wmin
// * @date 2021/9/17 3:46 下午
// * 项目管理（任务、需求
// */
//@RestController
//@Slf4j
//public class ProjectManagerController {
//
//    @Autowired
//    ProjectManagerService projectManagerService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @RequestMapping(value = "/api/pm/orgId/new", method = RequestMethod.POST)
//    public Result<Boolean> createOrgId(
//            HttpServletRequest request,
//            @RequestParam("orgId") String orgId,
//            @RequestParam("orgName") String orgName,
//            @RequestParam(value = "desc", defaultValue = "", required = false) String desc
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return projectManagerService.createOrgId(sessionAccount.getUsername(), orgId, orgName, desc);
//    }
//
//    @RequestMapping(value = "/api/pm/orgId/edit", method = RequestMethod.POST)
//    public Result<Boolean> editOrgId(
//            HttpServletRequest request,
//            @RequestParam("id") long id,
//            @RequestParam("orgId") String orgId,
//            @RequestParam("orgName") String orgName,
//            @RequestParam(value = "desc", defaultValue = "", required = false) String desc
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return projectManagerService.editOrgId(sessionAccount.getUsername(), id, orgId, orgName, desc);
//    }
//
//    @RequestMapping(value = "/api/pm/orgId/del", method = RequestMethod.DELETE)
//    public Result<Boolean> delOrgId(
//            HttpServletRequest request,
//            @RequestParam("id") long id
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return projectManagerService.delOrgId(sessionAccount.getUsername(), id);
//    }
//
//    @RequestMapping(value = "/api/pm/orgId/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getOrgId(
//            HttpServletRequest request,
//            @RequestParam(value = "orgName", required = false) String orgName,
//            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
//            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return projectManagerService.getOrgId(sessionAccount.getUsername(), orgName, page, pageSize);
//    }
//
//
//    /**
//     * 查询用户参与的项目
//     * @param request
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/project/qry", method = RequestMethod.POST)
//    public Result<ProjectListResult> qryProjects(
//            HttpServletRequest request,
//            @RequestBody ProjectListParam param
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return projectManagerService.qryProjects(sessionAccount.getUsername(), param);
//    }
//
//    /**
//     * 查询某项目下任务列表
//     * @param request
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/task/qry", method = RequestMethod.POST)
//    public Result<TaskListResult> qryTasksByProjectId(
//            HttpServletRequest request,
//            @RequestBody TaskListParam param
//    ){
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        if (StringUtils.isBlank(param.getProjectId())){
//            new Result<>(CommonError.InvalidParamError.getCode(), "projectId is null", null);
//        }
//        if (param.getPageSize()<=0){
//            param.setPageSize(10);
//        }
//        return projectManagerService.qryTasksByProjectId(sessionAccount.getUsername(), param);
//    }
//
//    /**
//     * 查询子任务列表
//     * @param request
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/subTask/qry", method = RequestMethod.POST)
//    public Result<TaskListResult> qryByParentTaskId(
//            HttpServletRequest request,
//            @RequestBody TaskListParam param
//    ){
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        if (StringUtils.isBlank(param.getParentTaskId())){
//            new Result<>(CommonError.InvalidParamError.getCode(), "parentTaskId is null", null);
//        }
//        return projectManagerService.qryByParentTaskId(sessionAccount.getUsername(), param);
//    }
//
//    /**
//     * 查询任务状态集
//     * @param request
//     * @param projectId
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/task/status/qry", method = RequestMethod.GET)
//    public Result<List<TaskStatusBo>> qryTaskStatus(
//            HttpServletRequest request,
//            @RequestParam("orgId") String orgId,
//            @RequestParam("projectId") String projectId
//    ){
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return projectManagerService.qryTaskStatus(orgId, projectId);
//    }
//
//    /**
//     * 查询任务详情
//     * @param request
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/task/detail", method = RequestMethod.POST)
//    public Result<TaskListResult> qryByTaskId(
//            HttpServletRequest request,
//            @RequestBody TaskListParam param
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        if (StringUtils.isBlank(param.getTaskId())){
//            new Result<>(CommonError.InvalidParamError.getCode(), "taskId is null", null);
//        }
//        return projectManagerService.qryByTaskId(sessionAccount.getUsername(), param);
//    }
//
//    /**
//     * 更新任务
//     * @param request
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/task/update", method = RequestMethod.POST)
//    public Result<TaskBo> updateTask(
//            HttpServletRequest request,
//            @RequestBody TaskBo param
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        if (StringUtils.isBlank(param.getTaskId())){
//            new Result<>(CommonError.InvalidParamError.getCode(), "taskId is null", null);
//        }
//        return projectManagerService.updateTask(sessionAccount.getUsername(), param);
//    }
//
//    /**
//     * 关联
//     * @param request
//     * @param param
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/task/relEnv", method = RequestMethod.POST)
//    public Result<Boolean> relTask(
//            HttpServletRequest request,
//            @RequestBody TaskEnvBo param
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return projectManagerService.relTask(sessionAccount.getUsername(), param);
//    }
//
//    /**
//     * 取消关联
//     * @param request
//     * @param taskId
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/task/unRelEnv", method = RequestMethod.GET)
//    public Result<Boolean> relTaskDel(
//            HttpServletRequest request,
//            @RequestParam("taskId") String taskId
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return projectManagerService.relTaskDel(sessionAccount.getUsername(), taskId);
//    }
//
//    /**
//     * 查询关联关系
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/api/pm/task/relEnv", method = RequestMethod.GET)
//    public Result<TaskEnvBo> relTaskQry(
//            HttpServletRequest request,
//            @RequestParam("taskId") String taskId
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession();
//        return projectManagerService.relTaskQry(sessionAccount.getUsername(), taskId);
//    }
//
//}
