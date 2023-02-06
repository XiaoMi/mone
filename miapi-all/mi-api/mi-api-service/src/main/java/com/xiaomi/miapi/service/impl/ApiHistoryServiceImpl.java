package com.xiaomi.miapi.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.common.pojo.Api;
import com.xiaomi.miapi.common.pojo.ApiHistoryRecord;
import com.xiaomi.miapi.common.pojo.ApiHistoryRecordExample;
import com.xiaomi.miapi.mapper.ApiHistoryRecordMapper;
import com.xiaomi.miapi.mapper.ApiMapper;
import com.xiaomi.miapi.service.ApiHistoryService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.service.DubboApiService;
import com.xiaomi.miapi.service.GatewayApiService;
import com.xiaomi.miapi.service.HttpApiService;
import com.xiaomi.mone.dubbo.docs.core.beans.ModuleCacheItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiHistoryServiceImpl implements ApiHistoryService {

    @Autowired
    ApiHistoryRecordMapper historyRecordMapper;

    @Autowired
    ApiServiceImpl apiService;

    @Autowired
    HttpApiService httpApiService;

    @Autowired
    DubboApiService dubboApiService;

    @Autowired
    GatewayApiService gatewayApiService;

    @Autowired
    ApiMapper apiMapper;

    public static final Gson gson = new Gson();

    @Override
    public Result<Map<String, Object>> getApiHistoryList(Integer apiId, Integer pageNo, Integer pageSize) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize <= 0) {
            pageSize = Consts.DEFAULT_PAGE_SIZE;
        }
        int offset = (pageNo - 1) * pageSize;

        Map<String, Object> resultMap = new HashMap<>();
        ApiHistoryRecordExample totalExample = new ApiHistoryRecordExample();
        totalExample.createCriteria().andApiIdEqualTo(apiId);

        ApiHistoryRecordExample example = new ApiHistoryRecordExample();
        example.createCriteria().andApiIdEqualTo(apiId);
        example.setOrderByClause("update_time desc limit " + pageSize + " offset " + offset);

        resultMap.put("recordList", historyRecordMapper.selectByExample(example));
        resultMap.put("total", historyRecordMapper.countByExample(totalExample));

        return Result.success(resultMap);
    }

    @Override
    public Result<Boolean> rollbackToHis(Integer apiID, Integer targetHisID) {
        ApiHistoryRecord record = historyRecordMapper.selectByPrimaryKey(targetHisID);

        Api oldApi = apiMapper.getApiInfo(record.getProjectId(),apiID);

        if (record.getApiProtocal() == Consts.HTTP_API_TYPE){
            Map<String,Object> cache = null;
            String apiErrorCodes = "";
            String headerInfo = "";
            String requestInfo = "";
            String resultInfo = "";
            try {
                cache = gson.fromJson(record.getApiHistiryJson(),new TypeToken<Map<String,Object>>() {
                }.getType());
                Map<String,Object> baseInfo = (Map<String, Object>) cache.get("baseInfo");

                oldApi.setApiName((String) baseInfo.get("apiName"));
                oldApi.setApiDesc((String) baseInfo.get("apiDesc"));
                oldApi.setApiURI((String) baseInfo.get("apiURI"));
                oldApi.setApiRequestType((int)(double) baseInfo.get("apiRequestType"));
                oldApi.setApiStatus((int) (double) baseInfo.get("apiStatus"));
                oldApi.setApiRemark((String) baseInfo.get("apiRemark"));
                oldApi.setApiRequestParamType((int)(double)  baseInfo.get("apiRequestParamType"));
                oldApi.setApiResponseParamType((int) (double) baseInfo.get("apiResponseParamType"));

                oldApi.setApiRequestRaw((String) baseInfo.get("apiRequestRaw"));
                oldApi.setApiResponseRaw((String) baseInfo.get("apiResponseRaw"));

                if (cache.get("headerInfo") != null){
                    headerInfo = gson.toJson(cache.get("headerInfo"));
                }

                if (cache.get("requestInfo") != null){
                    requestInfo = gson.toJson(cache.get("requestInfo"));
                }

                if (cache.get("resultInfo") != null){
                    resultInfo = gson.toJson(cache.get("resultInfo"));
                }

                if (cache.get("apiErrorCodes") != null){
                    apiErrorCodes = gson.toJson(cache.get("apiErrorCodes"));
                }

            } catch (JsonSyntaxException e) {
                return Result.fail(CommonError.InvalidParamError);
            }
            Result<Boolean> rt = httpApiService.editHttpApi(oldApi, headerInfo, requestInfo, resultInfo,apiErrorCodes,false);

            if (rt.getData()){
                ApiHistoryRecordExample example = new ApiHistoryRecordExample();
                example.createCriteria().andApiIdEqualTo(apiID).andIsNowEqualTo(true);
                //变更当前api标志
                List<ApiHistoryRecord> records = historyRecordMapper.selectByExample(example);
                ApiHistoryRecord old = null;
                if (!records.isEmpty()) {
                    old = records.get(0);
                    old.setIsNow(false);
                    historyRecordMapper.updateByPrimaryKey(old);
                }
                record.setIsNow(true);
                historyRecordMapper.updateByPrimaryKey(record);
                return Result.success(true);
            }
            return Result.fail(CommonError.UnknownError);
        }else {
            return Result.fail(CommonError.InvalidParamError);
        }
    }

    @Override
    public Result<ApiHistoryRecord> getHistoryRecordById(Integer recordId) {
        return Result.success(historyRecordMapper.selectByPrimaryKey(recordId));
    }

    @Override
    public Result<Map<String, Object>> compareWithOldVersion(Integer recordId, Integer apiId) {
        Map<String, Object> result = new HashMap<>();
        ApiHistoryRecord oldRecord = historyRecordMapper.selectByPrimaryKey(recordId);
        result.put("oldApi", oldRecord.getApiHistiryJson());
        ApiHistoryRecordExample example = new ApiHistoryRecordExample();
        example.createCriteria().andApiIdEqualTo(apiId).andIsNowEqualTo(true);
        List<ApiHistoryRecord> newRecord = historyRecordMapper.selectByExampleWithBLOBs(example);
        if (!newRecord.isEmpty()) {
            result.put("currentApi", newRecord.get(0).getApiHistiryJson());
        } else {
            result.put("currentApi", "");
        }
        return Result.success(result);
    }

    @Override
    public Map<String, String> compareTwoVersionApi(Integer apiID, Integer compareID) {

        ApiHistoryRecordExample example = new ApiHistoryRecordExample();
        example.createCriteria().andApiIdEqualTo(apiID).andIsNowEqualTo(true);
        List<ApiHistoryRecord> currentRecord = historyRecordMapper.selectByExampleWithBLOBs(example);

        ApiHistoryRecord oldRecord = historyRecordMapper.selectByPrimaryKey(compareID);

        Map<String, String> body = new HashMap<>();
        apiService.compareApiAlterType(currentRecord.get(0), oldRecord, body);
        body.put("currentRecord", gson.toJson(currentRecord.get(0)));
        body.put("oldRecord", gson.toJson(oldRecord));
        return body;
    }
}
