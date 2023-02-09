package com.xiaomi.miapi.controller;

import com.alibaba.fastjson.JSON;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.common.pojo.Document;
import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.service.DocumentService;
import com.xiaomi.miapi.service.impl.LoginService;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.youpin.hermes.service.BusProjectService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 项目文档控制器
 */
@Controller
@RequestMapping("/Document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private LoginService loginService;

    @Reference(check = false,group = "${ref.hermes.service.group}")
    private BusProjectService busProjectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    /**
     * 添加文档
     *
     * @param document
     * @return
     */
    @ResponseBody
    @RequestMapping("/addDocument")
    public Result<Boolean> addDocument(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Document document) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[DocumentController.addDocument] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.addDocument] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, document.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "您没有权限执行此操作");
            return null;
        }

       if (document.getContentType() == null
                || (document.getContentType() != 1 && document.getContentType() != 0)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        document.setUserID(account.getId().intValue());
        return documentService.addDocument(document, account.getUsername());
    }

    /**
     * 编辑文档
     *
     * @param document
     * @return
     */
    @ResponseBody
    @RequestMapping("/editDocument")
    public Result<Boolean> editDocument(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Document document) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[DocumentController.editDocument] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.editDocument] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, document.getProjectID().longValue(), account.getUsername())) {
            response.sendError(401, "您没有权限执行此操作");
            return null;
        }

        if (document.getDocumentID() == null || !String.valueOf(document.getDocumentID()).matches("^[0-9]{1,11}$")) {
            return Result.fail(CommonError.InvalidParamError);
        }
        document.setUserID(account.getId().intValue());
        return this.documentService.editDocument(document,account.getUsername());
    }

    /**
     * 获取项目下所有文档列表
     *
     * @param projectID
     * @return
     */
    @ResponseBody
    @RequestMapping("/getAllDocumentList")
    public Result<List<DocumentDTO>> getAllDocumentList(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        @RequestParam("projectID") Integer projectID) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[DocumentController.getAllDocumentList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getAllDocumentList] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "您不是该项目成员");
            return null;
        }
        return this.documentService.getAllDocumentList(projectID);
    }

    /**
     * 搜索文档
     *
     * @param projectID
     * @param tips
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/searchDocument", method = RequestMethod.POST)
    public Result<List<DocumentDTO>> searchDocument(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    Integer projectID,
                                                    String tips,Integer type) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[DocumentController.searchDocument] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.searchDocument] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isMember(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "您不是该项目成员");
            return null;
        }

        if (tips.length() < 1 || tips.length() > 255) {
            return Result.fail(CommonError.InvalidParamError);
        }
        return documentService.searchDocument(projectID, tips,type);
    }

    /**
     * 获取文档详情
     *
     * @param documentID
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDocument")
    public Result<Map<String, Object>> getDocument(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @RequestParam("documentID") Integer documentID) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[DocumentController.getDocument] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.getDocument] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }
        return documentService.getDocument(documentID);
    }

    /**
     * 批量删除文档
     *
     * @param projectID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteDocuments", method = RequestMethod.POST)
    public Result<Boolean> deleteDocuments(HttpServletRequest request,
                                           HttpServletResponse response, String documentIDs,
                                           Integer projectID) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (null == account) {
            LOGGER.warn("[DocumentController.deleteDocuments] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        if (account.getRole() != Consts.ROLE_ADMIN && account.getRole() != Consts.ROLE_WORK) {
            LOGGER.warn("[DocumentController.deleteDocuments] not authorized to create project");
            return Result.fail(CommonError.UnAuthorized);
        }

        if (!busProjectService.isAboveWork(Consts.PROJECT_NAME, projectID.longValue(), account.getUsername())) {
            response.sendError(401, "您不是该项目成员");
            return null;
        }

        List<Integer> documentIDlist = new ArrayList<Integer>();

        List<String> parseArray = JSON.parseArray(documentIDs, String.class);

        for (String id : parseArray) {
            if (!id.matches("^[0-9]{1,11}$")) {
                return Result.fail(CommonError.InvalidParamError);
            } else {
                documentIDlist.add(Integer.parseInt(id));
            }
        }
        return this.documentService.deleteBatchDocument(documentIDlist, projectID, account.getId().intValue(), account.getUsername());
    }
}
