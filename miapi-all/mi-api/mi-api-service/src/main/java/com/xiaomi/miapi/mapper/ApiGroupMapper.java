package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ApiGroupMapper
{
	int addApiGroup(ApiGroup apiGroup);

	int deleteGroup(@Param("groupIDS")List<Integer> groupIDS);

	List<Map<String,Object>> getGroupList(@Param("projectID")Integer projectID);

	int editGroup(ApiGroup apiGroup);

	int sortGroup(@Param("projectID")Integer projectID, @Param("orderList")String orderList);

	ApiGroup getGroupByID(@Param("groupID")Integer groupID);

	ApiGroup getGroupByName(@Param("projectID")Integer projectID,@Param("groupName")String groupName);
}
