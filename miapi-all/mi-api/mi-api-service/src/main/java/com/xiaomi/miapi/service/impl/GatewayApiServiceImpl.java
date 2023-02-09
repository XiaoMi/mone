package com.xiaomi.miapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.pojo.*;
import com.xiaomi.miapi.util.Md5Utils;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.miapi.bo.GatewayApiInfoBo;
import com.xiaomi.miapi.dto.ManualGatewayUpDTO;
import com.xiaomi.miapi.dto.UrlDTO;
import com.xiaomi.miapi.service.GatewayApiService;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.youpin.codegen.bo.ApiHeaderBo;
import com.xiaomi.youpin.gwdash.service.IGatewayOpenApi;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Service
public class GatewayApiServiceImpl implements GatewayApiService {
    @Autowired
    RedisUtil redis;

    @Autowired
    ApiMapper apiMapper;

    @Autowired
    EoDubboApiInfoMapper dubboApiInfoMapper;

    @Autowired
    GatewayApiInfoMapper gatewayApiInfoMapper;

    @Autowired
    ApiGroupMapper apiGroupMapper;
    @Autowired
    ApiCacheMapper apiCacheMapper;

    @Autowired
    ApiRequestExpMapper requestExpMapper;

    @Autowired
    ApiResponseExpMapper responseExpMapper;

    @Autowired
    RecordService recordService;

    @Autowired
    MockService mockService;

    @DubboReference(registry = "olRegistry", lazy = true,check = false, interfaceClass = IGatewayOpenApi.class, group = "${ref.gwdash.service.group}", timeout = 4000)
    private IGatewayOpenApi gwdashApiService;

    @DubboReference(registry = "olRegistry", lazy = true,check = false, interfaceClass = IGatewayOpenApi.class, group = "${ref.gwdash.service.group.ot}", timeout = 4000)
    private IGatewayOpenApi otGwdashApiService;

    @DubboReference(registry = "stRegistry",lazy = true, check = false, interfaceClass = IGatewayOpenApi.class, group = "${ref.gwdash.service.group.st}", timeout = 4000)
    private IGatewayOpenApi stIGatewayOpenApi;

    @Autowired
    private ApiMockExpectMapper mockExpectMapper;

    @Autowired
    ApiServiceImpl apiService;

