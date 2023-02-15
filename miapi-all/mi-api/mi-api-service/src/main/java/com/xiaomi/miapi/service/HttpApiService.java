package com.xiaomi.miapi.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.bo.BatchImportApiBo;
import com.xiaomi.miapi.bo.HttpApiUpdateNotifyBo;
import com.xiaomi.miapi.dto.ManualHttpUpDTO;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.pojo.Api;

import java.util.List;
import java.util.Map;

public interface HttpApiService {
    Result<Boolean> addHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes,boolean randomGen);

    Result<Boolean> batchAddHttpApi(String apiEnv, List<BatchImportApiBo> bos);

    Result<Boolean> editHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam,String apiErrorCodes,boolean doRecord);

    Result<Map<String,Object>> getAllHttpModulesInfo(String serviceName,String ip);

    Result<Boolean> manualUpdateHttpApi(ManualHttpUpDTO dto) throws NacosException;

    Map<String, Object> getHttpApi(String username,Integer projectID, Integer apiID);

    void httpApiUpdateNotify(HttpApiUpdateNotifyBo bo) throws InterruptedException;

}
