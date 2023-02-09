package com.xiaomi.miapi.common.pojo;
/**
 * 环境请求头部
 */
public class ApiHeader
{
	private Integer headerID;//请求头部ID
	private Integer apiID;//接口ID
	private String headerName;//标签
	private String headerValue;//内容

	public Integer getHeaderID()
	{
		return headerID;
	}

	public void setHeaderID(Integer headerID)
	{
		this.headerID = headerID;
	}

	public Integer getApiID()
	{
		return apiID;
	}

	public void setApiID(Integer apiID)
	{
		this.apiID = apiID;
	}

	public String getHeaderName()
	{
		return headerName;
	}

	public void setHeaderName(String headerName)
	{
		this.headerName = headerName;
	}

	public String getHeaderValue()
	{
		return headerValue;
	}

	public void setHeaderValue(String headerValue)
	{
		this.headerValue = headerValue;
	}
}
