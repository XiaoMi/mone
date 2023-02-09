package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.pojo.ApiHistoryRecord;

import java.util.Map;

public interface ApiHistoryService {
    Result<Map<String,Object>> getApiHistoryList(Integer apiId,Integer pageNo, Integer pageSize);

    Result<Boolean> rollbackToHis(Integer apiID,Integer targetHisID);

    Result<ApiHistoryRecord> getHistoryRecordById(Integer recordId);

    Result<Map<String,Object>> compareWithOldVersion(Integer recordId, Integer apiId);

    Map<String, String> compareTwoVersionApi(Integer apiID,Integer compareID);
}
