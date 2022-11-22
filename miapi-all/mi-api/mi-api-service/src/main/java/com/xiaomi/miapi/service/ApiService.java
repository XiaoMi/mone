package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.dto.ProjectApisDTO;
import com.xiaomi.miapi.common.pojo.Api;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 接口
 */
public interface ApiService
{
	public Result<Boolean> editApiStatus(Integer projectId,Integer apiId,Integer status);

	public Result<Boolean> editApiDiyExp(Integer apiID,Integer expType,Integer type,String content);

	//移除接口到回收站
	public boolean removeApi(Integer projectID, String apiID, Integer userID,String username);

	//删除接口
	public boolean deleteApi(Integer projectID, String apiID, String  username);

	//获取接口列表
	public Map<String,Object> getApiList(Integer pageNo, Integer pageSize,Integer projectID,  Integer groupID, Integer orderBy, Integer asc);

	public Result<List<Map<String, Object>>> getApiListByProjectId(ProjectApisDTO dto);

	public Map<Integer,List<Map<String, Object>>> getGroupApiViewList(Integer projectID);

	public Map<Integer,List<Map<String, Object>>> getAllIndexGroupApiViewList(Integer projectID);

	public Result<List<Map<String, String>>> getApiListByIndex(Integer indexID) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;

	//搜索接口
	public List<Map<String, Object>> searchApi(Integer projectID, String tips, Integer type);

	//获取接口历史列表
	public List<Map<String, Object>> getApiHistoryList(Integer projectID, Integer apiID);

	public List<Api> getRecentlyApiList(Integer userId);

}
