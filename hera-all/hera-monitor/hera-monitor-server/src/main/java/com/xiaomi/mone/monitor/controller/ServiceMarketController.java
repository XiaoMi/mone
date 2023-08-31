package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.ServiceMarketService;
import com.xiaomi.mone.monitor.service.model.ServiceMarketQuery;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangxiaowei6
 */
@Slf4j
@RestController
public class ServiceMarketController {

    @Autowired
    ServiceMarketService serviceMarket;

    //创建服务大盘
    @PostMapping("/serviceMarket/mimonitor/createMarket")
    public Result createServiceMarket(HttpServletRequest request, @RequestBody ServiceMarketQuery param) {
        log.info("ServiceMarket.createMarket : {} " , param);
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (StringUtils.isNotEmpty(param.getMarketName()) && StringUtils.isNotEmpty(param.getServiceList()) ) {
            return serviceMarket.createMarket(user,param.getMarketName(), param.getBelongTeam(), param.getServiceList(), param.getRemark(),param.getServiceType());
        }
        return Result.fail(ErrorCode.RequestBodyIsEmpty);
    }

    //查看创建的market
    @GetMapping("/serviceMarket/mimonitor/searchMarket")
    public Result searchServiceMarket(HttpServletRequest request,Integer primaryId) {
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (primaryId != null && primaryId != 0) {
            return serviceMarket.searchMarket(user,primaryId);
        }
        return Result.fail(ErrorCode.ScrapeIdIsEmpty);
    }

    //更新创建的market
    @PostMapping("/serviceMarket/mimonitor/updateMarket")
    public Result updateServiceMarket(HttpServletRequest request,@RequestBody ServiceMarketQuery param) {
        log.info("ServiceMarket.updateServiceMarket : {} " , param);
        Integer id = param.getId();
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (id != null && id != 0 && StringUtils.isNotEmpty(param.getMarketName())) {
            return serviceMarket.updateMarket(user,id,param.getServiceList(),param.getMarketName(),param.getRemark(),param.getBelongTeam(),param.getServiceType());
        }
        return Result.fail(ErrorCode.invalidParamError);
    }

    //删除market
    @PostMapping("/serviceMarket/mimonitor/deleteMarket")
    public Result deleteServiceMarket(HttpServletRequest request,Integer primaryId) {
        log.info("ServiceMarket.deleteServiceMarket id:{} " ,primaryId);
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (primaryId != null && primaryId != 0) {
            return serviceMarket.deleteMarket(user,primaryId);
        }
        return Result.fail(ErrorCode.invalidParamError);
    }

    //查列表
    @GetMapping("/serviceMarket/mimonitor/searchMarketList")
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
        return serviceMarket.searchMarketList(user, pageSize,page,creator,marketName,serviceName);
    }

    //查看大盘，获取grafanaUrl
    @GetMapping("/serviceMarket/mimonitor/getServiceMarketGrafana")
    public Result getServiceMarketGrafana(HttpServletRequest request, Integer serviceType) {
        log.info("ServiceMarket.getServiceMarketGrafana type: {} " , serviceType);
        String user = checkUser(request);
        if (StringUtils.isEmpty(user)) {
            return Result.fail(ErrorCode.ThisUserNotHaveAuth);
        }
        if (serviceType == null || serviceType < 0) {
            return Result.fail(ErrorCode.RequestBodyIsEmpty);
        }
        String url = serviceMarket.getServiceMarketGrafana(serviceType);
        return Result.success(url);
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
