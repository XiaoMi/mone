package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.ProjectOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 项目操作日志
 */
public interface ProjectOperationLogMapper
{
	//添加项目操作日志
	public Integer addProjectOperationLog(ProjectOperationLog projectOperationLog);

	//获取项目操作日志列表
	public List<Map<String, Object>> getProjectLogList(@Param("projectID") Integer projectID,
			@Param("page") Integer page, @Param("pageSize") Integer pageSize, @Param("dayOffset") Integer dayOffset);

	//获取操作日志数量
	public Integer getLogCount(@Param("projectID") Integer projectID, @Param("dayOffset") Integer dayOffset);
}
