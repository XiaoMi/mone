package com.xiaomi.youpin.prometheus.agent.param.alert;


import com.xiaomi.youpin.prometheus.agent.param.BaseParam;
import lombok.Data;
import lombok.ToString;


import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@ToString(callSuper = true)
public class RuleSilenceParam extends BaseParam {

    private String alertId;
    private List<Matcher> matcher;
    private Long startTime;
    private Long endTime;
    private String comment;

    public boolean argCheck() {
        //comment不能为空
        if (comment.equals("") || comment.isEmpty()) {
            return false;
        }
        // alertId和matcher不能同时有或者同时为空
        if ((alertId == null || alertId.isEmpty()) && matcher.isEmpty() || (!alertId.isEmpty() && !matcher.isEmpty())) {
            return false;
        }
        //检验Matchers
        if (!ValidateMatchers(matcher)) {
            return false;
        }
        //时间校验
        if (startTime == null || endTime == null || startTime < endTime ) {
            return false;
        }

        return true;
    }

    private boolean ValidateMatchers(List<Matcher> matchers) {
        AtomicBoolean valid = new AtomicBoolean(true);
        //name、value字段不可为空，isEqual与isRegex不传默认false
        matchers.forEach(matcher -> {
            if (matcher.getName().isEmpty() || matcher.getValue().isEmpty() || (!matcher.isEqual() && matcher.isRegex())) {
                valid.set(false);
            }
        });
        return valid.get();
    }
}
