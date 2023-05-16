package com.xiaomi.youpin.prometheus.agent.service.prometheus;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.xiaomi.youpin.prometheus.agent.Impl.RuleAlertDao;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.param.alert.AMSilence;
import com.xiaomi.youpin.prometheus.agent.param.alert.AMSilenceResponse;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleSilenceParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.util.Http;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static com.xiaomi.youpin.prometheus.agent.Commons.HTTP_POST;

@Slf4j
@Service
public class RuleSilenceService {

    @Autowired
    RuleAlertDao ruleAlertDao;

    @NacosValue(value = "${job.alertManager.Addr}", autoRefreshed = true)
    private String alertManagerAddr;

    private final Gson gson = new Gson();

    public static final String CREATE_SILENCE = "/api/v2/silences";

    public Result createRuleSilence(RuleSilenceParam param) {

        if (!param.getAlertId().isEmpty() && ruleAlertDao.GetRuleAlert(param.getAlertId()) == null) {
            return Result.fail(ErrorCode.NO_DATA_FOUND);
        }
        //校验时间
        String timeValid = ValidateTime(param.getStartTime(), param.getEndTime());
        if (!timeValid.equals("")) {
            return Result.fail(ErrorCode.invalidParamError, timeValid);
        }
        //请求alterManager
        String s = AddSilence(param);
        System.out.println("res: " + s);

        return null;
    }

    private String ValidateTime(long startTime, long endTime) {
        Timestamp sTimeStamp = new Timestamp(startTime);
        Timestamp eTimeStamp = new Timestamp(endTime);
        Timestamp nowTimeStamp = new Timestamp(System.currentTimeMillis() / 1000);
        if (sTimeStamp.equals(0) || eTimeStamp.equals(0)) {
            return "invalid zero start timestamp ro end timestamp";
        }
        if (eTimeStamp.before(nowTimeStamp)) {
            return "end time can not be in the past";
        }
        if (eTimeStamp.before(sTimeStamp)) {
            return "end time must not be before start time";
        }
        return "";
    }

    private String AddSilence(RuleSilenceParam silence) {
        // 非屏蔽规则时才去 alertmanager 创建 silence
        //if (silence.getAlertId().isEmpty()) {
        // 向 alertmanager 创建 silence
        String requestPath = alertManagerAddr + CREATE_SILENCE;
        AMSilence amSilence = convertToAMSilence(silence);
        String amSilenceStr = gson.toJson(amSilence);
        String response = Http.innerRequest(amSilenceStr, requestPath, HTTP_POST);
        AMSilenceResponse amSilenceResponse = gson.fromJson(response, AMSilenceResponse.class);
        String silenceId = amSilenceResponse.getSilenceID();
        //}
        return silenceId;
    }

    private AMSilence convertToAMSilence(RuleSilenceParam silence) {
        AMSilence amSilence = new AMSilence();
        amSilence.setComment(silence.getComment());
        amSilence.setMatchers(silence.getMatcher());
        //TODO：以后改为真实用户
        amSilence.setCreatedBy("xxx");
        //startTime与endTime为UTC时间
        amSilence.setStartsAt(silence.getStartTime() - 8 * 3600);
        amSilence.setEndsAt(silence.getEndTime() - 8 * 3600);
        amSilence.setId("1");
        return amSilence;
    }

}
