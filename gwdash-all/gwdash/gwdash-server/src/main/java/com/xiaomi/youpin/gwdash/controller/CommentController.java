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


import com.alibaba.nacos.client.utils.StringUtils;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.HttpUtils;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.GwCommentService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.UploadService;
import com.xiaomi.youpin.tesla.im.bo.CommentQueryVo;
import com.xiaomi.youpin.tesla.im.bo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@RestController
@Slf4j
public class CommentController {

    @Autowired
    private GwCommentService commentService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private LoginService loginService;


    @RequestMapping(value = "/api/comment/create", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> create(HttpServletRequest request, @RequestBody CommentVo commentVo) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        commentVo.setAuthorId(account.getId().intValue());
        commentVo.setAuthorName(account.getUsername());
        commentVo.setCtime(now);
        commentVo.setUtime(now);
        Result<Boolean> res = commentService.comment(commentVo);
        if (res.getData()) {
            //如果是回复别人,需要邮件提醒
            if (commentVo.getType() == 4) {
                sendMail(commentVo.getParentId(), commentVo.getContent(),account.getEmail());
            }
        }
        return Result.success(true);
    }

    private void sendMail(int parentId, String content,String email) {
        if (StringUtils.isEmpty(email)) {
            return;
        }
        log.info("send mail {}", parentId);
        try {
            CommentVo fetchVo = new CommentVo();
            fetchVo.setId(parentId);
            Result<CommentVo> data = commentService.fetch(fetchVo);
            CommentVo cv = data.getData();
            int authorId = cv.getAuthorId();
            log.info("parentId:{} email:{}", parentId, email);
            HttpUtils.sendEmail("http://support.d.xiaomi.net/mail/send&?mailType=OTHER", email, cv.getTitle(), content);
        } catch (Exception ex) {
            log.warn("send mail error:{}", ex.getMessage());
        }
    }


    @RequestMapping(value = "/api/comment/reply", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> reply(HttpServletRequest request, @RequestBody CommentVo commentVo) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        commentVo.setReplyUserId(account.getId().intValue());
        commentVo.setReplyUserName(account.getUsername());
        commentVo.setCtime(now);
        commentVo.setUtime(now);
        commentService.reply(commentVo);
        return Result.success(true);
    }


    @RequestMapping(value = "/api/comment/del", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> del(HttpServletRequest request, @RequestBody CommentVo commentVo) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        commentVo.setAuthorId(account.getId().intValue());
        commentVo.setUtime(now);
        commentService.delete(commentVo);
        return Result.success(true);
    }


    @RequestMapping(value = "/api/comment/modify", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Boolean> modify(HttpServletRequest request, @RequestBody CommentVo commentVo) {
        long now = System.currentTimeMillis();
        SessionAccount account = loginService.getAccountFromSession(request);
        commentVo.setUpdaterId(account.getId().intValue());
        commentVo.setUpdaterName(account.getUsername());
        commentVo.setUtime(now);
        commentService.modify(commentVo);
        return Result.success(true);
    }

    @RequestMapping(value = "/api/comment/query", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<List<CommentVo>> query(HttpServletRequest request, @RequestBody CommentVo commentVo) {
        RpcContext.getContext().clearAttachments();
        return commentService.query(commentVo);
    }

    @RequestMapping(value = "/api/comment/fuzzy/query", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Map<String, Object>> fuzzyQuery(HttpServletRequest request, @RequestBody CommentQueryVo commentQueryVo) {
        RpcContext.getContext().clearAttachments();
        return commentService.fuzzyQuery(commentQueryVo);
    }

    @RequestMapping(value = "/api/comment/fetch", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<CommentVo> fetch(HttpServletRequest request, @RequestBody CommentVo commentVo) {
        return commentService.fetch(commentVo);
    }

    @RequestMapping(value = "/api/comment/uplaod/image", method = RequestMethod.POST)
    public Result<String> uploadImage(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return uploadService.uploadFile(file);
    }
}
