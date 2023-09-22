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
        //The comment cannot be empty.
        if (comment.equals("") || comment.isEmpty()) {
            return false;
        }
        // alertId and matcher cannot both be present or both be empty.
        if ((alertId == null || alertId.isEmpty()) && matcher.isEmpty() || (!alertId.isEmpty() && !matcher.isEmpty())) {
            return false;
        }
        //Check Matchers
        if (!ValidateMatchers(matcher)) {
            return false;
        }
        //Time synchronization
        if (startTime == null || endTime == null || startTime < endTime) {
            return false;
        }

        return true;
    }

    private boolean ValidateMatchers(List<Matcher> matchers) {
        AtomicBoolean valid = new AtomicBoolean(true);
        //The ame and value fields cannot be empty, isEqual and isRegex default to false if not passed.
        matchers.forEach(matcher -> {
            if (matcher.getName().isEmpty() || matcher.getValue().isEmpty() || (!matcher.isEqual() && matcher.isRegex())) {
                valid.set(false);
            }
        });
        return valid.get();
    }
}
