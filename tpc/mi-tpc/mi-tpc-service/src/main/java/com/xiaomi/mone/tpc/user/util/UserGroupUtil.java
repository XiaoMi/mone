package com.xiaomi.mone.tpc.user.util;

import com.xiaomi.mone.tpc.dao.entity.UserGroupEntity;
import com.xiaomi.mone.tpc.common.vo.UserGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class UserGroupUtil {

    public static List<UserGroupVo> toVoList(List<UserGroupEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<UserGroupVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static UserGroupVo toVo(UserGroupEntity entity) {
        if (entity == null) {
            return null;
        }
        UserGroupVo vo = new UserGroupVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        return vo;
    }

    public static UserGroupEntity toEntity(UserGroupVo vo) {
        if (vo == null) {
            return null;
        }
        UserGroupEntity entity = new UserGroupEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}
