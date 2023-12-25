package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.PagedResp;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.ReqRespLogRecord;
import run.mone.mimeter.dashboard.bo.report.SearchApiLogReq;

import java.util.List;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/6
 */
public interface EsLogService {

    Result<PagedResp<List<ReqRespLogRecord>>> searchApiLogs(SearchApiLogReq req);
}
