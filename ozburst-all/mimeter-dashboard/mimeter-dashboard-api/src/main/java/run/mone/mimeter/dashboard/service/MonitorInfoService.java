package run.mone.mimeter.dashboard.service;

import java.util.List;

public interface MonitorInfoService {
    List<String> getAppListByReportID(Integer sceneId, String reportId);
}
