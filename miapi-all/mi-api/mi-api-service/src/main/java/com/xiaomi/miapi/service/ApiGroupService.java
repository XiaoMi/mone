package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.pojo.ApiGroup;

import java.util.List;
import java.util.Map;

public interface ApiGroupService
{
	boolean addApiGroup(ApiGroup apiGroup, String opUsername);

	Result<Boolean> deleteGroup(Integer projectID, Integer groupID,String username);

	List<Map<String, Object>> getGroupList(Integer projectID);

	boolean editGroup(ApiGroup apiGroup,String opUserName);

}
