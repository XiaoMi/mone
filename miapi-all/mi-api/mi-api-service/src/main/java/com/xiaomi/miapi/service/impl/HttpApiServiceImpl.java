package com.xiaomi.miapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.bo.BatchImportApiBo;
import com.xiaomi.miapi.bo.MockServerInfo;
import com.xiaomi.miapi.pojo.*;
import com.xiaomi.miapi.util.Md5Utils;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.miapi.bo.GetApiBasicRequest;
import com.xiaomi.miapi.bo.HttpApiUpdateNotifyBo;
import com.xiaomi.miapi.dto.ManualHttpUpDTO;
import com.xiaomi.miapi.service.HttpApiService;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.mone.http.docs.core.beans.HttpApiCacheItem;
import com.xiaomi.mone.http.docs.core.beans.HttpLayerItem;
import com.xiaomi.mone.http.docs.core.beans.HttpModuleCacheItem;
import com.xiaomi.youpin.codegen.bo.ApiHeaderBo;
import org.apache.dubbo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Service
public class HttpApiServiceImpl implements HttpApiService {

    @Autowired
    RedisUtil redis;

    @Autowired
    ApiMapper apiMapper;

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

    @Autowired
    ApiServiceImpl apiService;

    @Autowired
    private HttpPushDataMapper httpPushDataMapper;

    @Autowired
    private ModuleNameDataMapper moduleMapper;
    @Autowired
    private ApiMockExpectMapper mockExpectMapper;

    @Autowired
    private MockServerInfo mockServerInfo;

