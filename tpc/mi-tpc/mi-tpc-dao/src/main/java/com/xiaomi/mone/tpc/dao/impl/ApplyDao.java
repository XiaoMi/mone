package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/2 18:39
 */
@Slf4j
@Repository
public class ApplyDao extends BaseDao{

    public List<ApplyEntity> getListByPage(boolean myApply, Long memberId, Long nodeId, Integer type, Integer status, String applyName, PageDataVo pageData) {
        //我申请的工单
        if (myApply) {
            SqlExpressionGroup sqlExpr = Cnd.cri().where();
            sqlExpr = sqlExpr.andEquals("apply_user_id", memberId);
            if (nodeId != null) {
                sqlExpr = sqlExpr.andEquals("cur_node_id", nodeId);
            }
            if (type != null) {
                sqlExpr = sqlExpr.andEquals("type", type);
            }
            if (status != null) {
                sqlExpr = sqlExpr.andEquals("status", status);
            }
            if (StringUtils.isNotBlank(applyName)) {
                sqlExpr = sqlExpr.andLike("apply_name", applyName);
            }
            return getListByPage(sqlExpr, pageData, ApplyEntity.class);
        } else {
            //待我审核的工单
            Map<String, Object> params = Maps.newHashMap();
            setParam(params, "memberId", memberId);
            setParam(params, "nodeId", nodeId);
            setParam(params, "type", type);
            setParam(params, "status", status);
            if (StringUtils.isNotBlank(applyName)) {
                setParam(params, "applyName", "%" + applyName + "%");
            }
            StringBuilder sqlExpr = new StringBuilder();
            sqlExpr.append("select a.* from node_user_rel_entity nul,apply_entity a where nul.user_id=@memberId and nul.node_id=a.cur_node_id and a.deleted=0 and nul.deleted=0");
            if (nodeId != null) {
                sqlExpr.append(" and a.cur_node_id=@nodeId");
            }
            if (type != null) {
                sqlExpr.append(" and a.type=@type");
            }
            if (status != null) {
                sqlExpr.append(" and a.status=@status");
            }
            if (StringUtils.isNotBlank(applyName)) {
                sqlExpr.append(" and a.apply_name like @applyName");
            }
            sqlExpr.append(" order by create_time desc");
            return getListByPage(sqlExpr, params, pageData, ApplyEntity.class);
        }
    }

}
