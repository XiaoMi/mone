package com.xiaomi.mone.monitor.dao.impl;

import com.xiaomi.mone.monitor.bo.AlarmStrategyInfo;
import com.xiaomi.mone.monitor.bo.AlarmStrategyType;
import com.xiaomi.mone.monitor.dao.AppAlarmStrategyDao;
import com.xiaomi.mone.monitor.dao.model.AlarmStrategy;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.user.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.Daos;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class AppAlarmStrategyDaoImpl implements AppAlarmStrategyDao {

    @Autowired
    private Dao dao;

    @Autowired
    UserConfigService userConfigService;

    @Override
    public AlarmStrategy getById(Integer id) {
        return dao.fetch(AlarmStrategy.class, id);
    }

    /**
     * 清洗数据专用
     * @param type
     * @return
     */
    @Override
    public List<AlarmStrategy> queryByType(int type) {
        return dao.query(AlarmStrategy.class, Cnd.where("strategy_type", "=", 3).and("group3", "=", ""));
    }

    @Override
    public AlarmStrategyInfo getInfoById(Integer id) {
        return buildAlarmStrategyInfo(getById(id));
    }

    @Override
    public boolean insert(AlarmStrategy strategy) {
        if (strategy.getCreateTime() == null) {
            strategy.setCreateTime(new Date());
        }
        if (strategy.getUpdateTime() == null) {
            strategy.setUpdateTime(new Date());
        }
        if (strategy.getStatus() == null) {
            strategy.setStatus(0);
        }
        if (strategy.getStrategyType() == null) {
            strategy.setStrategyType(0);
        }
        if (StringUtils.isBlank(strategy.getGroup3())) {
            strategy.setGroup3("");
        }
        if (StringUtils.isBlank(strategy.getGroup4())) {
            strategy.setGroup4("");
        }
        if (StringUtils.isBlank(strategy.getGroup5())) {
            strategy.setGroup5("");
        }
        try {
            return dao.insert(strategy) != null;
        } catch (Exception e) {
            log.error("appAlarmStrategy表插入异常； strategy={}", strategy, e);
            return false;
        }
    }

    @Override
    public boolean updateById(AlarmStrategy strategy) {
        if (strategy.getUpdateTime() == null) {
            strategy.setUpdateTime(new Date());
        }
        try {
            return  dao.updateIgnoreNull(strategy) > 0;
        } catch (Exception e) {
            log.error("appAlarmStrategy表更新异常； strategy={}", strategy, e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            return  dao.delete(AlarmStrategy.class, id) > 0;
        } catch (Exception e) {
            log.error("appAlarmStrategy表删除异常； id={}", id, e);
            return false;
        }
    }

    @Override
    public PageData<List<AlarmStrategyInfo>> searchByCond(final String user,Boolean filterOwner, AlarmStrategy strategy, int page, int pageSize,String sortBy,String sortRule) {
        log.info("AppAlarmStrategyDao.searchByCond strategy={}, page={}, pageSize={}", strategy, page, pageSize);
        PageData<List<AlarmStrategyInfo>> pageData = new PageData();
        pageData.setPage(page);
        pageData.setPageSize(pageSize);
        try {
            StringBuilder sqlB = new StringBuilder();
            sqlB.append("select an.id").append(",an.iamId").append(",an.appId").append(",an.appName").append(",an.strategy_type")
                    .append(",an.strategy_name").append(",an.desc").append(",an.creater").append(",an.status").append(",an.alert_team")
                    .append(",an.create_time").append(",an.update_time").append(",an.group3").append(",an.group4").append(",an.group5")
                    .append(",an.envs,an.alert_members,at_members");

            if(strategy.getStrategyType() != null && strategy.getStrategyType().equals(AlarmStrategyType.TESLA.getCode())){
                sqlB.append(",an.creater as owner  from app_alarm_strategy an where 1=1 ");
                if(!userConfigService.isAdmin(user)){

                    StringBuilder builder = new StringBuilder();

                    if (StringUtils.isNotBlank(strategy.getGroup3())) {
                        builder.append(" an.group3='").append(strategy.getGroup3()).append("'");
                    }
                    if (StringUtils.isNotBlank(strategy.getGroup4())) {
                        builder.append(" and an.group4='").append(strategy.getGroup4()).append("'");
                    }
                    if (StringUtils.isNotBlank(strategy.getGroup5())) {
                        builder.append(" and an.group5='").append(strategy.getGroup5()).append("'");
                    }
                    if(builder.length() > 0){
                        sqlB.append("and ((");
                        sqlB.append(builder.toString());
                        sqlB.append(")");
                        sqlB.append(" or creater=");
                        sqlB.append("@user");
                        sqlB.append(") ");
                    }else{
                        sqlB.append("and creater=");
                        sqlB.append("@user");
                    }
                }
            }else{
                sqlB.append(",app.owner").append(" from ")
                        .append("app_alarm_strategy an " +
                                    "left join app_monitor app" +
                                        " on an.appId=app.project_id")
                                        //" and an.iamId=app.iam_tree_id")
                        .append(" where app.status=0 ");
                        if (filterOwner != null && filterOwner) {
                            sqlB.append(" and app.owner=").append("@user");
                        } else {
                            sqlB.append(" and (app.owner=").append("@user").append(" or app.care_user=").append("@user").append(")");
                        }
            }

            if (strategy.getStatus() != null) {
                sqlB.append(" and an.status=").append(strategy.getStatus());
            }
            if (strategy.getAppId() != null) {
                sqlB.append(" and an.appId=").append(strategy.getAppId());
            }
            if (StringUtils.isNotBlank(strategy.getAppName())) {
                sqlB.append(" and an.appName LIKE ").append("@appName");
            }
            if (StringUtils.isNotBlank(strategy.getCreater())) {
                sqlB.append(" and an.creater=").append("@creater");
            }
            if (strategy.getStrategyType() != null) {
                sqlB.append(" and an.strategy_type=").append(strategy.getStrategyType());
            }else{
                //非tesla（不指定策略类型时）过滤掉tesla类型的策略
                sqlB.append(" and an.strategy_type!=").append(AlarmStrategyType.TESLA.getCode());
            }
            if (StringUtils.isNotBlank(strategy.getStrategyName())) {
                sqlB.append(" and an.strategy_name LIKE ").append("@strategyName");
            }

            if(StringUtils.isNotBlank(sortBy) && StringUtils.isNotBlank(sortRule)){
                sqlB.append(" order by an.").append(sortBy).append(" ").append(sortRule);
            }else{
                log.info("Alarm strategy searchByCond no order info assign! use default order");
                sqlB.append(" order by an.id desc");
            }
            Sql sql = Sqls.create(sqlB.toString()).setParam("user", user).setParam("creater", strategy.getCreater());
            if (StringUtils.isNotBlank(strategy.getAppName())) {
                sql = sql.setParam("appName", "%" + strategy.getAppName() + "%");
            }
            if (StringUtils.isNotBlank(strategy.getStrategyName())) {
                sql = sql.setParam("strategyName", "%" + strategy.getStrategyName() + "%");
            }
            long totalCount = Daos.queryCount(dao, sql);
            if (totalCount <= 0L) {
                pageData.setTotal(0L);
                return pageData;
            }

            log.info("searchByCond# sql:{}",sql);

            pageData.setTotal(totalCount);
            sql.setPager(new Pager(page, pageSize));
            sql.setCallback(new SqlCallback() {
                @Override
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    List<AlarmStrategyInfo> list = new ArrayList<>();
                    while (rs.next()) {
                        AlarmStrategyInfo info = new AlarmStrategyInfo();
                        info.setId(rs.getInt("id"));
                        Date date = rs.getTimestamp("update_time");
                        if (date != null) {
                            info.setUpdateTime(date.getTime());
                        }
                        date = rs.getTimestamp("create_time");
                        if (date != null) {
                            info.setCreateTime(date.getTime());
                        }
                        Integer strategyType =  rs.getInt("strategy_type");
                        info.setAppId(rs.getInt("appId"));
                        info.setAppName(rs.getString("appName"));
                        info.setStrategyName(rs.getString("strategy_name"));
                        info.setAlertTeam(rs.getString("alert_team"));
                        info.setStrategyType(strategyType);
                        info.setCreater(rs.getString("creater"));
                        info.setStrategyDesc(rs.getString("desc"));
                        info.setStatus(rs.getInt("status"));
                        info.setIamId(rs.getInt("iamId"));
                        String owner = rs.getString("owner");
                        if(AlarmStrategyType.TESLA.getCode().equals(strategyType)){
                            if (user.equals(rs.getString("creater"))) {
                                info.setOwner(true);
                            } else {
                                info.setOwner(false);
                            }
                        }else{
                            if (user.equals(owner)) {
                                info.setOwner(true);
                            } else {
                                info.setOwner(false);
                            }
                        }

                        String envs = rs.getString("envs");
                        info.convertEnvList(envs);
                        String alertMembers = rs.getString("alert_members");
                        info.setAlertMembers(StringUtils.isBlank(alertMembers) ? null :  Arrays.asList(alertMembers.split(",")));
                        String atMembers = rs.getString("at_members");
                        info.setAtMembers(StringUtils.isBlank(atMembers) ? null :  Arrays.asList(atMembers.split(",")));

                        list.add(info);
                    }
                    return list;
                }
            });
            dao.execute(sql);
            pageData.setList(sql.getList(AlarmStrategyInfo.class));
            return pageData;
        } catch (Exception e) {
            log.error("appAlarmStrategy表查询异常； strategy={}", strategy, e);
            pageData.setTotal(0L);
            return pageData;
        } finally {
            log.info("AppAlarmStrategyDao.searchByCond.result pageData={}", pageData);
        }
    }

    @Deprecated
    @Override
    public PageData<List<AlarmStrategyInfo>> searchByCondNoUser( AlarmStrategy strategy, int page, int pageSize,String sortBy,String sortRule) {

        log.info("AppAlarmStrategyDao.searchWashData strategy={}, page={}, pageSize={}", strategy, page, pageSize);

        PageData<List<AlarmStrategyInfo>> pageData = new PageData();
        pageData.setPage(page);
        pageData.setPageSize(pageSize);
        try {
            StringBuilder sqlB = new StringBuilder();
            sqlB.append("select an.id").append(",an.iamId").append(",an.appId").append(",an.appName").append(",an.strategy_type")
                    .append(",an.strategy_name").append(",an.desc").append(",an.creater").append(",an.status").append(",an.alert_team")
                    .append(",an.create_time").append(",an.update_time").append(",an.group3").append(",an.group4").append(",an.group5")
                    .append(",an.envs,an.alert_members,at_members");

            sqlB.append(" from ")
                    .append("app_alarm_strategy an ").append(" where 1=1 ");

            if (strategy.getStatus() != null) {
                sqlB.append(" and an.status=").append(strategy.getStatus());
            }
            if (strategy.getAppId() != null) {
                sqlB.append(" and an.appId=").append(strategy.getAppId());
            }
            if (StringUtils.isNotBlank(strategy.getAppName())) {
                sqlB.append(" and an.appName = ").append("@appName");
            }
            if (StringUtils.isNotBlank(strategy.getCreater())) {
                sqlB.append(" and an.creater=@creater");
            }
            if (strategy.getStrategyType() != null) {
                sqlB.append(" and an.strategy_type=").append(strategy.getStrategyType());
            }else{
                //非tesla（不指定策略类型时）过滤掉tesla类型的策略
                sqlB.append(" and an.strategy_type!=").append(AlarmStrategyType.TESLA.getCode());
            }
            if (StringUtils.isNotBlank(strategy.getStrategyName())) {
                sqlB.append(" and an.strategy_name LIKE ").append("@strategyName");
            }

            if(StringUtils.isNotBlank(sortBy) && StringUtils.isNotBlank(sortRule)){
                sqlB.append(" order by an.").append(sortBy).append(" ").append(sortRule);
            }else{
                log.info("Alarm strategy searchByCond no order info assign! use default order");
                sqlB.append(" order by an.id desc");
            }

            Sql sql = Sqls.create(sqlB.toString()).setParam("creater", strategy.getCreater()).setParam("appName", strategy.getAppName());
            if (StringUtils.isNotBlank(strategy.getStrategyName())) {
                sql = sql.setParam("strategyName", "%" + strategy.getStrategyName() + "%");
            }
            long totalCount = Daos.queryCount(dao, sql);
            if (totalCount <= 0L) {
                pageData.setTotal(0L);
                return pageData;
            }
            pageData.setTotal(totalCount);
            sql.setPager(new Pager(page, pageSize));
            sql.setCallback(new SqlCallback() {
                @Override
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    List<AlarmStrategyInfo> list = new ArrayList<>();
                    while (rs.next()) {
                        AlarmStrategyInfo info = new AlarmStrategyInfo();
                        info.setId(rs.getInt("id"));
                        Date date = rs.getTimestamp("update_time");
                        if (date != null) {
                            info.setUpdateTime(date.getTime());
                        }
                        date = rs.getTimestamp("create_time");
                        if (date != null) {
                            info.setCreateTime(date.getTime());
                        }
                        Integer strategyType =  rs.getInt("strategy_type");
                        info.setAppId(rs.getInt("appId"));
                        info.setAppName(rs.getString("appName"));
                        info.setStrategyName(rs.getString("strategy_name"));
                        info.setAlertTeam(rs.getString("alert_team"));
                        info.setStrategyType(strategyType);
                        info.setCreater(rs.getString("creater"));
                        info.setStrategyDesc(rs.getString("desc"));
                        info.setStatus(rs.getInt("status"));
                        info.setIamId(rs.getInt("iamId"));
                        String envs = rs.getString("envs");
                        info.convertEnvList(envs);
                        String alertMembers = rs.getString("alert_members");
                        info.setAlertMembers(StringUtils.isBlank(alertMembers) ? null :  Arrays.asList(alertMembers.split(",")));
                        String atMembers = rs.getString("at_members");
                        info.setAtMembers(StringUtils.isBlank(atMembers) ? null :  Arrays.asList(atMembers.split(",")));

                        list.add(info);
                    }
                    return list;
                }
            });
            dao.execute(sql);
            pageData.setList(sql.getList(AlarmStrategyInfo.class));
            return pageData;
        } catch (Exception e) {
            log.error("appAlarmStrategy表查询异常； strategy={}", strategy, e);
            pageData.setTotal(0L);
            return pageData;
        } finally {
            log.info("AppAlarmStrategyDao.searchByCond.result pageData={}", pageData);
        }
    }

    private AlarmStrategyInfo buildAlarmStrategyInfo(AlarmStrategy strategy) {
        if (strategy == null) {
            return null;
        }
        AlarmStrategyInfo info = new AlarmStrategyInfo();
        BeanUtils.copyProperties(strategy, info);
        if (strategy.getCreateTime() != null) {
            info.setCreateTime(strategy.getCreateTime().getTime());
        }
        if (strategy.getUpdateTime() != null) {
            info.setUpdateTime(strategy.getUpdateTime().getTime());
        }
        info.setStrategyDesc(strategy.getDesc());
        info.convertEnvList(strategy.getEnvs());
        info.setAlertMembers(StringUtils.isBlank(strategy.getAlertMembers()) ? null : Arrays.asList(strategy.getAlertMembers().split(",")));
        info.setAtMembers(StringUtils.isBlank(strategy.getAtMembers()) ? null : Arrays.asList(strategy.getAtMembers().split(",")));
        return info;
    }

}
