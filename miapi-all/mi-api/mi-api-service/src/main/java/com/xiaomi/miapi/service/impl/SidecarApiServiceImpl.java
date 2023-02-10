package com.xiaomi.miapi.service.impl;
import com.xiaomi.miapi.bo.BatchImportApiBo;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.dto.ManualSidecarUpDTO;
import com.xiaomi.miapi.pojo.Api;
import com.xiaomi.miapi.service.SidecarApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * work for faas service mesh sidecar
 * will impl after faas push its opensource code
 */
@Service
@Slf4j
public class SidecarApiServiceImpl implements SidecarApiService {
    @Override
    public Result<Boolean> batchAddSidecarApi(String apiEnv, List<BatchImportApiBo> bos) {
        return null;
    }

    @Override
    public Result<Boolean> editSidecarApi(Api api, String apiRequestParam, String apiResultParam, boolean doRecord) {
        return null;
    }

    @Override
    public Result<Map<String, Object>> getAllSidecarModulesInfo(String moduleName, String ip) {
        return null;
    }

    @Override
    public Result<Boolean> manualUpdateSidecarApi(ManualSidecarUpDTO dto) {
        return null;
    }

    @Override
    public Map<String, Object> getSidecarApi(String username, Integer projectID, Integer apiID) {
        return null;
    }
}
