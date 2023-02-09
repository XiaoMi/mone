package com.xiaomi.miapi.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.bo.*;
import com.xiaomi.miapi.dto.ManualDubboUpDTO;
import com.xiaomi.miapi.dto.ManualGatewayUpDTO;
import com.xiaomi.miapi.dto.ManualHttpUpDTO;
import com.xiaomi.miapi.dto.ManualSidecarUpDTO;
import com.xiaomi.miapi.service.*;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.pojo.Api;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with api request
 */
@Controller
@RequestMapping("/Api")
public class ApiController {
    @Autowired
    private ApiService apiService;

    @Autowired
    private DubboApiService dubboApiService;

    @Autowired
    private HttpApiService httpApiService;

    @Autowired
    private SidecarApiService sidecarApiService;
    @Autowired
    private GatewayApiService gatewayApiService;

    @Autowired
    private GrpcApiService grpcApiService;

    @Autowired
    private MockService mockService;
    @Autowired
    private LoginService loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    /**
     * add http api
     */
    @ResponseBody
    @RequestMapping(value = "/addHttpApi", method = RequestMethod.POST)
    public Result<Boolean> addHttpApi(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Api api,
                                      @RequestParam(value = "apiHeader", required = false) String apiHeader,
                                      @RequestParam(value = "apiRequestParam", required = false) String apiRequestParam,
                                      @RequestParam(value = "apiResultParam", required = false) String apiResultParam,
                                      @RequestParam(value = "apiErrorCodes", required = false) String apiErrorCodes
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.addHttpApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        api.setUpdateUsername(account.getUsername());
        return httpApiService.addHttpApi(api, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes, false);
    }

    /**
     * batch add sidecar api
     */
    @ResponseBody
    @RequestMapping(value = "/batchAddSidecarApi", method = RequestMethod.POST)
    public Result<Boolean> batchAddSidecarApi(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody BatchAddApiBo httpApiBo
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.batchAddSidecarApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (httpApiBo.getBos().isEmpty()) {
            return Result.fail(CommonError.InvalidParamError);
        }
        httpApiBo.getBos().forEach(bo -> bo.setUpdateUserName(account.getUsername()));
        return sidecarApiService.batchAddSidecarApi(httpApiBo.getApiEnv(), httpApiBo.getBos());
    }

    /**
     * batch add dubbo api
     */
    @ResponseBody
    @RequestMapping(value = "/batchAddHttpApi", method = RequestMethod.POST)
    public Result<Boolean> batchAddHttpApi(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestBody BatchAddApiBo httpApiBo
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.batchAddHttpApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (httpApiBo.getBos().isEmpty()) {
            return Result.fail(CommonError.InvalidParamError);
        }
        httpApiBo.getBos().forEach(bo -> bo.setUpdateUserName(account.getUsername()));
        return httpApiService.batchAddHttpApi(httpApiBo.getApiEnv(), httpApiBo.getBos());
    }

    /**
     * update http api
     */
    @ResponseBody
    @RequestMapping(value = "/editHttpApi", method = RequestMethod.POST)
    public Result<Boolean> editHttpApi(HttpServletRequest request, Api api,
                                       HttpServletResponse response,
                                       @RequestParam(value = "apiHeader", required = false) String apiHeader,
                                       @RequestParam(value = "apiRequestParam", required = false) String apiRequestParam,
                                       @RequestParam(value = "apiResultParam", required = false) String apiResultParam,
                                       @RequestParam(value = "apiErrorCodes", required = false) String apiErrorCodes
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.editHttpApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        api.setUpdateUsername(account.getUsername());
        return httpApiService.editHttpApi(api, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes, true);
    }

