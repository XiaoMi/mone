package com.xiaomi.miapi.pojo;
/**
 * 接口返回参数
 */
public class ApiResultParam
{
	private Integer paramID;//返回参数ID
	private String paramName;//返回参数描述
	private String paramKey;//返回参数名称
	private Integer apiID;//接口ID
	private Integer paramNotNull;//是否必含
	private Integer paramType;//参数类型
	private String paramValue;//参数值
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
	public Integer getParamType()
	{
		return paramType;
	}
	public void setParamType(Integer paramType)
	{
		this.paramType = paramType;
	}
	public String getParamValue()
	{
		return paramValue;
	}
	public void setParamValue(String paramValue)
	{
		this.paramValue = paramValue;
	}
}
