package com.xiaomi.miapi.api.service;

import com.xiaomi.miapi.api.service.bo.*;
import com.xiaomi.youpin.infra.rpc.Result;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface MiApiDataService {

    Result<Boolean> feiShuDubboApplyCallback(DubboApplyDTO dto);

    void pushServiceDocDataToMiApi(DubboDocDataBo dubboDocDataBo);

    void pushServiceDocDataToMiApi(HttpDocDataBo httpDocDataBo);

    void pushServiceDocDataToMiApi(SidecarDocDataBo sidecarDocDataBo);

}