    public static final Gson gson = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApiServiceImpl.class);

    @Override
    public Result<Map<String, Object>> getGatewayApiDetail(String username, Integer projectID, Integer apiID) {
        Map<String, Object> map = new HashMap<>();
        Api api = apiMapper.getApiInfo(projectID, apiID);
        if (null == api) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        GatewayApiInfo gatewayApiInfo = gatewayApiInfoMapper.selectByPrimaryKey(api.getGatewayApiId().longValue());
        if (StringUtils.isEmpty(api.getApiEnv())) {
            map.put("apiEnv", "staging");
        } else {
            map.put("apiEnv", api.getApiEnv());
        }
        map.put("gatewayApiBaseInfo", gatewayApiInfo);
        map.put("updateUsername", api.getUpdateUsername());
        map.put("projectID", api.getProjectID());
        map.put("groupID", api.getGroupID());
        map.put("apiStatus", api.getApiStatus());
        String groupName = apiGroupMapper.getGroupByID(api.getGroupID()).getGroupName();
        map.put("groupName", groupName);
        Map<String, Object> result = apiMapper.getApi(projectID, apiID);
        if (result != null && !result.isEmpty()) {
            Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
            if (apiJson != null && !apiJson.isEmpty()) {
                map.put("headerInfo", apiJson.get("headerInfo"));
                map.put("requestInfo", apiJson.get("requestInfo"));
                map.put("resultInfo", apiJson.get("resultInfo"));
                map.put("apiErrorCodes", apiJson.get("errorCodes"));
            }
        }

        map.put("apiNoteType", api.getApiNoteType());
        map.put("apiRemark", api.getApiRemark());
        map.put("apiDesc", api.getApiDesc());
        map.put("apiRequestParamType", api.getApiRequestParamType());
        map.put("apiRequestRaw", api.getApiRequestRaw());
        map.put("apiResponseParamType", api.getApiResponseParamType());
        map.put("apiResponseRaw", api.getApiResponseRaw());

        ApiRequestExpExample reqExample = new ApiRequestExpExample();
        reqExample.createCriteria().andApiIdEqualTo(apiID);
        List<ApiRequestExp> reqExpList = requestExpMapper.selectByExampleWithBLOBs(reqExample);
        map.put("reqExpList", reqExpList);

        ApiResponseExpExample respExample = new ApiResponseExpExample();
        respExample.createCriteria().andApiIdEqualTo(apiID);
        List<ApiResponseExp> respExpList = responseExpMapper.selectByExampleWithBLOBs(respExample);
        map.put("respExpList", respExpList);

        String md5Location = Md5Utils.getMD5(gatewayApiInfo.getUrl());
        String uri = gatewayApiInfo.getUrl().replaceAll("/", ":");
        map.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT, Consts.MockUrlPrefix + Consts.GatewayMockPrefix, md5Location, uri));
        redis.recordRecently10Apis(username, apiID);
        return Result.success(map);
    }

    @Override
    public Result<List<Map<String, Object>>> getGatewayApiDetailByUrl(List<UrlDTO> urls) {
        List<Map<String, Object>> resultList = new ArrayList<>(urls.size());
        for (UrlDTO urlDTO :
                urls) {
            Map<String, Object> map = new HashMap<>();
            Api api = apiMapper.getApiInfoByUrl(urlDTO.getUrl(), urlDTO.getRequestType());
            if (null == api) {
                continue;
            }
            GatewayApiInfo gatewayApiInfo = gatewayApiInfoMapper.selectByPrimaryKey(api.getGatewayApiId().longValue());
            if (StringUtils.isEmpty(api.getApiEnv())) {
                map.put("apiEnv", "staging");
            } else {
                map.put("apiEnv", api.getApiEnv());
            }
            map.put("url",urlDTO.getUrl());
            map.put("httpMethod",urlDTO.getRequestType());
            map.put("gatewayApiBaseInfo", gatewayApiInfo);
            map.put("updateUsername", api.getUpdateUsername());
            Map<String, Object> result = apiMapper.getApi(api.getProjectID(), api.getApiID());
            if (result != null && !result.isEmpty()) {
                Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
                if (apiJson != null && !apiJson.isEmpty()) {
                    map.put("headerInfo", apiJson.get("headerInfo"));
                    map.put("requestInfo", apiJson.get("requestInfo"));
                    map.put("resultInfo", apiJson.get("resultInfo"));
                    map.put("apiErrorCodes", apiJson.get("errorCodes"));
                }
            }
            map.put("apiRequestParamType", api.getApiRequestParamType());
            map.put("apiRequestRaw", api.getApiRequestRaw());
            map.put("apiResponseParamType", api.getApiResponseParamType());
            map.put("apiResponseRaw", api.getApiResponseRaw());
            resultList.add(map);
        }
        return Result.success(resultList);
    }

    @Override
    public Result<Boolean> manualUpdateGatewayApi(ManualGatewayUpDTO dto) {
        Api api = apiMapper.getApiInfo(dto.getProjectID(), dto.getApiID());
        if (Objects.isNull(api)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        Map<String, Object> gatewayBo = this.loadGatewayApiInfoFromRemote(dto.getEnv(), api.getApiURI()).getData();

        if (Objects.isNull(gatewayBo)){
            return Result.fail(CommonError.APIDoNotExist);
        }
        GatewayApiInfoBo bo = new GatewayApiInfoBo();
        com.xiaomi.youpin.gwdash.bo.GatewayApiInfo baseInfo = (com.xiaomi.youpin.gwdash.bo.GatewayApiInfo) gatewayBo.get("baseInfo");
        BeanUtils.copyProperties(baseInfo, bo);
        bo.setApiEnv(dto.getEnv());
        bo.setProjectId(api.getProjectID());
        bo.setGroupId(api.getGroupID());
        bo.setUpdater(dto.getOpUsername());
        bo.setUpdateMsg(dto.getUpdateMsg());
        bo.setApiRequestParamType(Consts.JSON_DATA_TYPE);
        bo.setApiResponseParamType(Consts.JSON_DATA_TYPE);
        String apiHeader = "";
        String apiRequestParam = "";
        String apiResultParam = "";
        String apiErrorCodes = "";

        if (bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_HTTP) {
            apiRequestParam = (String) gatewayBo.get("httpParam");
            apiResultParam = (String) gatewayBo.get("httpResp");
        } else if (bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_MI_DUBBO || bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_DUBBO) {
            apiRequestParam = (String) gatewayBo.get("dubboParam");
            apiResultParam = (String) gatewayBo.get("dubboResp");
        }
        updateGatewayApi(bo, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes, api.getApiID(),Consts.GW_ALTER_TYPE_MANUAL);

        return Result.success(true);
    }


    @Override
    @Transactional
    public Result<Boolean> addGatewayApi(GatewayApiInfoBo bo, String apiHeader, String apiRequestParam, String
            apiResultParam, String apiErrorCodes) {
        int apiRequestType = 0;
        if ("GET".equalsIgnoreCase(bo.getHttpMethod())) {
            apiRequestType = 1;
        }
        Api oldApi = apiMapper.getApiInfoByUrlAndProject(bo.getUrl(), apiRequestType,bo.getProjectId());
        if (oldApi != null) {
            return Result.fail(CommonError.UrlExistError);
        }
        GatewayApiInfo gatewayApiInfo = new GatewayApiInfo();
        BeanUtils.copyProperties(bo, gatewayApiInfo);

        gatewayApiInfo.setCtime(System.currentTimeMillis());
        gatewayApiInfo.setUtime(System.currentTimeMillis());

        int rt = gatewayApiInfoMapper.insert(gatewayApiInfo);
        if (rt < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        if (gatewayApiInfo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_DUBBO || gatewayApiInfo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_MI_DUBBO) {
            apiRequestParam = apiService.transDubboParam2Http(apiRequestParam);
            apiResultParam = apiService.transDubboResp2Http(apiResultParam);
            bo.setApiRequestParamType(Consts.JSON_DATA_TYPE);
        }
        Map<String, Object> cache = new HashMap<>();
        cache.put("headerInfo", JSONArray.parseArray(apiHeader));
        cache.put("requestInfo", JSONArray.parseArray(apiRequestParam));
        cache.put("resultInfo", JSONArray.parseArray(apiResultParam));
        cache.put("errorCodes", JSONArray.parseArray(apiErrorCodes));

        Api api = new Api();
        api.setApiEnv(bo.getApiEnv());
        api.setApiRequestParamType(bo.getApiRequestParamType());
        api.setApiResponseParamType(bo.getApiResponseParamType());
        api = apiService.checkAndFillApiInfo(api);
        api.setProjectID(bo.getProjectId());
        api.setGroupID(bo.getGroupId());
        api.setApiRequestType(apiRequestType);
        if (StringUtils.isEmpty(api.getApiRequestRaw())) {
            api.setApiRequestRaw("");
        }
        if (bo.getApiRequestParamType() == Consts.JSON_DATA_TYPE) {
            api.setApiRequestRaw(gson.toJson(mockService.parseStructToJson(apiRequestParam,false)));
        } else {
            api.setApiRequestRaw(bo.getApiRequestRaw());
        }

        if (StringUtils.isEmpty(bo.getApiResponseRaw())) {
            api.setApiResponseRaw("");
        }
        if (bo.getApiResponseParamType() == Consts.JSON_DATA_TYPE) {
            api.setApiResponseRaw(gson.toJson(mockService.parseStructToJson(apiResultParam,false)));
        } else {
            api.setApiResponseRaw(bo.getApiResponseRaw());
        }
        api.setApiStatus(bo.getStatus());
        api.setApiName(bo.getName());
        api.setApiProtocol(Consts.GATEWAY_API_TYPE);
        api.setApiURI(bo.getUrl());
        api.setUpdateUsername(bo.getUpdater());
        api.setGatewayApiId(gatewayApiInfo.getId().intValue());
        if (bo.getApiNoteType() == null) {
            api.setApiNoteType(0);
        } else {
            api.setApiNoteType(bo.getApiNoteType());
        }
        if (bo.getApiDesc() == null) {
            api.setApiDesc("");
        } else {
            api.setApiDesc(bo.getApiDesc());
        }
        if (bo.getApiRemark() == null) {
            api.setApiRemark("");
        } else {
            api.setApiRemark(bo.getApiRemark());
        }
        api.setDubboApiId(0);
        Integer ok = apiMapper.addApi(api);
        if (ok < 0) {
            return Result.fail(CommonError.UnknownError);
        }
        ApiCache apiCache = new ApiCache();
        apiCache.setApiID(api.getApiID());
        cache.put("baseInfo", api);
        apiCache.setApiJson(gson.toJson(cache));
        apiCache.setGroupID(api.getGroupID());
        apiCache.setProjectID(api.getProjectID());
        apiCache.setStarred(api.getStarred());
        apiCache.setUpdateUsername(api.getUpdateUsername());
        if (apiCacheMapper.addApiCache(apiCache) < 1) {
            return Result.fail(CommonError.UnknownError);
        }

        mockService.updateGatewayApiMockData(api.getUpdateUsername(), null, api.getApiID(), api.getProjectID(), "", "", 0, "系统默认Mock期望", gatewayApiInfo, api.getApiResponseRaw(), 1, true,false,"");

        try {
            List<ApiHeaderBo> headerList = gson.fromJson(apiHeader, new TypeToken<List<ApiHeaderBo>>() {
            }.getType());
            apiService.codeGen(api, api.getApiRequestParamType(), apiRequestParam, api.getApiResponseParamType(), apiResultParam, headerList, false);
        } catch (Exception e) {
            LOGGER.warn("生成代码失败", e);
        }

        String updateMsg = "add gateway api";
        if (StringUtils.isNotEmpty(bo.getUpdateMsg())) {
            updateMsg = bo.getUpdateMsg();
        }
        apiService.recordApiHistory(api, apiCache.getApiJson(), updateMsg);

        recordService.doRecord(api, "", "添加网关类型接口", "添加网关类型接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_ADD);

        return Result.success(true);
    }

    @Override
    public Result<Boolean> batchAddGatewayApi(Integer projectID, Integer groupID, String env, String gatewayInfos, String username) {
        List<String> urlArr = gson.fromJson(gatewayInfos, new TypeToken<List<String>>() {
        }.getType());

        try {
            urlArr.forEach(url -> {
                Map<String, Object> gatewayApi = loadGatewayApiInfoFromRemote(env, url).getData();
                if (Objects.nonNull(gatewayApi)) {
                    GatewayApiInfoBo bo = new GatewayApiInfoBo();
                    com.xiaomi.youpin.gwdash.bo.GatewayApiInfo baseInfo = (com.xiaomi.youpin.gwdash.bo.GatewayApiInfo) gatewayApi.get("baseInfo");
                    BeanUtils.copyProperties(baseInfo, bo);
                    bo.setApiEnv(env);
                    bo.setProjectId(projectID);
                    bo.setGroupId(groupID);
                    bo.setUpdater(username);
                    bo.setUpdateMsg("batch_add");
                    bo.setApiRequestParamType(Consts.JSON_DATA_TYPE);
                    bo.setApiResponseParamType(Consts.JSON_DATA_TYPE);
                    String apiHeader = "";
                    String apiRequestParam = "";
                    String apiResultParam = "";
                    String apiErrorCodes = "";

                    if (bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_HTTP) {
                        apiRequestParam = (String) gatewayApi.get("httpParam");
                        apiResultParam = (String) gatewayApi.get("httpResp");
                    } else if (bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_MI_DUBBO || bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_DUBBO) {
                        apiRequestParam = (String) gatewayApi.get("dubboParam");
                        apiResultParam = (String) gatewayApi.get("dubboResp");
                    }
                    addGatewayApi(bo, apiHeader, apiRequestParam, apiResultParam, apiErrorCodes);
                }
            });
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(true);
    }

    @Override
    @Transactional
    public Result<Boolean> updateGatewayApi(GatewayApiInfoBo bo, String apiHeader, String apiRequestParam, String
            apiResultParam, String apiErrorCodes, Integer apiId, int alterType) {
        Api api = apiMapper.getApiInfo(bo.getProjectId(), apiId);
        if (api == null) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        if (StringUtils.isEmpty(api.getApiEnv())) {
            api.setApiEnv("staging");
        }
        api.setApiUpdateTime(new Timestamp(System.currentTimeMillis()));
        api.setGroupID(bo.getGroupId());
        api.setApiName(bo.getName());
        api.setApiProtocol(Consts.GATEWAY_API_TYPE);
        api.setUpdateUsername(bo.getUpdater());
        api.setApiNoteType(bo.getApiNoteType());
        api.setApiDesc(bo.getApiDesc());
        api.setApiRemark(bo.getApiRemark());
        api.setApiRequestParamType(bo.getApiRequestParamType());
        api.setApiResponseParamType(bo.getApiResponseParamType());
        api.setUpdateUsername(bo.getUpdater());

        if (alterType == Consts.GW_ALTER_TYPE_MANUAL) {
            if (bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_DUBBO || bo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_MI_DUBBO) {
                apiRequestParam = apiService.transDubboParam2Http(apiRequestParam);
                apiResultParam = apiService.transDubboResp2Http(apiResultParam);
                bo.setApiRequestParamType(Consts.JSON_DATA_TYPE);
            }
        }

        if (StringUtils.isEmpty(api.getApiRequestRaw())) {
            api.setApiRequestRaw("");
        }
        if (bo.getApiRequestParamType() == Consts.JSON_DATA_TYPE) {
            api.setApiRequestRaw(gson.toJson(mockService.parseStructToJson(apiRequestParam,false)));
        } else {
            api.setApiRequestRaw(bo.getApiRequestRaw());
        }

        if (StringUtils.isEmpty(bo.getApiResponseRaw())) {
            api.setApiResponseRaw("");
        }
        if (bo.getApiResponseParamType() == Consts.JSON_DATA_TYPE) {
            api.setApiResponseRaw(gson.toJson(mockService.parseStructToJson(apiResultParam,false)));
        } else {
            api.setApiResponseRaw(bo.getApiResponseRaw());
        }

        if ("get".equalsIgnoreCase(bo.getHttpMethod())) {
            api.setApiRequestType(1);
        } else if ("post".equalsIgnoreCase(bo.getHttpMethod())) {
            api.setApiRequestType(0);
        }

        apiService.checkAndFillApiInfo(api);

        int result = apiMapper.updateApi(api);
        if (result < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        Map<String, Object> cache = new HashMap<String, Object>();

        cache.put("headerInfo", JSONArray.parseArray(apiHeader));
        cache.put("requestInfo", JSONArray.parseArray(apiRequestParam));
        cache.put("resultInfo", JSONArray.parseArray(apiResultParam));

        try {
            cache.put("errorCodes", JSONArray.parseArray(apiErrorCodes));
        } catch (Exception e) {
            cache.put("errorCodes",new ArrayList<>());
        }
        cache.put("baseInfo", api);

        ApiCache apiCache = new ApiCache();
        apiCache.setApiID(api.getApiID());
        apiCache.setApiJson(gson.toJson(cache));
        apiCache.setGroupID(api.getGroupID());
        apiCache.setProjectID(api.getProjectID());
        apiCache.setStarred(api.getStarred());
        apiCache.setUpdateUsername(api.getUpdateUsername());
        if (apiCacheMapper.updateApiCache(apiCache) < 1) {
            return Result.fail(CommonError.UnknownError);
        }
        GatewayApiInfo gatewayApiInfo = new GatewayApiInfo();
        BeanUtils.copyProperties(bo, gatewayApiInfo);
        gatewayApiInfo.setUtime(System.currentTimeMillis());
        gatewayApiInfo.setId(api.getGatewayApiId().longValue());

        int rt = gatewayApiInfoMapper.updateByPrimaryKeyWithBLOBs(gatewayApiInfo);
        if (rt < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        ApiMockExpectExample example = new ApiMockExpectExample();
        example.createCriteria().andApiIdEqualTo(api.getApiID()).andIsDefaultEqualTo(true);
        List<ApiMockExpect> expects = mockExpectMapper.selectByExample(example);
        if (Objects.nonNull(expects) && !expects.isEmpty()) {
            mockService.updateGatewayApiMockData(api.getUpdateUsername(), expects.get(0).getId(), api.getApiID(), api.getProjectID(), "", "", 0, "系统默认Mock期望", gatewayApiInfo, api.getApiResponseRaw(), 1, true,false,"");
        }
        try {
            List<ApiHeaderBo> headerList = gson.fromJson(apiHeader, new TypeToken<List<ApiHeaderBo>>() {
            }.getType());
            apiService.codeGen(api, api.getApiRequestParamType(), apiRequestParam, api.getApiResponseParamType(), apiResultParam, headerList, true);
        } catch (Exception e) {
            LOGGER.warn("生成代码失败", e);
        }
        String updateMsg = "update gateway api";
        if (StringUtils.isNotEmpty(bo.getUpdateMsg())) {
            updateMsg = bo.getUpdateMsg();
        }
        apiService.recordApiHistory(api, apiCache.getApiJson(), updateMsg);

        recordService.doRecord(api, null, "更新网关类型接口", "更新网关类型接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_UPDATE);
        return Result.success(true);
    }

    @Override
    public Result<Map<String, Object>> loadGatewayApiInfoFromRemote(String env, String url) {
        Map<String, Object> resultMap = new HashMap<>();
        com.xiaomi.youpin.infra.rpc.Result<com.xiaomi.youpin.gwdash.bo.GatewayApiInfo> result;

        if (StringUtils.isNotEmpty(env) && env.equals("staging")) {
            result = stIGatewayOpenApi.getGatewayApiInfo(url,"1");
        } else if (StringUtils.isNotEmpty(env) && env.equals("outer")) {
            result = otGwdashApiService.getGatewayApiInfo(url,"1");
        } else {
            result = gwdashApiService.getGatewayApiInfo(url,"1");
        }
        com.xiaomi.youpin.gwdash.bo.GatewayApiInfo gatewayApiInfo = result.getData();
        if (result.getCode() != CommonError.Success.getCode()) {
            return Result.fail(CommonError.GatewayAPIDoesNotExist);
        }
        resultMap.put("baseInfo", gatewayApiInfo);
        if (gatewayApiInfo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_HTTP) {
            // load http doc from anno, we need to load http api first
            resultMap.put("httpParam", "");
            resultMap.put("httpResp", "");
        } else if (gatewayApiInfo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_MI_DUBBO || gatewayApiInfo.getRouteType() == Consts.GATEWAY_ROUTE_TYPE_DUBBO) {
            //load dubbo doc from anno
            String dubboServicePath = StringUtils.join(new String[]{gatewayApiInfo.getServiceName(), gatewayApiInfo.getServiceGroup(), gatewayApiInfo.getServiceVersion(), gatewayApiInfo.getMethodName()}, ':');
            LOGGER.info("dubboServicePath is :{}",dubboServicePath);
            Api dubboApi = apiMapper.getApiInfoByUrl(dubboServicePath, 0);
            if (Objects.nonNull(dubboApi)) {
                LOGGER.info("dubboServicePath dubboApi id is :{}",dubboApi.getDubboApiId());
                EoDubboApiInfo dubboApiInfo = dubboApiInfoMapper.selectByPrimaryKey(dubboApi.getDubboApiId());
                resultMap.put("dubboParam", dubboApiInfo.getMethodparaminfo());
                resultMap.put("dubboResp", dubboApiInfo.getResponse());
            } else {
                resultMap.put("dubboParam", "");
                resultMap.put("dubboResp", "");
            }
        }
        return Result.success(resultMap);
    }
}
