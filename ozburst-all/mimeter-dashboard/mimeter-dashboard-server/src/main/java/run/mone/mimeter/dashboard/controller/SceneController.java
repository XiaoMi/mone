package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.SceneParamData;
import run.mone.mimeter.dashboard.bo.scene.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.scenegroup.GetSceneGroupListReq;
import run.mone.mimeter.dashboard.bo.scenegroup.SceneGroupList;
import run.mone.mimeter.dashboard.common.SceneSource;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.SceneService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/bench/scene")
@HttpApiModule(value = "SceneController", apiController = SceneController.class)
public class SceneController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SceneService sceneService;

    @HttpApiDoc(value = "/api/bench/scene/newScene", apiName = "新增压测场景", method = MiApiRequestMethod.POST, description = "新增压测场景")
    @RequestMapping(value = "/newScene", method = RequestMethod.POST)
    public Result<SceneDTO> newScene(
            HttpServletRequest request,
            @RequestBody CreateSceneDTO createSceneReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.newScene] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        createSceneReq.setSceneSource(SceneSource.CONSOLE.code);
        createSceneReq.setTenant(account.getTenant());
        try {
            return sceneService.newScene(createSceneReq, account.getUsername());
        } catch (Exception e) {
            return Result.fail(CommonError.InvalidParamError);
        }
    }

    @HttpApiDoc(value = "/api/bench/scene/deleteScene", apiName = "删除压测场景", method = MiApiRequestMethod.POST, description = "根据ID删除压测场景")
    @RequestMapping(value = "/deleteScene", method = RequestMethod.POST)
    public Result<Boolean> deleteScene(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "sceneID", required = true, description = "场景ID", defaultValue = "66")
                    Integer sceneID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.newScene] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        //todo 租户权限校验

        try {
            return sceneService.delScene(sceneID, account.getUsername());
        } catch (Exception e) {
            log.warn("[SceneController.newScene] error :{}",e.getMessage());
            return Result.fail(CommonError.InvalidParamError);
        }
    }

    @HttpApiDoc(value = "/api/bench/scene/editScene", apiName = "编辑压测场景", method = MiApiRequestMethod.POST, description = "编辑压测场景")
    @RequestMapping(value = "/editScene", method = RequestMethod.POST)
    public Result<Boolean> editScene(
            HttpServletRequest request,
            @RequestBody EditSceneDTO editSceneReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.editScene] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        editSceneReq.setTenant(account.getTenant());
        editSceneReq.setSceneSource(SceneSource.CONSOLE.code);
        try {
            return sceneService.editScene(editSceneReq, account.getUsername());
        } catch (Exception e) {
            log.warn("[SceneController.editScene] error :{}",e.getMessage());
            return Result.fail(CommonError.InvalidParamError);
        }
    }

    @HttpApiDoc(value = "/api/bench/scene/getSceneByID", apiName = "获取压测场景详情", method = MiApiRequestMethod.GET, description = "根据ID获取压测场景")
    @RequestMapping(value = "/getSceneByID", method = RequestMethod.GET)
    public Result<SceneDTO> getSceneByID(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "sceneID", required = true, description = "场景ID", defaultValue = "66")
                    Integer sceneID) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.newScene] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        try {
            return sceneService.getSceneByID(sceneID,false);
        } catch (Exception e) {
            log.warn("[SceneController.getSceneByID] error :{}",e);
            return Result.fail(CommonError.InvalidParamError);
        }
    }

    @HttpApiDoc(value = "/api/bench/scene/getSceneList", apiName = "获取压测场景列表", method = MiApiRequestMethod.POST, description = "获取压测场景列表")
    @RequestMapping(value = "/getSceneList", method = RequestMethod.POST)
    public Result<SceneList> getSceneList(GetSceneListReq req,HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.getSceneList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        req.setTenant(account.getTenant());
        return sceneService.getSceneList(req);
    }

    @HttpApiDoc(value = "/api/bench/scene/getSceneListByIds", apiName = "根据id列表获取压测场景列表", method = MiApiRequestMethod.POST, description = "根据id列表获取压测场景列表")
    @RequestMapping(value = "/getSceneListByIds", method = RequestMethod.POST)
    public Result<List<SceneDTO>> getSceneListByIds(@RequestBody GetSceneListByIdsReq req,HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.getSceneListByIds] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return sceneService.getSceneListByIds(req);
    }

    @HttpApiDoc(value = "/api/bench/scene/getSceneListByGroup", apiName = "获取压测场景分组列表", method = MiApiRequestMethod.POST, description = "获取压测场景分组列表")
    @RequestMapping(value = "/getSceneListByGroup", method = RequestMethod.POST)
    public Result<SceneGroupList> getSceneListByGroup(GetSceneGroupListReq req, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.getSceneListByGroup] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        req.setTenant(account.getTenant());
        return sceneService.getSceneListByGroup(req);
    }

    @HttpApiDoc(value = "/api/bench/scene/searchSceneByKeyword", apiName = "按关键字搜索场景", method = MiApiRequestMethod.POST, description = "按关键字搜索场景")
    @RequestMapping(value = "/searchSceneByKeyword", method = RequestMethod.POST)
    public Result<SceneList> searchSceneByKeyword(GetSceneListReq req,HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.newScene] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        req.setTenant(account.getTenant());
        try {
            return sceneService.getSceneListByKeyword(req);
        } catch (Exception e) {
            log.warn("[SceneController.searchSceneByKeyword] error :{}",e.getMessage());
            return Result.fail(CommonError.InvalidParamError);        }
    }

    @HttpApiDoc(value = "/api/bench/scene/getSceneParamData", apiName = "获取压测场景参数数据", method = MiApiRequestMethod.GET, description = "获取压测场景参数数据，目前支持全局参数")
    @RequestMapping(value = "/getSceneParamData", method = RequestMethod.POST)
    public Result<SceneParamData> getSceneDataset(
            @HttpApiDocClassDefine(value = "场景id",required = true,description = "所属场景id",defaultValue = "66")
            Integer sceneId, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneController.getSceneParamData] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return sceneService.getSceneParamData(sceneId);
    }

}
