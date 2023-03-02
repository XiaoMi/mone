package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.pojo.ApiMockExpect;
import com.xiaomi.miapi.pojo.GatewayApiInfo;

import java.util.Map;

public interface MockService
{

	Result<Map<String,Object>> getMockExpectList(Integer apiID);

	Result<ApiMockExpect> getMockExpectDetail(Integer mockExpectID);

	Result<Boolean> deleteMockExpect(Integer mockExpectID);

	Result<Boolean> enableMockExpect(Integer mockExpectID,Integer enable);

	Result updateHttpApiMockData(String opUsername,Integer mockExpID,Integer apiID,String paramsJson,String paramRaw,Integer paramType,String mockExpName, Integer projectID, String mockRule,Integer mockDataType,boolean isDefault,boolean enableMockScript,String mockScript);

	Result updateDubboApiMockData(String opUsername,Integer mockExpID,Integer apiID,String paramsJson,String paramRaw,Integer paramType,String mockExpName, Integer projectID, String mockRule,Integer mockDataType,boolean isDefault,boolean enableMockScript,String mockScript);

	Result updateGatewayApiMockData(String opUsername, Integer mockExpID, Integer apiID, Integer projectID, String paramsJson, String paramRaw, Integer paramType, String mockExpName, GatewayApiInfo gatewayApiInfo, String mockRule, Integer mockDataType, boolean isDefault,boolean enableMockScript,String mockScript);

	Result<String> gatewayApiMock(String url);

	Result<String> dubboApiMock(String url);

	Result<String> httpApiMock(String url);

	Object generateParamValue(Integer paramType, String rule);

	Object generateDefaultValue(Integer paramType);

	Object parseStructToJson(String paramStruct,boolean randomGen);

	Object parseStructToJsonByDefault(String paramStruct);

	Object previewMockData(Object mockData);

	Result selfConfMockUrl(Integer expectId,String origin,String newUrl);

}
