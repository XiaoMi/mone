package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.bo.BatchAddGrpcApiBo;
import com.xiaomi.miapi.common.bo.GrpcApiInfosBo;
import com.xiaomi.miapi.common.bo.UpdateGrpcApiBo;
import com.xiaomi.miapi.common.Result;

import java.util.Map;

public interface GrpcApiService {
    Result<GrpcApiInfosBo> loadGrpcApiInfos(String appName) throws Exception;

    Result<String> loadGrpcServerAddr(String appName) throws Exception;

    Result<Boolean> batchAddGrpcApi(BatchAddGrpcApiBo grpcApiBo) throws Exception;

    Result<Boolean> updateGrpcApi(UpdateGrpcApiBo updateGrpcApiBo);

    Result<Map<String,Object>> getGrpcApiDetail(int accountID, int projectID, int apiID);

}
