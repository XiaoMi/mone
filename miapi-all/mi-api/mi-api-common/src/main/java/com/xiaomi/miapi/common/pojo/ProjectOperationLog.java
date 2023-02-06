package com.xiaomi.miapi.common.pojo;

import java.sql.Timestamp;

/**
 * 项目操作日志
 */
public class ProjectOperationLog
{
	private Integer opID;// 日志ID
	private Integer opType;// 操作类型
	private String opUsername;// 操作用户名
	private String opDesc;// 操作描述
	private Timestamp opTime;// 操作时间
	private Integer opProjectID;// 操作项目ID
	private Integer opTarget;// 操作目标
	private Integer opTargetID;// 操作目标ID
	private String partnerNickName;//成员备注
	private String userNickName;//成员昵称

	public static Integer OP_TYPE_ADD = 0; //添加操作
	public static Integer OP_TYPE_UPDATE = 1;//更新操作
	public static Integer OP_TYPE_DELETE = 2;//删除操作
	public static Integer OP_TYPE_OTHERS = 3;//其他操作
	public static Integer OP_TARGET_PROJECT = 0;//操作项目
	public static Integer OP_TARGET_API = 1;//操作接口
	public static Integer OP_TARGET_API_GROUP = 2;//操作接口分组
	public static Integer OP_TARGET_STATUS_CODE = 3;//操作状态码
	public static Integer OP_TARGET_STATUS_CODE_GROUP = 4;//操作状态码分组
	public static Integer OP_TARGET_ENVIRONMENT = 5;//操作环境
	public static Integer OP_TARGET_PARTNER = 6;//操作协作成员
	public static Integer OP_TARGET_PROJECT_DOCUMENT_GROUP = 7;//操作文档分组
	public static Integer OP_TARGET_PROJECT_DOCUMENT = 8;//操作文档
	public static Integer OP_TARGET_AUTOMATED_TEST_CASE_GROUP = 9;//操作用例分组
	public static Integer OP_TARGET_AUTOMATED_TEST_CASE = 10;//操作用例

	public Integer getOpID()
	{
		return opID;
	}

	public void setOpID(Integer opID)
	{
		this.opID = opID;
	}

	public Integer getOpType()
	{
		return opType;
	}

	public void setOpType(Integer opType)
	{
		this.opType = opType;
	}

	public String getOpUsername() {
		return opUsername;
	}

	public void setOpUsername(String opUsername) {
		this.opUsername = opUsername;
	}

	public String getOpDesc()
	{
		return opDesc;
	}

	public void setOpDesc(String openDesc)
	{
		this.opDesc = openDesc;
	}

	public Timestamp getOpTime()
	{
		return opTime;
	}

	public void setOpTime(Timestamp opTime)
	{
		this.opTime = opTime;
	}

	public Integer getOpProjectID()
	{
		return opProjectID;
	}

	public void setOpProjectID(Integer opProjectID)
	{
		this.opProjectID = opProjectID;
	}

	public Integer getOpTarget()
	{
		return opTarget;
	}

	public void setOpTarget(Integer opTarget)
	{
		this.opTarget = opTarget;
	}

	public Integer getOpTargetID()
	{
		return opTargetID;
	}

	public void setOpTargetID(Integer opTargetID)
	{
		this.opTargetID = opTargetID;
	}

	public String getPartnerNickName() {
		return partnerNickName;
	}

	public void setPartnerNickName(String partnerNickName) {
		this.partnerNickName = partnerNickName;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

}
