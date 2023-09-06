package com.xiaomi.youpin.prometheus.agent.Impl;

import com.xiaomi.youpin.prometheus.agent.entity.ScrapeConfigEntity;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.enums.ScrapeJobStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class ScrapeConfigDao extends BaseDao {

    public Long CreateScrapeConfig(ScrapeConfigEntity entity) {
        Long id = dao.insert(entity).getId();
        return id;
    }

    public int DeleteScrapeConfig(String id) {
        // soft delete
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("id", id).andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        ScrapeConfigEntity dbRes = dao.fetch(ScrapeConfigEntity.class, cnd);
        if (dbRes == null) {
            return -1;
        }
        dbRes.setDeletedBy("xxx");  // TODO:use real user name
        dbRes.setDeletedTime(new Date());
        int updateRes = dao.update(dbRes);
        return updateRes;
    }

    public ScrapeConfigEntity GetScrapeConfig(String id) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("id", id).andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        ScrapeConfigEntity dbRes = dao.fetch(ScrapeConfigEntity.class, cnd);
        return dbRes;
    }

    public ScrapeConfigEntity GetScrapeConfigByName(String name) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("job_name", name).andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        ScrapeConfigEntity dbRes = dao.fetch(ScrapeConfigEntity.class, cnd);
        return dbRes;
    }

    public List<ScrapeConfigEntity> GetScrapeConfigList(Integer pageSize, Integer pageNo) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        List<ScrapeConfigEntity> datas = dao.query(ScrapeConfigEntity.class, cnd.desc("id"), buildPager(pageNo, pageSize));
        return datas;
    }

    public List<ScrapeConfigEntity> GetAllScrapeConfigList(String status) {
        // Retrieve data in pending status and already deleted
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIsNull("deleted_time");//.andEquals("status", status);
        Cnd cnd = Cnd.where(sqlExpr);
        List<ScrapeConfigEntity> datas = dao.query(ScrapeConfigEntity.class, cnd.desc("id"));
        return datas;
    }

    public Integer CountScrapeConfig() {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        int count = dao.count(ScrapeConfigEntity.class, cnd);
        return count;
    }

    public String UpdateScrapeConfigList(String id, ScrapeConfigEntity entity) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("id", id).andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        try {
            ScrapeConfigEntity data = dao.fetch(ScrapeConfigEntity.class, cnd);
            if (data == null) {
                return ErrorCode.invalidParamError.getMessage();
            }
            // update
            int update = dao.updateIgnoreNull(entity);
            return String.valueOf(update);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public int UpdateScrapeConfigListByJobName(String jobName, String status) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where().andEquals("job_name", jobName).andIsNull("deleted_time");
        Cnd cnd = Cnd.where(sqlExpr);
        try {
            ScrapeConfigEntity data = dao.fetch(ScrapeConfigEntity.class, cnd);
            data.setStatus(status);
            // update
            int update = dao.updateIgnoreNull(data);
            return update;
        } catch (Exception e) {
            log.error("ScrapeConfigDao UpdateScrapeConfigListByJobName error:{}", e.getMessage());
            return 0;
        }
    }
}
