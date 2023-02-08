package com.xiaomi.miapi.common.pojo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 接口历史
 */
@Data
public class ApiHistory
{
	private Integer projectID;//项目ID
	private Integer groupID;//分组ID
	private Integer apiID;//接口ID
	private String historyJson;//历史数据
	private String updateDesc;//更新描述
	private String updateUsername;//更新人员名
	private Timestamp updateTime;//更新时间
	private Integer isNow;//是否当前版本
}
