package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.common.Result;
import java.sql.SQLException;

public interface HdfsService {


    /**
     * hive query 离线查询 只开放时间+traceId
     */
    Result<String> queryHive(String date,String traceId) throws SQLException;



}