    public static final Gson gson = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApiServiceImpl.class);

    private final ExecutorService pushDataPool = Executors.newCachedThreadPool();

    @Override
    @Transactional
    public Result<Boolean> addHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes, boolean randomGen) {

        api = apiService.checkAndFillApiInfo(api);
        //check if url exist
        Api oldApi = apiMapper.getApiInfoByUrlAndProject(api.getApiURI(), api.getApiRequestType(), api.getProjectID());
        if (oldApi != null) {
            return Result.fail(CommonError.APIUrlAlreadyExist);
        }
        api.setDubboApiId(0);
        api.setGatewayApiId(0);
        if (StringUtils.isEmpty(api.getApiRequestRaw())) {
            api.setApiRequestRaw("");
        }
        if (api.getApiRequestParamType() == Consts.JSON_DATA_TYPE) {
            if (randomGen) {
                api.setApiRequestRaw(gson.toJson(mockService.parseStructToJson(apiRequestParam, false)));
            } else {
                api.setApiRequestRaw(gson.toJson(mockService.parseStructToJsonByDefault(apiRequestParam)));
            }
        } else {
            api.setApiRequestRaw(api.getApiRequestRaw());
        }
        if (StringUtils.isEmpty(api.getApiResponseRaw())) {
            api.setApiResponseRaw("");
        }
        if (api.getApiResponseParamType() == Consts.JSON_DATA_TYPE) {
            if (randomGen) {
                api.setApiResponseRaw(gson.toJson(mockService.parseStructToJson(apiResultParam, false)));
            } else {
                api.setApiResponseRaw(gson.toJson(mockService.parseStructToJsonByDefault(apiResultParam)));
            }
        } else {
            api.setApiResponseRaw(api.getApiResponseRaw());
        }

        Map<String, Object> cache = new HashMap<String, Object>();
        cache.put("baseInfo", api);
        cache.put("headerInfo", JSONArray.parseArray(apiHeader));
        cache.put("requestInfo", JSONArray.parseArray(apiRequestParam));
        cache.put("resultInfo", JSONArray.parseArray(apiResultParam));
        if (!apiErrorCodes.equals(Consts.IMPORT_SWAGGER_FLAG)) {
            cache.put("errorCodes", JSONArray.parseArray(apiErrorCodes));
        }
        Integer result = apiMapper.addApi(api);
        if (result > 0) {
            ApiCache apiCache = new ApiCache();
            apiCache.setApiID(api.getApiID());
            apiCache.setApiJson(gson.toJson(cache));
            apiCache.setGroupID(api.getGroupID());
            apiCache.setProjectID(api.getProjectID());
            apiCache.setStarred(api.getStarred());
            apiCache.setUpdateUsername(api.getUpdateUsername());
            if (apiCacheMapper.addApiCache(apiCache) < 1) {
                return Result.fail(CommonError.UnknownError);
            }
            mockService.updateHttpApiMockData(api.getUpdateUsername(), null, api.getApiID(), "", "", Consts.FORM_DATA_TYPE, "系统全局ApiMock", api.getProjectID(), api.getApiResponseRaw(), 1, true, false, "");
            try {
                List<ApiHeaderBo> headerList = gson.fromJson(apiHeader, new TypeToken<List<ApiHeaderBo>>() {
                }.getType());
                apiService.codeGen(api, api.getApiRequestParamType(), apiRequestParam, api.getApiResponseParamType(), apiResultParam, headerList, false);
            } catch (Exception e) {
                LOGGER.warn("生成代码失败", e);
            }

            String updateMsg = "add http api";
            if (StringUtils.isNotEmpty(api.getUpdateMsg())) {
                updateMsg = api.getUpdateMsg();
            }
            if (apiErrorCodes.equals(Consts.IMPORT_SWAGGER_FLAG)) {
                updateMsg = Consts.IMPORT_SWAGGER_FLAG;
            }
            apiService.recordApiHistory(api, apiCache.getApiJson(), updateMsg);

            recordService.doRecord(api, JSON.toJSONString(cache), "添加HTTP接口", "添加HTTP接口:" + api.getApiName(), ProjectOperationLog.OP_TYPE_ADD);

            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    @Transactional
    public Result<Boolean> batchAddHttpApi(String apiEnv, List<BatchImportApiBo> bos) {
        bos.forEach(bo -> {
            List<HttpApiCacheItem> httpItems = new ArrayList<>(bo.getApiNames().size());

            bo.getApiNames().forEach(apiMethodName -> {
                try {
                    GetApiBasicRequest getApiBasicRequest = new GetApiBasicRequest();
                    getApiBasicRequest.setApiName(apiMethodName);
                    getApiBasicRequest.setIp(bo.getIp());
                    getApiBasicRequest.setPort(bo.getPort());
                    getApiBasicRequest.setModuleClassName(bo.getModuleClassName());

                    HttpApiCacheItem item = getHttpApiDetailFromRemote(getApiBasicRequest);
                    httpItems.add(item);
                } catch (Exception e) {
                    LOGGER.warn("batchAddHttpApi error:{}", e.getMessage());
                }
            });
            for (HttpApiCacheItem item :
                    httpItems) {
                try {
                    Api api = new Api();
                    api.setApiEnv(apiEnv);
                    api = apiService.checkAndFillApiInfo(api);
                    api.setGroupID(bo.getGroupID());
                    api.setProjectID(bo.getProjectID());
                    api.setApiRequestType(ApiServiceImpl.transferStrMethod2Num(item.getApiMethod()));
                    api.setApiName(item.getApiName());
                    api.setApiURI(item.getApiPath());
                    api.setApiDesc(item.getDescription());
                    api.setApiProtocol(Consts.HTTP_API_TYPE);
                    api.setApiRequestParamType(judgeParamType(item.getParamsLayerList()));
                    api.setApiResponseParamType(Consts.JSON_DATA_TYPE);
                    api.setUpdateUsername(bo.getUpdateUserName());
                    api.setHttpControllerPath(bo.getModuleClassName());
                    List<HttpLayerItem> respLayer = new ArrayList<>();
                    respLayer.add(item.getResponseLayer());
                    Api oldApi = apiMapper.getApiInfoByUrlAndProject(item.getApiPath(), ApiServiceImpl.transferStrMethod2Num(item.getApiMethod()), bo.getProjectID());
                    if (Objects.isNull(oldApi)) {
                        addHttpApi(api, "", gson.toJson(item.getParamsLayerList()), gson.toJson(respLayer), "", false);
                    } else if (bo.getForceUpdate()) {
                        api.setApiID(oldApi.getApiID());
                        editHttpApi(api, "", gson.toJson(item.getParamsLayerList()), gson.toJson(respLayer), "", true);
                    }
                } catch (Exception e) {
                    LOGGER.warn("batchAddHttpApi error:{}", e.getMessage());
                }
            }
        });
        return Result.success(true);
    }

    @Override
    @Transactional
    public Result<Boolean> editHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes, boolean doRecord) {
        api = apiService.checkAndFillApiInfo(api);
        Api oldApi = apiMapper.getApiInfo(api.getProjectID(), api.getApiID());
        if (Objects.isNull(oldApi)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        if (!oldApi.getApiURI().equals(api.getApiURI())) {
            Api newUrlApi = apiMapper.getApiInfoByUrlAndProject(api.getApiURI(), api.getApiRequestType(), api.getProjectID());
            if (newUrlApi != null) {
                return Result.fail(CommonError.APIUrlAlreadyExist);
            }
        }
        Date date = new Date();
        Timestamp updateTime = new Timestamp(date.getTime());
        api.setApiUpdateTime(updateTime);
        if (StringUtils.isEmpty(oldApi.getApiEnv())) {
            api.setApiEnv("staging");
        }
        if (StringUtils.isEmpty(api.getApiRequestRaw())) {
            api.setApiRequestRaw("");
        }
        if (api.getApiRequestParamType() == Consts.JSON_DATA_TYPE) {
            api.setApiRequestRaw(gson.toJson(mockService.parseStructToJson(apiRequestParam, false)));
        } else {
            api.setApiRequestRaw(api.getApiRequestRaw());
        }

        if (StringUtils.isEmpty(api.getApiResponseRaw())) {
            api.setApiResponseRaw("");
        }
        if (api.getApiResponseParamType() == Consts.JSON_DATA_TYPE) {
            api.setApiResponseRaw(gson.toJson(mockService.parseStructToJson(apiResultParam, false)));
        } else {
            api.setApiResponseRaw(api.getApiResponseRaw());
        }

        Map<String, Object> cache = new HashMap<>();
        cache.put("baseInfo", api);
        if (StringUtils.isNotEmpty(apiHeader)) {
            cache.put("headerInfo", JSONArray.parseArray(apiHeader));
        }
        cache.put("requestInfo", JSONArray.parseArray(apiRequestParam));
        cache.put("resultInfo", JSONArray.parseArray(apiResultParam));

        if (StringUtils.isNotEmpty(apiErrorCodes)) {
            cache.put("apiErrorCodes", JSONArray.parseArray(apiErrorCodes));
        }
        Integer result = apiMapper.updateApi(api);
        if (result > 0) {
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

            ApiMockExpectExample example = new ApiMockExpectExample();
            example.createCriteria().andApiIdEqualTo(api.getApiID()).andIsDefaultEqualTo(true);
            List<ApiMockExpect> expects = mockExpectMapper.selectByExample(example);
            if (Objects.nonNull(expects) && !expects.isEmpty()) {
                mockService.updateHttpApiMockData(api.getUpdateUsername(), expects.get(0).getId(), api.getApiID(), "", "", 0, "系统全局ApiMock", api.getProjectID(), api.getApiResponseRaw(), 1, true, false, "");
            }
            try {
                List<ApiHeaderBo> headerList = gson.fromJson(apiHeader, new TypeToken<List<ApiHeaderBo>>() {
                }.getType());
                apiService.codeGen(api, api.getApiRequestParamType(), apiRequestParam, api.getApiResponseParamType(), apiResultParam, headerList, true);
            } catch (Exception e) {
                LOGGER.warn("更新生成代码失败", e);
            }
            if (doRecord) {
                String updateMsg = "update http api";
                if (StringUtils.isNotEmpty(api.getUpdateMsg())) {
                    updateMsg = api.getUpdateMsg();
                }
                apiService.recordApiHistory(api, apiCache.getApiJson(), updateMsg);
            }
            recordService.doRecord(api, JSON.toJSONString(cache), "[快速保存]修改接口", "[快速保存]修改接口:" + api.getApiName(), ProjectOperationLog.OP_TYPE_UPDATE);
            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    public Map<String, Object> getHttpApi(String username, Integer projectID, Integer apiID) {

        Map<String, Object> result = apiMapper.getApi(projectID, apiID);
        Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
        if (apiJson != null && !apiJson.isEmpty()) {
            Map<String, Object> baseInfo = (JSONObject) apiJson.get("baseInfo");
            baseInfo.put("groupID", result.get("groupID"));
            baseInfo.put("projectID", result.get("projectID"));
            baseInfo.put("apiID", result.get("apiID"));
            baseInfo.putIfAbsent("apiEnv", "staging");
            ApiGroup apiGroup = apiGroupMapper.getGroupByID(Integer.parseInt(result.get("groupID").toString()));
            if (apiGroup == null) {
                return null;
            }
            String groupName = apiGroup.getGroupName();
            baseInfo.put("groupName", groupName);
            apiJson.put("baseInfo", baseInfo);
            ApiRequestExpExample reqExample = new ApiRequestExpExample();
            reqExample.createCriteria().andApiIdEqualTo(apiID);
            List<ApiRequestExp> reqExpList = requestExpMapper.selectByExampleWithBLOBs(reqExample);
            apiJson.put("reqExpList", reqExpList);

            ApiResponseExpExample respExample = new ApiResponseExpExample();
            respExample.createCriteria().andApiIdEqualTo(apiID);
            List<ApiResponseExp> respExpList = responseExpMapper.selectByExampleWithBLOBs(respExample);
            apiJson.put("respExpList", respExpList);

            Map<String, Object> mockInfo = new HashMap<>();
            String apiURI = baseInfo.getOrDefault("apiURI", "").toString();
            String uriMd5 = Md5Utils.getMD5(apiURI);
            String uri = apiURI.replaceAll("/", ":").replaceAll(" ", "");

            mockInfo.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT,  mockServerInfo.getMockServerAddr()+ Consts.HttpMockPrefix, uriMd5, uri));
            apiJson.put("mockInfo", mockInfo);
        }

        redis.recordRecently10Apis(username, apiID);
        return apiJson;
    }

    @Override
    public Result<Map<String, Object>> getAllHttpModulesInfo(String serviceName, String ip) {
        Map<String, Object> resultMap = new HashMap<>();
        ModuleNameDataExample moduleExp = new ModuleNameDataExample();
        moduleExp.createCriteria().andModuleNameEqualTo(serviceName);
        List<ModuleNameData> moduleList = moduleMapper.selectByExample(moduleExp);
        if (moduleList == null || moduleList.isEmpty()) {
            return Result.success(resultMap);
        }
        List<String> ipAndPortList = new ArrayList<>();
        moduleList.forEach(instance -> ipAndPortList.add(instance.getAddress()));
        if (ipAndPortList.size() == 0) {
            return Result.fail(CommonError.HttpApiForIpPortNotFound);
        }
        List<HttpModuleCacheItem> list;
        try {
            HttpPushDataExample example = new HttpPushDataExample();
            if (null == ip || ip.isEmpty()) {
                example.createCriteria().andAddressEqualTo(ipAndPortList.get(0));
            } else {
                example.createCriteria().andAddressEqualTo(ip);
            }
            List<HttpPushData> httpPushDataList = httpPushDataMapper.selectByExampleWithBLOBs(example);
            if (httpPushDataList == null || httpPushDataList.size() == 0) {
                return Result.fail(CommonError.HttpApiForIpPortNotFound);
            }
            String httpApiModuleListAndApiInfoStr = httpPushDataList.get(0).getHttpapimodulelistandapiinfo();
            list = gson.fromJson(httpApiModuleListAndApiInfoStr, new TypeToken<List<HttpModuleCacheItem>>() {
            }.getType());
        } catch (Exception e) {
            LOGGER.error("get http doc error", e);
            return Result.fail(CommonError.UnknownError);
        }
        resultMap.put("list", list);
        resultMap.put("ipAndPort", ipAndPortList);
        return Result.success(resultMap);
    }

    @Override
    public Result<Boolean> manualUpdateHttpApi(ManualHttpUpDTO httpUpDTO) {
        Api api = apiMapper.getApiInfo(httpUpDTO.getProjectID(), httpUpDTO.getApiID());
        if (Objects.isNull(api.getHttpControllerPath()) || api.getHttpControllerPath().isEmpty()) {
            return Result.fail(CommonError.ApiMustBeLoaded);
        }
        ModuleNameDataExample moduleExp = new ModuleNameDataExample();
        moduleExp.createCriteria().andModuleNameEqualTo(api.getHttpControllerPath());
        List<ModuleNameData> moduleList = moduleMapper.selectByExample(moduleExp);
        if (moduleList == null || moduleList.isEmpty()) {
            return Result.fail(CommonError.ServiceMustRun);
        }
        String[] ipAndPort = moduleList.get(0).getAddress().split(":");
        GetApiBasicRequest getApiBasicRequest = new GetApiBasicRequest();
        getApiBasicRequest.setApiName(api.getApiURI().substring(api.getApiURI().lastIndexOf("/") + 1));
        getApiBasicRequest.setIp(ipAndPort[0]);
        getApiBasicRequest.setPort(Integer.valueOf(ipAndPort[1]));
        getApiBasicRequest.setModuleClassName(api.getHttpControllerPath());

        HttpApiCacheItem item = getHttpApiDetailFromRemote(getApiBasicRequest);
        if (Objects.isNull(item)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        Api oldApi = apiMapper.getApiInfoByUrlAndProject(item.getApiPath(), ApiServiceImpl.transferStrMethod2Num(item.getApiMethod()), httpUpDTO.getProjectID());
        if (Objects.isNull(oldApi)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        if (item.getApiMethodName() != null && !item.getApiMethodName().isEmpty()) {
            oldApi.setApiName(item.getApiMethodName());
        } else {
            oldApi.setApiName(item.getApiName());
        }
        oldApi.setApiDesc(item.getDescription());
        oldApi.setUpdateUsername(httpUpDTO.getOpUsername());
        oldApi.setUpdateMsg(httpUpDTO.getUpdateMsg());
        List<HttpLayerItem> respLayer = new ArrayList<>();
        respLayer.add(item.getResponseLayer());
        editHttpApi(oldApi, "", gson.toJson(item.getParamsLayerList()), gson.toJson(respLayer), "", true);
        return Result.success(true);
    }

    @Override
    public void httpApiUpdateNotify(HttpApiUpdateNotifyBo bo) {
        pushDataPool.submit(() -> {
            HttpModuleCacheItem httpModuleCacheItem;
            int retry = 0;
            while (retry < 3) {
                try {
                    HttpPushDataExample example = new HttpPushDataExample();
                    example.createCriteria().andAddressEqualTo(bo.getIp() + ":" + bo.getPort());
                    List<HttpPushData> httpPushDataList = httpPushDataMapper.selectByExampleWithBLOBs(example);
                    if (httpPushDataList == null || httpPushDataList.size() == 0) {
                        retry++;
                        Thread.sleep(3000);
                        continue;
                    }
                    String httpApiModuleInfoStr = httpPushDataList.get(0).getHttpapimoduleinfo();

                    Map<String, HttpModuleCacheItem> apiModulesCache = gson.fromJson(httpApiModuleInfoStr, new TypeToken<Map<String, HttpModuleCacheItem>>() {
                    }.getType());
                    httpModuleCacheItem = apiModulesCache.get(bo.getApiController());

                    List<HttpApiCacheItem> httpItems = new ArrayList<>(httpModuleCacheItem.getHttpModuleApiList().size());

                    httpModuleCacheItem.getHttpModuleApiList().forEach(httpApiCacheItem -> {
                        GetApiBasicRequest getApiBasicRequest = new GetApiBasicRequest();
                        getApiBasicRequest.setApiName(httpApiCacheItem.getApiName());
                        getApiBasicRequest.setIp(bo.getIp());
                        getApiBasicRequest.setPort(bo.getPort());
                        getApiBasicRequest.setModuleClassName(bo.getApiController());

                        HttpApiCacheItem item = getHttpApiDetailFromRemote(getApiBasicRequest);
                        httpItems.add(item);
                    });
                    for (HttpApiCacheItem item :
                            httpItems) {
                        Api oldApi = apiMapper.getApiInfoByUrl(item.getApiPath(), ApiServiceImpl.transferStrMethod2Num(item.getApiMethod()));
                        if (Objects.nonNull(oldApi)) {
                            oldApi.setApiName(item.getApiName());
                            oldApi.setApiDesc(item.getDescription());
                            oldApi.setUpdateUsername(bo.getOpUsername());
                            List<HttpLayerItem> respLayer = new ArrayList<>();
                            respLayer.add(item.getResponseLayer());
                            editHttpApi(oldApi, "", gson.toJson(item.getParamsLayerList()), gson.toJson(respLayer), "", true);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("get dubbo doc error", e);
                    retry++;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }
                break;
            }
        });

    }

    public HttpApiCacheItem getHttpApiDetailFromRemote(GetApiBasicRequest httpApiRequestBo) {
        HttpApiCacheItem apiCacheItem = null;
        try {
            HttpPushDataExample example = new HttpPushDataExample();
            example.createCriteria().andAddressEqualTo(httpApiRequestBo.getIp() + ":" + httpApiRequestBo.getPort());
            List<HttpPushData> httpPushDataList = httpPushDataMapper.selectByExampleWithBLOBs(example);
            if (httpPushDataList == null || httpPushDataList.size() == 0) {
                return null;
            }
            String detailParamInfo = httpPushDataList.get(0).getHttpapiparamsresponseinfo();
            Map<String, HttpApiCacheItem> apiParamsAndRespCache = gson.fromJson(detailParamInfo, new TypeToken<Map<String, HttpApiCacheItem>>() {
            }.getType());
            apiCacheItem = apiParamsAndRespCache.get(httpApiRequestBo.getModuleClassName() + "." + httpApiRequestBo.getApiName());
        } catch (Exception e) {
            LOGGER.error("getHttpApiDetailFromRemote error", e);
        }
        return apiCacheItem;
    }

    private int judgeParamType(List<HttpLayerItem> httpLayerItems) {
        if (Objects.isNull(httpLayerItems) || httpLayerItems.isEmpty()) {
            return Consts.FORM_DATA_TYPE;
        }
        boolean isJson = false;
        for (HttpLayerItem item :
                httpLayerItems) {
            if (Objects.isNull(item.getChildList())) {
                continue;
            }
            if (!item.getChildList().isEmpty()) {
                isJson = true;
                break;
            }
        }
        if (isJson) {
            return Consts.JSON_DATA_TYPE;
        }
        return Consts.FORM_DATA_TYPE;
    }
}
