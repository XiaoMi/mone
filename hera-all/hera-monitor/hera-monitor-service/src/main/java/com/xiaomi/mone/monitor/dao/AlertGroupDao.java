package com.xiaomi.mone.monitor.dao;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.xiaomi.mone.monitor.dao.model.AlertGroup;
import com.xiaomi.mone.monitor.dao.model.AlertGroupMember;
import com.xiaomi.mone.monitor.service.model.PageData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.*;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AlertGroupDao {


    @Autowired
    private Dao dao;

    public List<AlertGroup> getByIds(List<Long> ids, boolean needMember) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andInList("id", ids);
        List<AlertGroup> agList = dao.query(AlertGroup.class, Cnd.where(sqlExpr));
        if (CollectionUtils.isEmpty(agList) || !needMember) {
            return agList;
        }
        sqlExpr = Cnd.cri().where().andInList("alert_group_id", agList.stream().map(AlertGroup::getId).collect(Collectors.toList()));
        List<AlertGroupMember> agmList = dao.query(AlertGroupMember.class, Cnd.where(sqlExpr));
        if (!CollectionUtils.isEmpty(agmList)) {
            Map<Long, AlertGroup> agMap = agList.stream().collect(Collectors.toMap(ag -> ag.getId(), ag -> ag));
            agmList.stream().forEach(agm -> {
                AlertGroup ag = agMap.get(agm.getAlertGroupId());
                if (ag == null) {
                    return;
                }
                if (ag.getMembers() == null) {
                    ag.setMembers(new ArrayList<>());
                }
                ag.getMembers().add(agm);
            });
        }
        return agList;
    }

    public AlertGroup getById(long id) {
        AlertGroup ag = dao.fetch(AlertGroup.class, id);
        if (ag == null || Integer.valueOf("1").equals(ag.getDeleted())) {
            return null;
        }
        SqlExpressionGroup sql = new SqlExpressionGroup();
        sql.andEquals("deleted", 0);
        dao.fetchLinks(ag, "members", Cnd.where(sql));
        return ag;
    }

    public AlertGroup getByRelId(String type, long relId) {
        AlertGroup ag = dao.fetch(AlertGroup.class, Cnd.where("rel_id", "=", relId).and("type","=", type).and("deleted", "=", 0));
        if (ag == null) {
            return null;
        }
        SqlExpressionGroup sql = new SqlExpressionGroup();
        sql.andEquals("deleted", 0);
        dao.fetchLinks(ag, "members", Cnd.where(sql));
        return ag;
    }

    public List<AlertGroup> getByRelIds(String type, List<Long> relIds) {
        SqlExpressionGroup exprSql = Cnd.cri().where().andEquals("type", type).andEquals("deleted", 0).andInList("rel_id", relIds);
        //没有读取成员扩展表信息
        return dao.query(AlertGroup.class, Cnd.where(exprSql));
    }

    public boolean delete(AlertGroup alertGroup) {
        boolean result = false;
        try {
            alertGroup.setDeleted(1);
            dao.updateIgnoreNull(alertGroup);
            dao.update(AlertGroupMember.class, Chain.make("deleted",1), Cnd.where("alertGroupId","=",alertGroup.getId()));
        } catch (Throwable e) {
            log.error("删除告警组异常; alertGroup={}", alertGroup, e);
            result = false;
        } finally {
            log.info("删除告警组{}, result={}",alertGroup, result);
        }
        return result;
    }

    public boolean insert(AlertGroup alertGroup) {
        alertGroup.setDeleted(0);
        Date now = new Date();
        alertGroup.setUpdateTime(now);
        if (alertGroup.getCreateTime() == null) {
            alertGroup.setCreateTime(now);
        }
        if (alertGroup.getRelId() == null) {
            alertGroup.setRelId(0L);
        }
        if (StringUtils.isBlank(alertGroup.getType())) {
            alertGroup.setType("alert");
        }
        if (!CollectionUtils.isEmpty(alertGroup.getMembers())) {
            for (AlertGroupMember addMember : alertGroup.getMembers()) {
                addMember.setCreateTime(alertGroup.getCreateTime());
                addMember.setUpdateTime(alertGroup.getUpdateTime());
                addMember.setCreater(alertGroup.getCreater());
                addMember.setDeleted(0);
            }
        }
        try {
            dao.insert(alertGroup);
            if (!CollectionUtils.isEmpty(alertGroup.getMembers())) {
                for (AlertGroupMember member : alertGroup.getMembers()) {
                    member.setAlertGroupId(alertGroup.getId());
                }
                dao.insert(alertGroup.getMembers());
            }
            return true;
        } catch (Throwable e) {
            AlertGroupDao.log.error("HeraOperLog表插入或更新异常； log={}", log, e);
            return false;
        }
    }

    public boolean updateById(AlertGroup alertGroup, List<AlertGroupMember> addMembers, List<AlertGroupMember> delMembers) {
        Date now = new Date();
        alertGroup.setUpdateTime(now);
        if (!CollectionUtils.isEmpty(addMembers)) {
            for (AlertGroupMember addMember : addMembers) {
                addMember.setCreateTime(now);
                addMember.setUpdateTime(now);
                addMember.setDeleted(0);
                addMember.setCreater(alertGroup.getCreater());
                addMember.setAlertGroupId(alertGroup.getId());
            }
        }
        Condition cnd = null;
        if (!CollectionUtils.isEmpty(delMembers)) {
            long [] array = delMembers.stream().mapToLong(t->t.getMemberId().longValue()).toArray();
            cnd = Cnd.where(Cnd.cri().where().andIn("member_id", array).andEquals("alert_group_id", alertGroup.getId()));
        }
        try {
            //软删除
            if (cnd != null) {
                dao.update(AlertGroupMember.class, Chain.make("deleted",1), cnd);
            }
            if (!CollectionUtils.isEmpty(addMembers)) {
                dao.insert(addMembers);
            }
            dao.updateIgnoreNull(alertGroup);
            return true;
        } catch (Exception e) {
            AlertGroupDao.log.error("HeraOperLog表插入或更新异常； log={}", log, e);
            return false;
        }
    }

    public PageData<List<AlertGroup>> searchByCond(Boolean isAdmin,String member, String name, String type, int page, int pageSize) {
        PageData<List<AlertGroup>> pageData = new PageData();
        pageData.setPage(page);
        pageData.setPageSize(pageSize);
        try {
            StringBuilder sqlB = new StringBuilder();
            sqlB.append("select ").append("ag.id,ag.rel_id,ag.name,ag.chat_id,ag.creater,ag.create_time,ag.update_time,ag.type,ag.desc").append(" from ")
                    .append("alert_group ag left join alert_group_member agm on ag.id=agm.alert_group_id")
                    .append(" where ag.deleted=0 and agm.deleted=0 ");
            if(!isAdmin){
                sqlB.append(" and agm.member='").append(member).append("'");
            }

            if (StringUtils.isNotBlank(name)) {
                sqlB.append(" and ag.name LIKE '%").append(name).append("%'");
            }
            if (StringUtils.isNotBlank(type)) {
                sqlB.append(" and ag.type = '").append(type).append("'");
            }
            sqlB.append(" GROUP BY ag.id");
            sqlB.append(" order by ag.id desc");
            Sql sql = Sqls.create(sqlB.toString());
            long totalCount = Daos.queryCount(dao, sql);
            if (totalCount <= 0L) {
                pageData.setTotal(0L);
                return pageData;
            }
            pageData.setTotal(totalCount);
            sql.setPager(new Pager(page, pageSize));
            Map<Long, AlertGroup> agMap = new HashMap<>();
            sql.setCallback(new SqlCallback() {
                @Override
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    List<AlertGroup> list = new ArrayList<>();
                    while (rs.next()) {
                        AlertGroup ag = new AlertGroup();
                        ag.setId(rs.getLong("id"));
                        ag.setRelId(rs.getLong("rel_id"));
                        ag.setName(rs.getString("name"));
                        ag.setChatId(rs.getString("chat_id"));
                        ag.setCreater(rs.getString("creater"));
                        ag.setUpdateTime(rs.getTimestamp("update_time"));
                        ag.setCreateTime(rs.getTimestamp("create_time"));
                        ag.setType(rs.getString("type"));
                        ag.setDesc(rs.getString("desc"));
                        list.add(ag);
                        agMap.put(ag.getId(), ag);
                    }
                    return list;
                }
            });
            dao.execute(sql);
            pageData.setList(sql.getList(AlertGroup.class));
            if (!agMap.isEmpty()) {
                long[] array = agMap.keySet().stream().mapToLong(t -> t.longValue()).toArray();
                List<AlertGroupMember> agmList = dao.query(AlertGroupMember.class, Cnd.where(Cnd.cri().where().andIn("alert_group_id", array).andEquals("deleted", "0")));
                if (!CollectionUtils.isEmpty(agmList)) {
                    agmList.forEach(agm -> {
                        AlertGroup ag = agMap.get(agm.getAlertGroupId());
                        if (ag == null) {
                            return;
                        }
                        if (ag.getMembers() == null) {
                            ag.setMembers(new ArrayList<>());
                        }
                        ag.getMembers().add(agm);
                    });
                }
            }
            return pageData;
        } catch (Exception e) {
            log.error("AlertGroup表查询异常； member={},name={}", member, name, e);
            pageData.setTotal(0L);
            pageData.setList(null);
            return pageData;
        } finally {
            log.info("AlertGroupDao.searchByCond.result pageData={}", pageData);
        }
    }



}
