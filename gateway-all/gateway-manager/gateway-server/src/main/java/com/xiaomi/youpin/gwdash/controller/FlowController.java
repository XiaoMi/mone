///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
package com.xiaomi.youpin.gwdash.controller;


import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.Approval;
import com.xiaomi.youpin.gwdash.service.FlowService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

import static com.xiaomi.youpin.gwdash.common.Consts.ROLE_ADMIN;


/**
 * @author goodjava@qq.com
 */
@RestController
@Slf4j
public class FlowController {


    @Autowired
    private FlowService flowService;

    @Autowired
    private LoginService loginService;


    /**
     * 创建批准的流程单(发起流程)
     * 申请获取token
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/flow/create", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> create(HttpServletRequest request, @RequestBody Approval param) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        //申请者
        param.setApplicantId(account.getId().intValue());
        param.setCtime(now);
        param.setUtime(now);
        param.setApplicantName(account.getName());
        //key 就是申请者id
        param.setKey(UUID.randomUUID().toString() + ":" + account.getId());
        return flowService.create(param, account.getId());
    }

    /**
     * 结束流程 (3=成功  4=失败)
     * FlowStatus
     *
     * @param request
     * @param param
     * @return
     */
    @RequestMapping(value = "/api/flow/over", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> over(HttpServletRequest request, @RequestBody Approval param) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        //申请者
        param.setApplicantId(account.getId().intValue());
        param.setCtime(now);
        param.setUtime(now);
        int code = flowService.over(param);
        if (code == 100) {
            return new Result<>(code,"您无权归还权限",false);
        }
        return Result.success(true);
    }


    @RequestMapping(value = "/api/flow/agree", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> agree(HttpServletRequest request, @RequestBody Approval param) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account.getRole() != ROLE_ADMIN) {
            log.warn("[FlowController.agree] current user is not admin");
            return new Result<>(401, "没有权限审核", false);
        }
        flowService.agree(param.getId());
        return Result.success(true);
    }


    @RequestMapping(value = "/api/flow/refuse", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> refuse(HttpServletRequest request, @RequestBody Approval param) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account.getRole() != ROLE_ADMIN) {
            log.warn("[FlowController.refuse] current user is not admin");
            return new Result<>(401, "没有权限审核", false);
        }
        flowService.refuse(param.getId());
        return Result.success(true);
    }


    @RequestMapping(value = "/api/flow/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Map<String, Object>> list(HttpServletRequest request, @RequestBody Approval param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        Map<String, Object> data = flowService.list(param.getStatus(), param.getPage(), param.getPageSize());
        return Result.success(data);
    }


    @RequestMapping(value = "/api/flow/detail", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Approval> detail(HttpServletRequest request, @RequestBody Approval param) {
        Approval data = flowService.detail(param.getKey());
        return Result.success(data);
    }

}
