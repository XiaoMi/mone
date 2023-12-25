package run.mone.mimeter.dashboard.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.mimeter.dashboard.bo.DubboService;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.DefaultSceneInfo;
import run.mone.mimeter.dashboard.bo.sceneapi.GetApiDetailReq;
import run.mone.mimeter.dashboard.bo.sceneapi.GetDubboServiceReq;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.SceneApiService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/bench/sceneApi")
@HttpApiModule(value = "SceneApiController", apiController = SceneApiController.class)
public class SceneApiController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SceneApiService sceneApiService;

    @HttpApiDoc(value = "/api/bench/sceneApi/searchApiFromMiApi", apiName = "模糊搜索mi-api中的接口", method = MiApiRequestMethod.POST, description = "根据keyword模糊搜索mi-api中的接口")
    @RequestMapping(value = "/searchApiFromMiApi", method = RequestMethod.POST)
    public Result<Object> searchApiFromMiApi(
            HttpServletRequest request,
            @HttpApiDocClassDefine(value = "keyword", required = true, description = "关键字", defaultValue = "getUser")
            String keyword,
            @HttpApiDocClassDefine(value = "apiProtocol", required = true, description = "接口协议类型 1:http 3:dubbo", defaultValue = "3")
            Integer apiProtocol
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneApiController.searchApiFromMiApi] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        if (Objects.isNull(keyword)) {
            keyword = "";
        }
        return sceneApiService.searchApiFromMiApi(keyword, apiProtocol);
    }

    @HttpApiDoc(value = "/api/bench/sceneApi/getSceneBasicInfoFromApiID", apiName = "通过mi-api接口id获取场景基本默认数据", method = MiApiRequestMethod.POST, description = "通过mi-api接口id获取场景基本默认数据")
    @RequestMapping(value = "/getSceneBasicInfoFromApiID", method = RequestMethod.POST)
    public Result<DefaultSceneInfo> getSceneBasicInfoFromApiID(
            HttpServletRequest request,
            GetApiDetailReq getApiDetailReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneApiController.getSceneBasicInfoFromApiID] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return sceneApiService.getSceneBasicInfoFromApiID(getApiDetailReq);
    }

    @HttpApiDoc(value = "/api/bench/sceneApi/getApiDetailFromMiApi", apiName = "获取mi-api中的接口详情", method = MiApiRequestMethod.POST, description = "根据id获取mi-api中的接口详情")
    @RequestMapping(value = "/getApiDetailFromMiApi", method = RequestMethod.POST)
    public Result<Object> getApiDetailFromMiApi(
            HttpServletRequest request,
            GetApiDetailReq getApiDetailReq
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SceneApiController.getApiDetailFromMiApi] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return sceneApiService.getApiDetailFromMiApi(getApiDetailReq);
    }

    /**
     * 根据服务名大致搜索nacos上的服务列表可搜dubbo服务
     *
     * @return
     * @throws IOException
     */
    @HttpApiDoc(value = "/api/bench/sceneApi/loadDubboApiServices", apiName = "搜索dubbo接口", method = MiApiRequestMethod.POST, description = "根据服务名大致搜索nacos上的服务列表可搜dubbo服务")
    @ResponseBody
    @RequestMapping(value = "/loadDubboApiServices", method = RequestMethod.POST)
    public Result<List<DubboService>> loadDubboApiServices(GetDubboServiceReq req) throws IOException {
        return sceneApiService.loadDubboApiServices(req.getServiceName(), req.getEnv());
    }

    /**
     * 根据服务名获取方法列表
     *
     * @return
     * @throws IOException
     */
    @HttpApiDoc(value = "/api/bench/sceneApi/getServiceMethod", apiName = "根据服务名获取方法列表", method = MiApiRequestMethod.POST, description = "根据服务名获取方法列表")
    @ResponseBody
    @RequestMapping(value = "/getServiceMethod", method = RequestMethod.POST)
    public Result<List<String>> getServiceMethod(GetDubboServiceReq req) throws IOException, NacosException {
        return sceneApiService.getServiceMethod(req.getServiceName(), req.getEnv());
    }

    @HttpApiDoc(value = "/api/bench/sceneApi/getUrlById", apiName = "根据apiId获取url",
            method = MiApiRequestMethod.POST, description = "根据apiId获取url")
    @RequestMapping(value = "/getUrlById", method = RequestMethod.POST)
    public Result<String> getUrlById(HttpServletRequest request, Integer apiId) {
        SessionAccount account = this.loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("getUrlById current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        return sceneApiService.getApiUrlById(apiId);
    }

}
