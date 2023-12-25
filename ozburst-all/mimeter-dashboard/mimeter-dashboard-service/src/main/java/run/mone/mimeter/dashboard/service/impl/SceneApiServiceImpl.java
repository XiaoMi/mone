package run.mone.mimeter.dashboard.service.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.api.service.MiApiDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.DubboService;
import run.mone.mimeter.dashboard.bo.DubboServiceList;
import run.mone.mimeter.dashboard.bo.NacosInfo;
import run.mone.mimeter.dashboard.bo.NacosLoginInfo;
import run.mone.mimeter.dashboard.bo.common.Constants;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.scene.DefaultSceneInfo;
import run.mone.mimeter.dashboard.bo.scene.DubboApiInfoDTO;
import run.mone.mimeter.dashboard.bo.scene.HttpApiInfoDTO;
import run.mone.mimeter.dashboard.bo.scene.TspAuthInfo;
import run.mone.mimeter.dashboard.bo.sceneapi.*;
import run.mone.mimeter.dashboard.common.DubboParamItem;
import run.mone.mimeter.dashboard.common.TaskType;
import run.mone.mimeter.dashboard.common.util.BizUtils;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.exception.CommonException;
import run.mone.mimeter.dashboard.mapper.CheckPointInfoMapper;
import run.mone.mimeter.dashboard.mapper.SceneApiInfoMapper;
import run.mone.mimeter.dashboard.pojo.CheckPointInfo;
import run.mone.mimeter.dashboard.pojo.SceneApiInfo;
import run.mone.mimeter.dashboard.pojo.SceneApiInfoExample;
import run.mone.mimeter.dashboard.service.SceneApiService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static run.mone.mimeter.dashboard.bo.common.Constants.*;

@Service
@Slf4j
public class SceneApiServiceImpl implements SceneApiService {

    //mimeter只会有一个环境，调用mi-api线上即可
    @DubboReference(check = false, group = "${ref.miapi.service.group}", version = "${ref.miapi.service.version}")
    private MiApiDataService miApiDataService;

    @Autowired
    private SceneApiInfoMapper sceneApiInfoMapper;

    @Resource(name = "nacosNamingSt")
    private NacosNaming nacosNamingSt;

    @Resource(name = "nacosNamingOl")
    private NacosNaming nacosNamingOl;
    private static final Gson gson = Util.getGson();
    ;
    public static String stNacosAccessToken = "";

    public static String olNacosAccessToken = "";


    @Autowired
    private CheckPointInfoMapper checkPointInfoMapper;

    @Autowired
    NacosInfo nacosInfo;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * 每5分钟更新nacos的accessToken
     */
    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(this::refreshStNacosToken, 0, 5, TimeUnit.MINUTES);

