package com.xiaomi.mock.service;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.common.Result;
import com.xiaomi.mock.bo.EditMockDataBo;
import com.xiaomi.mock.bo.EnableMockBo;
import com.xiaomi.mock.bo.MockProxyBo;

import javax.script.ScriptException;
import java.util.Map;

public interface ApiMockService {
    Object getMockDataByApi(String url, String paramsMd5, Map<String,String> headers, JsonElement paramJson) throws ScriptException;
    Object getMockDataByProxyUrl(String proxyUrl,String paramsMd5,Map<String,String> headers,JsonElement paramJson) throws ScriptException;

    Result<Boolean> editMockData(EditMockDataBo bo);
    Result<Boolean> enableApiMock(EnableMockBo bo);
    Result<Boolean> addUrlProxy(MockProxyBo bo);

}
