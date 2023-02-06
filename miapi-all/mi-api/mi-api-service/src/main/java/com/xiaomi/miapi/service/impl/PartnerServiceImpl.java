package com.xiaomi.miapi.service.impl;

import com.xiaomi.miapi.common.pojo.ProjectOperationLog;
import com.xiaomi.miapi.mapper.ProjectOperationLogMapper;
import com.xiaomi.miapi.service.PartnerService;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.bo.ProjectGroupMemberBo;
import com.xiaomi.youpin.hermes.bo.ProjectMemberBo;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.bo.response.BusProjectGroupRoleResp;
import com.xiaomi.youpin.hermes.bo.response.BusProjectRoleResp;
import com.xiaomi.youpin.hermes.service.AccountService;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * 项目成员[业务处理层]
 */
@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="java.lang.Exception")
public class PartnerServiceImpl implements PartnerService
{
	@Autowired
	private ProjectOperationLogMapper projectOperationLogMapper;

	@Reference(check = false,group = "${ref.hermes.service.group}")
	private BusProjectService busProjectService;

	@Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}", timeout = 4000)
	private AccountService accountService;
	/**
	 * 邀请成员[业务处理层]
	 */
	@Override
	public int invitePartner(String username,Integer projectID, Integer inviteUserID, List<Integer> userIds,Integer roleType)
	{
		ProjectMemberBo projectMemberBo = new ProjectMemberBo();
		projectMemberBo.setProjectId(projectID.longValue());
		projectMemberBo.setRoleType(roleType);
		projectMemberBo.setMembers(userIds);

		boolean ok = busProjectService.addMembers(inviteUserID,projectMemberBo);
		if (ok)
		{
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("邀请新成员");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PARTNER);
			projectOperationLog.setOpTargetID(inviteUserID);
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
			projectOperationLog.setOpUsername(username);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return 1;
		} else{
			return -1;
		}
	}


	@Override
	public Result<Boolean> inviteGroupPartner(String username, Integer groupId, Integer inviteUserID, List<Integer> userIds, Integer roleType) {
		ProjectGroupMemberBo projectGroupMemberBo = new ProjectGroupMemberBo();
		projectGroupMemberBo.setGroupId(groupId);
		projectGroupMemberBo.setRoleType(roleType);
		projectGroupMemberBo.setMembers(userIds);

		busProjectService.addGroupMembers(inviteUserID,projectGroupMemberBo);
		return Result.success(true);
	}

	@Override
	public Result<Boolean> editPartnerRole(Integer opUserId, String opUsername, Integer userId, Integer projectId, List<Integer> roleTypes) {
		com.xiaomi.youpin.hermes.bo.Result<Boolean> result = busProjectService.editMemberRole(opUserId,userId,projectId,roleTypes);
		if (result.getData()!=null && result.getData()){
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectId);
			projectOperationLog.setOpDesc("编辑成员角色");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PARTNER);
			projectOperationLog.setOpTargetID(userId);
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
			projectOperationLog.setOpUsername(opUsername);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return Result.success(true);
		}
		return Result.fail(CommonError.valueOf(result.getMessage()));
	}

	@Override
	public Result<Boolean> editGroupPartnerRole(Integer opUserId, String opUsername, Integer userId, Integer groupId, List<Integer> roleTypes) {
		busProjectService.editGroupMemberRole(opUserId,userId,groupId,roleTypes);
		return Result.success(true);
	}

	@Override
	public Result<Boolean> removePartner(Integer opUserId, String opUsername, Integer userId, Integer projectId) {
		com.xiaomi.youpin.hermes.bo.Result<Boolean> result = busProjectService.removeMemberRole(opUserId,userId,projectId);
		if (result.getData()!= null && result.getData()){
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectId);
			projectOperationLog.setOpDesc("移除项目成员");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PARTNER);
			projectOperationLog.setOpTargetID(userId);
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
			projectOperationLog.setOpUsername(opUsername);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return Result.success(true);
		}
		return Result.fail(CommonError.valueOf(result.getMessage()));
	}

	@Override
	public Result<Boolean> removeGroupPartner(Integer opUserId, String opUsername, Integer userId, Integer groupId) {
		busProjectService.removeGroupMemberRole(opUserId,userId,groupId);
		return Result.success(true);
	}

	/**
	 * 获取成员列表[业务处理层]
	 */
	@Override
	public Map<String,List<Map<Integer,String>>> getPartnerList(Integer projectID)
	{
		Map<String,List<Map<Integer,String>>> map = new HashMap<>();
		map.put("Admin",new ArrayList<>());
		map.put("Member",new ArrayList<>());
		map.put("Guest",new ArrayList<>());

		List<BusProjectRoleResp> memberList = busProjectService.members(projectID);
		for (BusProjectRoleResp member :
				memberList) {
			Map<Integer,String> userInfo = new HashMap<>();
			userInfo.put(member.getAccountId(),member.getAccountName());
			switch (member.getRoleType()){
				case 0:
					List<Map<Integer,String>> mapList = map.get("Admin");
					mapList.add(userInfo);
					break;
				case 1:
					List<Map<Integer,String>> mapList2 = map.get("Member");
					mapList2.add(userInfo);
					break;
				case 2:
					List<Map<Integer,String>> mapList3 = map.get("Guest");
					mapList3.add(userInfo);
					break;
				default:
					break;
			}
		}
		return map;
	}

	@Override
	public Map<String, List<Map<Integer, String>>> getGroupPartnerList(Integer groupID) {
		Map<String,List<Map<Integer,String>>> map = new HashMap<>();
		map.put("Admin",new ArrayList<>());
		map.put("Member",new ArrayList<>());
		map.put("Guest",new ArrayList<>());

		List<BusProjectGroupRoleResp> memberList = busProjectService.groupMembers(groupID);
		for (BusProjectGroupRoleResp member :
				memberList) {
			Map<Integer,String> userInfo = new HashMap<>();
			userInfo.put(member.getAccountId(),member.getAccountName());
			switch (member.getRoleType()){
				case 0:
					List<Map<Integer,String>> mapList = map.get("Admin");
					mapList.add(userInfo);
					break;
				case 1:
					List<Map<Integer,String>> mapList2 = map.get("Member");
					mapList2.add(userInfo);
					break;
				case 2:
					List<Map<Integer,String>> mapList3 = map.get("Guest");
					mapList3.add(userInfo);
					break;
				default:
					break;
			}
		}
		return map;
	}

	@Override
	public Result<List<Map<Integer, String>>> getAllPartnerList() {
		List<Account> accounts = accountService.getAllAccountList();

		List<Map<Integer, String>> mapList = new ArrayList<>(accounts.size());
		for (Account a :
				accounts) {
			Map<Integer,String> map = new HashMap<>();
			map.put(a.getId().intValue(), StringUtils.join(a.getName(),"[",a.getUserName(),"]"));
			mapList.add(map);
		}
		return Result.success(mapList);
	}
}