        executorService.scheduleAtFixedRate(this::refreshOlNacosToken, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public Result<Object> searchApiFromMiApi(String keyword, Integer apiProtocol) {
        try {
            List<Map<String, Object>> apiDatas = miApiDataService.searchAllApiByKeyword(keyword, apiProtocol);

            if (apiProtocol == Constants.HTTP_API_TYPE || apiProtocol == Constants.GATEWAY_API_TYPE) {
                List<SceneHttpApiInfoItemBasic> items = new ArrayList<>();
                //http、mione网关接口集
                apiDatas.forEach(apiData -> {
                    SceneHttpApiInfoItemBasic httpItem = new SceneHttpApiInfoItemBasic();
                    httpItem.setApiName((String) apiData.getOrDefault("apiName", ""));
                    httpItem.setApiRequestType((Integer) apiData.getOrDefault("apiRequestType", 0));
                    httpItem.setApiUrl((String) apiData.getOrDefault("apiURI", ""));
                    httpItem.setApiProtocol((Integer) apiData.getOrDefault("apiProtocol", HTTP_API_TYPE));
                    Long apiID = (Long) apiData.getOrDefault("apiID", 0);
                    httpItem.setApiID(apiID.intValue());

                    Long projectID = (Long) apiData.getOrDefault("projectID", 0);
                    httpItem.setProjectID(projectID.intValue());
                    items.add(httpItem);
                });
                return Result.success(items);
            } else if (apiProtocol == Constants.DUBBO_API_TYPE) {
                List<SceneDubboApiInfoItemBasic> items = new ArrayList<>();
                //dubbo接口集
                apiDatas.forEach(apiData -> {
                    SceneDubboApiInfoItemBasic dubboItem = new SceneDubboApiInfoItemBasic();
                    dubboItem.setApiName((String) apiData.getOrDefault("apiName", ""));

                    Long apiID = (Long) apiData.getOrDefault("apiID", 0);

                    dubboItem.setApiID(apiID.intValue());

                    Long projectID = (Long) apiData.getOrDefault("projectID", 0);
                    dubboItem.setProjectID(projectID.intValue());
                    dubboItem.setApiProtocol((Integer) apiData.getOrDefault("apiProtocol", DUBBO_API_TYPE));
                    String dubboPath = (String) apiData.getOrDefault("apiURI", "");
                    String[] dubboInfo = dubboPath.split(":", 4);
                    if (dubboInfo.length != 4) {
                        return;
                    }
                    dubboItem.setServiceName(dubboInfo[0]);
                    dubboItem.setGroup(dubboInfo[1]);
                    dubboItem.setVersion(dubboInfo[2]);
                    dubboItem.setMethodName(dubboInfo[3]);
                    dubboItem.setPath(dubboPath);
                    items.add(dubboItem);
                });
                return Result.success(items);
            }
        } catch (Exception e) {
            return Result.fail(CommonError.LoadMiApiDataFail);
        }
        return Result.success(new ArrayList<>());
    }

    @Override
    public Result<Object> getApiDetailFromMiApi(GetApiDetailReq req) {
        try {
            String apiDetailStr = miApiDataService.getApiDetailById(req.getProjectID(), req.getApiID(), req.getApiProtocol());
            Map<String, Object> apiDetail = gson.fromJson(apiDetailStr, new TypeToken<Map<String, Object>>() {
            }.getType());
            //http或网关类型的接口
            if (req.getApiProtocol() == Constants.HTTP_API_TYPE || req.getApiProtocol() == Constants.GATEWAY_API_TYPE) {
                SceneHttpApiInfoItemDetail httpDetail = new SceneHttpApiInfoItemDetail();
                httpDetail.setHeaderInfo(gson.toJson(apiDetail.getOrDefault("headerInfo", "")));
                Map<String, Object> baseInfo = null;
                String baseUrl = "";
                if (req.getApiProtocol() == Constants.HTTP_API_TYPE) {
                    baseInfo = (Map<String, Object>) apiDetail.get("baseInfo");
                    if (Objects.nonNull(baseInfo)) {
                        httpDetail.setApiName((String) baseInfo.get("apiName"));
                        httpDetail.setApiID((Integer) baseInfo.get("apiID"));
                        int requestMethod = (int) baseInfo.get("apiRequestType");
                        httpDetail.setApiRequestType(requestMethod);
                        baseUrl = (String) baseInfo.get("apiURI");
                        httpDetail.setApiRequestParamType((Integer) baseInfo.get("apiRequestParamType"));
                        httpDetail.setRequestInfoRaw((String) baseInfo.get("apiRequestRaw"));
                    }
                } else if (req.getApiProtocol() == Constants.GATEWAY_API_TYPE) {
                    baseInfo = (Map<String, Object>) apiDetail.get("gatewayApiBaseInfo");
                    if (Objects.nonNull(baseInfo)) {
                        httpDetail.setApiName((String) baseInfo.get("name"));
                        httpDetail.setApiID((Integer) baseInfo.get("id"));
                        String requestMethod = (String) baseInfo.get("httpMethod");
                        if (requestMethod.equalsIgnoreCase("get")) {
                            httpDetail.setApiRequestType(MI_API_HTTP_REQ_GET);
                        } else {
                            httpDetail.setApiRequestType(MI_API_HTTP_REQ_POST);
                        }
                        httpDetail.setApiRequestParamType(2);
                        baseUrl = (String) baseInfo.get("url");
                        httpDetail.setRequestInfoRaw((String) apiDetail.getOrDefault("apiRequestRaw", "{}"));

                    }
                }
                //http或网关类型接口
                httpDetail.setApiProtocol(req.getApiProtocol());
                StringBuilder url = new StringBuilder("127.0.0.1:8080");
                url.append(baseUrl);
                if (httpDetail.getApiRequestType() == MI_API_HTTP_REQ_GET) {
                    List<FormParamValue> reqParamList = (List<FormParamValue>) apiDetail.getOrDefault("requestInfo", "");
                    if (reqParamList != null && reqParamList.size() != 0) {
                        url.append("?");
                    }
                    for (int i = 0; i < Objects.requireNonNull(reqParamList).size(); i++) {
                        Map<String, String> pair = (Map<String, String>) reqParamList.get(i);
                        url.append(pair.get("paramKey"));
                        url.append("=");
                        url.append(pair.get("paramValue"));
                        if (i != reqParamList.size() - 1) {
                            url.append("&");
                        }
                    }
                } else {
                    httpDetail.setRequestInfo(gson.toJson(apiDetail.getOrDefault("requestInfo", "")));
                }
                httpDetail.setApiUrl(url.toString());
                return Result.success(httpDetail);
            } else if (req.getApiProtocol() == Constants.DUBBO_API_TYPE) {
                //dubbo类型接口
                SceneDubboApiInfoItemDetail dubboDetail = new SceneDubboApiInfoItemDetail();
                dubboDetail.setApiName((String) apiDetail.getOrDefault("name", ""));
                dubboDetail.setDubboEnv((String) apiDetail.getOrDefault("apiEnv", "staging"));
                dubboDetail.setProjectID((Integer) apiDetail.getOrDefault("projectID", ""));
                dubboDetail.setApiProtocol(Constants.DUBBO_API_TYPE);

                Map<String, Object> dubboApiBaseInfo = (Map<String, Object>) apiDetail.get("dubboApiBaseInfo");
                if (Objects.nonNull(dubboApiBaseInfo)) {
                    dubboDetail.setServiceName((String) dubboApiBaseInfo.get("apimodelclass"));
                    dubboDetail.setGroup((String) dubboApiBaseInfo.get("apigroup"));
                    dubboDetail.setVersion((String) dubboApiBaseInfo.get("apiversion"));
                    dubboDetail.setMethodName((String) dubboApiBaseInfo.get("apiname"));
                    dubboDetail.setApiID((Integer) dubboApiBaseInfo.get("id"));

                    if (Objects.nonNull(dubboApiBaseInfo.get("methodparaminfo"))) {
                        String paramsJson = (String) dubboApiBaseInfo.get("methodparaminfo");
                        List<DubboParamItem> itemList = gson.fromJson(paramsJson, new TypeToken<List<DubboParamItem>>() {
                        }.getType());
                        if (Objects.nonNull(itemList)) {
                            dubboDetail.setRequestParamTypeList(itemList.stream().map(DubboParamItem::getItemClassStr).collect(Collectors.toList()));
                        }
                    } else {
                        dubboDetail.setRequestParamTypeList(new ArrayList<>());
                    }

                    //请求体，需要mi-api中对应项目使用较高版本依赖才能自动生成请求体
                    if (Objects.nonNull(dubboApiBaseInfo.get("request"))) {
                        dubboDetail.setRequestBody((String) dubboApiBaseInfo.get("request"));
                    }
                }
                return Result.success(dubboDetail);
            }
            return Result.fail(CommonError.InvalidParamError);
        } catch (Exception e) {
            return Result.fail(CommonError.LoadMiApiDataFail);
        }
    }

    @Override
    public Result<DefaultSceneInfo> getSceneBasicInfoFromApiID(GetApiDetailReq req) {
        DefaultSceneInfo defaultSceneInfo = new DefaultSceneInfo();
        Object apiInfo;
        try {
            apiInfo = this.getApiDetailFromMiApi(req).getData();

            defaultSceneInfo.setDefaultSceneName("默认场景（请修改）");
            if (req.getApiProtocol() == Constants.HTTP_API_TYPE || req.getApiProtocol() == Constants.GATEWAY_API_TYPE) {
                defaultSceneInfo.setSceneType(TaskType.http.name());
                SceneHttpApiInfoItemDetail httpApi = (SceneHttpApiInfoItemDetail) apiInfo;
                if (httpApi != null) {
                    defaultSceneInfo.setDefaultSceneName(httpApi.getApiName());
                }
            } else if (req.getApiProtocol() == Constants.DUBBO_API_TYPE) {
                defaultSceneInfo.setSceneType(TaskType.dubbo.name());
                SceneDubboApiInfoItemDetail dubboApi = (SceneDubboApiInfoItemDetail) apiInfo;
                if (dubboApi != null) {
                    defaultSceneInfo.setDefaultSceneName(dubboApi.getApiName());
                }
            }
            defaultSceneInfo.setApiInfo(apiInfo);
        } catch (Exception e) {
            log.error("get api error:{}", e.getMessage());
        }
        return Result.success(defaultSceneInfo);
    }

    @Override
    public boolean newHttpSceneApis(List<HttpApiInfoDTO> httpApiInfoDTOS, int sceneId, int serialLinkId) {
        httpApiInfoDTOS.forEach(httpApiInfoDTO -> {
            Pair<Integer, String> checkRes = checkHttpSceneApiParam(httpApiInfoDTO);
            if (checkRes.getKey() != 0) {
                throw new CommonException(CommonError.InvalidParamError.code, checkRes.getValue());
            }
            SceneApiInfo info = new SceneApiInfo();
            BeanUtils.copyProperties(httpApiInfoDTO, info);
            info.setApiType(Constants.CASE_TYPE_HTTP);
            info.setSerialLinkId(serialLinkId);
            info.setSceneId(sceneId);
            //raw数据，post请求使用raw格式
            if (httpApiInfoDTO.getRequestInfoRaw() == null || httpApiInfoDTO.getRequestInfoRaw().isEmpty()) {
                info.setRequestBody("[{}]");
            } else {
                info.setRequestBody(httpApiInfoDTO.getRequestInfoRaw());
            }
            //带格式参数数据
            info.setRequestMethod(httpApiInfoDTO.getApiRequestType());
            info.setRequestParamInfo(httpApiInfoDTO.getRequestInfo());
            if (httpApiInfoDTO.getApiRequestType() == Constants.HTTP_REQ_GET) {
                Pair<Integer, List<FormParamValue>> res = parseGetUrl(httpApiInfoDTO.getApiUrl());
                if (res.getLeft() == -1) {
                    return;
                } else {
                    info.setRequestParamInfo(gson.toJson(res.getRight()));
                }
            }
            info.setApiHeader(httpApiInfoDTO.getHeaderInfo());

            //汽车部tsp接口鉴权
            if (httpApiInfoDTO.getApiTspAuth() != null){
                info.setApiTspAuth(gson.toJson(httpApiInfoDTO.getApiTspAuth()));
            }else {
                info.setApiTspAuth(gson.toJson(new TspAuthInfo(false)));
            }
            //该接口是否使用录制的流量
            if (httpApiInfoDTO.getApiTrafficInfo() != null){
                info.setApiTrafficInfo(gson.toJson(httpApiInfoDTO.getApiTrafficInfo()));
            }else {
                info.setApiTrafficInfo(gson.toJson(new ApiTrafficInfo(false)));
            }

            //该接口是否使用x5
            if (httpApiInfoDTO.getApiX5Info() != null){
                info.setApiX5Info(gson.toJson(httpApiInfoDTO.getApiX5Info()));
            }else {
                info.setApiX5Info(gson.toJson(new ApiX5Info(false)));
            }

            //转换解析表达式
            BizUtils.processOutputParamExpr(httpApiInfoDTO.getOutputParamInfos());
            info.setOutputParamInfo(gson.toJson(httpApiInfoDTO.getOutputParamInfos()));

            //处理检查点数据
            processCheckPoints(null, httpApiInfoDTO, info);

            //过滤条件
            info.setFilterCondition(gson.toJson(httpApiInfoDTO.getFilterCondition()));
            sceneApiInfoMapper.insert(info);
            httpApiInfoDTO.setApiID(info.getId());
        });
        return true;
    }

    @Override
    public boolean updateHttpSceneApi(HttpApiInfoDTO httpApiInfoDTO) {
        Pair<Integer, String> checkRes = checkHttpSceneApiParam(httpApiInfoDTO);
        if (checkRes.getKey() != 0) {
            return false;
        }
        SceneApiInfo info = sceneApiInfoMapper.selectByPrimaryKey(httpApiInfoDTO.getApiID());

        BeanUtils.copyProperties(httpApiInfoDTO, info);
        info.setApiType(Constants.CASE_TYPE_HTTP);
        //raw数据，post请求使用raw格式
        info.setRequestBody(httpApiInfoDTO.getRequestInfoRaw());
        //带格式参数数据
        info.setRequestMethod(httpApiInfoDTO.getApiRequestType());
        info.setRequestParamInfo(httpApiInfoDTO.getRequestInfo());
        if (httpApiInfoDTO.getApiRequestType() == Constants.HTTP_REQ_GET) {
            Pair<Integer, List<FormParamValue>> res = parseGetUrl(httpApiInfoDTO.getApiUrl());
            if (res.getLeft() == -1) {
                return false;
            } else {
                info.setRequestParamInfo(gson.toJson(res.getRight()));
            }
        }
        info.setApiHeader(httpApiInfoDTO.getHeaderInfo());

        //汽车部tsp接口鉴权
        if (httpApiInfoDTO.getApiTspAuth() != null){
            info.setApiTspAuth(gson.toJson(httpApiInfoDTO.getApiTspAuth()));
        }else {
            info.setApiTspAuth(gson.toJson(new TspAuthInfo(false)));
        }
        //该接口是否使用录制的流量
        if (httpApiInfoDTO.getApiTrafficInfo() != null){
            info.setApiTrafficInfo(gson.toJson(httpApiInfoDTO.getApiTrafficInfo()));
        }else {
            info.setApiTrafficInfo(gson.toJson(new ApiTrafficInfo(false)));
        }
        //该接口是否使用x5
        if (httpApiInfoDTO.getApiX5Info() != null){
            info.setApiX5Info(gson.toJson(httpApiInfoDTO.getApiX5Info()));
        }else {
            info.setApiX5Info(gson.toJson(new ApiX5Info(false)));
        }
        //转换解析表达式
        BizUtils.processOutputParamExpr(httpApiInfoDTO.getOutputParamInfos());
        info.setOutputParamInfo(gson.toJson(httpApiInfoDTO.getOutputParamInfos()));

        //处理检查点数据
        processCheckPoints(null, httpApiInfoDTO, info);

        //过滤条件
        info.setFilterCondition(gson.toJson(httpApiInfoDTO.getFilterCondition()));

        sceneApiInfoMapper.updateByPrimaryKeyWithBLOBs(info);
        return true;
    }

    @Override
    public boolean updateDubboSceneApi(DubboApiInfoDTO dubboApiInfoDTO) {
        Pair<Integer, String> checkRes = checkDubboSceneApiParam(dubboApiInfoDTO);
        if (checkRes.getKey() != 0) {
            throw new CommonException(CommonError.InvalidParamError.code, checkRes.getValue());
        }
        SceneApiInfo info = sceneApiInfoMapper.selectByPrimaryKey(dubboApiInfoDTO.getApiID());
        BeanUtils.copyProperties(dubboApiInfoDTO, info);
        info.setApiType(Constants.CASE_TYPE_RPCX);
        info.setDubboGroup(dubboApiInfoDTO.getGroup());
        info.setDubboVersion(dubboApiInfoDTO.getVersion());
        //nacos环境
        if ("staging".equals(dubboApiInfoDTO.getDubboEnv())) {
            info.setNacosType(Constants.NACOS_TYPE_ST);
        } else if ("online".equals(dubboApiInfoDTO.getDubboEnv())) {
            info.setNacosType(Constants.NACOS_TYPE_OL);
        }
        //参数类型列表
        info.setParamTypeList(gson.toJson(dubboApiInfoDTO.getRequestParamTypeList()));
        //参数体 json
        info.setDubboParamJson(dubboApiInfoDTO.getRequestBody());

        //更新attachment
        info.setApiHeader(dubboApiInfoDTO.getAttachments());

        //转换解析表达式
        BizUtils.processOutputParamExpr(dubboApiInfoDTO.getOutputParamInfos());
        info.setOutputParamInfo(gson.toJson(dubboApiInfoDTO.getOutputParamInfos()));

        //处理检查点数据
        processCheckPoints(dubboApiInfoDTO, null, info);

        //过滤条件
        info.setFilterCondition(gson.toJson(dubboApiInfoDTO.getFilterCondition()));
        sceneApiInfoMapper.updateByPrimaryKeyWithBLOBs(info);
        return true;
    }

    @Override
    public boolean newDubboSceneApis(List<DubboApiInfoDTO> dubboApiInfoDTOS, int sceneId, int serialLinkId) {
        dubboApiInfoDTOS.forEach(dubboApiInfoDTO -> {
            Pair<Integer, String> checkRes = checkDubboSceneApiParam(dubboApiInfoDTO);
            if (checkRes.getKey() != 0) {
                throw new CommonException(CommonError.InvalidParamError.code, checkRes.getValue());
            }
            SceneApiInfo info = new SceneApiInfo();
            BeanUtils.copyProperties(dubboApiInfoDTO, info);
            info.setDubboGroup(dubboApiInfoDTO.getGroup());
            info.setDubboVersion(dubboApiInfoDTO.getVersion());

            info.setRequestMethod(0);
            info.setNeedLogin(false);

            info.setApiType(Constants.CASE_TYPE_RPCX);
            info.setSceneId(sceneId);
            info.setSerialLinkId(serialLinkId);
            //nacos环境
            if ("staging".equals(dubboApiInfoDTO.getDubboEnv())) {
                info.setNacosType(Constants.NACOS_TYPE_ST);
            } else if ("online".equals(dubboApiInfoDTO.getDubboEnv())) {
                info.setNacosType(Constants.NACOS_TYPE_OL);
            }
            //参数类型列表
            info.setParamTypeList(gson.toJson(dubboApiInfoDTO.getRequestParamTypeList()));
            //参数体 json
            info.setDubboParamJson(dubboApiInfoDTO.getRequestBody());
            //dubbo请求携带的attachment
            info.setApiHeader(dubboApiInfoDTO.getAttachments());

            //转换解析表达式
            BizUtils.processOutputParamExpr(dubboApiInfoDTO.getOutputParamInfos());
            info.setOutputParamInfo(gson.toJson(dubboApiInfoDTO.getOutputParamInfos()));

            //处理检查点数据
            processCheckPoints(dubboApiInfoDTO, null, info);

            //过滤条件
            info.setFilterCondition(gson.toJson(dubboApiInfoDTO.getFilterCondition()));
            sceneApiInfoMapper.insert(info);
            dubboApiInfoDTO.setApiID(info.getId());
        });
        return true;
    }

    @Override
    public boolean deleteSceneApisBySceneId(int sceneId) {
        SceneApiInfoExample example = new SceneApiInfoExample();
        example.createCriteria().andSceneIdEqualTo(sceneId);
        return sceneApiInfoMapper.deleteByExample(example) >= 0;
    }

    @Override
    public boolean deleteSceneApisBySerialLinkId(int serialLinkId) {
        SceneApiInfoExample example = new SceneApiInfoExample();
        example.createCriteria().andSerialLinkIdEqualTo(serialLinkId);
        return sceneApiInfoMapper.deleteByExample(example) >= 0;
    }

    private void processCheckPoints(DubboApiInfoDTO dubboApiInfoDTO, HttpApiInfoDTO httpApiInfoDTO, SceneApiInfo info) {
        List<CheckPointInfoDTO> checkPointInfoDTOList = new ArrayList<>();
        //检查点信息
        if (info.getApiType() == Constants.CASE_TYPE_HTTP) {
            checkPointInfoDTOList = httpApiInfoDTO.getCheckPointInfoList();
        } else if (info.getApiType() == Constants.CASE_TYPE_RPCX) {
            checkPointInfoDTOList = dubboApiInfoDTO.getCheckPointInfoList();
        }
        List<CheckPointInfo> checkPointInfos = new ArrayList<>(checkPointInfoDTOList.size());
        checkPointInfoDTOList.forEach(checkPointInfoDTO -> {
            CheckPointInfo checkPointInfo = new CheckPointInfo();
            checkPointInfo.setCheckType(checkPointInfoDTO.getCheckType());
            checkPointInfo.setCheckObj(checkPointInfoDTO.getCheckObj());
            checkPointInfo.setCheckCondition(checkPointInfoDTO.getCheckCondition());
            checkPointInfo.setCheckContent(checkPointInfoDTO.getCheckContent());
            checkPointInfos.add(checkPointInfo);
        });

        //插入检查点数据
        if (checkPointInfos.size() != 0) {
            checkPointInfoMapper.batchInsert(checkPointInfos);
        }
        info.setCheckPoint(gson.toJson(checkPointInfos));
    }

    private Pair<Integer, String> checkHttpSceneApiParam(HttpApiInfoDTO apiInfoDTO) {
        if (apiInfoDTO.getApiUrl() == null) {
            return Pair.of(-1, "接口url");
        }
        if (apiInfoDTO.getApiRequestType() == null) {
            return Pair.of(-1, "接口请求方式必传");
        }
        if (apiInfoDTO.getApiOrder() == null) {
            return Pair.of(-1, "接口顺序必传");
        }
        if (apiInfoDTO.getApiRequestType() == Constants.HTTP_REQ_POST && apiInfoDTO.getContentType() == null) {
            return Pair.of(-1, "参数类型必传");
        }
        if (apiInfoDTO.getRequestTimeout() == null) {
            return Pair.of(-1, "接口超时时间必传");
        }
        if (apiInfoDTO.getNeedLogin() == null) {
            apiInfoDTO.setNeedLogin(false);
        }

        return Pair.of(0, "success");
    }

    private Pair<Integer, String> checkDubboSceneApiParam(DubboApiInfoDTO apiInfoDTO) {
        if (apiInfoDTO.getServiceName() == null) {
            return Pair.of(-1, "dubbo 服务名必传");
        }
        if (apiInfoDTO.getMethodName() == null) {
            return Pair.of(-1, "dubbo 方法名必传");
        }
        if (apiInfoDTO.getGroup() == null) {
            apiInfoDTO.setGroup("");
        }
        if (apiInfoDTO.getVersion() == null) {
            apiInfoDTO.setVersion("");
        }
        if (apiInfoDTO.getApiOrder() == null) {
            return Pair.of(-1, "接口顺序必传");
        }
        if (apiInfoDTO.getRequestTimeout() == null) {
            return Pair.of(-1, "接口超时时间必传");
        }
        return Pair.of(0, "success");
    }

    @Override
    public Result<List<DubboService>> loadDubboApiServices(String keyword, String env) {
        DubboServiceList serviceList = new DubboServiceList();
        String serviceListStr = "";

        String DEFAULT_NAMESPACE = "";
        if (NACOS_CN_ONLINE.equals(env)) {
            serviceListStr = nacosNamingOl.serviceList2(DEFAULT_NAMESPACE, 1, 50, keyword, olNacosAccessToken);
        }else if (NACOS_ST.equals(env)){
            serviceListStr = nacosNamingSt.serviceList2(DEFAULT_NAMESPACE, 1, 50, keyword, stNacosAccessToken);
        }
        if (Objects.nonNull(serviceListStr) && StringUtils.isNotEmpty(serviceListStr)) {
            try {
                serviceList = gson.fromJson(serviceListStr, new TypeToken<DubboServiceList>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                log.error("获取nacos服务列表失败,serviceList:{}", serviceListStr);
                this.refreshOlNacosToken();
                this.refreshStNacosToken();
                try {
                    if (NACOS_CN_ONLINE.equals(env)) {
                        serviceListStr = nacosNamingOl.serviceList2(DEFAULT_NAMESPACE, 1, 50, keyword, olNacosAccessToken);
                    }else if (NACOS_ST.equals(env)){
                        serviceListStr = nacosNamingSt.serviceList2(DEFAULT_NAMESPACE, 1, 50, keyword, stNacosAccessToken);
                    }
                    serviceList = gson.fromJson(serviceListStr, new TypeToken<DubboServiceList>() {
                    }.getType());
                } catch (Exception ex) {
                    return Result.fail(500, "获取nacos服务列表失败");
                }
            }
        }

        if (Objects.nonNull(serviceList)) {
            return Result.success(serviceList.getServiceList().stream().filter(service -> (service.getHealthyInstanceCount() > 0 && service.getName().startsWith("providers:"))).collect(Collectors.toList()));
        } else {
            return Result.success(new ArrayList<>());
        }
    }

    @Override
    public Result<List<String>> getServiceMethod(String serviceName, String env) throws NacosException {
        List<String> methodNames;
        List<Instance> instanceList;

        switch (env) {
            case Constants.NACOS_ST:
                instanceList = nacosNamingSt.getAllInstances(serviceName);
                break;
            case Constants.NACOS_CN_ONLINE:
                instanceList = nacosNamingOl.getAllInstances(serviceName);
                break;
            default:
                instanceList = new ArrayList<>();
        }

        if (Objects.nonNull(instanceList) && !instanceList.isEmpty()) {
            Instance instance = instanceList.get(0);
            String[] methodsArr = instance.getMetadata().getOrDefault("methods", "").split(",");
            methodNames = Arrays.stream(methodsArr).collect(Collectors.toList());
            return Result.success(methodNames);
        }
        return Result.success(new ArrayList<>());
    }

    public List<SceneApiInfoItemBasic> getBasicInfosByIds(List<Integer> apiIdList) {
        if (apiIdList == null || apiIdList.isEmpty()) {
            return new ArrayList<>();
        }
        SceneApiInfoExample example = new SceneApiInfoExample();
        example.createCriteria().andIdIn(apiIdList);
        List<SceneApiInfo> poList = this.sceneApiInfoMapper.selectByExample(example);

        return poList.stream().map((po) -> SceneApiInfoItemBasic.builder()
                .apiID(po.getId())
                .apiName(po.getApiName())
                .build()).collect(Collectors.toList());
    }

    @Override
    public Result<String> getApiUrlById(Integer apiId) {
        SceneApiInfo apiInfo = sceneApiInfoMapper.selectByPrimaryKey(apiId);
        if (apiInfo == null) {
            return Result.success("");
        }
        if (apiInfo.getApiType() == CASE_TYPE_HTTP) {
            return Result.success(apiInfo.getApiUrl());
        } else if (apiInfo.getApiType() == CASE_TYPE_RPCX) {
            return Result.success(apiInfo.getServiceName() + "|" + apiInfo.getMethodName());
        }
        return Result.success("");
    }

    private Pair<Integer, List<FormParamValue>> parseGetUrl(String url) {
        //url 例: http://www.baidu.com?a=b&b=2
        List<FormParamValue> kvPairs = new ArrayList<>();
        if (url.contains("?")) {
            String[] urlArr = url.split("\\?", 2);
            if (urlArr.length != 2) {
                return Pair.of(-1, null);
            }
            //[a=b,b=2]
            String[] kvStrPair = urlArr[1].split("&");
            for (String s : kvStrPair) {
                String[] kAndV = s.split("=", 2);
                kvPairs.add(new FormParamValue(kAndV[0], kAndV[1]));
            }
        }
        return Pair.of(0, kvPairs);
    }

    public void refreshStNacosToken() {
        //测试环境nacos
        try {
            NacosLoginInfo stNacosLoginInfo = new Gson().fromJson(nacosNamingSt.login(nacosInfo.getUsernameSt(), nacosInfo.getPasswordSt()), NacosLoginInfo.class);
            if (null != stNacosLoginInfo && StringUtils.isNotEmpty(stNacosLoginInfo.getAccessToken())) {
                stNacosAccessToken = stNacosLoginInfo.getAccessToken();
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

    private void refreshOlNacosToken() {
        //中国区线上环境nacos
        try {
            NacosLoginInfo olNacosLoginInfo = new Gson().fromJson(nacosNamingOl.login(nacosInfo.getUsernameOl(), nacosInfo.getPasswordOl()), NacosLoginInfo.class);
            if (null != olNacosLoginInfo && StringUtils.isNotEmpty(olNacosLoginInfo.getAccessToken())) {
                olNacosAccessToken = olNacosLoginInfo.getAccessToken();
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

}
