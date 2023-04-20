package com.xiaomi.mone.monitor.dao.mapper;

import com.xiaomi.mone.monitor.dao.model.AlarmHealthQuery;
import com.xiaomi.mone.monitor.dao.model.AlarmHealthResult;
import com.xiaomi.mone.monitor.dao.model.AppMonitor;
import com.xiaomi.mone.monitor.dao.model.AppMonitorExample;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;
import java.util.List;

@Resource
public interface AppMonitorMapper {

    List<AlarmHealthResult> selectAlarmHealth(AlarmHealthQuery query);

    long countByExample(AppMonitorExample example);

    int deleteByExample(AppMonitorExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppMonitor record);

    int insertSelective(AppMonitor record);

    List<AppMonitor> selectByExample(AppMonitorExample example);

    List<AppMonitor> selectByGroupBy(@Param("offset") Integer offset, @Param("limit") Integer limit);

    List<Integer> selectTreeIdByOwnerOrCareUser(@Param("userName") String userName);

    AppMonitor selectByPrimaryKey(Integer id);

    List<AppMonitor> getMyAndCareAppList(@Param("userName") String userName, @Param("appName") String appName);

    List<AppMonitor> selectAllMyAppDistinct(@Param("userName") String userName, @Param("appName") String appName, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long countAllMyAppDistinct(@Param("userName") String userName, @Param("appName") String appName);

    int updateByExampleSelective(@Param("record") AppMonitor record, @Param("example") AppMonitorExample example);

    int updateByExample(@Param("record") AppMonitor record, @Param("example") AppMonitorExample example);

    int updateByPrimaryKeySelective(AppMonitor record);

    int updateByPrimaryKey(AppMonitor record);

    int batchInsert(@Param("list") List<AppMonitor> list);

    int batchInsertSelective(@Param("list") List<AppMonitor> list, @Param("selective") AppMonitor.Column ... selective);
}