package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.pojo.ApiHistoryRecord;

import java.util.Map;

public interface ApiHistoryService {
    public Result<Map<String,Object>> getApiHistoryList(Integer apiId,Integer pageNo, Integer pageSize);

    Result<Boolean> rollbackToHis(Integer apiID,Integer targetHisID);

    public Result<ApiHistoryRecord> getHistoryRecordById(Integer recordId);

    public Result<Map<String,Object>> compareWithOldVersion(Integer recordId, Integer apiId);

    public Map<String, String> compareTwoVersionApi(Integer apiID,Integer compareID);
}
