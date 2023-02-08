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

import com.google.common.collect.Lists;
import com.xiaomi.youpin.gwdash.bo.FlowType;
import com.xiaomi.youpin.gwdash.bo.PluginInfoBo;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.*;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.hermes.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jws.Oneway;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * 审批
 */
@Service
@Slf4j
public class FlowService {

    @Value("${gwdash.review.url}")
    private String reviewUrl;

    @Autowired
    private Dao dao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FeiShuService feiShuService;

    @Reference(check = false, interfaceClass = AccountService.class, group = "${ref.hermes.service.group}")
    private AccountService accountService;

    /**
     * 创建审批
     *
     * @param approval
     */
    public Result<Boolean> create(Approval approval, long accountId) {
        // plugin
        if (approval.getType() == ApprovalType.plugin.ordinal()) {

            PluginInfoBo info = dao.fetch(PluginInfoBo.class, Cnd.where("id", "=", approval.getBizId()));
            if (StringUtils.isNotEmpty(info.getFlowKey())) {
                // 已经申请过了
                return new Result<>(0, "已经申请过了", false);
            }

            if (info.getProjectId() == 0) {
                return new Result<>(1, "绑定审批项目不存在", false);
            }

            approval.setReason("deploy plugin:" + info.getName());

            //项目的拥有者都可以审核
            List<ProjectRole> list = dao.query(ProjectRole.class, Cnd.where("projectId", "=", info.getProjectId()).and("roleType", "=", RoleType.Owner.ordinal()));

            list.forEach(it -> {
                approval.setAuditorId(it.getAccountId());
                dao.insert(approval);
            });

            if (list.size() > 0) {
                dao.update(PluginInfoBo.class, Chain.make("flow_key", approval.getKey()), Cnd.where("id", "=", approval.getBizId()));
            }
        }

        // 部署服务
        if (approval.getType() == ApprovalType.service.ordinal()) {
            if (!projectService.isMember(approval.getProjectId(), accountId)) {
                return new Result<>(1, "需项目成员才可操作", false);
            }
            Project project = dao.fetch(Project.class, approval.getProjectId());
            approval.setReason("deploy project:" + project.getName());
            // key: 项目id+项目type+branch
            approval.setKey(approval.getProjectId() + ":" + approval.getType() + ":" + approval.getBranch());
            // value: commitId
            approval.setContent(approval.getCommitId());
            List<ProjectRole> list = dao.query(ProjectRole.class, Cnd.where("projectId", "=", approval.getProjectId()).and("roleType", "=", RoleType.Owner.ordinal()));

            Approval record = dao.fetch(Approval.class, Cnd.where("key", "=", approval.getKey())
                    .and("content", "=", approval.getContent())
                    .and(Cnd.exps("status", "=", FlowStatus.none.ordinal())
                            .or("status", "=", FlowStatus.agree.ordinal())));

            if (null != record) {
                log.warn("有相同审核条目");
                int status = record.getStatus();
                String errMsg = "申请条目已在审核中";
                if (status == FlowStatus.agree.ordinal()) {
                    errMsg = "申请条目已经通过";
                }
                return new Result<>(1, errMsg, false);
            }

            list.forEach(it -> {
                approval.setAuditorId(it.getAccountId());
                dao.insert(approval);

                //send msg to auditor
                Account account = accountService.queryUserById(it.getAccountId());
                if (account == null) {
                    return;
                }

                StringBuffer sb = new StringBuffer();
                sb.append("米效平台提示\n");
                sb.append("\n您有新的待审核条目");
                sb.append("\n审核内容： ");
                sb.append(approval.getReason());
                sb.append("\n审核地址： ");
                sb.append(reviewUrl);
                feiShuService.sendMsg2Person(account.getUserName(), sb.toString());
            });
        }

        return Result.success(true);
    }

    /**
     * over 结束流程
     * <p>
     * 支持 plugin service
     *
     * @param approval
     */
    public int over(Approval approval) {
        int status = approval.getStatus();

        if (approval.getType() == ApprovalType.plugin.ordinal()) {

            //plugin 需要单独处理
            if (approval.getType() == ApprovalType.plugin.ordinal()) {
                PluginInfoBo bo = dao.fetch(PluginInfoBo.class, Cnd.where("id", "=", approval.getBizId()));
                if (null != bo) {
                    String key = bo.getFlowKey();
                    if (StringUtils.isNotEmpty(key)) {
                        String accountId = key.split(":")[1];
                        if (!String.valueOf(approval.getApplicantId()).equals(accountId)) {
                            //无权归还
                            return 100;
                        }
                    }
                }
            }

            //3 成功  4 失败
            if (status == FlowStatus.success.ordinal() || status == FlowStatus.failure.ordinal()) {
                dao.update(Approval.class, Chain.make("status", status).add("utime", System.currentTimeMillis()), Cnd.where("key", "=", approval.getKey()));
                dao.update(PluginInfoBo.class, Chain.make("flow_key", ""), Cnd.where("id", "=", approval.getBizId()));
            }
        }

        //服务(应用)
        if (approval.getType() == ApprovalType.service.ordinal()) {
            if (status == FlowStatus.success.ordinal() || status == FlowStatus.failure.ordinal()) {
                dao.update(Approval.class, Chain.make("status", status).add("utime", System.currentTimeMillis()),
                        Cnd.where("projectId", "=", approval.getProjectId())
                                .and("status", "=", FlowStatus.agree)
                                .and("applicantId", "=", approval.getApplicantId())
                                .and("type", "=", ApprovalType.service.ordinal()));
            }
        }

        return 0;
    }

