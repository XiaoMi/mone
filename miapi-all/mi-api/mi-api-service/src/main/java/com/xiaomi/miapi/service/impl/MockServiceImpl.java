package com.xiaomi.miapi.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.bo.MockServerInfo;
import com.xiaomi.miapi.pojo.*;
import com.xiaomi.miapi.util.HttpUtils;
import com.xiaomi.miapi.util.Md5Utils;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.HttpResult;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.bo.FormBo;
import com.xiaomi.miapi.service.MockService;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.mapper.*;
import com.xiaomi.youpin.codegen.bo.HttpJsonParamsBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Service
@Slf4j
public class MockServiceImpl implements MockService {

    @Autowired
    private ApiMapper apiMapper;

    @Autowired
    EoDubboApiInfoMapper dubboApiInfoMapper;

    @Autowired
    ApiMockExpectMapper apiMockExpectMapper;

    @Autowired
    private MockServerInfo mockServerInfo;
    private ScriptEngine engine;

    private static final Gson gson = new Gson();

    @PostConstruct
    public void init() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("javascript");
        // load mockjs
        try {
            InputStream is = this.getClass().getResourceAsStream("/mock.js");
            if (is != null){
                Reader fileReader = new InputStreamReader(is);
                engine.eval(fileReader);
                engine.eval("var Random = Mock.Random;");
            }
        } catch (Exception e) {
            log.error("MockServiceImpl.init:{}", e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getMockExpectList(Integer apiID) {
        Map<String, Object> map = new HashMap<>();
        ApiMockExpectExample example = new ApiMockExpectExample();
        example.createCriteria().andApiIdEqualTo(apiID);
        List<ApiMockExpect> apiMockExpects = apiMockExpectMapper.selectByExample(example);
        map.put("expectList", apiMockExpects);
        apiMockExpects.forEach(expect -> {
            if (expect.getIsDefault()) {
                map.put("proxyUrl", expect.getProxyUrl());
            }
        });
        return Result.success(map);
    }

    @Override
    public Result<ApiMockExpect> getMockExpectDetail(Integer mockExpectID) {
        ApiMockExpect apiMockExpect = apiMockExpectMapper.selectByPrimaryKey(mockExpectID);
        if (apiMockExpect != null) {
            return Result.success(apiMockExpect);
        } else {
            return Result.fail(CommonError.InvalidIDParamError);
        }
    }

    @Override
    public Result<Boolean> deleteMockExpect(Integer mockExpectID) {
        int result = apiMockExpectMapper.deleteByPrimaryKey(mockExpectID);
        if (result <= 0) {
            return Result.fail(CommonError.UnknownError);
        } else {
            return Result.success(true);
        }
    }

    @Override
    public Result<Boolean> enableMockExpect(Integer mockExpectID, Integer enable) {
        HttpResult result = null;

        ApiMockExpect expect = apiMockExpectMapper.selectByPrimaryKey(mockExpectID);
        if (expect == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (enable == 1) {
            expect.setEnable(true);
        } else {
            expect.setEnable(false);
        }
        if (apiMockExpectMapper.updateByPrimaryKey(expect) > 0) {
            //update mock-server status
            Map<String, String> params = new HashMap<>();
            params.put("mockExpID", mockExpectID.toString());
            params.put("enable", enable.toString());
            try {
                result = HttpUtils.post( mockServerInfo.getMockServerAddr()+ Consts.ENABLE_MOCK_URL,
                        null,
                        gson.toJson(params),
                        3000);
                return gson.fromJson(result.getContent(), Result.class);
            } catch (Exception e) {
                return Result.fail(CommonError.UnknownError);
            }
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateHttpApiMockData(String opUsername, Integer mockExpID, Integer apiID, String paramsJson, String paramRaw, Integer paramType, String mockExpName, Integer projectID, String mockRule, Integer mockDataType, boolean isDefault, boolean enableMockScript, String mockScript) {

        HttpResult result = null;

        String paramsMd5 = "";
        if (paramType == Consts.JSON_DATA_TYPE) {
            if (StringUtils.isNotEmpty(paramRaw)) {
                paramsMd5 = Md5Utils.getMD5(paramRaw);
            }
        } else {
            //sort the key
            if (StringUtils.isNotEmpty(paramsJson)) {
                List<FormBo> formBos = gson.fromJson(paramsJson, new TypeToken<List<FormBo>>() {
                }.getType());
                formBos.sort(Comparator.comparing(FormBo::getParamKey));

                List<Map<String, Object>> list = new ArrayList<>();
                formBos.forEach(formBo -> {
                    Map<String, Object> map = new HashMap<>(1);
                    map.put(formBo.getParamKey(), formBo.getParamValue());
                    list.add(map);
                });
                paramsMd5 = Md5Utils.getMD5(gson.toJson(list));
            } else {
                paramsMd5 = Md5Utils.getMD5(paramsJson);
            }
        }

        String mockData;
        if (mockDataType == 0) {
            mockData = gson.toJson(parseStructToJson(mockRule,false));
        } else {
            mockData = mockRule;
        }
        ApiMockExpect apiMockExpect = new ApiMockExpect();
        apiMockExpect.setApiId(apiID);
        apiMockExpect.setMockParams(paramsJson);
        apiMockExpect.setMockRequestParamType(paramType);
        apiMockExpect.setMockRequestRaw(paramRaw);
        apiMockExpect.setParamsMd5(paramsMd5);
        apiMockExpect.setMockRule(mockRule);
        apiMockExpect.setMockData(mockData);
        apiMockExpect.setMockDataType(mockDataType);
        apiMockExpect.setIsDefault(isDefault);
        apiMockExpect.setMockExpName(mockExpName);
        apiMockExpect.setUpdateUser(opUsername);
        apiMockExpect.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        apiMockExpect.setEnable(isDefault);
        apiMockExpect.setUseMockScript(enableMockScript);
        apiMockExpect.setMockScript(mockScript);

        Api api = apiMapper.getApiInfo(projectID, apiID);
        if (api == null) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        String apiPathProxy = api.getApiURI();
        if (apiPathProxy.startsWith("/")) {
            apiPathProxy = apiPathProxy.substring(1);
        }
        if (mockExpID == null) {
            ApiMockExpectExample example = new ApiMockExpectExample();
            example.createCriteria().andApiIdEqualTo(apiID).andParamsMd5EqualTo(paramsMd5);
            if (!apiMockExpectMapper.selectByExample(example).isEmpty()) {
                return Result.fail(CommonError.MockExceptAlreadyExist);
            }
            apiMockExpect.setProxyUrl(apiPathProxy);
            apiMockExpectMapper.insert(apiMockExpect);
        } else {
            apiMockExpect.setId(mockExpID);
            if (Objects.isNull(apiMockExpect.getProxyUrl()) || apiMockExpect.getProxyUrl().isEmpty()) {
                apiMockExpect.setProxyUrl(apiPathProxy);
            }
            apiMockExpectMapper.updateByPrimaryKeyWithBLOBs(apiMockExpect);
        }

        Map<String, String> params = new HashMap<>();
        params.put("mockData", mockData);
        params.put("paramsMd5", paramsMd5);
        if (enableMockScript) {
            params.put("useMockScript", "1");
        } else {
            params.put("useMockScript", "0");
        }
        params.put("mockScript", mockScript);
        String md5Location = "";

        md5Location = Md5Utils.getMD5(api.getApiURI());
        String uri = api.getApiURI().replaceAll("/", ":").replaceAll(" ", "");
        params.put("url", String.format(Consts.UPDATE_MOCK_DATA_URL_FORMAT, Consts.HttpMockPrefix + "/" + md5Location, uri));
        params.put("proxyUrl", "/mock/" + apiPathProxy);
        params.put("mockExpID", Integer.toString(apiMockExpect.getId()));
        if (apiMockExpect.getEnable()) {
            params.put("enable", "1");
        } else {
            params.put("enable", "0");
        }
        try {
            result = HttpUtils.post(mockServerInfo.getMockServerAddr() + Consts.HTTP_UPDATE_MOCK_URL,
                    null,
                    gson.toJson(params),
                    3000);
            return gson.fromJson(result.getContent(), Result.class);
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDubboApiMockData(String opUsername, Integer mockExpID, Integer apiID, String paramsJson, String paramRaw, Integer paramType, String mockExpName, Integer projectID, String mockRule, Integer mockDataType, boolean isDefault, boolean enableMockScript, String mockScript) {

        HttpResult result = null;

        Api api = apiMapper.getApiInfo(projectID, apiID);
        if (api == null) {
            return Result.fail(CommonError.APIDoNotExist);
        }

        String mockData;
        if (mockDataType == 0) {
            mockData = gson.toJson(parseStructToJson(mockRule,false));
        } else {
            mockData = mockRule;
        }

        EoDubboApiInfo dubboApiInfo = dubboApiInfoMapper.selectByPrimaryKey(api.getDubboApiId());

        String paramsMd5 = "";
        if (paramType == Consts.JSON_DATA_TYPE) {
            if (StringUtils.isNotEmpty(paramRaw)) {
                paramsMd5 = Md5Utils.getMD5(paramRaw);
            }
        } else {
            //sort the key
            if (StringUtils.isNotEmpty(paramsJson)) {
                List<FormBo> formBos = gson.fromJson(paramsJson, new TypeToken<List<FormBo>>() {
                }.getType());
                formBos.sort(Comparator.comparing(FormBo::getParamKey));
                List<Map<String, Object>> list = new ArrayList<>();
                formBos.forEach(formBo -> {
                    Map<String, Object> map = new HashMap<>(1);
                    map.put(formBo.getParamKey(), formBo.getParamValue());
                    list.add(map);
                });
                paramsMd5 = Md5Utils.getMD5(gson.toJson(list));
            } else {
                paramsMd5 = Md5Utils.getMD5(paramsJson);
            }
        }
        ApiMockExpect apiMockExpect = new ApiMockExpect();
        apiMockExpect.setApiId(apiID);
        apiMockExpect.setMockParams(paramsJson);
        apiMockExpect.setMockRequestRaw(paramRaw);
        apiMockExpect.setMockRequestParamType(paramType);
        apiMockExpect.setParamsMd5(paramsMd5);
        apiMockExpect.setMockRule(mockRule);
        apiMockExpect.setMockData(mockData);
        apiMockExpect.setMockDataType(mockDataType);
        apiMockExpect.setIsDefault(isDefault);
        apiMockExpect.setMockExpName(mockExpName);
        apiMockExpect.setUpdateUser(opUsername);
        apiMockExpect.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        apiMockExpect.setEnable(isDefault);
        apiMockExpect.setUseMockScript(enableMockScript);
        apiMockExpect.setMockScript(mockScript);

        if (mockExpID == null) {
            ApiMockExpectExample example = new ApiMockExpectExample();
            example.createCriteria().andApiIdEqualTo(apiID).andParamsMd5EqualTo(paramsMd5);
            if (!apiMockExpectMapper.selectByExample(example).isEmpty()) {
                return Result.fail(CommonError.MockExceptAlreadyExist);
            }
            apiMockExpect.setProxyUrl(" ");
            apiMockExpectMapper.insert(apiMockExpect);
        } else {
            apiMockExpect.setId(mockExpID);
            if (Objects.isNull(apiMockExpect.getProxyUrl()) || apiMockExpect.getProxyUrl().isEmpty()) {
                apiMockExpect.setProxyUrl(" ");
            }
            apiMockExpectMapper.updateByPrimaryKeyWithBLOBs(apiMockExpect);
        }

        Map<String, String> params = new HashMap<>();

        params.put("mockData", mockData);
        params.put("paramsMd5", paramsMd5);
        String md5Location = Md5Utils.getMD5(Consts.getServiceKey(dubboApiInfo.getApimodelclass(), dubboApiInfo.getApiversion(), dubboApiInfo.getApigroup()));
        params.put("url", String.format(Consts.UPDATE_MOCK_DATA_URL_FORMAT, Consts.MockPrefix + "/" + md5Location, dubboApiInfo.getApiname()));
        params.put("mockExpID", Integer.toString(apiMockExpect.getId()));
        if (apiMockExpect.getEnable()) {
            params.put("enable", "1");
        } else {
            params.put("enable", "0");
        }
        if (enableMockScript) {
            params.put("useMockScript", "1");
        } else {
            params.put("useMockScript", "0");
        }
        params.put("mockScript", mockScript);
        try {
            result = HttpUtils.post(mockServerInfo.getMockServerAddr() + Consts.UPDATE_MOCK_URL,
                    null,
                    gson.toJson(params),
                    3000);
            if (result != null) {
                return gson.fromJson(result.getContent(), Result.class);
            } else {
                return Result.fail(CommonError.UnknownError);
            }
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateGatewayApiMockData(String opUsername, Integer mockExpID, Integer apiID, Integer projectID, String paramsJson, String paramRaw, Integer paramType, String mockExpName, GatewayApiInfo gatewayApiInfo, String mockRule, Integer mockDataType, boolean isDefault, boolean enableMockScript, String mockScript) {
        Api api = apiMapper.getApiInfo(projectID, apiID);

        if (api == null){
            return Result.fail(CommonError.APIDoNotExist);
        }
        HttpResult result = null;

        String paramsMd5 = "";
        if (paramType == Consts.JSON_DATA_TYPE) {
            if (StringUtils.isNotEmpty(paramRaw)) {
                paramsMd5 = Md5Utils.getMD5(paramRaw);
            }
        } else {
            //sort the key
            if (StringUtils.isNotEmpty(paramsJson)) {
                List<FormBo> formBos = gson.fromJson(paramsJson, new TypeToken<List<FormBo>>() {
                }.getType());
                formBos.sort(Comparator.comparing(FormBo::getParamKey));
                List<Map<String, Object>> list = new ArrayList<>();
                formBos.forEach(formBo -> {
                    Map<String, Object> map = new HashMap<>(1);
                    map.put(formBo.getParamKey(), formBo.getParamValue());
                    list.add(map);
                });
                paramsMd5 = Md5Utils.getMD5(gson.toJson(list));
            } else {
                paramsMd5 = Md5Utils.getMD5(paramsJson);
            }
        }
        String mockData;
        //form type
        if (mockDataType == 0) {
            mockData = gson.toJson(parseStructToJson(mockRule,false));
        } else {
            mockData = mockRule;
        }
        ApiMockExpect apiMockExpect = new ApiMockExpect();
        apiMockExpect.setApiId(apiID);
        apiMockExpect.setMockParams(paramsJson);
        apiMockExpect.setMockRequestRaw(paramRaw);
        apiMockExpect.setMockRequestParamType(paramType);
        apiMockExpect.setParamsMd5(paramsMd5);
        apiMockExpect.setMockRule(mockRule);
        apiMockExpect.setMockData(mockData);
        apiMockExpect.setMockDataType(mockDataType);
        apiMockExpect.setIsDefault(isDefault);
        apiMockExpect.setMockExpName(mockExpName);
        apiMockExpect.setUpdateUser(opUsername);
        apiMockExpect.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        apiMockExpect.setEnable(isDefault);
        apiMockExpect.setUseMockScript(enableMockScript);
        apiMockExpect.setMockScript(mockScript);

        String apiPathProxy = api.getApiURI();
        if (apiPathProxy.startsWith("/")) {
            apiPathProxy = apiPathProxy.substring(1);
        }
        if (mockExpID == null) {
            ApiMockExpectExample example = new ApiMockExpectExample();
            example.createCriteria().andApiIdEqualTo(apiID).andParamsMd5EqualTo(paramsMd5);
            if (!apiMockExpectMapper.selectByExample(example).isEmpty()) {
                return Result.fail(CommonError.MockExceptAlreadyExist);
            }
            apiMockExpect.setProxyUrl(apiPathProxy);
            apiMockExpectMapper.insert(apiMockExpect);
        } else {
            apiMockExpect.setId(mockExpID);
            if (Objects.isNull(apiMockExpect.getProxyUrl()) || apiMockExpect.getProxyUrl().isEmpty()) {
                apiMockExpect.setProxyUrl(apiPathProxy);
            }
            apiMockExpectMapper.updateByPrimaryKeyWithBLOBs(apiMockExpect);
        }

        Map<String, String> params = new HashMap<>();
        params.put("mockData", mockData);
        params.put("paramsMd5", paramsMd5);
        params.put("mockExpID", Integer.toString(apiMockExpect.getId()));
        String md5Location;
        if (gatewayApiInfo != null) {
            md5Location = Md5Utils.getMD5(gatewayApiInfo.getUrl());
            String uri = gatewayApiInfo.getUrl().replaceAll("/", ":");
            params.put("url", String.format(Consts.UPDATE_MOCK_DATA_URL_FORMAT, Consts.GatewayMockPrefix + "/" + md5Location, uri));
        } else {
            md5Location = Md5Utils.getMD5(api.getApiURI());
            String uri = api.getApiURI().replaceAll("/", ":");
            params.put("url", String.format(Consts.UPDATE_MOCK_DATA_URL_FORMAT, Consts.GatewayMockPrefix + "/" + md5Location, uri));
        }
        if (apiMockExpect.getEnable()) {
            params.put("enable", "1");
        } else {
            params.put("enable", "0");
        }
        if (enableMockScript) {
            params.put("useMockScript", "1");
        } else {
            params.put("useMockScript", "0");
        }
        params.put("mockScript", mockScript);

        params.put("proxyUrl", "/mock/" + apiPathProxy);
        try {
            result = HttpUtils.post(mockServerInfo.getMockServerAddr() + Consts.GATEWAY_UPDATE_MOCK_URL,
                    null,
                    gson.toJson(params),
                    3000);
            return gson.fromJson(result.getContent(), Result.class);
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    public Result<String> dubboApiMock(String url) {
        HttpResult result = null;
        try {
            result = HttpUtils.get(url,
                    null,
                    null,
                    3000);

            Gson gson = new Gson();
            return gson.fromJson(result.getContent(), Result.class);
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    public Result<String> httpApiMock(String url) {
        String[] mockUrlArr = url.split(":");
        if (mockUrlArr.length != 2) {
            return Result.fail(CommonError.InvalidParamError);
        }
        HttpResult result = null;
        try {
            result = HttpUtils.get(url,
                    null,
                    null,
                    3000);

            Gson gson = new Gson();
            return gson.fromJson(result.getContent(), Result.class);
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @Override
    public Result<String> gatewayApiMock(String url) {
        String[] mockUrlArr = url.split(":");
        if (mockUrlArr.length != 2) {
            return Result.fail(CommonError.InvalidParamError);
        }
        HttpResult result = null;
        try {
            result = HttpUtils.get(url,
                    null,
                    null,
                    3000);

            Gson gson = new Gson();
            return gson.fromJson(result.getContent(), Result.class);
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
    }

    public Object generateParamValue(Integer paramType, String rule) {
        if (StringUtils.isEmpty(rule)) {
            switch (paramType) {
                case 3:
                    //int
                    return RandomUtils.nextInt();
                case 4:
                    //float
                    return RandomUtils.nextFloat();
                case 5 | 14:
                    //double
                    return RandomUtils.nextDouble();
                case 8:
                    //boolean
                    return RandomUtils.nextBoolean();
                case 11:
                    //long
                    return RandomUtils.nextLong();
                default:
                    return RandomStringUtils.randomAlphanumeric(10);
            }
        } else {
            return "no impl now";
        }
    }

    @Override
    public Object generateDefaultValue(Integer paramType) {
        switch (paramType) {
            case 3:
                //int
                return 0;
            case 4:
                //float
                return 0.0;
            case 5 | 14:
                //double
                return 0.0;
            case 8:
                //boolean
                return false;
            case 11:
                //long
                return 0;
            default:
                return "";
        }
    }

    @Override
    public Object parseStructToJson(String paramStruct,boolean randomGen) {
        if (StringUtils.isEmpty(paramStruct)) {
            return "";
        }
        List<HttpJsonParamsBo> rspParamList = gson.fromJson(paramStruct, new TypeToken<List<HttpJsonParamsBo>>() {
        }.getType());
        if (rspParamList.isEmpty()) {
            return "";
        }
        //基本类型
        if (rspParamList.get(0).getParamType() != 12 && rspParamList.get(0).getParamType() != 13 && rspParamList.get(0).getParamType() == 2) {
            if (StringUtils.isNotEmpty(rspParamList.get(0).getParamValue())) {
                return rspParamList.get(0).getParamValue();
            } else {
                return generateParamValue(rspParamList.get(0).getParamType(), "");
            }
        }
        //object and array
        else if (rspParamList.get(0).getParamType() == 12) {
            List<Object> listResult = new ArrayList<>();
            List<HttpJsonParamsBo> paramsBos = rspParamList.get(0).getChildList();
            for (HttpJsonParamsBo paramsBo : paramsBos) {
                recursiveAppendJson(paramsBo, listResult,randomGen);
            }
            return listResult;
        } else if (rspParamList.get(0).getParamType() == 13) {
            Map<String, Object> objResult = new HashMap<>();
            List<HttpJsonParamsBo> paramsBos = rspParamList.get(0).getChildList();
            if (Objects.nonNull(paramsBos)) {
                for (HttpJsonParamsBo b :
                        paramsBos) {
                    recursiveAppendJson(b, objResult,randomGen);
                }
            }
            return objResult;
        }
        return "";
    }

    @Override
    public Object parseStructToJsonByDefault(String paramStruct) {
        if (StringUtils.isEmpty(paramStruct)) {
            return "";
        }
        List<HttpJsonParamsBo> rspParamList = gson.fromJson(paramStruct, new TypeToken<List<HttpJsonParamsBo>>() {
        }.getType());
        if (rspParamList.isEmpty()) {
            return "";
        }
        //basic type
        if (rspParamList.get(0).getParamType() != 12 && rspParamList.get(0).getParamType() != 13 && rspParamList.get(0).getParamType() == 2) {
            if (StringUtils.isNotEmpty(rspParamList.get(0).getParamValue())) {
                return rspParamList.get(0).getParamValue();
            } else {
                return generateDefaultValue(rspParamList.get(0).getParamType());
            }
        }
        //object and array
        else if (rspParamList.get(0).getParamType() == 12) {
            List<Object> listResult = new ArrayList<>();
            List<HttpJsonParamsBo> paramsBos = rspParamList.get(0).getChildList();
            for (HttpJsonParamsBo paramsBo : paramsBos) {
                recursiveAppendJson(paramsBo, listResult,true);
            }
            return listResult;
        } else if (rspParamList.get(0).getParamType() == 13) {
            Map<String, Object> objResult = new HashMap<>();
            List<HttpJsonParamsBo> paramsBos = rspParamList.get(0).getChildList();
            if (Objects.nonNull(paramsBos)) {
                for (HttpJsonParamsBo b :
                        paramsBos) {
                    recursiveAppendJson(b, objResult,true);
                }
            }
            return objResult;
        }
        return "";
    }

    @Override
    public Result<Object> previewMockData(Object mockData) {
        String mockJson = gson.toJson(mockData);
        Object mockResult = null;
        if (engine instanceof Invocable) {
            // call the js func
            try {
                mockResult = engine.eval("JSON.stringify(Mock.mock(" + mockJson + "), null, 2)");
            } catch (ScriptException e) {
                log.error(e.getMessage());
            }
        }
        if (null == mockResult) {
            return Result.fail(CommonError.ErrorMockJsRule);
        }
        return Result.success(mockResult);
    }

    @SuppressWarnings("unchecked")
    private void recursiveAppendJson(HttpJsonParamsBo paramsBo, Object parentContainer, boolean randomGen) {
        if (parentContainer instanceof ArrayList<?>) {
            if (paramsBo.getParamType() != 12 && paramsBo.getParamType() != 13 && paramsBo.getParamType() != 2) {
                if (StringUtils.isNotEmpty(paramsBo.getParamValue())) {
                    ((List<Object>) parentContainer).add(paramsBo.getParamValue());
                } else {
                    if (randomGen){
                        ((List<Object>) parentContainer).add(generateParamValue(paramsBo.getParamType(), ""));
                    }else {
                        ((List<Object>) parentContainer).add(generateDefaultValue(paramsBo.getParamType()));
                    }
                }
            } else if (paramsBo.getParamType() == 12) {
                //array
                List<Object> paramList = new ArrayList<>();
                paramsBo.getChildList().forEach(param -> recursiveAppendJson(param, paramList,randomGen));
                ((List<Object>) parentContainer).add(paramList);
            } else if (paramsBo.getParamType() == 13) {
                //obj
                Map<String, Object> paramMap = new HashMap<>();
                if (paramsBo.getChildList() != null){
                    paramsBo.getChildList().forEach(param -> recursiveAppendJson(param, paramMap,randomGen));
                }
                ((List<Object>) parentContainer).add(paramMap);
            }
        } else if (parentContainer instanceof Map) {
            //basic type
            if (paramsBo.getParamType() != 12 && paramsBo.getParamType() != 13 && paramsBo.getParamType() != 2) {
                String key = paramsBo.getParamKey();
                if (StringUtils.isNotEmpty(paramsBo.getRule())) {
                    key += "|" + paramsBo.getRule();
                }
                if (StringUtils.isNotEmpty(paramsBo.getParamValue())) {
                    ((Map<String, Object>) parentContainer).put(key, paramsBo.getParamValue());
                } else {
                    if (randomGen){
                        ((Map<String, Object>) parentContainer).put(key, generateParamValue(paramsBo.getParamType(), ""));
                    }else {
                        ((Map<String, Object>) parentContainer).put(key, generateDefaultValue(paramsBo.getParamType()));
                    }
                }
            } else if (paramsBo.getParamType() == 12) {
                String key = paramsBo.getParamKey();
                if (StringUtils.isNotEmpty(paramsBo.getRule())) {
                    key += "|" + paramsBo.getRule();
                }
                //array
                List<Object> paramList = new ArrayList<>();
                if (Objects.nonNull(paramsBo.getChildList())) {
                    paramsBo.getChildList().forEach(param -> recursiveAppendJson(param, paramList,randomGen));
                }
                ((Map<String, Object>) parentContainer).put(key, paramList);
            } else if (paramsBo.getParamType() == 13) {
                String key = paramsBo.getParamKey();
                if (StringUtils.isNotEmpty(paramsBo.getRule())) {
                    key += "|" + paramsBo.getRule();
                }
                //obj
                Map<String, Object> paramMap = new HashMap<>();
                if (Objects.nonNull(paramsBo.getChildList())) {
                    paramsBo.getChildList().forEach(param -> recursiveAppendJson(param, paramMap,randomGen));
                }
                ((Map<String, Object>) parentContainer).put(key, paramMap);
            }
        }
    }

    @Override
    public Result selfConfMockUrl(Integer expectId, String origin, String newUrl) {
        ApiMockExpect expect = apiMockExpectMapper.selectByPrimaryKey(expectId);
        expect.setProxyUrl(newUrl);
        apiMockExpectMapper.updateByPrimaryKey(expect);

        Map<String, String> params = new HashMap<>();
        params.put("originUrl", "/" + origin);
        params.put("newUrl", "/mock/" + newUrl);
        String md5Location = "";

        md5Location = Md5Utils.getMD5(md5Location);
        params.put("paramMd5", md5Location);
        HttpResult result;

        try {
            result = HttpUtils.post(mockServerInfo.getMockServerAddr() + Consts.ADD_PROXY_URL,
                    null,
                    gson.toJson(params),
                    3000);
            return gson.fromJson(result.getContent(), Result.class);
        } catch (Exception e) {
            return Result.fail(CommonError.UnknownError);
        }
    }
}
