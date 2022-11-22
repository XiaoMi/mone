package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.OperationLogBo;
import com.xiaomi.youpin.gwdash.bo.Page;
import com.xiaomi.youpin.gwdash.bo.openApi.OperationLogRequest;
import com.xiaomi.youpin.infra.rpc.Result;

public interface OperationLogService {


    /**
     * 存入操作日志接口
     * @return
     */
    Result<Boolean> saveOperationLog(OperationLogRequest request);


    /**
     * 查询操作日志接口
     */

    Page<OperationLogBo> queryLogInfoList(String appName,String dataId,int  type, int pageIndex, int pageSize);

}
