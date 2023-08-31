package com.xiaomi.mone.tpc.dao.impl;

import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.cache.enums.ModuleEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.dao.entity.SystemEntity;
import com.xiaomi.mone.tpc.dao.entity.UserGroupEntity;
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
public class UserGroupDao extends BaseDao{

    public List<UserGroupEntity> getListByPage(String groupName, PageDataVo pageData) {
        SqlExpressionGroup sqlExpr = Cnd.cri().where();
        if (StringUtils.isNotBlank(groupName)) {
            sqlExpr = sqlExpr.andLike("group_name", groupName);
        }
        return getListByPage(sqlExpr, pageData, UserGroupEntity.class);
    }

    public List<UserGroupEntity> getListByPage(Long userId, String groupName, Integer status, PageDataVo pageData) {
        Map<String,Object> param = Maps.newHashMap();
        setParam(param, "userId", userId.toString());
        setParam(param, "groupName", "%" + groupName + "%");
        setParam(param, "status", status);
        StringBuilder sqlExpr = new StringBuilder();
        sqlExpr.append("select ug.* from user_group_entity ug ,user_group_rel_entity ugrel ");
        sqlExpr.append("where ug.deleted=0 and ugrel.deleted=0 and ug.id=ugrel.group_id ");
        sqlExpr.append("and ugrel.user_id=@userId ");
        if (status != null) {
            sqlExpr.append("and ug.status=@status ");
        }
        if (StringUtils.isNotBlank(groupName)) {
            sqlExpr.append("and ug.group_name like @groupName ");
        }
        sqlExpr.append("order by ug.create_time desc");
        return getListByPage(sqlExpr, param, pageData, UserGroupEntity.class);
    }

    public UserGroupEntity getOneByName(String groupName) {
        return fetch(Cnd.cri().where().andEquals("group_name", groupName), UserGroupEntity.class);
    }

}
