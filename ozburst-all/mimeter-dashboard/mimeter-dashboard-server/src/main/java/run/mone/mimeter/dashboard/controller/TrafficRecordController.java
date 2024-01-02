package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.gateway.manager.bo.openApi.GatewayApiInfoList;
import com.xiaomi.youpin.gateway.manager.bo.openApi.GetGatewayApiInfoListReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.CommonEnum;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.GetRecordingConfigListReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigList;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.RecordingDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import run.mone.mimeter.dashboard.bo.traffic.record.GetGwApiInfoListReq;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.impl.GatewayService;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("/api/bench/trafficRecord")
@HttpApiModule(value = "TrafficRecordController", apiController = TrafficRecordController.class)
public class TrafficRecordController {

    @Autowired
    private LoginService loginService;

    @Value("${server.env}")
    private String env;

    @DubboReference(group = "${ref.traffic.service.group}", interfaceClass = RecordingDubboService.class, check = false,timeout = 3000)
    private RecordingDubboService recordingDubboService;

    @Autowired
    private GatewayService gatewayService;

    @HttpApiDoc(apiName = "网关流量录制---配置列表", value = "/api/bench/trafficRecord/gateway/config/list", method = MiApiRequestMethod.POST, description = "网关流量录制---配置列表")
    @RequestMapping(value = "/gateway/config/list", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<RecordingConfigList> getGatewayRecordingConfigList(HttpServletRequest request,
                                                                     @RequestBody GetRecordingConfigListReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.getGatewayRecordingConfigList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        log.info("[TrafficRecordController.getGatewayRecordingConfigList] param: {}", param);
        try {
            return recordingDubboService.getRecordingConfigList(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---新增配置", value = "/api/bench/trafficRecord/gateway/config/new", method = MiApiRequestMethod.POST, description = "网关流量录制---新增配置")
    @RequestMapping(value = "/gateway/config/new", method = {RequestMethod.POST})
    public Result<Boolean> newGatewayRecordingConfig(HttpServletRequest request,
                                                     @RequestBody RecordingConfig param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.newGatewayRecordingConfig] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }
        param.setCreator(account.getUsername());
        param.setUpdater(account.getUsername());

        log.info("[TrafficRecordController.newGatewayRecordingConfig] param: {}", param);
        try {
            return recordingDubboService.newRecordingConfig(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---更新配置", value = "/api/bench/trafficRecord/gateway/config/update", method = MiApiRequestMethod.POST, description = "网关流量录制---更新配置")
    @RequestMapping(value = "/gateway/config/update", method = {RequestMethod.POST})
    public Result<Boolean> updateGatewayRecordingConfig(HttpServletRequest request,
                                                        @RequestBody RecordingConfig param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.updateGatewayRecordingConfig] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }
        param.setUpdater(account.getUsername());

        log.info("[TrafficRecordController.updateGatewayRecordingConfig] param: {}", param);
        try {
            return recordingDubboService.updateRecordingConfig(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---删除配置", value = "/api/bench/trafficRecord/gateway/config/delete", method = MiApiRequestMethod.POST, description = "网关流量录制---删除配置")
    @RequestMapping(value = "/gateway/config/delete", method = {RequestMethod.POST})
    public Result<Boolean> deleteGatewayRecordingConfig(HttpServletRequest request,
                                                        @RequestBody RecordingConfigReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.deleteGatewayRecordingConfig] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }
        param.setUser(account.getUsername());

        log.info("[TrafficRecordController.deleteGatewayRecordingConfig] param: {}", param);
        try {
            return recordingDubboService.deleteRecordingConfig(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---配置详情", value = "/api/bench/trafficRecord/gateway/config/detail", method = MiApiRequestMethod.POST, description = "网关流量录制---配置详情")
    @RequestMapping(value = "/gateway/config/detail", method = {RequestMethod.POST})
    public Result<RecordingConfig> gatewayRecordingConfigDetail(HttpServletRequest request,
                                                                @RequestBody RecordingConfigReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.gatewayRecordingConfigDetail] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        log.info("[TrafficRecordController.gatewayRecordingConfigDetail] param: {}", param);
        try {
            return recordingDubboService.recordingConfigDetail(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---开始录制", value = "/api/bench/trafficRecord/gateway/config/start", method = MiApiRequestMethod.POST, description = "网关流量录制---开始录制")
    @RequestMapping(value = "/gateway/config/start", method = {RequestMethod.POST})
    public Result<RecordingConfig> startGatewayRecording(HttpServletRequest request,
                                                         @RequestBody RecordingConfigReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.startGatewayRecording] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        param.setUser(account.getUsername());
        log.info("[TrafficRecordController.startGatewayRecording] param: {}", param);
        try {
            return recordingDubboService.startRecording(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---停止录制", value = "/api/bench/trafficRecord/gateway/config/stop", method = MiApiRequestMethod.POST, description = "网关流量录制---停止录制")
    @RequestMapping(value = "/gateway/config/stop", method = {RequestMethod.POST})
    public Result<RecordingConfig> stopGatewayRecording(HttpServletRequest request,
                                                        @RequestBody RecordingConfigReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.stopGatewayRecording] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        param.setUser(account.getUsername());
        log.info("[TrafficRecordController.stopGatewayRecording] param: {}", param);
        try {
            return recordingDubboService.stopRecording(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---获取网关环境枚举", value = "/api/bench/trafficRecord/gateway/envTypes", method = MiApiRequestMethod.GET, description = "网关流量录制---获取网关环境枚举")
    @RequestMapping(value = "/gateway/envTypes", method = {RequestMethod.GET})
    public Result<List<CommonEnum>> getGatewayEnvTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.getGatewayEnvTypes] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        try {
            return recordingDubboService.getGatewayEnvTypes(env);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "网关流量录制---网关接口列表", value = "/api/bench/trafficRecord/gateway/apiInfoList", method = MiApiRequestMethod.POST, description = "网关流量录制---网关接口列表")
    @RequestMapping(value = "/gateway/apiInfoList", method = {RequestMethod.POST})
    public run.mone.mimeter.dashboard.bo.common.Result<GatewayApiInfoList> getGatewayApiInfoList(HttpServletRequest request,
                                                                                                 @RequestBody GetGwApiInfoListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[SlaController.newSla] current user not have valid account info in session");
            return run.mone.mimeter.dashboard.bo.common.Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        try {
            GetGatewayApiInfoListReq req1 = new GetGatewayApiInfoListReq();
            BeanUtils.copyProperties(req, req1);
            return gatewayService.getGatewayApiInfoList(req1, account.getName(), req.getEnv());
        } catch (Exception e) {
            return run.mone.mimeter.dashboard.bo.common.Result.fail(-1, e.getMessage());
        }
    }


    @HttpApiDoc(apiName = "流量录制---获取录制策略枚举", value = "/api/bench/trafficRecord/recordingStrategys", method = MiApiRequestMethod.GET, description = "流量录制---获取录制策略枚举")
    @RequestMapping(value = "/recordingStrategys", method = {RequestMethod.GET})
    public Result<List<CommonEnum>> getRecordingStrategys(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficRecordController.getRecordingStrategys] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        try {
            return recordingDubboService.getRecordingStrategys();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

}
