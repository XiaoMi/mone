package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.GetTrafficReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.TrafficList;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.TrafficDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.service.impl.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("/api/bench/traffic")
@HttpApiModule(value = "TrafficController", apiController = TrafficController.class)
public class TrafficController {

    @Autowired
    private LoginService loginService;

    @DubboReference(group = "${ref.traffic.service.group}", interfaceClass = TrafficDubboService.class, check = false,timeout = 3000)
    private TrafficDubboService trafficDubboService;

    @HttpApiDoc(apiName = "流量列表", value = "/api/bench/traffic/list", method = MiApiRequestMethod.POST, description = "流量列表")
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<TrafficList> getTrafficList(HttpServletRequest request,
                                              @RequestBody GetTrafficReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficController.getTrafficList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        log.info("[TrafficController.getTrafficList] param: {}", param);
        try {
            return trafficDubboService.getTrafficList(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @HttpApiDoc(apiName = "流量删除", value = "/api/bench/traffic/del", method = MiApiRequestMethod.POST, description = "流量删除")
    @RequestMapping(value = "/del", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<Boolean> delTraffic(HttpServletRequest request,
                                              @RequestBody GetTrafficReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[TrafficController.delTraffic] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser.code, CommonError.UnknownUser.message);
        }

        log.info("[TrafficController.delTraffic] param: {}", param);
        try {
            return trafficDubboService.delTraffic(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }


}
