package com.xiaomi.miapi.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.common.bo.*;
import com.xiaomi.miapi.common.dto.ManualDubboUpDTO;
import com.xiaomi.miapi.common.dto.ManualGatewayUpDTO;
import com.xiaomi.miapi.common.dto.ManualHttpUpDTO;
import com.xiaomi.miapi.service.*;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.common.pojo.Api;
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
import java.util.stream.Collectors;

/**
 * 接口控制器
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
    private GatewayApiService gatewayApiService;

    @Autowired
    private GrpcApiService grpcApiService;

    @Autowired
    private MockService mockService;
    @Autowired
    private LoginService loginService;

    @Reference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    /**
     * 添加http接口
     *
     * @param request
     * @return
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.addProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, api.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        api.setUpdateUsername(account.getUsername());
        return httpApiService.addHttpApi(api, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes, false);
    }

    /**
     * 批量添加dubbo接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/batchAddHttpApi", method = RequestMethod.POST)
    public Result<Boolean> batchAddHttpApi(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestBody BatchAddHttpApiBo httpApiBo
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiController.batchAddHttpApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, httpApiBo.getBos().get(0).getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        httpApiBo.getBos().forEach(bo -> bo.setUpdateUserName(account.getUsername()));
        return httpApiService.batchAddHttpApi(httpApiBo.getApiEnv(), httpApiBo.getBos());
    }

    /**
     * 修改http接口
     *
     * @param request
     * @return
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

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.addProject] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, api.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        api.setUpdateUsername(account.getUsername());
        return httpApiService.editHttpApi(api, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes,true);
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editApiStatus] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        return apiService.editApiStatus(projectID, apiID, status);
    }


    /**
     * 根据服务名大致搜索nacos上的服务列表可搜dubbo、http接口服务
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/loadDubboApiServices", method = RequestMethod.POST)
    public Result<List<DubboService>> loadDubboApiServices(HttpServletRequest request,
                                                           HttpServletResponse response, String env, String namespace, String serviceName) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.loadDubboApiServices] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.loadDubboApiServices] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (namespace == null || namespace.equalsIgnoreCase("default")) {
            namespace = "";
        }

        return dubboApiService.loadDubboApiServices(serviceName, env, namespace);
    }

    /**
     * 根据服务名大致搜索nacos上的服务列表可搜grpc服务
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/loadGrpcService", method = RequestMethod.POST)
    public Result<List<DubboService>> loadGrpcService(HttpServletRequest request,
                                                      HttpServletResponse response, String serviceName) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[AccountController.loadGrpcService] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.loadGrpcService] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (Objects.isNull(serviceName)) {
            serviceName = "";
        }
        serviceName = "grpc:" + serviceName;
        List<DubboService> services = dubboApiService.loadDubboApiServices(serviceName, "staging", "").getData();
        services = services.stream().filter(service -> service.getName().startsWith("grpc:")).collect(Collectors.toList());
        return Result.success(services);
    }

    /**
     * 根据服务名加载 grpc 类型 api 服务及方法名
     *
     * @throws IOException
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.loadGrpcApiInfos] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return grpcApiService.loadGrpcApiInfos(appName);
    }

    /**
     * 根据服务名加载 grpc 类型 api 服务及方法名
     *
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.loadGrpcServerAddr] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return grpcApiService.loadGrpcServerAddr(appName);
    }

    /**
     * 批量添加 grpc 接口
     *
     * @param request
     * @return
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiController.batchAddGrpcApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, grpcApiBo.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        grpcApiBo.setUpdateUserName(account.getUsername());
        return grpcApiService.batchAddGrpcApi(grpcApiBo);
    }

    /**
     * 更新 grpc 接口
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiController.updateGrpcApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, updateGrpcApiBo.getProjectId().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        updateGrpcApiBo.setUpdateUserName(account.getUsername());
        return grpcApiService.updateGrpcApi(updateGrpcApiBo);
    }

    /**
     * 获取Grpc接口详情
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getGrpcApiDetail] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return grpcApiService.getGrpcApiDetail(account.getId().intValue(), projectID, apiID);
    }

    /**
     * 根据服务名加载http类型api
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/loadHttpApiInfos", method = RequestMethod.POST)
    public Result<Map<String, Object>> loadHttpApiInfos(HttpServletRequest request,
                                                        HttpServletResponse response, String serviceName) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.loadHttpApiInfos] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.loadHttpApiInfos] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return httpApiService.getAllHttpModulesInfo(serviceName);
    }

    /**
     * 根据服务名加载dubbo类型api
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.loadDubboApiInfos] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (ip == null) {
            ip = "";
        }
        return dubboApiService.getAllModulesInfo(env, serviceName, ip);
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/editApiDiyExp", method = RequestMethod.POST)
    public Result<Boolean> editApiDiyExp(HttpServletRequest request,
                                         HttpServletResponse response, Integer apiID, Integer expType, Integer type,String content) throws IOException, NacosException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.editApiDiyExp] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiController.editApiDiyExp] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return apiService.editApiDiyExp(apiID,expType,type,content);
    }

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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.manualUpdateDubboApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.manualUpdateHttpApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        dto.setOpUsername(account.getUsername());
        return httpApiService.manualUpdateHttpApi(dto);
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.manualUpdateGatewayApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        dto.setOpUsername(account.getUsername());
        return gatewayApiService.manualUpdateGatewayApi(dto);
    }

    /**
     * 根据url路径加载mione网关中的api
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.loadDubboApiInfos] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        return gatewayApiService.loadGatewayApiInfoFromRemote(env, url);
    }

    /**
     * 获取dubbo接口详情
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getGatewayApiDetail] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        return gatewayApiService.getGatewayApiDetail(account.getId().intValue(), projectID, apiID);
    }

    /**
     * 添加网关类型接口
     *
     * @param request
     * @return
     */
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
                                         @RequestParam(value = "dubboResp", required = false) String dubboResp
    ) throws IOException {

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

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, gatewayApiInfoBo.getProjectId().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
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


    /**
     * 添加网关类型接口
     *
     * @param request
     * @return
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.batchAddGatewayApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        return gatewayApiService.batchAddGatewayApi(projectID, groupID, env, urlList, account.getUsername());
    }

    /**
     * 更新网关类型接口
     *
     * @param request
     * @return
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiController.editApiStatus] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, gatewayApiInfoBo.getProjectId().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        gatewayApiInfoBo.setUpdater(account.getUsername());
        return gatewayApiService.updateGatewayApi(gatewayApiInfoBo, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes, apiID, Consts.GW_ALTER_TYPE_NORMAL);
//        }
    }

    /**
     * 添加dubbo接口
     *
     * @param request
     * @return
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiController.editApiStatus] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, apiBo.getProjectId().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        apiBo.setUsername(account.getUsername());
        return dubboApiService.addDubboApi(apiBo);
    }

    /**
     * 批量添加dubbo接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/batchAddDubboApi", method = RequestMethod.POST)
    public Result<Boolean> batchAddDubboApi(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestBody BatchAddDubboApiBo dubboApiBo
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiController.batchAddDubboApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ApiController.batchAddDubboApi] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (dubboApiBo.getBos().isEmpty()) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, dubboApiBo.getBos().get(0).getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        dubboApiBo.getBos().forEach(bo -> bo.setUpdateUserName(account.getUsername()));
        return dubboApiService.batchAddDubboApi(dubboApiBo.getApiEnv(), dubboApiBo.getBos());
    }

    /**
     * 更新dubbo接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateDubboApi", method = RequestMethod.POST)
    public Result<Boolean> updateDubboApi(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @RequestBody ApiCacheItemBo apiBo
    ) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.updateDubboApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editApiStatus] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, apiBo.getProjectId().longValue(), account.getUsername())) {
            response.sendError(401, "需要work以上权限");
            return null;
        }
        apiBo.setUsername(account.getUsername());
        return dubboApiService.updateDubboApi(apiBo, apiBo.getApiID());
//        }
    }

    /**
     * 获取dubbo接口详情
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.getDubboApiDetail] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return dubboApiService.getDubboApiDetail(account.getId().intValue(), projectID, apiID);
    }

    /**
     * 获取dubbo接口详情
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/getDubboApiDetailRemote", method = RequestMethod.POST)
    public Result<ApiCacheItem> getDubboApiDetailFromRemote(HttpServletRequest request,
                                                            HttpServletResponse response, String env, String service, GetDubboApiRequestBo dubboApiRequestBo) throws IOException {
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


    /**
     * 批量删除api,将其移入回收站
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeApi", method = RequestMethod.POST)
    public Result<Boolean> removeApi(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String apiIDs, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.removeApi] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (!busProjectService.isBusProjectAdmin(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
            return null;
        }

        boolean result = apiService.removeApi(projectID, apiIDs, account.getId().intValue(), account.getUsername());
        if (result) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * 彻底删除接口
     *
     * @param request
     * @return
     */
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
        if (!busProjectService.isBusProjectAdmin(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
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
     * 获取http接口详情
     *
     * @param request
     * @param apiID
     * @param projectID
     * @return
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editApiStatus] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是项目成员");
            return null;
        }

        Map<String, Object> result = httpApiService.getHttpApi(account.getId().intValue(), projectID, apiID);
        return Result.success(result);
    }

    /**
     * 获取分组接口列表
     *
     * @param request
     * @param projectID
     * @param orderBy
     * @param asc
     * @return
     */
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
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是项目成员");
            return null;
        }
        Map<String, Object> result = apiService.getApiList(pageNo, pageSize, projectID, groupID, orderBy, asc);

        return Result.success(result);
    }

    /**
     * 获取分组接口列表
     *
     * @param request
     * @param projectID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getGroupApiViewList", method = RequestMethod.POST)
    public Result<Map<Integer, List<Map<String, Object>>>> getGroupApiViewList(HttpServletRequest request,
                                                                               HttpServletResponse response,
                                                                               Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getGroupApiViewList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是项目成员");
            return null;
        }
        Map<Integer, List<Map<String, Object>>> result = apiService.getGroupApiViewList(projectID);
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
        List<Api> result = apiService.getRecentlyApiList(account.getId().intValue());
        return Result.success(result);
    }


    /**
     * 根据索引分组获取接口列表
     *
     * @param request
     * @return
     */
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

        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是项目成员");
            return null;
        }
        return apiService.getApiListByIndex(indexID);
    }

    /**
     * 获取索引分组接口列表
     *
     * @param request
     * @param projectID
     * @return
     */
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
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是项目成员");
            return null;
        }
        Map<Integer, List<Map<String, Object>>> result = apiService.getAllIndexGroupApiViewList(projectID);
        return Result.success(result);
    }

    /**
     * 获取接口详情
     *
     * @param request
     * @param projectID
     * @return
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editApiStatus] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editApiMockExpect] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, bo.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
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

    /**
     * @param request
     * @param projectID
     * @return
     */
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
