package com.xiaomi.mone.app.controller;

import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import com.xiaomi.mone.app.model.vo.HeraAppOperateVo;
import com.xiaomi.mone.app.service.HeraAppEnvService;
import com.xiaomi.mone.app.valid.AddGroup;
import com.xiaomi.mone.app.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wtt
 * @version 1.0
 * @description 应用环境相关的http服务
 * @date 2022/11/9 17:45
 */
@RestController
@RequestMapping("/hera/app/env")
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class HeraAppEnvController {

    @Autowired
    private HeraAppEnvService heraAppEnvService;

    @GetMapping("/id")
    public HeraAppEnvVo queryAppEnvById(@RequestParam("id") Long id) {
        return heraAppEnvService.queryAppEnvById(id);
    }

    @PostMapping("/add")
    public Long addAppEnv(@Validated({AddGroup.class}) @RequestBody HeraAppOperateVo operateVo) {
        return heraAppEnvService.addAppEnv(operateVo);
    }

    @PostMapping("/update")
    public Long updateAppEnv(@Validated({UpdateGroup.class}) @RequestBody HeraAppOperateVo operateVo) {
        return heraAppEnvService.updateAppEnv(operateVo);
    }

    @PostMapping("/delete")
    public Boolean deleteAppEnv(@RequestParam("id") Long id) {
        return heraAppEnvService.deleteAppEnv(id);
    }
}
