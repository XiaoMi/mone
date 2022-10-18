package com.xiaomi.miapi.common.pojo;

import java.sql.Timestamp;

/**
 * 项目
 */
public class Project
{
	private Integer projectID;//项目ID
	private String projectName;//项目名称
	private Integer projectGroupID; //所属项目组ID
	private Integer isPublic; //共有\私有
	private Timestamp projectUpdateTime;//项目更新时间
	private String desc;//项目版本

	public Integer getProjectID()
	{
		return projectID;
	}

	public void setProjectID(Integer projectID)
	{
		this.projectID = projectID;
	}

	public Integer getProjectGroupID() {
		return projectGroupID;
	}

	public void setProjectGroupID(Integer projectGroupID) {
		this.projectGroupID = projectGroupID;
	}

	public Integer getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Integer isPublic) {
		this.isPublic = isPublic;
	}

	public String getProjectName()
	{
		return projectName;
	}

	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}

	public Timestamp getProjectUpdateTime()
	{
		return projectUpdateTime;
	}

	public void setProjectUpdateTime(Timestamp projectUpdateTime)
	{
		this.projectUpdateTime = projectUpdateTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