    /**
     * 同意
     * 同时修改多条
     * <p>
     * service key = a
     *
     * @param id
     */
    public void agree(int id, int auditorId) {
        Approval approval = dao.fetch(Approval.class, Cnd.where("id", "=", id).and("auditorId", "=", auditorId));
        if (null != approval) {
            dao.update(Approval.class,
                    Chain.make("status", FlowStatus.agree.ordinal())
                            .add("utime", System.currentTimeMillis()),
                    Cnd.where("key", "=", approval.getKey())
                            .and("commit_id", "=", approval.getContent())
                            .and("status", "=", FlowStatus.none.ordinal()));
        }
    }

    /**
     * 拒绝
     * 同时修改多条
     *
     * @param id
     */
    public void refuse(int id, int auditorId) {
        Approval approval = dao.fetch(Approval.class, Cnd.where("id", "=", id).and("auditorId", "=", auditorId));
        if (null != approval) {
            dao.update(Approval.class,
                    Chain.make("status", FlowStatus.refuse.ordinal())
                            .add("utime", System.currentTimeMillis()),
                    Cnd.where("key", "=", approval.getKey())
                            .and("commit_id", "=", approval.getContent())
                            .and("status", "=", FlowStatus.none.ordinal()));
        }
    }

    public void delete(int id) {
        dao.delete(Approval.class, id);
    }


    /**
     * 获取审批列表
     *
     * @param accountId
     * @return
     */
    public List<Approval> list(int accountId, int status) {
        return dao.query(Approval.class,
                Cnd.where("status", "=", status)
                        .and("auditorId", "=", accountId));
    }

    public Map<String, Object> list(int accountId, int status, int page, int pageSize) {
        Cnd cnd =  Cnd.where("status", "=", status)
                .and("auditorId", "=", accountId);
        cnd.desc("id");
        Map<String, Object> map = new HashMap<>();
        map.put("total",  dao.count(Approval.class, cnd));
        map.put("list", dao.query(Approval.class,
                cnd,
                new Pager(page, pageSize)));
        return map;
    }


    public Approval detail(String key) {
        return dao.fetch(Approval.class, Cnd.where("key", "=", key));
    }

    /**
     * 是否被允许u
     *
     * @param key (审批的key)
     * @return
     */
    public boolean isAllow(String key) {
        return dao.count(Approval.class, Cnd.where("key", "=", key).and("status", "=", FlowStatus.agree.ordinal())) > 0;
    }


    /**
     * 是否允许物理机发布服务
     *
     * @param projectId
     * @param envId
     * @param commitId
     * @param accountId
     * @return
     */
    public boolean isAllow(long projectId, long envId, String commitId, Long accountId) {
        Approval approval = dao.fetch(Approval.class,
                Cnd.where("commit_id", "=", commitId)
                        .and("env_id", "=", envId)
                        .and("status", "=", FlowStatus.agree.ordinal())
        );
        if (null == approval) {
            log.warn("isAllow approval is null commitID:{}", commitId);
            return false;
        }
        if (projectService.isMember(projectId, accountId)) {
            log.warn("is not owenr or members");
            return true;
        }
        return false;
    }


    /**
     * 查询审批状态(只会返回没有审批(包含之前已经成功的和失败的)和审批中)
     *
     * @param approval
     * @return 如果为空, 则是可申请
     */
    public Approval getApprovalInfo(Approval approval) {
        //获取最后一条记录即可,状态都是一致的
        Approval data = dao.fetch(Approval.class,
                Cnd.where("projectId", "=", approval.getProjectId())
                        //审批中
                        .and("status", "in", Lists.newArrayList(FlowStatus.none, FlowStatus.agree))
                        .and("type", "=", approval.getType())
                        .orderBy("id", "desc")
        );
        if (null == data) {
            return null;
        }
        return data;
    }


    /**
     * 获取状态.有一条通过了,就说明都通过了
     *
     * @param key
     * @return
     */
    public int getStatus(String key) {
        return Optional.ofNullable(dao.fetch(Approval.class, Cnd.where("key", "=", key))).map(it -> it.getStatus()).orElse(4);
    }

    /**
     * 走流程(审批)
     *
     * @param projectId
     * @param accountId
     * @return
     */
    public Pair<Boolean, Integer> createFlow(String key, int projectId, Long accountId, FlowType flowType, String resion) {
        long now = System.currentTimeMillis();
        if (projectService.isMember(projectId, accountId)) {
            Approval approval = new Approval();
            approval.setApplicantId(accountId.intValue());
            approval.setProjectId(projectId);
            approval.setUtime(now);
            approval.setCtime(now);
            approval.setStatus(0);
            approval.setType(flowType.ordinal());
            approval.setReason(resion);
            approval.setKey(key);
            create(approval, accountId);
            return Pair.of(true, 100);
        }
        return Pair.of(false, 400);
    }


}
