package com.xiaomi.data.push.common.test;

import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.common.TimeUtils;
import com.xiaomi.data.push.dto.AlertEventDto;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TimeUtilsTest {


    @Test
    public void testMoreThanOneHour() {
        System.out.println(TimeUtils.moreThanOneHour(1576208017000L));
    }

    @Test
    public void testFalcon() {
        AlertEventDto.Meta meta = AlertEventDto.Meta.builder()
                .errorRetryNum(3)
                .id(464)
                .title("【st-mischedule告警】")
                .url("http://xmmischedule.test.mi.com/#/detail?id=111318")
                .taskResult("jopjlikcdsvhs中文").build();
        Map headers = new HashMap();
        headers.put("Authorization", "Bearer " + "86e8efdc-f804-52f2-9056-c5210e33e0bf");
        headers.put("content-type", "application/json");
        AlertEventDto eventDto = AlertEventDto.builder()
                .source_id(50)
                .level(102).type(0)
                .target(AlertEventDto.Target.builder().type(1).name("测试使用1").build())
                .meta(meta)
                .build();
        String req = (new Gson()).toJson(eventDto);
        String response = HttpClientV2.post("http://staging-falcon-api.mioffice.cn/nox/api/v1/event", new String(req.getBytes(), StandardCharsets.UTF_8), headers, 2000);
        System.out.println(response);
    }
}
