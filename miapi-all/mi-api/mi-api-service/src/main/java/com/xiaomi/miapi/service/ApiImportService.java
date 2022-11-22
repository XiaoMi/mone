package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.Result;

public interface ApiImportService {
    Result<Integer> importSwagger(Integer projectID,String data, boolean randomGen,String userName);
}
