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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gwdash.bo.ReviewBo;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.LockUtils;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.common.ReviewStatusEnum;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.ProjectRole;
import com.xiaomi.youpin.gwdash.dao.model.Review;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.FeiShuService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import com.xiaomi.youpin.gwdash.service.ReviewService;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 项目审核
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private FeiShuService feiShuService;

    @Value("${review.success.url}")
    private String reviewSuccessUrl;

    @Autowired
    private ProjectService projectService;

    @Value("${hermes.project.name}")
    private String hermesProjectName;

    @Autowired
    private LockUtils lock;

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}")
    private AccountService accountService;

    @RequestMapping(value = "/test/review/users", method = RequestMethod.GET)
    public Result getUsers(@RequestParam Long projectId) {
        List<Account> testers = accountService.queryUserByGroupName("测试审核组");
        List<ProjectRole> projectTesters = projectService.getTesters(projectId);
        if (CollectionUtils.isEmpty(projectTesters)) {
            return Result.success(testers);
        }
        List<Integer> accountIds;
        if (CollectionUtils.isEmpty(testers)) {
            accountIds=projectTesters.stream().map(e->e.getAccountId()).collect(Collectors.toList());
        }else{
            Set<Long> testIds = testers.stream().map(e -> e.getId()).collect(Collectors.toSet());
            accountIds=projectTesters.stream().filter(e->!testIds.contains(Long.valueOf(e.getAccountId()))).map(e->e.getAccountId()).collect(Collectors.toList());
        }
        for (Integer accountId : accountIds) {
            Account account = accountService.queryUserById(accountId);
            if (account == null) {
                log.error("account id error: {}", accountId);
                continue;
            }
            testers.add(account);
        }
        return Result.success(testers);
    }

    @RequestMapping(value = "/test/review/initiate", method = RequestMethod.POST)
    public Result initiate(HttpServletRequest request, @RequestBody ReviewBo reviewbo) {
        Result<Project> checkResult = reviewService.checkParms(reviewbo);
        if (checkResult.getCode() != CommonError.Success.code) {
            return checkResult;
        }
        boolean tryLock = lock.tryLock(reviewbo.getCommitId());
        if (!tryLock) {
            return Result.success("请勿重复提交");
        }

        try {
            Review passedReview = reviewService.getReview(reviewbo.getProjectId(), reviewbo.getCommitId(), ReviewStatusEnum.PASS.getCode());
            if (null != passedReview) {
                log.error("review has passed, projectId: {}, commitId: {}", reviewbo.getProjectId(), reviewbo.getCommitId());
                return Result.success("审核已通过");
            }
            Review underReview = reviewService.getReview(reviewbo.getProjectId(), reviewbo.getCommitId(), ReviewStatusEnum.UNDER_REVIEW.getCode());
            String gitUrl = checkResult.getData().getGitAddress() + "/-/commit/" + reviewbo.getCommitId();
            SessionAccount account = loginService.getAccountFromSession(request);
            if (underReview == null) {
                Review review = new Review();
                BeanUtils.copyProperties(reviewbo, review);
                review.setProjectName(checkResult.getData().getName());
                review.setSubmitter(account.getUsername());
                review.setReviewer(new Gson().toJson(reviewbo.getReviewers()));
                review.setStatus(ReviewStatusEnum.UNDER_REVIEW.getCode());
                review.setUrl(gitUrl);
                reviewService.createReview(review);
                sendNotifyMsgTo(reviewbo.getReviewers(), gitUrl);
            } else {
                underReview.setReviewer(merge(underReview.getReviewer(), reviewbo.getReviewers()));
                underReview.setSubmitter(account.getUsername());
                boolean success = reviewService.updateReview(underReview);
                if (success) {
                    sendNotifyMsgTo(reviewbo.getReviewers(), gitUrl);
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }finally {
            lock.unLock(reviewbo.getCommitId());
        }
        return Result.success("测试审核已提交");
    }

    /**
     * 发消息给审批人
     */
    private void sendNotifyMsgTo(List<String> reviewers,String url) {
        reviewers.forEach(it->feiShuService.sendMsg2Person(it,"您有新的测试审核: \r\n"+url+"\r\n"+reviewSuccessUrl));
    }

    /**
     * 合并新老数据
     */
    private String merge(String reviewer, List<String> newReviewer) {
        List<String> reviewers = new Gson().fromJson(reviewer, new TypeToken<List<String>>() {
        }.getType());
        newReviewer.stream().forEach(it->{
            if (!reviewers.contains(it)) {
                reviewers.add(it);
            }
        });
        return new Gson().toJson(reviewers);
    }


    /**
     * 项目审核，审核列表
     * @param submitter 提交人
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/test/review/page", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Map<String, Object>> reviewPage(HttpServletRequest request, String submitter, Integer page, Integer pageSize) {
        log.info("ReviewController /test/review/page submitter:{}, page:{}, pageSize:{}", submitter, page, pageSize);

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }

        SessionAccount account = loginService.getAccountFromSession(request);
        String username = null;
        if (account != null) {
            username = account.getUsername();
        }
        log.info("ReviewController /test/review/operation username:{}", username);
        Result<Map<String, Object>> result = reviewService.reviewPage(submitter, page, pageSize, username);
        log.info("ReviewController /test/review/page result:{}", result);
        return result;
    }

    /**
     * 项目审核，操作日志
     * @param relevanter 相关人
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/test/review/log", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Map<String, Object>> reviewLog(String relevanter, Integer status, Integer page, Integer pageSize) {
        log.info("ReviewController /test/review/log relevanter:{}, status:{}, page:{}, pageSize:{}", relevanter, status, page, pageSize);

        if (status == null) {
            status = -1;
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        Result<Map<String, Object>> result = reviewService.reviewLog(relevanter, status, page, pageSize);
        log.info("ReviewController /test/review/log result:{}", result);
        return result;
    }

    /**
     * 项目审核，通过、驳回 操作
     * @param id
     * @param status 通过传1、驳回传2
     * @return
     */
    @RequestMapping(value = "/test/review/operation", method = {RequestMethod.POST, RequestMethod.GET})
    public Result<Boolean> reviewOperation(HttpServletRequest request, String id, Integer status, String remarks) {
        log.info("ReviewController /test/review/operation id:{}, status:{}, remarks:{}", id, status, remarks);
        if (StringUtils.isBlank(id)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (status == null) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (status == ReviewStatusEnum.REFUSE.getCode()) {
            if (StringUtils.isBlank(remarks)) {
                // 如果驳回状态，必须填写原因
                return Result.fail(CommonError.InvalidParamError);
            }
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        String username = null;
        if (account != null) {
            username = account.getUsername();
        }
        log.info("ReviewController /test/review/operation username:{}", username);
        Result<Boolean> result = reviewService.reviewOperation(id, status, remarks, username);
        log.info("ReviewController /test/review/operation result:{}", result);
        return result;
    }


}
