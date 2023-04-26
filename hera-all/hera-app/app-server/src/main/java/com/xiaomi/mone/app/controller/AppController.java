package com.xiaomi.mone.app.controller;

import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.enums.CommonError;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import com.xiaomi.mone.app.service.HeraAppRoleService;
import com.xiaomi.mone.app.service.impl.HeraAppBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 11:57
 */
@Slf4j
@RestController
public class AppController {

    @Autowired
    HeraAppBaseInfoService heraAppBaseInfoService;

    private final HeraAppService heraAppService;

    public AppController(HeraAppService heraAppService) {
        this.heraAppService = heraAppService;
    }

    @Autowired
    private HeraAppRoleService heraAppRoleService;

    @GetMapping("test")
    public String test() {
        return "hello world";
    }

    @GetMapping("query/app/id/info")
    public AppBaseInfo queryByIdInfo(@RequestParam("id") Long id) {
        AppBaseInfo appBaseInfo = heraAppService.queryById(id);
        return appBaseInfo;
    }

    @GetMapping("query/app/id")
    public HeraAppBaseInfo queryById(@RequestParam("id") Long id) {
        HeraAppBaseInfo heraAppBaseInfo = heraAppBaseInfoService.queryById(id);
        return heraAppBaseInfo;
    }

    @GetMapping("query/app/log")
    public List<AppBaseInfo> queryAppInfoWithLog(String appName, Integer type) {
        return heraAppService.queryAppInfoWithLog(appName, type);
    }

    @PostMapping("/hera/app/add")
    public Result heraAppAdd(@RequestBody HeraAppBaseInfo heraAppBaseInfo) {
        if(StringUtils.isBlank(heraAppBaseInfo.getBindId()) || StringUtils.isBlank(heraAppBaseInfo.getAppName())){
            log.error("heraAppAdd param error! BindId or AppName is blank!heraAppBaseInfo:{}",heraAppBaseInfo);
            return Result.fail(CommonError.ParamsError);
        }

        if(heraAppBaseInfo.getAppType() == null){
            heraAppBaseInfo.setAppType(0);//默认0 应用型应用，用户可以扩展自己的绑定方式
        }

        if(heraAppBaseInfo.getBindType() == null){
            heraAppBaseInfo.setBindType(0);//默认按0 appId类型绑定，用户可以根据需要扩展自己的绑定类型
        }

        if(heraAppBaseInfo.getPlatformType() == null){
            heraAppBaseInfo.setPlatformType(0);//默认按0 开源类型，用户可以根据需要扩展自己的平台类型
        }

        try {
            heraAppBaseInfoService.create(heraAppBaseInfo);
            return Result.success();
        } catch (Exception e) {
            log.error("heraAppAdd error! {}",e);
            return Result.fail(CommonError.UnknownError);
        }
    }

    @GetMapping("/mimonitor/addHeraRoleGet")
    public Result addRoleByAppIdAndPlat(String appId, Integer plat, String user){

        try{
            heraAppRoleService.addRoleGet(appId,plat,user);
            return Result.success();
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return Result.fail(CommonError.UnknownError);
        }

    }

    @PostMapping("app/base/query/batch")
    public List<AppBaseInfo> queryByIds(@RequestBody List<Long> ids) {
        return heraAppService.queryByIds(ids);
    }
}
