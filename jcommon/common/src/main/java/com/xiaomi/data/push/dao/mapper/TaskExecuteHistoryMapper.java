package com.xiaomi.data.push.dao.mapper;

import com.xiaomi.data.push.dao.model.TaskExecuteHistory;
import com.xiaomi.data.push.dao.model.TaskExecuteHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskExecuteHistoryMapper {
    int countByExample(TaskExecuteHistoryExample example);

    int deleteByExample(TaskExecuteHistoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TaskExecuteHistory record);

    int insertSelective(TaskExecuteHistory record);

    List<TaskExecuteHistory> selectByExample(TaskExecuteHistoryExample example);

    TaskExecuteHistory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TaskExecuteHistory record, @Param("example") TaskExecuteHistoryExample example);

    int updateByExample(@Param("record") TaskExecuteHistory record, @Param("example") TaskExecuteHistoryExample example);

    int updateByPrimaryKeySelective(TaskExecuteHistory record);

    int updateByPrimaryKey(TaskExecuteHistory record);
}