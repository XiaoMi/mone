package com.xiaomi.miapi.common.pojo;
/**
 * 项目文档分组
 */
public class DocumentGroup
{
	private Integer groupID;//分组ID
	private String groupName;//分组名称
	private Integer projectID;//项目ID

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

	public Integer getProjectID()
	{
		return projectID;
	}

	public void setProjectID(Integer projectID)
	{
		this.projectID = projectID;
	}

}
