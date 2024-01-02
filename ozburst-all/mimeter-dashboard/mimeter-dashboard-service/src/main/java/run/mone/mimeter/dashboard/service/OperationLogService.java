package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.operationlog.GetOperationLogListReq;
import run.mone.mimeter.dashboard.bo.operationlog.OperationLogDto;
import run.mone.mimeter.dashboard.bo.operationlog.OperationLogList;
import run.mone.mimeter.dashboard.bo.sla.GetSlaListReq;
import run.mone.mimeter.dashboard.bo.sla.SlaDto;
import run.mone.mimeter.dashboard.bo.sla.SlaList;

import java.util.List;

public interface OperationLogService {

    /**
     * sla
     */
    void newOperationLog(OperationLogDto param);

    Result<OperationLogList> getOperationLogList(GetOperationLogListReq req);

}
