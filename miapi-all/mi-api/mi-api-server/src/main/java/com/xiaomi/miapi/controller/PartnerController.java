package com.xiaomi.miapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.vo.UserInfoVo;
import com.xiaomi.miapi.bo.InvitePartnerBo;
import com.xiaomi.miapi.service.PartnerService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with partner request
 */
@Controller
@RequestMapping("/Partner")
public class PartnerController {
    @Resource
    private PartnerService partnerService;

    @Autowired
    private LoginService loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @ResponseBody
    @RequestMapping("/getUserInfo")
    public Result<UserInfoVo> getUserInfo(HttpServletRequest request,
                                          HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getUserInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        UserInfoVo user = new UserInfoVo();
        user.setUserName(account.getUsername());
        return Result.success(user);
    }

    /**
     * batch invite others
     */
    @ResponseBody
    @RequestMapping(value = "/invitePartner", method = RequestMethod.POST)
    public Result<Boolean> invitePartner(HttpServletRequest request,
                                         HttpServletResponse response, @RequestBody InvitePartnerBo invitePartnerBo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.invitePartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return Result.success(true);
    }

    @ResponseBody
    @RequestMapping(value = "/inviteGroupPartner", method = RequestMethod.POST)
    public Result<Boolean> inviteGroupPartner(HttpServletRequest request,
                                              HttpServletResponse response, @RequestBody InvitePartnerBo invitePartnerBo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.invitePartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        //partnerService.inviteGroupPartner(account.getUsername(), invitePartnerBo.getGroupID(), invitePartnerBo.getInviterUserID(), invitePartnerBo.getUserIds(), invitePartnerBo.getRoleType());
        return Result.success(true);
    }

    @ResponseBody
    @RequestMapping(value = "/editPartnerRole", method = RequestMethod.POST)
    public Result<Boolean> editPartnerRole(HttpServletRequest request,
                                           HttpServletResponse response, Integer userID, Integer projectID, String roleTypes) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.editPartnerRole] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        JSONArray jsonArray = JSONArray.parseArray(roleTypes);
        List<Integer> roleTypesArr = new ArrayList<Integer>();
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (Object o : jsonArray) {
                roleTypesArr.add((Integer) o);
            }
        }
        // partnerService.editPartnerRole(account.getId().intValue(), account.getUsername(), userID, projectID, roleTypesArr);
        return Result.success(true);
    }

    @ResponseBody
    @RequestMapping(value = "/editGroupPartnerRole", method = RequestMethod.POST)
    public Result<Boolean> editGroupPartnerRole(HttpServletRequest request,
                                                HttpServletResponse response, Integer userID, Integer groupID, String roleTypes) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.editGroupPartnerRole] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        JSONArray jsonArray = JSONArray.parseArray(roleTypes);
        List<Integer> roleTypesArr = new ArrayList<Integer>();
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                roleTypesArr.add((Integer) iterator.next());
            }
        }
        // partnerService.editGroupPartnerRole(account.getId().intValue(), account.getUsername(), userID, groupID, roleTypesArr);
        return Result.success(true);
    }

    @ResponseBody
    @RequestMapping(value = "/removePartner", method = RequestMethod.POST)
    public Result<Boolean> removePartner(HttpServletRequest request,
                                         HttpServletResponse response, Integer userID, Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.removePartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        // partnerService.removePartner(account.getId().intValue(), account.getUsername(), userID, projectID);
        return Result.success(true);
    }

    @ResponseBody
    @RequestMapping(value = "/removeGroupPartner", method = RequestMethod.POST)
    public Result<Boolean> removeGroupPartner(HttpServletRequest request,
                                              HttpServletResponse response, Integer userID, Integer groupID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.removeGroupPartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        // partnerService.removeGroupPartner(account.getId().intValue(), account.getUsername(), userID, groupID);
        return Result.success(true);
    }


    /**
     * 获取项目协作人员列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getPartnerList", method = RequestMethod.POST)
    public Result<Map<String, List<Map<Integer, String>>>> getPartnerList(HttpServletRequest request,
                                                                          HttpServletResponse response,
                                                                          Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getPartnerList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<String, List<Map<Integer, String>>> resps = partnerService.getPartnerList(projectID);
        if (resps != null) {
            return Result.success(resps);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getGroupPartnerList", method = RequestMethod.POST)
    public Result<Map<String, List<Map<Integer, String>>>> getGroupPartnerList(HttpServletRequest request,
                                                                               HttpServletResponse response,
                                                                               Integer groupID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getGroupPartnerList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        Map<String, List<Map<Integer, String>>> resps = partnerService.getGroupPartnerList(groupID);
        if (resps != null) {
            return Result.success(resps);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getAllPartnerList", method = RequestMethod.GET)
    public Result<List<Map<Integer, String>>> getAllPartnerList(HttpServletRequest request,
                                                                HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getAllPartnerList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        return partnerService.getAllPartnerList();
    }
}
