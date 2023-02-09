package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ProjectOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ProjectOperationLogMapper
{
	Integer addProjectOperationLog(ProjectOperationLog projectOperationLog);

	List<Map<String, Object>> getProjectLogList(@Param("projectID") Integer projectID,
			@Param("page") Integer page, @Param("pageSize") Integer pageSize, @Param("dayOffset") Integer dayOffset);

	Integer getLogCount(@Param("projectID") Integer projectID, @Param("dayOffset") Integer dayOffset);
}
