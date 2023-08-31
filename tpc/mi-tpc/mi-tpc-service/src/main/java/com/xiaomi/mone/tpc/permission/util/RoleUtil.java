package com.xiaomi.mone.tpc.permission.util;

import com.google.common.collect.Lists;
import com.xiaomi.mone.tpc.dao.entity.RoleEntity;
import com.xiaomi.mone.tpc.common.vo.RoleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:17
 */
public class RoleUtil {

    public static List<RoleVo> toVoList(List<RoleEntity> entitys) {
        if ( CollectionUtils.isEmpty(entitys)) {
            return null;
        }
        List<RoleVo> voList = new ArrayList<>(entitys.size());
        entitys.stream().forEach(e->voList.add(toVo(e)));
        return voList;
    }

    public static RoleVo toVo(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        RoleVo vo = new RoleVo();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCreateTime() != null) {
            vo.setCreateTime(entity.getCreateTime().getTime());
        }
        if (entity.getUpdateTime() != null) {
            vo.setUpdateTime(entity.getUpdateTime().getTime());
        }
        return vo;
    }
}
