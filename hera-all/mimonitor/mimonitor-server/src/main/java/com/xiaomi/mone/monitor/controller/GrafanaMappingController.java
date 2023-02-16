package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.bo.PlatFormType;
import com.xiaomi.mone.monitor.dao.model.HeraAppBaseInfo;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.AppGrafanaMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaoxihui
 * @date 2021/7/8 8:28 下午
 */
@Slf4j
@RestController
public class GrafanaMappingController {

    @Autowired
    AppGrafanaMappingService appGrafanaMappingService;

    @GetMapping("/mimonitor/getGrafanaUrlByAppName")
    public Result getGrafanaUrlByAppName(String appName){
        return appGrafanaMappingService.getGrafanaUrlByAppName(appName);
    }

    @ResponseBody
    @GetMapping("/mimonitor/getGrafanaUrlByAppId")
    public Result<String> getGrafanaUrlByAppId(Integer appId){
        return appGrafanaMappingService.getGrafanaUrlByAppId(appId);
    }

    @ResponseBody
    @GetMapping("/mimonitor/getGrafanaUrlByIamTreeId")
    public Result<String> getGrafanaUrlByIamTreeId(Integer iamTreeId){
        return appGrafanaMappingService.getByIamTreeId(iamTreeId);
    }

    @GetMapping("/api-manual/test")
    public String manualTest(){

        log.info("GrafanaMappingController.manualTest ...");

        return "ooooook!!!";
    }


    @GetMapping("/api-manual/mimonitor/createGrafanaUrlByBaseInfo")
    public String createGrafanaUrlByBaseInfo(Integer appId,String appName,String plat,Integer appType,String language){

        log.info("GrafanaMappingController.createGrafanaUrlByBaseInfo request appId:{},appName:{},plat{},appType:{},language:{}",
                appId,appName,plat,appType,language);

        HeraAppBaseInfo baseInfo = new HeraAppBaseInfo();
        baseInfo.setBindId(appId + "");
        baseInfo.setAppName(appName);
        baseInfo.setPlatformType(PlatFormType.getCodeByGrafanaDir(plat));
        baseInfo.setAppType(appType);
        baseInfo.setAppLanguage(language);

        appGrafanaMappingService.createTmpByAppBaseInfo(baseInfo);

        return "Success!";
    }

    @GetMapping("/mimonitor/loadGrafanaTemplateBase")
    public Result loadGrafanaTemplateBase(Integer id){

        if(id == null){
            log.error("loadGrafanaTemplateBase invalid param id is null!");
            return Result.fail(ErrorCode.invalidParamError);
        }

        try {
            appGrafanaMappingService.reloadTmpByAppId(id);
        } catch (Exception e) {
            log.error("loadGrafanaTemplateBase error!{}",e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }

        return Result.success("success");
    }


}
