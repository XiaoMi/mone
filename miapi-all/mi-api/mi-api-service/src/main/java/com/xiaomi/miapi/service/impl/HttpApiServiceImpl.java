package com.xiaomi.miapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.common.pojo.*;
import com.xiaomi.miapi.util.Md5Utils;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.common.bo.BatchImportHttpApiBo;
import com.xiaomi.miapi.common.bo.GetHttpApiRequestBo;
import com.xiaomi.miapi.common.bo.HttpApiUpdateNotifyBo;
import com.xiaomi.miapi.common.dto.ManualHttpUpDTO;
import com.xiaomi.miapi.service.HttpApiService;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.mone.dubbo.docs.core.beans.ModuleCacheItem;
import com.xiaomi.mone.http.docs.core.beans.HttpApiCacheItem;
import com.xiaomi.mone.http.docs.core.beans.HttpLayerItem;
import com.xiaomi.mone.http.docs.core.beans.HttpModuleCacheItem;
import com.xiaomi.mone.http.docs.providers.IHttpDocProvider;
import com.xiaomi.youpin.codegen.bo.ApiHeaderBo;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.address.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
    ApiIndexMapper apiIndexMapper;

    @Autowired
    IndexInfoMapper indexInfoMapper;

    @Autowired
    ApiHistoryRecordMapper historyRecordMapper;

    @Autowired
    MockService mockService;

    @Autowired
    ApiServiceImpl apiService;

    @DubboReference(registry = "stRegistry", lazy = true, check = false, group = "", interfaceClass = IHttpDocProvider.class, timeout = 1000, parameters = {"router", "address"}, retries = 0)
    private IHttpDocProvider httpDocProvider;

    @Resource(name = "nacosNamingSt")
    private NacosNaming nacosNamingSt;

    @Autowired
    private ApiMockExpectMapper mockExpectMapper;

    public static final Gson gson = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApiServiceImpl.class);


    /**
     * 新增http接口
     */
    @Override
    @Transactional
    public Result<Boolean> addHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes, boolean randomGen) {

        api = apiService.checkAndFillApiInfo(api);
        //校验url是否存在
        Api oldApi = apiMapper.getApiInfoByUrl(api.getApiURI(), api.getApiRequestType());
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
            //创建http类型接口系统默认期望
            mockService.updateHttpApiMockData(api.getUpdateUsername(), null, api.getApiID(), "", "", Consts.FORM_DATA_TYPE, "系统全局ApiMock", api.getProjectID(), api.getApiResponseRaw(), 1, true, false, "");
            //示例代码生成
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
            //记录历史版本
            apiService.recordApiHistory(api, apiCache.getApiJson(), updateMsg);

            //操作记录
            recordService.doRecord(api, JSON.toJSONString(cache), "添加HTTP接口", "添加HTTP接口:" + api.getApiName(), ProjectOperationLog.OP_TYPE_ADD);

            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    @Transactional
    public Result<Boolean> batchAddHttpApi(String apiEnv, List<BatchImportHttpApiBo> bos) {
        bos.forEach(bo -> {
            List<HttpApiCacheItem> httpItems = new ArrayList<>(bo.getApiNames().size());

            bo.getApiNames().forEach(apiMethodName -> {
                try {
                    GetHttpApiRequestBo getHttpApiRequestBo = new GetHttpApiRequestBo();
                    getHttpApiRequestBo.setApiName(apiMethodName);
                    getHttpApiRequestBo.setIp(bo.getIp());
                    getHttpApiRequestBo.setPort(bo.getPort());
                    getHttpApiRequestBo.setModuleClassName(bo.getHttpModuleClassName());

                    HttpApiCacheItem item = getHttpApiDetailFromRemote(getHttpApiRequestBo);
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
                    api.setHttpControllerPath(bo.getHttpModuleClassName());
                    List<HttpLayerItem> respLayer = new ArrayList<>();
                    respLayer.add(item.getResponseLayer());
                    //唯一关联 path& request type
                    Api oldApi = apiMapper.getApiInfoByUrl(item.getApiPath(), ApiServiceImpl.transferStrMethod2Num(item.getApiMethod()));
                    if (Objects.isNull(oldApi)) {
                        //不存在则添加；
                        //todo cache中字段需要选择性更新,或者这里带上errorCode
                        addHttpApi(api, "", gson.toJson(item.getParamsLayerList()), gson.toJson(respLayer), "", false);
                    } else if (bo.getForceUpdate()) {
                        //强制更新
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

    /**
     * 修改接口
     */
    @Override
    @Transactional
    public Result<Boolean> editHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes, boolean doRecord) {
        api = apiService.checkAndFillApiInfo(api);
        Api oldApi = apiMapper.getApiInfo(api.getProjectID(), api.getApiID());
        if (Objects.isNull(oldApi)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        //校验url是否存在
        if (!oldApi.getApiURI().equals(api.getApiURI())) {
            Api newUrlApi = apiMapper.getApiInfoByUrl(api.getApiURI(), api.getApiRequestType());
            if (newUrlApi != null) {
                return Result.fail(CommonError.APIUrlAlreadyExist);
            }
        }
        Date date = new Date();
        Timestamp updateTime = new Timestamp(date.getTime());
        api.setApiUpdateTime(updateTime);
        //老的接口默认测试环境
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

            //更新http类型接口系统默认期望
            ApiMockExpectExample example = new ApiMockExpectExample();
            example.createCriteria().andApiIdEqualTo(api.getApiID()).andIsDefaultEqualTo(true);
            List<ApiMockExpect> expects = mockExpectMapper.selectByExample(example);
            if (Objects.nonNull(expects) && !expects.isEmpty()) {
                mockService.updateHttpApiMockData(api.getUpdateUsername(), expects.get(0).getId(), api.getApiID(), "", "", 0, "系统全局ApiMock", api.getProjectID(), api.getApiResponseRaw(), 1, true, false, "");
            }

            //示例代码生成
            try {
                List<ApiHeaderBo> headerList = gson.fromJson(apiHeader, new TypeToken<List<ApiHeaderBo>>() {
                }.getType());
                apiService.codeGen(api, api.getApiRequestParamType(), apiRequestParam, api.getApiResponseParamType(), apiResultParam, headerList, true);
            } catch (Exception e) {
                LOGGER.warn("更新生成代码失败", e);
            }
            //记录历史按本
            if (doRecord) {
                String updateMsg = "update http api";
                if (StringUtils.isNotEmpty(api.getUpdateMsg())) {
                    updateMsg = api.getUpdateMsg();
                }
                //记录历史版本
                apiService.recordApiHistory(api, apiCache.getApiJson(), updateMsg);
            }
            //操作记录
            recordService.doRecord(api, JSON.toJSONString(cache), "[快速保存]修改接口", "[快速保存]修改接口:" + api.getApiName(), ProjectOperationLog.OP_TYPE_UPDATE);
            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    /**
     * 获取http接口详情
     */
    @Override
    public Map<String, Object> getHttpApi(Integer userId, Integer projectID, Integer apiID) {

        Map<String, Object> result = apiMapper.getApi(projectID, apiID);
        Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
        if (apiJson != null && !apiJson.isEmpty()) {
            Map<String, Object> baseInfo = (JSONObject) apiJson.get("baseInfo");
            baseInfo.put("groupID", result.get("groupID"));
            baseInfo.put("projectID", result.get("projectID"));
            baseInfo.put("apiID", result.get("apiID"));
            baseInfo.putIfAbsent("apiEnv", "staging");
            String groupName = apiGroupMapper.getGroupByID(Integer.parseInt(result.get("groupID").toString())).getGroupName();
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

            mockInfo.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT, Consts.MockUrlPrefix + Consts.HttpMockPrefix, uriMd5, uri));
            apiJson.put("mockInfo", mockInfo);
        }

        redis.recordRecently10Apis(userId, apiID);
        return apiJson;
    }

    /**
     * 获取http接口基本信息
     */
    @Override
    public Map<String, Object> getBasicHttpApi(Integer projectID, Integer apiID) {
        Map<String, Object> result = apiMapper.getApi(projectID, apiID);
        Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
        if (apiJson != null && !apiJson.isEmpty()) {
            Map<String, Object> baseInfo = (JSONObject) apiJson.get("baseInfo");
            baseInfo.put("apiID", result.get("apiID"));
            baseInfo.putIfAbsent("apiEnv", "staging");
            apiJson.put("baseInfo", baseInfo);
        }
        return apiJson;
    }

    @Override
    public Result<Map<String, Object>> getAllHttpModulesInfo(String serviceName) throws NacosException {
        Map<String, Object> resultMap = new HashMap<>();
        List<Instance> instanceList;
        instanceList = nacosNamingSt.getAllInstances(serviceName);

        if (instanceList.isEmpty()) {
            return Result.success(resultMap);
        }
        Address address = new Address(instanceList.get(0).getIp(), instanceList.get(0).getPort());
        RpcContext.getContext().setObjectAttachment("address", address);

        String ipAndPort = instanceList.get(0).getIp() + ":" + instanceList.get(0).getPort();
        List<HttpModuleCacheItem> list;

        try {
            list = httpDocProvider.httpApiModuleListAndApiInfo();
        } catch (Exception e) {
            if (e instanceof RpcException) {
                if (((RpcException) e).getCode() == 6) {
                    return Result.fail(CommonError.HttpApiForIpPortNotFound);
                }
            }
            LOGGER.error("get http doc error", e);
            return Result.fail(CommonError.UnknownError);
        }
        resultMap.put("list", list);
        resultMap.put("ipAndPort", ipAndPort);
        return Result.success(resultMap);
    }

    @Override
    public Result<Boolean> manualUpdateHttpApi(ManualHttpUpDTO httpUpDTO) throws NacosException {
        Api api = apiMapper.getApiInfo(httpUpDTO.getProjectID(), httpUpDTO.getApiID());
        if (Objects.isNull(api.getHttpControllerPath()) || api.getHttpControllerPath().isEmpty()) {
            return Result.fail(CommonError.HttpApiMustBeLoaded);
        }
        List<Instance> instanceList;
        instanceList = nacosNamingSt.getAllInstances(api.getHttpControllerPath());
        if (instanceList.isEmpty()) {
            return Result.fail(CommonError.ServiceMustRun);
        }
        instanceList = instanceList.stream().filter(Instance::isHealthy).collect(Collectors.toList());
        if (instanceList.isEmpty()) {
            return Result.fail(CommonError.ServiceMustRun);
        }

        //获取方法集
        GetHttpApiRequestBo getHttpApiRequestBo = new GetHttpApiRequestBo();
        getHttpApiRequestBo.setApiName(api.getApiURI().substring(api.getApiURI().lastIndexOf("/") + 1));
        getHttpApiRequestBo.setIp(instanceList.get(0).getIp());
        getHttpApiRequestBo.setPort(instanceList.get(0).getPort());
        getHttpApiRequestBo.setModuleClassName(api.getHttpControllerPath());

        HttpApiCacheItem item = getHttpApiDetailFromRemote(getHttpApiRequestBo);
        if (Objects.isNull(item)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        //唯一关联 path& request type
        Api oldApi = apiMapper.getApiInfoByUrl(item.getApiPath(), ApiServiceImpl.transferStrMethod2Num(item.getApiMethod()));
        if (Objects.isNull(oldApi)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        //更新
        oldApi.setApiName(item.getApiName());
        oldApi.setApiDesc(item.getDescription());
        oldApi.setUpdateUsername(httpUpDTO.getOpUsername());
        oldApi.setUpdateMsg(httpUpDTO.getUpdateMsg());
        List<HttpLayerItem> respLayer = new ArrayList<>();
        respLayer.add(item.getResponseLayer());
        //todo deal with header and error_code
        editHttpApi(oldApi, "", gson.toJson(item.getParamsLayerList()), gson.toJson(respLayer), "", true);
        return Result.success(true);
    }

    /**
     * http接口自动更新通知
     *
     * @param bo
     * @return
     * @throws InterruptedException
     */
    @Override
    public Result<Boolean> httpApiUpdateNotify(HttpApiUpdateNotifyBo bo) throws InterruptedException {
        //获取方法集
        String docInfo;
        HttpModuleCacheItem httpModuleCacheItem;
        int retry = 0;
        while (retry < 3) {
            try {
                Address address = new Address(bo.getIp(), bo.getPort());
                RpcContext.getContext().setObjectAttachment("address", address);
                if (StringUtils.isNotEmpty(bo.getEnv()) && bo.getEnv().equals("staging")) {
                    docInfo = httpDocProvider.httpApiModuleInfo(bo.getApiController());
                } else {
                    return Result.fail(CommonError.UnknownError);
                }
                httpModuleCacheItem = gson.fromJson(docInfo, new TypeToken<ModuleCacheItem>() {
                }.getType());

                List<HttpApiCacheItem> httpItems = new ArrayList<>(httpModuleCacheItem.getHttpModuleApiList().size());

                httpModuleCacheItem.getHttpModuleApiList().forEach(httpApiCacheItem -> {
                    GetHttpApiRequestBo getHttpApiRequestBo = new GetHttpApiRequestBo();
                    getHttpApiRequestBo.setApiName(httpApiCacheItem.getApiName());
                    getHttpApiRequestBo.setIp(bo.getIp());
                    getHttpApiRequestBo.setPort(bo.getPort());
                    getHttpApiRequestBo.setModuleClassName(bo.getApiController());

                    HttpApiCacheItem item = getHttpApiDetailFromRemote(getHttpApiRequestBo);
                    httpItems.add(item);
                });
                for (HttpApiCacheItem item :
                        httpItems) {
                    //唯一关联 path& request type
                    Api oldApi = apiMapper.getApiInfoByUrl(item.getApiPath(), ApiServiceImpl.transferStrMethod2Num(item.getApiMethod()));
                    if (Objects.isNull(oldApi)) {
                        //不存在则跳过；
                    } else {
                        //更新
                        oldApi.setApiName(item.getApiName());
                        oldApi.setApiDesc(item.getDescription());
                        oldApi.setUpdateUsername(bo.getOpUsername());
                        List<HttpLayerItem> respLayer = new ArrayList<>();
                        respLayer.add(item.getResponseLayer());
                        //todo deal with header and error_code
                        editHttpApi(oldApi, "", gson.toJson(item.getParamsLayerList()), gson.toJson(respLayer), "", true);
                    }
                }

            } catch (Exception e) {
                if (e instanceof RpcException) {
                    if (((RpcException) e).getCode() == 6) {
                        retry++;
                        //等10s再重试
                        Thread.sleep(10000);
                        continue;
                    }
                }
                LOGGER.warn("get dubbo doc error", e);
            }
            break;
        }
        return Result.success(true);
    }

    public HttpApiCacheItem getHttpApiDetailFromRemote(GetHttpApiRequestBo httpApiRequestBo) {
        Address address = new Address(httpApiRequestBo.getIp(), httpApiRequestBo.getPort());
        RpcContext.getContext().setObjectAttachment("address", address);
        HttpApiCacheItem apiCacheItem = null;
        try {
            String apiCacheItemStr;
            apiCacheItemStr = httpDocProvider.httpApiParamsResponseInfo(httpApiRequestBo.getModuleClassName() + "." + httpApiRequestBo.getApiName());
            apiCacheItem = gson.fromJson(apiCacheItemStr, HttpApiCacheItem.class);
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
