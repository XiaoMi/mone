package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.pojo.ApiMockExpect;
import com.xiaomi.miapi.common.pojo.GatewayApiInfo;

import java.util.Map;

/**
 * mock service
 */
public interface MockService
{

	public Result<Map<String,Object>> getMockExpectList(Integer apiID);

	public Result<ApiMockExpect> getMockExpectDetail(Integer mockExpectID);

	public Result<Boolean> deleteMockExpect(Integer mockExpectID);

	public Result<Boolean> enableMockExpect(Integer mockExpectID,Integer enable);

	public Result updateHttpApiMockData(String opUsername,Integer mockExpID,Integer apiID,String paramsJson,String paramRaw,Integer paramType,String mockExpName, Integer projectID, String mockRule,Integer mockDataType,boolean isDefault,boolean enableMockScript,String mockScript);

	//创建或更新dubbo类型接口的Mock信息
	public Result updateDubboApiMockData(String opUsername,Integer mockExpID,Integer apiID,String paramsJson,String paramRaw,Integer paramType,String mockExpName, Integer projectID, String mockRule,Integer mockDataType,boolean isDefault,boolean enableMockScript,String mockScript);

	//创建或更新gateway类型接口的Mock信息
	public Result updateGatewayApiMockData(String opUsername, Integer mockExpID, Integer apiID, Integer projectID, String paramsJson, String paramRaw, Integer paramType, String mockExpName, GatewayApiInfo gatewayApiInfo, String mockRule, Integer mockDataType, boolean isDefault,boolean enableMockScript,String mockScript);

	public Result<String> gatewayApiMock(String url);

	public Result<String> dubboApiMock(String url);

	public Result<String> httpApiMock(String url);

	public Object generateParamValue(Integer paramType, String rule);

	public Object generateDefaultValue(Integer paramType);

	public Object parseStructToJson(String paramStruct,boolean randomGen);

	public Object parseStructToJsonByDefault(String paramStruct);

	public Object previewMockData(Object mockData);

	public Result selfConfMockUrl(Integer expectId,String origin,String newUrl);

}
