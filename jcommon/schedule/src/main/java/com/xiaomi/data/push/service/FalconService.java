package com.xiaomi.data.push.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.dto.AlertEventDto;
import com.xiaomi.data.push.schedule.task.TaskParam;
import com.xiaomi.youpin.feishu.bo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * @date 2022/9/8
 */
@Service
@Slf4j
public class FalconService {

    @NacosValue("${falcon.token}")
    private String falconToken;
    @NacosValue("${falcon.source.id}")
    private int falconId;
    @NacosValue("${falcon.url}")
    private String falconUrl;

    public boolean alert(TaskParam taskParam, AlertEventDto.Meta meta) {
        try {
            Map headers = new HashMap();
            headers.put("Authorization", "Bearer " + falconToken);
            headers.put("content-type", "application/json");
            AlertEventDto eventDto = AlertEventDto.builder()
                    .source_id(falconId)
                    .level(AlertEventDto.LevelEnum.check(taskParam.getAlarmLevel())?taskParam.getAlarmLevel():AlertEventDto.LevelEnum.P2.getCode())
                    .type(0)
                    .target(AlertEventDto.Target.builder().type(7).name(taskParam.getAlarmGroup()).build())
                    .meta(meta)
                    .build();
            String req = (new Gson()).toJson(eventDto);
            log.info("call falcon.task id:{} req:{} headers:{}, url:{}", taskParam.getTaskId(), req, headers, falconUrl);
            String response = HttpClientV2.post(falconUrl+"/nox/api/v1/event", new String(req.getBytes(), StandardCharsets.UTF_8), headers, 2000);
            if (StringUtils.isEmpty(response)) {
                log.error("http post error when send msg, response:{}", response);
                return false;
            }
            Result result = (new Gson()).fromJson(response, Result.class);
            log.info("falcon alert.task id:{} rst:{}", taskParam.getTaskId(), result);
        } catch (Exception var13) {
            log.error("falcon alert error:{}", var13.getMessage());
            return false;
        }
        return true;
    }
}
