//package com.xiaomi.youpin.gwdash.controller;
//
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.OptRecord;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.gwdash.service.OptRecordService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@RestController
//@Slf4j
//public class OptRecordController {
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    OptRecordService optRecordService;
//
//    /**
//     * 目前仅支持按ResourceUrl查询
//     * @param param
//     * @param request
//     * @param response
//     * @return
//     * @throws IOException
//     */
//    @RequestMapping(value = "/api/optRecord/list", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<OptRecordListResult> getOptRecordList(@RequestBody OptRecordParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        log.info("[OptRecordController.getOptRecordList] param: {}", param);
//        if (param == null || StringUtils.isEmpty(param.getResourceUrl())) {
//            return new Result<>(CommonError.InvalidPageParamError.getCode(), CommonError.InvalidPageParamError.getMessage());
//        }
//        SessionAccount account = loginService.getAccountFromSession(request);
//        log.info("[OptRecordController.getOptRecordList] account: {}", account);
//        if (null == account) {
//            log.warn("[OptRecordController.getOptRecordList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        if (param.getPageNo() <= 0) {
//            param.setPageNo(1);
//        }
//        if (param.getPageSize() <= 0) {
//            param.setPageSize(Consts.DEFAULT_PAGE_SIZE);
//        }
//        OptRecordListResult ret = optRecordService.getOptRecordList(param);
//        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
//    }
//
//    @RequestMapping(value = "/api/optRecord/detail/info", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<OptRecord> getOptRecordDetail(@RequestBody OptRecord param) {
//        log.info("[OptRecordController.getOptRecordDetail] param: {}", param);
//        if (param == null || param.getId()<1) {
//            return new Result<>(CommonError.InvalidPageParamError.getCode(), CommonError.InvalidPageParamError.getMessage());
//        }
//        OptRecord ret = optRecordService.getOptRecordDetail(param);
//        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
//    }
//}
