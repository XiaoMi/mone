package com.xiaomi.mone.tpc.controller;

import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.serviceuser.ServiceUserService;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/service/user")
public class ServiceUserController {

    @Autowired
    private ServiceUserService serviceUserService;

    /**
     * 服务用户请求拦截
     * @param sysName
     * @param sysSign
     * @param userToken
     * @param reqTime
     * @return
     */
    @ArgCheck(needUser = false)
    @RequestMapping(value = "/token/parse")
    public ResultVo<AuthUserVo> parseToken(@RequestParam(name = "sysName") String sysName,
                                           @RequestParam(name = "sysSign") String sysSign,
                                           @RequestParam(name = "userToken", required = false) String userToken,
                                           @RequestParam(name = "dataSign", required = false) String dataSign,
                                           @RequestParam(name = "reqTime") Long reqTime) {
        return serviceUserService.parseToken(sysName, sysSign, userToken, reqTime, dataSign);
    }

    /**
     * 服务用户申请
     * @param sysName
     * @param sysSign
     * @param account
     * @param reqTime 毫秒
     * @param ttlMills 默认一天 毫秒
     * @return
     */
    @ArgCheck(needUser = false)
    @RequestMapping(value = "/apply")
    public ResultVo<AuthUserVo> apply(@RequestParam(name = "sysName") String sysName,
                                      @RequestParam(name = "sysSign") String sysSign,
                                      @RequestParam(name = "account") String account,
                                      @RequestParam(name = "reqTime") Long reqTime,
                                      @RequestParam(name = "ttlMills") Long ttlMills) {
        return serviceUserService.apply(sysName, sysSign, account, reqTime, ttlMills);
    }

}