    /**
     * update sidecar 接口
     *
     */
    @ResponseBody
    @RequestMapping(value = "/editSidecarApi", method = RequestMethod.POST)
    public Result<Boolean> editSidecarApi(HttpServletRequest request,
                                          HttpServletResponse response, Api api,
                                          @RequestParam(value = "apiRequestParam", required = false) String apiRequestParam,
                                          @RequestParam(value = "apiResultParam", required = false) String apiResultParam
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.editSidecarApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        api.setUpdateUsername(account.getUsername());
        return sidecarApiService.editSidecarApi(api, apiRequestParam, apiResultParam, true);
    }

    @ResponseBody
    @RequestMapping(value = "/editApiStatus", method = RequestMethod.POST)
    public Result<Boolean> editApiStatus(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Integer projectID,
                                         Integer apiID,
                                         Integer status
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.editApiStatus] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiService.editApiStatus(projectID, apiID, status);
    }


    /**
     * search service list by service name from local db,supports dubbo&http
     */
    @ResponseBody
    @RequestMapping(value = "/loadDubboApiServices", method = RequestMethod.POST)
    public Result<Set<ServiceName>> loadDubboApiServices(HttpServletRequest request,
                                                         HttpServletResponse response, String serviceName) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.loadDubboApiServices] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return dubboApiService.loadApiServices(serviceName);
    }

    /**
     * search dubbo service list by service name from nacos
     */
    @ResponseBody
    @RequestMapping(value = "/loadDubboApiServicesFromNacos", method = RequestMethod.POST)
    public Result<List<ServiceName>> loadDubboApiServicesFromNacos(HttpServletRequest request,
                                                                   HttpServletResponse response, String namespace, String serviceName, String env) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.loadDubboApiServicesFromNacos] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return dubboApiService.loadDubboApiServicesFromNacos(serviceName, env);
    }

    /**
     * search grpc service list by service name
     */
    @ResponseBody
    @RequestMapping(value = "/loadGrpcService", method = RequestMethod.POST)
    public Result<Set<ServiceName>> loadGrpcService(HttpServletRequest request,
                                                    HttpServletResponse response, String serviceName) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.loadGrpcService] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (Objects.isNull(serviceName)) {
            serviceName = "";
        }
        serviceName = "grpc:" + serviceName;
        Set<ServiceName> services = dubboApiService.loadApiServices(serviceName).getData();
        services = services.stream().filter(service -> service.getName().startsWith("grpc:")).collect(Collectors.toSet());
        return Result.success(services);
    }

    /**
     * load grpc api info
     */
    @ResponseBody
    @RequestMapping(value = "/loadGrpcApiInfos", method = RequestMethod.POST)
    public Result<GrpcApiInfosBo> loadGrpcApiInfos(HttpServletRequest request,
                                                   HttpServletResponse response, String appName) throws Exception {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.loadGrpcApiInfos] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return grpcApiService.loadGrpcApiInfos(appName);
    }

    @ResponseBody
    @RequestMapping(value = "/loadGrpcServerAddr", method = RequestMethod.POST)
    public Result<String> loadGrpcServerAddr(HttpServletRequest request,
                                             HttpServletResponse response, String appName) throws Exception {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.loadGrpcServerAddr] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return grpcApiService.loadGrpcServerAddr(appName);
    }

    /**
     * batch add grpc apis
     */
    @ResponseBody
    @RequestMapping(value = "/batchAddGrpcApi", method = RequestMethod.POST)
    public Result<Boolean> batchAddGrpcApi(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestBody BatchAddGrpcApiBo grpcApiBo
    ) throws Exception {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.batchAddGrpcApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        grpcApiBo.setUpdateUserName(account.getUsername());
        return grpcApiService.batchAddGrpcApi(grpcApiBo);
    }

    /**
     * update grpc api
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateGrpcApi", method = RequestMethod.POST)
    public Result<Boolean> updateGrpcApi(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestBody UpdateGrpcApiBo updateGrpcApiBo
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.updateGrpcApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        updateGrpcApiBo.setUpdateUserName(account.getUsername());
        return grpcApiService.updateGrpcApi(updateGrpcApiBo);
    }

    /**
     * get grpc api info
     */
    @ResponseBody
    @RequestMapping(value = "/getGrpcApiDetail", method = RequestMethod.POST)
    public Result<Map<String, Object>> getGrpcApiDetail(HttpServletRequest request,
                                                        HttpServletResponse response, Integer projectID,
                                                        Integer apiID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getGrpcApiDetail] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return grpcApiService.getGrpcApiDetail(account.getUsername(), projectID, apiID);
    }

    /**
     * load http api by controller name
     */
    @ResponseBody
    @RequestMapping(value = "/loadHttpApiInfos", method = RequestMethod.POST)
    public Result<Map<String, Object>> loadHttpApiInfos(HttpServletRequest request,
                                                        HttpServletResponse response, String serviceName, String ip) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.loadHttpApiInfos] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return httpApiService.getAllHttpModulesInfo(serviceName, ip);
    }

    /**
     * load dubbo service api by service name
     */
    @ResponseBody
    @RequestMapping(value = "/loadDubboApiInfos", method = RequestMethod.POST)
    public Result<Map<String, Object>> loadDubboApiInfos(HttpServletRequest request,
                                                         HttpServletResponse response, String env, String serviceName, String ip) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.loadDubboApiInfos] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (ip == null) {
            ip = "";
        }
        return dubboApiService.getAllModulesInfo(env, serviceName, ip);
    }

    /**
     * load sidecar api by module name
     */
    @ResponseBody
    @RequestMapping(value = "/loadSidecarApiInfos", method = RequestMethod.POST)
    public Result<Map<String, Object>> loadSidecarApiInfos(HttpServletRequest request,
                                                           HttpServletResponse response, String moduleName, String ip) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.loadSidecarApiInfos] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return sidecarApiService.getAllSidecarModulesInfo(moduleName, ip);
    }

    /**
     * update api mock except
     */
    @ResponseBody
    @RequestMapping(value = "/editApiDiyExp", method = RequestMethod.POST)
    public Result<Boolean> editApiDiyExp(HttpServletRequest request,
                                         HttpServletResponse response, Integer apiID, Integer expType, Integer type, String content) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.editApiDiyExp] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiService.editApiDiyExp(apiID, expType, type, content);
    }

    /**
     * update dubbo api doc info by manual
     */
    @ResponseBody
    @RequestMapping(value = "/manualUpdateDubboApi", method = RequestMethod.POST)
    public Result<Boolean> manualUpdateDubboApi(HttpServletRequest request,
                                                HttpServletResponse response, ManualDubboUpDTO dto) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.manualUpdateDubboApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        dto.setOpUsername(account.getUsername());
        return dubboApiService.manualUpdateDubboApi(dto);
    }

    @ResponseBody
    @RequestMapping(value = "/manualUpdateHttpApi", method = RequestMethod.POST)
    public Result<Boolean> manualUpdateHttpApi(HttpServletRequest request,
                                               HttpServletResponse response, ManualHttpUpDTO dto) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.manualUpdateHttpApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        dto.setOpUsername(account.getUsername());
        return httpApiService.manualUpdateHttpApi(dto);
    }

    @ResponseBody
    @RequestMapping(value = "/manualUpdateSidecarApi", method = RequestMethod.POST)
    public Result<Boolean> manualUpdateSidecarApi(HttpServletRequest request,
                                                  HttpServletResponse response, ManualSidecarUpDTO dto) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.manualUpdateSidecarApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        dto.setOpUsername(account.getUsername());
        return sidecarApiService.manualUpdateSidecarApi(dto);
    }

    @ResponseBody
    @RequestMapping(value = "/manualUpdateGatewayApi", method = RequestMethod.POST)
    public Result<Boolean> manualUpdateGatewayApi(HttpServletRequest request,
                                                  HttpServletResponse response, ManualGatewayUpDTO dto) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.manualUpdateGatewayApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        dto.setOpUsername(account.getUsername());
        return gatewayApiService.manualUpdateGatewayApi(dto);
    }

    @ResponseBody
    @RequestMapping(value = "/loadGatewayApiInfo", method = RequestMethod.POST)
    public Result<Map<String, Object>> loadGatewayApiInfo(HttpServletRequest request,
                                                          HttpServletResponse response, String env, String url) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.loadGatewayApiInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return gatewayApiService.loadGatewayApiInfoFromRemote(env, url);
    }

    @ResponseBody
    @RequestMapping(value = "/getGatewayApiDetail", method = RequestMethod.POST)
    public Result<Map<String, Object>> getGatewayApiDetail(HttpServletRequest request,
                                                           HttpServletResponse response, Integer projectID,
                                                           Integer apiID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getGatewayApiDetail] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return gatewayApiService.getGatewayApiDetail(account.getUsername(), projectID, apiID);
    }

    @ResponseBody
    @RequestMapping(value = "/addGatewayApi", method = RequestMethod.POST)
    public Result<Boolean> addGatewayApi(HttpServletRequest request,
                                         HttpServletResponse response,
                                         GatewayApiInfoBo gatewayApiInfoBo,
                                         @RequestParam(value = "apiHeader", required = false) String apiHeader,
                                         @RequestParam(value = "apiRequestParam", required = false) String apiRequestParam,
                                         @RequestParam(value = "apiResultParam", required = false) String apiResultParam,
                                         @RequestParam(value = "apiErrorCodes", required = false) String apiErrorCodes,
                                         @RequestParam(value = "dubboParam", required = false) String dubboParam,
                                         @RequestParam(value = "dubboResp", required = false) String dubboResp) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.addGatewayApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editApiStatus] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        gatewayApiInfoBo.setUpdater(account.getUsername());

        if (Objects.nonNull(dubboParam) && !dubboParam.isEmpty()) {
            apiRequestParam = dubboParam;
        }
        if (Objects.nonNull(dubboResp) && !dubboResp.isEmpty()) {
            apiResultParam = dubboResp;
        }
        if (Objects.isNull(apiErrorCodes)) {
            apiErrorCodes = "";
        }
        if (Objects.isNull(apiHeader)) {
            apiHeader = "";
        }
        if (gatewayApiInfoBo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_HTTP) {
            apiResultParam = "[]";
        }
        return gatewayApiService.addGatewayApi(gatewayApiInfoBo, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes);
    }


    @ResponseBody
    @RequestMapping(value = "/batchAddGatewayApi", method = RequestMethod.POST)
    public Result<Boolean> batchAddGatewayApi(HttpServletRequest request,
                                              HttpServletResponse response, Integer projectID, Integer groupID, String env, String urlList) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.batchAddGatewayApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return gatewayApiService.batchAddGatewayApi(projectID, groupID, env, urlList, account.getUsername());
    }

    @ResponseBody
    @RequestMapping(value = "/updateGatewayApi", method = RequestMethod.POST)
    public Result<Boolean> updateGatewayApi(HttpServletRequest request,
                                            HttpServletResponse response,
                                            GatewayApiInfoBo gatewayApiInfoBo,
                                            @RequestParam(value = "apiHeader", required = false) String apiHeader,
                                            @RequestParam(value = "apiRequestParam", required = false) String apiRequestParam,
                                            @RequestParam(value = "apiResultParam", required = false) String apiResultParam,
                                            @RequestParam(value = "apiErrorCodes", required = false) String apiErrorCodes,
                                            Integer apiID) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.updateGatewayApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        gatewayApiInfoBo.setUpdater(account.getUsername());
        return gatewayApiService.updateGatewayApi(gatewayApiInfoBo, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes, apiID, Consts.GW_ALTER_TYPE_NORMAL);
    }

    /**
     * add dubbo api
     *
     */
    @ResponseBody
    @RequestMapping(value = "/addDubboApi", method = RequestMethod.POST)
    public Result<Boolean> addDubboApi(HttpServletRequest request,
                                       HttpServletResponse response,
                                       @RequestBody ApiCacheItemBo apiBo
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.addDubboApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        apiBo.setUsername(account.getUsername());
        return dubboApiService.addDubboApi(apiBo);
    }

    /**
     * batch add dubbo apis
     */
    @ResponseBody
    @RequestMapping(value = "/batchAddDubboApi", method = RequestMethod.POST)
    public Result<Boolean> batchAddDubboApi(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestBody BatchAddApiBo dubboApiBo
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.batchAddDubboApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (dubboApiBo.getBos().isEmpty()) {
            return Result.fail(CommonError.InvalidParamError);
        }
        dubboApiBo.getBos().forEach(bo -> bo.setUpdateUserName(account.getUsername()));
        return dubboApiService.batchAddDubboApi(dubboApiBo.getApiEnv(), dubboApiBo.getBos());
    }

    /**
     * update dubbo api
     */
    @ResponseBody
    @RequestMapping(value = "/updateDubboApi", method = RequestMethod.POST)
    public Result<Boolean> updateDubboApi(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestBody ApiCacheItemBo apiBo) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.updateDubboApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        apiBo.setUsername(account.getUsername());
        return dubboApiService.updateDubboApi(apiBo, apiBo.getApiID());
    }

    /**
     * get dubbo api info
     */
    @ResponseBody
    @RequestMapping(value = "/getDubboApiDetail", method = RequestMethod.POST)
    public Result<Map<String, Object>> getDubboApiDetail(HttpServletRequest request,
                                                         HttpServletResponse response, Integer projectID,
                                                         Integer apiID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getDubboApiDetail] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return dubboApiService.getDubboApiDetail(account.getUsername(), projectID, apiID);
    }

    /**
     * 获取dubbo接口详情
     */
    @ResponseBody
    @RequestMapping(value = "/getDubboApiDetailRemote", method = RequestMethod.POST)
    public Result<ApiCacheItem> getDubboApiDetailFromRemote(HttpServletRequest request,
                                                            HttpServletResponse response, String env, String service, GetApiBasicRequest dubboApiRequestBo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getDubboApiDetail] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        String[] addrArr = service.split(":");
        if (addrArr.length != 2) {
            return Result.fail(CommonError.InvalidParamError);
        }
        dubboApiRequestBo.setIp(addrArr[0]);
        dubboApiRequestBo.setPort(Integer.parseInt(addrArr[1]));
        return dubboApiService.getDubboApiDetailFromRemote(env, dubboApiRequestBo);
    }


    @ResponseBody
    @RequestMapping(value = "/deleteApi", method = RequestMethod.POST)
    public Result<Boolean> deleteApi(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String apiIDs, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.deleteApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        boolean result = apiService.deleteApi(projectID, apiIDs, account.getUsername());
        if (result) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * get http api detail
     */
    @ResponseBody
    @RequestMapping(value = "/getHttpApi", method = RequestMethod.POST)
    public Result<Map<String, Object>> getHttpApi(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  Integer apiID, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getHttpApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        Map<String, Object> result = httpApiService.getHttpApi(account.getUsername(), projectID, apiID);
        return Result.success(result);
    }

    /**
     * get sidecar api info
     */
    @ResponseBody
    @RequestMapping(value = "/getSidecarApi", method = RequestMethod.POST)
    public Result<Map<String, Object>> getSidecarApi(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     Integer apiID, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getSidecarApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<String, Object> result = sidecarApiService.getSidecarApi(account.getId().intValue(), projectID, apiID);
        return Result.success(result);
    }

    @ResponseBody
    @RequestMapping(value = "/getApiList", method = RequestMethod.POST)
    public Result<Map<String, Object>> getApiList(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  Integer pageNo, Integer pageSize,
                                                  Integer projectID, Integer groupID,
                                                  Integer orderBy, Integer asc) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.getApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<String, Object> result = apiService.getApiList(pageNo, pageSize, projectID, groupID, orderBy, asc);

        return Result.success(result);
    }

    @ResponseBody
    @RequestMapping(value = "/getGroupApiViewList", method = RequestMethod.POST)
    public Result<Map<Integer, List<Map<String, Object>>>> getGroupApiViewList(HttpServletRequest request,
                                                                               HttpServletResponse response,
                                                                               Integer projectID, Integer orderBy) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getGroupApiViewList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<Integer, List<Map<String, Object>>> result = apiService.getGroupApiViewList(projectID, orderBy);
        return Result.success(result);
    }

    @ResponseBody
    @RequestMapping(value = "/getRecentlyApiList", method = RequestMethod.GET)
    public Result<List<Api>> getRecentlyApiList(HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ProjectController.getRecentlyApiList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<Api> result = apiService.getRecentlyApiList(account.getUsername());
        return Result.success(result);
    }

    @ResponseBody
    @RequestMapping(value = "/getApiListByIndex", method = RequestMethod.POST)
    public Result<List<Map<String, String>>> getApiListByIndex(HttpServletRequest request,
                                                               HttpServletResponse response,
                                                               Integer indexID, Integer projectID) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getApiListByIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiService.getApiListByIndex(indexID);
    }

    @ResponseBody
    @RequestMapping(value = "/getAllIndexGroupApiViewList", method = RequestMethod.POST)
    public Result<Map<Integer, List<Map<String, Object>>>> getAllIndexGroupApiViewList(HttpServletRequest request,
                                                                                       HttpServletResponse response,
                                                                                       Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getAllIndexGroupApiViewList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<Integer, List<Map<String, Object>>> result = apiService.getAllIndexGroupApiViewList(projectID);
        return Result.success(result);
    }

    @ResponseBody
    @RequestMapping(value = "/searchApi", method = RequestMethod.POST)
    public Result<List<Map<String, Object>>> searchApi(HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       Integer projectID, String tips, Integer type) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.searchApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        List<Map<String, Object>> result = apiService.searchApi(projectID, tips, type);
        return Result.success(result);
    }

    @ResponseBody
    @RequestMapping(value = "/editApiMockExpect", method = RequestMethod.POST)
    public Result editApiMockExpect(HttpServletRequest request,
                                    HttpServletResponse response,
                                    MockExpectBo bo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.editApiMockExpect] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        switch (bo.getApiType()) {
            case 1:
                return mockService.updateHttpApiMockData(account.getUsername(), bo.getMockExpID(), bo.getApiID(), bo.getParamsJson(), bo.getMockRequestRaw(), bo.getMockRequestParamType(), bo.getMockExpName(), bo.getProjectID(), bo.getMockRule(), bo.getMockDataType(), bo.getDefaultSys(), bo.getEnableMockScript(), bo.getMockScript());
            case 3:
                return mockService.updateDubboApiMockData(account.getUsername(), bo.getMockExpID(), bo.getApiID(), bo.getParamsJson(), bo.getMockRequestRaw(), bo.getMockRequestParamType(), bo.getMockExpName(), bo.getProjectID(), bo.getMockRule(), bo.getMockDataType(), bo.getDefaultSys(), bo.getEnableMockScript(), bo.getMockScript());
            case 4:
                return mockService.updateGatewayApiMockData(account.getUsername(), bo.getMockExpID(), bo.getApiID(), bo.getProjectID(), bo.getParamsJson(), bo.getMockRequestRaw(), bo.getMockRequestParamType(), bo.getMockExpName(), null, bo.getMockRule(), bo.getMockDataType(), bo.getDefaultSys(), bo.getEnableMockScript(), bo.getMockScript());
            default:
                return Result.fail(CommonError.InvalidParamError);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getApiHistoryList", method = RequestMethod.POST)
    public Result<List<Map<String, Object>>> getApiHistoryList(HttpServletRequest request,
                                                               HttpServletResponse response,
                                                               Integer projectID, Integer apiID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.getApiHistoryList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        List<Map<String, Object>> result = apiService.getApiHistoryList(projectID, apiID);
        return Result.success(result);
    }
}
