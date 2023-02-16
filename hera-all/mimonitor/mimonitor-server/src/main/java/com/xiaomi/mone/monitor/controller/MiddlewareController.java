package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.MiddleWareService;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.model.middleware.DbInstanceQuery;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gaoxihui
 * @date 2021/10/22 11:24 上午
 */
@Slf4j
@RestController
public class MiddlewareController {

    @Autowired
    MiddleWareService middleWareService;

    @ResponseBody
    @PostMapping("/middleware/list")
    public Result<PageData> middleWareList(HttpServletRequest request, @RequestBody DbInstanceQuery param){
        log.info("MiddlewareController middleWareList param : {}",param);

        AuthUserVo userInfo = UserUtil.getUser();
        if(userInfo == null){
            log.info("MiddlewareController.middleWareList request info error no user info found! param : {} ", param);
            return Result.fail(ErrorCode.unknownError);
        }

        String user = userInfo.genFullAccount();
        log.info("MiddlewareController.middleWareList param : {} ,user : {}", param,user);

        try {
            return middleWareService.queryMiddlewareInstance(user,param,param.getPage(),param.getPageSize());
        } catch (Exception e) {
            log.error("MiddlewareController.middleWareList Error" + e.getMessage(),e);
            return Result.fail(ErrorCode.unknownError);
        }
    }
}
