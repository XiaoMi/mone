package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.SceneSnapshotBo;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.SceneService;
import run.mone.mimeter.dashboard.service.SceneSnapshotService;
import run.mone.mimeter.dashboard.service.impl.LoginService;
import run.mone.mimeter.dashboard.common.ApiConsts;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/23
 */
@Slf4j
@RestController
@RequestMapping(ApiConsts.API_PREFIX + ApiConsts.SCENE_SNAPSHOT_ROUTE)
@HttpApiModule(value = "SceneSnapshotController", apiController = SceneSnapshotController.class)
public class SceneSnapshotController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SceneSnapshotService sceneSnapshotService;

    @Autowired
    private SceneService sceneService;

    private final String logPrefix = "[SceneSnapshotController]";

    @HttpApiDoc(value = ApiConsts.API_PREFIX + ApiConsts.SCENE_SNAPSHOT_ROUTE + ApiConsts.GENERAL_DETAILS_ENDPOINT, apiName = "获取压测场景快照",
            method = MiApiRequestMethod.GET, description = "获取压测场景快照")
    @GetMapping(ApiConsts.GENERAL_DETAILS_ENDPOINT)
    public Result<SceneSnapshotBo> snapshotDetails(HttpServletRequest request,
                                                   @HttpApiDocClassDefine(value = "snapshotId", required = true)
                                                   @RequestParam String snapshotId) {
        SessionAccount account = this.loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn(this.logPrefix + "snapshotDetails current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return this.sceneSnapshotService.getSceneSnapshotById(snapshotId);
    }

    @GetMapping("/scene")
    public Result<SceneSnapshotBo> getByScene(HttpServletRequest request,
                                                   @RequestParam Long sceneId) {
        SessionAccount account = this.loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn(this.logPrefix + "getByScene current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return this.sceneSnapshotService.getSceneSnapshotByScene(sceneId);
    }
}
