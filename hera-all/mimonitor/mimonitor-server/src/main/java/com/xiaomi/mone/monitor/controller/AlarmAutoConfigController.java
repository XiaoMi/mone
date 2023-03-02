package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaoxihui
 * @date 2021/9/9 10:10 上午
 */
@Slf4j
@RestController
public class AlarmAutoConfigController {

    @Autowired
    AppMonitorService appMonitorService;

    @ResponseBody
    @GetMapping("/manual/alarm/appPlatMove")
    public Result appPlatMove(Integer OProjectId,Integer OPlat,Integer NProjectId,Integer Nplat,Integer newIamId,String NprojectName){

        appMonitorService.appPlatMove(OProjectId, OPlat, NProjectId, Nplat, newIamId, NprojectName,true);

        return Result.success();
    }



}
