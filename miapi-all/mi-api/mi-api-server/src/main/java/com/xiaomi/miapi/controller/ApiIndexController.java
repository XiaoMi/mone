package com.xiaomi.miapi.controller;

import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.pojo.IndexInfo;
import com.xiaomi.miapi.service.ApiIndexService;
import com.xiaomi.miapi.common.Result;
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
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with api index request
 */
@Controller
@RequestMapping("/ApiIndex")
public class ApiIndexController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiIndexService apiIndexService;


    private static final Logger LOGGER = LoggerFactory.getLogger(ApiIndexController.class);

    @ResponseBody
    @RequestMapping(value = "/addApiToIndex", method = RequestMethod.POST)
    public Result<Boolean> addApiToIndex(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String apiIDs,
                                         Integer indexID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.addApiIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiIndexService.batchGroupApis(apiIDs, indexID, account.getUsername());
    }

    @ResponseBody
    @RequestMapping(value = "/removeApiFromIndex", method = RequestMethod.POST)
    public Result<Boolean> removeApiFromIndex(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Integer apiID,
                                              Integer indexID
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.removeApiFromIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiIndexService.removeApiFromIndex(apiID, indexID, account.getUsername());
    }

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
        return apiIndexService.addIndex(projectID, indexName, description, indexDoc, account.getUsername());
    }

    @ResponseBody
    @RequestMapping(value = "/editIndex", method = RequestMethod.POST)
    public Result<Integer> editIndex(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Integer indexID,
                                     String description,
                                     String indexName,
                                     String indexDoc
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.editIndex] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiIndexService.editIndex(indexName, indexID, description, indexDoc, account.getUsername());
    }

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

        return apiIndexService.deleteIndex(indexID, account.getUsername());
    }

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
        return apiIndexService.getIndexList(projectID);
    }

    @ResponseBody
    @RequestMapping(value = "/getIndexPageInfo", method = RequestMethod.POST)
    public Result<List<Map<String, Object>>> getIndexPageInfo(HttpServletRequest request,
                                                              HttpServletResponse response, String indexIDs
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiIndexController.getIndexPageInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return apiIndexService.getIndexPageInfo(indexIDs);
    }
}
