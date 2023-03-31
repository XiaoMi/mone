package com.xiaomi.mone.tpc.user.util;

import com.xiaomi.mone.tpc.dao.entity.UserGroupRelEntity;
import com.xiaomi.mone.tpc.common.vo.UserGroupRelVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class UserGroupRelUtil {

    public static List<UserGroupRelVo> toVoList(List<UserGroupRelEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<UserGroupRelVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static UserGroupRelVo toVo(UserGroupRelEntity entity) {
        if (entity == null) {
            return null;
        }
        UserGroupRelVo vo = new UserGroupRelVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        return vo;
    }

    public static UserGroupRelEntity toEntity(UserGroupRelVo vo) {
        if (vo == null) {
            return null;
        }
        UserGroupRelEntity entity = new UserGroupRelEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}
