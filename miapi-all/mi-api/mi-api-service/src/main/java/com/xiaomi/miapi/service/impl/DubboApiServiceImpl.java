package com.xiaomi.miapi.service.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.common.bo.*;
import com.xiaomi.miapi.common.dto.ManualDubboUpDTO;
import com.xiaomi.miapi.common.pojo.*;
import com.xiaomi.miapi.util.Md5Utils;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.service.DubboApiService;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.mone.dubbo.docs.core.beans.ModuleCacheItem;
import org.apache.dubbo.apidocs.core.providers.IDubboDocProvider;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.address.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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

    @DubboReference(registry = "olRegistry", lazy = true,check = false, group = "", interfaceClass = IDubboDocProvider.class, timeout = 1000, parameters = {"router", "address"}, retries = 0)
    private IDubboDocProvider olDubboDocProvider;

    @DubboReference(registry = "stRegistry",lazy = true, check = false, group = "", interfaceClass = IDubboDocProvider.class, timeout = 1000, parameters = {"router", "address"}, retries = 0)
    private IDubboDocProvider stDubboDocProvider;

    @Resource(name = "nacosNamingSt")
    private NacosNaming nacosNamingSt;

    @Resource(name = "nacosNamingOl")
    private NacosNaming nacosNamingOl;

    @Autowired
    private ApiMockExpectMapper mockExpectMapper;

    private String DEFAULT_NAMESPACE = "";

    public static final Gson gson = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboApiServiceImpl.class);

    @Override
    public Result<Map<String, Object>> getDubboApiDetail(Integer userId, Integer projectID, Integer apiID) {
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
        map.put("mavenAddr",api.getMavenAddr());
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

        map.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT, Consts.MockUrlPrefix + Consts.MockPrefix, md5Location, dubboApiInfo.getApiname()));

        redis.recordRecently10Apis(userId, apiID);

        return Result.success(map);
    }

    @Override
    public Map<String, Object> getBasicDubboApiDetail(Integer projectID, Integer apiID) {
        Map<String, Object> map = new HashMap<>();
        Api api = apiMapper.getApiInfo(projectID, apiID);
        if (api != null){
            EoDubboApiInfo dubboApiInfo = dubboApiInfoMapper.selectByPrimaryKey(api.getDubboApiId());
            map.put("dubboApiBaseInfo", dubboApiInfo);
            if (StringUtils.isEmpty(api.getApiEnv())) {
                map.put("apiEnv", "staging");
            } else {
                map.put("apiEnv", api.getApiEnv());
            }
            map.put("projectID",api.getProjectID());
            map.put("updateUsername", api.getUpdateUsername());
            map.put("apiNoteType", api.getApiNoteType());
            map.put("apiRemark", api.getApiRemark());
            map.put("apiDesc", api.getApiDesc());
            map.put("apiStatus", api.getApiStatus());
            map.put("name", api.getApiName());
        }
        return map;
    }

    @Override
    public Result<Map<String, Object>> getAllModulesInfo(String env, String serviceName, String ip) throws NacosException {
        Map<String, Object> resultMap = new HashMap<>();
        List<Instance> instanceList;
        List<String> ipAndPortList = new ArrayList<>();
        if ("staging".equals(env)) {
            instanceList = nacosNamingSt.getAllInstances(serviceName);
        } else if ("online".equals(env)){
            instanceList = nacosNamingOl.getAllInstances(serviceName);
        }else {
            instanceList = new ArrayList<>();
        }
        if (instanceList.isEmpty()) {
            return Result.success(resultMap);
        }
        instanceList = instanceList.stream().filter(Instance::isHealthy).collect(Collectors.toList());
        Address address;
        if (!ip.isEmpty()){
            String[] ipPort = ip.split(":");
            address = new Address(ipPort[0],Integer.parseInt(ipPort[1]));
        }else {
            address = new Address(instanceList.get(0).getIp(), instanceList.get(0).getPort());
        }
        RpcContext.getContext().setObjectAttachment("address", address);
        instanceList.forEach(instance -> {
            String ipAndPort = instance.getIp() + ":" + instance.getPort();
            ipAndPortList.add(ipAndPort);
        });

        if (stDubboDocProvider == null || olDubboDocProvider == null) {
            LOGGER.error("get dubbo doc error,init dubboDocProvider error");
        }
        String apiDoc;
        List<ModuleCacheItem> list;
        try {
            if (env.equals("staging")) {
                apiDoc = stDubboDocProvider.apiModuleList();
            } else if (env.equals("online")) {
                apiDoc = olDubboDocProvider.apiModuleList();
            }else {
                return Result.fail(CommonError.UnknownError);
            }
            list = gson.fromJson(apiDoc, new TypeToken<List<ModuleCacheItem>>() {
            }.getType());
        } catch (Exception e) {
            if (e instanceof RpcException) {
                if (((RpcException) e).getCode() == 6) {
                    return Result.fail(CommonError.DubboApiForIpPortNotFound);
                }
            }
            LOGGER.error("get dubbo doc error", e);
            return Result.fail(CommonError.UnknownError);
        }
        resultMap.put("list", list);
        resultMap.put("ipAndPort", ipAndPortList);
        return Result.success(resultMap);
    }

    @Override
    public Result<List<DubboService>> loadDubboApiServices(String serviceName, String env,String namespace) {
        DubboServiceList serviceList = new DubboServiceList();
        String serviceListStr = "";
        if ("staging".equals(env)) {
            serviceListStr = nacosNamingSt.serviceList2(DEFAULT_NAMESPACE, 1, 50, serviceName, ApiServiceImpl.stNacosAccessToken);
        } else if ("online".equals(env)){
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
            return Result.success(serviceList.getServiceList().stream().filter(service -> (service.getHealthyInstanceCount() > 0 && !service.getName().startsWith("consumers:"))).collect(Collectors.toList()));
        } else {
            return Result.success(new ArrayList<>());
        }
    }

    @Override
    public Result<Boolean> manualUpdateDubboApi(ManualDubboUpDTO dubboUpDTO) throws NacosException {
        List<String> list = new ArrayList<>();
        list.add("providers");
        list.add(dubboUpDTO.getServiceName());
        if (Objects.nonNull(dubboUpDTO.getVersion()) && !dubboUpDTO.getVersion().isEmpty()){
            list.add(dubboUpDTO.getVersion());
        }
        if (Objects.nonNull(dubboUpDTO.getGroup()) && !dubboUpDTO.getGroup().isEmpty()){
            list.add(dubboUpDTO.getGroup());
        }
        String serviceNameAddr = StringUtils.join(StringUtils.toStringArray(list),':');
        List<Instance> instanceList;
        if ("staging".equals(dubboUpDTO.getEnv())) {
            instanceList = nacosNamingSt.getAllInstances(serviceNameAddr);
        } else if ("online".equals(dubboUpDTO.getEnv())){
            instanceList = nacosNamingOl.getAllInstances(serviceNameAddr);
        }else {
            instanceList = new ArrayList<>();
        }
        if (instanceList.isEmpty()) {
            return Result.fail(CommonError.ServiceMustRun);
        }
        instanceList = instanceList.stream().filter(Instance::isHealthy).collect(Collectors.toList());

        if (stDubboDocProvider == null || olDubboDocProvider == null) {
            LOGGER.error("get dubbo doc error,init dubboDocProvider error");
        }
        GetDubboApiRequestBo requestBo = new GetDubboApiRequestBo();
        requestBo.setIp(instanceList.get(0).getIp());
        requestBo.setPort(instanceList.get(0).getPort());
        requestBo.setApiName(dubboUpDTO.getMethodName());
        requestBo.setModuleClassName(dubboUpDTO.getServiceName());
        ApiCacheItem item = getDubboApiDetailFromRemote(dubboUpDTO.getEnv(), requestBo).getData();

        String dubboServicePath = StringUtils.join(new String[]{item.getApiModelClass(), item.getApiGroup(), item.getApiVersion(), item.getApiName()}, ':');
        Api oldApi = apiMapper.getApiInfoByUrl(dubboServicePath, 0);
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

    /**
     * 通知dubbo接口更新
     *
     * @param bo
     * @return
     */
    @Override
    public Result<Boolean> dubboApiUpdateNotify(DubboApiUpdateNotifyBo bo) throws InterruptedException {
        //获取方法集
        String docInfo;
        ModuleCacheItem moduleCacheItem;
        int retry = 0;
        while (retry < 3) {
            try {
                Address address = new Address(bo.getIp(), bo.getPort());
                RpcContext.getContext().setObjectAttachment("address", address);
                if (StringUtils.isNotEmpty(bo.getEnv()) && bo.getEnv().equals("staging")) {
                    docInfo = stDubboDocProvider.apiModuleInfo(bo.getModuleClassName());
                } else if (StringUtils.isNotEmpty(bo.getEnv()) && bo.getEnv().equals("online")) {
                    docInfo = olDubboDocProvider.apiModuleInfo(bo.getModuleClassName());
                } else {
                    return Result.fail(CommonError.UnknownError);
                }
                moduleCacheItem = gson.fromJson(docInfo, new TypeToken<ModuleCacheItem>() {
                }.getType());

                List<ApiCacheItem> dubboItems = new ArrayList<>();
                moduleCacheItem.getModuleApiList().forEach(apiCacheItem -> {
                    GetDubboApiRequestBo requestBo = new GetDubboApiRequestBo();
                    requestBo.setIp(bo.getIp());
                    requestBo.setPort(bo.getPort());
                    requestBo.setApiName(apiCacheItem.getApiName());
                    requestBo.setModuleClassName(bo.getModuleClassName());
                    ApiCacheItem item = getDubboApiDetailFromRemote(bo.getEnv(), requestBo).getData();
                    dubboItems.add(item);
                });
                for (ApiCacheItem item :
                        dubboItems) {
                    //唯一关联
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

    @Override
    public Result<ApiCacheItem> getDubboApiDetailFromRemote(String env, GetDubboApiRequestBo dubboApiRequestBo) {
        Address address = new Address(dubboApiRequestBo.getIp(), dubboApiRequestBo.getPort());
        RpcContext.getContext().setObjectAttachment("address", address);
        ApiCacheItem apiCacheItem;
        try {
            String apiCacheItemStr;
            if (env != null && env.equals("staging")) {
                apiCacheItemStr = stDubboDocProvider.apiParamsResponseInfo(dubboApiRequestBo.getModuleClassName() + "." + dubboApiRequestBo.getApiName());
            } else if (env != null && env.equals("online")){
                apiCacheItemStr = olDubboDocProvider.apiParamsResponseInfo(dubboApiRequestBo.getModuleClassName() + "." + dubboApiRequestBo.getApiName());
            }else {
                return Result.fail(CommonError.InvalidParamError);
            }
            apiCacheItem = gson.fromJson(apiCacheItemStr, ApiCacheItem.class);
        } catch (Exception e) {
            LOGGER.error("getDubboApiDetailFromRemote error", e);
            return Result.fail(CommonError.UnknownError);
        }
        return Result.success(apiCacheItem);
    }

    @Override
    @Transactional
    public Result<Boolean> addDubboApi(ApiCacheItemBo apiBo) {
        //唯一关联
        String dubboServicePath = StringUtils.join(new String[]{apiBo.getApiModelClass(), apiBo.getApiGroup(), apiBo.getApiVersion(), apiBo.getApiName()}, ':');
        Api oldApi = apiMapper.getApiInfoByUrl(dubboServicePath, 0);
        if (Objects.nonNull(oldApi)) {
            return Result.fail(CommonError.APIAlreadyExist);
        }
        // 插入dubboApi
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
        String responseParam = "";
        String requestParam = "";
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
        apiService.dubboApiCodeGen(api, dubboApiInfo, rsp,req,false);
        //插入系统默认mock
        mockService.updateDubboApiMockData(api.getUpdateUsername(), null, api.getApiID(), "", "", 0, "API全局Mock", api.getProjectID(), rsp, 1, true,false,"");

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

        //记录历史变更
        String updateMsg = "add dubbo api";
        if (StringUtils.isNotEmpty(apiBo.getUpdateMsg())) {
            updateMsg = apiBo.getUpdateMsg();
        }
        //记录历史版本
        apiService.recordApiHistory(api, gson.toJson(cache), updateMsg);

        recordService.doRecord(api, null, "添加dubbo接口", "添加dubbo接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_ADD);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> batchAddDubboApi(String apiEnv,List<BatchImportDubboApiBo> bos) {

        bos.forEach(bo -> {
            List<ApiCacheItem> dubboItems = new ArrayList<>(bo.getApiNames().size());
            bo.getApiNames().forEach(apiName -> {
                GetDubboApiRequestBo getDubboApiRequestBo = new GetDubboApiRequestBo();
                getDubboApiRequestBo.setApiName(apiName);
                getDubboApiRequestBo.setIp(bo.getIp());
                getDubboApiRequestBo.setPort(bo.getPort());
                getDubboApiRequestBo.setModuleClassName(bo.getModuleClassName());

                ApiCacheItem item = getDubboApiDetailFromRemote(bo.getEnv(), getDubboApiRequestBo).getData();
                if (Objects.nonNull(item)){
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
                if (Objects.nonNull(item.getRequest()) && !item.getRequest().isEmpty()){
                    apiCacheItemBo.setReqExp(item.getRequest());
                }

                //唯一关联
                String dubboServicePath = StringUtils.join(new String[]{item.getApiModelClass(), item.getApiGroup(), item.getApiVersion(), item.getApiName()}, ':');
                Api oldApi = apiMapper.getApiInfoByUrl(dubboServicePath, 0);
                if (Objects.isNull(oldApi)) {
                    //不存在则添加；
                    addDubboApi(apiCacheItemBo);
                } else if (bo.getForceUpdate()) {
                    //强制更新
                    updateDubboApi(apiCacheItemBo, oldApi.getApiID());
                }
            }
        });
        return Result.success(true);
    }

    @Override
    @Transactional
    public Result<Boolean> updateDubboApi(ApiCacheItemBo apiBo, Integer apiId) {
        //唯一关联
        String dubboServicePath = StringUtils.join(new String[]{apiBo.getApiModelClass(), apiBo.getApiGroup(), apiBo.getApiVersion(), apiBo.getApiName()}, ':');
        Api api = apiMapper.getApiInfoByUrl(dubboServicePath, 0);
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

        // 更新dubboApi
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
            //更新dubbo类型接口系统默认期望
            ApiMockExpectExample example = new ApiMockExpectExample();
            example.createCriteria().andApiIdEqualTo(api.getApiID()).andIsDefaultEqualTo(true);
            List<ApiMockExpect> expects = mockExpectMapper.selectByExample(example);
            if (Objects.nonNull(expects) && !expects.isEmpty()) {
                mockService.updateDubboApiMockData(api.getUpdateUsername(), expects.get(0).getId(), api.getApiID(), "", "", 0, "API全局Mock", api.getProjectID(), rsp, 1, true,false,"");
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

        //记录历史变更
        String updateMsg = "update dubbo api";
        if (StringUtils.isNotEmpty(apiBo.getUpdateMsg())) {
            updateMsg = apiBo.getUpdateMsg();
        }
        //记录历史版本
        apiService.recordApiHistory(api, gson.toJson(cache), updateMsg);

        recordService.doRecord(api, null, "更新dubbo接口", "更新dubbo接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_UPDATE);
        return Result.success(true);
    }
}
