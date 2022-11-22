package com.xiaomi.miapi.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.common.bo.BatchImportHttpApiBo;
import com.xiaomi.miapi.common.bo.HttpApiUpdateNotifyBo;
import com.xiaomi.miapi.common.dto.ManualHttpUpDTO;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.pojo.Api;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface HttpApiService {
    //添加接口
    public Result<Boolean> addHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam, String apiErrorCodes,boolean randomGen);

    //批量添加http接口
    public Result<Boolean> batchAddHttpApi(String apiEnv, List<BatchImportHttpApiBo> bos);

    //修改接口
    public Result<Boolean> editHttpApi(Api api, String apiHeader, String apiRequestParam, String apiResultParam,String apiErrorCodes,boolean doRecord);

    public Result<Map<String,Object>> getAllHttpModulesInfo(String serviceName) throws NacosException;

    public Result<Boolean> manualUpdateHttpApi(ManualHttpUpDTO dto) throws NacosException;

    //获取接口信息
    public Map<String, Object> getHttpApi(Integer userId,Integer projectID, Integer apiID);

    Map<String, Object> getBasicHttpApi(Integer projectID, Integer apiID);

    public Result<Boolean> httpApiUpdateNotify(HttpApiUpdateNotifyBo bo) throws InterruptedException;

}
