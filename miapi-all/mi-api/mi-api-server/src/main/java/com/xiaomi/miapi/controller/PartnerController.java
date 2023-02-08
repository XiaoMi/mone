package com.xiaomi.miapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.vo.UserInfoVo;
import com.xiaomi.miapi.common.bo.InvitePartnerBo;
import com.xiaomi.miapi.service.PartnerService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.service.impl.UserService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.dubbo.config.annotation.Reference;
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
 * 项目协作管理控制器
 */
@Controller
@RequestMapping("/Partner")
public class PartnerController {
    @Resource
    private PartnerService partnerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Reference(check = false, interfaceClass = BusProjectService.class, group = "${ref.hermes.service.group}", timeout = 4000)
    private BusProjectService busProjectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    /**
     * 获取个人信息
     *
     * @return
     */
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

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getAllDocumentList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        UserInfoVo user = userService.queryUserById(account.getId().intValue());
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * 批量邀请项目协作人员
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/invitePartner", method = RequestMethod.POST)
    public Result<Boolean> invitePartner(HttpServletRequest request,
                                         HttpServletResponse response, @RequestBody  InvitePartnerBo invitePartnerBo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.invitePartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getAllDocumentList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isBusProjectAdmin(Consts.PROJECT_NAME, invitePartnerBo.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
            return null;
        }
        int result = partnerService.invitePartner(account.getUsername(),invitePartnerBo.getProjectID(), invitePartnerBo.getInviterUserID(), invitePartnerBo.getUserIds(), invitePartnerBo.getRoleType());
        if (result > 0) {
            return Result.success(true);
        } else {
            return Result.fail(CommonError.UnknownError);
        }
    }

    /**
     * 批量邀请项目组协作人员
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/inviteGroupPartner", method = RequestMethod.POST)
    public Result<Boolean> inviteGroupPartner(HttpServletRequest request,
                                         HttpServletResponse response, @RequestBody  InvitePartnerBo invitePartnerBo) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.invitePartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getAllDocumentList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isBusProjectGroupAboveWork(Consts.PROJECT_NAME, invitePartnerBo.getGroupID().longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
            return null;
        }
        return partnerService.inviteGroupPartner(account.getUsername(),invitePartnerBo.getGroupID(), invitePartnerBo.getInviterUserID(), invitePartnerBo.getUserIds(), invitePartnerBo.getRoleType());
    }

    /**
     * 修改人员角色
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editPartnerRole", method = RequestMethod.POST)
    public Result<Boolean> editPartnerRole(HttpServletRequest request,
                                         HttpServletResponse response, Integer userID,Integer projectID,String roleTypes) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.editPartnerRole] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getAllDocumentList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isBusProjectAdmin(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
            return null;
        }
        JSONArray jsonArray = JSONArray.parseArray(roleTypes);
        List<Integer> roleTypesArr = new ArrayList<Integer>();
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                roleTypesArr.add((Integer) iterator.next());
            }
        }
        return partnerService.editPartnerRole(account.getId().intValue(), account.getUsername(), userID,projectID,roleTypesArr);
    }

    /**
     * 修改项目组人员角色
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editGroupPartnerRole", method = RequestMethod.POST)
    public Result<Boolean> editGroupPartnerRole(HttpServletRequest request,
                                           HttpServletResponse response, Integer userID,Integer groupID,String roleTypes) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.editGroupPartnerRole] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.editGroupPartnerRole] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isBusProjectGroupAboveWork(Consts.PROJECT_NAME, groupID.longValue(), account.getUsername())) {
            response.sendError(401, "需要项目组work以上权限执行此操作");
            return null;
        }
        JSONArray jsonArray = JSONArray.parseArray(roleTypes);
        List<Integer> roleTypesArr = new ArrayList<Integer>();
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                roleTypesArr.add((Integer) iterator.next());
            }
        }
        return partnerService.editGroupPartnerRole(account.getId().intValue(), account.getUsername(), userID,groupID,roleTypesArr);
    }

    /**
     * 移除项目成员
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removePartner", method = RequestMethod.POST)
    public Result<Boolean> removePartner(HttpServletRequest request,
                                           HttpServletResponse response, Integer userID,Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.removePartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.removePartner] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isBusProjectAdmin(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
            return null;
        }
        return partnerService.removePartner(account.getId().intValue(), account.getUsername(),userID,projectID);
    }

    /**
     * 移除项目成员
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeGroupPartner", method = RequestMethod.POST)
    public Result<Boolean> removeGroupPartner(HttpServletRequest request,
                                         HttpServletResponse response, Integer userID,Integer groupID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[PartnerController.removeGroupPartner] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.removeGroupPartner] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isBusProjectGroupAboveWork(Consts.PROJECT_NAME, groupID.longValue(), account.getUsername())) {
            response.sendError(401, "需要admin权限执行此操作");
            return null;
        }
        return partnerService.removeGroupPartner(account.getId().intValue(), account.getUsername(),userID,groupID);
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

    /**
     * 获取项目组协作人员列表
     *
     * @return
     */
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

    /**
     * 获取全部人员列表
     *
     * @return
     */
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
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getAllPartnerList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        return partnerService.getAllPartnerList();
    }
}
