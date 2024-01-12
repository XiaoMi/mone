package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.mimeter.dashboard.bo.common.Constants;
import run.mone.mimeter.dashboard.bo.common.EmitterTypeEnum;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.ReportInfoBo;
import run.mone.mimeter.dashboard.bo.sla.SlaRuleItemTypeEnum;
import run.mone.mimeter.dashboard.bo.statistics.TotalStatAnalysisEvent;
import run.mone.mimeter.dashboard.bo.sla.BenchEvent;
import run.mone.mimeter.dashboard.bo.sla.SlaEvent;
import run.mone.mimeter.dashboard.common.util.Util;
import run.mone.mimeter.dashboard.mapper.CheckPointInfoMapper;
import run.mone.mimeter.dashboard.mapper.ReportInfoMapper;
import run.mone.mimeter.dashboard.mapper.SceneApiInfoMapper;
import run.mone.mimeter.dashboard.pojo.CheckPointInfo;
import run.mone.mimeter.dashboard.pojo.ReportInfo;
import run.mone.mimeter.dashboard.pojo.ReportInfoExample;
import run.mone.mimeter.dashboard.pojo.SceneApiInfo;
import run.mone.mimeter.dashboard.service.BenchBroadcastService;
import run.mone.mimeter.dashboard.service.ReportInfoService;
import run.mone.mimeter.dashboard.service.SceneApiService;
import run.mone.mimeter.dashboard.service.SceneService;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static run.mone.mimeter.dashboard.bo.common.Constants.REPORT_STATUS_INACTIVE;

/**
 * @author dongzhenxing
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/21
 */
@Slf4j
@DubboService(interfaceClass = BenchBroadcastService.class, group = "${dubbo.group}",version = "${dubbo.version}",timeout = 20000)
public class BenchBroadcastServiceImpl implements BenchBroadcastService {

    private final String logPrefix = "[BenchBroadcastService]";

    @Autowired
    private ReportEmitterService reportEmitterService;

    @Autowired
    private SceneApiService sceneApiService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ReportInfoService reportInfoService;

    @Autowired
    private SceneApiInfoMapper sceneApiInfoMapper;

    @Autowired
    private CheckPointInfoMapper checkPointInfoMapper;

    @Autowired
    private ReportInfoMapper reportInfoMapper;

    private static final Gson gson = Util.getGson();

