package com.xiaomi.mock.service.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.common.Result;
import com.xiaomi.mock.bo.EditMockDataBo;
import com.xiaomi.mock.bo.EnableMockBo;
import com.xiaomi.mock.bo.MockProxyBo;
import com.xiaomi.mock.dao.ApiMockDao;
import com.xiaomi.mock.uitl.Md5Utils;
import com.xiaomi.mock.entity.ApiMockData;
import com.xiaomi.mock.service.ApiMockService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author zhenxing.dong
 * @Date 2021/8/9 11:08
 */
@Service
@Slf4j
public class ApiMockServiceImpl implements ApiMockService {

    @Resource
    private ApiMockDao apiMockDao;

    private static final Gson gson = new Gson();

    private ScriptEngine engine;

    @PostConstruct
    public void init() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("javascript");
        // load mockjs
        try {
            InputStream is = this.getClass().getResourceAsStream("/mock.js");
            Reader fileReader = new InputStreamReader(is);
            engine.eval(fileReader);
            engine.eval("var Random = Mock.Random;");
        } catch (Exception e) {
            log.error("MockServiceImpl.init:{}", e.getMessage());
        }
    }

    @Override
    public Object getMockDataByProxyUrl(String proxyUrl, String paramsMd5, Map<String, String> headers, JsonElement paramJson) throws ScriptException {
        ApiMockData mockData = apiMockDao.getApiMockInfoByProxyUrl(proxyUrl, paramsMd5);
        if (mockData == null) {
            paramsMd5 = Md5Utils.getMD5("");
            mockData = apiMockDao.getApiMockInfoByProxyUrl(proxyUrl, paramsMd5);
            if (mockData == null) {
                return "api dose not exist";
            }
        }
        return getMockDataByApi(mockData.getUrl(), mockData.getParamsMd5(), headers, paramJson);
    }

    @Override
    public Object getMockDataByApi(String url, String paramsMd5, Map<String, String> headers, JsonElement paramJson) throws ScriptException {
        Map<String, Object> resultMap = new HashMap<>();
        ApiMockData mockData = apiMockDao.getApiMockResult(url, paramsMd5);
        Object mockResult = null;
        if (mockData != null) {
            if (engine instanceof Invocable) {
                // call js script，get the result
                try {
                    String jsonStr = mockData.getApiMockData().replaceAll("\n", "");
                    mockResult = engine.eval("JSON.stringify(Mock.mock(" + jsonStr + "), null, 2)");
                } catch (ScriptException e) {
                    log.error(e.getMessage());
                }
            }
            mockData.setApiMockData((String) mockResult);
            if (mockData.getEnable()) {
                if (null != mockData.getUseMockScript() && mockData.getUseMockScript() && null != mockData.getMockScript()
                        && (null != headers || null != paramJson)
                ) {
                    Map<String,String> cookie = new HashMap<>();
                    if (headers != null){
                        if (headers.containsKey("Cookie")){
                            String cookies = headers.get("Cookie");
                            String[] cookiesArr = cookies.split(";");
                            for (String cookieStr :
                                    cookiesArr) {
                                String[] kv = cookieStr.split("=", 2);
                                cookie.put(kv[0],kv[1]);
                            }
                        }
                        engine.put("header",headers);
                        engine.put("cookie",cookie);
                    }
                    if (paramJson != null){
                        Map<String,Object> map = gson.fromJson(gson.toJson(paramJson),new TypeToken<HashMap<String,Object>>() {
                        }.getType());
                        engine.put("params",map);
                    }else {
                        engine.put("params",new HashMap<>());
                    }
                    engine.put("mockJson",mockData.getApiMockData());
                    engine.eval(mockData.getMockScript());
                    Object result = engine.get("mockJson");
                    if (result instanceof String){
                        mockData.setApiMockData((String) result);
                    }else {
                        mockData.setApiMockData(gson.toJson(result));
                    }
                }
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(mockData.getApiMockData());
                if (element instanceof JsonObject || element instanceof JsonArray) {
                    return element;
                } else {
                    try {
                        Integer.parseInt(mockData.getApiMockData());
                    } catch (NumberFormatException e) {
                        try {
                            Double.parseDouble(mockData.getApiMockData());
                        } catch (NumberFormatException ex) {
                            return gson.fromJson(mockData.getApiMockData(), String.class);
                        }
                        return gson.fromJson(mockData.getApiMockData(), Double.class);
                    }
                    return gson.fromJson(mockData.getApiMockData(), Integer.class);
                }
            } else {
                resultMap.put("403", "forbidden,please turn on the mock except");
                return resultMap;
            }
        } else {
            return "api does not exist";
        }
    }

    @Override
    public Result<Boolean> editMockData(EditMockDataBo bo) {
        if (apiMockDao.getApiMockInfo(bo.getUrl(), bo.getParamsMd5()) == null) {
            ApiMockData apiMockData = new ApiMockData();
            apiMockData.setUrl(bo.getUrl());
            apiMockData.setApiMockData(bo.getMockData());
            apiMockData.setParamsMd5(bo.getParamsMd5());
            apiMockData.setMockExpID(Integer.parseInt(bo.getMockExpID()));
            apiMockData.setEnable(true);
            apiMockData.setMockProxyUrl(bo.getProxyUrl());
            apiMockData.setUseMockScript(bo.getUseMockScript() > 0);
            apiMockData.setMockScript(bo.getMockScript());
            if (apiMockDao.addApiMockInfo(apiMockData)) {
                return new Result<>(200, "success", true);
            } else {
                return new Result<>(500, "add api mock data error", false);
            }
        } else {
            boolean ok;
            if (bo.getEnable() > 0) {
                ok = apiMockDao.editApiMockResult(bo.getUrl(), bo.getMockData(), bo.getParamsMd5(), Integer.parseInt(bo.getMockExpID()), true, bo.getUseMockScript() > 0, bo.getMockScript());
            } else {
                ok = apiMockDao.editApiMockResult(bo.getUrl(), bo.getMockData(), bo.getParamsMd5(), Integer.parseInt(bo.getMockExpID()), false, bo.getUseMockScript() > 0, bo.getMockScript());
            }
            if (ok) {
                return new Result<>(200, "success", true);
            } else {
                return new Result<>(500, "update api mock data error", false);
            }
        }
    }

    @Override
    public Result<Boolean> addUrlProxy(MockProxyBo bo) {
        ApiMockData mockData = apiMockDao.getApiMockInfo(bo.getOriginUrl(), bo.getParamMd5());
        if (Objects.isNull(mockData)) {
            return new Result<>(500, "add proxy url error", false);
        }
        mockData.setMockProxyUrl(bo.getNewUrl());
        apiMockDao.editMockProxy(mockData);
        return new Result<>(200, "success", true);
    }

    @Override
    public Result<Boolean> enableApiMock(EnableMockBo bo) {
        if (apiMockDao.enableApiMock(bo)) {
            return new Result<>(200, "success", true);
        } else {
            return new Result<>(500, "update api mock status error", false);
        }
    }
}
