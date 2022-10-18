package com.xiaomi.miapi.common.pojo;

/**
 * 接口分组
 */
public class ApiGroup
{
	private Integer groupID;//分组ID
	private String groupName;//分组名称
	private Integer projectID;//项目ID
	private String groupDesc;
	private Integer userID;
	private Boolean systemGroup;


	public Boolean getSystemGroup() {
		return systemGroup;
	}

	public void setSystemGroup(Boolean systemGroup) {
		this.systemGroup = systemGroup;
	}

	public Integer getGroupID()
	{
		return groupID;
	}

	public void setGroupID(Integer groupID)
	{
		this.groupID = groupID;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public Integer getProjectID()
	{
		return projectID;
	}

	public void setProjectID(Integer projectID)
	{
		this.projectID = projectID;
	}

	public Integer getUserID()
	{
		return userID;
	}

	public void setUserID(Integer userID)
	{
		this.userID = userID;
	}
}
