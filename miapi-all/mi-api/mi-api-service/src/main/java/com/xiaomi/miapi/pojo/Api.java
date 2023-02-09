package com.xiaomi.miapi.pojo;

import lombok.Data;

import java.sql.Timestamp;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class Api {
	private Integer apiID;
	private String apiName;
	private String apiURI;
	private Integer apiProtocol;
	private String apiEnv;
	private Integer apiRequestType;
	private Integer apiStatus;
	private Timestamp apiUpdateTime;
	private Integer groupID;
	private Integer projectID;
	private Integer starred;
	private Integer removed;
	private Timestamp removeTime;
	private Integer apiNoteType;
	private String apiRemark;
	private String apiDesc;
	//0：form-data  1：form-json 2：raw
	private Integer apiRequestParamType;
	// 1:json 2：raw
	private Integer apiResponseParamType;
	private String apiRequestRaw;
	private String apiResponseRaw;
	private String updateUsername;
	private Integer dubboApiId;
	private Integer gatewayApiId;
	private String updateMsg;
	private String httpControllerPath = "";
	private String mavenAddr;
}
