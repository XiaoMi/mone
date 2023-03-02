package com.xiaomi.miapi.pojo;

import java.sql.Timestamp;

public class Document
{
	private Integer documentID;
	private Integer projectID;
	private Integer contentType;
	private String contentRaw;
	private String content;
	private String title;
	private Timestamp updateTime;
	private Integer userID;
	private Integer topParentGroupID;
	private String createUserName;

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Integer getDocumentID()
	{
		return documentID;
	}

	public void setDocumentID(Integer documentID)
	{
		this.documentID = documentID;
	}

	public Integer getProjectID()
	{
		return projectID;
	}

	public void setProjectID(Integer projectID)
	{
		this.projectID = projectID;
	}

	public Integer getContentType()
	{
		return contentType;
	}

	public void setContentType(Integer contentType)
	{
		this.contentType = contentType;
	}

	public String getContentRaw()
	{
		return contentRaw;
	}

	public void setContentRaw(String contentRaw)
	{
		this.contentRaw = contentRaw;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Timestamp getUpdateTime()
	{
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime)
	{
		this.updateTime = updateTime;
	}

	public Integer getUserID()
	{
		return userID;
	}

	public void setUserID(Integer userID)
	{
		this.userID = userID;
	}

	public Integer getTopParentGroupID()
	{
		return topParentGroupID;
	}

	public void setTopParentGroupID(Integer topParentGroupID)
	{
		this.topParentGroupID = topParentGroupID;
	}

}
