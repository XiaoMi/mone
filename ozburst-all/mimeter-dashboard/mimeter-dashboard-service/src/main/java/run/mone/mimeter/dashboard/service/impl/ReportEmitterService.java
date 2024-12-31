package run.mone.mimeter.dashboard.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.mimeter.dashboard.bo.common.EmitterTypeEnum;
import run.mone.mimeter.dashboard.bo.statistics.TotalStatAnalysisEvent;
import run.mone.mimeter.dashboard.bo.sla.SlaEvent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/30
 */
@Service
@Slf4j
public class ReportEmitterService {

    private final ConcurrentMap<String, ConcurrentHashMap<String, SseEmitter>> reportEmitterMap = new ConcurrentHashMap<>();

    private final String logPrefix = "[ReportEmitterService]";

    public int countSessions(String reportId) {
        if (!this.reportEmitterMap.containsKey(reportId)) {
            return 0;
        }
        return this.reportEmitterMap.get(reportId).size();
    }

    public SseEmitter connect(String reportId, String username) {
        checkArgument(StringUtils.isNotBlank(reportId) && StringUtils.isNotBlank(username),
                this.logPrefix + "connect empty input");
        SseEmitter emitter = new SseEmitter(3600_000L);
        ConcurrentHashMap<String, SseEmitter> map = Optional.ofNullable(this.reportEmitterMap.get(reportId)).orElse(new ConcurrentHashMap<>());
        map.put(username, emitter);
        this.reportEmitterMap.put(reportId, map);
        log.info(this.logPrefix + "connected with {}, report id:{}", username, reportId);

        return emitter;
    }

    public void sendSlaMsg(String reportId, SlaEvent slaEvent) {
        if (!this.reportEmitterMap.containsKey(reportId)) {
            log.warn(this.logPrefix + "sendSlaMsg client connection is not established, report id:" + reportId);
            return;
        }
        this.doSendMessage("sla_event", reportId, slaEvent);
    }

    public void sendTotalStatisticMsg(String reportId, TotalStatAnalysisEvent analysisEvent) {
        if (!this.reportEmitterMap.containsKey(reportId)) {
            log.warn(this.logPrefix + "sendErrStatisticMsg client connection is not established, report id:" + reportId);
            return;
        }
        try {
            this.doSendMessage("total_analysis_event", reportId, analysisEvent);
        } catch (Exception e) {
            log.warn("send sendErrStatisticMsg error_analysis_event:{}",e.getMessage());
        }
    }

    private void doSendMessage(String eventName, String reportId, Object data) {
        List<String> removeUsers = new ArrayList<>();

        this.reportEmitterMap.get(reportId).forEach((username, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data, MediaType.APPLICATION_JSON));
            } catch (IllegalStateException illegalStateException) {
                Optional.of(emitter).ifPresent(ResponseBodyEmitter::complete);
                removeUsers.add(username);
                log.error(this.logPrefix + "doSendMessage id " + reportId + ", username:" + username + " is complete; " +
                        illegalStateException.getMessage());
            } catch (Exception e) {
                log.error(this.logPrefix + "doSendMessage id:" + reportId + ", username " + username, e);
            }
        });
        removeUsers.forEach(username -> this.reportEmitterMap.get(reportId).remove(username));
    }

    public boolean complete(String reportId) {
        if (StringUtils.isBlank(reportId) || !this.reportEmitterMap.containsKey(reportId)) {
            log.warn(this.logPrefix + "complete report id is empty or could not be found");
            return true;
        }
        log.info(logPrefix + "complete invoked report id: {}, list size: {}", reportId, this.reportEmitterMap.get(reportId).size());

        this.reportEmitterMap.get(reportId).forEach((username, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name(EmitterTypeEnum.FINISH.getValue()).data("finish",MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                log.error(this.logPrefix + "complete id:" + reportId + " username:" + username, e);
            }
        });
        // wait some time to close after sending FIN, so it's in another for loop
        this.reportEmitterMap.get(reportId).forEach((username, emitter) -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.error(this.logPrefix + "complete id:" + reportId + " username:" + username, e);
            }
        });
        return this.reportEmitterMap.remove(reportId) != null;
    }
}
