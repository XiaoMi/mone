package com.xiaomi.miapi.pojo;
public class ApiHeader
{
	private Integer headerID;
	private Integer apiID;
	private String headerName;
	private String headerValue;

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
