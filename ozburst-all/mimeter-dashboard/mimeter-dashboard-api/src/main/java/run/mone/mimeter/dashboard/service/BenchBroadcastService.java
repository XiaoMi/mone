package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.EmitterTypeEnum;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.ReportInfoBo;
import run.mone.mimeter.dashboard.bo.statistics.TotalStatAnalysisEvent;
import run.mone.mimeter.dashboard.bo.sla.SlaEvent;


/**
 * @author dongzhenxing
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/21
 */
public interface BenchBroadcastService {

    /**
     * 压测结束事件通知
     */
    Result<Boolean> notifyEvent(EmitterTypeEnum emitEventType, String reportId, ReportInfoBo reportInfoBo);

    /**
     * sla事件通知
     */
    Result<Boolean> notifyEvent(EmitterTypeEnum emitEventType, String reportId, SlaEvent slaEvent);

    /**
     * 数据统计分析事件通知
     */
    Result<Boolean> notifyEvent(EmitterTypeEnum emitEventType, String reportId, TotalStatAnalysisEvent analysisEvent);

}
