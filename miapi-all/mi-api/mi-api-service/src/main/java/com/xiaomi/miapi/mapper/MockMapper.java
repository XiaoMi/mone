package com.xiaomi.miapi.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * mock相关mapper
 */
public interface MockMapper
{
	//获取成功示例
	public String getSuccessResult(@Param("projectID") Integer projectID, @Param("apiURI") String uri,
			@Param("apiRequestType") Integer requstType);

	//获取失败示例
	public String getFailureResult(@Param("projectID") Integer projectID, @Param("apiURI") String uri,
			@Param("apiRequestType") Integer requstType);

	//获取restful mock数据
	public List<Map<String, Object>> getRestfulMock(@Param("projectID") Integer projectID,
			@Param("apiRequestType") Integer requstType);

	//获取mock结果
	public String getMockResult(@Param("projectID") Integer projectID, @Param("apiURI") String uri,
			@Param("apiRequestType") Integer requstType);
}
