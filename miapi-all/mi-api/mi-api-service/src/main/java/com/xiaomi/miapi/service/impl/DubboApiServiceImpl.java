package com.xiaomi.miapi.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.bo.*;
import com.xiaomi.miapi.dto.ManualDubboUpDTO;
import com.xiaomi.miapi.pojo.*;
import com.xiaomi.miapi.util.Md5Utils;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.miapi.service.DubboApiService;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.mone.dubbo.docs.core.beans.ModuleCacheItem;
import org.apache.dubbo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Service
public class DubboApiServiceImpl implements DubboApiService {

    @Autowired
    RedisUtil redis;

    @Autowired
    ApiMapper apiMapper;

    @Autowired
    ApiCacheMapper apiCacheMapper;

    @Autowired
    EoDubboApiInfoMapper dubboApiInfoMapper;

    @Autowired
    ApiGroupMapper apiGroupMapper;

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
    private DubboPushDataMapper dubboPushDataMapper;

    @Resource(name = "nacosNamingSt")
    private NacosNaming nacosNamingSt;

    @Resource(name = "nacosNamingOl")
    private NacosNaming nacosNamingOl;

    @Autowired
    private ModuleNameDataMapper moduleMapper;
    @Autowired
    private ApiMockExpectMapper mockExpectMapper;

    @Autowired
    private MockServerInfo mockServerInfo;

    private String DEFAULT_NAMESPACE = "";

    public static final Gson gson = new Gson();

