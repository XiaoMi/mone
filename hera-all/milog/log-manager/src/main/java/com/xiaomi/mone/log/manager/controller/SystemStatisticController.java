package com.xiaomi.mone.log.manager.controller;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.StatisticsQuery;
import com.xiaomi.mone.log.manager.model.dto.RocketMqStatisticDTO;
import com.xiaomi.mone.log.manager.model.vo.RocketMQStatisCommand;
import com.xiaomi.mone.log.manager.service.RocketMqService;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Controller
public class SystemStatisticController {

    @Resource
    private RocketMqService rocketMqService;

    @RequestMapping(path = "/milog/statistic/rmq")
    public Result<RocketMqStatisticDTO> statisticRmq(@RequestParam("param") RocketMQStatisCommand command) {
        return rocketMqService.httpGetProducerTps(command);
    }

}
