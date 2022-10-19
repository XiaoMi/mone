package com.xiaomi.mone.tpc.user.util;

import com.xiaomi.mone.tpc.dao.entity.UserEntity;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class UserUtil {

    public static List<UserVo> toVoList(List<UserEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<UserVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static UserVo toVo(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserVo vo = new UserVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        return vo;
    }

    public static UserEntity toEntity(UserVo vo) {
        if (vo == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
    }
}