    @Override
    public Result<Boolean> notifyEvent(EmitterTypeEnum emitEventType, String reportId, SlaEvent slaEvent) {
        notifySlaEventSync(emitEventType, reportId, slaEvent);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> notifyEvent(EmitterTypeEnum emitEventType, String reportId, TotalStatAnalysisEvent analysisEvent) {
        notifyTotalAnalysisEvent(emitEventType, reportId, analysisEvent);
        return Result.success(true);
    }

    public Result<Boolean> notifyEvent(EmitterTypeEnum emitEventType, String reportId, ReportInfoBo reportInfoBo) {
        notifyFinishSync(emitEventType, reportId, reportInfoBo);
        return Result.success(true);
    }

    private void notifyFinishSync(EmitterTypeEnum emitEventType, String reportId, ReportInfoBo reportInfoBo){
        checkArgument(StringUtils.isNotBlank(reportId), this.logPrefix + "notifyEvent empty report id");
        String logMsg = this.logPrefix + "notifyEvent report id " + reportId + ", emit type " + emitEventType + "; ";

        if (EmitterTypeEnum.FINISH.getValue().equals(emitEventType.getValue())) {
            reportInfoBo.setStatus(REPORT_STATUS_INACTIVE);
            this.reportInfoService.updateReport(reportInfoBo);
            try {
                int sessionCnt = this.reportEmitterService.countSessions(reportId);
                if (0 == sessionCnt) {
                    log.warn(logMsg + "session is zero");
                    return;
                }
                log.info(logMsg + "session cnt is {}", sessionCnt);
                this.reportEmitterService.complete(reportId);
            } catch (Exception e) {
                log.warn("complete failed,e:{}",e.getMessage());
            }
        } else {
            log.error(logMsg + "invalid event type");
        }
    }
    private void notifySlaEventSync(EmitterTypeEnum emitEventType, String reportId, SlaEvent slaEvent) {
        checkArgument(StringUtils.isNotBlank(reportId), this.logPrefix + "notifyEvent empty report id");
        String logMsg = this.logPrefix + "notifyEvent report id " + reportId + ", emit type " + emitEventType + "; ";
        int sessionCnt = this.reportEmitterService.countSessions(reportId);

        if (0 == sessionCnt) {
            log.warn(logMsg + "session is zero");
            return;
        }
        log.info(logMsg + "session cnt is {}", sessionCnt);

        //sla 事件消息
        try {
            this.reportEmitterService.sendSlaMsg(reportId, slaEvent);
        } catch (Exception e) {
            log.warn("reportEmitterService.sendSlaMsg error");
        } finally {
            //补充接口信息
            processSlaEventApiInfo(slaEvent);
            //更新,追加报告中的事件列表
            updateSlaEventByReport(reportId, slaEvent);
        }

    }

    private void notifyTotalAnalysisEvent(EmitterTypeEnum emitEventType, String reportId, TotalStatAnalysisEvent analysisEvent) {
        checkArgument(StringUtils.isNotBlank(reportId), this.logPrefix + "notifyEvent empty report id");
        String logMsg = this.logPrefix + "notifyEvent report id " + reportId + ", emit type " + emitEventType + "; ";
        int sessionCnt = this.reportEmitterService.countSessions(reportId);

        if (0 == sessionCnt) {
            //已经结束，直接更新报告数据
            //更新错误分析事件列表
            //补充事件信息
            if (analysisEvent.isFinish()){
                log.info("total!!  "+analysisEvent.getTotalReq()+"_____err:"+analysisEvent.getTotalErrReq());
                processTotalStatisticEvent(analysisEvent);
                updateTotalStatEventByReport(reportId, analysisEvent,true);

                ReportInfoExample example = new ReportInfoExample();
                example.createCriteria().andReportIdEqualTo(reportId);
                List<ReportInfo> reportInfoList = reportInfoMapper.selectByExample(example);
                if (reportInfoList != null && reportInfoList.size() !=0){
                    ReportInfo reportInfo = reportInfoList.get(0);
                    reportInfo.setStatus(REPORT_STATUS_INACTIVE);
                    long now = System.currentTimeMillis();
                    if (reportInfo.getFinishTime() == null){
                        reportInfo.setFinishTime(now +300000);
                    }
                    if (reportInfo.getDuration() == null){
                        int duration = (int) ((now -reportInfo.getCreateTime().getTime())/1000);
                        reportInfo.setDuration(duration);
                    }
                    reportInfoMapper.updateByPrimaryKey(reportInfo);
                }
            }
            log.warn(logMsg + "session is zero");
            return;
        }
        log.debug(logMsg + "session cnt is {}", sessionCnt);

        try {
            //补充事件信息
            processTotalStatisticEvent(analysisEvent);
            // 推送统计事件
            this.reportEmitterService.sendTotalStatisticMsg(reportId, analysisEvent);
        } catch (Exception e) {
            log.warn("reportEmitterService.sendErrStatisticMsg error");
        } finally {
            //更新数据分析事件列表
            updateTotalStatEventByReport(reportId, analysisEvent,false);
        }
    }

    private void processSlaEventApiInfo(SlaEvent slaEvent){
        try {
            if (slaEvent.getRuleItemType().equals(SlaRuleItemTypeEnum.BusinessMetrics.ruleItemTypeName)){
                //业务指标报警
                if (slaEvent.getTriggerApiId() != 0){
                    SceneApiInfo apiInfo = sceneApiInfoMapper.selectByPrimaryKey(slaEvent.getTriggerApiId());
                    slaEvent.setApiName(apiInfo.getApiName());
                    if (apiInfo.getApiType() == Constants.CASE_TYPE_HTTP){
                        slaEvent.setApiUrlOrServiceName(apiInfo.getApiUrl());
                    }else if (apiInfo.getApiType() == Constants.CASE_TYPE_RPCX){
                        slaEvent.setApiUrlOrServiceName(apiInfo.getServiceName()+":"+apiInfo.getMethodName());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("get api info err:{}",e.getMessage());
        }
    }

    private void processTotalStatisticEvent(TotalStatAnalysisEvent totalAnalysisEvent) {
        totalAnalysisEvent.getErrorTypeAnalyses().forEach(errorTypeAnalysis -> {
            //各个错误总计类
            Integer apiId = errorTypeAnalysis.getMostErrApi();
            if (apiId != null) {
                //次数最多的api
                SceneApiInfo sceneApiInfo = sceneApiInfoMapper.selectByPrimaryKey(apiId);
                errorTypeAnalysis.setMostErrApiName(sceneApiInfo.getApiName());

                if (errorTypeAnalysis.getErrorType() == 1) {
                    //检查点错误
                    int checkPointId = errorTypeAnalysis.getCheckPointId();
                    CheckPointInfo checkPointInfo = checkPointInfoMapper.selectByPrimaryKey(checkPointId);
                    errorTypeAnalysis.setCheckPointDesc(gson.toJson(checkPointInfo));
                }
            }
        });

        //接口信息补充
        totalAnalysisEvent.getApiStatisticsList().forEach(apiStatistics -> {
            SceneApiInfo apiInfo = sceneApiInfoMapper.selectByPrimaryKey(apiStatistics.getApiId());
            if (apiInfo != null){
                apiStatistics.setApiName(apiInfo.getApiName());
                apiStatistics.setApiType(apiInfo.getApiType());
                if (apiInfo.getApiType() == Constants.CASE_TYPE_HTTP){
                    apiStatistics.setUri(apiInfo.getApiUrl());
                    if (apiInfo.getRequestMethod() == Constants.HTTP_REQ_GET){
                        apiStatistics.setMethod("get");
                    }else {
                        apiStatistics.setMethod("post");
                    }
                }else if (apiInfo.getApiType() == Constants.CASE_TYPE_RPCX){
                    apiStatistics.setDubboServiceName(apiInfo.getServiceName());
                    apiStatistics.setDubboMethodName(apiInfo.getMethodName());
                    apiStatistics.setDubboGroup(apiInfo.getDubboGroup());
                    apiStatistics.setDubboVersion(apiInfo.getDubboVersion());
                }
                apiStatistics.setSerialId(apiInfo.getSerialLinkId());
            }
        });
    }

    private void updateTotalStatEventByReport(String reportId, TotalStatAnalysisEvent analysisEvent,boolean lastTime) {
        //更新报告中的数据分析事件
        ReportInfoExample example = new ReportInfoExample();
        example.createCriteria().andReportIdEqualTo(reportId);

        List<ReportInfo> reportInfos = reportInfoMapper.selectByExampleWithBLOBs(example);
        if (reportInfos == null || reportInfos.size() != 1) {
            return;
        }
        ReportInfo reportInfo = reportInfos.get(0);
        reportInfo.setConcurrency(analysisEvent.getAvgTps());
        reportInfo.setConcurrencyMax(analysisEvent.getMaxTps());
        reportInfo.setTotalStatAnalysisEventList(gson.toJson(analysisEvent));
        if (lastTime){
            reportInfo.setFinishTime(System.currentTimeMillis());
        }
        //更新链路 rps 数据
        if (analysisEvent.getLinkToDagTaskRpsMap() != null && !analysisEvent.getLinkToDagTaskRpsMap().isEmpty()){
            reportInfo.setLinkToDagId(gson.toJson(analysisEvent.getLinkToDagTaskRpsMap()));
        }
        reportInfoMapper.updateByPrimaryKeyWithBLOBs(reportInfo);
    }

    private void updateSlaEventByReport(String reportId, BenchEvent benchEvent) {
        //更新报告中的事件列表
        ReportInfoExample example = new ReportInfoExample();
        example.createCriteria().andReportIdEqualTo(reportId);

        List<ReportInfo> reportInfos = reportInfoMapper.selectByExampleWithBLOBs(example);
        if (reportInfos == null || reportInfos.size() != 1) {
            return;
        }
        ReportInfo reportInfo = reportInfos.get(0);
        List<BenchEvent> benchEvents = gson.fromJson(reportInfo.getSlaEventList(), new TypeToken<List<SlaEvent>>() {
        }.getType());
        if (benchEvents != null) {
            benchEvents.add(benchEvent);
        } else {
            benchEvents = new ArrayList<>();
            benchEvents.add(benchEvent);
        }
        reportInfo.setSlaEventList(gson.toJson(benchEvents));
        reportInfoMapper.updateByPrimaryKeyWithBLOBs(reportInfo);
    }

}
