package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;

import java.util.List;
import java.util.Map;

/**
 * 项目成员
 */
public interface PartnerService
{
	//邀请项目成员
	public int invitePartner(String username,Integer projectID, Integer inviteUserID, List<Integer> userIds,Integer roleType);

	//邀请项目组成员
	public Result<Boolean> inviteGroupPartner(String username,Integer groupId, Integer inviteUserID, List<Integer> userIds,Integer roleType);

	public Result<Boolean> editPartnerRole(Integer opUserId, String opUsername, Integer userId, Integer projectId, List<Integer> roleTypes);

	public Result<Boolean> editGroupPartnerRole(Integer opUserId, String opUsername, Integer userId, Integer groupId, List<Integer> roleTypes);

	public Result<Boolean> removePartner(Integer opUserId,String opUsername,Integer userId,Integer projectId);

	public Result<Boolean> removeGroupPartner(Integer opUserId,String opUsername,Integer userId,Integer groupId);

	//获取成员列表
	public Map<String,List<Map<Integer,String>>> getPartnerList(Integer projectID);

	public Map<String,List<Map<Integer,String>>> getGroupPartnerList(Integer groupID);

	public Result<List<Map<Integer, String>>> getAllPartnerList();
}
