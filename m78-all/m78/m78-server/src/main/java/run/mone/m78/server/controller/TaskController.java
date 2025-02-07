package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.service.bo.task.CustomTaskBO;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.task.TaskService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/task")
@HttpApiModule(value = "TaskController", apiController = TaskController.class)
public class TaskController {

    @Resource
    private TaskService taskService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/createTask")
    @ResponseBody
    @HttpApiDoc(value = "/api/v1/task/create", method = MiApiRequestMethod.POST, apiName = "创建任务")
    public Result<Boolean> createTask(HttpServletRequest request,
                                            @RequestBody CustomTaskBO task) {
        try {
            log.info("createDatasource:{}", task);
            SessionAccount account = loginService.getAccountFromSession(request);
            if (account == null) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
            String username = account.getUsername();
            task.setUserName(username);
            taskService.createTask(task);
            return Result.success(true);
        } catch (Exception e) {
            log.error("createDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/queryTask", method = RequestMethod.POST)
    @HttpApiDoc(value = "/api/v1/task/query", method = MiApiRequestMethod.POST, apiName = "查询任务")
    public Result<List<CustomTaskBO>> queryTask(HttpServletRequest request,
                                                @RequestBody CustomTaskBO task) {
        try {
            log.info("queryDatasource:{}", task);
            SessionAccount account = loginService.getAccountFromSession(request);
            if (account == null) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
            String username = account.getUsername();
            task.setUserName(username);
            List<CustomTaskBO> list = taskService.queryTask(task);
            return Result.success(list);
        } catch (Exception e) {
            log.error("queryDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/updateTask", method = RequestMethod.POST)
    @HttpApiDoc(value = "/api/v1/task/update", method = MiApiRequestMethod.POST, apiName = "更新任务")
    public Result<Boolean> updateTask(HttpServletRequest request,
                                                @RequestBody CustomTaskBO task) {
        try {
            log.info("updateDatasource:{}", task);
            SessionAccount account = loginService.getAccountFromSession(request);
            if (account == null) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
            String username = account.getUsername();
            task.setUserName(username);
            return Result.success(taskService.updateTask(task));
        } catch (Exception e) {
            log.error("updateDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/deleteTask", method = RequestMethod.POST)
    @HttpApiDoc(value = "/api/v1/task/delete", method = MiApiRequestMethod.POST, apiName = "删除任务")
    public Result<Boolean> deleteTask(HttpServletRequest request,
                                      @RequestBody CustomTaskBO task) {
        try {
            log.info("deleteTaskDatasource:{}", task);
            SessionAccount account = loginService.getAccountFromSession(request);
            if (account == null) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
            String username = account.getUsername();
            task.setUserName(username);
            return Result.success(taskService.deleteTask(task));
        } catch (Exception e) {
            log.error("deleteTaskDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/executeTask", method = RequestMethod.POST)
    @HttpApiDoc(value = "/api/v1/task/execute", method = MiApiRequestMethod.POST, apiName = "执行任务")
    public Result<String> executeTask(HttpServletRequest request,
                                                @RequestBody CustomTaskBO task) {
        try {
            log.info("executeTaskDatasource:{}", task);
            SessionAccount account = loginService.getAccountFromSession(request);
            if (account == null) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
            String username = account.getUsername();
            task.setUserName(username);
            return Result.success(taskService.executeTask(task));
        } catch (Exception e) {
            log.error("executeDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/disableTask", method = RequestMethod.POST)
    @HttpApiDoc(value = "/api/v1/task/disable", method = MiApiRequestMethod.POST, apiName = "暂停、恢复任务")
    public Result<Boolean> disableTask(HttpServletRequest request,
                                      @RequestBody CustomTaskBO task) {
        try {
            log.info("disableTaskTaskDatasource:{}", task);
            SessionAccount account = loginService.getAccountFromSession(request);
            if (account == null) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
            String username = account.getUsername();
            task.setUserName(username);
            return Result.success(taskService.disableTask(task));
        } catch (Exception e) {
            log.error("disableDatasource error", e);
            return Result.fail(STATUS_INTERNAL_ERROR, e.getMessage());
        }
    }
}
