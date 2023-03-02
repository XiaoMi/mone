package com.xiaomi.miapi.pojo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApiHistory
{
	private Integer projectID;
	private Integer groupID;
	private Integer apiID;
	private String historyJson;
	private String updateDesc;
	private String updateUsername;
	private Timestamp updateTime;
	private Integer isNow;
}
