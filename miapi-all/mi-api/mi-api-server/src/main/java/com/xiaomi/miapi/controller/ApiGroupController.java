package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.pojo.ApiGroup;
import com.xiaomi.miapi.service.ApiGroupService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with api group request
 */
@Controller
@RequestMapping("/ApiGroup")
public class ApiGroupController {

    @Resource
    ApiGroupService apiGroupService;

    @Autowired
    private LoginService loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupController.class);

    @ResponseBody
    @RequestMapping(value = "/addGroup", method = RequestMethod.POST)
    public Result<Boolean> addApiGroup(HttpServletRequest request,
                                       HttpServletResponse response,
                                       ApiGroup apiGroup) throws IOException {
        if (apiGroup.getProjectID() == null || apiGroup.getProjectID() <= 0) {
            return Result.fail(CommonError.InvalidParamError);
        } else {
            SessionAccount account = loginService.getAccountFromSession(request);

            if (null == account) {
                LOGGER.warn("[ApiGroupController.addApiGroup] current user not have valid account info in session");
                response.sendError(401, "未登录或者无权限");
                return null;
            }
            boolean result = apiGroupService.addApiGroup(apiGroup,account.getUsername());
            if (result) {
                return Result.success(true);
            } else {
                return Result.fail(CommonError.UnknownError);
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteGroup", method = RequestMethod.POST)
    public Result<Boolean> deleteGroup(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Integer groupID, Integer projectID) throws IOException {
        if (groupID == null || groupID <= 0) {
            return Result.fail(CommonError.InvalidParamError);
        } else {
            SessionAccount account = loginService.getAccountFromSession(request);

            if (null == account) {
                LOGGER.warn("[ApiGroupController.deleteGroup] current user not have valid account info in session");
                response.sendError(401, "未登录或者无权限");
                return null;
            }
            return apiGroupService.deleteGroup(projectID, groupID, account.getUsername());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getGroupList", method = RequestMethod.POST)
    public Result<Map<String, Object>> getGroupList(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    Integer projectID) throws IOException {
        Map<String, Object> map = new HashMap<>();
        if (projectID == null || projectID <= 0) {
            return Result.fail(CommonError.InvalidParamError);
        } else {
            SessionAccount account = loginService.getAccountFromSession(request);

            if (null == account) {
                LOGGER.warn("[ApiGroupController.getGroupList] current user not have valid account info in session");
                response.sendError(401, "未登录或者无权限");
                return null;
            }
            List<Map<String, Object>> result = apiGroupService.getGroupList(projectID);
            map.put("groupList", result);
            return Result.success(map);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/editGroup", method = RequestMethod.POST)
    public Result<Boolean> editGroup(HttpServletRequest request,
                                     HttpServletResponse response,
                                     ApiGroup apiGroup) throws IOException {
        if (apiGroup.getGroupID() <= 0) {
            return Result.fail(CommonError.InvalidParamError);
        } else {
            SessionAccount account = loginService.getAccountFromSession(request);

            if (null == account) {
                LOGGER.warn("[ApiGroupController.editGroup] current user not have valid account info in session");
                response.sendError(401, "未登录或者无权限");
                return null;
            }
            boolean result = apiGroupService.editGroup(apiGroup,account.getUsername());
            if (result) {
                return Result.success(true);
            } else {
                return Result.fail(CommonError.UnknownError);
            }
        }
    }
}
