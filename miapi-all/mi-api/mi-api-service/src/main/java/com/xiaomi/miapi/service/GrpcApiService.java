package com.xiaomi.miapi.service;

import com.xiaomi.miapi.bo.BatchAddGrpcApiBo;
import com.xiaomi.miapi.bo.GrpcApiInfosBo;
import com.xiaomi.miapi.bo.UpdateGrpcApiBo;
import com.xiaomi.miapi.common.Result;

import java.util.Map;

public interface GrpcApiService {
    Result<GrpcApiInfosBo> loadGrpcApiInfos(String appName) throws Exception;

    Result<String> loadGrpcServerAddr(String appName) throws Exception;

    Result<Boolean> batchAddGrpcApi(BatchAddGrpcApiBo grpcApiBo) throws Exception;

    Result<Boolean> updateGrpcApi(UpdateGrpcApiBo updateGrpcApiBo);

    Result<Map<String,Object>> getGrpcApiDetail(String username, int projectID, int apiID);

}
