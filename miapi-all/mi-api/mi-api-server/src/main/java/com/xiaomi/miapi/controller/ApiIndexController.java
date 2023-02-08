package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.common.pojo.IndexInfo;
import com.xiaomi.miapi.service.ApiIndexService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 接口索引分组控制器
 */
@Controller
@RequestMapping("/ApiIndex")
public class ApiIndexController {


    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiIndexService apiIndexService;

    @Reference(check = false, group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiIndexController.class);

    /**
     * 将接口拉入某个索引组
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addApiToIndex", method = RequestMethod.POST)
    public Result<Boolean> addApiToIndex(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String apiIDs,
                                         Integer indexID,
                                         Integer projectID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.addApiIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.addApiIndex] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        } else {
            return apiIndexService.batchGroupApis(apiIDs, indexID, account.getUsername());
        }
    }

    /**
     * 将接口从某个索引组移除
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeApiFromIndex", method = RequestMethod.POST)
    public Result<Boolean> removeApiFromIndex(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Integer apiID,
                                         Integer indexID,
                                         Integer projectID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.removeApiFromIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.removeApiFromIndex] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        } else {
            return apiIndexService.removeApiFromIndex(apiID, indexID, account.getUsername());
        }
    }

    /**
     * 创建索引组
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addIndex", method = RequestMethod.POST)
    public Result<Integer> addIndex(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String indexName,
                                    String description,
                                    Integer projectID,
                                    String indexDoc
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.addApiIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.addApiIndex] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        }
        return apiIndexService.addIndex(projectID, indexName, description,indexDoc, account.getUsername());
    }

    /**
     * 修改某个索引组名
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editIndex", method = RequestMethod.POST)
    public Result<Integer> editIndex(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Integer indexID,
                                     String description,
                                     String indexName,
                                     Integer projectID,
                                     String indexDoc
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.editIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[ProjectController.editIndex] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        }

        return apiIndexService.editIndex(indexName, indexID, description,indexDoc, account.getUsername());
    }

    /**
     * 删除索引组
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteIndex", method = RequestMethod.POST)
    public Result<Boolean> deleteIndex(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Integer indexID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.deleteIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ProjectController.deleteIndex] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        return apiIndexService.deleteIndex(indexID, account.getUsername());
    }

    /**
     * 获取索引组列表
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getIndexList", method = RequestMethod.POST)
    public Result<List<IndexInfo>> getIndexList(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Integer projectID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.getIndexList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        } else {
            return apiIndexService.getIndexList(projectID);
        }
    }

    /**
     * 具体集合API页面数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getIndexPageInfo", method = RequestMethod.POST)
    public Result<List<Map<String,Object>>> getIndexPageInfo(HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       Integer projectID, String indexIDs
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.getIndexPageInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "不是该项目成员");
            return null;
        }
        return apiIndexService.getIndexPageInfo(indexIDs);
    }
}
