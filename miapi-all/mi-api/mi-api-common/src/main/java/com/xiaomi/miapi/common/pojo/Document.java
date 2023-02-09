package com.xiaomi.miapi.common.pojo;

import java.sql.Timestamp;

/**
 * 项目文档
 */
public class Document
{
	private Integer documentID;// 文档ID
	private Integer projectID;// 项目ID
	private Integer contentType;// 文档类型
	private String contentRaw;// 富文本内容
	private String content;// Markdown内容
	private String title;// 文档标题
	private Timestamp updateTime;// 更新时间
	private Integer userID;// 更新人员ID
	private Integer topParentGroupID;//一级父分组ID
	private String createUserName; //创建者

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
