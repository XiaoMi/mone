package com.xiaomi.miapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaomi.miapi.common.pojo.*;
import com.xiaomi.miapi.util.Md5Utils;
import com.xiaomi.miapi.service.ApiIndexService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.mapper.*;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ApiIndexServiceImpl implements ApiIndexService {

    @Autowired
    private ApiIndexMapper apiIndexMapper;

    @Autowired
    private IndexInfoMapper indexInfoMapper;

    @Autowired
    EoDubboApiInfoMapper dubboApiInfoMapper;

    @Autowired
    GatewayApiInfoMapper gatewayApiInfoMapper;

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    ApiRequestExpMapper requestExpMapper;

    @Autowired
    ApiResponseExpMapper responseExpMapper;

    @Override
    public Result<Boolean> batchGroupApis(String apiID, Integer indexID, String username) {
        JSONArray jsonArray = JSONArray.parseArray(apiID);
        List<Integer> apiIDs = new ArrayList<>();
        List<ApiIndex> apiIndices = new ArrayList<>();
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (Object o : jsonArray) {
                apiIDs.add((Integer) o);
            }
        }
        for (Integer apiId :
                apiIDs) {
            ApiIndex apiIndex = new ApiIndex();
            apiIndex.setIndexId(indexID);
            apiIndex.setApiId(apiId);

            ApiIndexExample example = new ApiIndexExample();
            example.createCriteria().andIndexIdEqualTo(indexID).andApiIdEqualTo(apiId);
            List<ApiIndex> apiIndexList = apiIndexMapper.selectByExample(example);
            //已存在的就跳过
            if (apiIndexList != null && apiIndexList.size() != 0) {
                continue;
            }
            apiIndices.add(apiIndex);
        }
        if (apiIndices.isEmpty()) {
            return Result.success(true);

        }
        apiIndexMapper.batchInsert(apiIndices);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> removeApiFromIndex(Integer apiID, Integer indexID, String username) {
        ApiIndexExample example = new ApiIndexExample();
        example.createCriteria().andIndexIdEqualTo(indexID).andApiIdEqualTo(apiID);
        List<ApiIndex> apiIndexList = apiIndexMapper.selectByExample(example);
        if (apiIndexList != null && apiIndexList.size() != 0) {
            if (apiIndexMapper.deleteByPrimaryKey(apiIndexList.get(0).getId()) > 0) {
                return Result.success(true);
            } else {
                return Result.fail(CommonError.UnknownError);
            }
        } else {
            return Result.fail(CommonError.InvalidParamError);
        }
    }

    @Override
    public Result<Integer> addIndex(Integer projectId, String indexName, String description, String indexDoc, String username) {
        IndexInfo indexInfo = new IndexInfo();
        indexInfo.setProjectId(projectId);
        indexInfo.setIndexName(indexName);
        indexInfo.setDescription(description);
        indexInfo.setIndexDoc(indexDoc);
        indexInfoMapper.insert(indexInfo);
        return Result.success(indexInfo.getIndexId());
    }


    @Override
    public Result<Integer> editIndex(String indexName, Integer indexId, String description, String indexDoc, String username) {
        IndexInfo indexInfo = indexInfoMapper.selectByPrimaryKey(indexId);
        if (indexInfo == null) {
            return Result.fail(CommonError.IndexDoNotExist);
        }
        indexInfo.setDescription(description);
        indexInfo.setIndexName(indexName);
        indexInfo.setIndexDoc(indexDoc);
        indexInfoMapper.updateByPrimaryKeyWithBLOBs(indexInfo);
        return Result.success(indexId);
    }

    @Override
    @Transactional
    public Result<Boolean> deleteIndex(Integer indexId, String username) {
        if (indexInfoMapper.deleteByPrimaryKey(indexId) > 0) {
            ApiIndexExample example = new ApiIndexExample();
            example.createCriteria().andIndexIdEqualTo(indexId);
            apiIndexMapper.deleteByExample(example);
            return Result.success(true);
        }
        return Result.fail(CommonError.UnknownError);
    }

    @Override
    public Result<List<IndexInfo>> getIndexList(Integer projectId) {
        IndexInfoExample example = new IndexInfoExample();
        example.createCriteria().andProjectIdEqualTo(projectId);
        List<IndexInfo> indexInfos = indexInfoMapper.selectByExampleWithBLOBs(example);
        return Result.success(indexInfos);
    }

    @Override
    public Result<List<Map<String, Object>>> getIndexPageInfo(String indexIDs) {
        List<Map<String, Object>> rt = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(indexIDs);
        List<Integer> indexIDArr = new ArrayList<Integer>();
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (Object o : jsonArray) {
                indexIDArr.add((Integer) o);
            }
        }
        for (Integer indexID : indexIDArr
             ) {
            IndexInfo indexInfo = indexInfoMapper.selectByPrimaryKey(indexID);
            if (!Optional.ofNullable(indexInfo).isPresent()) {
                return Result.success(rt);
            }

            Map<String, Object> resultMap = new HashMap<>();

            List<Map<String, Object>> apiInfoList = new ArrayList<>();
            ApiIndexExample example = new ApiIndexExample();
            example.createCriteria().andIndexIdEqualTo(indexID);
            List<ApiIndex> apiIndices = apiIndexMapper.selectByExample(example);

            List<Integer> apiIDs = new ArrayList<>(apiIndices.size());
            apiIndices.forEach(apiIndex -> apiIDs.add(apiIndex.getApiId()));
            if (apiIDs.isEmpty()){
                continue;
            }
            List<Api> apiList = apiMapper.getApiListByIDs(apiIDs);
            for (Api api :
                    apiList) {
                Map<String, Object> tmpMap = new HashMap<>();
                tmpMap.put("protocol", api.getApiProtocol());
                //http接口
                if (api.getApiProtocol() == Consts.HTTP_API_TYPE) {
                    Map<String, Object> result = apiMapper.getApiById(api.getApiID());
                    Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
                    if (apiJson != null && !apiJson.isEmpty()) {
                        Map<String, Object> baseInfo = (JSONObject) apiJson.get("baseInfo");
                        baseInfo.put("projectID", result.get("projectID"));
                        baseInfo.put("apiID", result.get("apiID"));
                        apiJson.put("baseInfo", baseInfo);
                        Map<String, Object> mockInfo = new HashMap<>();
                        String apiURI = baseInfo.getOrDefault("apiURI", "").toString();
                        String uriMd5 = Md5Utils.getMD5(apiURI);
                        String uri = apiURI.replaceAll("/", ":");
                        mockInfo.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT, Consts.MockUrlPrefix + Consts.HttpMockPrefix, uriMd5, uri));
                        apiJson.put("mockInfo", mockInfo);
                        ApiRequestExpExample reqExample = new ApiRequestExpExample();
                        reqExample.createCriteria().andApiIdEqualTo(api.getApiID());
                        List<ApiRequestExp> reqExpList = requestExpMapper.selectByExampleWithBLOBs(reqExample);
                        apiJson.put("reqExpList",reqExpList);

                        ApiResponseExpExample respExample = new ApiResponseExpExample();
                        respExample.createCriteria().andApiIdEqualTo(api.getApiID());
                        List<ApiResponseExp> respExpList = responseExpMapper.selectByExampleWithBLOBs(respExample);
                        apiJson.put("respExpList",respExpList);
                    }
                    tmpMap.put("apiInfo", apiJson);
                } else if (api.getApiProtocol() == Consts.DUBBO_API_TYPE) {
                    Map<String,Object> map = new HashMap<>();
                    //dubbo接口
                    EoDubboApiInfo dubboApiInfo = dubboApiInfoMapper.selectByPrimaryKey(api.getDubboApiId());
                    map.put("dubboApiBaseInfo", dubboApiInfo);
                    map.put("projectID", api.getProjectID());
                    map.put("groupID", api.getGroupID());
                    map.put("apiNoteType", api.getApiNoteType());
                    map.put("apiRemark", api.getApiRemark());
                    map.put("apiDesc", api.getApiDesc());
                    map.put("apiStatus", api.getApiStatus());
                    map.put("name",api.getApiName());
                    map.put("apiID",api.getApiID());

                    String md5Location = Md5Utils.getMD5(Consts.getServiceKey(dubboApiInfo.getApimodelclass(), dubboApiInfo.getApiversion(), dubboApiInfo.getApigroup()));

                    map.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT, Consts.MockUrlPrefix + Consts.MockPrefix, md5Location, dubboApiInfo.getApiname()));
                    ApiRequestExpExample reqExample = new ApiRequestExpExample();
                    reqExample.createCriteria().andApiIdEqualTo(api.getApiID());
                    List<ApiRequestExp> reqExpList = requestExpMapper.selectByExampleWithBLOBs(reqExample);
                    map.put("reqExpList", reqExpList);

                    ApiResponseExpExample respExample = new ApiResponseExpExample();
                    respExample.createCriteria().andApiIdEqualTo(api.getApiID());
                    List<ApiResponseExp> respExpList = responseExpMapper.selectByExampleWithBLOBs(respExample);
                    map.put("respExpList", respExpList);
                    tmpMap.put("apiInfo",map);
                } else if (api.getApiProtocol() == Consts.GATEWAY_API_TYPE) {
                    Map<String,Object> map = new HashMap<>();
                    //网关接口
                    GatewayApiInfo gatewayApiInfo = gatewayApiInfoMapper.selectByPrimaryKey(api.getGatewayApiId().longValue());
                    map.put("gatewayApiBaseInfo", gatewayApiInfo);
                    map.put("projectID", api.getProjectID());
                    map.put("groupID", api.getGroupID());
                    map.put("apiStatus", api.getApiStatus());
                    map.put("apiID",api.getApiID());
                    Map<String, Object> result = apiMapper.getApiById(api.getApiID());
                    if (result != null && !result.isEmpty()) {
                        Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
                        if (apiJson != null && !apiJson.isEmpty()) {
                            map.put("headerInfo", apiJson.get("headerInfo"));
                            map.put("requestInfo", apiJson.get("requestInfo"));
                            map.put("resultInfo", apiJson.get("resultInfo"));
                        }
                    }
                    //文档信息
                    map.put("apiNoteType", api.getApiNoteType());
                    map.put("apiRemark", api.getApiRemark());
                    map.put("apiDesc", api.getApiDesc());
                    String md5Location = Md5Utils.getMD5(gatewayApiInfo.getUrl());
                    String uri = gatewayApiInfo.getUrl().replaceAll("/", ":");
                    map.put("mockUrl", String.format(Consts.REQUEST_URL_FORMAT, Consts.MockUrlPrefix + Consts.GatewayMockPrefix, md5Location, uri));
                    ApiRequestExpExample reqExample = new ApiRequestExpExample();
                    reqExample.createCriteria().andApiIdEqualTo(api.getApiID());
                    List<ApiRequestExp> reqExpList = requestExpMapper.selectByExampleWithBLOBs(reqExample);
                    map.put("reqExpList",reqExpList);

                    ApiResponseExpExample respExample = new ApiResponseExpExample();
                    respExample.createCriteria().andApiIdEqualTo(api.getApiID());
                    List<ApiResponseExp> respExpList = responseExpMapper.selectByExampleWithBLOBs(respExample);
                    map.put("respExpList",respExpList);
                    tmpMap.put("apiInfo",map);
                } else if (api.getApiProtocol() == Consts.GRPC_API_TYPE){
                    Map<String, Object> map = new HashMap<>();
                    if (StringUtils.isEmpty(api.getApiEnv())) {
                        map.put("apiEnv", "staging");
                    } else {
                        map.put("apiEnv", api.getApiEnv());
                    }
                    String fullServiceName = extraPrefix(api.getApiURI());
                    String methodName = extraSuffix(api.getApiURI());
                    String serviceName = extraSuffix(fullServiceName);
                    map.put("updateUsername", api.getUpdateUsername());
                    map.put("projectID", api.getProjectID());
                    map.put("groupID", api.getGroupID());
                    map.put("apiNoteType", api.getApiNoteType());
                    map.put("apiRemark", api.getApiRemark());
                    map.put("apiDesc", api.getApiDesc());
                    map.put("apiStatus", api.getApiStatus());
                    map.put("fullApiPath", api.getApiURI());
                    map.put("apiID",api.getApiID());
                    map.put("apiName", api.getApiName());
                    map.put("methodName",methodName);
                    map.put("serviceName", serviceName);

                    Map<String, Object> result = apiMapper.getApi(api.getProjectID(), api.getApiID());
                    Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
                    if (apiJson != null && !apiJson.isEmpty()){
                        map.put("requestInfo", apiJson.get("requestInfo"));
                        map.put("resultInfo", apiJson.get("resultInfo"));
                        map.put("appName",apiJson.get("appName"));
                        map.put("errorCodes", apiJson.get("errorCodes"));
                    }
                    tmpMap.put("apiInfo",map);
                }else {
                    continue;
                }
                apiInfoList.add(tmpMap);
            }
            resultMap.put("apiList",apiInfoList);
            resultMap.put("indexDoc",indexInfo.getIndexDoc());
            resultMap.put("indexName",indexInfo.getIndexName());
            rt.add(resultMap);
        }

        return Result.success(rt);
    }

    /**
     * 获取前缀
     */
    private String extraPrefix(String content) {
        int index = content.lastIndexOf(".");
        return content.substring(0, index);
    }

    /**
     * 获取后缀
     */
    private String extraSuffix(String content) {
        int index = content.lastIndexOf(".");
        return content.substring(index + 1);
    }
}
