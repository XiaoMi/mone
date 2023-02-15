package com.xiaomi.miapi.pojo;
/**
 * 接口返回参数值
 */
public class ApiResultValue
{
	private Integer valueID;// 返回参数值ID
	private String value;// 返回参数值
	private String valueDescription;// 返回参数值描述
	private Integer paramID;// 参数ID

	public Integer getValueID()
	{
		return valueID;
	}

	public void setValueID(Integer valueID)
	{
		this.valueID = valueID;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValueDescription()
	{
		return valueDescription;
	}

	public void setValueDescription(String valueDescription)
	{
		this.valueDescription = valueDescription;
	}

	public Integer getParamID()
	{
		return paramID;
	}

	public void setParamID(Integer paramID)
	{
		this.paramID = paramID;
	}
}
