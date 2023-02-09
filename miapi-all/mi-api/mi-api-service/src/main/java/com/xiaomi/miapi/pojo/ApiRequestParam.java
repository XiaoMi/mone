package com.xiaomi.miapi.pojo;
/**
 * 接口请求参数
 */
public class ApiRequestParam
{
	private Integer paramID;//参数ID
	private String paramName;//参数说明
	private String paramKey;//参数名称
	private String paramValue;//参数可能值
	private Integer paramType;//参数类型
	private String paramLimit;//参数限制
	private Integer apiID;//接口名称
	private Integer paramNotNull;//是否必填

	public Integer getParamID()
	{
		return paramID;
	}

	public void setParamID(Integer paramID)
	{
		this.paramID = paramID;
	}

	public String getParamName()
	{
		return paramName;
	}

	public void setParamName(String paramName)
	{
		this.paramName = paramName;
	}

	public String getParamKey()
	{
		return paramKey;
	}

	public void setParamKey(String paramKey)
	{
		this.paramKey = paramKey;
	}

	public String getParamValue()
	{
		return paramValue;
	}

	public void setParamValue(String paramValue)
	{
		this.paramValue = paramValue;
	}

	public Integer getParamType()
	{
		return paramType;
	}

	public void setParamType(Integer paramType)
	{
		this.paramType = paramType;
	}

	public String getParamLimit()
	{
		return paramLimit;
	}

	public void setParamLimit(String paramLimit)
	{
		this.paramLimit = paramLimit;
	}

	public Integer getApiID()
	{
		return apiID;
	}

	public void setApiID(Integer apiID)
	{
		this.apiID = apiID;
	}

	public Integer getParamNotNull()
	{
		return paramNotNull;
	}

	public void setParamNotNull(Integer paramNotNull)
	{
		this.paramNotNull = paramNotNull;
	}
}
