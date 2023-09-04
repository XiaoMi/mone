package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.model.UserInfo;
import com.xiaomi.mone.monitor.service.user.LocalUser;
import com.xiaomi.mone.monitor.service.user.UseDetailInfo;
import com.xiaomi.mone.monitor.service.user.UserConfigService;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/9/9 10:10 上午
 */
@Slf4j
@RestController
public class UserController {


    @Autowired
    UserConfigService userConfigService;

    @ResponseBody
    @RequestMapping("/user/info")
    public Result userInfo(HttpServletRequest request){

        try {
            AuthUserVo userVo = UserUtil.getUser();
            if(userVo == null){
                log.info("UserController.userInfo request info error no user info found!");
                return Result.fail(ErrorCode.unknownError);
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setDepartmentName(userVo.getDepartmentName());
            userInfo.setDisplayName(userVo.getName());
            userInfo.setEmail(userVo.getEmail());
            userInfo.setName(userVo.getName());
            userInfo.setUser(userVo.genFullAccount());
            userInfo.setAvatar(userVo.getAvatarUrl());
            userInfo.setIsAdmin(userConfigService.isAdmin(userVo.genFullAccount()) ? true : false);

            Map<Integer, UseDetailInfo.DeptDescr> depts = LocalUser.getDepts();
            UseDetailInfo.DeptDescr dept = depts == null ? null : depts.get(1);
            UseDetailInfo.DeptDescr dept2 = depts == null ? null : depts.get(2);
            userInfo.setFirstDepartment(dept == null ? null : dept.getDeptName());
            userInfo.setSecondDepartment(dept2 == null ? null : dept2.getDeptName());

            log.info("UserController.userInfo userInfo :{}",userInfo);
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("UserController.addAlarmTemplate",e);
            return Result.fail(ErrorCode.unknownError);
        }
    }

}
