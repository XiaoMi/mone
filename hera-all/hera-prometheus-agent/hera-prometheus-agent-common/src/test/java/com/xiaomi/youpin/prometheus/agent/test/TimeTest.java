package com.xiaomi.youpin.prometheus.agent.test;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

public class TimeTest {

    @Test
    public void testStartTimeAndEndTime() {
        System.out.println(ValidateTime(1682975833, 1682975832));
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
}
