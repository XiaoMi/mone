package com.xiaomi.mone.monitor.dao;

import com.xiaomi.mone.monitor.bo.AlarmStrategyInfo;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.service.model.PageData;
import java.util.*;

public interface AppAlarmStrategyDao {

    AlarmStrategy getById(Integer id);

    /**
     * 清洗数据专用
     * @param type
     * @return
     */
    List<AlarmStrategy> queryByType(int type);

    AlarmStrategyInfo getInfoById(Integer id);

    boolean insert(AlarmStrategy strategy);

    boolean updateById(AlarmStrategy strategy);

    boolean deleteById(Integer id);

    PageData<List<AlarmStrategyInfo>> searchByCond(final String user, Boolean filterOwner, AlarmStrategy strategy, int page, int pageSize, String sortBy, String sortRule);

    @Deprecated
    PageData<List<AlarmStrategyInfo>> searchByCondNoUser(AlarmStrategy strategy, int page, int pageSize,String sortBy,String sortRule);

}
