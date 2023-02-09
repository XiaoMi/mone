package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.dto.ProjectApisDTO;
import com.xiaomi.miapi.pojo.Api;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface ApiService
{
	Result<Boolean> editApiStatus(Integer projectId,Integer apiId,Integer status);

	Result<Boolean> editApiDiyExp(Integer apiID,Integer expType,Integer type,String content);

	boolean deleteApi(Integer projectID, String apiID, String  username);

	Map<String,Object> getApiList(Integer pageNo, Integer pageSize,Integer projectID,  Integer groupID, Integer orderBy, Integer asc);

	Result<List<Map<String, Object>>> getApiListByProjectId(ProjectApisDTO dto);

	Map<Integer,List<Map<String, Object>>> getGroupApiViewList(Integer projectID,Integer orderBy);

	Map<Integer,List<Map<String, Object>>> getAllIndexGroupApiViewList(Integer projectID);

	Result<List<Map<String, String>>> getApiListByIndex(Integer indexID) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;

	List<Map<String, Object>> searchApi(Integer projectID, String tips, Integer type);

	List<Map<String, Object>> getApiHistoryList(Integer projectID, Integer apiID);

	List<Api> getRecentlyApiList(String  username);

}
