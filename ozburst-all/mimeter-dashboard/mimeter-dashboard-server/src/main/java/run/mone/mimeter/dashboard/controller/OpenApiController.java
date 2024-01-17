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
import run.mone.mimeter.dashboard.bo.scene.CreateSceneDTO;
import run.mone.mimeter.dashboard.bo.scene.SceneDTO;
import run.mone.mimeter.dashboard.common.SceneSource;
import run.mone.mimeter.dashboard.service.SceneService;

@Slf4j
@RestController
@RequestMapping("/api/bench/agent/openapi")
@HttpApiModule(value = "OpenApiController", apiController = OpenApiController.class)
public class OpenApiController {

    @Autowired
    private SceneService sceneService;

    @HttpApiDoc(value = "/api/bench/agent/openapi/createScene", apiName = "批量绑定压测机域名", method = MiApiRequestMethod.POST, description = "创建场景的openapi")
    @RequestMapping(value = "/createScene", method = RequestMethod.POST)
    public Result<SceneDTO> createScene(
            @RequestBody CreateSceneDTO createSceneReq) {
        createSceneReq.setSceneSource(SceneSource.OPEN_API.code);
        createSceneReq.setTenant("open_api");
        return sceneService.newScene(createSceneReq, "open_api");
    }
}
