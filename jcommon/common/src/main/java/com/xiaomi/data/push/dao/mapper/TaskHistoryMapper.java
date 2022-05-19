package com.xiaomi.data.push.dao.mapper;

import com.xiaomi.data.push.dao.model.TaskHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskHistoryMapper {

    int update(TaskHistory taskHistory);

    int insert(TaskHistory taskHistory);

    List<TaskHistory> getTaskHistory(@Param("taskId") Integer taskId, @Param("status") Integer status);

}