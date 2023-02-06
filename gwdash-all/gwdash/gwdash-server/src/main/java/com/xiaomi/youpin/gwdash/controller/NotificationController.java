/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.controller;


import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.tesla.im.bo.ChangeLog;
import com.xiaomi.youpin.tesla.im.service.ChangeLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tsingfu
 */
@Slf4j
@RestController
public class NotificationController {


    @Reference(check = false, interfaceClass = ChangeLogService.class, group = "${dubbo.group}")
    private ChangeLogService changeLogService;

    @RequestMapping(value = "/open/getNotification", method = RequestMethod.GET)
    public Result getNotification(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("[NotificationController.getNotification]");

        try{
            Result<ChangeLog> res = changeLogService.list("gwdash","7");
            log.info("[NotificationController.getNotification] ChangeLog res:{}",res);
            return res;
        }catch (Exception e){
            log.info("[NotificationController.getNotification] ChangeLog e:{}",e);
            return Result.success(null);
        }

    }
}
