package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.QualityMarketService;
import com.xiaomi.mone.monitor.service.model.QualityMarketQuery;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangxiaowei6
 */
@Slf4j
@RestController

public class QualityMarketController {

    @Autowired
    QualityMarketService  qualityMarketService;

    //创建服务大盘
    @PostMapping("/qualityMarket/mimonitor/createMarket")
    public Result createQualityMarket(HttpServletRequest request, @RequestBody QualityMarketQuery param) {
        log.info("qualityMarket.createMarket : {} " , param);
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (StringUtils.isNotEmpty(param.getMarketName()) && StringUtils.isNotEmpty(param.getServiceList()) ) {
            return qualityMarketService.createMarket(user,param.getMarketName(), param.getServiceList(), param.getRemark());
        }
        return Result.fail(ErrorCode.RequestBodyIsEmpty);
    }

    //查看创建的market
    @GetMapping("/qualityMarket/mimonitor/searchMarket")
    public Result searchQualityMarket(HttpServletRequest request,Integer primaryId) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (primaryId != null && primaryId != 0) {
            return qualityMarketService.searchMarket(user,primaryId);
        }
        return Result.fail(ErrorCode.ScrapeIdIsEmpty);
    }

    //更新创建的market
    @PostMapping("/qualityMarket/mimonitor/updateMarket")
    public Result updateQualityMarket(HttpServletRequest request,@RequestBody QualityMarketQuery param) {
        log.info("qualityMarket.updateQualityMarket : {} " , param);
        Integer id = param.getId();
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (id != null && id != 0 && StringUtils.isNotEmpty(param.getMarketName())) {
            return qualityMarketService.updateMarket(user,id,param.getServiceList(),param.getMarketName(),param.getRemark());
        }
        return Result.fail(ErrorCode.invalidParamError);
    }

    //删除market
    @PostMapping("/qualityMarket/mimonitor/deleteMarket")
    public Result deleteQualityMarket(HttpServletRequest request,Integer primaryId) {
        log.info("qualityMarket.deleteQualityMarket id:{} " ,primaryId);
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (primaryId != null && primaryId != 0) {
            return qualityMarketService.deleteMarket(user,primaryId);
        }
        return Result.fail(ErrorCode.invalidParamError);
    }

    //查列表
    @GetMapping("/qualityMarket/mimonitor/searchMarketList")
    public Result searchMarketList(HttpServletRequest request, Integer pageSize, Integer page, String creator,String marketName,String serviceName) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        //如果不传默认为看第一页前十条
        if (pageSize == 0) {
            pageSize = 10;
        }
        if (page == 0) {
            page = 1;
        }
        return qualityMarketService.searchMarketList(user, pageSize,page,creator,marketName,serviceName);
    }


    public String checkUser(HttpServletRequest request) {
        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            return "";
        } else {
            return userInfo.genFullAccount();
        }
    }
}
