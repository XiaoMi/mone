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

package com.xiaomi.youpin.gwdash.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gwdash.bo.ReviewBo;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.common.ReviewStatusEnum;
import com.xiaomi.youpin.gwdash.dao.model.Project;
import com.xiaomi.youpin.gwdash.dao.model.Review;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.hermes.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.NotNull;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ReviewService {

    @Autowired
    private Dao dao;

    @Autowired
    private FeiShuService feiShuService;

    @Value("${review.success.url}")
    private String reviewSuccessUrl;

    @Value("${review.fail.url}")
    private String reviewFailUrl;

    @Autowired
    private ProjectService projectService;

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}")
    private AccountService accountService;

    public void createReview(Review review) {
        long now = System.currentTimeMillis();
        review.setCtime(now);
        review.setUtime(now);
        review.setVersion(0);
        dao.insert(review);
    }

    public boolean updateReview(Review review) {
        review.setUtime(System.currentTimeMillis());
        int count = dao.updateWithVersion(review);
        return count > 0;
    }

    public Review getReview(long projectId, String commitId,int status) {
        Cnd condition = Cnd.where("project_id", "=", projectId).and("commit_id", "=", commitId).and("status", "=", status);
        List<Review> reviews = dao.query(Review.class, condition);
        if (CollectionUtils.isEmpty(reviews)) {
            return null;
        }
        return reviews.get(0);
    }

    public ReviewBo getReviewStatus(long projectId, String commitId) {
        ReviewBo reviewBo = new ReviewBo();
        reviewBo.setCommitId(commitId);
        Cnd condition = Cnd.where("project_id", "=", projectId).and("commit_id", "=", commitId);
        List<Review> reviews = dao.query(Review.class, condition);
        if (CollectionUtils.isEmpty(reviews)) {
            reviewBo.setStatus(ReviewStatusEnum.TO_BE_REVIEW.getCode());
            return reviewBo;
        }

        //如果已通过，则显示审批者uid
        Optional<Review> passedReview = reviews.stream().filter(e -> e.getStatus() == ReviewStatusEnum.PASS.getCode()).findAny();
        if (passedReview.isPresent()) {
            return wrapReviewBo(reviewBo, passedReview.get(), ReviewStatusEnum.PASS.getCode());
        }

        Optional<Review> underReview = reviews.stream().filter(e -> e.getStatus() == ReviewStatusEnum.UNDER_REVIEW.getCode()).findAny();
        if (underReview.isPresent()) {
            return wrapReviewBo(reviewBo, underReview.get(), ReviewStatusEnum.UNDER_REVIEW.getCode());
        }

        //如果驳回，显示驳回原因
        Optional<Review> refusedReview = reviews.stream().filter(e -> e.getStatus() == ReviewStatusEnum.REFUSE.getCode()).findAny();
        if (refusedReview.isPresent()) {
            return wrapReviewBo(reviewBo, refusedReview.get(), ReviewStatusEnum.REFUSE.getCode());
        }

        reviewBo.setStatus(ReviewStatusEnum.TO_BE_REVIEW.getCode());
        return reviewBo;
    }

    @NotNull
    private ReviewBo wrapReviewBo(ReviewBo reviewBo, Review review, int status) {
        reviewBo.setStatus(status);
        reviewBo.setRemarks(review.getRemarks());
        reviewBo.setOperator(review.getOperator());
        reviewBo.setReviewers(getReviewers(review.getReviewer()));
        return reviewBo;
    }

    private List<String> getReviewers(String reviewer) {
        List<String> reviewers=null;
        try {
            reviewers = new Gson().fromJson(reviewer, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception e) {
            log.error("reviewer info error");
        }
        return reviewers;
    }

    public Result<Map<String, Object>> reviewPage(String submitter, Integer page, Integer pageSize, String username) {
        try {
            // 审核列表 submitter 提交人
            Cnd cnd = null;
            if (StringUtils.isNotBlank(submitter)) {
                // 有提交人
                cnd = (Cnd) Cnd.where("submitter", "like", "%"+submitter+"%").and("reviewer", "like", "%"+username+"%").and("status", "=", ReviewStatusEnum.UNDER_REVIEW.getCode()).orderBy("ctime","desc");
            }  else {
                // 没有提交人
                cnd = (Cnd) Cnd.where("status", "=", ReviewStatusEnum.UNDER_REVIEW.getCode()).and("reviewer", "like", "%"+username+"%").orderBy("ctime","desc");
            }

            Map<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("total", dao.count(Review.class, cnd));
            result.put("list", dao.query(Review.class, cnd, new Pager(page, pageSize)));
            return Result.success(result);
        } catch (Exception e) {
            log.error("ReviewService reviewPage error", e);
        }
        return Result.fail(CommonError.UnknownError);
    }

    public Result<Map<String, Object>> reviewLog(String relevanter, Integer status, Integer page, Integer pageSize) {
        try {
            // 操作日志 relevanter 相关人
            Cnd cnd = null;
            if (StringUtils.isBlank(relevanter)) {
                // 没有相关人
                if (status == -1) {
                    // 状态全部
                    cnd = (Cnd) Cnd.where("status", ">", ReviewStatusEnum.UNDER_REVIEW.getCode()).orderBy("operate_time", "desc");
                } else {
                    cnd = (Cnd) Cnd.where("status", "=", status).orderBy("operate_time", "desc");
                }

            } else  {
                // 有相关人
                if (status == -1) {
                    // 状态全部
                    cnd = (Cnd) Cnd.where("status", ">", ReviewStatusEnum.UNDER_REVIEW.getCode()).and(Cnd.exps("submitter", "like", "%"+relevanter+"%").or("operator", "like", "%"+relevanter+"%")).orderBy("operate_time", "desc");
                } else {
                    cnd = (Cnd) Cnd.where("status", "=", status).and(Cnd.exps("submitter", "like", "%"+relevanter+"%").or("operator", "like", "%"+relevanter+"%")).orderBy("operate_time", "desc");
                }

            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("total", dao.count(Review.class, cnd));
            result.put("list", dao.query(Review.class, cnd, new Pager(page, pageSize)));
            return Result.success(result);
        } catch (Exception e) {
            log.error("ReviewService reviewLog error", e);
        }
        return Result.fail(CommonError.UnknownError);
    }

    public Result<Boolean> reviewOperation(String id, Integer status, String remarks, String username) {
        try {
            Review review = dao.fetch(Review.class, Cnd.where("id", "=", id));
            if (review != null) {
                if (review.getStatus() == ReviewStatusEnum.UNDER_REVIEW.getCode()) {
                    // 项目未审核
                    long time = System.currentTimeMillis();
                    if (StringUtils.isNotBlank(remarks)) {
                        dao.update(Review.class, Chain.make("status", status).add("utime", time).add("operate_time", time).add("operator", username).add("remarks", remarks), Cnd.where("id", "=", id));
                        this.sendNotifyMsgToSubmitterFail(review.getSubmitter());// 通过飞书发送失败消息
                    } else {
                        dao.update(Review.class, Chain.make("status", status).add("utime", time).add("operate_time", time).add("operator", username), Cnd.where("id", "=", id));
                        this.sendNotifyMsgToSubmitterSuccess(review.getSubmitter());// 通过飞书发送成功消息
                    }


                    return Result.success(true);
                } else if (review.getStatus() == ReviewStatusEnum.PASS.getCode()||review.getStatus() == ReviewStatusEnum.EMERGENCY_RELEASE.getCode()) {
                    // 项目已经审核完毕 前端code 必须是0才能解析
                    Result result = new Result(CommonError.Success.code, CommonError.ReviewPassError.message, review.getOperator());
                    return result;
                } else if (review.getStatus() == ReviewStatusEnum.REFUSE.getCode()) {
                    // 项目驳回 前端code 必须是0才能解析
                    Result result = new Result(CommonError.Success.code, CommonError.ReviewPassError.message, review.getOperator());
                    return result;
                }

                return Result.success(false);
            } else {
                return Result.success(false);
            }
        } catch (Exception e) {
            log.error("ReviewService reviewOperation error", e);
            return Result.success(false);
        }
    }

    private void sendNotifyMsgToSubmitterSuccess(String submitter) {
        feiShuService.sendMsg2Person(submitter, "您提交的测试审核已经通过: " + reviewSuccessUrl);
    }

    private void sendNotifyMsgToSubmitterFail(String submitter) {
        feiShuService.sendMsg2Person(submitter, "您提交的测试审核被驳回: " + reviewFailUrl);
    }

    /**
     * 紧急发布记录入库
     */
    public void saveEmergencyLog(long projectId, String commitId, String username) {
        Result<Project> project = projectService.getProjectById(projectId);
        if (project.getCode() != CommonError.Success.code) {
            log.error("project id error: }", projectId);
            return;
        }

        Review underReveiwReview = getReview(projectId, commitId, ReviewStatusEnum.UNDER_REVIEW.getCode());
        long now = System.currentTimeMillis();
        if (underReveiwReview != null) {
            underReveiwReview.setStatus(ReviewStatusEnum.EMERGENCY_RELEASE.getCode());
            underReveiwReview.setUtime(now);
            underReveiwReview.setOperateTime(now);
            underReveiwReview.setOperator(username);
            dao.update(underReveiwReview);
        }else{
            Review review = new Review();
            review.setProjectId(projectId);
            review.setProjectName(project.getData().getName());
            review.setCommitId(commitId);
            review.setUrl(project.getData().getGitAddress()+"/-/commit/"+commitId);
            review.setSubmitter(username);
            List reviewers = new ArrayList();
            reviewers.add(username);
            review.setReviewer(new Gson().toJson(reviewers));
            review.setOperator(username);
            review.setOperateTime(now);
            review.setStatus(ReviewStatusEnum.EMERGENCY_RELEASE.getCode());
            review.setRemarks("紧急发布");
            createReview(review);
        }
    }

    public Result<Project> checkParms(ReviewBo review) {
        if (review==null||StringUtils.isBlank(review.getCommitId()) || review.getProjectId() <= 0 || CollectionUtils.isEmpty(review.getReviewers())) {
            log.error("params error:  {}",review.toString());
            return Result.fail(CommonError.InvalidParamError);
        }

        Result<Project> project = projectService.getProjectById(review.getProjectId());
        if (project.getData() == null) {
            log.error("project not exist, project id {}", review.getProjectId());
            return Result.fail(CommonError.InvalidParamError);
        }

        return project;
    }
}
