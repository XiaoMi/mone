package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scenegroup.SceneGroupDTO;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.SceneGroupService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/bench/scene")
@HttpApiModule(value = "SceneGroupController", apiController = SceneGroupController.class)
public class SceneGroupController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SceneGroupService sceneGroupService;

    @HttpApiDoc(value = "/api/bench/scene/newSceneGroup", apiName = "新增场景分组", method = MiApiRequestMethod.POST, description = "新增场景分组")
    @RequestMapping(value = "/newSceneGroup", method = RequestMethod.POST)
    public Result<Integer> newSceneGroup(
            HttpServletRequest request,
            @RequestBody SceneGroupDTO sceneGroupReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.newSceneGroup] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        sceneGroupReq.setTenant(account.getTenant());
        return sceneGroupService.newSceneGroup(sceneGroupReq, account.getUsername());
    }

    @HttpApiDoc(value = "/api/bench/scene/deleteSceneGroup", apiName = "删除压测场景分组", method = MiApiRequestMethod.POST, description = "根据ID删除压测场景")
    @RequestMapping(value = "/deleteSceneGroup", method = RequestMethod.POST)
    public Result<Boolean> deleteSceneGroup(
            HttpServletRequest request ,@RequestBody SceneGroupDTO sceneGroupReq){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.deleteSceneGroup] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        return sceneGroupService.delSceneGroup(sceneGroupReq, account.getUsername());
    }

    @HttpApiDoc(value = "/api/bench/scene/editSceneGroup", apiName = "编辑压测场景分组信息", method = MiApiRequestMethod.POST, description = "编辑压测场景")
    @RequestMapping(value = "/editSceneGroup", method = RequestMethod.POST)
    public Result<Boolean> editSceneGroup(
            HttpServletRequest request,
            @RequestBody SceneGroupDTO sceneGroupReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.editSceneGroup] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return sceneGroupService.editSceneGroup(sceneGroupReq, account.getUsername());
    }

}