    /**
     * the thread pool to process data push
     */
    private final ExecutorService pushDataPool = Executors.newCachedThreadPool();

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboApiServiceImpl.class);


    @Override
    public Result<Map<String, Object>> getDubboApiDetail(String username, Integer projectID, Integer apiID) {
        Map<String, Object> map = new HashMap<>();
        Api api = apiMapper.getApiInfo(projectID, apiID);
        if (null == api) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        EoDubboApiInfo dubboApiInfo = dubboApiInfoMapper.selectByPrimaryKey(api.getDubboApiId());
        map.put("dubboApiBaseInfo", dubboApiInfo);
        if (StringUtils.isEmpty(api.getApiEnv())) {
            map.put("apiEnv", "staging");
        } else {
            map.put("apiEnv", api.getApiEnv());
        }
        map.put("updateUsername", api.getUpdateUsername());
        map.put("projectID", api.getProjectID());
        map.put("groupID", api.getGroupID());
        map.put("apiNoteType", api.getApiNoteType());
        map.put("apiRemark", api.getApiRemark());
        map.put("apiDesc", dubboApiInfo.getDescription());
        map.put("mavenAddr", api.getMavenAddr());
        map.put("apiStatus", api.getApiStatus());
        map.put("name", api.getApiName());
        String groupName = apiGroupMapper.getGroupByID(api.getGroupID()).getGroupName();
        map.put("groupName", groupName);

        ApiRequestExpExample reqExample = new ApiRequestExpExample();
        reqExample.createCriteria().andApiIdEqualTo(apiID);
        List<ApiRequestExp> reqExpList = requestExpMapper.selectByExampleWithBLOBs(reqExample);
        map.put("reqExpList", reqExpList);

        ApiResponseExpExample respExample = new ApiResponseExpExample();
        respExample.createCriteria().andApiIdEqualTo(apiID);
        List<ApiResponseExp> respExpList = responseExpMapper.selectByExampleWithBLOBs(respExample);
        map.put("respExpList", respExpList);

        String md5Location = Md5Utils.getMD5(Consts.getServiceKey(dubboApiInfo.getApimodelclass(), dubboApiInfo.getApiversion(), dubboApiInfo.getApigroup()));

        map.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT, mockServerInfo.getMockServerAddr() + Consts.MockPrefix, md5Location, dubboApiInfo.getApiname()));

        redis.recordRecently10Apis(username, apiID);

        return Result.success(map);
    }

    @Override
    public Result<Map<String, Object>> getAllModulesInfo(String env, String serviceName, String ip) {
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
            return Result.fail(CommonError.DubboApiForIpPortNotFound);
        }
        List<ModuleCacheItem> list;
        try {
            DubboPushDataExample example = new DubboPushDataExample();
            //default return the first data
            if (null == ip || ip.isEmpty()) {
                example.createCriteria().andAddressEqualTo(ipAndPortList.get(0));
            } else {
                example.createCriteria().andAddressEqualTo(ip);
            }

            List<DubboPushData> dubboPushDataList = dubboPushDataMapper.selectByExampleWithBLOBs(example);
            if (dubboPushDataList == null || dubboPushDataList.size() == 0) {
                return Result.fail(CommonError.DubboApiForIpPortNotFound);
            }
            String apiDoc = dubboPushDataList.get(0).getApimodulelist();
            list = gson.fromJson(apiDoc, new TypeToken<List<ModuleCacheItem>>() {
            }.getType());
        } catch (Exception e) {
            LOGGER.error("get dubbo doc error", e);
            return Result.fail(CommonError.DubboApiForIpPortNotFound);
        }
        resultMap.put("list", list);
        resultMap.put("ipAndPort", ipAndPortList);
        return Result.success(resultMap);
    }

    @Override
    public Result<Set<ServiceName>> loadApiServices(String serviceName) {
        Set<ServiceName> serviceSet = new HashSet<>();

        ModuleNameDataExample example = new ModuleNameDataExample();
        example.createCriteria().andModuleNameLike("%" + serviceName + "%");

        example.setOrderByClause("id desc limit " + 20);

        List<ModuleNameData> moduleList = moduleMapper.selectByExample(example);
        if (moduleList == null || moduleList.isEmpty()) {
            return Result.success(serviceSet);
        }
        moduleList.forEach(moduleNameData -> serviceSet.add(new ServiceName(moduleNameData.getModuleName())));
        return Result.success(serviceSet);
    }

    @Override
    public Result<List<ServiceName>> loadDubboApiServicesFromNacos(String serviceName, String env) {
        DubboServiceList serviceList = new DubboServiceList();
        String serviceListStr = "";
        if ("staging".equals(env)) {
            serviceListStr = nacosNamingSt.serviceList2(DEFAULT_NAMESPACE, 1, 50, serviceName, ApiServiceImpl.stNacosAccessToken);
        } else if ("online".equals(env)) {
            serviceListStr = nacosNamingOl.serviceList2(DEFAULT_NAMESPACE, 1, 50, serviceName, ApiServiceImpl.olNacosAccessToken);
        }
        if (Objects.nonNull(serviceListStr) && StringUtils.isNotEmpty(serviceListStr)) {
            try {
                serviceList = gson.fromJson(serviceListStr, new TypeToken<DubboServiceList>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                LOGGER.error("获取nacos服务列表失败,serviceList:{},stAccessToken:{}", serviceListStr, ApiServiceImpl.stNacosAccessToken);
                apiService.refreshStNacosToken();
                return Result.fail(CommonError.JsonDeSerializeError);
            }
        }
        if (Objects.nonNull(serviceList)) {
            return Result.success(serviceList.getServiceList().stream().filter(service -> (!service.getName().startsWith("consumers:"))).collect(Collectors.toList()));
        } else {
            return Result.success(new ArrayList<>());
        }
    }

    @Override
    public Result<Boolean> manualUpdateDubboApi(ManualDubboUpDTO dubboUpDTO) {
        ModuleNameDataExample moduleExp = new ModuleNameDataExample();
        moduleExp.createCriteria().andModuleNameEqualTo(getFullServiceName(dubboUpDTO));
        List<ModuleNameData> moduleList = moduleMapper.selectByExample(moduleExp);
        if (moduleList == null || moduleList.isEmpty()) {
            return Result.fail(CommonError.ServiceMustRun);
        }
        String[] ipAndPort = moduleList.get(0).getAddress().split(":");
        GetApiBasicRequest requestBo = new GetApiBasicRequest();
        requestBo.setIp(ipAndPort[0]);
        requestBo.setPort(Integer.valueOf(ipAndPort[1]));
        requestBo.setApiName(dubboUpDTO.getMethodName());
        requestBo.setModuleClassName(dubboUpDTO.getServiceName());
        ApiCacheItem item = getDubboApiDetailFromRemote(dubboUpDTO.getEnv(), requestBo).getData();
        if (item == null) {
            return Result.fail(CommonError.ServiceMustRun);
        }
        String dubboServicePath = StringUtils.join(new String[]{item.getApiModelClass(), item.getApiGroup(), item.getApiVersion(), item.getApiName()}, ':');
        Api oldApi = apiMapper.getApiInfoByUrlAndProject(dubboServicePath, 0, dubboUpDTO.getProjectID());
        if (Objects.isNull(oldApi)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        ApiCacheItemBo apiCacheItemBo = new ApiCacheItemBo();
        BeanUtils.copyProperties(item, apiCacheItemBo);
        apiCacheItemBo.setGroupId(oldApi.getGroupID());
        apiCacheItemBo.setProjectId(oldApi.getProjectID());
        apiCacheItemBo.setName(item.getApiDocName());
        apiCacheItemBo.setUsername(dubboUpDTO.getOpUsername());
        apiCacheItemBo.setUpdateMsg(dubboUpDTO.getUpdateMsg());

        apiCacheItemBo.setApiNoteType(0);
        apiCacheItemBo.setApiRemark("");
        apiCacheItemBo.setApiDesc(item.getApiDocName());
        apiCacheItemBo.setRspExp(item.getResponse());
        apiCacheItemBo.setReqExp(item.getRequest());
        updateDubboApi(apiCacheItemBo, oldApi.getApiID());
        return Result.success(true);
    }

    private String getFullServiceName(ManualDubboUpDTO dubboUpDTO) {
        List<String> list = new ArrayList<>();
        list.add("providers");
        list.add(dubboUpDTO.getServiceName());
        if (Objects.nonNull(dubboUpDTO.getVersion()) && !dubboUpDTO.getVersion().isEmpty()) {
            list.add(dubboUpDTO.getVersion());
        }
        if (Objects.nonNull(dubboUpDTO.getGroup()) && !dubboUpDTO.getGroup().isEmpty()) {
            list.add(dubboUpDTO.getGroup());
        }

        return StringUtils.join(StringUtils.toStringArray(list), ':');
    }

    @Override
    public void dubboApiUpdateNotify(DubboApiUpdateNotifyBo bo) {
        pushDataPool.submit(() -> {
            int retry = 0;
            while (retry < 3) {
                try {
                    DubboPushDataExample example = new DubboPushDataExample();
                    example.createCriteria().andAddressEqualTo(bo.getIp() + ":" + bo.getPort());
                    List<DubboPushData> dubboPushDataList = dubboPushDataMapper.selectByExampleWithBLOBs(example);
                    if (dubboPushDataList == null || dubboPushDataList.size() == 0) {
                        retry++;
                        Thread.sleep(3000);
                        continue;
                    }
                    String apiModuleInfoStr = dubboPushDataList.get(0).getApimoduleinfo();
                    Map<String, ModuleCacheItem> apiModulesCache = gson.fromJson(apiModuleInfoStr, new TypeToken<Map<String, ModuleCacheItem>>() {
                    }.getType());
                    ModuleCacheItem moduleCacheItem = apiModulesCache.get(bo.getModuleClassName());

                    List<ApiCacheItem> dubboItems = new ArrayList<>();
                    moduleCacheItem.getModuleApiList().forEach(apiCacheItem -> {
                        GetApiBasicRequest requestBo = new GetApiBasicRequest();
                        requestBo.setIp(bo.getIp());
                        requestBo.setPort(bo.getPort());
                        requestBo.setApiName(apiCacheItem.getApiName());
                        requestBo.setModuleClassName(bo.getModuleClassName());
                        ApiCacheItem item = getDubboApiDetailFromRemote(bo.getEnv(), requestBo).getData();
                        dubboItems.add(item);
                    });
                    for (ApiCacheItem item :
                            dubboItems) {
                        String dubboServicePath = StringUtils.join(new String[]{item.getApiModelClass(), item.getApiGroup(), item.getApiVersion(), item.getApiName()}, ':');
                        Api oldApi = apiMapper.getApiInfoByUrl(dubboServicePath, 0);
                        if (Objects.isNull(oldApi)) {
                            continue;
                        }
                        ApiCacheItemBo apiCacheItemBo = new ApiCacheItemBo();
                        BeanUtils.copyProperties(item, apiCacheItemBo);
                        apiCacheItemBo.setGroupId(oldApi.getGroupID());
                        apiCacheItemBo.setProjectId(oldApi.getProjectID());
                        apiCacheItemBo.setName(item.getApiDocName());
                        apiCacheItemBo.setUsername(bo.getOpUsername());
                        apiCacheItemBo.setUpdateMsg(bo.getUpdateMsg());

                        apiCacheItemBo.setApiNoteType(0);
                        apiCacheItemBo.setApiRemark("");
                        apiCacheItemBo.setApiDesc(item.getApiDocName());
                        apiCacheItemBo.setRspExp(item.getResponse());
                        updateDubboApi(apiCacheItemBo, oldApi.getApiID());
                    }
                } catch (Exception e) {
                    LOGGER.error("get dubbo doc error", e);
                    retry++;
                    //10s retry
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

    @Override
    public Result<ApiCacheItem> getDubboApiDetailFromRemote(String env, GetApiBasicRequest dubboApiRequestBo) {
        ApiCacheItem apiCacheItem;
        try {
            DubboPushDataExample example = new DubboPushDataExample();
            example.createCriteria().andAddressEqualTo(dubboApiRequestBo.getIp() + ":" + dubboApiRequestBo.getPort());
            List<DubboPushData> dubboPushDataList = dubboPushDataMapper.selectByExampleWithBLOBs(example);
            if (dubboPushDataList == null || dubboPushDataList.size() == 0) {
                return Result.success(null);
            }
            String detailInfoStr = dubboPushDataList.get(0).getApiparamsresponseinfo();
            Map<String, ApiCacheItem> apiParamsAndRespCache = gson.fromJson(detailInfoStr, new TypeToken<Map<String, ApiCacheItem>>() {
            }.getType());

            apiCacheItem = apiParamsAndRespCache.get(dubboApiRequestBo.getModuleClassName() + "." + dubboApiRequestBo.getApiName());
        } catch (Exception e) {
            LOGGER.error("getDubboApiDetailFromRemote error", e);
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(apiCacheItem);
    }

    @Override
    @Transactional
    public Result<Boolean> addDubboApi(ApiCacheItemBo apiBo) {
        String dubboServicePath = StringUtils.join(new String[]{apiBo.getApiModelClass(), apiBo.getApiGroup(), apiBo.getApiVersion(), apiBo.getApiName()}, ':');
        Api oldApi = apiMapper.getApiInfoByUrlAndProject(dubboServicePath, 0, apiBo.getProjectId());
        if (Objects.nonNull(oldApi)) {
            return Result.fail(CommonError.APIAlreadyExist);
        }
        // add dubbo api
        EoDubboApiInfo dubboApiInfo = new EoDubboApiInfo();
        dubboApiInfo.setAsync(apiBo.getAsync());
        dubboApiInfo.setApidocname(apiBo.getApiDocName());
        dubboApiInfo.setApigroup(apiBo.getApiGroup());
        dubboApiInfo.setApimodelclass(apiBo.getApiModelClass());
        dubboApiInfo.setApiname(apiBo.getApiName());
        dubboApiInfo.setApirespdec("");
        dubboApiInfo.setApiversion(apiBo.getApiVersion());
        dubboApiInfo.setDescription(apiBo.getDescription());
        dubboApiInfo.setErrorcodes(apiBo.getApiErrorCodes());
        String responseParam;
        String requestParam;
        try {
            responseParam = gson.toJson(apiBo.getResponseLayer());
            requestParam = gson.toJson(apiBo.getParamsLayerList());
        } catch (Exception e) {
            return Result.fail(CommonError.JsonSerializeError);
        }
        dubboApiInfo.setResponse(responseParam);
        dubboApiInfo.setRequest(apiBo.getReqExp());
        dubboApiInfo.setMethodparaminfo(requestParam);
        int rt = dubboApiInfoMapper.insert(dubboApiInfo);
        if (rt < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        Api api = new Api();
        api.setApiEnv(apiBo.getApiEnv());
        api = apiService.checkAndFillApiInfo(api);
        api.setProjectID(apiBo.getProjectId());
        api.setGroupID(apiBo.getGroupId());
        api.setApiRequestType(0);
        api.setApiStatus(0);
        api.setMavenAddr(apiBo.getMavenAddr());
        api.setStarred(0);
        api.setApiRequestParamType(0);
        api.setApiResponseParamType(0);
        api.setApiName(apiBo.getName());
        api.setApiProtocol(Consts.DUBBO_API_TYPE);
        api.setApiURI(dubboServicePath);
        api.setUpdateUsername(apiBo.getUsername());
        api.setDubboApiId(dubboApiInfo.getId());
        if (apiBo.getApiNoteType() == null) {
            api.setApiNoteType(0);
        } else {
            api.setApiNoteType(apiBo.getApiNoteType());
        }
        if (apiBo.getApiDesc() == null) {
            api.setApiDesc("");
        } else {
            api.setApiDesc(apiBo.getApiDesc());
        }
        if (apiBo.getApiRemark() == null) {
            api.setApiRemark("");
        } else {
            api.setApiRemark(apiBo.getApiRemark());
        }
        api.setGatewayApiId(0);
        int result = apiMapper.addApi(api);
        if (result < 0) {
            return Result.fail(CommonError.UnknownError);
        }
        api.setUpdateUsername(apiBo.getUsername());

        String rsp = "";
        if (!StringUtils.isEmpty(apiBo.getRspExp())) {
            rsp = apiBo.getRspExp();
        }
        String req = "";
        if (!StringUtils.isEmpty(apiBo.getReqExp())) {
            req = apiBo.getReqExp();
        }
        apiService.dubboApiCodeGen(api, dubboApiInfo, rsp, req, false);
        //add system default mock data
        mockService.updateDubboApiMockData(api.getUpdateUsername(), null, api.getApiID(), "", "", 0, "API全局Mock", api.getProjectID(), rsp, 1, true, false, "");

        Map<String, Object> cache = new HashMap<String, Object>();
        cache.put("baseInfo", api);
        cache.put("dubboInfo", dubboApiInfo);
        cache.put("requestInfo", requestParam);
        cache.put("resultInfo", responseParam);
        cache.put("errorCodes", apiBo.getApiErrorCodes());

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

        String updateMsg = "add dubbo api";
        if (StringUtils.isNotEmpty(apiBo.getUpdateMsg())) {
            updateMsg = apiBo.getUpdateMsg();
        }
        apiService.recordApiHistory(api, gson.toJson(cache), updateMsg);

        recordService.doRecord(api, null, "添加dubbo接口", "添加dubbo接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_ADD);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> batchAddDubboApi(String apiEnv, List<BatchImportApiBo> bos) {

        bos.forEach(bo -> {
            List<ApiCacheItem> dubboItems = new ArrayList<>(bo.getApiNames().size());
            bo.getApiNames().forEach(apiName -> {
                GetApiBasicRequest getDubboApiRequestBo = new GetApiBasicRequest();
                getDubboApiRequestBo.setApiName(apiName);
                getDubboApiRequestBo.setIp(bo.getIp());
                getDubboApiRequestBo.setPort(bo.getPort());
                getDubboApiRequestBo.setModuleClassName(bo.getModuleClassName());

                ApiCacheItem item = getDubboApiDetailFromRemote(bo.getEnv(), getDubboApiRequestBo).getData();
                if (Objects.nonNull(item)) {
                    dubboItems.add(item);
                }
            });
            for (ApiCacheItem item :
                    dubboItems) {
                ApiCacheItemBo apiCacheItemBo = new ApiCacheItemBo();
                BeanUtils.copyProperties(item, apiCacheItemBo);
                apiCacheItemBo.setGroupId(bo.getGroupID());
                apiCacheItemBo.setProjectId(bo.getProjectID());
                apiCacheItemBo.setName(item.getApiDocName());
                apiCacheItemBo.setUsername(bo.getUpdateUserName());
                apiCacheItemBo.setApiEnv(apiEnv);
                apiCacheItemBo.setApiNoteType(0);
                apiCacheItemBo.setApiRemark("");
                apiCacheItemBo.setApiDesc(item.getApiDocName());
                apiCacheItemBo.setMavenAddr(item.getMavenAddr());
                apiCacheItemBo.setRspExp(item.getResponse());
                if (Objects.nonNull(item.getRequest()) && !item.getRequest().isEmpty()) {
                    apiCacheItemBo.setReqExp(item.getRequest());
                }

                String dubboServicePath = StringUtils.join(new String[]{item.getApiModelClass(), item.getApiGroup(), item.getApiVersion(), item.getApiName()}, ':');
                Api oldApi = apiMapper.getApiInfoByUrlAndProject(dubboServicePath, 0, bo.getProjectID());
                if (Objects.isNull(oldApi)) {
                    addDubboApi(apiCacheItemBo);
                } else if (bo.getForceUpdate()) {
                    updateDubboApi(apiCacheItemBo, oldApi.getApiID());
                }
            }
        });
        return Result.success(true);
    }

    @Override
    @Transactional
    public Result<Boolean> updateDubboApi(ApiCacheItemBo apiBo, Integer apiId) {
        String dubboServicePath = StringUtils.join(new String[]{apiBo.getApiModelClass(), apiBo.getApiGroup(), apiBo.getApiVersion(), apiBo.getApiName()}, ':');
        Api api = apiMapper.getApiInfoByUrlAndProject(dubboServicePath, 0, apiBo.getProjectId());
        if (Objects.isNull(api)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        if (StringUtils.isEmpty(api.getApiEnv())) {
            api.setApiEnv("staging");
        }
        api.setApiUpdateTime(new Timestamp(System.currentTimeMillis()));
        api.setProjectID(apiBo.getProjectId());
        api.setGroupID(apiBo.getGroupId());
        api.setApiName(apiBo.getName());
        api.setApiURI(dubboServicePath);
        api.setUpdateUsername(apiBo.getUsername());
        api.setApiNoteType(apiBo.getApiNoteType());
        api.setApiDesc(apiBo.getApiDesc());
        api.setApiRemark(apiBo.getApiRemark());
        api.setMavenAddr(apiBo.getMavenAddr());

        int result = apiMapper.updateApi(api);
        if (result < 0) {
            return Result.fail(CommonError.UnknownError);
        }

        EoDubboApiInfo dubboApiInfo = new EoDubboApiInfo();
        dubboApiInfo.setAsync(apiBo.getAsync());
        dubboApiInfo.setId(api.getDubboApiId());
        dubboApiInfo.setApidocname(apiBo.getApiDocName());
        dubboApiInfo.setApigroup(apiBo.getApiGroup());
        dubboApiInfo.setApimodelclass(apiBo.getApiModelClass());
        dubboApiInfo.setApiname(apiBo.getApiName());
        dubboApiInfo.setApirespdec("");
        dubboApiInfo.setApiversion(apiBo.getApiVersion());
        dubboApiInfo.setErrorcodes(apiBo.getApiErrorCodes());
        dubboApiInfo.setDescription(apiBo.getDescription());
        String responseParam = "";
        String requestParam = "";
        try {
            responseParam = gson.toJson(apiBo.getResponseLayer());
            requestParam = gson.toJson(apiBo.getParamsLayerList());
        } catch (Exception e) {
            return Result.fail(CommonError.JsonSerializeError);
        }
        dubboApiInfo.setRequest(apiBo.getReqExp());
        dubboApiInfo.setResponse(responseParam);
        dubboApiInfo.setMethodparaminfo(requestParam);

        int rt = dubboApiInfoMapper.updateByPrimaryKeyWithBLOBs(dubboApiInfo);
        if (rt < 0) {
            return Result.fail(CommonError.UnknownError);
        }
        String rsp = "";
        String req = "";
        if (!StringUtils.isEmpty(apiBo.getRspExp())) {
            rsp = apiBo.getRspExp();
            //update dubbo api default mock data
            ApiMockExpectExample example = new ApiMockExpectExample();
            example.createCriteria().andApiIdEqualTo(api.getApiID()).andIsDefaultEqualTo(true);
            List<ApiMockExpect> expects = mockExpectMapper.selectByExample(example);
            if (Objects.nonNull(expects) && !expects.isEmpty()) {
                mockService.updateDubboApiMockData(api.getUpdateUsername(), expects.get(0).getId(), api.getApiID(), "", "", 0, "API全局Mock", api.getProjectID(), rsp, 1, true, false, "");
            }
        }
        if (!StringUtils.isEmpty(apiBo.getReqExp())) {
            req = apiBo.getReqExp();
        }
        apiService.dubboApiCodeGen(api, dubboApiInfo, rsp, req, true);
        Map<String, Object> cache = new HashMap<String, Object>();
        cache.put("baseInfo", api);
        cache.put("dubboInfo", dubboApiInfo);
        cache.put("requestInfo", requestParam);
        cache.put("resultInfo", responseParam);
        cache.put("errorCodes", apiBo.getApiErrorCodes());

        ApiCache apiCache = new ApiCache();
        apiCache.setApiID(api.getApiID());
        apiCache.setApiJson(gson.toJson(cache));
        apiCache.setGroupID(api.getGroupID());
        apiCache.setProjectID(api.getProjectID());
        apiCache.setStarred(api.getStarred());
        apiCache.setUpdateUsername(api.getUpdateUsername());
        if (apiCacheMapper.updateApiCache(apiCache) < 1) {
            apiCacheMapper.addApiCache(apiCache);
        }

        String updateMsg = "update dubbo api";
        if (StringUtils.isNotEmpty(apiBo.getUpdateMsg())) {
            updateMsg = apiBo.getUpdateMsg();
        }
        apiService.recordApiHistory(api, gson.toJson(cache), updateMsg);

        recordService.doRecord(api, null, "更新dubbo接口", "更新dubbo接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_UPDATE);
        return Result.success(true);
    }
}
