package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.ApiGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 接口分组[数据库操作]
 */
public interface ApiGroupMapper
{
	//新建分组
	public int addApiGroup(ApiGroup apiGroup);

	//删除分组
	public int deleteGroup(@Param("groupIDS")List<Integer> groupIDS);

	//获取父分组列表
	public List<Map<String,Object>> getGroupList(@Param("projectID")Integer projectID);

	//修改分组
	public int editGroup(ApiGroup apiGroup);

	//对分组进行排序
	public int sortGroup(@Param("projectID")Integer projectID, @Param("orderList")String orderList);

	//获取分组
	public ApiGroup getGroupByID(@Param("groupID")Integer groupID);

	//获取分组
	public ApiGroup getGroupByName(@Param("projectID")Integer projectID,@Param("groupName")String groupName);
}
