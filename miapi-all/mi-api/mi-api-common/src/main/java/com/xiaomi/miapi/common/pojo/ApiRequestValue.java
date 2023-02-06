package com.xiaomi.miapi.common.pojo;
/**
 * 接口请求参数值
 */
public class ApiRequestValue {

	private Integer valueID;//请求参数值ID
	private String value;//请求参数值内容
	private String valueDescription;//请求参数值描述
	private Integer paramID;//参数ID
	public Integer getValueID() {
		return valueID;
	}
	public void setValueID(Integer valueID) {
		this.valueID = valueID;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValueDescription() {
		return valueDescription;
	}
	public void setValueDescription(String valueDescription) {
		this.valueDescription = valueDescription;
	}
	public Integer getParamID() {
		return paramID;
	}
	public void setParamID(Integer paramID) {
		this.paramID = paramID;
	}
}
