package com.xiaomi.mone.tpc.dao.impl;

import com.xiaomi.mone.tpc.dao.entity.BaseEntity;
import com.xiaomi.mone.tpc.login.common.vo.PageDataVo;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 10:57
 */
@Slf4j
public abstract class BaseDao {

    @Autowired
    protected Dao dao;

    public boolean insert(BaseEntity entity) {
        try {
            return insertWithException(entity);
        } catch (Throwable e) {
            log.error("BaseDao.insert={}",entity, e);
            return false;
        }
    }

    public boolean insertWithException(BaseEntity entity) {
        entity.insertInit();
        BaseEntity result = dao.insert(entity);
        log.info("BaseDao.insert={}",result);
        return result != null;
    }

    public boolean updateById(BaseEntity entity) {
        try {
            return updateByIdWithExcption(entity);
        } catch (Throwable e) {
            log.error("BaseDao.update={}",entity, e);
            return false;
        }
    }

    public boolean updateByIdWithExcption(BaseEntity entity) {
        entity.updateInit();
        dao.updateIgnoreNull(entity);
        log.info("BaseDao.update={}",entity);
        return true;
    }

    public boolean batchInsert(List<? extends BaseEntity> entities) {
        try {
            return batchInsertWithException(entities);
        } catch (Throwable e) {
            log.error("BaseDao.batchInsert={}",entities, e);
            return false;
        }
    }

    public boolean batchInsertWithException(List<? extends BaseEntity> entities) {
        if ( CollectionUtils.isEmpty(entities)) {
            return true;
        }
        for (BaseEntity entity : entities) {
            entity.insertInit();
        }
        dao.insert(entities);
        log.info("BaseDao.batchInsert={}",entities);
        return true;
    }

    public <T extends BaseEntity> T getById(Long id, Class<T> clazz) {
        if (id == null) {
            return null;
        }
        T t = dao.fetch(clazz, Cnd.where(Cnd.cri().where().andEquals("id", id).andEquals("deleted", 0)));
        log.info("BaseDao.getById id={}, clazzName={}, result={}", id, clazz.getName(), t);
        return t;
    }

    public <T extends BaseEntity> List<T> getByIds(Collection<Long> ids, Class<T> clazz) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        long[] idArr = ids.stream().mapToLong(e -> e.longValue()).toArray();
        List<T> tList = dao.query(clazz, Cnd.where(Cnd.cri().where().andIn("id", idArr).andEquals("deleted", 0)));
        log.info("BaseDao.getByIds ids={}, clazzName={}, result={}", ids, clazz.getName(), tList.size());
        return tList;
    }

    public <T extends BaseEntity> boolean deleteById(BaseEntity entity) {
        try {
            if (entity.getId() == null) {
                return false;
            }
            entity.setDeleted(1);
            entity.updateInit();
            dao.updateIgnoreNull(entity);
            log.info("BaseDao.deleteById entity={}", entity);
            return true;
        } catch (Throwable e) {
            log.error("BaseDao.deleteById entity={}", entity, e);
            return false;
        }
    }

    public <T extends BaseEntity> boolean delete(SqlExpressionGroup sqlExpr, Class<T> clazz) {
        sqlExpr = sqlExpr.andEquals("deleted",0);
        try {
            BaseEntity entity = clazz.newInstance();
           // entity.setDeleted(1);
            dao.update(clazz, Chain.make("deleted",1), Cnd.where(sqlExpr));
            log.info("BaseDao.delete sqlExpr={}, clazzName={}", sqlExpr, clazz.getName());
            return true;
        } catch (Throwable e) {
            log.error("BaseDao.delete sqlExpr={}, clazzName={}", sqlExpr, clazz.getName(), e);
            return false;
        }
    }

    public <T> T fetch(SqlExpressionGroup sqlExpr, Class<T> clazz) {
        sqlExpr = sqlExpr.andEquals("deleted",0);
        return dao.fetch(clazz, Cnd.where(sqlExpr));
    }

    public <T> List<T> query(SqlExpressionGroup sqlExpr, Class<T> clazz) {
        return query(sqlExpr, clazz, 100);
    }

    public <T> List<T> query(SqlExpressionGroup sqlExpr, Class<T> clazz, int limit) {
        sqlExpr = sqlExpr.andEquals("deleted",0);
        return dao.query(clazz, Cnd.where(sqlExpr).desc("id"), new Pager(1, limit));
    }

    public <T> List<T> getListByPage(SqlExpressionGroup sqlExpr, PageDataVo pageData, Class<T> clazz) {
        sqlExpr = sqlExpr.andEquals("deleted",0);
        Cnd cnd = Cnd.where(sqlExpr);
        if (pageData.isPager()) {
            int total = dao.count(clazz, cnd);
            pageData.setTotal(total);
            if (total <= 0) {
                return null;
            }
        }
        return dao.query(clazz, cnd.desc("id"), buildPager(pageData));
    }

    public <T> List<T> getListByPage(StringBuilder sqlExpr, Map<String, Object> params, PageDataVo pageData, Class<T> clazz) {
        Sql sql = Sqls.queryEntity(sqlExpr.toString());
        sql.setEntity(dao.getEntity(clazz));
        sql.setPager(buildPager(pageData));
        if (!CollectionUtils.isEmpty(params)) {
            sql.params().putAll(params);
        }
        if (pageData.isPager()) {
            long total = Daos.queryCount(dao, sql);
            pageData.setTotal((int)total);
            if (total <= 0) {
                return null;
            }
        }
        dao.execute(sql);
        return sql.getList(clazz);
    }

    protected Pager buildPager(PageDataVo pageData) {
        Pager pager = new Pager();
        pager.setPageNumber(pageData.getPage());
        pager.setPageSize(pageData.getPageSize());
        return pager;
    }

    protected void setParam(Map<String, Object> params, String key, Object val) {
        if (val == null) {
            return;
        }
        params.put(key, val);
    }

}
