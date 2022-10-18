package com.xiaomi.miapi.common.pojo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Api接口类
 */
@Data
public class Api
{
	private Integer apiID;//接口ID
	private String apiName;//接口名称
	private String apiURI;//接口URI
	private Integer apiProtocol;//接口类型
	private String apiEnv;//接口的环境
	private Integer apiRequestType;//请求类型
	private Integer apiStatus;//接口状态
	private Timestamp apiUpdateTime;//接口更新时间
	private Integer groupID;//分组ID
	private Integer projectID;//项目ID
	private Integer starred;//星标状态
	private Integer removed;//是否被移到回收站
	private Timestamp removeTime;//移到回收站的时间
	private Integer apiNoteType;//接口详细说明文本类型
	private String apiRemark;//详细说明富文本
	private String apiDesc;//详细说明Markdown
	private Integer apiRequestParamType;//接口请求类型 0：form-data  1：form-json 2：raw
	private Integer apiResponseParamType;//接口请求类型  1:json 2：raw
	private String apiRequestRaw;//接口源数据
	private String apiResponseRaw;//接口源数据
	private String updateUsername;//更新人员ID
	private Integer dubboApiId;
	private Integer gatewayApiId;
	private String updateMsg;
	private String httpControllerPath = "";
	private String mavenAddr;
}
