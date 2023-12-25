package run.mone.mimeter.dashboard.service;

import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.ReportInfoBo;

import java.util.List;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/22
 */
public interface ReportInfoService {

    Result<List<ReportInfoBo>> listReports(String tenant, String keyword, Integer pageNo, Integer pageSize);

    Result<Long> countReports(String tenant, String keyword, Integer pageNo, Integer pageSize);

    Result<ReportInfoBo> getReportDetails(Long sceneId, String reportId);

    Result<String> createReport(ReportInfoBo bo);

    Result<Integer> removeReports(List<Long> reportIds);

    Result<Boolean> updateReport(ReportInfoBo bo);
}
