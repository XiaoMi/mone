package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyApprovalEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class ApplyApprovalDao extends BaseDao{

    public List<ApplyApprovalEntity> getListByPage(Long approvalUserId, Long applyId, Long nodeId, Integer type, Integer status, String approvalName, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (approvalUserId != null) {
            sqlExpr = sqlExpr.andEquals("creater_id", approvalUserId);
        }
        if (applyId != null) {
            sqlExpr = sqlExpr.andEquals("apply_id", applyId);
        }
        if (nodeId != null) {
            sqlExpr = sqlExpr.andEquals("cur_node_id", nodeId);
        }
        if (type != null) {
            sqlExpr = sqlExpr.andEquals("type", type);
        }
        if (status != null) {
            sqlExpr = sqlExpr.andEquals("status", status);
        }
        if (StringUtils.isNotBlank(approvalName)) {
            sqlExpr = sqlExpr.andLike("approval_name", approvalName);
        }
        return getListByPage(sqlExpr, pageData, ApplyApprovalEntity.class);
    }
}
