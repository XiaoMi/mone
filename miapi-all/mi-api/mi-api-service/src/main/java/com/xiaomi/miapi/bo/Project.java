package com.xiaomi.miapi.bo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class Project
{
	private Integer projectID;
	private String projectName;
	private Integer projectGroupID;
	private Integer isPublic;
	private Timestamp projectUpdateTime;
	private String desc;
	private String projectVersion;
}
