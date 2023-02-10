package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.bo.BatchImportApiBo;
import com.xiaomi.miapi.dto.ManualSidecarUpDTO;
import com.xiaomi.miapi.pojo.Api;

import java.util.List;
import java.util.Map;

public interface SidecarApiService {

    Result<Boolean> batchAddSidecarApi(String apiEnv, List<BatchImportApiBo> bos);

    Result<Boolean> editSidecarApi(Api api, String apiRequestParam, String apiResultParam,boolean doRecord);

    Result<Map<String,Object>> getAllSidecarModulesInfo(String moduleName, String ip);

    Result<Boolean> manualUpdateSidecarApi(ManualSidecarUpDTO dto);

    Map<String, Object> getSidecarApi(String username,Integer projectID, Integer apiID);

}
