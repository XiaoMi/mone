package com.xiaomi.youpin.prometheus.agent.Impl;

import com.xiaomi.youpin.prometheus.agent.entity.RuleAlertEntity;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class RuleAlertDao extends BaseDao {

    public SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Long CreateRuleAlert(RuleAlertEntity entity) {
        Long id = dao.insert(entity).getId();
        return id;
    }

    public String UpdateRuleAlert(String id, RuleAlertEntity entity) {
        try {
            //更新
            int update = dao.updateIgnoreNull(entity);
            return String.valueOf(update);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public int DeleteRuleAlert(String id) {
        //软删除
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("id", id).andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        RuleAlertEntity dbRes = dao.fetch(RuleAlertEntity.class, cnd);
        if (dbRes == null) {
            return -1;
        }
        dbRes.setDeletedBy("xxx");  // TODO:加上真是用户名
        dbRes.setUpdatedTime(new Date());
        dbRes.setDeletedTime(new Date());
        int updateRes = dao.updateIgnoreNull(dbRes);
        return updateRes;
    }

    public RuleAlertEntity GetRuleAlert(String id) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("id", id).andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        RuleAlertEntity dbRes = dao.fetch(RuleAlertEntity.class, cnd);
        return dbRes;
    }

    public RuleAlertEntity GetRuleAlertByAlertName(String name) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("name", name).andIsNull("deleted_time").andEquals("enabled", 1);
        Cnd cnd = Cnd.where(sqlExpr);
        RuleAlertEntity dbRes = dao.fetch(RuleAlertEntity.class, cnd);
        return dbRes;
    }

    public String[] GetRuleAlertAtPeople(String name) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("name", name).andIsNull("deleted_time").andEquals("enabled", 1);
        Cnd cnd = Cnd.where(sqlExpr);
        RuleAlertEntity dbRes = dao.fetch(RuleAlertEntity.class, cnd);
        if (dbRes == null) {
            return null;
        }
        String[] peoples = dbRes.getAlertAtPeople().split(",");
        return peoples;
    }

    public List<RuleAlertEntity> GetRuleAlertList(Integer pageSize, Integer pageNo) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        List<RuleAlertEntity> datas = dao.query(RuleAlertEntity.class, cnd.desc("id"), buildPager(pageNo, pageSize));
        return datas;
    }

    public Integer CountRuleAlert() {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        int count = dao.count(RuleAlertEntity.class, cnd);
        return count;
    }

    public List<RuleAlertEntity> GetAllRuleAlertList() {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIsNull("deleted_time").andEquals("enabled", 1);
        Cnd cnd = Cnd.where(sqlExpr);
        List<RuleAlertEntity> datas = dao.query(RuleAlertEntity.class, cnd.desc("id"));
        return datas;
    }

}
