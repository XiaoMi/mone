package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.pojo.ApiHistoryRecord;
import com.xiaomi.miapi.service.ApiHistoryService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with api history request
 */
@Controller
@RequestMapping("/ApiHistory")
@Slf4j
public class ApiHistoryController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiHistoryService apiHistoryService;

    @ResponseBody
    @RequestMapping(value = "/getHistoryRecordList", method = RequestMethod.POST)
    public Result<Map<String,Object>> getHistoryRecordList(HttpServletRequest request,
                                                               HttpServletResponse response,
                                                               Integer pageNo, Integer pageSize,
                                                               Integer apiID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            log.warn("[ApiHistoryController.getHistoryRecordList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return apiHistoryService.getApiHistoryList(apiID,pageNo,pageSize);
    }

    @ResponseBody
    @RequestMapping(value = "/rollbackToHis", method = RequestMethod.POST)
    public Result<Boolean> rollbackToHis(HttpServletRequest request,
                                                           HttpServletResponse response,
                                                           Integer apiID,Integer targetHisID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            log.warn("[ApiHistoryController.rollbackToHis] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return apiHistoryService.rollbackToHis(apiID,targetHisID);
    }

    @ResponseBody
    @RequestMapping(value = "/getHistoryRecordById", method = RequestMethod.POST)
    public Result<ApiHistoryRecord> getHistoryRecordById(HttpServletRequest request,
                                                               HttpServletResponse response,
                                                               Integer recordID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            log.warn("[ApiHistoryController.getHistoryRecordById] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiHistoryService.getHistoryRecordById(recordID);
    }

    @ResponseBody
    @RequestMapping(value = "/compareWithOldVersion", method = RequestMethod.POST)
    public Result<Map<String,Object>> compareWithOldVersion(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Integer recordID, Integer apiID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            log.warn("[ApiHistoryController.compareWithOldVersion] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiHistoryService.compareWithOldVersion(recordID,apiID);
    }
}
