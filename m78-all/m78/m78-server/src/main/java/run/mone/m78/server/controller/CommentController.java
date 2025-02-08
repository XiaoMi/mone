/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.server.controller;

import com.mybatisflex.core.paginate.Page;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78BotComment;
import run.mone.m78.service.dao.entity.M78BotCommentStatistics;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.comment.CommentService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/insertOrUpdate")
    public Result insertOrUpdate(M78BotComment comment, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String user = account.getUsername();
        Date date = new Date();
        if (comment.getId() == null) {
            comment.setCreateBy(user);
            comment.setCreateTime(date);
            comment.setUpdateBy(user);
            comment.setUpdateTime(date);
        } else {
            comment.setUpdateBy(user);
            comment.setUpdateTime(date);
        }
        return commentService.insertOrUpdate(comment);
    }

    @GetMapping("/page")
    public Result<Page<M78BotComment>> page(Long itemId, int type, String commentType, Integer pageNum, Integer pageSize, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String user = account.getUsername();
        return commentService.getByBotId(itemId, type, user, commentType, pageSize, pageNum);
    }

    @GetMapping("/detail")
    public Result<M78BotComment> detail(Long itemId, int type, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String user = account.getUsername();
        return commentService.getByBotIdAndUser(itemId, type, user);
    }

    @GetMapping("/statistics")
    public Result<M78BotCommentStatistics> statistics(Long itemId, int type, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return commentService.statistics(itemId, type);
    }
}
